package aix.project.chatez.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;

@GetMapping("/join")
public String join(MemberForm memberForm){
    return "join";
}

@PostMapping("/join")
public String join(@Valid MemberForm memberForm, BindingResult bindingResult){

    if(bindingResult.hasErrors()){
        return "/join";
    }

    if (!memberForm.getPassword1().equals(memberForm.getPassword2())) {
        bindingResult.rejectValue("password2", "passwordInCorrect",
                "패스워드가 서로 일치하지 않습니다.");
        return "/join";
    }

    try{
        memberService.save(memberForm.getName(),memberForm.getEmail(),
                memberForm.getPassword1());
    }catch (DataIntegrityViolationException e){
        bindingResult.reject("joinFailed", "이미등록된 사용자 입니다.");
        return "/join";
    }
    return "redirect:/welcome";
}

@GetMapping("/welcome")
public String myService(){
    return "welcome";
}

@GetMapping("/login")
public String login(){
    return "login";
}

@GetMapping("/success")
public String success(Principal principal, Model model){
    String memberName = principal.getName();
    model.addAttribute("memberName", memberName);
    return "login_success";
}


}
