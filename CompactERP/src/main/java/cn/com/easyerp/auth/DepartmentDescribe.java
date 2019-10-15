package cn.com.easyerp.auth;

public class DepartmentDescribe {
    private String id;
    private String parent_id;
    private String name;
    private String name_abbr;
    private String address;
    private String leader;
    private String telephone;
    private String fax;
    private String participate_in_sale;
    private String cre_user;
    private String cre_date;
    private String upd_user;
    private String upd_date;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent_id() {
        return this.parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName_abbr() {
        return this.name_abbr;
    }

    public void setName_abbr(String name_abbr) {
        this.name_abbr = name_abbr;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLeader() {
        return this.leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getFax() {
        return this.fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getParticipate_in_sale() {
        return this.participate_in_sale;
    }

    public void setParticipate_in_sale(String participate_in_sale) {
        this.participate_in_sale = participate_in_sale;
    }

    public String getCre_user() {
        return this.cre_user;
    }

    public void setCre_user(String cre_user) {
        this.cre_user = cre_user;
    }

    public String getCre_date() {
        return this.cre_date;
    }

    public void setCre_date(String cre_date) {
        this.cre_date = cre_date;
    }

    public String getUpd_user() {
        return this.upd_user;
    }

    public void setUpd_user(String upd_user) {
        this.upd_user = upd_user;
    }

    public String getUpd_date() {
        return this.upd_date;
    }

    public void setUpd_date(String upd_date) {
        this.upd_date = upd_date;
    }
}
