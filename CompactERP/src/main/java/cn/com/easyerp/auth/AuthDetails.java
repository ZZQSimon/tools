package cn.com.easyerp.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.widget.menu.MenuModel;

public class AuthDetails implements UserDetails {
    private static final long serialVersionUID = 902132188618399529L;

    public class UserMenu {
        private String id;
        private String international_id;
        private String icon;
        private String icon_out;
        private String page_id;
        private List<UserMenu> sub;
        private String[] modules;

        public UserMenu(MenuModel menu, List<UserMenu> sub) {
            this.id = menu.getId();
            this.international_id = menu.getInternational_id();
            this.icon = menu.getIcon();
            this.icon_out = menu.getIcon_out();
            this.page_id = menu.getPage_id();
            this.sub = sub;
            this.modules = menu.getListModule();
        }

        public String getId() {
            return this.id;
        }

        public String getInternational_id() {
            return this.international_id;
        }

        public String getIcon() {
            return this.icon;
        }

        public String getIcon_out() {
            return this.icon_out;
        }

        public String getPage_id() {
            if (this.page_id == null) {
                return "";
            }
            return this.page_id;
        }

        public List<UserMenu> getSub() {
            return this.sub;
        }

        public String[] getModules() {
            return this.modules;
        }
    }

    class AuthGrantedAuthority implements GrantedAuthority {
        private static final long serialVersionUID = 4619463942645292555L;
        private String role;

        private AuthGrantedAuthority(String role) {
            this.role = "ROLE_" + role;
        }

        private AuthGrantedAuthority() {
            this.role = "ROLE_" + role_id;
        }

        public String getAuthority() {
            return this.role;
        }
    }

    public static final String AUTHENTICATED_ROLE = "authenticated_user";
    private static final String AUTHORITY_PREFIX = "ROLE_";
    List<UserMenu> shortcuts = null;
    List<UserMenu> menu = null;
    List<UserMenu> mobileMenu = null;
    List<String> menuNames = null;
    String defaultModule;
    Map<String, List<UserMenu>> moduleMenus = null;
    private String id;
    private String parent_id;
    private String name;
    private String _password;
    private String sex;
    private Date birthdate;
    private String photo;
    private String telephone;
    private String mobile;
    private String email;
    private String msn;
    private String qq;
    private String language_id;
    private String role_id;
    private List<AuthDetailsAdditional> additional;
    private String depts;
    private String roles;
    private List<String> role_ids;
    private String department_id;
    private List<String> department_ids;
    private boolean is_leader;
    private int status;
    private String memo;
    private List<GrantedAuthority> authorities = null;
    private String domain = null;
    private boolean logged = false;
    private String corpId;
    private String secret;
    private String agentId;
    private String position;
    private String imageUrl;
    private int status_wx;
    private int isMobileLogin;
    private String openid;

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getCorpId() {
        return this.corpId;
    }

    public void setCorpId(String corpId) {
        this.corpId = corpId;
    }

    public String getSecret() {
        return this.secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getAgentId() {
        return this.agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStatus_wx() {
        return this.status_wx;
    }

    public void setStatus_wx(int status_wx) {
        this.status_wx = status_wx;
    }

    public int getIsMobileLogin() {
        return this.isMobileLogin;
    }

    public void setIsMobileLogin(int isMobileLogin) {
        this.isMobileLogin = isMobileLogin;
    }

    public String getOpenid() {
        return this.openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getDefaultModule() {
        return this.defaultModule;
    }

    public void setDefaultModule(String defaultModule) {
        this.defaultModule = defaultModule;
    }

    public List<String> getMenuNames() {
        return this.menuNames;
    }

    public void setMenuNames(List<String> menuNames) {
        this.menuNames = menuNames;
    }

    public Map<String, List<UserMenu>> getModuleMenus() {
        return this.moduleMenus;
    }

    public void setModuleMenus(Map<String, List<UserMenu>> moduleMenus) {
        this.moduleMenus = moduleMenus;
    }

    public String getDepts() {
        return this.depts;
    }

    public void setDepts(String depts) {
        this.depts = depts;
    }

    public String getRoles() {
        return this.roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public List<String> getDepartment_ids() {
        return this.department_ids;
    }

    public void setDepartment_ids(List<String> department_ids) {
        this.department_ids = department_ids;
    }

    public List<AuthDetailsAdditional> getAdditional() {
        return this.additional;
    }

    public void setAdditional(List<AuthDetailsAdditional> additional) {
        this.additional = additional;
    }

    public List<String> getRole_ids() {
        return this.role_ids;
    }

    public void setRole_ids(List<String> role_ids) {
        this.role_ids = role_ids;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonIgnore
    public String get_password() {
        return this._password;
    }

    public void set_password(String _password) {
        this._password = _password;
    }

    public String getSex() {
        return this.sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMobile() {
        return this.mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMsn() {
        return this.msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }

    public String getQq() {
        return this.qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getLanguage_id() {
        return this.language_id;
    }

    public void setLanguage_id(String language_id) {
        this.language_id = language_id;
    }

    public String getRole_id() {
        return this.role_id;
    }

    public void setRole_id(String role_id) {
        this.role_id = role_id;
        this.authorities = new ArrayList<>();
        this.authorities.add(new AuthGrantedAuthority());
        this.authorities.add(new AuthGrantedAuthority("authenticated_user"));
    }

    public String getDepartment_id() {
        return this.department_id;
    }

    public void setDepartment_id(String department_id) {
        this.department_id = department_id;
    }

    public boolean isIs_leader() {
        return this.is_leader;
    }

    public void setIs_leader(boolean is_leader) {
        this.is_leader = is_leader;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @JsonIgnore
    public List<UserMenu> getShortcuts() {
        return this.shortcuts;
    }

    @JsonIgnore
    public List<UserMenu> getMenu() {
        return this.menu;
    }

    @JsonIgnore
    public List<UserMenu> getMoblieMenu() {
        return this.mobileMenu;
    }

    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @JsonIgnore
    public String getPassword() {
        return this._password;
    }

    @JsonIgnore
    public String getUsername() {
        return String.format("%s@%s", new Object[] { this.id, this.domain });
    }

    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }

    public String getDomain() {
        return this.domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public boolean isLogged() {
        return this.logged;
    }

    public void setLogged(boolean logged) {
        this.logged = logged;
    }

    public boolean equals(Object obj) {
        if (!AuthDetails.class.isInstance(obj)) {
            return false;
        }
        AuthDetails details = (AuthDetails) obj;
        return (this.id.equals(details.id) && this.domain.equals(details.domain));
    }

    public int hashCode() {
        return getUsername().hashCode();
    }
}