package aix.project.chatez.config;

import aix.project.chatez.oauth2.OAuth2MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@RequiredArgsConstructor
@Configuration
public class OAuthSecurityConfig {

    private final OAuth2MemberService oAuth2MemberService;


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
                                .requestMatchers("/","/join","/login").permitAll()
                                .anyRequest().authenticated());
//        login & out
        http.formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .usernameParameter("email")
                        .defaultSuccessUrl("/my-service"))
                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                );

        http.oauth2Login((oauth2Login)->oauth2Login
                        .loginPage("/login")
                        .defaultSuccessUrl("/my-service")
                        .userInfoEndpoint()
                        .userService(oAuth2MemberService));


        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public AuthenticationManager authenticationManager(){
//        MemberDetailService memberDetailService = new MemberDetailService(memberRepository,passwordEncoder());
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setPasswordEncoder(passwordEncoder());
//        provider.setUserDetailsService(memberDetailService);
//        return new ProviderManager(provider);
//    }




}
