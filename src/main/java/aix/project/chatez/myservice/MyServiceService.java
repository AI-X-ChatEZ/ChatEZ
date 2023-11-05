package aix.project.chatez.myservice;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.opensearch.action.search.SearchRequest;
import org.opensearch.action.search.SearchResponse;
import org.opensearch.client.RequestOptions;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.search.SearchHit;
import org.opensearch.search.builder.SearchSourceBuilder;
import org.springframework.messaging.simp.SimpMessagingTemplate;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyServiceService {

    private final MyServiceRepository myServiceRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3 amazonS3;
    private final SimpMessagingTemplate messagingTemplate;
    @Value("${cloud.aws.s3-bucket}")
    private String bucket;
    @Value("${cloud.aws.s3-upload-path}")
    private String uploadPath;


    public List<MyService> findByMember_MemberNo(Long memberNo){
        return myServiceRepository.findByMember_MemberNo(memberNo);
    }


    public MyService userFileUplaod(MultipartFile imageFile, String aiName, String serviceId, String email) throws IOException {

        String newFileName = s3FileUpload(imageFile);

        Member member = memberRepository.findByEmail(email).get();
        //엔티티와 엔티티 간의 연결 설정

//        String urlValue = aiName+System.currentTimeMillis();
        return myServiceRepository.save(MyService.builder()
                .serviceName(aiName)
                .serviceId(serviceId)
                .profilePic(newFileName)
//                .url(UUID.nameUUIDFromBytes(urlValue.getBytes()).toString().replace("-",""))
                .member(member)
                .build());
    }


    public String handleFileUpdate(String updateName, MultipartFile updateFile, String selectNo) {
        Long no = Long.parseLong(selectNo);

        if (StringUtils.isEmpty(updateName)) {
            return "업데이트할 이름이 없습니다.";
        }

        try {
            Optional<MyService> optionalMyService = myServiceRepository.findById(no);

            if (optionalMyService.isPresent()) {
                MyService myService = optionalMyService.get();
                myService.updateServiceName(updateName);
                log.info("new name:{}",myService.getServiceName());

                // 파일이 있는 경우에만 파일 처리
                if (updateFile != null && !updateFile.isEmpty()) {
                    if (!myService.getProfilePic().isEmpty()) {
                        //s3에 있는 기존 파일 delete
                        String imagePath = String.format("%s/%s",bucket, uploadPath);
                        amazonS3.deleteObject(imagePath, myService.getProfilePic());
                    }
                    String newFileName = s3FileUpload(updateFile);
                    myService.updateProfilePic(newFileName);
                    log.info("new pic:{}",myService.getProfilePic());
                }

                myServiceRepository.save(myService);

                return "my_service";
            } else {
                return "선택된 번호에 해당하는 레코드를 찾을 수 없습니다.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "my_service";
        }
    }

    public String handleDeleteService(String serviceNo) {
        try {
            Long no = Long.parseLong(serviceNo);
            Optional<MyService> optionalMyService = myServiceRepository.findById(no);

            if (optionalMyService.isPresent()) {
                MyService myService = optionalMyService.get();

                if (!myService.getProfilePic().isEmpty()) {
                    //s3에 있는 연결된 파일 delete
                    String imagePath = String.format("%s/%s",bucket, uploadPath);
                    amazonS3.deleteObject(imagePath, myService.getProfilePic());
                }

                myServiceRepository.deleteById(no);

                return "my_service";
            } else {
                return "my_service";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "my_service";
        }
    }

    @Transactional
    public void activateServiceById(String serviceId) {
        MyService myService = myServiceRepository.findByServiceId(serviceId);

        if (myService != null) {
            myService.activate();
            messagingTemplate.convertAndSend("/topic/notifications", serviceId);
        } else {
            System.out.println("Service with ID: " + serviceId + " not found.");
        }
    }

    //s3파일 업로드
     private String s3FileUpload(MultipartFile file) throws IOException {

        String fileName = file.getOriginalFilename();
        log.info("fileName:{}",fileName);
        //업로드 날짜 설정
        String uploadTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String onlyFileName = fileName.substring(0,fileName.lastIndexOf("."));
        String extension = fileName.substring(fileName.lastIndexOf("."));
        String newFileName = String.format("%s_%s%s",onlyFileName, uploadTime, extension);
        //s3 파일 업로드
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());
        String imagePath = String.format("%s/%s",bucket, uploadPath);
        log.info("buket path:{}",imagePath);
        amazonS3.putObject(imagePath, newFileName, file.getInputStream(),metadata);
        log.info("new Filename: {}",newFileName);

        return newFileName;
    }

    //s3 파일 다운로드
    public ResponseEntity<UrlResource> downloadFile(String fileName){
        UrlResource urlResource = new UrlResource(amazonS3.getUrl(String.format("%s/example",bucket), fileName));
        String contentDisposition = String.format("attachment; filename=\\%s\\", URLEncoder.encode(fileName, StandardCharsets.UTF_8));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(urlResource);
    }

    public List<MyService> userServiceDate(String email) {


            Member member = memberRepository.findByEmail(email).get();

        return myServiceRepository.findByMember_MemberNo(member.getMemberNo());

    }


    public Map<String, List<Map<String, Object>>> awsFileData(String email) {
        if (email == null) {
            throw new IllegalArgumentException("알 수 없는 사용자");
        }

        RestHighLevelClient client = OpenSearchClient.createClient();
        Optional<Member> loginUser = memberRepository.findByEmail(email);

        if (!loginUser.isPresent()) {
            throw new IllegalArgumentException("회원의 Email 값을 찾지 못했습니다.");
        }

        Optional<Member> member = memberRepository.findByEmail(email);

        Long memberNo;
        if (member.isPresent()) {
            memberNo = member.get().getMemberNo();
            log.info("no : {}",memberNo);
        } else {
            return Collections.emptyMap();
        }

        List<MyService> myServices = myServiceRepository.findByMember_MemberNo(memberNo);
        Map<String, List<Map<String, Object>>> servicesFilesMap = new HashMap<>();

        for (MyService myService : myServices) {
            List<Map<String, Object>> files = new ArrayList<>();
            Set<String> existDocumentIds = new HashSet<>();
//            Set<String> existNames = new HashSet<>();
            log.info("name : {} ", myService.getServiceName());
            try {
                SearchRequest searchRequest = new SearchRequest(myService.getServiceId()); // 서비스 이름을 인덱스로 사용
                SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

                // Only fetch the "size", "name", and "contentType" fields
                String[] includeFields = new String[]{"documentId","totalSize", "name", "contentType", "uploadTime"};
                String[] excludeFields = new String[]{};
                searchSourceBuilder.fetchSource(includeFields, excludeFields);

                searchRequest.source(searchSourceBuilder);

                SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    Map<String, Object> source = hit.getSourceAsMap();
                    String documentId = (String) source.get("documentId");
//                    String name = (String) source.get("name");
                    log.info("document id1:{}", documentId);
                    if (!existDocumentIds.contains(documentId)) {
                        existDocumentIds.add(documentId);
                        log.info("document id2:{}", documentId);
//                        existNames.add(name);
                        files.add(source);
                    }// 각 hit의 정보를 리스트에 추가합니다.
                    log.info("source:{}",source);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            servicesFilesMap.put(myService.getServiceName(), files);
        }
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return servicesFilesMap;
    }
}
