package cn.com.easyerp.core.evaluate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.SqlMap;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.evaluate.DataModel.Model;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;

@Service
public class FormulaService {
    public static final String IS_SQL = "dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)";
    private static final String PARAM = "[\\$#]\\{(.*?)\\}";
    private static final String FUNCTION = "dxf.([a-zA-Z]*)\\(\\\\?['\"]?(.*?)\\\\?['\"]?\\)";
    private static final String IS_JSON = "^\\((.*)\\)$";
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = this.manager.getEngineByName("javascript");
    @Autowired
    private CacheService cacheService;
    @Autowired
    private CacheModelService cacheModelService;
    @Autowired
    private Function function;
    @Autowired
    private SystemDao systemDao;

    /*
     * public FormulaService() { this.manager = new ScriptEngineManager();
     * this.engine = this.manager.getEngineByName("javascript"); }
     */

    public Object evaluate(String formula, final Object param) {
        if (formula == null || "".equals(formula)) {
            return null;
        }
        final Pattern patternSql = Pattern.compile("dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)");
        final Matcher matcherSql = patternSql.matcher(formula);
        while (matcherSql.find()) {
            formula = this.replaceSql(formula);
        }
        final String regex = PARAM;
        final Pattern pattern = Pattern.compile(regex);
        final Matcher matcher = pattern.matcher(formula);
        Object value = null;
        while (matcher.find()) {
            final String matcherV = matcher.group();
            final String formulaV = matcherV.substring(2, matcherV.length() - 1);
            value = this.evaluateVar(formulaV, param);
            if (value != null) {
                if (matcherV.indexOf("#") != -1) {
                    formula = formula.replace(matcherV, "'" + value.toString() + "'");
                } else {
                    formula = formula.replace(matcherV, value.toString());
                }
            } else if (matcherV.indexOf("#") != -1) {
                formula = formula.replace(matcherV, "''");
            } else {
                formula = formula.replace(matcherV, "");
            }
        }
        formula = this.matcherFunction(formula, param, true);
        try {
            value = this.engine.eval(formula);
        } catch (Exception e) {
            throw new ApplicationException("variable:" + formula + " not exists");
        }
        return value;
    }

