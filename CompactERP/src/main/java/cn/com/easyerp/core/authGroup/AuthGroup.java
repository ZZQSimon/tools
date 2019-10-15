package cn.com.easyerp.core.authGroup;

import java.util.List;

public class AuthGroup {
    private String department;
    private String department_relation;
    private String role;
    private String user;
    private String user_relation;
    private String read;
    private String create;
    private String update;
    private String delete;
    private String operate;
    private String import_auth;
    private String export_auth;
    private int editOrdetele = 0;
    private int type;
    private String table;
    private String fields;
    private String menu;
    private List<MenuGroup> menus;
    private String template;
    private String column;
    private String userName;
    private String deptName;
    private String roleName;
    private String department_relation_name;
    private String user_relation_name;
    private String block;
    private String editTemplateName;
    private String data_group_id;
    private String ref_column_dept;
    private String ref_column_user;

    public String getRef_column_dept() {
        return this.ref_column_dept;
    }

    public void setRef_column_dept(String ref_column_dept) {
        this.ref_column_dept = ref_column_dept;
    }

    public String getRef_column_user() {
        return this.ref_column_user;
    }

    public void setRef_column_user(String ref_column_user) {
        this.ref_column_user = ref_column_user;
    }

    public String getEditTemplateName() {
        return this.editTemplateName;
    }

    public void setEditTemplateName(String editTemplateName) {
        this.editTemplateName = editTemplateName;
    }

    public int getEditOrdetele() {
        return this.editOrdetele;
    }

    public void setEditOrdetele(int editOrdetele) {
        this.editOrdetele = editOrdetele;
    }

    public List<MenuGroup> getMenus() {
        return this.menus;
    }

    public void setMenus(List<MenuGroup> menus) {
        this.menus = menus;
    }

    public String getBlock() {
        return this.block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getDepartment_relation_name() {
        return this.department_relation_name;
    }

    public void setDepartment_relation_name(String department_relation_name) {
        this.department_relation_name = department_relation_name;
    }

    public String getUser_relation_name() {
        return this.user_relation_name;
    }

    public void setUser_relation_name(String user_relation_name) {
        this.user_relation_name = user_relation_name;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDeptName() {
        return this.deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getRoleName() {
        return this.roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getDepartment() {
        return this.department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDepartment_relation() {
        return this.department_relation;
    }

    public void setDepartment_relation(String department_relation) {
        this.department_relation = department_relation;
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

    public String getUser_relation() {
        return this.user_relation;
    }

    public void setUser_relation(String user_relation) {
        this.user_relation = user_relation;
    }

    public String getOperate() {
        return this.operate;
    }

    public void setOperate(String operate) {
        this.operate = operate;
    }

    public String getRead() {
        return this.read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getCreate() {
        return this.create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getUpdate() {
        return this.update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getDelete() {
        return this.delete;
    }

    public void setDelete(String delete) {
        this.delete = delete;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTable() {
        return this.table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getFields() {
        return this.fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getMenu() {
        return this.menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }

    public String getTemplate() {
        return this.template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getImport_auth() {
        return this.import_auth;
    }

    public void setImport_auth(String import_auth) {
        this.import_auth = import_auth;
    }

    public String getExport_auth() {
        return this.export_auth;
    }

    public void setExport_auth(String export_auth) {
        this.export_auth = export_auth;
    }

    public String getData_group_id() {
        return this.data_group_id;
    }

    public void setData_group_id(String data_group_id) {
        this.data_group_id = data_group_id;
    }
}
