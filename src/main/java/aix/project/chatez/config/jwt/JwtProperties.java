package aix.project.chatez.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties("jwt")//자바 클래스에 프로퍼티 값을 가져와서 사용하는 annotaion
public class JwtProperties {

    private String issuer;
    private String secreteKey;
}