    public Object evaluate(final String id, final String formula) {
        final Model cacheModel = this.cacheModelService.getCacheModel(id);
        final String target = this.evaluateVar(formula, cacheModel);
        Object val;
        try {
            val = this.engine.eval(target);
        } catch (Exception e) {
            return target;
        }
        return val;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object evaluate(final String id, final String dataId, final String column, final String formula,
            final Map<String, Object> data) {
        Model cacheModel = null;
        if (dataId == null || "".equals(dataId)) {
            cacheModel = this.cacheModelService.getCacheModel(id);
        } else {
            cacheModel = this.cacheModelService.getCacheModel(id, dataId);
        }
        String target = "";
        if (data != null) {
            target = this.evaluateVar(formula, data);
        } else {
            target = this.evaluateVar(formula, cacheModel);
        }
        if (target.matches(IS_JSON)) {
            final Pattern p = Pattern.compile(IS_JSON);
            final Matcher m = p.matcher(target);
            m.find();
            return Common.fromJson(m.group(1), (TypeReference) new TypeReference<Object>() {
            });
        }
        Object val;
        try {
            val = this.engine.eval(target);
        } catch (Exception e) {
            return target;
        }
        if (cacheModel != null && cacheModel.getTableName() != null && column != null) {
            final ColumnDescribe columnDesc = this.cacheService.getTableDesc(cacheModel.getTableName())
                    .getColumn(column);
            if (formula.equals(columnDesc.getFormula()) || formula.equals(columnDesc.getDefault_value())) {
                this.cacheModelService.changeModify(cacheModel, column, val);
            }
        }
        return val;
    }

    private String evaluateVar(String str, final Model cacheModel) {
        if (str == null || "".equals(str)) {
            return null;
        }
        str = str.trim();
        if ("'dx.user.id'".equals(str)) {
            str = str.substring(1, str.length() - 1);
            str = str.trim();
            return "\"" + this.findVal("__" + str, null).toString() + "\"";
        }
        final Pattern patternSql = Pattern.compile("dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)");
        final Matcher matcherSql = patternSql.matcher(str);
        while (matcherSql.find()) {
            str = this.replaceSql(str);
        }
        final Pattern pattern = Pattern.compile(PARAM);
        final Matcher matcher = pattern.matcher(str);
        Object value = null;
        while (matcher.find()) {
            final String matcherV = matcher.group();
            value = this.findVal(matcher.group(1), cacheModel);
            if (value != null) {
                if (matcherV.indexOf("$") != -1) {
                    str = str.replace(matcherV, value.toString());
                } else {
                    if (matcherV.indexOf("#") == -1) {
                        continue;
                    }
                    str = str.replace(matcherV, "'" + value.toString() + "'");
                }
            } else if (matcherV.indexOf("$") != -1) {
                str = str.replace(matcherV, "");
            } else {
                if (matcherV.indexOf("#") == -1) {
                    continue;
                }
                str = str.replace(matcherV, "''");
            }
        }
        final String result = this.matcherFunction(str, cacheModel);
        return result;
    }

    private String evaluateVar(String str, final Map<String, Object> data) {
        if ("".equals(str)) {
            return null;
        }
        str = str.trim();
        if ("'dx.user.id'".equals(str)) {
            str = str.substring(1, str.length() - 1);
            str = str.trim();
            return "\"" + this.findVal("__" + str, null).toString() + "\"";
        }
        final Pattern patternSql = Pattern.compile("dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)");
        final Matcher matcherSql = patternSql.matcher(str);
        while (matcherSql.find()) {
            str = this.replaceSql(str);
        }
        final Pattern pattern = Pattern.compile(PARAM);
        final Matcher matcher = pattern.matcher(str);
        Object value = null;
        while (matcher.find()) {
            final String matcherV = matcher.group();
            value = this.findVal(matcher.group(1), data, true);
            if (value != null) {
                if (matcherV.indexOf("$") != -1) {
                    str = str.replace(matcherV, value.toString());
                } else {
                    if (matcherV.indexOf("#") == -1) {
                        continue;
                    }
                    str = str.replace(matcherV, "'" + value.toString() + "'");
                }
            } else if (matcherV.indexOf("$") != -1) {
                str = str.replace(matcherV, "");
            } else {
                if (matcherV.indexOf("#") == -1) {
                    continue;
                }
                str = str.replace(matcherV, "''");
            }
        }
        final String result = this.matcherFunction(str, null);
        return result;
    }

    @SuppressWarnings({ "rawtypes" })
    private Object evaluateVar(final String formulaV, final Object param) {
        final String reg = "__dx\\.(user|sys)\\.(.*)$";
        if (formulaV.matches(reg)) {
            final Pattern p = Pattern.compile(reg);
            final Matcher m = p.matcher(formulaV);
            m.find();
            final String group = m.group(1);
            switch (group) {
            case "user": {
                final String columnNameUser = m.group(2);
                final AuthDetails currentUser = AuthService.getCurrentUser();
                if (currentUser == null) {
                    return null;
                }
                final BeanWrapper wrapperUser = (BeanWrapper) new BeanWrapperImpl((Object) currentUser);
                if (wrapperUser.isReadableProperty(columnNameUser)) {
                    return wrapperUser.getPropertyValue(columnNameUser);
                }
                break;
            }
            case "sys": {
                final String columnNameSys = m.group(2);
                final SystemParameter systemParam = this.cacheService.getSystemParam();
                if (systemParam == null) {
                    return null;
                }
                final BeanWrapper wrapperSys = (BeanWrapper) new BeanWrapperImpl((Object) systemParam);
                if (wrapperSys.isReadableProperty(columnNameSys)) {
                    return wrapperSys.getPropertyValue(columnNameSys);
                }
                break;
            }
            }
        }
        if (param == null) {
            return null;
        }
        final BeanWrapper wrapper = (BeanWrapper) new BeanWrapperImpl(param);
        final String[] name = formulaV.split("\\.");
        Object value = new Object();
        if (name.length == 1) {
            if (param instanceof Map) {
                if (!((Map) param).containsKey(name[0])) {
                    throw new ApplicationException("has no column: " + name[0]);
                }
                return ((Map) param).get(name[0]);
            } else {
                if (wrapper.isReadableProperty(name[0])) {
                    return wrapper.getPropertyValue(name[0]);
                }
                throw new ApplicationException("has no column: " + name[0]);
            }
        } else {
            if (name.length != 2) {
                return null;
            }
            if (param instanceof Map) {
                if (!((Map) param).containsKey(name[0] + ".ref")) {
                    throw new ApplicationException("has no ref table: " + name[0]);
                }
                value = ((Map) param).get(name[0] + ".ref");
                if (value instanceof Map) {
                    if (!((Map) value).containsKey(name[1])) {
                        throw new ApplicationException("has no ref column: " + name[1]);
                    }
                    return ((Map) value).get(name[1]);
                } else {
                    final BeanWrapper wrapper2 = (BeanWrapper) new BeanWrapperImpl(value);
                    if (wrapper2.isReadableProperty(name[1])) {
                        return wrapper2.getPropertyValue(name[1]);
                    }
                    throw new ApplicationException("has no ref column: " + name[1]);
                }
            } else {
                final Object propertyValue = wrapper.getPropertyValue(name[0]);
                if (propertyValue instanceof Map) {
                    if (!((Map) propertyValue).containsKey(name[1])) {
                        throw new ApplicationException("has no ref column: " + name[1]);
                    }
                    return ((Map) propertyValue).get(name[1]);
                } else {
                    final BeanWrapper wrapper3 = (BeanWrapper) new BeanWrapperImpl(propertyValue);
                    if (wrapper3.isReadableProperty(name[1])) {
                        return wrapper.getPropertyValue(name[1]);
                    }
                    throw new ApplicationException("has no ref column: " + name[1]);
                }
            }
        }
    }

    private Object findVal(final String column, final Model cacheModel) {
        final String regUser = "__dx\\.user\\.(.*)$";
        final Pattern p = Pattern.compile(regUser);
        final Matcher m = p.matcher(column);
        if (m.find()) {
            final String columnNameUser = m.group(1);
            final AuthDetails currentUser = AuthService.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            final BeanWrapper wrapperUser = (BeanWrapper) new BeanWrapperImpl((Object) currentUser);
            if (wrapperUser.isReadableProperty(columnNameUser)) {
                return wrapperUser.getPropertyValue(columnNameUser);
            }
        }
        final String regSys = "dx\\.sys\\.(.*)$";
        final Pattern pSys = Pattern.compile(regSys);
        final Matcher mSys = pSys.matcher(column);
        while (mSys.find()) {
            final String columnNameSys = mSys.group(1);
            final SystemParameter systemParam = this.cacheService.getSystemParam();
            if (systemParam == null) {
                return null;
            }
            final BeanWrapper wrapperSys = (BeanWrapper) new BeanWrapperImpl((Object) systemParam);
            if (wrapperSys.isReadableProperty(columnNameSys)) {
                return wrapperSys.getPropertyValue(columnNameSys);
            }
        }
        final String[] name = column.split("\\.");
        if (cacheModel == null) {
            return column;
        }
        if (cacheModel.getFieldMap().get(name[0]) == null) {
            throw new ApplicationException("variable:" + column + " not exists");
        }
        if (name.length == 1) {
            return cacheModel.getFieldMap().get(name[0]).getValue();
        }
        if (name.length != 2) {
            return null;
        }
        final FieldModelBase fieldModelBase = cacheModel.getFieldMap().get(name[0]);
        if (!(fieldModelBase instanceof FieldWithRefModel)) {
            return null;
        }
        if (!"create".equals(cacheModel.getAction())) {
            return ((FieldWithRefModel) fieldModelBase).getRef().get(name[1]);
        }
        final TableDescribe table = this.cacheService.getTableDesc(fieldModelBase.getTable());
        final ColumnDescribe columnDesc = table.getColumn(fieldModelBase.getColumn());
        final Map<String, Object> refData = this.getRefData(columnDesc.getRef_table_name(), fieldModelBase.getValue());
        if (refData == null) {
            return null;
        }
        return refData.get(name[1]);
    }

    @SuppressWarnings({ "rawtypes" })
    private Object findVal(final String column, final Map<String, Object> data, final boolean withNoCache) {
        final String regUser = "__dx\\.user\\.(.*)$";
        final Pattern p = Pattern.compile(regUser);
        final Matcher m = p.matcher(column);
        if (m.find()) {
            final String columnNameUser = m.group(1);
            final AuthDetails currentUser = AuthService.getCurrentUser();
            if (currentUser == null) {
                return null;
            }
            final BeanWrapper wrapperUser = (BeanWrapper) new BeanWrapperImpl((Object) currentUser);
            if (wrapperUser.isReadableProperty(columnNameUser)) {
                return wrapperUser.getPropertyValue(columnNameUser);
            }
        }
        final String regSys = "dx\\.sys\\.(.*)$";
        final Pattern pSys = Pattern.compile(regSys);
        final Matcher mSys = pSys.matcher(column);
        while (mSys.find()) {
            final String columnNameSys = mSys.group(1);
            final SystemParameter systemParam = this.cacheService.getSystemParam();
            if (systemParam == null) {
                return null;
            }
            final BeanWrapper wrapperSys = (BeanWrapper) new BeanWrapperImpl((Object) systemParam);
            if (wrapperSys.isReadableProperty(columnNameSys)) {
                return wrapperSys.getPropertyValue(columnNameSys);
            }
        }
        final String[] name = column.split("\\.");
        if (data == null) {
            return null;
        }
        if (name.length != 1) {
            return null;
        }
        final Object columnData = data.get(name[0]);
        if (columnData == null) {
            return "";
        }
        if (columnData instanceof Map) {
            return "\"" + ((Map) columnData).get("value").toString() + "\"";
        }
        return "" + columnData.toString() + "";
    }

    public String replaceSql(String str) {
        final Pattern p = Pattern.compile("dxf.sql\\(\\\\?['\"](.*?)\\\\?['\"]\\)");
        final Matcher m = p.matcher(str);
        while (m.find()) {
            final String sqlId = m.group(1);
            final SqlMap sqlMap = this.cacheService.getSqlMap(sqlId);
            str = str.replace(sqlId, sqlMap.getSql());
        }
        return str;
    }

    private String matcherFunction(String str, final Model cacheModel) {
        final Pattern pattern = Pattern.compile(FUNCTION);
        final Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            final String funName = matcher.group(1);
            final String funStr = this.getFunStr(str, funName);
            String funVal = this.getFunVal(funStr, funName);
            final Pattern patternVal = Pattern.compile(FUNCTION);
            final Matcher matcherVal = patternVal.matcher(funVal);
            while (matcherVal.find()) {
                funVal = this.matcherFunction(funVal, cacheModel);
            }
            final String value = this.execFunction(funName, funVal, cacheModel);
            str = str.replace(funStr, (value == null) ? "" : value);
        }
        return str;
    }

