package aix.project.chatez.member;

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

    @ManyToOne
    @JoinColumn(name = "member_no")
    private Member member;
}
