package aix.project.chatez.ai;

import aix.project.chatez.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class AI {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_id")
    private Long aiId;

    @Column(name="ai_name", nullable=false, unique = true)
    private String aiName;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member", referencedColumnName="member_no")
    private Member member;

    @Column(name="profile_pic")
    private String profilePic;

}
