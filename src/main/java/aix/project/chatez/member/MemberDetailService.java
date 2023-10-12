package aix.project.chatez.member;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class MemberDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Member loadUserByUsername(String email) {

        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException((email)));

    }
}
