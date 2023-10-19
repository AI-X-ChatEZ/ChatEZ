package aix.project.chatez.oauth2;

import aix.project.chatez.member.SocialType;

import java.util.Map;

public class GoogleOAuth2Info implements OAuth2Info {
    private Map<String, Object> attributes;

    public GoogleOAuth2Info(Map<String, Object> attributes) {
        this.attributes = attributes;
    }


    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getSocialId() {
        return (String) attributes.get("sub");
    }

    @Override
    public SocialType getSocialType() {
        return SocialType.GOOGLE;
    }
}
