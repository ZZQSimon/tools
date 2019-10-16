package cn.com.easyerp.core.data;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Clob;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthControlDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthDetailsAdditional;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.auth.DepartmentDescribe;
import cn.com.easyerp.auth.EncrypDES;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.authGroup.AuthDataGroup;
import cn.com.easyerp.core.authGroup.AuthDataGroupDetail;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OperationDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableShortcutDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.cache.style.ColumnMapViewStyle;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.dao.UserColumnDao;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.logger.Loggable;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.serializer.ClobSerializer;
import cn.com.easyerp.core.widget.CheckBoxField;
import cn.com.easyerp.core.widget.ComplexInputFieldModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.core.widget.MapFieldModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.core.widget.grid.UserColumn;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingOrderModel;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingParameterModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.DateFormatType;
import cn.com.easyerp.framework.enums.LogType;
import cn.com.easyerp.framework.exception.ApplicationException;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service
public class DataService {

    public static final String defaultPasswordText = "********";
    public static final String DEFAULT_CODE_TYPE = "year";
    public static final int DEFAULT_CODE_LONG = 1;
    public static final String USER_JS_PATH = "js/userJs/";
    public static final String CODE_RULE = "G1";
    public static final String COLUMN_CREATE_USER = "cre_user";
    public static final String COLUMN_CREATE_DATE = "cre_date";
    public static final String COLUMN_UPDATE_USER = "upd_user";
    public static final String COLUMN_UPDATE_DATE = "upd_date";
    public static final String COLUMN_UPDATE_DATE_ORIG = "upd_date_orig";
    public static final String COLUMN_OWNER = "owner";
    public static final String NAME_EXPRESSION = "ref____name_Expression";
    public static final String NO_AUTH_ROLE_ID = "admin";
    public static final Set<String> sysGroupColumnName = new HashSet<>();
    public static final String SYS_GROUP_NAME = "_SYS_COLUMN";
    public static final int MAX_QUERY_BIND_PARAMETERS_COUNT = 2000;
    private static final Map<String, Integer> dataType = new HashMap<>();
    private static final Map<String, Map<DateFormatType, SimpleDateFormat>> dateFormats = new HashMap<>();
    // private static final String TAG = "DataService";
    // private static final String SYS_USER_ALIAS_FOR_AUTH = "sys_user_for_auth";
    private static final Pattern WITH_REF_PATTERN = Pattern.compile("#[a-zA-Z]");
    public String rootPath = "";
    public String projectUrl = "";
    public EncrypDES des;
    @Loggable
    private Logger logger;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private CacheService cache;
    @Autowired
    private AutoKeyService autoKeyService;
    // @Autowired
    // private StorageService storageService;

    public DataService() {
        try {
            this.des = new EncrypDES("G1");
        } catch (Exception e) {
            throw new ApplicationException("EncrypDES false");
        }
    }

    @Autowired
    private UserColumnDao userColumnDao;
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private AuthService authService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private LogService logService;

    static {
        dataType.put("string", Integer.valueOf(1));
        dataType.put("digits", Integer.valueOf(2));
        dataType.put("number", Integer.valueOf(3));
        dataType.put("date", Integer.valueOf(4));
        dataType.put("boolean", Integer.valueOf(5));
        dataType.put("password", Integer.valueOf(6));
        dataType.put("pic", Integer.valueOf(7));
        dataType.put("file", Integer.valueOf(8));
        dataType.put("link", Integer.valueOf(9));
        dataType.put("email", Integer.valueOf(10));
        dataType.put("time", Integer.valueOf(11));
        dataType.put("datetime", Integer.valueOf(12));
        dataType.put("auto", Integer.valueOf(13));
        dataType.put("textArea", Integer.valueOf(14));
        dataType.put("editorInput", Integer.valueOf(15));
        dataType.put("color", Integer.valueOf(16));

        sysGroupColumnName.add("cre_user");
        sysGroupColumnName.add("cre_date");
        sysGroupColumnName.add("upd_user");
        sysGroupColumnName.add("upd_date");
        Map formats = new HashMap<>();
        formats.put(DateFormatType.month, new SimpleDateFormat("yyyy-MM"));
        formats.put(DateFormatType.date, new SimpleDateFormat("yyyy-MM-dd"));
        dateFormats.put("cn", formats);
        dateFormats.put("jp", formats);
        dateFormats.put("en", formats);
    }

    public static Map<String, Integer> getDataType() {
        return dataType;
    }

    public static List<ColumnValue> mapToCV(Map<String, Object> map) {
        if (map == null)
            return null;
        List<ColumnValue> ret = new ArrayList<ColumnValue>(map.size());
        for (Map.Entry<String, Object> entry : map.entrySet())
            ret.add(new ColumnValue((String) entry.getKey(), entry.getValue()));
        return ret;
    }

    public static DatabaseDataMap cvToMap(List<ColumnValue> values) {
        if (values == null)
            return null;
        DatabaseDataMap map = new DatabaseDataMap(values.size());
        for (ColumnValue cv : values)
            map.put(cv.getId(), cv.getValue());
        return map;
    }

    public static boolean isSystemGroup(String id) {
        return "_SYS_COLUMN".equals(id);
    }

    public SystemParameter getSystemParam() {
        return this.cache.getSystemParam();
    }

    public String getDictText(String key1, String key2) {
        Map<String, I18nDescribe> dict = this.cache.getDict(key1);
        if (dict == null)
            return "";
        return i18nString((I18nDescribe) this.cache.getDict(key1).get(key2));
    }

    public Map<String, I18nDescribe> getDict(String key1) {
        return this.cache.getDict(key1);
    }

    public Map<String, Map<String, I18nDescribe>> getDicts() {
        return this.cache.getDicts();
    }

    public I18nDescribe getDictNameI18N(String dictName) {
        return this.cache.getDictNameI18N(dictName);
    }

    public OperationDescribe getOperation(String oid) {
        return (OperationDescribe) this.cache.getOperations().get(oid);
    }

    public TriggerDescribe getTrigger(String actionId) {
        return (TriggerDescribe) this.cache.getTriggers().get(actionId);
    }

    public ComplexColumnDescribe getComplexColumn(String id) {
        return (ComplexColumnDescribe) this.cache.getComplexColumns().get(id);
    }

    public TableShortcutDescribe getTableShortcut(String sid) {
        return (TableShortcutDescribe) this.cache.getTableShortcuts().get(sid);
    }

    public BatchDescribe getBatch(String bid) {
        return this.cache.getBatch(bid);
    }

    public ColumnDescribe getColumnDesc(FieldModelBase field) {
        return getColumnDesc(field.getTable(), field.getColumn());
    }

    public ColumnDescribe getColumnDesc(String table, String column) {
        return this.cache.getTableDesc(table).getColumn(column);
    }

    public Object getBizParam(String table, String column) {
        return ((Map) this.cache.getBizParam().get(table)).get(column);
    }

    public List<FieldModelBase> buildModel(String tableName, Map<String, Object> map) {
        return buildModel(getTableDesc(tableName), map);
    }

    private String makeRefMapKey(String name) {
        return name + ".ref";
    }

    public List<FieldModelBase> buildModel(TableDescribe table, Map<String, Object> map) {
        return buildModel(table, map, true);
    }

    public boolean isMapColumn(ColumnDescribe column) {
        boolean b = (column.getIs_multiple() == 1 && column.getRef_table_name() != null);
        return b;
    }

    private Map<String, String> getMapList(ColumnDescribe desc, String value) {
        ColumnMapViewStyle map = desc.getViewStyle().getMap();
        String targetName = map.getTarget();
        String id = map.getTargetId();
        String name = map.getTargetName();
        String where = id + " in (select " + map.getInventory() + ".column_key from " + map.getInventory() + " where "
                + map.getInventory() + ".key = '" + value + "')";
        List<Map<String, Object>> list = selectRecordsByValues(targetName, map.getCols(), (List) null, where);
        Map<String, String> ret = new LinkedHashMap<String, String>(list.size());
        for (Map<String, Object> item : list)
            ret.put((String) item.get(id), (name == null) ? "" : (String) item.get(name));
        return ret;
    }

    public List<FieldModelBase> buildModel(TableDescribe table, Map<String, Object> map, boolean strict) {
        List<FieldModelBase> ret = new ArrayList<FieldModelBase>();
        for (ColumnDescribe desc : table.getColumns()) {
            FileFieldModel fileFieldModel1 = null;
            FieldModelBase field;
            @SuppressWarnings("unused")
            ComplexInputFieldModel complexInputFieldModel;
            FileFieldModel fileFieldModel2;
            CheckBoxField checkBoxField;
            // FieldModelBase field;
            Object value;
            if (map != null) {
                if (strict && !map.containsKey(desc.getColumn_name()))
                    continue;
                value = convertValueFromDb(desc, map.get(desc.getColumn_name()));
            } else {
                value = null;
            }
            switch (desc.getData_type()) {

            case 1:
            case 13:
            case 14:
            case 15:
                if (!Common.isBlank(desc.getRef_table_name()) && desc.getIs_multiple() == 0) {
                    Map<String, Object> ref;

                    if (value instanceof Integer)
                        value = value + "";
                    if (Common.isBlank((String) value)) {
                        ref = null;
                    } else {

                        ref = (Map) map.get(makeRefMapKey(desc.getColumn_name()));
                        if (ref == null) {
                            String v = String.class.isInstance(value) ? (String) value : String.valueOf(value);
                            if (desc.getRefFilter() != null) {
                                ref = getFilterRef(desc, v, map);
                            } else if (Common.isBlank(desc.getRef_table_sql())) {
                                ref = getRef(desc, v, null);
                            } else {
                                ref = getSqlRef(desc, v, map);
                            }
                        }
                    }
                    if (ref != null) {
                        ref.put("ref____name_Expression",
                                buildNameExpression(this.cacheService.getTableDesc(desc.getRef_table_name()), ref));
                    }
                    FieldWithRefModel fieldWithRefModel = new FieldWithRefModel(desc.getTable_id(),
                            desc.getColumn_name(), value, ref);
                    ret.add(fieldWithRefModel);
                    break;
                }
                if (isMapColumn(desc)) {
                    MapFieldModel mapFieldModel = new MapFieldModel(desc.getTable_id(), desc.getColumn_name(), value,
                            getMapList(desc, (String) value));
                    ret.add(mapFieldModel);
                    break;
                }
            case 4:
            case 9:
            case 10:
            case 11:
            case 12:
                field = new FieldModelBase(desc.getTable_id(), desc.getColumn_name(), value);
                ret.add(field);
                break;
            case 2:
            case 3:
                if (desc.getComplex_id() == null) {
                    field = new FieldModelBase(desc.getTable_id(), desc.getColumn_name(), value);
                    ret.add(field);
                    break;
                }
                complexInputFieldModel = new ComplexInputFieldModel(desc, value,
                        getComplexColumn(desc.getComplex_id()));
                break;

            case 5:
                checkBoxField = new CheckBoxField(desc.getTable_id(), desc.getColumn_name(),
                        (value != null && ((Boolean) value).booleanValue()) ? true : false);
                ret.add(checkBoxField);
                break;
            case 6:
                field = new FieldModelBase(desc.getTable_id(), desc.getColumn_name(), value);
                ret.add(field);
                break;
            case 7:
                fileFieldModel2 = new FileFieldModel(desc.getTable_id(), desc.getColumn_name(), value);
                fileFieldModel2.setValue((desc.getDefault_value() == null) ? "" : desc.getDefault_value());
                ret.add(fileFieldModel2);
                break;
            case 8:
                fileFieldModel1 = new FileFieldModel(desc.getTable_id(), desc.getColumn_name(), value);
                ret.add(fileFieldModel1);
                break;
            default:
                throw new ApplicationException(String.format("not defined data type: '%d'",
                        new Object[] { Integer.valueOf(desc.getData_type()) }));
            }
        }
        return ret;
    }

