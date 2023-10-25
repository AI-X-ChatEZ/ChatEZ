package aix.project.chatez.member;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor
@Entity
public class Member{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_no")
    private Long memberNo;

    private String oauth2Id;

    @Column(name = "name",columnDefinition = "VARCHAR(255)")
    private String name;

    @Column(name = "email",columnDefinition = "VARCHAR(255)", nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    private String socialId;

    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @Column(name = "password", nullable = false)
    private String password;


    @Builder
    public Member(String oauth2Id, String name, String password, String email, MemberRole role, SocialType socialType, String socialId) {
        this.oauth2Id=oauth2Id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.role = role;
        this.socialType = socialType;
        this.socialId = socialId;
    }
}
