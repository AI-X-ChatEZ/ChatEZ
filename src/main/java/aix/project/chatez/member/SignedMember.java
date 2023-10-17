package aix.project.chatez.member;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Getter
public class SignedMember extends MemberDetails {
    private Member member;
    private Map<String, Object> attributes;
    public SignedMember(Member member) {
        super(member);
        this.member = member;
    }
    public SignedMember(Member member, Map<String, Object> attributes){
        super(member, attributes);
        this.member = member;
        this.attributes = attributes;
    }
}
