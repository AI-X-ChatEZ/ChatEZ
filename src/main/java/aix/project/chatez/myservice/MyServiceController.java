package aix.project.chatez.myservice;

import aix.project.chatez.config.S3Properties;
import aix.project.chatez.member.MemberDetails;
import aix.project.chatez.member.MemberService;
import aix.project.chatez.member.Member;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.apache.http.entity.mime.MultipartEntityBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyServiceController {

    private final MyServiceService myServiceService;
    private final MemberService memberService;
    private final S3Properties s3Properties;
    private final ExecutorService executorService = Executors.newFixedThreadPool(20);

    @Value("${fastApi.endpoint}")
    private String fastApiEndpoint;


    @GetMapping("/service_layout")
    public String service_layout(){return "service/service_layout";}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_service")
    public ModelAndView my_service(Principal principal){
        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);
        List<MyService> myServices = myServiceService.findByMember_MemberNo(member.getMemberNo());

        ModelAndView modelAndView = new ModelAndView("service/my_service");
        modelAndView.addObject("myServices",myServices);
        modelAndView.addObject("member",member);
        modelAndView.addObject("bucket", s3Properties.getS3Bucket());
        modelAndView.addObject("folder",s3Properties.getS3UploadPath());

        return modelAndView;
    }

    private Path saveTempFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new IOException("Original filename is null");
        }
