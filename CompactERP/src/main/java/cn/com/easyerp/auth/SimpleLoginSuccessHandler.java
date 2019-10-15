package cn.com.easyerp.auth;

import java.io.IOException;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class SimpleLoginSuccessHandler implements AuthenticationSuccessHandler, InitializingBean {
    protected Log logger = LogFactory.getLog(getClass());

    private String defaultTargetUrl = "/index.view";
    private String mobileTargetUrl = "/mobileIndex.view";

    private boolean forwardToDestination = false;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        saveLoginInfo(request, authentication);
        AuthDetails user = (AuthDetails) authentication.getPrincipal();
        if (this.forwardToDestination) {
            this.logger.info("Login success,Forwarding to " + this.defaultTargetUrl);

            request.getRequestDispatcher(this.defaultTargetUrl).forward(request, response);
        } else {
            this.logger.info("Login success,Redirecting to " + this.defaultTargetUrl);
            if (user.getIsMobileLogin() == 1) {
                this.redirectStrategy.sendRedirect(request, response, this.mobileTargetUrl);
            } else {
                this.redirectStrategy.sendRedirect(request, response, this.defaultTargetUrl);
            }
        }
    }

    @SuppressWarnings("unused")
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = { Exception.class })
    public void saveLoginInfo(HttpServletRequest request, Authentication authentication) {
        AuthDetails user = (AuthDetails) authentication.getPrincipal();
        try {
            String ip = getIpAddress(request);
            Date date = new Date();
        } catch (DataAccessException e) {
            if (this.logger.isWarnEnabled()) {
                this.logger.info("无法更新用户登录信息至数据库");
            }
        }
    }

    public String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    public void setForwardToDestination(boolean forwardToDestination) {
        this.forwardToDestination = forwardToDestination;
    }

    public void afterPropertiesSet() {
        if (StringUtils.isEmpty(this.defaultTargetUrl))
            ;
    }
}
