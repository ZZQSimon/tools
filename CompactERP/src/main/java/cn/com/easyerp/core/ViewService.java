package cn.com.easyerp.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Clob;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.auth.EncrypDES;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.companyCalendar.PCCalendarService;
import cn.com.easyerp.core.dao.CalendarDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.serializer.ClobSerializer;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.TagIdGenerator;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;

@Service
public class ViewService {
    private static final String SESSION_WIDGET_CACHE_KEY = "SESSION_WIDGET_CACHE_KEY";
    private static final String REQUEST_WIDGET_CACHE_KEY = "REQUEST_WIDGET_CACHE_KEY";
    private static final String COLUMN_PARENT_PREFIX = "_parent.";
    private static TagIdGenerator tig = new TagIdGenerator();
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatterDay = new SimpleDateFormat("yyyy-MM-dd");
    @Autowired
    private DataService dataService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private CalendarDao calendarDao;
    @Autowired
    private PCCalendarService pcCalendarService;
    private static EncrypDES des;

    public ViewService() {
        try {
            des = new EncrypDES("G1");
        } catch (Exception e) {
            throw new ApplicationException("EncrypDES false");
        }
    }

    public static void cacheWidgetToRequest(WidgetModelBase widget) {
        List<WidgetModelBase> list = (List) Common.getRequestObject("REQUEST_WIDGET_CACHE_KEY");
        if (list == null) {
            list = new ArrayList<WidgetModelBase>();
            Common.setRequestObject("REQUEST_WIDGET_CACHE_KEY", list);
        }
        list.add(widget);
    }

    public static WidgetModelBase fetchWidgetModel(String id) {
        return fetchCachedModel("SESSION_WIDGET_CACHE_KEY", id);
    }

    public static WidgetModelBase fetchCachedModel(String key, String id) {
        return (WidgetModelBase) ((Map) Common.getSessionObject(key)).get(id);
    }

    public static FieldModelBase fetchFieldModel(String id) {
        return (FieldModelBase) fetchCachedModel("SESSION_WIDGET_CACHE_KEY", id);
    }

    public static FormModelBase fetchFormModel(String id) {
        WidgetModelBase widget = fetchCachedModel("SESSION_WIDGET_CACHE_KEY", id);
        if (FormModelBase.class.isInstance(widget))
            return (FormModelBase) widget;
        throw new ApplicationException("no form found with id:" + id);
    }

    public static void cacheForm(FormModelBase form, HttpServletRequest request) {
        cacheRequestWidgets(form, (List) request.getAttribute("REQUEST_WIDGET_CACHE_KEY"), true);
    }

    public static void cacheRequestWidgets(FormModelBase form) {
        cacheRequestWidgets(form, (List) Common.getRequestObject("REQUEST_WIDGET_CACHE_KEY"), false);
    }

    private static void cacheRequestWidgets(FormModelBase form, List<WidgetModelBase> widgets, boolean cacheForm) {
        Map<String, WidgetModelBase> widgetCache = (Map) Common.getSessionObject("SESSION_WIDGET_CACHE_KEY");
        if (widgetCache == null) {
            widgetCache = new ConcurrentHashMap<String, WidgetModelBase>();
            Common.putSessionObject("SESSION_WIDGET_CACHE_KEY", widgetCache);
        }
        if (cacheForm) {
            widgetCache.put(form.getId(), form);
        }
        if (widgets == null)
            return;
        List<WidgetModelBase> list = form.getWidgets();
        if (list == null) {
            form.setWidgets(widgets);
        } else {
            list.addAll(widgets);
        }
        for (WidgetModelBase widget : widgets) {
            widgetCache.put(widget.getId(), widget);
            widget.setForm(form);
        }
    }

    public static void removeWidgetsFromCache(List<WidgetModelBase> widgets) {
        Map<String, WidgetModelBase> widgetCache = (Map) Common.getSessionObject("SESSION_WIDGET_CACHE_KEY");
        if (widgetCache == null)
            return;
        for (WidgetModelBase widget : widgets)
            widgetCache.remove(widget.getId());
    }

    public String name(FieldWithRefModel field) {
        String ret;
        if (field.getRef() == null)
            return text(this.dataService.getColumnDesc(field), field.getValue());
        ColumnDescribe column = this.dataService.getColumnDesc(field);
        TableDescribe table = this.dataService.getTableDesc(column.getRef_table_name());

        if (Common.isBlank(table.getName_column())) {
            String[] idColumns = table.getIdColumns();
            ret = (String) field.getRef().get(idColumns[idColumns.length - 1]);
        } else {
            ret = (String) field.getRef().get(table.getName_column());
        }
        return (ret == null) ? "" : ret;
    }

