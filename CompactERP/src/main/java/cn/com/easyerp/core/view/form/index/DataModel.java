package cn.com.easyerp.core.view.form.index;

import java.util.List;
import java.util.Map;

public class DataModel {
    private List<Map<String, Object>> desktopDate;
    private int heightY;
    private Map<String, DesktopListModel> listMap;

    public List<Map<String, Object>> getDesktopDate() {
        return this.desktopDate;
    }

    public void setDesktopDate(List<Map<String, Object>> desktopDate) {
        this.desktopDate = desktopDate;
    }

    public int getHeightY() {
        return this.heightY;
    }

    public void setHeightY(int heightY) {
        this.heightY = heightY;
    }

    public Map<String, DesktopListModel> getListMap() {
        return this.listMap;
    }

    public void setListMap(Map<String, DesktopListModel> listMap) {
        this.listMap = listMap;
    }
}
