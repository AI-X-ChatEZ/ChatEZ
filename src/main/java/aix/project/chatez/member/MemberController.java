package aix.project.chatez.member;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Controller
public class MemberController {

    private final MemberService memberService;
    @GetMapping("/")
    public String Index(){return "index";}

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







        @GetMapping("/logout")
        public String logout(HttpServletRequest request, HttpServletResponse response){
            new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());
            return "redirect:/";
        }


}
