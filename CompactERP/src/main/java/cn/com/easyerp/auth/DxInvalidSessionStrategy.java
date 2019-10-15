package cn.com.easyerp.auth;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.web.DefaultRedirectStrategy;

import cn.com.easyerp.framework.common.Common;

public class DxInvalidSessionStrategy extends DefaultRedirectStrategy {
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            Common.writeInvalidSessionResult(response);
        } else {
            super.sendRedirect(request, response, url);
        }
    }
}
