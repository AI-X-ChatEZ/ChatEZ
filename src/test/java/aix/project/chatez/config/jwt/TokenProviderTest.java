package aix.project.chatez.config.jwt;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberRepository;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
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
        log.info("memberNo>>{}",memberNo);
        assertThat(memberNo).isEqualTo(test.getMemberNo());
    }

    @DisplayName("validToken():만료된 토큰이면 유효성 검증에 실패")
    @Test
    void validToken_invalidToken(){

        //given
        String token = JwtFactory.builder().expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                .build().createToken(jwtProperties);
        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isFalse();
    }

    @DisplayName("validToken():유효한 토큰일 때 유효성 검증 성공")
    @Test
    void getAthentification(){

        //given
        String token = JwtFactory.builder().subject("test@test.com").build().createToken(jwtProperties);

        //when
        Authentication authentication = tokenProvider.getAuthentication(token);
        log.info("token>>{}",token);
        //then
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo("test@test.com");


    }

    @DisplayName("getMemberNo(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getMemberNo(){

        //given
        Long memberNo = 1L;
        String token = JwtFactory.builder().claims(Map.of("id",memberNo)).build().createToken(jwtProperties);

        //when
        Long memberIdByToken = tokenProvider.getMemberNo(token);
        log.info("memberIdByToken>>{}",memberIdByToken);
        //then
        assertThat(memberIdByToken).isEqualTo(memberNo);

    }


}
