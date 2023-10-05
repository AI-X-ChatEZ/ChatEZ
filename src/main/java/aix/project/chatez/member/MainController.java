package aix.project.chatez.member;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @GetMapping("/service_layout")
    public String service_layout(){
        return "service_layout";
    }

    @GetMapping("/my_service")
    public String my_service(){
        return "my_service";
    }

    @GetMapping("/file_manager")
    public String file_manager(){
        return "file_manager";
    }
}
