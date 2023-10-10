package aix.project.chatez.member;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import aix.project.chatez.DataNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Long save(MemberForm memberForm){
        return memberRepository.save(Member.builder()
                .name(memberForm.getName())
                .email(memberForm.getEmail())
                .password(passwordEncoder.encode(memberForm.getPassword1()))
                .build()).getMemberNo();
    }

    public Member findByMemberNo(Long memberNo){
        return memberRepository.findById(memberNo)
                .orElseThrow(()-> new IllegalArgumentException("알 수 없는 사용자"));
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("알 수 없는 사용자"));
    }

//    public Member getMember(String name){
//        Optional<Member> member = this.memberRepository.findByName(name);
//        if(member.isPresent()){
//            return member.get();
//        }else {
//            throw new DataNotFoundException("siteUser not found");
//        }
//    }

}