    private String matcherFunction(String str, final Object param, final boolean isCheckFormula) {
        final Pattern pattern = Pattern.compile(FUNCTION);
        final Matcher matcher = pattern.matcher(str);
        while (matcher.find()) {
            final String funName = matcher.group(1);
            final String funStr = this.getFunStr(str, funName);
            String funVal = this.getFunVal(funStr, funName);
            final Pattern patternVal = Pattern.compile(FUNCTION);
            final Matcher matcherVal = patternVal.matcher(funVal);
            while (matcherVal.find()) {
                funVal = this.matcherFunction(funVal, param, true);
            }
            final String value = this.execFunction(funName, funVal, param, isCheckFormula);
            str = str.replace(funStr, (value == null) ? "" : value);
        }
        return str;
    }

    private String execFunction(final String funName, final String funVal, final Model cacheModel) {
        if (funName == null || "".equals(funName)) {
            return null;
        }
        switch (funName) {
        case "sum": {
            return this.function.sumFunction(funVal, cacheModel) + "";
        }
        case "max": {
            return this.function.maxFunction(funVal, cacheModel);
        }
        case "min": {
            return this.function.minFunction(funVal, cacheModel);
        }
        case "unique": {
            return this.function.uniqueFunction(funVal, cacheModel);
        }
        case "uniqueCount": {
            return this.function.uniqueCountFunction(funVal, cacheModel);
        }
        case "count": {
            return this.function.countFunction(funVal, cacheModel) + "";
        }
        case "sql": {
            return this.function.sqlFunction(funVal, cacheModel);
        }
        case "date": {
            return this.function.dateFunction(funVal, cacheModel);
        }
        case "dateText": {
            return this.function.dateTextFunction(funVal, cacheModel);
        }
        case "orig": {
            return this.function.origFunction(funVal, cacheModel);
        }
        case "dict": {
            return this.function.dictFunction(funVal, cacheModel);
        }
        case "domainKey": {
            return this.function.domainKeyFunction(funVal, cacheModel);
        }
        case "childVal": {
            return "";
        }
        default: {
            throw new ApplicationException("no function name: " + funName);
        }
        }
    }

