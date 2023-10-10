package aix.project.chatez.token;

import aix.project.chatez.config.jwt.TokenProvider;
import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@RequiredArgsConstructor
@Service
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final MemberService memberService;

    public String createNewAccessToken(String refreshToken){
        //토큰 유효성 검사에 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("알수없는 토큰");
        }

        Long memberNo = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        Member member = memberService.findByMemberNo(memberNo);
        return tokenProvider.generateToken(member, Duration.ofHours(2));
    }
}