    public static String text(ColumnDescribe desc, Object value) {
        String ret;
        if (value == null)
            return "";
        value = value.toString().replace("\"", "&quot;");
        value = value.toString().replace("<", "&lt;");
        value = value.toString().replace(">", "&gt;");
        value = value.toString().replace("'", "&#39;");

        switch (desc.getData_type()) {
        case 1:
            if (Clob.class.isInstance(value))
                value = ClobSerializer.convert((Clob) value);
        case 6:
        case 9:
        case 10:
        case 13:
        case 14:
        case 15:
            return String.valueOf(value);
        case 5:
            return (((Integer) value).intValue() == 1) ? "true" : "false";
        case 2:
            return String.valueOf(value);
        case 3:
            if (Double.class.isInstance(value)) {
                ret = String.format("%f", new Object[] { (Double) value });
            } else if (BigDecimal.class.isInstance(value)) {
                ret = String.format("%f", new Object[] { (BigDecimal) value });
            } else {
                ret = value.toString();
            }
            return ret;
        case 4:
            return value.toString();
        case 11:
            return value.toString();
        case 12:
            if (value instanceof String) {
                ret = value.toString();
            } else {
                ret = formatter.format(value);
            }
            return ret;
        case 7:
        case 8:
            return StringUtils.substringAfterLast((String) value, "/");
        }
        throw new ApplicationException(
                String.format("not defined data type: '%d'", new Object[] { Integer.valueOf(desc.getData_type()) }));
    }

    public static String getNextTagId(String prefix) {
        return tig.get(prefix + "_");
    }

    public static Object convertToDBValue(ColumnDescribe desc, Object value) {
        if (value == null)
            return null;
        try {
            Object ret;
            switch (desc.getData_type()) {
            case 1:
            case 2:
            case 3:
            case 7:
            case 8:
            case 9:
            case 10:
            case 13:
            case 14:
            case 15:
                return value;
            case 5:
                return Integer.valueOf(((Boolean) value).booleanValue() ? 1 : 0);
            case 4:
            case 12:
                try {
                    ret = Common.convertTimestamp(((Long) value).longValue());
                } catch (Exception e) {
                    if (value instanceof Date) {
                        ret = Long.valueOf(((Date) value).getTime());
                    } else {
                        ret = value;
                    }
                }
                return ret;
            case 6:
                return value;
            case 11:
                throw new ApplicationException(String.format("not defined data type: '%d'",
                        new Object[] { Integer.valueOf(desc.getData_type()) }));
            }
            throw new ApplicationException(String.format("not defined data type: '%d'",
                    new Object[] { Integer.valueOf(desc.getData_type()) }));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApplicationException(String.format("failed convert column '%s.%s' with value '%s'",
                    new Object[] { desc.getTable_id(), desc.getColumn_name(), value.toString() }));
        }
    }

    public DatabaseDataMap convertToDBValues(TableDescribe table, ViewDataMap values) {
        DatabaseDataMap ret = new DatabaseDataMap(values.size());
        for (Map.Entry<String, Object> entry : values.entrySet()) {
            ColumnDescribe column;
            String key = (String) entry.getKey();
            if (key.startsWith("__")) {
                ret.put(key, entry.getValue());
                continue;
            }
            if (key.startsWith("_parent.")) {
                TableDescribe parentTable = this.dataService.getTableDesc(table.getParent_id());
                column = parentTable.getColumn(key.substring("_parent.".length()));
            } else if (key.contains(".")) {
                String[] names = Common.split(key, ".");
                column = table.getColumn(names[0]);
                column = this.dataService.getTableDesc(column.getRef_table_name()).getColumn(names[1]);
            } else {
                column = table.getColumn(key);
            }
            ret.put(key, convertToDBValue(column, entry.getValue()));
        }
        return ret;
    }

    public void destroyFormCache(String id) {
        Map<String, WidgetModelBase> widgetCache = (Map) Common.getSessionObject("SESSION_WIDGET_CACHE_KEY");
        FormModelBase form = (FormModelBase) widgetCache.get(id);
        if (form.getWidgets() != null)
            for (WidgetModelBase widget : form.getWidgets())
                widgetCache.remove(widget.getId());
        widgetCache.remove(form.getId());
    }

    public DatabaseDataMap mapDataToDB(Map<String, Object> fieldMap) {
        return mapDataToDB(fieldMap, null);
    }

    public DatabaseDataMap mapDataToDB(Map<String, Object> fieldMap, Collection<String> needed) {
        return mapDataToDB(fieldMap, needed, null);
    }

