package cn.com.easyerp.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.authGroup.AuthDataGroup;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.MenuGroup;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.framework.common.Common;

@Service
public class AuthService implements UserDetailsService {
    public static final String ACCESS_DENIED_MSG = "access is denied";
    public static final String SELF_USER_ID = "_SELF";
    @Autowired
    private AuthDao authDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private DataService dataService;

    public static AuthDetails getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null)
            return null;
        Object ret = authentication.getPrincipal();
        if (!AuthDetails.class.isInstance(ret))
            return null;
        return (AuthDetails) ret;
    }

    public static String getCurrentUserId() {
        AuthDetails user = getCurrentUser();
        if (user == null) {
            return null;
        }
        return user.getId();
    }

    public List<AuthDetails> loadUsers(Set<String> uids) {
        return this.authDao.selectUsers(uids);
    }

    public AuthDetails loadUser(String uid) throws UsernameNotFoundException {
        if (uid == null)
            return null;
        return loadUser(uid, this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey()));
    }

    public DepartmentDescribe loaddept(String uid) {
        if (uid == null)
            return null;
        return this.authDao.loadDept(uid);
    }

    private AuthDetails loadUser(String uid, String domain) {
        if (Common.isBlank(uid))
            return null;
        AuthDetails details = null;
        try {
            details = this.authDao.selectUser(uid, false, this.cacheService.getDecryptKey(domain));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if ("Default".equals(uid)) {
            details = this.authDao.selectUser(uid, false, this.cacheService.getDecryptKey(domain));
        }
        if (details == null && this.cacheService.getSystemParam().isMobile_login())
            details = this.authDao.selectUser(uid, true, this.cacheService.getDecryptKey(domain));
        if (details == null)
            return null;

        details.setAdditional(this.authDao.selectUserAdditional(uid));

        if (details.getLanguage_id() == null)
            details.setLanguage_id(this.cacheService.getSystemParam().getDefault_language());
        details.setDomain(domain);
        return details;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SecurityContextHolder.getContextHolderStrategy();
        HttpSession currentSession = Common.getSession();

        String[] names = username.split("@");
        boolean isMobile = (names.length == 4);
        this.dxRoutingDataSource.setDomainKey(names[1]);
        AuthDetails details = null;

        try {
            details = loadUser(names[0], names[1]);
            if (names.length > 2 && names[2] != null && !"".equals(names[2]) && !"1".equals(names[2])) {
                details.setLanguage_id(names[2]);
            }

            this.cacheService.reloadAuth();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (details == null || details.getStatus() != 1)
            throw new UsernameNotFoundException("user not found: '" + username + "'");
        try {
            details.shortcuts = generateMenu(details, this.cacheService.getShortcut(),
                    this.cacheService.getMenuAuthGroup());
            details.menu = generateMenu(details, this.cacheService.getMenu(), this.cacheService.getMenuAuthGroup());
            details.mobileMenu = generateMobileMenu(details, this.cacheService.getMenu(),
                    this.cacheService.getMenuAuthGroup());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!isMobile) {
            for (SessionInformation session : this.sessionRegistry.getAllSessions(details, false)) {
                if (!session.getSessionId().equals(currentSession.getId())) {
                    details.setLogged(true);
                }
            }
        }

        if (isMobile)
            details.setIsMobileLogin(Integer.parseInt(names[3]));
        return details;
    }

    public void logoutElse(AuthDetails user, HttpSession currentSession) {
        for (SessionInformation session : this.sessionRegistry.getAllSessions(user, false)) {
            if (!session.getSessionId().equals(currentSession.getId())) {
                session.expireNow();
            }
        }
    }

    public void reload() {
        this.cacheService.reloadAuth();
    }

    public <T> T getOwner(Map<String, T> map) {
        if (map.containsKey("owner")) {
            return (T) map.get("owner");
        }
        return (T) map.get("cre_user");
    }

    private boolean ownerMatched(AuthDetails operator, AuthDetails owner, List<AuthEntryModel.OperatorModel> list) {
        List<String> department_ids = owner.getDepartment_ids();
        List<String> role_ids = owner.getRole_ids();
        for (AuthEntryModel.OperatorModel op : list) {
            if (Common.notBlank(op.user)) {
                if ("*".equals(op.user))
                    return true;
                if ("_SELF".equals(op.user))
                    return operator.getId().equals(owner.getId());
                if (op.user.equals(owner.getId()))
                    return true;
                continue;
            }
            if (("*".equals(op.dept) || op.dept.equals(owner.getDepartment_id()))
                    && ("*".equals(op.role) || op.role.equals(owner.getRole_id()))) {
                return true;
            }

            if (department_ids != null) {
                for (String dept_string : department_ids) {
                    if (("*".equals(op.dept) || op.dept.equals(dept_string)) && role_ids != null) {
                        for (String role_string : role_ids) {
                            if ("*".equals(op.role) || op.role.equals(role_string)) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean operatorMatched(AuthDetails operator, AuthDetails owner, List<AuthEntryModel.OperatorModel> list) {
        List<String> department_ids = operator.getDepartment_ids();
        List<String> role_ids = operator.getRole_ids();
        for (AuthEntryModel.OperatorModel op : list) {
            if (op.user != null) {

                if ("_SELF".equals(op.user)) {

                    if (owner == null || operator.getId().equals(owner.getId()))
                        return true;
                    continue;
                }
                if ("*".equals(op.user) || op.user.equals(operator.getId()))
                    return true;
                continue;
            }
            if (("*".equals(op.dept) || op.dept.equals(operator.getDepartment_id()))
                    && ("*".equals(op.role) || op.role.equals(operator.getRole_id()))) {
                return true;
            }

            if (department_ids != null)
                for (String dept_string : department_ids) {
                    if (("*".equals(op.dept) || op.dept.equals(dept_string)) && role_ids != null) {
                        for (String role_string : role_ids) {
                            if ("*".equals(op.role) || op.role.equals(role_string)) {
                                return true;
                            }
                        }
                    }
                }
        }
        return false;
    }

    public List<AuthEntryModel> entryMatch(AuthDetails operator, AuthDetails owner, List<AuthEntryModel> entries) {
        List<AuthEntryModel> ret = new ArrayList<AuthEntryModel>();
        for (AuthEntryModel entry : entries) {

            if (owner != null && entry.getOwners() != null && !ownerMatched(operator, owner, entry.getOwners())) {
                continue;
            }
            if (operatorMatched(operator, owner, entry.getOperators()))
                ret.add(entry);
        }
        return (ret.size() == 0) ? null : ret;
    }

    public AuthConfigDescribe getAuthControl(AuthConfigDescribe.Target target, String id, AuthConfigDescribe.Type op) {
        return (AuthConfigDescribe) this.cacheService.getAuthControl(target)
                .get(CacheService.makeAuthConfigKey(id, op));
    }

    public List<AuthDetails> authorizedUsers(AuthConfigDescribe.Target target, String id, AuthConfigDescribe.Type op,
            AuthDetails owner) {
        return authorizedUsers(target, id, op, owner, loadUsers(null));
    }

    public List<AuthDetails> authorizedUsers(AuthConfigDescribe.Target target, String id, AuthConfigDescribe.Type op,
            AuthDetails owner, List<AuthDetails> users) {
        return authorizedUsers(getAuthControl(target, id, op), owner, users);
    }

    public List<AuthDetails> authorizedUsers(AuthConfigDescribe control, AuthDetails owner, List<AuthDetails> users) {
        if (control == null)
            return users;
        List<AuthEntryModel> entries = control.getEntries();
        List<AuthDetails> ret = new ArrayList<AuthDetails>();
        if (entries == null)
            return ret;
        for (AuthDetails user : users) {
            if (entryMatch(user, owner, entries) != null)
                ret.add(user);
        }
        return ret;
    }

    public Set<String> noAuthColumns(TableDescribe table, AuthDetails user, AuthConfigDescribe.Type type) {
        Set<String> ret = new HashSet<String>();
        for (ColumnDescribe column : table.getColumns()) {
            String name = column.getColumn_name();
            if (!newAuth(user, this.cacheService.getColumnAuth(columnAuthID(table.getId(), column.getColumn_name())), 3,
                    null)) {
                ret.add(name);
            }
        }
        return (ret.size() > 0) ? ret : null;
    }

    public String columnAuthID(String table, String column) {
        return table + '.' + column;
    }

    private List<AuthDetails.UserMenu> generateMenu(AuthDetails user, List<MenuModel> root,
            List<AuthGroup> authGroups) {
        List<AuthDetails.UserMenu> ret = new ArrayList<AuthDetails.UserMenu>();
        if (root == null)
            return ret;
        for (MenuModel menu : root) {
            AuthDetails.UserMenu userMenu;
            if (menu.getIs_mobile_menu() == 1)
                continue;
            if (menu.getSub() != null) {
                List<AuthDetails.UserMenu> sub = generateMenu(user, menu.getSub(), authGroups);
                if (sub.size() == 0)
                    continue;
                userMenu = new AuthDetails().new UserMenu(menu, sub);
            } else {
                if (authGroups == null || !newAuth(user, authGroups, 1, menu))
                    continue;
                userMenu = new AuthDetails().new UserMenu(menu, null);
            }
            ret.add(userMenu);
        }
        return ret;
    }

    private List<AuthDetails.UserMenu> generateMobileMenu(AuthDetails user, List<MenuModel> root,
            List<AuthGroup> authGroups) {
        List<AuthDetails.UserMenu> ret = new ArrayList<AuthDetails.UserMenu>();
        if (root == null)
            return ret;
        for (MenuModel menu : root) {
            AuthDetails.UserMenu userMenu;
            if (menu.getIs_mobile_menu() == 0)
                continue;
            if (menu.getSub() != null) {
                List<AuthDetails.UserMenu> sub = generateMobileMenu(user, menu.getSub(), authGroups);
                if (sub.size() == 0)
                    continue;
                userMenu = new AuthDetails().new UserMenu(menu, sub);
            } else {
                if (authGroups == null || !newAuth(user, authGroups, 1, menu))
                    continue;
                userMenu = new AuthDetails().new UserMenu(menu, null);
            }
            ret.add(userMenu);
        }
        return ret;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void menuModule(AuthDetails user, List<AuthDetails.UserMenu> userMenus) {
        List<String> menuNames = new ArrayList<String>();
        Map<String, List<AuthDetails.UserMenu>> moduleMenus = new HashMap<String, List<AuthDetails.UserMenu>>();
        for (int i = 0; i < userMenus.size(); i++) {
            String module;
            if (((AuthDetails.UserMenu) userMenus.get(i)).getModules() == null
                    || ((AuthDetails.UserMenu) userMenus.get(i)).getModules().length == 0) {
                module = "";
            } else {
                module = ((AuthDetails.UserMenu) userMenus.get(i)).getModules()[0];
            }
            if (menuNames.contains(module)) {
                ((List) moduleMenus.get(module)).add(userMenus.get(i));
            } else {
                menuNames.add(module);
                moduleMenus.put(module, new ArrayList<>());
                ((List) moduleMenus.get(module)).add(userMenus.get(i));
            }
            if (i == 0) {
                user.setDefaultModule(module);
            }
        }
        user.setMenuNames(menuNames);
        user.setModuleMenus(moduleMenus);
    }

    public boolean newAuth(AuthDetails user, String menuId) {
        List<AuthGroup> menuAuthGroup = this.cacheService.getMenuAuthGroup();
        for (AuthGroup authGroup : menuAuthGroup) {
            List<MenuGroup> menus = authGroup.getMenus();
            for (MenuGroup menuGroup : menus) {
                if (menuId.equals(menuGroup.getMenu_id()) && operatorAuth(user, authGroup, null)) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<AuthGroup> getOptionAuthGroup(String tableName) {
        return this.cacheService.getOptionAuth(tableName);
    }

    public List<AuthGroup> getOptionAuthGroup(String tableName, String type) {
        List<AuthGroup> optionAuth = this.cacheService.getOptionAuth(tableName);
        List<AuthGroup> optionAuthByType = new ArrayList<AuthGroup>();
        if (optionAuth == null)
            return null;
        for (AuthGroup authGroup : optionAuth) {
            switch (type) {
            case "read":
                if (authGroup.getRead() != null && !"".equals(authGroup.getRead()))
                    optionAuthByType.add(authGroup);
                break;
            case "create":
                if (authGroup.getCreate() != null && !"".equals(authGroup.getCreate()))
                    optionAuthByType.add(authGroup);
                break;
            case "update":
                if (authGroup.getUpdate() != null && !"".equals(authGroup.getUpdate()))
                    optionAuthByType.add(authGroup);
                break;
            case "delete":
                if (authGroup.getDelete() != null && !"".equals(authGroup.getDelete()))
                    optionAuthByType.add(authGroup);
                break;
            case "operate":
                if (authGroup.getOperate() != null && !"".equals(authGroup.getOperate())) {
                    optionAuthByType.add(authGroup);
                }
                break;
            }

        }
        return optionAuthByType;
    }

    public boolean newAuth(AuthDetails user, List<AuthGroup> authGroups, int type, Object obj) {
        switch (type) {
        case 1:
            if (obj instanceof MenuModel) {
                MenuModel menuModel = (MenuModel) obj;
                return menuAuth(user, authGroups, menuModel);
            }
            return false;

        case 3:
            if (authGroups == null || authGroups.size() == 0)
                return true;
            for (AuthGroup authGroup : authGroups) {
                if (operatorAuth(user, authGroup, null))
                    return true;
            }
            return false;
        }

        return false;
    }

    public boolean newAuthColumn(AuthDetails user, String key) {
        return newAuth(user, this.cacheService.getColumnAuth(key), 3, null);
    }

    public boolean menuAuth(AuthDetails user, List<AuthGroup> authGroups, MenuModel menu) {
        // int count = 0;
        if ("super".equals(user.getId().toLowerCase()))
            return true;
        for (AuthGroup authGroup : authGroups) {
            List<MenuGroup> menus = authGroup.getMenus();
            if (menus == null)
                continue;
            for (MenuGroup menuGroup : menus) {
                if (menuGroup.getMenu_id().equals(menu.getId())) {
                    if (operatorAuth(user, authGroup, null))
                        return true;
                    // count++;
                }
            }
        }

        return false;
    }

    public void assertAuth(AuthDetails user, String tableName, String actionId) {
        assertAuth(user, tableName, actionId, null);
    }

    public void assertAuth(AuthDetails user, String tableName, String actionId, Object ownerId) {
        if (!optionAuth(user, tableName, actionId, ownerId))
            throw new ApplicationException("access is denied");
    }

    public boolean exportAuth(AuthDetails user, String tableName, Object ownerId, boolean isSelected) {
        if (ownerId == null)
            ownerId = "";
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        List<AuthGroup> optionAuth = this.cacheService.getOptionAuth(tableName);

        if (table.getIs_auth() == 1 && (optionAuth == null || optionAuth.size() == 0)) {
            return false;
        }
        if (table.getIs_auth() != 1) {
            return true;
        }
        for (AuthGroup authGroup : optionAuth) {
            if (authGroup.getExport_auth() == null || "".equals(authGroup.getExport_auth()))
                continue;
            if (isSelected && "allOut".equals(authGroup.getExport_auth()))
                continue;
            if (!isSelected && "selected".equals(authGroup.getExport_auth()))
                continue;
            if (operatorAuth(user, authGroup, ownerId.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean importAuth(AuthDetails user, String tableName, Object ownerId, boolean isInsert) {
        if (ownerId == null)
            ownerId = "";
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        List<AuthGroup> optionAuth = this.cacheService.getOptionAuth(tableName);

        if (table.getIs_auth() == 1 && (optionAuth == null || optionAuth.size() == 0)) {
            return false;
        }
        if (table.getIs_auth() != 1) {
            return true;
        }
        for (AuthGroup authGroup : optionAuth) {
            if (authGroup.getImport_auth() == null || "".equals(authGroup.getImport_auth()))
                continue;
            if (isInsert && "update".equals(authGroup.getImport_auth()))
                continue;
            if (!isInsert && "insert".equals(authGroup.getImport_auth()))
                continue;
            if (operatorAuth(user, authGroup, ownerId.toString())) {
                return true;
            }
        }
        return false;
    }

    public boolean optionAuth(AuthDetails user, String tableName, String actionId, Object ownerId) {
        if (ownerId == null)
            ownerId = "";
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        List<AuthGroup> optionAuth = this.cacheService.getOptionAuth(tableName);

        if (table.getIs_auth() == 1 && (optionAuth == null || optionAuth.size() == 0)) {
            return false;
        }
        if (table.getIs_auth() != 1) {
            return true;
        }
        for (AuthGroup authGroup : optionAuth) {
            if ((actionId.equals(authGroup.getRead()) || actionId.equals(authGroup.getCreate())
                    || actionId.equals(authGroup.getDelete()) || actionId.equals(authGroup.getUpdate())
                    || (authGroup.getOperate() != null && authGroup.getOperate().indexOf(actionId) != -1))
                    && operatorAuth(user, authGroup, ownerId.toString())) {
                return true;
            }
        }

        return false;
    }

    public boolean optionAuth(AuthDetails user, String tableName, String actionId, Object ownerId,
            Map<String, Object> ids) {
        if (ownerId == null)
            ownerId = "";
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        List<AuthGroup> optionAuth = getAuthGroup(tableName, ids);

        if (table.getIs_auth() == 1 && (optionAuth == null || optionAuth.size() == 0)) {
            return false;
        }
        if (table.getIs_auth() != 1) {
            return true;
        }
        for (AuthGroup authGroup : optionAuth) {
            if ((actionId.equals(authGroup.getRead()) || actionId.equals(authGroup.getCreate())
                    || actionId.equals(authGroup.getDelete()) || actionId.equals(authGroup.getUpdate())
                    || (authGroup.getOperate() != null && authGroup.getOperate().indexOf(actionId) != -1))
                    && operatorAuth(user, authGroup, ownerId.toString())) {
                return true;
            }
        }

        return false;
    }

    private boolean operatorAuth(AuthDetails user, AuthGroup authGroup, String ownerId) {
        AuthDetails owner = loadUser(ownerId);
        if ("*".equals(authGroup.getUser()) || user.getId().equals(authGroup.getUser())) {
            return true;
        }
        if ("*".equals(authGroup.getDepartment())) {
            if ("*".equals(authGroup.getRole())) {
                return true;
            }
            if (authGroup.getRole().equals(user.getRole_id())) {
                return true;
            }
            if (user.getAdditional() != null && user.getAdditional().size() != 0) {
                for (AuthDetailsAdditional additional : user.getAdditional()) {
                    if (authGroup.getRole().equals(additional.getRole_id())) {
                        return true;
                    }
                }
            }
        } else if (!"*".equals(authGroup.getDepartment()) && authGroup.getDepartment() != null
                && !"".equals(authGroup.getDepartment())
                && (authGroup.getDepartment_relation() == null || "".equals(authGroup.getDepartment_relation()))) {
            if ("*".equals(authGroup.getRole())) {
                if (authGroup.getDepartment().equals(user.getDepartment_id())) {
                    return true;
                }
                if (user.getAdditional() != null && user.getAdditional().size() != 0)
                    for (AuthDetailsAdditional additional : user.getAdditional()) {
                        if (authGroup.getDepartment().equals(additional.getDepartment_id())) {
                            return true;
                        }
                    }
            } else {
                if (authGroup.getDepartment().equals(user.getDepartment_id())
                        && authGroup.getRole().equals(user.getRole_id())) {
                    return true;
                }
                if (user.getAdditional() != null && user.getAdditional().size() != 0) {
                    for (AuthDetailsAdditional additional : user.getAdditional()) {
                        if (authGroup.getDepartment().equals(additional.getDepartment_id())
                                && authGroup.getRole().equals(additional.getRole_id())) {
                            return true;
                        }
                    }
                }
            }
        }
        if (owner != null) {
            if (authGroup.getUser_relation() != null && !"".equals(authGroup.getUser_relation())) {
                AuthDetails parent_3;
                AuthDetails parent;
                switch (authGroup.getUser_relation()) {
                case "0":
                    if (owner.getId().equals(user.getId()))
                        return true;
                    break;
                case "1":
                    if (owner.getParent_id().equals(user.getId()))
                        return true;
                    break;
                case "2":
                    parent = loadUser(owner.getParent_id());
                    if (parent != null && parent.getParent_id().equals(user.getId()))
                        return true;
                    break;
                case "3":
                    parent_3 = loadUser(owner.getParent_id());
                    if (parent_3 != null) {
                        AuthDetails parent_3_1 = loadUser(parent_3.getParent_id());
                        if (parent_3_1.getParent_id().equals(user.getId()))
                            return true;
                    }
                    break;
                default:
                    return false;
                }
            }
            if (authGroup.getDepartment_relation() != null && !"".equals(authGroup.getDepartment_relation())) {
                DepartmentDescribe parentDept_3_pare;
                DepartmentDescribe parentDept_3;
                DepartmentDescribe ownerDept_3;
                DepartmentDescribe parentDept;
                DepartmentDescribe ownerDept_2;
                DepartmentDescribe ownerDept;
                switch (authGroup.getDepartment_relation()) {
                case "0":
                    if ("*".equals(authGroup.getRole())) {
                        if (owner.getDepartment_id().equals(user.getDepartment_id())) {
                            return true;
                        }
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (owner.getDepartment_id().equals(additional.getDepartment_id())) {
                                    return true;
                                }
                            }
                    } else {
                        if (owner.getDepartment_id() == null || user.getRole_id() == null) {
                            return false;
                        }
                        if (owner.getDepartment_id().equals(user.getDepartment_id())
                                && user.getRole_id().equals(authGroup.getRole())) {
                            return true;
                        }
                        if (user.getAdditional() != null && user.getAdditional().size() != 0) {
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (owner.getDepartment_id().equals(additional.getDepartment_id())
                                        && authGroup.getRole().equals(additional.getRole_id())) {
                                    return true;
                                }
                            }
                        }
                    }

                    return false;
                case "1":
                    ownerDept = loaddept(owner.getId());
                    if (ownerDept == null)
                        return false;
                    if ("*".equals(authGroup.getRole())) {
                        if (ownerDept.getParent_id().equals(user.getDepartment_id()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (ownerDept.getParent_id().equals(additional.getDepartment_id()))
                                    return true;
                            }
                    } else {
                        if (ownerDept.getParent_id().equals(user.getDepartment_id())
                                && user.getRole_id().equals(authGroup.getRole()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (ownerDept.getParent_id().equals(additional.getDepartment_id())
                                        && authGroup.getRole().equals(additional.getRole_id()))
                                    return true;
                            }
                    }
                    return false;
                case "2":
                    ownerDept_2 = loaddept(owner.getId());
                    if (ownerDept_2 == null)
                        return false;
                    parentDept = loaddept(ownerDept_2.getParent_id());
                    if (parentDept == null)
                        return false;
                    if ("*".equals(authGroup.getRole())) {
                        if (parentDept.getParent_id().equals(user.getDepartment_id()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (parentDept.getParent_id().equals(additional.getDepartment_id()))
                                    return true;
                            }
                    } else {
                        if (parentDept.getParent_id().equals(user.getDepartment_id())
                                && user.getRole_id().equals(authGroup.getRole()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (parentDept.getParent_id().equals(additional.getDepartment_id())
                                        && authGroup.getRole().equals(additional.getRole_id()))
                                    return true;
                            }
                    }
                    return false;
                case "3":
                    ownerDept_3 = loaddept(owner.getId());
                    if (ownerDept_3 == null)
                        return false;
                    parentDept_3 = loaddept(ownerDept_3.getParent_id());
                    if (parentDept_3 == null)
                        return false;
                    parentDept_3_pare = loaddept(parentDept_3.getParent_id());
                    if (parentDept_3_pare == null)
                        return false;
                    if ("*".equals(authGroup.getRole())) {
                        if (parentDept_3_pare.getParent_id().equals(user.getDepartment_id()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (parentDept_3_pare.getParent_id().equals(additional.getDepartment_id()))
                                    return true;
                            }
                    } else {
                        if (parentDept_3_pare.getParent_id().equals(user.getDepartment_id())
                                && user.getRole_id().equals(authGroup.getRole()))
                            return true;
                        if (user.getAdditional() != null && user.getAdditional().size() != 0)
                            for (AuthDetailsAdditional additional : user.getAdditional()) {
                                if (parentDept_3_pare.getParent_id().equals(additional.getDepartment_id())
                                        && authGroup.getRole().equals(additional.getRole_id()))
                                    return true;
                            }
                    }
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<AuthGroup> getAuthGroup(String tableName, Map<String, Object> ids) {
        List<AuthGroup> optionAuthGroup = this.cacheService.getOptionAuth(tableName);
        List<AuthGroup> resultAuthGroup = new ArrayList<AuthGroup>();
        Map<String, String> authDataGroup = new HashMap<String, String>();
        Map<String, List<AuthGroup>> authDataGroupMap = new HashMap<String, List<AuthGroup>>();
        if (optionAuthGroup == null || optionAuthGroup.size() == 0)
            return null;
        for (AuthGroup authGroup : optionAuthGroup) {
            if (authGroup.getData_group_id() != null && !"".equals(authGroup.getData_group_id())) {
                AuthDataGroup dataGroup = this.cacheService.getAuthDataGroup(authGroup.getData_group_id());

                if (authDataGroup.get(authGroup.getData_group_id()) == null) {
                    String dataGroupFilter = this.dataService.getAuthDataGroupCondition(dataGroup, tableName);
                    if (dataGroupFilter != null) {
                        authDataGroup.put(authGroup.getData_group_id(), dataGroupFilter);
                    }
                }
                if (authDataGroupMap.get(authGroup.getData_group_id()) == null) {
                    authDataGroupMap.put(authGroup.getData_group_id(), new ArrayList<>());
                }
                ((List) authDataGroupMap.get(authGroup.getData_group_id())).add(authGroup);
                continue;
            }
            resultAuthGroup.add(authGroup);
        }

        if (authDataGroup.size() == 0) {
            return optionAuthGroup;
        }
        List<Map<String, Object>> dataGroupCount = this.authDao.selectDataGroup(tableName, authDataGroup, ids);
        if (dataGroupCount == null || dataGroupCount.size() == 0) {
            return null;
        }
        if (dataGroupCount.size() != 0) {
            for (int i = 0; i < dataGroupCount.size(); i++) {
                if (!"0".equals(((Map) dataGroupCount.get(i)).get("group_count").toString())) {
                    resultAuthGroup.addAll((Collection) authDataGroupMap
                            .get(((Map) dataGroupCount.get(i)).get("group_id").toString()));
                }
            }
        }
        return resultAuthGroup;
    }
}
