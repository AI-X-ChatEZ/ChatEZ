package aix.project.chatez.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import aix.project.chatez.DataNotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public Member save(MemberForm memberForm)
//            throws Exception
    {
//        if(memberRepository.findByEmail(memberForm.getEmail()).isPresent()) {
//            throw new Exception("이미 존재하는 이메일입니다.");
//        }
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return memberRepository.save(Member.builder()
                .name(memberForm.getName())
                .email(memberForm.getEmail())
                .password(passwordEncoder.encode(memberForm.getPassword1()))
                .build());
    }

    public Member findByMemberNo(Long memberNo){
        return memberRepository.findById(memberNo)
                .orElseThrow(()-> new IllegalArgumentException("알 수 없는 사용자"));
    }

    public Member findByEmail(String email){
        return memberRepository.findByEmail(email)
                .orElseThrow(()->new IllegalArgumentException("알 수 없는 사용자"));
    }

    public Member findByName(String name){
        Optional<Member> member = this.memberRepository.findByName(name);
        if(member.isPresent()){
            return member.get();
        }else {
            throw new DataNotFoundException("알 수 없는 사용자");
        }
    }

}