    public DatabaseDataMap mapDataToDB(Map<String, Object> fieldMap, Collection<String> needed,
            ViewMapIteratorProcessor processor) {
        DatabaseDataMap data = new DatabaseDataMap();
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            Object value;
            String column;
            FieldModelBase field = fetchFieldModel((String) entry.getKey());
            ColumnDescribe desc = this.dataService.getColumnDesc(field);
            if (needed != null && !needed.contains(desc.getColumn_name()))
                continue;
            if ("cre_date".equals(desc.getColumn_name())) {
                continue;
            }
            if (desc.getData_type() == 7 || desc.getData_type() == 8) {
                FileFieldModel file = (FileFieldModel) field;
                String uuid = file.getUuid();
                if (uuid != null)
                    try {
                        file.setValue(this.storageService.saveUploadField(desc, uuid));
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                column = desc.getColumn_name();
                value = file.getValue();
            } else {
                column = desc.getColumn_name();
                value = convertToDBValue(desc, entry.getValue());
            }
            if (processor == null || processor.process(column, value, field))
                data.put(column, value);
        }
        return data;
    }

    public List<CalendarEvent> calendarColorData(Map<String, Object> tableData, TableDescribe table) {
        if (table.getIdColumns() == null || table.getIdColumns().length == 0)
            return null;
        List<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setRef_table(table.getId());
        calendarEvent.setRef_table_data(buildRefTableData(table, tableData));
        calendarEvents.add(calendarEvent);
        return calendarEvents;
    }

    public List<CalendarEvent> calendarColorData(Map<String, Object> tableData, TableDescribe table,
            Map<String, Object> fieldMap, Map<String, Object> calendarColorStatus, boolean isChild) {
        if (table.getIdColumns() == null || table.getIdColumns().length == 0 || fieldMap == null)
            return null;
        List<CalendarEvent> calendarEvents = new ArrayList<CalendarEvent>();
        String maxId = "0000000000";
        if (!isChild)
            maxId = this.calendarDao.getCalendarEventMaxId();
        for (Map.Entry<String, Object> entry : fieldMap.entrySet()) {
            CalendarEvent calendarEvent = new CalendarEvent();
            if (calendarColorStatus != null && "add".equals(calendarColorStatus.get(entry.getKey()))) {
                calendarEvent.setStatus("add");
            }
            calendarEvent.setCalendar_id("0000000001");
            if (!isChild) {
                String nextId = "0000000000";
                if (maxId == null) {
                    nextId = this.pcCalendarService.getNextId(nextId);
                    maxId = nextId;
                } else {
                    nextId = this.pcCalendarService.getNextId(maxId);
                    maxId = nextId;
                }
                calendarEvent.setP_calendar_event_id(nextId);
            }
            calendarEvent.setOwner(AuthService.getCurrentUserId());
            calendarEvent.setRef_table(table.getId());
            FieldModelBase field = fetchFieldModel((String) entry.getKey());
            ColumnDescribe desc = this.dataService.getColumnDesc(field);
            calendarEvent.setRef_table_column(desc.getColumn_name());
            if (desc.getData_type() == 4) {
                try {
                    calendarEvent.setBegin_date(formatterDay.parse(tableData.get(desc.getColumn_name()).toString()));
                    calendarEvent.setEnd_date(formatterDay.parse(tableData.get(desc.getColumn_name()).toString()));
                } catch (Exception e) {
                }

            } else if (desc.getData_type() == 12) {
                calendarEvent.setBegin_date((Date) tableData.get(desc.getColumn_name()));
                calendarEvent.setEnd_date((Date) tableData.get(desc.getColumn_name()));
            } else {
                calendarEvent.setBegin_date(new Date());
                calendarEvent.setEnd_date(new Date());
            }
            calendarEvent.setColor(entry.getValue().toString());
            Object eventName = this.dataService.buildNameExpression(table, tableData);
            calendarEvent.setEvent_name((eventName == null) ? "new event" : eventName.toString());
            calendarEvent.setRef_table_data(buildRefTableData(table, tableData));
            calendarEvents.add(calendarEvent);
        }
        return calendarEvents;
    }

    public List<CalendarEvent> calendarColorData(Map<String, Object> tableData, TableDescribe table,
            Map<String, Object> fieldMap, boolean isChild) {
        return calendarColorData(tableData, table, fieldMap, null, isChild);
    }

    public String buildRefTableData(TableDescribe table, Map<String, Object> tableData) {
        try {
            String[] idColumns = table.getIdColumns();
            Map<String, Object> dataMap = new HashMap<String, Object>();
            for (int i = 0; i < idColumns.length; i++) {
                Object value = convertToDBValue(table.getColumn(idColumns[i]), tableData.get(idColumns[i]));
                dataMap.put(idColumns[i], value);
            }
            return Common.toJson(dataMap);
        } catch (Exception e) {
            return null;
        }
    }

    public WidgetModelBase parents(String id, int level) {
        return parents(fetchWidgetModel(id), level);
    }

    public WidgetModelBase parents(WidgetModelBase widget, int level) {
        WidgetModelBase w = widget;
        for (int i = 0; i < level; i++) {
            w = fetchWidgetModel(w.getParent());
            if (w == null)
                throw new ApplicationException("failed fetch widget(" + widget.getId() + ") parent at level " + level);
        }
        return w;
    }
}