package aix.project.chatez.oauth2;

import aix.project.chatez.member.SocialType;


public interface OAuth2Info {

   String getName();
   String getEmail();
   String getSocialId();
   SocialType getSocialType();

}
