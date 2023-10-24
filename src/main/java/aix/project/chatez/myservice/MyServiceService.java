package aix.project.chatez.myservice;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberRepository;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class MyServiceService {

    private final MyServiceRepository myServiceRepository;
    private final MemberRepository memberRepository;
    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.upload-path}")
    private String uploadPath;


    public List<MyService> findByMember_MemberNo(Long memberNo){
        return myServiceRepository.findByMember_MemberNo(memberNo);
    }


    public MyService userFileUplaod(MultipartFile imageFile, String aiName, String serviceId, String email) throws IOException {

        String newFileName = s3FileUpload(imageFile);

        Member member = memberRepository.findByEmail(email).get();

//        MyService myService = new MyService();
//        myService.setServiceName(aiName);
//        log.info("aiName:{}",aiName);
//        myService.setProfilePic(newFileName);
//        myService.setMember(member);  //엔티티와 엔티티 간의 연결 설정

        String urlValue = aiName+System.currentTimeMillis();
        return myServiceRepository.save(MyService.builder()
                .serviceName(aiName)
                .serviceId(serviceId)
                .profilePic(newFileName)
                .url(UUID.nameUUIDFromBytes(urlValue.getBytes()).toString().replace("-",""))
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


    public String openSearchFileUpload(List<MultipartFile> uploadFile, String aiName) {
        for (MultipartFile file : uploadFile){
            System.out.print(file.getOriginalFilename());
        }
        return "my_service";
    }
}
