package aix.project.chatez.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class ChatEZAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if(!request.getRequestURI().equals("/login")) {
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().print("<script>alert('로그인이 필요한 페이지입니다.'); location.href='/';</script>");
            response.getWriter().flush();
        }
    }

}
