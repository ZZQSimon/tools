package cn.com.easyerp.core.widget.inputSelect;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.ReferenceRequestModel;
import cn.com.easyerp.core.filter.FilterService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/widget/inputSelect" })
public class InputSelectController {
    @Autowired
    private DataService dataService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private FilterService filterService;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @RequestMapping({ "filter.do" })
    public ActionResult filterList(@RequestBody ReferenceRequestModel request) {
        FieldModelBase field = ViewService.fetchFieldModel(request.getId());
        ColumnDescribe column = this.dataService.getColumnDesc(field);
        TableDescribe table = this.dataService.getTableDesc(column.getTable_id());
        TableDescribe refTable = this.dataService.getTableDesc(column.getRef_table_name());
        if (column.getRefFilter() != null || column.getRef_table_sql() == null) {
            String where = null;
            if (column.getRefFilter() != null)
                where = this.filterService.toWhere(column.getRef_table_name(), request.getFilters());
            List<Map<String, Object>> records = this.dataService.filterDataRecords(column, request.getText(), where);
            if (records != null && records.size() != 0) {
                for (Map<String, Object> record : records) {
                    record.put("ref____name_Expression", this.dataService.buildNameExpression(refTable, record));
                }
            }
            return new ActionResult(true, records);
        }
        List<Map<String, Object>> records = this.dataService.filterCustomDataRecords(column,
                this.viewService.convertToDBValues(table, request.getParam()), request.getText());
        if (records != null && records.size() != 0) {
            for (Map<String, Object> record : records) {
                record.put("ref____name_Expression", this.dataService.buildNameExpression(refTable, record));
            }
        }
        return new ActionResult(true, records);
    }

    @SuppressWarnings({ "rawtypes" })
    @RequestMapping({ "/custom_reference.do" })
    @ResponseBody
    public ActionResult customReference(@RequestBody ReferenceRequestModel rrm) {
        ColumnDescribe column = this.dataService.getColumnDesc(ViewService.fetchFieldModel(rrm.getId()));
        TableDescribe table = this.dataService.getTableDesc(column.getTable_id());
        Map<String, Object> record = this.dataService.getSqlRef(column, rrm.getText(),
                this.viewService.convertToDBValues(table, rrm.getParam()));
        if (record != null) {
            record.put("ref____name_Expression", this.dataService
                    .buildNameExpression(this.cacheService.getTableDesc(column.getRef_table_name()), record));
        }
        return new ActionResult(true, record);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @RequestMapping({ "/reference.do" })
    @ResponseBody
    public ActionResult reference(@RequestBody ReferenceRequestModel rrm) {
        ColumnDescribe column = this.dataService.getColumnDesc(ViewService.fetchFieldModel(rrm.getId()));
        String where = null;
        if (column.getRefFilter() != null)
            where = this.filterService.toWhere(column.getRef_table_name(), rrm.getFilters());
        Map<String, Object> record = this.dataService.getRef(column, rrm.getText(), where);
        if (record != null) {
            record.put("ref____name_Expression", this.dataService
                    .buildNameExpression(this.cacheService.getTableDesc(column.getRef_table_name()), record));
        }
        return new ActionResult(true, record);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @RequestMapping({ "/widgetFilter.do" })
    public ActionResult widgetFilter(@RequestBody ReferenceRequestModel request) {
        ColumnDescribe column = this.dataService.getColumnDesc(request.getTableName(), request.getColumnName());

        if (column.getRefFilter() != null || column.getRef_table_sql() == null) {
            String where = null;
            if (column.getRefFilter() != null)
                where = this.filterService.toWhere(column.getRef_table_name(), request.getFilters());
            return new ActionResult(true, this.dataService.filterDataRecords(column,
                    (request.getText() == null) ? "" : request.getText(), where));
        }
        TableDescribe table = this.dataService.getTableDesc(column.getTable_id());
        return new ActionResult(true, this.dataService.filterCustomDataRecords(column,
                this.viewService.convertToDBValues(table, request.getParam()), request.getText()));
    }
}
