package cn.com.easyerp.module.customReport;

import java.util.Map;

public class CustomData {
    private String Region;
    private String store_Type;
    private String EmployeeType;
    private String Grade;
    private String LeavingType;
    private String Store;
    private String sum_grade;
    private Map<String, CustomDataDescribe> customDataDescribes;
    private String month;
    private int BeginningHC;
    private int Exit;
    private int MonthEndHC;
    private String AttritionRate;

    public String getRegion() {
        return this.Region;
    }

    public void setRegion(String region) {
        this.Region = region;
    }

    public String getStore_Type() {
        return this.store_Type;
    }

    public void setStore_Type(String store_Type) {
        this.store_Type = store_Type;
    }

    public String getEmployeeType() {
        return this.EmployeeType;
    }

    public void setEmployeeType(String employeeType) {
        this.EmployeeType = employeeType;
    }

    public String getGrade() {
        return this.Grade;
    }

    public void setGrade(String grade) {
        this.Grade = grade;
    }

    public String getLeavingType() {
        return this.LeavingType;
    }

    public void setLeavingType(String leavingType) {
        this.LeavingType = leavingType;
    }

    public String getStore() {
        return this.Store;
    }

    public void setStore(String store) {
        this.Store = store;
    }

    public Map<String, CustomDataDescribe> getCustomDataDescribes() {
        return this.customDataDescribes;
    }

    public void setCustomDataDescribes(Map<String, CustomDataDescribe> customDataDescribes) {
        this.customDataDescribes = customDataDescribes;
    }

    public String getMonth() {
        return this.month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getBeginningHC() {
        return this.BeginningHC;
    }

    public void setBeginningHC(int beginningHC) {
        this.BeginningHC = beginningHC;
    }

    public int getExit() {
        return this.Exit;
    }

    public void setExit(int exit) {
        this.Exit = exit;
    }

    public int getMonthEndHC() {
        return this.MonthEndHC;
    }

    public void setMonthEndHC(int monthEndHC) {
        this.MonthEndHC = monthEndHC;
    }

    public String getSum_grade() {
        return this.sum_grade;
    }

    public void setSum_grade(String sum_grade) {
        this.sum_grade = sum_grade;
    }

    public String getAttritionRate() {
        return this.AttritionRate;
    }

    public void setAttritionRate(String attritionRate) {
        this.AttritionRate = attritionRate;
    }
}
