package aix.project.chatez.config.jwt;

import aix.project.chatez.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;

    public String generateToken(Member member, Duration expiredAt){
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()),member);
    }

//    Create Token Method
    private String makeToken(Date expiry, Member member){

        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(member.getEmail())
                .claim("id", member.getMemberNo())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecreteKey())
                .compact();
    }

//    jwt token validation check method
    public boolean validToken(String token){
        try{
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecreteKey())//비밀값으로 복호화
                    .parseClaimsJws(token);
            return true;

        }catch (Exception e){
            return false;

        }
    }

    // get authentication by token based method
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        return new UsernamePasswordAuthenticationToken(new org.springframework.security.core.userdetails.User(claims.getSubject
                (), "", authorities), token, authorities);
    }

    //get memberNo by token based method
    public Long getMemberNo(String token){
        Claims claims = getClaims(token);
        return claims.get("id",Long.class);
    }

    public Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecreteKey())
                .parseClaimsJws(token)
                .getBody();
    }


}
