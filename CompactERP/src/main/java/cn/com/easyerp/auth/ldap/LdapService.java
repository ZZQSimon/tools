package cn.com.easyerp.auth.ldap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.ad.ActiveDirectoryLdapAuthenticationProvider;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.auth.DxAuthenticationProvider;
import cn.com.easyerp.core.logger.Loggable;

public class LdapService implements DxAuthenticationProvider {
    private static final String DxUserNameMapperKey = "dxUser";
    @Loggable
    private Logger logger;
    private String domain;
    private String url;
    private ActiveDirectoryLdapAuthenticationProvider adProvider;
    private String userMapKey = DxUserNameMapperKey;

    @Autowired
    private AuthService authService;

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setUserMapKey(String userMapKey) {
        this.userMapKey = userMapKey;
    }

    @PostConstruct
    public void init() {
        initLdap();
        this.adProvider = new ActiveDirectoryLdapAuthenticationProvider(this.domain, this.url);
        this.adProvider.setUserDetailsContextMapper(new LdapUserMapper(this.authService, this.userMapKey));
    }

    private void initLdap() {
        System.setProperty("com.sun.jndi.ldap.connect.pool.debug", "fine");
        System.setProperty("com.sun.jndi.ldap.connect.pool.maxsize", String.valueOf(20));
    }

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication;

        String dxUser = (String) token.getPrincipal();
        int i = dxUser.indexOf('@');
        String dxDomain = dxUser.substring(i + 1);
        String user = dxUser.substring(0, i + 1).concat(this.domain);
        UsernamePasswordAuthenticationToken ldapToken = new UsernamePasswordAuthenticationToken(user,
                token.getCredentials());
        ldapToken.setDetails(authentication.getDetails());
        LdapUserMapper.setDxDomain(dxDomain);
        return this.adProvider.authenticate(ldapToken);
    }

    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
