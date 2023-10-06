package aix.project.chatez.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Clock;
import java.util.List;

@Controller
public class MainController {
    private final MemberRepository memberRepository;

    public MainController(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @GetMapping("/service_layout")
    public String service_layout(){return "service/service_layout";}

    @GetMapping("/my_service")
    public String my_service(Model model){
        List<Member> loginUser = this.memberRepository.findAll();
        String userName = loginUser.get(0).getName();
        System.out.print(userName);
        model.addAttribute("userName", userName);
        return "service/my_service";
    }

    @PostMapping("upload")
    public String uploadImage(@RequestParam(value="file", required = false) MultipartFile imageFile) {
        return "service/my_service";
    }

    @PostMapping
    public String handleFileUpload(@RequestParam("imageFile") MultipartFile imageFile) {
        System.out.print("테스트");

        return "service/my_service";
    }

    @GetMapping("/file_manager")
    public String file_manager(){
        return "service/file_manager";
    }
}
