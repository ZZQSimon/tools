package cn.com.easyerp.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.stereotype.Component;

@Component
public class AuthAccessDeniedHandler extends AccessDeniedHandlerImpl {
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        if ("GET".equals(request.getMethod())) {
            super.handle(request, response, accessDeniedException);
            return;
        }
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.getWriter()
                .print("{\"accessDenied\":true, \"errorMessage\":\"" + accessDeniedException.getMessage() + "\"}");
    }
}
