package aix.project.chatez.member;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static org.springframework.security.core.userdetails.User.builder;

@RequiredArgsConstructor
@Service
public class MemberDetailService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final HttpSession session;
//    private final PasswordEncoder passwordEncoder;
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        return new MemberDetails(member, null);
    }
//    private MemberDetails createUserDetails(Member member){
//        return Member.builder()
//                .username(member.getEmail())
//                .password(passwordEncoder.encode(member.getPassword()))
//                .build();
//    }
}
