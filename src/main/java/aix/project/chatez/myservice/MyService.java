package aix.project.chatez.myservice;

import aix.project.chatez.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class MyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_no")
    private Long serviceNo;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "profile_pic", nullable = false)
    private String profilePic;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member_no", referencedColumnName="member_no")
    private Member member;
}
