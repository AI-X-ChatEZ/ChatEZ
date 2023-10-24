package aix.project.chatez.myservice;

import aix.project.chatez.member.MemberDetails;
import aix.project.chatez.member.MemberService;
import aix.project.chatez.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class MyServiceController {

    private final MyServiceService myServiceService;
    private final MemberService memberService;


    @GetMapping("/service_layout")
    public String service_layout(){return "service/service_layout";}

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/my_service")
    public ModelAndView my_service(Principal principal){
        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);
        List<MyService> myServices = myServiceService.findByMember_MemberNo(member.getMemberNo());

        ModelAndView modelAndView = new ModelAndView("service/my_service");
        modelAndView.addObject("myServices",myServices);
        modelAndView.addObject("member",member);

        return modelAndView;
    }

    @ResponseBody
    @PostMapping("/upload")
    public String handleFileUpload(@RequestParam("imageFile") MultipartFile imageFile,
                                   @RequestParam("aiName") String aiName,
                                   @RequestParam("aiId") String aiId,
                                   Principal principal) throws IOException {

        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);

        myServiceService.userFileUplaod(imageFile, aiName, aiId, member.getEmail());
        return "redirect:/my_service";
    }

    @ResponseBody
    @PostMapping("/update")
    public String handleFileUpdate(@RequestParam("updateName") String updateName,
                                   @RequestParam(value="updateFile", required=false) MultipartFile updateFile,
                                   @RequestParam("selectNo") String selectNo) {
        String url = myServiceService.handleFileUpdate(updateName, updateFile, selectNo);

        return "redirect:"+url;
    }

    @ResponseBody
    @PostMapping("/delete")
    public String delete_service(@RequestParam("serviceNo") String serviceNo) {
        String url = myServiceService.handleDeleteService(serviceNo);
        return "redirect:"+url;
    }

    @GetMapping("/example_download")
    public ResponseEntity<UrlResource> downloadExample(){
        return myServiceService.downloadFile("sample.xlsx");
    }

    @GetMapping("/file_manager")
    public ModelAndView file_manager(Principal principal){
        String email = extractEmail(principal);
        Member member = memberService.findByEmail(email);

        ModelAndView modelAndView = new ModelAndView("service/file_manager");
        modelAndView.addObject("member",member);

        return modelAndView;
    }


    private String extractEmail(Principal principal) {
        log.info("Principal type: {}", principal.getClass().getName());
        log.info("Principal name: {}", principal.getName());

        if (principal instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken token = (OAuth2AuthenticationToken) principal;
            OAuth2User oauthUser = token.getPrincipal();

            Map<String, Object> attributes = oauthUser.getAttributes();

            String email = null;
            if (token.getAuthorizedClientRegistrationId().equals("google")) {
                email = (String) attributes.get("email");
            } else if (token.getAuthorizedClientRegistrationId().equals("kakao")) {
                Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
                email = (String) kakaoAccount.get("email");
            }

            log.info("Extracted email: {}", email);
            return email;

        } else if(principal instanceof UsernamePasswordAuthenticationToken){
            UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) principal;
            Object userDetails = token.getPrincipal();

            if(userDetails instanceof MemberDetails memberDetails){
                String username = memberDetails.getUsername();
                log.info("Extracted username from UserDetails: {}", username);
                return username;


            } else {
                throw new IllegalArgumentException("Unexpected type of UserDetails");
            }

        } else{
            throw new IllegalArgumentException("Unexpected type of Principal");
        }
    }




}
