package cn.com.easyerp.auth;

import java.util.List;

public class AuthEntryModel {
    public class OperatorModel {
        public String dept;
        public String role;
        public String user;
    }

    private List<OperatorModel> operators;
    private List<OperatorModel> owners;

    public List<OperatorModel> getOperators() {
        return this.operators;
    }

    public void setOperators(List<OperatorModel> operators) {
        this.operators = operators;
    }

    public List<OperatorModel> getOwners() {
        return this.owners;
    }

    public void setOwners(List<OperatorModel> owners) {
        this.owners = owners;
    }
}