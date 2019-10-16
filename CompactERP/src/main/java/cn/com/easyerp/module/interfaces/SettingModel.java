package cn.com.easyerp.module.interfaces;

import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("setting")
public class SettingModel extends FormModelBase {
    private GridModel grid;
    private List<Map<String, Object>> typeList;
    private List<Map<String, Object>> settingList;
    private List<Map<String, Object>> UcodeList;
    private List<Map<String, Object>> SubjectList;

    public SettingModel(ActionType action, String parent) {
        super(action, parent);
    }

    public GridModel getGrid() {
        return this.grid;
    }

    public void setTypeList(List<Map<String, Object>> typeList) {
        this.typeList = typeList;
    }

    public void setSettingList(List<Map<String, Object>> settingList) {
        this.settingList = settingList;
    }

    public void setUcodeList(List<Map<String, Object>> UcodeList) {
        this.UcodeList = UcodeList;
    }

    public void setSubjectList(List<Map<String, Object>> SubjectList) {
        this.SubjectList = SubjectList;
    }

    public List<Map<String, Object>> getTypeList() {
        return this.typeList;
    }

    public List<Map<String, Object>> getSettingList() {
        return this.settingList;
    }

    public List<Map<String, Object>> getUcodeList() {
        return this.UcodeList;
    }

    public List<Map<String, Object>> getSubjectList() {
        return this.SubjectList;
    }

    public String getTitle() {
        return "对照页面";
    }
}
