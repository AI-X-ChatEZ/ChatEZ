package aix.project.chatez;

import aix.project.chatez.member.MemberService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void 회원가입(){
        //given
        String name = "test";
        String email = "test@test.com";
        String password = "testest";

        //when

        //then


    }
}
