package aix.project.chatez.member;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class MainController {
    private final MemberRepository memberRepository;
    private final ChatEzService chatEzService;

    public MainController(MemberRepository memberRepository, ChatEzService chatEzService) {
        this.memberRepository = memberRepository;
        this.chatEzService = chatEzService;
    }

    @GetMapping("/service_layout")
    public String service_layout(){return "service/service_layout";}

    @GetMapping("/my_service")
    public String my_service(Model model){
        chatEzService.userServiceDate(model);
        return "service/my_service";
    }

    @ResponseBody
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("imageFile") MultipartFile imageFile,
                                   @RequestParam("aiName") String aiName) {
        String url = chatEzService.userFileUplaod(imageFile, aiName);
        return "redirect:"+url;
    }

    @ResponseBody
    @PostMapping("/update")
    public String handleFileUpdate(@RequestParam("updateName") String updateName,
                                   @RequestParam("updateFile") MultipartFile updateFile,
                                   @RequestParam("selectNo") String selectNo) {

        return "redirect:my_service";
    }

    @GetMapping("/file_manager")
    public String file_manager(){
        return "service/file_manager";
    }
}
