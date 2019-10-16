package cn.com.easyerp.module.interfaces;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("exports")
public class ExportsModel extends FormModelBase {
    private GridModel grid;
    private Map<String, List<Map<String, Object>>> typeList;
    private List<String> typeName;
    private List<Map<String, Object>> headList;
    private List<Map<String, Object>> bodyList;
    private List<Map<String, Object>> titleName;

    public ExportsModel(ActionType action, String parent) {
        super(action, parent);
    }

    public GridModel getGrid() {
        return this.grid;
    }

    public List<Map<String, Object>> getHeadList() {
        return this.headList;
    }

    public void setHeadList(List<Map<String, Object>> headList) {
        this.headList = headList;
    }

    public List<Map<String, Object>> getBodyList() {
        return this.bodyList;
    }

    public void setBodyList(List<Map<String, Object>> bodyList) {
        this.bodyList = bodyList;
    }

    public Map<String, List<Map<String, Object>>> getTypeList() {
        return this.typeList;
    }

    public List<Map<String, Object>> getTitleName() {
        return this.titleName;
    }

    public void settitleName(List<Map<String, Object>> titleName) {
        this.titleName = titleName;
    }

    public void setTypeList(Map<String, List<Map<String, Object>>> typeList) {
        this.typeList = typeList;
        this.typeName = new ArrayList<>();
        Set<Map.Entry<String, List<Map<String, Object>>>> entries = typeList.entrySet();
        Iterator<Map.Entry<String, List<Map<String, Object>>>> it = entries.iterator();
        while (it.hasNext()) {
            this.typeName.add((it.next()).getKey());
        }
    }

    public List<String> getTypeName() {
        return this.typeName;
    }

    public String getTitle() {
        return "导出页面";
    }
}