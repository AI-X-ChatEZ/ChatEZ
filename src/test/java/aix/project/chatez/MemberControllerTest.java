package aix.project.chatez;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations="classpath:application-test.properties")
class MemberControllerTest {

    @Autowired
    private MemberService memberService;

    //	스프링 시큐리티 로그인 테스트시 필요한 가짜 객체
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

//    @Test
//    void signUpTest(){
//
//        Member member = new Member();
//        member.setName("테스트");
//        member.setEmail("test@test.com");
//        member.setPassword("abc12345");
//
//
//
//
//
//    }

}