    public Object convertValueFromDb(ColumnDescribe desc, Object value) {
        if (value == null)
            return null;
        switch (desc.getData_type()) {
        case 1:
            if (Clob.class.isInstance(value))
                return ClobSerializer.convert((Clob) value);
        case 2:
        case 3:
        case 4:
        case 7:
        case 8:
        case 9:
        case 10:
        case 11:
        case 12:
        case 13:
        case 14:
        case 15:
            return value;
        case 5:
            return Boolean.valueOf((((Integer) value).intValue() == 1));
        case 6:
            return value;
        }
        throw new ApplicationException(
                String.format("not defined data type: '%d'", new Object[] { Integer.valueOf(desc.getData_type()) }));
    }

    public Map<String, Object> selectRecordByKey(String tableName, Map<String, Object> valueMap) {
        TableDescribe table = getTableDesc(tableName);
        String[] keys = table.getIdColumns();

        List<ColumnValue> cv = new ArrayList<ColumnValue>(keys.length);
        for (String key : keys)
            cv.add(new ColumnValue(key, valueMap.get(key)));
        List<ColumnDescribe> columns = table.getColumns();
        Set<String> columnsNeeded = new HashSet<String>(columns.size());
        for (ColumnDescribe column : columns)
            columnsNeeded.add(column.getColumn_name());
        List<Map<String, Object>> list = selectRecordsByValues(tableName, columnsNeeded, cv, null);
        return (list.size() == 0) ? null : (Map) list.get(0);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded) {
        return selectRecordsByValues(tableName, columnsNeeded, (List) null, null);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            Map<String, Object> valueMap) {
        return selectRecordsByValues(tableName, columnsNeeded, mapToCV(valueMap), null);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            Map<String, Object> valueMap, String where) {
        return selectRecordsByValues(tableName, columnsNeeded, mapToCV(valueMap), where);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            List<ColumnValue> cv, String where) {
        return selectRecordsByValues(tableName, columnsNeeded, cv, where, null);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            Map<String, Object> valueMap, String where, String[] orderBy) {
        return selectRecordsByValues(tableName, columnsNeeded, mapToCV(valueMap), where, orderBy);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            List<ColumnValue> cv, String where, String[] orderBy) {
        return selectRecordsByValues(tableName, columnsNeeded, cv, where, orderBy, null);
    }

    public List<Map<String, Object>> selectRecordsByValues(String tableName, Collection<String> columnsNeeded,
            List<ColumnValue> cv, String where, String[] orderBy, List<ReferenceModel> additionalRefs) {
        TableDescribe table = getTableDesc(tableName);
        if (columnsNeeded == null) {
            List<ColumnDescribe> columns = table.getColumns();
            columnsNeeded = new HashSet<String>(columns.size());
            for (ColumnDescribe column : columns)
                columnsNeeded.add(column.getColumn_name());
        }
        Iterator<String> i = columnsNeeded.iterator();
        while (i.hasNext()) {
            String column = (String) i.next();
            if (table.getColumn(column).isVirtual()) {
                i.remove();
            }
        }
        List<ReferenceModel> refs = makeRefModels(table, columnsNeeded);
        if (refs == null) {
            refs = additionalRefs;
        } else if (additionalRefs != null) {
            refs.addAll(additionalRefs);
        }
        String where_ids = where(table, refs);
        if (where != null && !"".equals(where)) {
            if (where_ids != null && !"".equals(where_ids))
                where = where + " and " + where_ids;
        } else {
            where = where_ids;
        }

        if (orderBy == null) {
            orderBy = makeOrderByArray(table);
        }
        Set<ColumnDescribe> columnList = new HashSet<ColumnDescribe>(columnsNeeded.size());
        Iterator<String> column = columnsNeeded.iterator();
        while (column.hasNext()) {
            columnList.add(table.getColumn((String) column.next()));
        }
        if (cv != null && cv.size() == 0)
            cv = null;

        String encrypt_str = this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey());
        List<Map<String, Object>> ret = this.systemDao.selectDataWithCondition(tableName, columnList, cv, where,
                orderBy, refs, null, false, null, encrypt_str, true);

