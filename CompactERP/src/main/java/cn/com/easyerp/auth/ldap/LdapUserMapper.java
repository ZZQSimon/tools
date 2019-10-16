package cn.com.easyerp.auth.ldap;

import java.util.Collection;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;

import cn.com.easyerp.auth.AuthService;

public class LdapUserMapper implements UserDetailsContextMapper {
    private static ThreadLocal<String> dxDomain;
    private AuthService authService;
    private String mapKey;

    LdapUserMapper(AuthService authService, String mapKey) {
        this.authService = authService;
        this.mapKey = mapKey;
    }

    private static String getDxDomain() {
        return (String) dxDomain.get();
    }

    static void setDxDomain(String domain) {
        dxDomain.set(domain);
    }

    public UserDetails mapUserFromContext(DirContextOperations dirContextOperations, String s,
            Collection<? extends GrantedAuthority> collection) {
        String dxUser = dirContextOperations.getStringAttribute(this.mapKey);
        return this.authService.loadUserByUsername(dxUser + "@" + getDxDomain());
    }

    public void mapUserToContext(UserDetails userDetails, DirContextAdapter dirContextAdapter) {
    }
}
