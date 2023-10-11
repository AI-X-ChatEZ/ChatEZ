package aix.project.chatez.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.security.Principal;

@Slf4j
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
        memberService.save(memberForm);
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


@PreAuthorize("isAuthenticated()")
@GetMapping("/success")
public String success(@AuthenticationPrincipal Member member, Model model){
    log.info("member>{}",member.getMemberNo());
    log.info("membername>{}",member.getName());
    Member loginedmember = memberService.findByEmail(member.getEmail());
    model.addAttribute("member", loginedmember);

    return "service/myService";
}

@GetMapping("/logout")
public String logout(HttpServletRequest request, HttpServletResponse response){
    new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    return "redirect:/";
}


}