        if (refs == null)
            return ret;
        return makeRefData(ret);
    }

    private List<ColumnValue> mapParentKey(TableDescribe table, Map<String, Object> parentData, boolean isRefChild,
            String refChildColumn) {
        if (parentData == null)
            return null;
        List<ColumnValue> ret = new ArrayList<ColumnValue>();
        if (isRefChild) {
            TableDescribe parentTable = this.cache.getTableDesc(table.getColumn(refChildColumn).getRef_table_name());
            ret.add(new ColumnValue(refChildColumn, parentData.get(parentTable.getIdColumns()[0])));
        } else {
            TableDescribe parentTable = getTableDesc(table.getParent_id());
            String[] parentKeys = parentTable.getIdColumns();
            String[] keys = table.getIdColumns();
            for (int i = 0; i < parentKeys.length; i++)
                ret.add(new ColumnValue(keys[i], parentData.get(parentKeys[i])));
        }
        return ret;
    }

    public List<ReferenceModel> makeRefModels(TableDescribe table) {
        return makeRefModels(table, null);
    }

    @SuppressWarnings("unused")
    private List<ReferenceModel> makeRefModels(TableDescribe table, Collection<String> columnsNeeded, String type) {
        if ("table".equals(type)) {
            List<ColumnDescribe> columns = table.getColumns();
            List<ReferenceModel> refs = null;
            int suffix = 0;
            for (ColumnDescribe column : columns) {
                if (columnsNeeded != null && !columnsNeeded.contains(column.getColumn_name()))
                    continue;
                if (column.isVirtual())
                    continue;
                String ref_table_name = column.getRef_table_name();
                if (!Common.isBlank(ref_table_name)) {
                    if (refs == null) {
                        refs = new ArrayList<ReferenceModel>();
                    }
                    TableDescribe child = getTableDesc(ref_table_name);
                    if (child.getIdColumns().length > 1) {
                        continue;
                    }
                    refs.add(new ReferenceModel(column,
                            String.format("%s_%d", new Object[] { ref_table_name, Integer.valueOf(suffix++) }), child));
                }
            }
            return refs;
        }
        return makeRefModels(table, null);
    }

    private List<ReferenceModel> makeRefModels(TableDescribe table, Collection<String> columnsNeeded) {
        List<ColumnDescribe> columns = table.getColumns();
        List<ReferenceModel> refs = null;
        int suffix = 0;
        for (ColumnDescribe column : columns) {
            if (columnsNeeded != null && !columnsNeeded.contains(column.getColumn_name()))
                continue;
            if (column.isVirtual())
                continue;
            String ref_table_name = column.getRef_table_name();
            if (!Common.isBlank(ref_table_name)) {
                if (refs == null) {
                    refs = new ArrayList<ReferenceModel>();
                }
                TableDescribe child = getTableDesc(ref_table_name);

                if (child.getIdColumns().length > 1) {
                    TableDescribe clone_child = null;
                    try {
                        clone_child = (TableDescribe) child.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                    refs.add(new ReferenceModel(column,
                            String.format("%s_%d", new Object[] { ref_table_name, Integer.valueOf(suffix++) }),
                            clone_child));
                    continue;
                }
                String id_column_to_mysql = child.getId_column();
                id_column_to_mysql = id_column_to_mysql.replace("[", "");
                id_column_to_mysql = id_column_to_mysql.replace("]", "");
                refs.add(new ReferenceModel(column,
                        String.format("%s_%d", new Object[] { ref_table_name, Integer.valueOf(suffix++) }), child));
            }
        }
        return refs;
    }

    private List<ReferenceModel> makeTableRefModel(TableDescribe table) {
        Set<String> columnsNeedRefs = new HashSet<String>();
        for (ColumnDescribe column : table.getColumns()) {
            if (!isSystemGroup(column.getGroup_name()) && !column.isVirtual())
                columnsNeedRefs.add(column.getColumn_name());
        }
        return makeRefModels(table, columnsNeedRefs);
    }

    public List<Map<String, Object>> makeRefData(List<Map<String, Object>> records) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> record : records) {
            Map<String, Object> rec = new HashMap<String, Object>();
            ret.add(rec);
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                Map<String, Object> ref;
                String name = (String) entry.getKey();
                Object val = entry.getValue();
                if (!name.contains("$")) {
                    rec.put(name, val);

                    continue;
                }
                String[] names = name.split("\\$");

                String key = names[0] + ".ref";
                if (rec.containsKey(key)) {
                    ref = (Map) rec.get(key);
                } else {
                    ref = new HashMap<String, Object>();
                    rec.put(key, ref);
                }
                ref.put(names[1], entry.getValue());
            }
        }
        return ret;
    }

    public List<Map<String, Object>> makeRefDataForJava(List<Map<String, Object>> records) {
        List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> record : records) {
            Map<String, Object> rec = new HashMap<String, Object>();
            ret.add(rec);
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                Map<String, Object> ref;
                String name = (String) entry.getKey();
                Object val = entry.getValue();
                if (!name.contains("$")) {
                    rec.put(name, val);
                    continue;
                }
                String[] names = name.split("\\$");
                String key = names[0] + ".ref";
                if (rec.containsKey(key)) {
                    ref = (Map) rec.get(key);
                } else {
                    ref = new HashMap<String, Object>();
                    rec.put(key, ref);
                }
                ref.put(names[1], entry.getValue());
            }
        }
        return ret;
    }

    private String[] makeOrderByArray(TableDescribe table) {
        return makeOrderByArray(table, null);
    }

    private String[] makeOrderByArray(TableDescribe table, String orderBy) {
        if (orderBy != null) {
            orderBy = orderBy.replace("[", "");
            orderBy = orderBy.replace("]", "");
            return orderBy.split(",");
        }
        if (table.getOrder_by() != null) {
            String tableOrderBY = table.getOrder_by();
            tableOrderBY = tableOrderBY.replace("[", "");
            tableOrderBY = tableOrderBY.replace("]", "");
            return tableOrderBY.split(",");
        }
        if (table.getChild_seq() != null && !"".equals(table.getChild_seq())) {
            return table.getChild_seq().split(",");
        }
        return null;
    }

    private String buildApproveCondition(String tableName, Map<String, Object> approveSelectParam) {
        if (approveSelectParam != null && approveSelectParam.get("myAlreadyApprove") != null) {
            return buildMyAlreadyApproveCondition(tableName);
        }
        String currentUserId = AuthService.getCurrentUserId();
        TableDescribe tableDesc = getTableDesc(tableName);
        if (tableDesc == null || tableDesc.getIdColumns() == null || tableDesc.getIdColumns().length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(
                " EXISTS (SELECT c1.data_id FROM c_approve_flow c1 INNER JOIN c_approve_flow_node c2 ON c1.approve_id = c2.approve_id INNER JOIN c_approve_flow_user c3 ON c2.approve_id = c3.approve_id AND c2.block_id = c3.block_id INNER JOIN "
                        + tableName + " data_table " + " ON data_table.`" + tableDesc.getIdColumns()[0]
                        + "` = c1.data_id " + " WHERE c1.state = 1 AND c2.state = 'wait' AND c3.`state` = 'wait'"
                        + " AND (c2.block_id = '" + approveSelectParam.get("blockId") + "' OR c2.belong_block = '"
                        + approveSelectParam.get("blockId") + "')" + " AND c2.sequence = "
                        + " (SELECT MAX(c4.sequence) FROM c_approve_flow_node c4 "
                        + " WHERE c2.approve_id = c4.approve_id AND c2.block_id = c4.block_id)" + " AND c2.node_seq = "
                        + " (SELECT MIN(c5.node_seq) FROM c_approve_flow_node c5 "
                        + " WHERE c2.approve_id = c5.approve_id AND c5.state = 'wait')" + " AND c3.user = '"
                        + currentUserId + "' AND c1.data_id = " + tableName + ".`" + tableDesc.getIdColumns()[0]
                        + "`)");
        return sb.toString();
    }

    private String buildMyAlreadyApproveCondition(String tableName) {
        String currentUserId = AuthService.getCurrentUserId();
        TableDescribe tableDesc = getTableDesc(tableName);
        if (tableDesc == null || tableDesc.getIdColumns() == null || tableDesc.getIdColumns().length == 0) {
            return null;
        }
        return " EXISTS (SELECT 1 FROM c_approve_flow c1 INNER JOIN c_approve_flow_node c2 ON c1.approve_id = c2.approve_id INNER JOIN c_approve_flow_user c3 ON c2.approve_id = c3.approve_id AND c2.block_id = c3.block_id WHERE c2.state <> 'wait' AND c1.table_id = '"
                + tableName + "' AND c3.`user` = '" + currentUserId + "'" + " AND " + tableName + ".`"
                + tableDesc.getIdColumns()[0] + "` = c1.data_id)";
    }

    private String buildApproveSelect(String tableName) {
        TableDescribe tableDesc = getTableDesc(tableName);
        String[] idColumns = tableDesc.getIdColumns();
        if (tableDesc == null || idColumns == null || idColumns.length == 0) {
            return null;
        }
        String currentUserId = AuthService.getCurrentUserId();
        String waitApprove = "( EXISTS (SELECT c1.data_id FROM c_approve_flow c1 INNER JOIN c_approve_flow_node c2 ON c1.approve_id = c2.approve_id INNER JOIN c_approve_flow_user c3 ON c2.approve_id = c3.approve_id AND c2.block_id = c3.block_id INNER JOIN "
                + tableName + " data_table " + " ON data_table.`" + idColumns[0] + "` = c1.data_id "
                + " WHERE c1.state = 1 AND c2.state = 'wait' AND c3.`state` = 'wait'" + " AND c2.sequence = "
                + " (SELECT MAX(c4.sequence) FROM c_approve_flow_node c4 "
                + " WHERE c2.approve_id = c4.approve_id AND c2.block_id = c4.block_id)" + " AND c2.node_seq = "
                + " (SELECT MIN(c5.node_seq) FROM c_approve_flow_node c5 "
                + " WHERE c2.approve_id = c5.approve_id AND c5.state = 'wait')" + " AND c3.user = '" + currentUserId
                + "' AND c1.data_id = " + tableName + ".`" + idColumns[0] + "`)";
        String approve = " OR EXISTS (SELECT c1.data_id FROM c_approve_flow c1 INNER JOIN c_approve_flow_node c2 ON c1.approve_id = c2.approve_id INNER JOIN c_approve_flow_user c3 ON c2.approve_id = c3.approve_id AND c2.block_id = c3.block_id INNER JOIN "
                + tableName + " data_table " + " ON data_table.`" + idColumns[0] + "` = c1.data_id "
                + " WHERE c2.sequence = " + " (SELECT MAX(c4.sequence) FROM c_approve_flow_node c4 "
                + " WHERE c2.approve_id = c4.approve_id AND c2.block_id = c4.block_id)" + " AND c3.user = '"
                + currentUserId + "'" + " AND c3.`state` <> 'wait'" + " AND c1.data_id = " + tableName + ".`"
                + idColumns[0] + "`))";
        return waitApprove + approve;
    }

    private String buildGroupAuthCondition(String tableName, List<AuthGroup> optionAuthGroup) {
        TableDescribe table = getTableDesc(tableName);
        TableDescribe userTable = getTableDesc("m_user");
        String[] idColumns = userTable.getIdColumns();
        if (idColumns == null || idColumns.length == 0)
            return null;
        String userIdColumn = idColumns[idColumns.length - 1];
        ColumnDescribe owner = table.getColumn("owner");

        if (owner == null)
            return null;
        if (optionAuthGroup != null && optionAuthGroup.size() != 0) {
            String ownerColumn = tableName + ".`owner`";
            AuthDetails currentUser = AuthService.getCurrentUser();
            String uid = currentUser.getId();
            StringBuilder sb = new StringBuilder();
            sb.append(" ( ");
            boolean flag = false;
            for (AuthGroup authGroup : optionAuthGroup) {

                if ("*".equals(authGroup.getUser())
                        || ("*".equals(authGroup.getDepartment()) && "*".equals(authGroup.getRole()))) {
                    return null;
                }

                if (authGroup.getUser() != null && !"".equals(authGroup.getUser())
                        && !"*".equals(authGroup.getUser())) {
                    if (authGroup.getUser().equals(currentUser.getId())) {
                        return null;
                    }
                    flag = true;
                }

                if ("*".equals(authGroup.getDepartment())) {
                    if (authGroup.getRole() == null || "".equals(authGroup.getRole())) {
                        return null;
                    }
                    if (authGroup.getRole().equals(currentUser.getRole_id())) {
                        return null;
                    }
                    flag = true;

                    if (currentUser.getAdditional() != null && currentUser.getAdditional().size() != 0)
                        for (AuthDetailsAdditional additional : currentUser.getAdditional()) {
                            if (authGroup.getRole().equals(additional.getRole_id())) {
                                return null;
                            }
                        }
                } else if (!"*".equals(authGroup.getDepartment()) && !"*".equals(authGroup.getRole())
                        && (authGroup.getDepartment_relation() == null || "".equals(authGroup.getDepartment_relation()))
                        && (authGroup.getUser() == null || "".equals(authGroup.getUser()))) {
                    if (sb.length() > 5) {
                        sb.append(" or ");
                    }
                    if (authGroup.getRole().equals(currentUser.getRole_id())
                            && authGroup.getDepartment().equals(currentUser.getDepartment_id())) {
                        return null;
                    }
                    flag = true;

                    if (currentUser.getAdditional() != null && currentUser.getAdditional().size() != 0)
                        for (AuthDetailsAdditional additional : currentUser.getAdditional()) {
                            if (authGroup.getRole().equals(additional.getRole_id())
                                    && authGroup.getDepartment().equals(additional.getDepartment_id())) {
                                return null;
                            }
                        }
                } else if (!"*".equals(authGroup.getDepartment()) && "*".equals(authGroup.getRole())) {

                    if (authGroup.getDepartment().equals(currentUser.getDepartment_id())) {
                        return null;
                    }
                    flag = true;
                    if (currentUser.getAdditional() != null && currentUser.getAdditional().size() != 0) {
                        for (AuthDetailsAdditional additional : currentUser.getAdditional()) {
                            if (authGroup.getRole().equals(additional.getRole_id())
                                    && authGroup.getDepartment().equals(additional.getDepartment_id())) {
                                return null;
                            }
                        }
                    }
                }
                if (authGroup.getDepartment_relation() != null && !"".equals(authGroup.getDepartment_relation())) {
                    String sql;
                    switch (authGroup.getDepartment_relation()) {
                    case "0":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" EXISTS (");
                        sql = "select 1 from m_user";
                        sql = sql + " sys_user_for_auth_1 where ";
                        sql = sql + " sys_user_for_auth_1.department_id = (";
                        sql = sql + " select sys_user_for_auth_2.department_id from m_user sys_user_for_auth_2 ";
                        sql = sql + " where sys_user_for_auth_2.`" + userIdColumn + "` = '" + uid + "'";
                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and sys_user_for_auth_2.role_id = '" + authGroup.getRole() + "'";
                        sql = sql + ")";
                        sql = sql + " AND sys_user_for_auth_1.`" + userIdColumn + "`=" + ownerColumn + ")";
                        sql = sql
                                + " OR EXISTS (SELECT 1 FROM m_user m1 INNER JOIN m_user_additional m2 ON m1.id = m2.user_id WHERE m2.user_id  = '"
                                + uid + "' " + " AND m2.dept = (SELECT m1.department_id FROM m_user m1 WHERE m1.id = "
                                + tableName + ".`owner`)";

                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and m2.role = '" + authGroup.getRole() + "'";
                        sql = sql + " AND m1.id = '" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "1":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" EXISTS (");
                        sql = "select 1 from m_user";
                        sql = sql + " sys_user_for_auth_1 where ";
                        sql = sql + " sys_user_for_auth_1.department_id in (";
                        sql = sql
                                + "select m_department_for_auth_2.department_id from m_department m_department_for_auth_2 ";
                        sql = sql + " where m_department_for_auth_2.`id` = (";
                        sql = sql + " select sys_user_for_auth_3.department_id from m_user sys_user_for_auth_3 ";
                        sql = sql + " where sys_user_for_auth_3.`" + userIdColumn + "` = '" + uid + "'";
                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and sys_user_for_auth_3.role_id = '" + authGroup.getRole() + "'";
                        sql = sql + "))";
                        sql = sql + " AND sys_user_for_auth_1.`" + userIdColumn + "`=" + ownerColumn + ")";
                        sql = sql
                                + " OR EXISTS (SELECT 1 FROM m_user m1 INNER JOIN m_user_additional m2 ON m1.id = m2.user_id WHERE m2.user_id  = '"
                                + uid + "' "
                                + " AND m2.dept = (SELECT m3.parent_id FROM m_department m3 WHERE m3.id = (SELECT m1.department_id FROM m_user m1 WHERE m1.id = "
                                + tableName + ".`owner`))";

                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and m2.role = '" + authGroup.getRole() + "'";
                        sql = sql + " AND m1.id ='" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "2":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" EXISTS (");
                        sql = "select 1 from m_user";
                        sql = sql + " sys_user_for_auth_1 where ";
                        sql = sql + " sys_user_for_auth_1.department_id in (";
                        sql = sql
                                + " select m_department_for_auth_1.parent_id from m_department m_department_for_auth_1 ";
                        sql = sql + " where m_department_for_auth_1.`id` in (";
                        sql = sql
                                + "select m_department_for_auth_2.parent_id from m_department m_department_for_auth_2 ";
                        sql = sql + " where m_department_for_auth_2.`id` = (";
                        sql = sql + " select sys_user_for_auth_3.department_id  from m_user sys_user_for_auth_3 ";
                        sql = sql + " where sys_user_for_auth_3.`" + userIdColumn + "` = '" + uid + "'";
                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and sys_user_for_auth_3.role_id = '" + authGroup.getRole() + "'";
                        sql = sql + ")))";
                        sql = sql + " AND sys_user_for_auth_1.`" + userIdColumn + "`=" + ownerColumn + ")";
                        sql = sql
                                + " OR EXISTS (SELECT 1 FROM m_user m1 INNER JOIN m_user_additional m2 ON m1.id = m2.user_id WHERE m2.user_id  = '"
                                + uid + "' "
                                + " AND m2.dept = (SELECT m4.parent_id FROM m_department m4 WHERE m4.id = (SELECT m3.parent_id FROM m_department m3 WHERE m3.id = (SELECT m1.department_id FROM m_user m1 WHERE m1.id = "
                                + tableName + ".`owner`)))";

                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and m2.role = '" + authGroup.getRole() + "'";
                        sql = sql + " AND m1.id ='" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "3":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" EXISTS (");
                        sql = "select 1 from m_user";
                        sql = sql + " sys_user_for_auth_1 where ";
                        sql = sql + " sys_user_for_auth_1.department_id in (";
                        sql = sql
                                + " select m_department_for_auth_10.parent_id from m_department m_department_for_auth_10 ";
                        sql = sql + " where m_department_for_auth_10.`id` in (";
                        sql = sql
                                + " select m_department_for_auth_1.parent_id from m_department m_department_for_auth_1 ";
                        sql = sql + " where m_department_for_auth_1.`id` in (";
                        sql = sql
                                + "select m_department_for_auth_2.parent_id from m_department m_department_for_auth_2 ";
                        sql = sql + " where m_department_for_auth_2.`id` = (";
                        sql = sql + " select sys_user_for_auth_3.department_id  from m_user sys_user_for_auth_3 ";
                        sql = sql + " where sys_user_for_auth_3.`" + userIdColumn + "` = '" + uid + "'";
                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and sys_user_for_auth_3.role_id = '" + authGroup.getRole() + "'";
                        sql = sql + "))))";
                        sql = sql + " AND sys_user_for_auth_1.`" + userIdColumn + "`=" + ownerColumn + ")";
                        sql = sql
                                + " OR EXISTS (SELECT 1 FROM m_user m1 INNER JOIN m_user_additional m2 ON m1.id = m2.user_id WHERE m2.user_id  = '"
                                + uid + "' "
                                + " AND m2.dept = (SELECT m5.parent_id FROM m_department m5 where m5.id = (SELECT m4.parent_id FROM m_department m4 WHERE m4.id = (SELECT m3.parent_id FROM m_department m3 WHERE m3.id = (SELECT m1.department_id FROM m_user m1 WHERE m1.id = "
                                + tableName + ".`owner`))))";

                        if (!"*".equals(authGroup.getRole()))
                            sql = sql + " and m2.role = '" + authGroup.getRole() + "'";
                        sql = sql + " AND m1.id ='" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    }
                }
                if (authGroup.getUser_relation() != null && !"".equals(authGroup.getUser_relation())) {
                    String sql;
                    switch (authGroup.getUser_relation()) {
                    case "0":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" " + ownerColumn + " = (");
                        sql = "select sys_user_for_auth_1.`" + userIdColumn + "` from " + "m_user";
                        sql = sql + " sys_user_for_auth_1 ";
                        sql = sql + " where sys_user_for_auth_1.`" + userIdColumn + "` = " + ownerColumn;
                        sql = sql + " and sys_user_for_auth_1.`" + userIdColumn + "` = '" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "1":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" " + ownerColumn + " = (");
                        sql = "select " + ownerColumn + " from " + "m_user";
                        sql = sql + " sys_user_for_auth_1 ";
                        sql = sql + " where sys_user_for_auth_1.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_2.parent_id from m_user sys_user_for_auth_2 ";
                        sql = sql + " where sys_user_for_auth_2.`" + userIdColumn + "` = " + ownerColumn + ")";
                        sql = sql + " and sys_user_for_auth_1.`" + userIdColumn + "` = '" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "2":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" " + ownerColumn + " = (");
                        sql = "select " + ownerColumn + " from " + "m_user";
                        sql = sql + " sys_user_for_auth_1 ";
                        sql = sql + " where sys_user_for_auth_1.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_3.parent_id from m_user sys_user_for_auth_3 ";
                        sql = sql + " where sys_user_for_auth_3.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_2.parent_id from m_user sys_user_for_auth_2 ";
                        sql = sql + " where sys_user_for_auth_2.`" + userIdColumn + "` = " + ownerColumn + "))";
                        sql = sql + " and sys_user_for_auth_1.`" + userIdColumn + "` = '" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    case "3":
                        if (sb.length() > 5)
                            sb.append(" or ");
                        sb.append(" " + ownerColumn + " = (");
                        sql = "select " + ownerColumn + " from " + "m_user";
                        sql = sql + " sys_user_for_auth_1 ";
                        sql = sql + " where sys_user_for_auth_1.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_4.parent_id from m_user sys_user_for_auth_4 ";
                        sql = sql + " where sys_user_for_auth_4.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_3.parent_id from m_user sys_user_for_auth_3 ";
                        sql = sql + " where sys_user_for_auth_3.`" + userIdColumn + "` = (";
                        sql = sql + " select sys_user_for_auth_2.parent_id from m_user sys_user_for_auth_2 ";
                        sql = sql + " where sys_user_for_auth_2.`" + userIdColumn + "` = " + ownerColumn + ")))";
                        sql = sql + " and sys_user_for_auth_1.`" + userIdColumn + "` = '" + uid + "'";
                        sb.append(sql + ")");
                        break;
                    }
                }
            }
            if (sb.length() > 5) {
                sb.append(" ) ");
                return sb.toString();
            }
            if (flag) {
                return " 1 = 2 ";
            }
        }
        return null;
    }

    public String buildGroupAuthDataCondition(String tableName, List<AuthGroup> optionAuthGroup) {
        TableDescribe table = getTableDesc(tableName);
        String[] idColumns = table.getIdColumns();
        if (idColumns == null || idColumns.length != 1)
            return null;
        if (optionAuthGroup == null)
            return null;
        Map<String, List<AuthGroup>> authDataGroup = new HashMap<String, List<AuthGroup>>();
        for (AuthGroup authGroup : optionAuthGroup) {
            if (authGroup.getData_group_id() == null || "".equals(authGroup.getData_group_id())) {
                if (authDataGroup.get("default") == null) {
                    authDataGroup.put("default", new ArrayList<>());
                }
                ((List) authDataGroup.get("default")).add(authGroup);
                continue;
            }
            if (authDataGroup.get(authGroup.getData_group_id()) == null) {
                authDataGroup.put(authGroup.getData_group_id(), new ArrayList<>());
            }
            ((List) authDataGroup.get(authGroup.getData_group_id())).add(authGroup);
        }

        String authCondition = null;
        for (Map.Entry<String, List<AuthGroup>> entry : authDataGroup.entrySet()) {

            String condition = buildGroupAuthCondition(tableName, (List) entry.getValue());
            AuthDataGroup dataGroup = this.cacheService.getAuthDataGroup((String) entry.getKey());
            if (dataGroup != null) {

                String dataGroupFilter = getAuthDataGroupCondition(dataGroup, tableName);
                String filter = "EXISTS (SELECT 1 FROM (" + dataGroupFilter
                        + ") t_data_group_filter where t_data_group_filter.`" + idColumns[0] + "`=" + tableName + ".`"
                        + idColumns[0] + "`)";

                if (condition != null) {
                    condition = "(" + condition + " AND " + filter + ")";
                } else {
                    condition = filter;
                }
            }
            if (authCondition == null) {
                authCondition = condition;
                continue;
            }
            if (condition != null) {
                authCondition = authCondition + " OR " + condition;
            }
        }
        return authCondition;
    }

    public String getAuthDataGroupCondition(AuthDataGroup dataGroup, String tableName) {
        String sql = "select * from `" + tableName + "` ";
        if (dataGroup == null || dataGroup.getDetails() == null || dataGroup.getDetails().size() == 0)
            return sql;
        sql = sql + " where 1 = 1";
        for (AuthDataGroupDetail detail : dataGroup.getDetails()) {
            String symbol = getSymbol(detail.getSymbol());
            if (symbol == null) {
                continue;
            }
            sql = sql + " and `" + StringEscapeUtils.escapeSql(detail.getColumn_name()) + "`" + symbol + "'"
                    + StringEscapeUtils.escapeSql((detail.getValue() == null) ? "" : detail.getValue()) + "'";
        }
        return sql;
    }

    private String getSymbol(String symbol) {
        if (symbol == null)
            return null;
        switch (symbol) {
        case ">":
            return ">";
        case "<":
            return "<";
        case "=":
            return "=";
        case ">=":
            return ">=";
        case "<=":
            return "<=";
        case "!=":
            return "<>";
        }
        return null;
    }

    private String buildAuthCondition(String tableName, List<AuthGroup> optionAuthGroup, int isApproveSelect,
            Map<String, Object> approveSelectParam) {
        if (isApproveSelect == 1) {
            return buildApproveCondition(tableName, approveSelectParam);
        }
        TableDescribe table = getTableDesc(tableName);
        String approveSelect = null;
        if (table.getIs_approve_select() == 1) {
            approveSelect = buildApproveSelect(tableName);
        }
        String groupAuthCondition = buildGroupAuthDataCondition(tableName, optionAuthGroup);
        if (approveSelect != null) {
            if (groupAuthCondition == null) {
                return approveSelect;
            }
            return "(" + approveSelect + " OR " + groupAuthCondition + ")";
        }
        return groupAuthCondition;
    }

    public int getDataCount(String tableName, Map<String, Object> parentData, String condition,
            List<AuthGroup> optionAuthGroup) {
        TableDescribe table = getTableDesc(tableName);
        return this.systemDao.selectCountWithCondition(tableName, mapParentKey(table, parentData, false, null),
                condition, buildAuthCondition(tableName, optionAuthGroup, 0, null));
    }

    private String[] preparePagingSort(TableDescribe table, DataTablePagingParameterModel paging) {
        List<DataTablePagingOrderModel> pageOrder = paging.getOrder();
        if (pageOrder == null || pageOrder.size() == 0) {
            String[] orders = makeOrderByArray(table);
            if (orders != null) {
                return orders;
            }
            return table.getIdColumns();
        }
        String[] orders = new String[pageOrder.size()];
        for (int i = 0; i < pageOrder.size(); i++) {
            DataTablePagingOrderModel order = (DataTablePagingOrderModel) pageOrder.get(i);
            orders[i] = "`" + order.getName() + "` " + order.getDir();
        }
        return orders;
    }

    public List<Map<String, Object>> fetchDataRecords(String tableName, Map<String, Object> parentData,
            String condition, List<AuthGroup> optionAuthGroup) {
        return fetchDataRecords(tableName, parentData, condition, optionAuthGroup, null, 0, null, false, null);
    }

    public int fetchDataRecordsCount(String tableName, Map<String, Object> parentData, String condition,
            List<AuthGroup> optionAuthGroup, DataTablePagingParameterModel paging, int isApproveSelect,
            Map<String, Object> approveSelectParam, boolean isRefChild, String refChildColumn) {
        TableDescribe table = getTableDesc(tableName);
        List<ColumnDescribe> columns = table.getColumns();
        Set<String> columnList = new HashSet<String>(columns.size());
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual())
                columnList.add(column.getColumn_name());
        }
        List<ReferenceModel> refs = makeRefModels(table);
        String where = where(table, refs);
        if (condition != null && !"".equals(condition)) {
            if (where != null && !"".equals(where))
                condition = condition + " and " + where;
        } else {
            condition = where;
        }
        String[] orderBy = (paging == null) ? makeOrderByArray(table) : preparePagingSort(table, paging);
        return this.systemDao.selectDataWithConditionCount(tableName, columnList,
                mapParentKey(table, parentData, isRefChild, refChildColumn), condition, orderBy, refs,
                buildAuthCondition(tableName, optionAuthGroup, isApproveSelect, approveSelectParam), false, null);
    }

    public List<Map<String, Object>> fetchDataRecords(String tableName, Map<String, Object> parentData,
            String condition, List<AuthGroup> optionAuthGroup, DataTablePagingParameterModel paging,
            int isApproveSelect, Map<String, Object> approveSelectParam, boolean isRefChild, String refChildColumn) {
        TableDescribe table = getTableDesc(tableName);
        List<ColumnDescribe> columns = table.getColumns();
        Set<ColumnDescribe> columnList = new HashSet<ColumnDescribe>(columns.size());
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual())
                columnList.add(column);
        }
        List<ReferenceModel> refs = makeRefModels(table);
        String where = where(table, refs);
        if (condition != null && !"".equals(condition)) {
            if (where != null && !"".equals(where))
                condition = condition + " and " + where;
        } else {
            condition = where;
        }
        String[] orderBy = (paging == null) ? makeOrderByArray(table) : preparePagingSort(table, paging);
        if (orderBy != null)
            for (int i = 0; i < orderBy.length; i++) {
                orderBy[i] = orderBy[i].replace("[", "");
                orderBy[i] = orderBy[i].replace("]", "");
            }
        if (paging != null)
            paging.setStart(paging.getStart());
        String encrypt_str = this.dxRoutingDataSource.getDomainKey();
        List<Map<String, Object>> records = this.systemDao.selectDataWithCondition(tableName, columnList,
                mapParentKey(table, parentData, isRefChild, refChildColumn), condition, orderBy, refs,
                buildAuthCondition(tableName, optionAuthGroup, isApproveSelect, approveSelectParam), false, paging,
                encrypt_str, false);
        if (refs == null)
            return records;
        return makeRefData(records);
    }

    private String where(TableDescribe table, List<ReferenceModel> refs) {
        if (refs == null || refs.size() == 0)
            return null;
        String where = "";
        List<ColumnDescribe> columns = table.getColumns();
        boolean flag = true;
        for (ColumnDescribe ColumnDescribe : columns) {
            String ref_table_sql = ColumnDescribe.getRef_table_sql();
            String ref_table_name = ColumnDescribe.getRef_table_name();
            TableDescribe tableDesc = this.cache.getTableDesc(ref_table_name);
            if (ref_table_sql != null && !"".equals(ref_table_sql) && ref_table_name != null
                    && !"".equals(ref_table_name) && tableDesc.getIdColumns().length > 1) {
                String ref_db_table_name = getDBTableName(refs, ref_table_name);
                if (ref_db_table_name != null) {
                    if (flag) {
                        where = where + " exists ( select * from( " + ref_table_sql;
                        flag = false;
                    } else {
                        where = where + " and exists ( select * from( " + ref_table_sql;
                    }
                    where = where + ") filter_sql where 1=1 ";
                    String id_column = "";
                    String[] id_columns = tableDesc.getIdColumns();
                    for (int i = 0; i < id_columns.length; i++) {
                        id_column = id_columns[i];
                        where = where + " and filter_sql." + id_column;
                        where = where + "=" + ref_db_table_name + "." + id_column;
                    }
                    where = where + ")";
                }
            }
        }
        where = where.replace("${", table.getId() + ".");
        where = where.replace("#{", table.getId() + ".");
        where = where.replace("}", "");
        return "".equals(where) ? null : where;
    }

    private String getDBTableName(List<ReferenceModel> refs, String tableName) {
        if (refs != null)
            for (ReferenceModel referenceModel : refs) {
                String ref_table_name = referenceModel.getAlias().substring(0,
                        referenceModel.getAlias().lastIndexOf("_"));
                if (tableName.equals(ref_table_name)) {
                    return referenceModel.getAlias();
                }
            }
        return null;
    }

    public List<Map<String, Object>> filterDataRecords(ColumnDescribe column, String term, String where) {
        String retTable = column.getRef_table_name();
        TableDescribe table = getTableDesc(retTable);
        String[] idColumns = table.getIdColumns();
        List<ReferenceModel> refs = makeRefModels(table);
        String refWhere = where(table, refs);
        if (where != null && !"".equals(where)) {
            if (refWhere != null && !"".equals(refWhere))
                where = where + " and " + refWhere;
        } else {
            where = refWhere;
        }
        List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(retTable, "read");
        String authCondition = buildAuthCondition(retTable, optionAuthGroup, 0, null);
        if (where == null || "".equals(where)) {
            where = authCondition;
        } else {
            where = where + " and " + authCondition;
        }
        String name_expression = buildNameExpression(column.getTable_id(), table, refs);
        return this.systemDao.filterInputSelect(retTable, table.getColumns(), refs, name_expression,
                idColumns[idColumns.length - 1], term.replace("'", "''"), getSystemParam().getInput_selector_lines(),
                where);
    }

    public Map<String, Object> getFilterRef(ColumnDescribe column, String value, Map<String, Object> map) {
        String where = null;
        String refTableName = column.getRef_table_name();
        for (Map.Entry<String, FilterDescribe> entry : column.getRefFilter().entrySet()) {
            if (where != null) {
                where = where + " and ";
            } else {
                where = "";
            }
            String eq = (String) ((FilterDescribe) entry.getValue()).getValue();
            String name = Common.getSqlParameterName(eq);
            String val = (name == null) ? eq : (String) map.get(name);
            where = where + refTableName + "." + (String) entry.getKey() + " = '" + val + "' ";
        }
        return getRef(column, value, where);
    }

    public Map<String, String> getRefNames(TableDescribe table, Collection<String> keys) {
        List<String> columns = new ArrayList<String>(2);
        String id = table.getIdColumns()[table.getIdColumns().length - 1];
        String name = table.getName_column();
        columns.add(id);
        columns.add(name);
        List<Map<String, Object>> list = selectRecordsByValues(table.getId(), columns, (List) null,
                id + " in (" + Common.join(keys, ",") + ")");
        Map<String, String> ret = new HashMap<String, String>(keys.size());
        for (Map<String, Object> map : list)
            ret.put((String) map.get(id), (String) map.get(name));
        return ret;
    }

    public Map<String, Object> getRef(ColumnDescribe column, String value, String where) {
        String tableName = column.getRef_table_name();
        TableDescribe table = getTableDesc(tableName);
        String[] idColumns = table.getIdColumns();
        return getRef(tableName, table.getColumns(), idColumns[idColumns.length - 1], value, where);
    }

    public Map<String, Object> getRef(String tableName, List<ColumnDescribe> columns, String conditionColumn,
            String value, String where) {
        if (Common.isBlank(value))
            return null;
        TableDescribe table = getTableDesc(tableName);
        Set<ColumnDescribe> columnNeeded = new HashSet<ColumnDescribe>(columns.size());
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual())
                columnNeeded.add(column);
        }
        List<ColumnValue> cv = new ArrayList<ColumnValue>();
        cv.add(new ColumnValue(conditionColumn, value));
        String encrypt_str = this.dxRoutingDataSource.getDomainKey();

        List<Map<String, Object>> list = this.systemDao.selectDataWithCondition(tableName, columnNeeded, cv, where,
                null, makeRefModels(table), null, false, null, encrypt_str, false);
        return (list.size() == 0) ? null : (Map) list.get(0);
    }

    public Map<String, Object> getSqlRef(ColumnDescribe column, String val, Map<String, Object> map) {
        Map<String, Object> realParam = convertDynamicMap(column.getRef_table_sql(), map);
        TableDescribe table = getTableDesc(column.getRef_table_name());
        realParam.put("TABLE_KEY", table);
        realParam.put("term", val);
        return this.systemDao.selectRecordFromCustomSql(realParam);
    }

    public List<Map<String, Object>> dynamicQueryList(String sql, Map<String, Object> map) {
        if (map == null)
            map = new HashMap<String, Object>();
        boolean withRef = WITH_REF_PATTERN.matcher(sql).find();
        List<Map<String, Object>> ret = this.systemDao.selectDynamicQueryList(convertDynamicMap(sql, map));
        return withRef ? makeRefData(ret) : ret;
    }

    public Map<String, Object> dynamicQuery(String sql, Map<String, Object> param) {
        List<Map<String, Object>> list = dynamicQueryList(sql, param);
        return (list.size() == 0) ? null : (Map) list.get(0);
    }

    public String getLabel(ColumnDescribe column) {
        return i18nString(this.cache.getColumnI18n(CacheService.makeColumnCacheKey(column)));
    }

    public String getLabel(OperationDescribe action) {
        return i18nString(this.cache.getActionI18n(action.getId()));
    }

    public String getLabel(TableShortcutDescribe shortcut) {
        return i18nString(shortcut.getI18n());
    }

    public String getLabel(FieldModelBase field) {
        return getLabel(getColumnDesc(field));
    }

    public String getLabel(GridModel grid) {
        return i18nString(this.cache.getTableI18n(grid.getTable()));
    }

    public String getLabel(TableDescribe table) {
        return i18nString(this.cache.getTableI18n(table.getId()));
    }

    public String getLabel(AuthControlDescribe control) {
        return i18nString(this.cache.getAuthGroupI18n(control.getId()));
    }

    public String getTableLabel(String tableName) {
        return i18nString(this.cache.getTableI18n(tableName));
    }

    public String getColumnLabel(String tableName, String columnLabel) {
        return i18nString(this.cache.getColumnI18n(CacheService.makeI18nCacheKey(tableName, columnLabel)));
    }

    public String getReportLabel(String id) {
        return i18nString(this.cache.getReportI18n(id));
    }

    public TableDescribe getTableDesc(String id) {
        return this.cache.getTableDesc(id);
    }

    public Map<String, TableDescribe> getTablesDesc() {
        return this.cache.getTablesDesc();
    }

    public String getMenuLabel(String id) {
        return i18nString(this.cache.getMenuI18n(id));
    }

    public String getBatchLabel(String id) {
        return i18nString(this.cache.getBatchI18n(id));
    }

    public String getOperationLabel(String id) {
        return getLabel((OperationDescribe) this.cache.getOperations().get(id));
    }

    public String getTableShortcutLabel(String id) {
        return getLabel((TableShortcutDescribe) this.cache.getTableShortcuts().get(id));
    }

    public void prepareUpdate(TableDescribe table, Map<String, Object> data) {
        if (table.hasColumn("upd_date")) {
            Object orig = data.put("upd_date", Common.now());
            if (orig != null)
                data.put("upd_date_orig", orig);
        }
        if (table.hasColumn("upd_user")) {
            data.put("upd_user", AuthService.getCurrentUserId());
        }
    }

    public int doUpdateRecord(TableDescribe table, Map<String, Object> data, Set<String> updateColumns,
            Set<String> keyColumns, String where) {
        List<ColumnValue> keys = new ArrayList<ColumnValue>();
        if (keyColumns == null) {
            for (String column : table.getIdColumns())
                keys.add(new ColumnValue(column, data.get(column)));
        } else {
            for (String column : keyColumns) {
                keys.add(new ColumnValue(column, data.get(column)));
            }
        }
        List<ColumnValue> values = new ArrayList<ColumnValue>();
        if (updateColumns == null) {
            updateColumns = new HashSet<String>();
            for (ColumnDescribe column : table.getColumns()) {
                String name = column.getColumn_name();
                if (column.isVirtual() || (column.getData_type() == 6 && Common.isBlank((String) data.get(name)))
                        || "upd_date".equals(name) || "upd_user".equals(name) || "cre_date".equals(name)
                        || "cre_user".equals(name))
                    continue;
                updateColumns.add(name);
            }
        } else {
            Iterator<String> i = updateColumns.iterator();
            while (i.hasNext()) {
                String columnName = (String) i.next();
                ColumnDescribe column = table.getColumn(columnName);
                if (column.isVirtual() || (column.getData_type() == 6 && Common.isBlank((String) data.get(columnName)))
                        || "upd_date".equals(columnName) || "upd_user".equals(columnName)
                        || "cre_date".equals(columnName) || "cre_user".equals(columnName)) {
                    i.remove();
                }
            }
        }
        for (String column : updateColumns) {
            values.add(new ColumnValue(column, data.get(column), table.getColumn(column).getIs_encrypt(),
                    table.getColumn(column).getData_type()));
        }
        if (table.hasColumn("upd_date"))
            values.add(new ColumnValue("upd_date", data.get("upd_date")));
        if (table.hasColumn("upd_user")) {
            values.add(new ColumnValue("upd_user", data.get("upd_user")));
        }
        String encrypt_str = this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey());
        if (table.getLogable()) {
            String uid = AuthService.getCurrentUserId();
            Map<String, Object> oldData = this.systemDao.selectOldRecord(table, keys, where);
            Timestamp ts = log(table, LogType.update_old, oldData, uid);
            int ret = this.systemDao.updateRecord(table.getId(), values, keys, where, encrypt_str);
            log(table, LogType.update_new, cvToMap(values), uid, ts);
            return ret;
        }
        return this.systemDao.updateRecord(table.getId(), values, keys, where, encrypt_str);
    }

    public Map<String, Object> makeDataToDb(TableDescribe table, Map<String, Object> data, Set<String> updateColumns,
            Set<String> keyColumns, String where) {
        Map<String, Object> updateParam = new HashMap<String, Object>();
        List<ColumnValue> keys = new ArrayList<ColumnValue>();
        if (keyColumns == null) {
            for (String column : table.getIdColumns())
                keys.add(new ColumnValue(column, data.get(column)));
        } else {
            for (String column : keyColumns) {
                keys.add(new ColumnValue(column, data.get(column)));
            }
        }
        List<ColumnValue> values = new ArrayList<ColumnValue>();
        if (updateColumns == null) {
            updateColumns = new HashSet<String>();
            for (ColumnDescribe column : table.getColumns()) {
                String name = column.getColumn_name();
                if (column.isVirtual() || (column.getData_type() == 6 && Common.isBlank((String) data.get(name)))
                        || "upd_date".equals(name) || "upd_user".equals(name))
                    continue;
                updateColumns.add(name);
            }
        } else {
            Iterator<String> i = updateColumns.iterator();
            while (i.hasNext()) {
                String columnName = (String) i.next();
                ColumnDescribe column = table.getColumn(columnName);
                if (column.isVirtual() || (column.getData_type() == 6 && Common.isBlank((String) data.get(columnName)))
                        || "upd_date".equals(columnName) || "upd_user".equals(columnName)) {
                    i.remove();
                }
            }
        }
        for (String column : updateColumns)
            values.add(new ColumnValue(column, data.get(column)));
        if (table.hasColumn("upd_date"))
            values.add(new ColumnValue("upd_date", data.get("upd_date")));
        if (table.hasColumn("upd_user")) {
            values.add(new ColumnValue("upd_user", data.get("upd_user")));
        }
        Object orig = data.get("upd_date_orig");
        if (orig != null)
            keys.add(new ColumnValue("upd_date", orig));
        updateParam.put("keys", keys);
        updateParam.put("values", values);
        updateParam.put("where", where);
        updateParam.put("table", table.getId());
        return updateParam;
    }

    public int updateRecord(String tableName, Map<String, Object> data, Set<String> updateColumns,
            Set<String> keyColumns, String where) {
        TableDescribe table = getTableDesc(tableName);
        prepareUpdate(table, data);
        return doUpdateRecord(table, data, updateColumns, keyColumns, where);
    }

    public int updateRecord(String tableName, Map<String, Object> data, Set<String> updateColumns) {
        return updateRecord(tableName, data, updateColumns, null, null);
    }

    public int updateRecord(String tableName, Map<String, Object> data) {
        return updateRecord(tableName, data, null);
    }

    public boolean prepareAutoKey(TableDescribe table, Map<String, Object> data) {
        String[] keys = table.getIdColumns();
        String lastKey = keys[keys.length - 1];
        ColumnDescribe lastKeyDesc = table.getColumn(lastKey);

        if (lastKeyDesc.getData_type() == 13) {
            if (Common.isBlank((String) data.get(lastKey)))
                data.put(lastKey, this.autoKeyService.update(table, data));
            return true;
        }
        return false;
    }

    public int selectReferencedCount(Object val, ColumnDescribe column) {
        List<ColumnValue> values = new ArrayList<ColumnValue>();
        values.add(new ColumnValue(column.getColumn_name(), val));
        return this.systemDao.selectRecordCountByValues(column.getTable_id(), values);
    }

    public void checkReferenced(Object val, List<ColumnDescribe> columns) {
        for (ColumnDescribe column : columns) {
            if (selectReferencedCount(val, column) > 0) {
                String columnLabel = getColumnLabel(column.getTable_id(), column.getColumn_name());
                throw new ApplicationException(getMessageText("record be referenced",
                        new Object[] { getTableLabel(column.getTable_id()), columnLabel }));
            }
        }
    }

    public void prepareInsert(TableDescribe table, Map<String, Object> data) {
        String[] keys = table.getIdColumns();

        if (!prepareAutoKey(table, data)) {

            List<ColumnValue> values = new ArrayList<ColumnValue>(keys.length);
            for (String key : keys) {
                values.add(new ColumnValue(key, data.get(key)));
            }
            if (this.systemDao.selectRecordCountByValues(table.getId(), values) > 0)
                throw new ApplicationException(
                        getMessageText("repeat key", new Object[] { getLabel(table), table.getId() }));
        }
        Timestamp now = Common.now();
        String user = AuthService.getCurrentUserId();

        if (table.hasColumn("cre_date"))
            data.put("cre_date", now);
        if (table.hasColumn("cre_user"))
            data.put("cre_user", user);
        if (table.hasColumn("upd_date"))
            data.put("upd_date", now);
        if (table.hasColumn("upd_user"))
            data.put("upd_user", user);
        if (table.hasColumn("owner") && (data.get("owner") == null || "".equals(data.get("owner").toString()))) {
            data.put("owner", user);
        }
    }

    public int doInsertRecord(TableDescribe table, Map<String, Object> data) {
        List<ColumnDescribe> columns = table.getColumns();
        List<ColumnValue> values = new ArrayList<ColumnValue>();
        for (ColumnDescribe column : columns) {
            if (column.isVirtual())
                continue;
            String name = column.getColumn_name();
            values.add(new ColumnValue(name, data.get(name), column.getIs_encrypt(), column.getData_type()));
        }
        if (table.getLogable())
            log(table, LogType.insert, cvToMap(values), AuthService.getCurrentUserId());
        String encrypt_str = this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey());
        return this.systemDao.insertRecord(table.getId(), values, encrypt_str);
    }

    public Map<String, Object> makeInsertRecordToDb(TableDescribe table, Map<String, Object> data) {
        List<ColumnDescribe> columns = table.getColumns();
        List<ColumnValue> values = new ArrayList<ColumnValue>();
        for (ColumnDescribe column : columns) {
            if (column.isVirtual())
                continue;
            String name = column.getColumn_name();
            values.add(new ColumnValue(name, data.get(name)));
        }
        if (table.getLogable())
            log(table, LogType.insert, cvToMap(values), AuthService.getCurrentUserId());
        Map<String, Object> insertParam = new HashMap<String, Object>();
        insertParam.put("values", values);
        insertParam.put("tableName", table.getId());
        return insertParam;
    }

    public int insertRecord(String tableName, Map<String, Object> data) {
        TableDescribe table = getTableDesc(tableName);

        prepareInsert(table, data);
        return doInsertRecord(table, data);
    }

    public int deleteRecord(String tableName, List<ColumnValue> key) {
        List<Map<String, Object>> list = selectRecordsByValues(tableName, null, key, null);
        if (list.size() == 0)
            return 0;
        return deleteRecord(tableName, (Map) list.get(0), key);
    }

    public int deleteRecord(String tableName, Map<String, Object> data) {
        return deleteRecord(tableName, data, null);
    }

    private int deleteRecord(String tableName, Map<String, Object> data, List<ColumnValue> key) {
        TableDescribe table = getTableDesc(tableName);
        String[] idColumns = table.getIdColumns();

        if (idColumns.length == 1 && table.getLinkedColumns() != null) {
            Object val = data.get(idColumns[0]);
            checkReferenced(val, table.getLinkedColumns());
        }

        if (key == null) {
            key = new ArrayList<ColumnValue>(idColumns.length);
            for (String id : idColumns)
                key.add(new ColumnValue(id, data.get(id)));
        }
        if (table.getLogable()) {

            List<ColumnValue> logKey = new ArrayList<ColumnValue>(idColumns.length);
            for (String id : idColumns) {
                logKey.add(new ColumnValue(id, data.get(id)));
            }
            Map<String, Object> map = this.systemDao.selectOldRecord(table, logKey, null);
            log(table, LogType.delete, map, AuthService.getCurrentUserId());
        }
        return this.systemDao.deleteRecords(tableName, key);
    }

    public Map<String, Object> initCache() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("dict", this.cache.getDictCache());
        map.put("dictNameI18NCache", this.cache.getDictNameI18NCache());
        map.put("table", this.cache.getTableDescCache());
        map.put("dataType", dataType);
        map.put("triggers", this.cache.getTriggers());
        map.put("message", this.cache.getMsgI18nCache());
        map.put("urlInterface", this.cache.getUrlInterface());
        map.put("report", this.cache.getReportI18nCache());
        map.put("pages", this.cache.getPages());
        map.put("operations", this.cache.getOperations());
        map.put("batches", this.cache.getBatches());
        map.put("shortcuts", this.cache.getTableShortcuts());
        map.put("complexColumns", this.cache.getComplexColumns());
        map.put("sysGroupName", "_SYS_COLUMN");
        map.put("sys", this.cache.getSystemParam());
        map.put("biz", this.cache.getBizParam());
        map.put("tableViewStyle", this.cache.getTableViewStyle());
        map.put("domainLanguage", this.cache.getDomainLanguage());
        map.put("flowBlocks", this.cache.getFlowBlock());
        map.put("sqlMap", this.cache.getSqlMaps());
        return map;
    }

    public Map<String, List<UserColumn>> getUserColumn(AuthDetails user) {
        List<UserColumn> userColumns = this.userColumnDao.selectUserColumnByUser(user.getId());
        return makeUserColumnMap(userColumns);
    }

    private Map<String, List<UserColumn>> makeUserColumnMap(List<UserColumn> userColumns) {
        Map<String, List<UserColumn>> map = new HashMap<String, List<UserColumn>>();
        for (UserColumn userColumn : userColumns) {
            if (userColumn.getTable_name() == null) {
                break;
            }
            TableDescribe tableDesc = this.cache.getTableDesc(userColumn.getTable_name());
            if (tableDesc == null) {
                continue;
            }

            ColumnDescribe columnDescribe = tableDesc.getColumn(userColumn.getColumn_name());
            userColumn.setColumnDescribe(columnDescribe);
            if (map.get(userColumn.getTable_name()) == null) {
                List<UserColumn> column = new ArrayList<UserColumn>();
                column.add(userColumn);
                map.put(userColumn.getTable_name(), column);
                continue;
            }
            ((List) map.get(userColumn.getTable_name())).add(userColumn);
        }

        return map;
    }

    public List<FieldModelBase> buildEmptyDataRecord(String tableName, Map<String, Object> parentKey) {
        TableDescribe table = getTableDesc(tableName);
        return buildModel(table, null);
    }

    public String i18nText(I18nDescribe i18n) {
        String text;
        if (i18n == null)
            return "";
        AuthDetails detail = AuthService.getCurrentUser();
        String id = (detail == null) ? getSystemParam().getDefault_language() : detail.getLanguage_id();
        if (id == null || "".equals(id)) {
            String cn = i18n.getCn();
            if (cn == null) {
                return i18n.getInternational_id();
            }
            return cn;
        }

        switch (id) {
        case "cn":
            text = i18n.getCn();
            if (text == null) {
                return i18n.getInternational_id();
            }
            return text;
        case "en":
            text = i18n.getEn();
            if (text == null) {
                return i18n.getInternational_id();
            }
            return text;
        case "jp":
            text = i18n.getJp();
            if (text == null) {
                return i18n.getInternational_id();
            }
            return text;
        case "other1":
            text = i18n.getOther1();
            if (text == null) {
                return i18n.getInternational_id();
            }
            return text;
        case "other2":
            text = i18n.getOther2();
            if (text == null) {
                return i18n.getInternational_id();
            }
            return text;
        }
        return i18n.getInternational_id();
    }

    public String getCurrentLanguage() {
        AuthDetails detail = AuthService.getCurrentUser();
        return (detail == null || detail.getLanguage_id() == null || "".equals(detail.getLanguage_id()))
                ? getSystemParam().getDefault_language()
                : detail.getLanguage_id();
    }

    public String i18nString(I18nDescribe desc) {
        try {
            if (desc == null && this.logger.isWarnEnabled()) {
                return null;
            }
            AuthDetails detail = AuthService.getCurrentUser();
            String id = (detail == null) ? getSystemParam().getDefault_language() : detail.getLanguage_id();
            BeanWrapperImpl beanWrapperImpl = new BeanWrapperImpl(desc);
            if (beanWrapperImpl.isReadableProperty(id)) {
                String ret = (String) beanWrapperImpl.getPropertyValue(id);
                if (Common.isBlank(ret))
                    ret = (String) beanWrapperImpl.getPropertyValue(this.cache.getSystemParam().getDefault_language());
                return ret;
            }

            if (Common.isBlank(desc.getKey2())) {
                return desc.getKey1();
            }
            return String.format("%s.%s", new Object[] { desc.getKey1(), desc.getKey2() });
        } catch (Exception e) {

            return "";
        }
    }

    public Map<String, I18nDescribe> getReports() {
        return this.cache.getReportI18nCache();
    }

    public String getMessageText(String msg, Object... param) {
        String m = i18nString(this.cache.getMsgI18n(msg.toLowerCase()));
        if (m == null) {
            m = i18nString(this.cache.getMsgI18n(msg));
        }
        if (m == null) {
            m = msg;
        }
        m = m.replaceAll("\\$(\\{[^\\}]*\\})", "$1");
        return MessageFormat.format(m, param);
    }

    public String getTabText(String table, String tab) {
        return i18nString(this.cache.getTableTabI18n(CacheService.makeI18nCacheKey(table, tab)));
    }

    public String getGroupText(String table, String group) {
        if ("_SYS_COLUMN".equals(group))
            return "";
        return i18nString(this.cache.getGroupI18n(CacheService.makeI18nCacheKey(table, group)));
    }

    private Map<String, Object> convertDynamicMap(String sql, Map<String, Object> param) {
        Map<String, Object> realParam = new HashMap<String, Object>(param.size());
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            String key = (String) entry.getKey();
            if (key.contains(".")) {
                String realKey = key.replace('.', '_');
                realParam.put(realKey, StringEscapeUtils.escapeSql(entry.getValue().toString()));
                sql = sql.replace(key, realKey);
                continue;
            }
            realParam.put(key,
                    StringEscapeUtils.escapeSql((entry.getValue() == null) ? null : entry.getValue().toString()));
        }

        realParam.put("SQL_KEY", sql);
        return realParam;
    }

    public List<Map<String, Object>> filterCustomDataRecords(ColumnDescribe column, Map<String, Object> param,
            String term) {
        Map<String, Object> realParam = convertDynamicMap(column.getRef_table_sql(), param);
        realParam.put("TABLE_KEY", column.getRef_table_name());

        List<AuthGroup> optionAuthGroup = this.authService.getOptionAuthGroup(column.getRef_table_name(), "read");
        String authCondition = buildAuthCondition(column.getRef_table_name(), optionAuthGroup, 0, null);
        realParam.put("where", authCondition);
        realParam.put("term", term);
        realParam.put("tops", Integer.valueOf(getSystemParam().getInput_selector_lines()));
        return this.systemDao.selectListFromCustomSql(realParam);
    }

    public List<String> getChildrenTable(String tableName) {
        return this.systemDao.selectChildrenTable(tableName);
    }

    public SimpleDateFormat getDateFormat(AuthDetails user, DateFormatType type) {
        return (SimpleDateFormat) ((Map) dateFormats.get(user.getLanguage_id())).get(type);
    }

    public int updatePassword(String uid, String password) {
        return this.systemDao.updatePassword(uid, password,
                this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey()));
    }

    public String insertImportData(String table, List<Map<String, Object>> data, String uid,
            HttpServletRequest httpRequest) {
        String template = table + "_template";
        String target = table + "_" + uid;
        try {
            this.systemDao.dropTable(target);
        } catch (Exception ignored) {
        }

        this.systemDao.copyTableUsingTemplate(template, target);
        for (Map<String, Object> map : data) {
            this.systemDao.insertImportData(target, map);
        }
        String logId = (String) httpRequest.getAttribute("logId");
        String tableId = (String) httpRequest.getAttribute("tableId");
        this.logService.updateLogNormal(logId, tableId, "");
        return target;
    }

    public FlowEvent getFlowEvent(String flow_event_id) {
        return this.cacheService.getFlowEvent(flow_event_id);
    }

    public List<DatabaseDataMap> selectImportSqlData(TableDescribe table, List<Map<String, Object>> data, String uid) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        insertImportData(table.getId(), data, uid, httpRequest);
        return this.systemDao.selectImportData(table, uid, makeTableRefModel(table));
    }

    public List<DatabaseDataMap> selectAllData(String tableName) {
        return selectAllData(getTableDesc(tableName), tableName);
    }

    public List<DatabaseDataMap> selectAllData(TableDescribe table, String target) {
        return this.systemDao.selectAllData(target, makeTableRefModel(table));
    }

    public List<RecordModel> dumpFailedImportData(TableDescribe table, List<DatabaseDataMap> data) {
        List<RecordModel> records = new ArrayList<RecordModel>();
        int index = 1;
        for (Map<String, Object> map : data) {
            Object err_code = map.get("err_code");
            if (err_code == null) {
                index++;
                continue;
            }
            int errCode = ((Integer) err_code).intValue();
            if (errCode == -1) {
                index++;
                continue;
            }
            records.add(new ImportRecordModel(buildModel(table, map, false), errCode, (String) map.get("err_params"),
                    index));
            index++;
        }
        return records;
    }

    public String makeDateConvertSql(String date) {
        return "convert(datetime,'" + date + "')";
    }

    private Timestamp log(TableDescribe table, LogType type, Map<String, Object> map, String uid) {
        Timestamp ts = Common.now();
        log(table, type, map, uid, ts);
        return ts;
    }

    private void log(TableDescribe table, LogType type, Map<String, Object> map, String uid, Timestamp ts) {
        String logId = this.logService.getMaxId();
        this.systemDao.insertLog(logId, ts, uid, type, table.getId(), Common.toJson(map));
    }

    public String getRootPath() {
        return this.rootPath;
    }

    public void setRootPath(String rootPath) {
        this.rootPath = rootPath;
    }

    public List<DatabaseDataMap> fetchAllDataRecords(String tableName, TableDescribe table, String condition) {
        List<ColumnDescribe> columns = table.getColumns();

        Set<String> columnList = new HashSet<String>(columns.size());
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual()) {
                columnList.add(column.getColumn_name());
            }
        }
        if (condition == null) {
            condition = "1=1";
        }

        return this.systemDao.selectAllDataWithCondition(tableName, columnList, condition);
    }

    public boolean selectIsAddApprove(String approve_id, String block_id) {
        Integer sequence = this.approveDao.getSequenceByApproveId(approve_id);
        int is_add_approve = this.approveDao.selectIsAddApprove(approve_id, block_id, sequence.intValue(),
                AuthService.getCurrentUserId());
        boolean isAddApprove = false;
        if (is_add_approve <= 0) {
            isAddApprove = true;
        }
        return isAddApprove;
    }

    private String buildNameExpression(String tableName, TableDescribe table, List<ReferenceModel> refs) {
        String nameColumn = table.getName_column();
        String nameExpression = table.getName_expression_publicity();
        if (nameExpression == null || "".equals(nameExpression)) {
            if (nameColumn == null || "".equals(nameColumn))
                return null;
            return table.getId() + "." + nameColumn;
        }
        String regex = "\\$\\{(.*?)\\}|\\#\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nameExpression);
        while (matcher.find()) {
            String param = matcher.group();
            if (param != null) {
                String name = param.substring(2, param.length() - 1);
                String[] split = name.split("\\.");
                if (split.length == 1) {
                    nameExpression = nameExpression.replace(param, "'," + table.getId() + "." + split[0] + ",'");
                    continue;
                }
                if (split.length == 2 && refs != null) {
                    for (int i = 0; i < refs.size(); i++) {
                        if (split[0].equals(((ReferenceModel) refs.get(i)).getColumn().getColumn_name())) {
                            nameExpression = nameExpression.replace(param,
                                    "'," + ((ReferenceModel) refs.get(i)).getAlias() + "." + split[1] + ",'");
                        }
                    }
                }
            }
        }
        return "concat('" + nameExpression + "')";
    }

    public Object buildNameExpression(TableDescribe table, Map<String, Object> tableData) {
        String nameColumn = table.getName_column();
        String nameExpression = table.getName_expression_publicity();
        if (nameExpression == null || "".equals(nameExpression)) {
            if (nameColumn == null || "".equals(nameColumn)) {
                if (table.getIdColumns() == null || table.getIdColumns().length == 0) {
                    return "";
                }
                return tableData.get(table.getIdColumns()[0]);
            }

            return tableData.get(nameColumn);
        }

        String regex = "\\$\\{(.*?)\\}|\\#\\{(.*?)\\}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(nameExpression);
        while (matcher.find()) {
            String param = matcher.group();
            if (param != null) {
                String name = param.substring(2, param.length() - 1);
                String[] split = name.split("\\.");
                if (split.length == 1) {
                    nameExpression = nameExpression.replace(param,
                            (tableData.get(name) == null) ? "" : tableData.get(name).toString());
                    continue;
                }
                if (split.length == 2)
                    ;
            }
        }

        return nameExpression;
    }

    public List<TreeNode> buildUserTree() {
        List<AuthDetails> getAllUser = this.approveDao.selectAllUser();
        Map<String, List<AuthDetails>> userMap = new HashMap<String, List<AuthDetails>>();
        for (int i = 0; i < getAllUser.size(); i++) {
            if (userMap.get(((AuthDetails) getAllUser.get(i)).getDepartment_id()) == null) {
                userMap.put(((AuthDetails) getAllUser.get(i)).getDepartment_id(), new ArrayList<>());
            }
            ((List) userMap.get(((AuthDetails) getAllUser.get(i)).getDepartment_id())).add(getAllUser.get(i));
        }
        List<DepartmentDescribe> getAllDept = this.approveDao.selectAllDept();
        List<TreeNode> treeData = new ArrayList<TreeNode>();
        for (DepartmentDescribe dept : getAllDept) {
            TreeNode node = new TreeNode();
            if (dept.getParent_id() == null || "".equals(dept.getParent_id())) {
                node.setParent("#");
            } else {
                node.setParent(dept.getParent_id() + "_dept");
            }
            List<AuthDetails> deptUser = (List) userMap.get(dept.getId());
            if (deptUser != null) {
                for (int k = 0; k < deptUser.size(); k++) {
                    TreeNode nodeUser = new TreeNode();
                    nodeUser.setParent(((AuthDetails) deptUser.get(k)).getDepartment_id() + "_dept");
                    nodeUser.setId(((AuthDetails) deptUser.get(k)).getId());
                    nodeUser.setText(((AuthDetails) deptUser.get(k)).getName());
                    nodeUser.setData(deptUser.get(k));
                    treeData.add(nodeUser);
                }
            }
            node.setId(dept.getId() + "_dept");
            node.setText(dept.getName());
            treeData.add(node);
        }
        return treeData;
    }

    public AuthDetails loadUserById(String id) {
        String domain = this.cacheService.getDecryptKey();
        return this.systemDao.getUserById(id, domain);
    }

    public String getEncryption(String encryptionStr) {
        String strDES = "";
        try {
            strDES = this.des.encrypt(encryptionStr);
        } catch (Exception e) {
            return "encrypt lose";
        }
        return strDES;
    }

    public String getDecode(String decodeStr) {
        String decryptStr = "";
        try {
            decryptStr = this.des.decrypt(decodeStr);
        } catch (Exception e) {
            return decodeStr;
        }

        return decryptStr;
    }

    public String setCodeStr(String str) {
        String uuid = ShortUUID.uuid();
        CodeStrDescribe codeStr = new CodeStrDescribe();
        codeStr.setUUID(uuid);
        codeStr.setStr(str);
        codeStr.setType("year");
        codeStr.setValid_time(1);
        codeStr.setCre_date(new Date());
        this.systemDao.setCodeStr(codeStr);
        return uuid.toString();
    }

    public String getCodeStr(String UUID) {
        CodeStrDescribe codeStr = this.systemDao.getCodeStr(UUID);
        if (null == codeStr)
            return null;
        if (!codeIsFailed(codeStr)) {
            return null;
        }
        return codeStr.getStr();
    }

    private boolean codeIsFailed(CodeStrDescribe codeStr) {
        Date now = new Date();
        Calendar rightNow = Calendar.getInstance();
        switch (codeStr.getType()) {
        case "day":
            if (codeStr.getCre_date().getTime() > now.getTime() - (codeStr.getValid_time() * 24 * 60 * 60 * 1000)) {
                return true;
            }

            return false;
        case "week":
            rightNow.add(3, codeStr.getValid_time());
            if (now.getTime() < rightNow.getTimeInMillis())
                return true;
            return false;
        case "month":
            rightNow.add(2, codeStr.getValid_time());
            if (now.getTime() < rightNow.getTimeInMillis())
                return true;
            return false;
        case "year":
            rightNow.add(1, codeStr.getValid_time());
            if (now.getTime() < rightNow.getTimeInMillis())
                return true;
            return false;
        case "hour":
            if (codeStr.getCre_date().getTime() > now.getTime() - (codeStr.getValid_time() * 60 * 60 * 1000))
                return true;
            return false;
        case "min":
            if (codeStr.getCre_date().getTime() > now.getTime() - (codeStr.getValid_time() * 60 * 1000))
                return true;
            return false;
        }
        return true;
    }

    public String getProjectUrl() {
        return this.projectUrl;
    }

    public void setProjectUrl(HttpServletRequest req) {
        String url = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        this.projectUrl = url;
    }

    public I18nDescribe getMsgI18N(String key) {
        return this.cacheService.getMsgI18n(key);
    }

    public boolean sqlParamCheck(String str) {
        if (str == null)
            return true;
        String reg = "(?:')|(?:--)|(/\\*(?:.|[\\n\\r])*?\\*/)|(\\b(select|update|union|and|or|delete|insert|trancate|char|into|substr|ascii|declare|exec|count|master|into|drop|execute)\\b)";

        Pattern sqlPattern = Pattern.compile(reg, 2);
        if (sqlPattern.matcher(str).find()) {
            return false;
        }
        return true;
    }

    public Properties getSysProper() {
        InputStream inStream = cn.com.easyerp.weixin.ServiceNumberImpl.class.getClassLoader()
                .getResourceAsStream("param.properties");
        Properties prop = new Properties();
        try {
            prop.load(inStream);
            return prop;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkPassword(String password, int level) {
        List<String> charLevel = new ArrayList<String>();
        for (int i = 0; i < password.length(); i++) {
            String num = numType(password.charAt(i));
            if (!charLevel.contains(num)) {
                charLevel.add(num);
            }
        }
        if (level == 1) {
            if (charLevel.size() >= 2 && password.length() >= 6) {
                return true;
            }
        } else if (level == 2) {
            if (charLevel.size() >= 3 && password.length() >= 6) {
                return true;
            }
        } else if (level == 3 && charLevel.size() >= 4 && password.length() >= 8) {
            return true;
        }

        return false;
    }

    private String numType(int num) {
        if (num >= 48 && num <= 57)
            return "num";
        if (num >= 97 && num <= 122)
            return "lchar";
        if (num >= 65 && num <= 90) {
            return "hchar";
        }
        return "tchar";
    }

    public Map<String, Object> checkFormulaData(String tableName) {
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        List<ColumnDescribe> columns = table.getColumns();
        String encrypt_str = this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey());
        DataTablePagingParameterModel paging = new DataTablePagingParameterModel();
        paging.setStart(0);
        paging.setLength(1);
        List<ReferenceModel> refs = makeRefModels(table);
        List<Map<String, Object>> data = this.systemDao.selectDataWithCondition(tableName, columns, null, null, null,
                refs, null, true, paging, encrypt_str, false);
        if (data == null)
            return null;
        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
        records.add(data.get(0));
        List<Map<String, Object>> maps = makeRefDataForJava(records);
        return (Map) maps.get(0);
    }
}
