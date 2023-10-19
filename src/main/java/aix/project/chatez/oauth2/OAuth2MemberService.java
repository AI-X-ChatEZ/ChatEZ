package aix.project.chatez.oauth2;


import aix.project.chatez.member.Member;
import aix.project.chatez.member.MemberDetails;
import aix.project.chatez.member.MemberRepository;
import aix.project.chatez.member.SocialType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2MemberService extends DefaultOAuth2UserService {

    private final MemberRepository memberRepository;

    private static final String KAKAO = "kakao";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2Info memberInfo = null;

        /**
         * userRequest에서 registrationId 추출 후 registrationId으로 SocialType 저장
         * http://localhost:8080/oauth2/authorization/kakao에서 kakao가 registrationId
         * userNameAttributeName은 이후에 nameAttributeKey로 설정된다.
         */
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        SocialType socialType = getSocialType(registrationId);

        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName(); // OAuth2 로그인 시 키(PK)가 되는 값
        Map<String, Object> attributes = oAuth2User.getAttributes();
        // 소셜 로그인에서 API가 제공하는 userInfo의 Json 값(유저 정보들)
        log.info("attributes>>{}",attributes.toString());
        // socialType에 따라 유저 정보를 통해 OAuthAttributes 객체 생성
        OAuthAttributes extractAttributes = OAuthAttributes.of(socialType, userNameAttributeName, attributes);

        Member member = getMember(extractAttributes, socialType); // getUser() 메소드로 Member객체 생성 후 반환

        // DefaultOAuth2User를 구현한 CustomOAuth2User 객체를 생성해서 반환
        return new MemberDetails(member, oAuth2User.getAttributes());
    }

    private SocialType getSocialType(String registrationId) {
        if(KAKAO.equals(registrationId)) {
            return SocialType.KAKAO;
        }
        return SocialType.GOOGLE;
    }

    private Member getMember(OAuthAttributes attributes, SocialType socialType) {
        Member findUser = memberRepository.findByEmailAndSocialType(attributes.getOAuth2Info().getEmail(),
                socialType).orElse(null);

        if(findUser == null) {
            return saveMember(attributes, socialType);
        }
        return findUser;
    }

    /**
     * OAuthAttributes의 toEntity() 메소드를 통해 빌더로 User 객체 생성 후 반환
     * 생성된 User 객체를 DB에 저장 : socialType, socialId, email, role 값만 있는 상태
     */
    private Member saveMember(OAuthAttributes attributes, SocialType socialType) {
        Member createdMember = attributes.toEntity(socialType, attributes.getOAuth2Info());
        return memberRepository.save(createdMember);
    }


//    유저가 있으면 업데이트, 없으면 유저 생성
//    private Member saveOrUpdate(OAuth2User oAuth2User){
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String)attributes.get("email");
//        String name = (String)attributes.get("name");
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        Member member = memberRepository.findByEmail(email)
//                .map(entity->entity.updateName(name))
//                .orElse(Member.builder()
//                        .name(name)
//                        .email(email)
//                        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
//                        .build());
//        return memberRepository.save(member);
//    }
}
