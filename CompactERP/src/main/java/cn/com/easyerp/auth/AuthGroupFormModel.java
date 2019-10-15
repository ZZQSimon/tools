package cn.com.easyerp.auth;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("authGroup")
public class AuthGroupFormModel extends FormModelBase {
    private List<AuthControlDescribe> controls;
    private List<AuthOption> users;
    private List<AuthOption> depts;
    private List<AuthOption> roles;

    protected AuthGroupFormModel(List<AuthControlDescribe> controls) {
        super(ActionType.edit, null);
        this.controls = controls;
    }

    public String getTitle() {
        return Common.getDataService().getMessageText("Auth", new Object[0]);
    }

    public String getIcon() {
        return "auth";
    }

    public List<AuthControlDescribe> getControls() {
        return this.controls;
    }

    @JsonIgnore
    public List<AuthOption> getUsers() {
        return this.users;
    }

    public void setUsers(List<AuthOption> users) {
        this.users = users;
    }

    @JsonIgnore
    public List<AuthOption> getDepts() {
        return this.depts;
    }

    public void setDepts(List<AuthOption> depts) {
        this.depts = depts;
    }

    @JsonIgnore
    public List<AuthOption> getRoles() {
        return this.roles;
    }

    public void setRoles(List<AuthOption> roles) {
        this.roles = roles;
    }
}