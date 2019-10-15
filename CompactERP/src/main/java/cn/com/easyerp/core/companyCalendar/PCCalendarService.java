package cn.com.easyerp.core.companyCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.ReferenceModel;
import cn.com.easyerp.core.exception.ApplicationException;

@Service
public class PCCalendarService {
    @Autowired
    private CalendarDao calendarDao;
    @Autowired
    private SystemDao systemDao;

    public List<PCCalendarModel> buildCalendarEvents(List<CalendarEvent> calendarEvents) {
        if (calendarEvents == null || calendarEvents.size() == 0)
            return null;
        List<PCCalendarModel> pcCalendarModels = new ArrayList<PCCalendarModel>();
        for (CalendarEvent calendarEvent : calendarEvents) {
            PCCalendarModel pcCalendarModel = new PCCalendarModel(calendarEvent);
            pcCalendarModels.add(pcCalendarModel);
        }
        return pcCalendarModels;
    }

    @Autowired
    private CacheService cacheService;
    @Autowired
    private DataService dataService;

    private List<Date> getDates(Date start, Date end) {
        ArrayList<Date> dates = new ArrayList<Date>();
        Calendar dd = Calendar.getInstance();
        dd.setTime(start);
        while (dd.getTime().getTime() <= end.getTime()) {
            dates.add(dd.getTime());
            dd.add(5, 1);
        }
        return dates;
    }

    public void updateCalendarEvents(CalendarEvent calendarEvent) {
    }

    public List<TreeNode> buildSourceGroupTree(Map<String, Object> param) {
        if (null == param.get("sourceTable") || "".equals(param.get("sourceTable").toString()))
            throw new ApplicationException("no source table");
        List<Map<String, Object>> sourceData = getSourceData(param);
        TableDescribe table = this.cacheService.getTableDesc(param.get("sourceTable").toString());
        return buildTreeNode(sourceData, table, (param.get("group") == null) ? null : param.get("group").toString());
    }

    private List<Map<String, Object>> getSourceData(Map<String, Object> param) {
        if (null == param.get("filter")) {
            return this.systemDao.execSql("select * from " + param.get("sourceTable"));
        }
        return this.systemDao.execSql(param.get("filter").toString());
    }

    private List<TreeNode> buildTreeNode(List<Map<String, Object>> sourceData, TableDescribe table, String column) {
        if (null == sourceData)
            return null;
        List<TreeNode> treeNodes = new ArrayList<TreeNode>();
        Object text = "";
        String parent = "";
        if (column == null) {
            for (Map<String, Object> data : sourceData) {
                text = this.dataService.buildNameExpression(table, data);
                TreeNode node = new TreeNode(ViewService.getNextTagId("t"), "#", (text == null) ? "" : text.toString(),
                        data, null);
                treeNodes.add(node);
            }
        } else {
            ColumnDescribe refColumn = table.getColumn(column);
            Map<Object, String> key = new HashMap<Object, String>();
            if (null != refColumn.getDic_id() && !"".equals(refColumn.getDic_id())) {
                Map<String, I18nDescribe> dict = this.cacheService.getDict(refColumn.getDic_id());
                if (null == dict)
                    throw new ApplicationException("dict: " + refColumn.getDic_id() + " is mot exists");
                for (Map.Entry<String, I18nDescribe> entry : dict.entrySet()) {
                    String treeNodeKey = ViewService.getNextTagId("t");
                    key.put(entry.getKey(), treeNodeKey);
                    TreeNode node = new TreeNode(treeNodeKey, "#",
                            this.dataService.i18nString((I18nDescribe) entry.getValue()), null, null);
                    treeNodes.add(node);
                }
            } else if (null != refColumn.getRef_table_name() && !"".equals(refColumn.getRef_table_name())) {
                Map<Object, Map<String, Object>> refDatas = buildRefTableData(table, column);
                TableDescribe refTable = this.cacheService.getTableDesc(refColumn.getRef_table_name());
                if (null != refDatas)
                    for (Map.Entry<Object, Map<String, Object>> entry : refDatas.entrySet()) {
                        String treeNodeKey = ViewService.getNextTagId("t");
                        key.put(entry.getKey(), treeNodeKey);
                        text = this.dataService.buildNameExpression(refTable, (Map) entry.getValue());
                        TreeNode node = new TreeNode(treeNodeKey, "#", (text == null) ? "" : text.toString(), null,
                                null);
                        treeNodes.add(node);
                    }
            } else {
                for (Map<String, Object> data : sourceData) {
                    if (!key.containsKey(data.get(column))) {
                        text = data.get(column).toString();
                        String treeNodeKey = ViewService.getNextTagId("t");
                        key.put(data.get(column), treeNodeKey);
                        TreeNode node = new TreeNode(treeNodeKey, "#", (text == null) ? "" : text.toString(), null,
                                null);
                        treeNodes.add(node);
                    }
                }
            }
            for (Map<String, Object> data : sourceData) {
                if (null != data.get(column)) {
                    parent = (String) key.get(data.get(column));
                    text = this.dataService.buildNameExpression(table, data);
                    TreeNode node = new TreeNode(ViewService.getNextTagId("t"), (parent == null) ? "#" : parent,
                            (text == null) ? "" : text.toString(), data, null);
                    treeNodes.add(node);
                }
            }
        }
        return treeNodes;
    }

    private Map<Object, Map<String, Object>> buildRefTableData(TableDescribe table, String column) {
        ColumnDescribe refColumn = table.getColumn(column);
        TableDescribe refTable = this.cacheService.getTableDesc(refColumn.getRef_table_name());
        if (null == refTable)
            throw new ApplicationException("table: " + refColumn.getRef_table_name() + " is not exists");
        List<ReferenceModel> refs = this.dataService.makeRefModels(refTable);
        List<ColumnDescribe> columns = refTable.getColumns();
        Set<ColumnDescribe> columnsNeeded = new HashSet<ColumnDescribe>(columns.size());
        for (ColumnDescribe columnToDb : columns)
            columnsNeeded.add(columnToDb);
        String where = null;
        if (null == refTable.getIdColumns()) {
            throw new ApplicationException("table: " + refTable.getId() + " has no id column");
        }
        where = refTable.getId() + ".`" + refTable.getIdColumns()[0] + "` in (select distinct `" + column + "` from "
                + table.getId() + ")";
        String encrypt_str = this.cacheService.getSystemParam().getEncrypt_str();
        List<Map<String, Object>> datas = this.dataService.makeRefDataForJava(this.systemDao.selectDataWithCondition(
                refTable.getId(), columnsNeeded, null, where, null, refs, null, true, null, encrypt_str, false));
        if (null == datas)
            return null;
        Map<Object, Map<String, Object>> refDatas = new HashMap<Object, Map<String, Object>>();
        for (Map<String, Object> ref : datas) {
            refDatas.put(ref.get(refTable.getIdColumns()[0]), ref);
        }
        return refDatas;
    }

    public String getNextId(String id) {
        return String.format("%0" + id.length() + "d", new Object[] { Integer.valueOf(Integer.parseInt(id) + 1) });
    }

    public List<Map<String, Object>> getEventTag(MobileCalendarRequestModel request, AuthDetails user) {
        return this.calendarDao.getEventTag(request, user);
    }

    public Map<String, Object> getCommonRule(MobileCalendarRequestModel request) {
        return this.calendarDao.getCommonRule(request);
    }

    public List<Map<String, Object>> getSpecialRule(MobileCalendarRequestModel request) {
        return this.calendarDao.getSpecialRule(request);
    }
}
