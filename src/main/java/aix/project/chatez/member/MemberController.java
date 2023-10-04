package aix.project.chatez.member;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

@GetMapping("/signup")
public String signUp(MemberForm memberForm){
    return "sign_up";
}

@PostMapping("/signup")
public String signUp(@Valid MemberForm memberForm, BindingResult bindingResult){

    if(bindingResult.hasErrors()){
        return "/sign_up";
    }

    if (!memberForm.getPassword1().equals(memberForm.getPassword2())) {
        bindingResult.rejectValue("password2", "passwordInCorrect",
                "패스워드가 서로 일치하지 않습니다.");
        return "/sign_up";
    }

    try{
        memberService.save(memberForm.getName(),memberForm.getEmail(),
                memberForm.getPassword1());
    }catch (DataIntegrityViolationException e){
        bindingResult.reject("signupFailed", "이미등록된 사용자 입니다.");
        return "/sign_up";
    }
    return "redirect:/myservice";
}

@GetMapping("/myservice")
public String myService(){
    return "my_service";
}

@GetMapping("/signin")
public String signIn(){
    return "login";
}

@GetMapping("/success")
public String success(Principal principal, Model model){
    String memberName = principal.getName();
    model.addAttribute("memberName", memberName);
    return "login_success";
}


}