    private String execFunction(final String funName, final String funVal, final Object param,
            final boolean isCheckFormula) {
        if (funName == null || "".equals(funName)) {
            return null;
        }
        switch (funName) {
        case "sum": {
            return this.function.sumFunction(funVal, null) + "";
        }
        case "max": {
            return this.function.maxFunction(funVal, null);
        }
        case "min": {
            return this.function.minFunction(funVal, null);
        }
        case "unique": {
            return this.function.uniqueFunction(funVal, null);
        }
        case "uniqueCount": {
            return this.function.uniqueCountFunction(funVal, null);
        }
        case "count": {
            return this.function.countFunction(funVal, null) + "";
        }
        case "sql": {
            return this.function.sqlFunction(funVal, null);
        }
        case "date": {
            return this.function.dateFunction(funVal, null);
        }
        case "dateText": {
            return this.function.dateTextFunction(funVal, null);
        }
        case "orig": {
            return this.function.origFunction(funVal, null);
        }
        case "dict": {
            return this.function.dictFunction(funVal, null);
        }
        case "domainKey": {
            return this.function.domainKeyFunction(funVal, null);
        }
        case "childVal": {
            return "";
        }
        default: {
            throw new ApplicationException("no function name: " + funName);
        }
        }
    }

    public String getFunStr(final String str, final String funName) {
        final String replaceStr = str;
        final int funIndex = replaceStr.indexOf("dxf." + funName);
        int count = 0;
        boolean one = true;
        for (int i = funIndex; i < replaceStr.length(); ++i) {
            // final String substring = replaceStr.substring(i, i + 1);
            if (one && "(".equals(replaceStr.substring(i, i + 1))) {
                one = false;
                ++count;
            } else {
                if (!one && "(".equals(replaceStr.substring(i, i + 1))) {
                    ++count;
                }
                if (!one && ")".equals(replaceStr.substring(i, i + 1))) {
                    --count;
                }
                if (!one && count == 0) {
                    final String funStr = replaceStr.substring(funIndex, i + 1);
                    return funStr;
                }
            }
        }
        return replaceStr;
    }

    public String getFunVal(final String funStr, final String funName) {
        final int funNameIndex = funStr.indexOf(funName);
        String funVal = funStr.substring(funNameIndex + funName.length(), funStr.length());
        funVal = funVal.trim();
        funVal = funVal.substring(1, funVal.length() - 1);
        if (funVal.indexOf("\\") == 0) {
            funVal = funVal.substring(1, funVal.length() - 1);
        }
        if (funVal.indexOf("'") == 0 || funVal.indexOf("\"") == 0) {
            funVal = funVal.substring(1, funVal.length() - 1);
        }
        return funVal;
    }

    private Map<String, Object> getRefData(final String tableName, final Object keyValue) {
        final TableDescribe refTable = this.cacheService.getTableDesc(tableName);
        if (refTable == null || refTable.getIdColumns() == null) {
            return null;
        }
        final Map<String, Object> ids = new HashMap<String, Object>();
        ids.put(refTable.getIdColumns()[0], keyValue);
        final List<Map<String, Object>> datas = this.systemDao.selectDataByKey(tableName, ids);
        if (datas == null || datas.size() != 1) {
            return null;
        }
        return datas.get(0);
    }
}
