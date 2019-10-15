package cn.com.easyerp.core.approve;

public class GetApproveUserDescribe {
    private int type;
    private String dept;
    private String role;
    private String user;
    private int dept_rela;
    private int user_rela;

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getDept() {
        return this.dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getDept_rela() {
        return this.dept_rela;
    }

    public void setDept_rela(int dept_rela) {
        this.dept_rela = dept_rela;
    }

    public int getUser_rela() {
        return this.user_rela;
    }

    public void setUser_rela(int user_rela) {
        this.user_rela = user_rela;
    }
}
