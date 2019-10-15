package cn.com.easyerp.report;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class PagePrintingInfo {
    int index;
    int pageStart = 0;
    int pageRows = 0;
    int detailIndex = -1;
    Map<String, Object> data;
    List<Map<String, Object>> details = null;
    Map<String, DetailGroup> groups = new HashMap<>();
    List<Integer> breaks = new ArrayList<>();

    PagePrintingInfo(int index, Map<String, Object> data, int pageStart, int pageRows) {
        this.index = index;
        this.data = data;
        this.pageStart = pageStart;
        this.pageRows = pageRows;
    }
}