//        String fileExtension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : ".tmp";
        Path tempDirectory = Files.createTempDirectory("uploadDir");
        Path targetPath = tempDirectory.resolve(originalFilename);
        file.transferTo(targetPath.toFile());

        return targetPath;
    }

    @PostMapping("/fileUpdate")
    public ResponseEntity<?> handleMultipleFileUpload(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("serviceId") String serviceId, Principal principal) throws IOException {
        String email = extractEmail(principal);
        myServiceService.startUpload(serviceId);
        List<Path> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            Path savedFile = saveTempFile(file);
            savedFiles.add(savedFile);
        }
        // 비동기 작업을 위한 Future 생성
        Future<?> future = executorService.submit(() -> {
            try {
                // 파일을 FastAPI 서버에 업데이트하는 로직
                updateToFastApi(serviceId, savedFiles);
                // 임시 파일 삭제
                for (Path savedFile : savedFiles) {
                    Files.deleteIfExists(savedFile);
                }
                myServiceService.completeUpload(serviceId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error processing files", e);
            }
        });

        try {
            // Future 작업의 완료를 기다립니다.
            future.get(); // 필요하다면 타임아웃을 설정할 수 있습니다.
            Thread.sleep(1000);
            // 비동기 작업이 완료된 후에 파일 데이터를 가져옵니다.
            Map<String, List<Map<String, Object>>> servicesFilesMap = myServiceService.awsFileData(email);
            // 업데이트된 파일 리스트를 JSON 형태로 클라이언트에 반환합니다.
            return ResponseEntity.ok(servicesFilesMap);
        } catch (Exception e) {
            // 작업 중 오류가 발생한 경우
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error during file processing"));
        }
    }

    @GetMapping("/uploadStatus")
    public ResponseEntity<?> getUploadStatus(@RequestParam String serviceId) {
        boolean isCompleted = myServiceService.isUploadCompleted(serviceId);
        if(isCompleted) {
            return ResponseEntity.ok(Map.of("serviceId", serviceId, "status", "completed"));
        } else {
            return ResponseEntity.ok(Map.of("serviceId", serviceId, "status", "inProgress"));
        }
    }

    @GetMapping("/getFileList")
    public ResponseEntity<?> getFileList(Principal principal) {
        String email = extractEmail(principal);
        Map<String, List<Map<String, Object>>> servicesFilesMap = myServiceService.awsFileData(email);
        return ResponseEntity.ok(servicesFilesMap);
    }

    private void updateToFastApi(String serviceId, List<Path> files) {
        // FastAPI 엔드포인트 URL 설정
        String fastApiUpdate = fastApiEndpoint+"/update_files";
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            // POST 요청 생성
            HttpPost postRequest = new HttpPost(fastApiUpdate);

            // multipart 엔티티 구성
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            for (Path file : files) {
                String encodedFileName = URLEncoder.encode(file.getFileName().toString(), StandardCharsets.UTF_8.toString());
                encodedFileName = encodedFileName.replaceAll("\\+", "%20");
                ContentType contentType = ContentType.create(ContentType.MULTIPART_FORM_DATA.getMimeType(), StandardCharsets.UTF_8);
                builder.addBinaryBody("files", Files.newInputStream(file), contentType, encodedFileName);
            }
            builder.addTextBody("index", serviceId, ContentType.TEXT_PLAIN);

            // 요청에 엔티티 추가
            HttpEntity entity = builder.build();
            postRequest.setEntity(entity);

            // 요청 실행
            HttpResponse response = httpClient.execute(postRequest);
            // 성공 여부 확인
            if (response.getStatusLine().getStatusCode() != 200) {
                // 에러 처리
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new RuntimeException("실패: HTTP 에러 코드: " + response.getStatusLine().getStatusCode() + " : " + responseBody);
            }
        } catch (IOException e) {
            // 예외 처리
            e.printStackTrace();
        }
    }

    @ResponseBody
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                   @RequestParam("aiName") String aiName,
                                   @RequestParam("aiId") String aiId,
                                   @RequestParam("files") MultipartFile[] files,
                                   Principal principal) throws IOException {

        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);

        myServiceService.userFileUplaod(imageFile, aiName, aiId, member.getEmail());
        List<Path> savedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            Path savedFile = saveTempFile(file);
            savedFiles.add(savedFile);
        }

        executorService.submit(() -> {
            try {
                uploadToFastApi(aiId, savedFiles);
                for (Path savedFile : savedFiles) {
                    Files.deleteIfExists(savedFile);
                }
                Thread.sleep(1000);
                myServiceService.activateServiceById(aiId);  // 추가된 코드
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return "redirect:/my_service";

    }

    private void uploadToFastApi(String aiId, List<Path> files) {
        String fastApiUpload = fastApiEndpoint+"/upload_files";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost postRequest = new HttpPost(fastApiUpload);

            // Creating multipart entity
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//            for (Path file : files) {
//                System.out.println(file.getFileName());
//                builder.addBinaryBody("files", Files.newInputStream(file), ContentType.MULTIPART_FORM_DATA, file.getFileName().toString());
//            }
            for (Path file : files) {
                String encodedFileName = URLEncoder.encode(file.getFileName().toString(), StandardCharsets.UTF_8.toString());
                encodedFileName = encodedFileName.replaceAll("\\+", "%20");
                ContentType contentType = ContentType.create(ContentType.MULTIPART_FORM_DATA.getMimeType(), StandardCharsets.UTF_8);
                builder.addBinaryBody("files", Files.newInputStream(file), contentType, encodedFileName);
            }
            builder.addTextBody("index", aiId);

            HttpEntity entity = builder.build();

            postRequest.setEntity(entity);

            HttpResponse response = httpClient.execute(postRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                // Error handling
                String responseBody = EntityUtils.toString(response.getEntity());
                throw new RuntimeException("Failed with HTTP error code: " + response.getStatusLine().getStatusCode() + " and message: " + responseBody);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @PreDestroy
    public void onDestroy() {
        executorService.shutdown();
    }


    @ResponseBody
    @PostMapping("/update")
    public String handleFileUpdate(@RequestParam("updateName") String updateName,
                                   @RequestParam(value="updateFile", required=false) MultipartFile updateFile,
                                   @RequestParam("selectNo") String selectNo) {
        String url = myServiceService.handleFileUpdate(updateName, updateFile, selectNo);

        return "redirect:"+url;
    }

    @ResponseBody
    @PostMapping("/delete")
    public String delete_service(@RequestParam("serviceNo") String serviceNo) {
        String url = myServiceService.handleDeleteService(serviceNo);
        return "redirect:"+url;
    }

    @GetMapping("/example_download")
    public ResponseEntity<UrlResource> downloadExample(){
        return myServiceService.downloadFile("example.zip");
    }

    @GetMapping("/file_manager")
    public ModelAndView file_manager(Principal principal){
        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);
        List<MyService> myServices = myServiceService.userServiceDate(member.getEmail());
        Map<String, List<Map<String, Object>>> servicesFilesMap = myServiceService.awsFileData(member.getEmail());
        ModelAndView modelAndView = new ModelAndView("service/file_manager");
        modelAndView.addObject("member",member);
        modelAndView.addObject("myServices",myServices);
        modelAndView.addObject("servicesFiles", servicesFilesMap);
        modelAndView.addObject("bucket", s3Properties.getS3Bucket());
        modelAndView.addObject("folder",s3Properties.getS3UploadPath());
        return modelAndView;
    }


    private String extractEmail(Principal principal) {
        log.info("Principal type: {}", principal.getClass().getName());
        log.info("Principal name: {}", principal.getName());

        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            OAuth2User oauthUser = token.getPrincipal();

            Map<String, Object> attributes = oauthUser.getAttributes();

            String email = null;
            if (token.getAuthorizedClientRegistrationId().equals("google")) {
                email = (String) attributes.get("email");
            } else if (token.getAuthorizedClientRegistrationId().equals("kakao")) {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = (String) kakaoAccount.get("email");
            }

            log.info("Extracted email: {}", email);
            return email;

        } else if(principal instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            Object userDetails = token.getPrincipal();

            if(userDetails instanceof MemberDetails memberDetails){
                String username = memberDetails.getUsername();
                log.info("Extracted username from UserDetails: {}", username);
                return username;


            } else {
                throw new IllegalArgumentException("Unexpected type of UserDetails");
            }

        } else{
            throw new IllegalArgumentException("Unexpected type of Principal");
        }
    }
}
