package cn.com.easyerp.auth.ldap;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

public class LdapAuthoritiesMapper implements GrantedAuthoritiesMapper {
    private String mapTable;
    private String ldapId;
    private String dxId;

    public LdapAuthoritiesMapper(String mapTable, String ldapId, String dxId) {
        this.mapTable = mapTable;
        this.ldapId = ldapId;
        this.dxId = dxId;
    }

    public Collection<? extends GrantedAuthority> mapAuthorities(Collection<? extends GrantedAuthority> authorities) {
        return null;
    }
}
