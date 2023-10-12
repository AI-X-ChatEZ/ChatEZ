package aix.project.chatez.member;

import aix.project.chatez.config.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

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
@PostMapping("/service/success")
public String success(Principal principal,
//        @AuthenticationPrincipal OAuth2User oAuth2User,
                      Model model
                        ){
//    Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
//    log.info("principal값>>{}",authentication.getPrincipal());
//    String email = (String) ((OAuth2User) authentication.getPrincipal()).getAttributes().get("email");
//
//    Member member = memberService.findByEmail(email);
//    model.addAttribute("member", member);

//    Authentication authentication = tokenProvider.getAuthentication(token);
//    String email = (String) ((OAuth2User) authentication.getPrincipal()).getAttributes().get("email");
//    Member member= memberService.findByEmail(email);
//    model.addAttribute(email);


    String email = principal.getName();
    log.info("principal.getName()>>{}",email);
    Member member= memberService.findByEmail(email);
    model.addAttribute("member",member);


    return "redirect:/service/success";
}
@GetMapping("/service/success")
public String successPost(
//        @AuthenticationPrincipal OAuth2User oAuth2User,
//                      Model model
                        ){
//    Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
//    log.info("principal값>>{}",authentication.getPrincipal());
//    String email = (String) ((OAuth2User) authentication.getPrincipal()).getAttributes().get("email");
////    log.info("member>{}",member.getName());
////    String email = (String) oAuth2User.getAttributes().get("email");
//////    Member loginedmember = memberService.findByEmail(member.getEmail());
//    Member member = memberService.findByEmail(email);
//    model.addAttribute("member", member);


    return "service/myService";
}

@GetMapping("/logout")
public String logout(HttpServletRequest request, HttpServletResponse response){
    new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
    return "redirect:/";
}


}
