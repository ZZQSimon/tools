package cn.com.easyerp.core.cache;

import java.util.HashMap;
import java.util.Map;

import cn.com.easyerp.framework.common.Common;

public class ComplexColumnDescribe {
    public boolean extended = false;
    private String id;
    private String base_tbl;
    private String detail_tbl;
    private String ins_disp_sql;
    private String upd_disp_sql;
    private String detail_tbl_pid_col;
    private String detail_tbl_pnm_col;
    private String detail_tbl_number_col;
    private String base_tbl_number_col;
    private String memo;
    private Map<String, String> map = null;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBase_tbl() {
        return this.base_tbl;
    }

    public void setBase_tbl(String base_tbl) {
        this.base_tbl = base_tbl;
        this.extended = Common.isBlank(base_tbl);
    }

    public String getDetail_tbl() {
        return this.detail_tbl;
    }

    public void setDetail_tbl(String detail_tbl) {
        this.detail_tbl = detail_tbl;
    }

    public String getIns_disp_sql() {
        return this.ins_disp_sql;
    }

    public void setIns_disp_sql(String ins_disp_sql) {
        this.ins_disp_sql = ins_disp_sql;
    }

    public String getUpd_disp_sql() {
        return this.upd_disp_sql;
    }

    public void setUpd_disp_sql(String upd_disp_sql) {
        this.upd_disp_sql = upd_disp_sql;
    }

    public String getDetail_tbl_pid_col() {
        return this.detail_tbl_pid_col;
    }

    public void setDetail_tbl_pid_col(String detail_tbl_pid_col) {
        this.detail_tbl_pid_col = detail_tbl_pid_col;
    }

    public String getDetail_tbl_pnm_col() {
        return this.detail_tbl_pnm_col;
    }

    public void setDetail_tbl_pnm_col(String detail_tbl_pnm_col) {
        this.detail_tbl_pnm_col = detail_tbl_pnm_col;
    }

    public String getDetail_tbl_number_col() {
        return this.detail_tbl_number_col;
    }

    public void setDetail_tbl_number_col(String detail_tbl_number_col) {
        this.detail_tbl_number_col = detail_tbl_number_col;
    }

    public String getBase_tbl_number_col() {
        return this.base_tbl_number_col;
    }

    public void setBase_tbl_number_col(String base_tbl_number_col) {
        this.base_tbl_number_col = base_tbl_number_col;
    }

    public String getMemo() {
        return this.memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public boolean isExtended() {
        return this.extended;
    }

    public void addMap(String detail_col, String base_col) {
        if (this.map == null)
            this.map = new HashMap<>();
        this.map.put(base_col, detail_col);
    }

    public Map<String, String> getMap() {
        return this.map;
    }
}
