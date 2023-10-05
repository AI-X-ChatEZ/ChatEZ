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

    public Member save(String name, String email, String password ){
        Member member = new Member();
        member.setName(name);
        member.setEmail(email);
        member.setPassword( passwordEncoder.encode(password));
        this.memberRepository.save(member);
        return  member;
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
