package aix.project.chatez.myservice;

import aix.project.chatez.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@Entity
public class MyService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "service_no")
    private Long serviceNo;

    @Column(name = "service_id", unique = true)
    private String serviceId;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "profile_pic", nullable = false)
    private String profilePic;

    @ManyToOne(fetch=FetchType.LAZY, optional=false)
    @JoinColumn(name="member_no", referencedColumnName="member_no")
    private Member member;

    @Builder
    public MyService(String serviceName,String serviceId, String url, String profilePic, Member member){
        this.serviceName = serviceName;
        this.serviceId = serviceId;
        this.url = url;
        this.profilePic = profilePic;
        this.member = member;
    }

    public void updateServiceName(String newServiceName){
        this.serviceName = newServiceName;
    }

    public void updateProfilePic(String newFileName){
        this.profilePic = newFileName;
    }


}

