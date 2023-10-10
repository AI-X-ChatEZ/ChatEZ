package aix.project.chatez.member;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class ChatEzService {
    private final MyServiceRepository myServiceRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ChatEzService(MyServiceRepository myServiceRepository, MemberRepository memberRepository) {
        this.myServiceRepository = myServiceRepository;
        this.memberRepository = memberRepository;
    }

    public void userServiceDate(Model model) {
        //로그인 페이지에서 아이디 연동 부분
        List<Member> loginUser = this.memberRepository.findAll();
        if(loginUser.get(0) != null) {
            String name = loginUser.get(0).getName();
            System.out.println("로그인 유저 : " + name);

            Member member = memberRepository.findByName(name);
            Long memberNo = null;
            if (member != null) {
                memberNo = member.getMemberNo();
                System.out.println("id : hhh7441 no : " + memberNo);
            } else {
                System.out.println("hhh7441 회원의 번호를 찾을 수 없습니다.");
            }
            List<MyService> myServices = myServiceRepository.findByMemberMemberNo(memberNo);
            if(!myServices.isEmpty()) {
                System.out.println("no : " + myServices.get(0).getServiceNo());
                System.out.println("name : " + myServices.get(0).getServiceName());
                System.out.println("profilePic : " + myServices.get(0).getProfilePic());
            }
            model.addAttribute("myServices", myServices);
            model.addAttribute("name", name);
        }
    }


    public String userFileUplaod(MultipartFile imageFile, String aiName) {
        if (imageFile.isEmpty() || StringUtils.isEmpty(aiName)) {
            return "파일 또는 AI의 이름이 없습니다.";
        }

        try {
            //파일 이름을 얻어옴
            String fileName = imageFile.getOriginalFilename();
//            String fileUploadDirectory = "C:/javas/IntlliJ/github/uploaded-images/" + fileName;
            String fileUploadDirectory = "C:/github/uploaded-images/" + fileName;
            Path path = Paths.get(fileUploadDirectory).toAbsolutePath();
            imageFile.transferTo(path.toFile());
            System.out.println("파일 명 : " + fileName);
            System.out.println("AI 이름 : " + aiName);

            List<Member> loginUser = this.memberRepository.findAll();
            if (!loginUser.isEmpty()) {
                Member member = loginUser.get(0);

                MyService myService = new MyService();
                myService.setServiceName(aiName);
                myService.setProfilePic("/images/" + fileName);
                myService.setMember(member);  //엔티티와 엔티티 간의 연결 설정

                myServiceRepository.save(myService);

                return "my_service";
            } else {
                return "로그인한 사용자 정보를 찾을 수 없습니다.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "redirect:my_service";
        }
    }
}
