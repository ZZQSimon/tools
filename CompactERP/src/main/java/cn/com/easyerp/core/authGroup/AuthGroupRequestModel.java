package cn.com.easyerp.core.authGroup;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class AuthGroupRequestModel extends FormRequestModelBase<AuthGroupModel> {
    private List<AuthGroup> authGroup;
    private List<String> menu_Id;
    private String menuGroupId;

    public Map<String, List<AuthGroup>> getAuthGroupMap() {
        return this.authGroupMap;
    }

    private String upMenuGroupId;
    private Map<String, List<AuthDataGroupDetail>> authDataGroupMap;
    private Map<String, List<AuthGroup>> authGroupMap;

    public void setAuthGroupMap(Map<String, List<AuthGroup>> authGroupMap) {
        this.authGroupMap = authGroupMap;
    }

    public Map<String, List<AuthDataGroupDetail>> getAuthDataGroupMap() {
        return this.authDataGroupMap;
    }

    public void setAuthDataGroupMap(Map<String, List<AuthDataGroupDetail>> authDataGroupMap) {
        this.authDataGroupMap = authDataGroupMap;
    }

    public String getUpMenuGroupId() {
        return this.upMenuGroupId;
    }

    public void setUpMenuGroupId(String upMenuGroupId) {
        this.upMenuGroupId = upMenuGroupId;
    }

    public String getMenuGroupId() {
        return this.menuGroupId;
    }

    public void setMenuGroupId(String menuGroupId) {
        this.menuGroupId = menuGroupId;
    }

    public List<String> getMenu_Id() {
        return this.menu_Id;
    }

    public void setMenu_Id(List<String> menu_Id) {
        this.menu_Id = menu_Id;
    }

    public List<AuthGroup> getAuthGroup() {
        return this.authGroup;
    }

    public void setAuthGroup(List<AuthGroup> authGroup) {
        this.authGroup = authGroup;
    }
}
