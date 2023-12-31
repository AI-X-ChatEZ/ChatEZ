package aix.project.chatez.oauth2;

import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberRole;
import aix.project.chatez.member.SocialType;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Getter
public class OAuthAttributes {

    private String nameAttributeKey; // OAuth2 로그인 진행 시 키가 되는 필드 값, PK와 같은 의미
    private OAuth2Info oAuth2Info; // 소셜 타입별 로그인 유저 정보(닉네임, 이메일, 프로필 사진 등등)

    @Builder
    public OAuthAttributes(String nameAttributeKey, OAuth2Info OAuth2Info) {
        this.nameAttributeKey = nameAttributeKey;
        this.oAuth2Info = OAuth2Info;
    }

    /**
     * SocialType에 맞는 메소드 호출하여 OAuthAttributes 객체 반환
     * 파라미터 : userNameAttributeName -> OAuth2 로그인 시 키(PK)가 되는 값 / attributes : OAuth 서비스의 유저 정보들
     * 소셜별 of 메소드(ofGoogle, ofKaKao, ofNaver)들은 각각 소셜 로그인 API에서 제공하는
     * 회원의 식별값(id), attributes, nameAttributeKey를 저장 후 build
     */
    public static OAuthAttributes of(SocialType socialType,
                                     String userNameAttributeName, Map<String, Object> attributes) {

        if (socialType == SocialType.KAKAO) {
            return ofKakao(userNameAttributeName, attributes);
        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        log.info("socialID 찾기 {}",attributes);
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .OAuth2Info(new KakaoOAuth2Info(attributes))
                .build();
    }

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        log.info("socialID 찾기 {}>>attributes",attributes);
        return OAuthAttributes.builder()
                .nameAttributeKey(userNameAttributeName)
                .OAuth2Info(new GoogleOAuth2Info(attributes))
                .build();
    }


    /**
     * of메소드로 OAuthAttributes 객체가 생성되어, 유저 정보들이 담긴 OAuth2Info가 소셜 타입별로 주입된 상태
     * OAuth2Info에서 socialId(식별값), nickname, imageUrl을 가져와서 build
     * email에는 UUID로 중복 없는 랜덤 값 생성
     * role은 GUEST로 설정
     */
    public Member toEntity(SocialType socialType, OAuth2Info oAuth2Info) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return Member.builder()
                .socialType(socialType)
                .socialId(oAuth2Info.getSocialId())
                .email(oAuth2Info.getEmail())
                .name(oAuth2Info.getName())
                .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                .role(MemberRole.USER)
                .build();
    }
}