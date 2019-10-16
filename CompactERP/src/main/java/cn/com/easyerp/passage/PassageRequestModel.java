package cn.com.easyerp.passage;

import java.util.Date;
import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.TableFormRequestModel;

public class PassageRequestModel<T extends FormModelBase> extends TableFormRequestModel<T> {
    private Map<String, Object> param;
    private List<List<Map<String, Object>>> exportData;
    private String passage_id;
    private Date start_date;
    private Date end_date;
    private String where;
    private String mode;
    private String searchType;
    private List<Map<String, Object>> pkeys;

    public Map<String, Object> getParam() {
        return this.param;
    }

    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    public List<List<Map<String, Object>>> getExportData() {
        return this.exportData;
    }

    public void setExportData(List<List<Map<String, Object>>> exportData) {
        this.exportData = exportData;
    }

    public String getPassage_id() {
        return this.passage_id;
    }

    public void setPassage_id(String passage_id) {
        this.passage_id = passage_id;
    }

    public Date getStart_date() {
        return this.start_date;
    }

    public void setStart_date(Date start_date) {
        this.start_date = start_date;
    }

    public Date getEnd_date() {
        return this.end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public String getWhere() {
        return this.where;
    }

    public void setWhere(String where) {
        this.where = where;
    }

    public String getMode() {
        return this.mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSearchType() {
        return this.searchType;
    }

    public void setSearchType(String searchType) {
        this.searchType = searchType;
    }

    public List<Map<String, Object>> getPkeys() {
        return this.pkeys;
    }

    public void setPkeys(List<Map<String, Object>> pkeys) {
        this.pkeys = pkeys;
    }
}
