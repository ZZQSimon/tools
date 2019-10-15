package cn.com.easyerp.auth;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

public class SimpleLogOutSuccessHandler extends SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        if (authentication.getPrincipal() == null) {
            String refererUrl = request.getHeader("Referer");
            response.sendRedirect(refererUrl);
        } else {
            AuthDetails user = (AuthDetails) authentication.getPrincipal();
            String contextPath = request.getContextPath();
            String path = (contextPath == null || "".equals(contextPath)) ? "/" : contextPath;
            if (user.getIsMobileLogin() == 1)
                path = path + "/auth/mobile.view";
            if (user.getDomain() != null && !"".equals(user.getDomain())) {
                path = path + "?domain=" + user.getDomain();
            }
            response.sendRedirect(path);
        }
        super.onLogoutSuccess(request, response, authentication);
    }
}
