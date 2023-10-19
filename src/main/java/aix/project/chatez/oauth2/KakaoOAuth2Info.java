package aix.project.chatez.oauth2;

import aix.project.chatez.member.SocialType;

import java.util.Map;

public class KakaoOAuth2Info implements OAuth2Info {

    private Map<String, Object> attributes;
    private Map<String, Object> kakaoAccountAttributes;
    private Map<String, Object> profileAttributes;

    public KakaoOAuth2Info(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) kakaoAccountAttributes.get("profile");

    }

    @Override
    public String getName() {
        return profileAttributes.get("nickname").toString();
    }

    @Override
    public String getEmail() {
        return kakaoAccountAttributes.get("email").toString();
    }

    @Override
    public String getSocialId() {
        return attributes.get("id").toString();
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.KAKAO;
    }
}
