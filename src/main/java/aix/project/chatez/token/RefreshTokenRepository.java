package aix.project.chatez.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findById(Long memberNo);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
