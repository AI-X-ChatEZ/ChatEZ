package aix.project.chatez.config.jwt;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken() : 유저정보와 만료기간을 전달해 토큰 생성")
    @Test
    void generateToken(){
        //given
        Member test = memberRepository.save(Member.builder()
                .email("test@test.com").name("테스트").password("testtest").build());

        //when
        String token = tokenProvider.generateToken(test, Duration.ofDays(14));

        //then
        Long memberNo = Jwts.parser()
                .setSigningKey(jwtProperties.getSecreteKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id",Long.class);

        assertThat(memberNo).isEqualTo(test.getMemberNo());
    }


}
