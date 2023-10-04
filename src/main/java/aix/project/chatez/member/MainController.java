package aix.project.chatez.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @GetMapping("/ChatEZ_layout")
    public String ChatEZ_layout(){
        return "ChatEZ_layout";
    }

    @GetMapping("/ChatEZ_my_service")
    public String my_service(){
        return "ChatEZ_my_service";
    }

    @GetMapping("/ChatEZ_file_manager")
    public String file_manager(){
        return "ChatEZ_file_manager";
    }
}
