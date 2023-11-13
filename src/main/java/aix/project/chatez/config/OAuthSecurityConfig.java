package aix.project.chatez.config;

import aix.project.chatez.oauth2.OAuth2MemberService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.io.PrintWriter;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class OAuthSecurityConfig {

    private final OAuth2MemberService oAuth2MemberService;

    private final S3Properties s3Properties;


    @Bean
    public WebSecurityCustomizer configure(){
        return(web)->web.ignoring()
                .requestMatchers("/img/**","/css/**","/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(crsf -> crsf.disable());

        http.authorizeHttpRequests((authorizeRequest) ->
                        authorizeRequest
                                .requestMatchers("/","/join","/login","/welcome").permitAll()
                                .anyRequest().authenticated());

        http.exceptionHandling((exception)->
                exception.authenticationEntryPoint(new ChatEZAuthenticationEntryPoint()));
//        login & out
        http.formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .successHandler(loginSuccessHandler())
                        .failureHandler(loginFailureHandler())
                )
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        http.oauth2Login((oauth2Login)->oauth2Login
                .loginPage("/login")
                .successHandler(loginSuccessHandler())
                .failureHandler(loginFailureHandler())
                .userInfoEndpoint()
                .userService(oAuth2MemberService));


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler loginSuccessHandler() {
        return new SimpleUrlAuthenticationSuccessHandler() {

            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                                Authentication authentication) throws IOException, ServletException {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();

                String builder = "<script type='text/javascript'>" +
                        "alert('로그인이 성공하였습니다.');" +
                        "location.href='/my_service';" +
                        "</script>";

                out.print(builder);
                out.flush();
            }
        };
    }

    @Bean
    public AuthenticationFailureHandler loginFailureHandler() {
        return new SimpleUrlAuthenticationFailureHandler() {

            @Override
            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException exception) throws IOException, ServletException {
                response.setContentType("text/html;charset=UTF-8");
                PrintWriter out = response.getWriter();

                String errorMessage = "로그인 실패";

                if (exception instanceof BadCredentialsException) {
                    errorMessage = "아이디와 비밀번호를 확인하세요.";
                } else if (exception instanceof UsernameNotFoundException) {
                    errorMessage = "회원 가입이 필요합니다.";
                } // 필요하다면 여기에 다른 예외 처리도 추가 가능

                String builder = "<script type='text/javascript'>" +
                        "alert('" + errorMessage + "');" +
                        "location.href='/';" + // 실패시 다시 로그인 페이지로 이동
                        "</script>";

                out.print(builder);
                out.flush();
            }
        };
    }

    @Bean
    public AmazonS3Client amazonS3Client(){
        BasicAWSCredentials credentials = new BasicAWSCredentials(s3Properties.getCredentialsAccessKey(), s3Properties.getCredentialsSecreteKey());

        return (AmazonS3Client) AmazonS3ClientBuilder
                .standard()
                .withRegion(s3Properties.getRegionStatic())
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .build();
    }


}