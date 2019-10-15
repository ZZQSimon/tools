package cn.com.easyerp.auth;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

public class AuthEntryPoint extends LoginUrlAuthenticationEntryPoint {
    public static final String LOGIN_FORM_URL = "/auth/login.view";

    public AuthEntryPoint() {
        super("/auth/login.view");
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException authException) throws IOException, ServletException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        if (parameterMap != null) {
            if (parameterMap.get("domain") != null)
                request.getSession().setAttribute("domain", parameterMap.get("domain")[0]);
            if (parameterMap.get("emailDetail") != null) {
                request.getSession().setAttribute("emailDetail", parameterMap.get("emailDetail")[0]);
            }
        }
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            response.setStatus(401);
        } else {
            super.commence(request, response, authException);
        }
    }
}