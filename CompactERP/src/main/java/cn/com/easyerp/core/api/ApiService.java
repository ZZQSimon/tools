package cn.com.easyerp.core.api;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.framework.httpclient.HttpClientUtil;

@Service
public class ApiService {
    public static final String INSERT_SUCCESS_MSG = "Insert success";
    private static final Pattern TRIGGER_PARAM_PARSER = Pattern.compile("[#$]\\{(.*)\\}");
    public static final String INSERT_FAILED_MSG = "Insert failed";
    public static final String UPDATE_SUCCESS_MSG = "Update success";
    public static final String UPDATE_FAILED_MSG = "Update failed";
    public static final String DELETE_SUCCESS_MSG = "Delete success";
    public static final String DELETE_FAILED_MSG = "Delete failed";
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private DataService dataService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private JApiService jApiService;
    @Autowired
    private CacheService cacheService;

    public DatabaseDataMap buildParams(ApiDescribe api, ApiRequestParam param, String id) {
        if (param.filesParams == null && param.getFiles() != null) {
            param.filesParams = this.viewService.mapDataToDB(param.getFiles());
        }
        UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) this.cacheService.getUrlInterface()
                .get(api.getId());
        TableDescribe table = this.dataService.getTableDesc(urlInterfaceDescribe.getUrl());
        ViewDataMap data = (ViewDataMap) param.getData().get(id);
        DatabaseDataMap ret = this.viewService.convertToDBValues(table, data);
        if (param.filesParams != null)
            ret.putAll(param.filesParams);
        return ret;
    }

    public ApiResult exec(ApiDescribe api, DataMap data) {
        return exec(api, data, genUuid());
    }

    public ApiResult exec(ApiDescribe api, DataMap data, String uuid) {
        return callApi(api, data, uuid);
    }

    public ApiResult exec(List<ApiDescribe> apis, List<DataMap> data) {
        List<String> msgs = new ArrayList<String>();
        int ret = 0;
        String uuid = genUuid();
        for (int i = 0; i < apis.size(); i++) {
            ApiResult result = exec((ApiDescribe) apis.get(i), (DataMap) data.get(i), uuid);
            msgs.addAll(result.getMessages());
            if (!result.isSuccess()) {
                ret = result.getResult();
                Common.rollback();
                break;
            }
        }
        return new ApiResult(ret, msgs);
    }

    public String genUuid() {
        return UUID.randomUUID().toString();
    }

    public ApiResult callApi(ApiDescribe api, DataMap data, String uuid) {
        System.out.println("data2=" + JSONObject.toJSONString(data));
        System.out.println("Common.isViewDataMap(data)=" + Common.isViewDataMap(data));
        if (Common.isViewDataMap(data)) {
            data = this.viewService.convertToDBValues(this.dataService.getTableDesc(api.getId()), (ViewDataMap) data);
        }
        return this.doCallApi(uuid, api, data);
    }

    private List<Object> buildParamSeq(TableDescribe api, Map<String, Object> dataMap) {
        if (api == null || api.getColumns() == null || api.getColumns().size() == 0)
            return null;
        List<Object> param = new ArrayList<Object>();
        for (ColumnDescribe column : api.getColumns()) {
            if (column.getData_type() == 11 || column.getData_type() == 4 || column.getData_type() == 12) {
                Date date = null;
                try {
                    long parseLong = Long.parseLong(dataMap.get(column.getColumn_name()).toString());
                    date = new Date(parseLong);
                } catch (Exception e) {
                    if (dataMap.get(column.getColumn_name()) != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = sdf.parse(dataMap.get(column.getColumn_name()).toString());
                        } catch (Exception ea) {
                        }
                    }
                }
                param.add(date);
                continue;
            }
            param.add(dataMap.get(column.getColumn_name()));
        }
        return param;
    }

    private List<Object> buildParamSeq(TableDescribe api, DataMap dataMap) {
        if (api == null || api.getColumns() == null || api.getColumns().size() == 0)
            return null;
        System.out.println("dataMap=" + JSONObject.toJSONString(dataMap));
        List<Object> param = new ArrayList<Object>();
        for (ColumnDescribe column : api.getColumns()) {
            if (column.getData_type() == 11 || column.getData_type() == 4 || column.getData_type() == 12) {
                Date date = null;
                try {
                    long parseLong = Long.parseLong(dataMap.get(column.getColumn_name()).toString());
                    date = new Date(parseLong);
                } catch (Exception e) {
                    if (dataMap.get(column.getColumn_name()) != null) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        try {
                            date = sdf.parse(dataMap.get(column.getColumn_name()).toString());
                        } catch (Exception ea) {
                        }
                    }
                }
                param.add(date);
                continue;
            }
            System.out.println("column.getColumn_name()=" + column.getColumn_name());
            param.add(dataMap.get(column.getColumn_name()));
        }
        return param;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ApiResult doCallApi(String urlId, List<Map<String, Object>> data) {
        String uuid = genUuid();
        UrlInterfaceDescribe url = (UrlInterfaceDescribe) this.cacheService.getUrlInterface().get(urlId);
        String apiName = url.getUrl();
        if (apiName == null || "".equals(apiName)) {
            throw new ApplicationException("api not found");
        }

        TableDescribe apiTable = this.cacheService.getTableDesc(apiName);
        List<Object> params = null;
        if (null == data || data.size() == 0) {
            params = null;
        } else if (url.getType() == 3) {
            ApiResult result = null;
            for (int i = 0; i < data.size(); i++) {
                params = buildParamSeq(apiTable, (Map) data.get(i));
                result = doCallStoredProcedureApi(uuid, apiName, params);
            }
            return result;
        }

        if (url.getType() == 2) {
            List<String> uuids = new ArrayList<String>();
            uuids.add(uuid);
            List<Map<String, Object>> batchData = new ArrayList<Map<String, Object>>();
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    Map<String, Object> valueMap = new HashMap<String, Object>();
                    for (Map.Entry<String, Object> entry : (data.get(i)).entrySet()) {
                        valueMap.put(entry.getKey(), entry.getValue());
                    }
                    batchData.add(valueMap);
                }
            }
            return this.jApiService.execBatchApi(uuids, apiName, batchData);
        }
        if (url.getType() == 4) {
            ApiResult result = new ApiResult();
            List<String> msg = new ArrayList<String>();
            if (null != data) {
                try {
                    for (int i = 0; i < data.size(); i++) {
                        Object outInterfaceResult = execOutInterface(url.getUrl(), (Map) data.get(i));
                        if (null != outInterfaceResult) {
                            msg.add(outInterfaceResult.toString());
                        }
                    }
                    result.setMessages(msg);
                } catch (Exception e) {
                    throw new ApplicationException("interface: " + e.getMessage());
                }
            }
            return result;
        }
        return null;
    }

    private ApiResult doCallApi(String uuid, ApiDescribe api, List<DatabaseDataMap> data) {
        String apiName = ((UrlInterfaceDescribe) this.cacheService.getUrlInterface().get(api.getId())).getUrl();
        if (apiName == null || "".equals(apiName)) {
            throw new ApplicationException("api not found");
        }

        TableDescribe apiTable = this.cacheService.getTableDesc(apiName);
        if (api.getType() == ApiType.sp) {
            ApiResult apiResult = null;
            if (data != null) {
                for (int i = 0; i < data.size(); i++) {
                    List<Object> params = buildParamSeq(apiTable, (DataMap) data.get(i));
                    apiResult = doCallStoredProcedureApi(uuid, apiName, params);
                }
                return apiResult;
            }
            return apiResult;
        }
        List<String> uuids = new ArrayList<String>();
        uuids.add(uuid);
        List<Map<String, Object>> batchData = new ArrayList<Map<String, Object>>();
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                Map<String, Object> valueMap = new HashMap<String, Object>();
                for (Map.Entry<String, Object> entry : ((DatabaseDataMap) data.get(i)).entrySet()) {
                    valueMap.put(entry.getKey(), entry.getValue());
                }
                batchData.add(valueMap);
            }
        }
        return this.jApiService.execBatchApi(uuids, apiName, batchData);
    }

    public ApiResult doCallApi(String uuid, ApiDescribe api, DataMap data) {
        UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) this.cacheService.getUrlInterface()
                .get(api.getId());
        System.out.println("urlInterfaceDescribe=" + JSONObject.toJSONString(urlInterfaceDescribe));
        if (urlInterfaceDescribe.getType() == 2)
            api.setType(ApiType.java);
        String apiName = urlInterfaceDescribe.getUrl();

        TableDescribe apiTable = this.cacheService.getTableDesc(apiName);
        List<Object> params = buildParamSeq(apiTable, data);
        System.out.println("params=" + JSONObject.toJSONString(params));
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        datas.add(data);
        List<String> uuids = new ArrayList<String>();
        uuids.add(uuid);
        if (apiName == null || "".equals(apiName)) {
            throw new ApplicationException("api not found");
        }
        System.out.println("urlInterfaceDescribe.getType()=" + urlInterfaceDescribe.getType());
        if (urlInterfaceDescribe.getType() == 3)
            return doCallStoredProcedureApi(uuid, apiName, params);
        if (urlInterfaceDescribe.getType() == 2)
            return this.jApiService.execBatchApi(uuids, apiName, datas);
        if (urlInterfaceDescribe.getType() == 4) {
            ApiResult result = new ApiResult();
            List<String> msg = new ArrayList<String>();
            if (null != data) {
                try {
                    Object outInterfaceResult = execOutInterface(urlInterfaceDescribe.getUrl(), data);
                    if (null != outInterfaceResult) {
                        msg.add(outInterfaceResult.toString());
                    }
                    result.setMessages(msg);
                } catch (Exception e) {
                    throw new ApplicationException("interface: " + e.getMessage());
                }
            }
            return result;
        }
        return null;
    }

    public ApiResult doCallApi(String uuid, String apiName, DataMap data, UrlInterfaceDescribe urlInterface,
            TableDescribe apiTable, String domain) {
        List<String> uuids = new ArrayList<String>();
        uuids.add(uuid);
        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
        datas.add(data);
        if (urlInterface.getType() == 3) {
            List<Object> params = buildParamSeq(apiTable, data);
            return doCallStoredProcedureApi(uuid, apiName, params, domain);
        }
        if (urlInterface.getType() == 2)
            return this.jApiService.execBatchApi(uuids, apiName, datas);
        if (urlInterface.getType() == 4) {
            ApiResult result = new ApiResult();
            List<String> msg = new ArrayList<String>();
            if (null != data) {
                try {
                    Object outInterfaceResult = execOutInterface(urlInterface.getUrl(), data);
                    if (null != outInterfaceResult) {
                        msg.add(outInterfaceResult.toString());
                    }
                    result.setMessages(msg);
                } catch (Exception e) {
                    throw new ApplicationException("interface: " + e.getMessage());
                }
            }
            return result;
        }
        return null;
    }

    private ApiResult doCallStoredProcedureApi(String uuid, String apiId, List<Object> data, String domain) {
        this.systemDao.callApiTimeTask(uuid, apiId, data, domain);
        List<ApiLog> logs = this.systemDao.selectApiLogsTimeTask(uuid, domain);
        List<String> msgs = new ArrayList<String>();
        int ret = 0;
        for (ApiLog log : logs) {
            String name = log.getProc_name();
            String msg = name + " : " + this.dataService.getMessageText(log.getMsg_id(),
                    (Object[]) Common.split(log.getMsg_param(), ","));
            if (ret == 0 && name.equals(apiId))
                ret = log.getResult();
            msgs.add(msg);
        }
        return new ApiResult(ret, msgs);
    }

    private ApiResult doCallStoredProcedureApi(String uuid, String apiId, List<Object> data) {
        this.systemDao.callApi(uuid, apiId, data);
        List<ApiLog> logs = this.systemDao.selectApiLogs(uuid);
        List<String> msgs = new ArrayList<String>();
        int ret = 0;
        for (ApiLog log : logs) {
            String name = log.getProc_name();
            String msg = name + " : " + this.dataService.getMessageText(log.getMsg_id(),
                    (Object[]) Common.split(log.getMsg_param(), ","));
            if (ret == 0 && name.equals(apiId))
                ret = log.getResult();
            msgs.add(msg);
        }
        return new ApiResult(ret, msgs);
    }

    public Map<String, Object> buildData(List<FieldModelBase> fields) {
        Map<String, Object> data = new HashMap<String, Object>();

        if (fields != null) {
            for (FieldModelBase field : fields)
                data.put(field.getColumn(), field.getOrig());
        }
        return data;
    }

    public DatabaseDataMap buildRawApiParam(ApiDescribe api, Map<String, Object> data) {
        DatabaseDataMap ret = new DatabaseDataMap();
        for (Map.Entry<String, Object> entry : api.getParams().entrySet()) {
            Object value = entry.getValue();
            if (!String.class.isInstance(value)) {
                ret.put(entry.getKey(), value);
                continue;
            }
            Matcher matcher = TRIGGER_PARAM_PARSER.matcher((String) value);
            if (matcher.find()) {
                String var = matcher.group(1);
                ret.put(entry.getKey(), data.get(var));
                continue;
            }
            ret.put(entry.getKey(), value);
        }
        return ret;
    }

    @SuppressWarnings("rawtypes")
    public DatabaseDataMap buildTriggerApiParam(ApiDescribe api, Map<String, Map<String, Object>> data) {
        DatabaseDataMap ret = new DatabaseDataMap();
        for (Map.Entry<String, Object> entry : api.getParams().entrySet()) {
            Object value = entry.getValue();
            if (!String.class.isInstance(value)) {
                ret.put(entry.getKey(), value);
                continue;
            }
            Matcher matcher = TRIGGER_PARAM_PARSER.matcher((String) value);
            if (matcher.find()) {
                String[] var = Common.split(matcher.group(1), ".");
                if (var == null || var.length != 2)
                    throw new ApplicationException("varible \"" + value + "\"not supported");
                ret.put(entry.getKey(), ((Map) data.get(var[0])).get(var[1]));
                continue;
            }
            ret.put(entry.getKey(), value);
        }
        return ret;
    }

    public ApiResult fireTrigger(TriggerDescribe trigger, Integer isList) {
        String uuid = genUuid();
        List<ApiResult> apiResults = new ArrayList<ApiResult>();
        for (ApiDescribe api : trigger.getApi()) {
            if (api.getIs_using().intValue() != 1) {
                continue;
            }
            if (api.getType() == ApiType.sp || api.getType() == ApiType.java) {
                if (isList != null && isList.intValue() == 1) {
                    ApiResult apiResult = doCallApi(uuid, api, api.getListRequestParams());
                    apiResults.add(apiResult);
                    continue;
                }
                List<DatabaseDataMap> params = new ArrayList<DatabaseDataMap>();
                params.add(api.getRequestParams());
                ApiResult apiResult = doCallApi(uuid, api, params);
                apiResults.add(apiResult);
                continue;
            }
            if (api.getType() == ApiType.URL)
                continue;
            if (api.getType() == ApiType.outside_interface)
                continue;
            if (api.getType() == ApiType.report)
                ;
        }
        ApiResult returnApiResult = new ApiResult();
        for (ApiResult apiResult : apiResults) {
            if (!apiResult.isSuccess()) {
                returnApiResult.setResult(1);
                if (returnApiResult.getMessages() == null) {
                    returnApiResult.setMessages(apiResult.getMessages());
                    continue;
                }
                returnApiResult.getMessages().addAll(apiResult.getMessages());
            }
        }
        return returnApiResult;
    }

    public ApiActionResult insertRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            TriggerDescribe requestTriggerr, Integer isList) {
        return insertRecordWithTrigger(table, data, true, requestTriggerr, isList);
    }

    public ApiActionResult buildFailureResult(ApiResult result, String failedMessage, List<String> msgs,
            boolean failedWithException) {
        ApiActionResult failure = new ApiActionResult(result.getResult(), failedMessage, msgs);
        if (failedWithException) {
            throw new ApplicationException(Common.join(msgs, "\n"));
        }
        return failure;
    }

    public ApiActionResult insertRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            boolean failedWithException, TriggerDescribe requestTriggerr, Integer isList) {
        this.dataService.prepareInsert(table, data);
        this.dataService.doInsertRecord(table, data);
        TriggerDescribe trigger = table.getTrigger(TriggerDescribe.TriggerType.add);
        List<String> msgs = new ArrayList<String>();
        String insertFailedMsg = this.dataService.getMessageText("Insert failed",
                new Object[] { this.dataService.getLabel(table) });
        if (trigger != null && trigger.getApi() != null && requestTriggerr != null) {
            ApiResult result = fireTrigger(requestTriggerr, isList);
            if (result.getMessages() != null)
                msgs.addAll(result.getMessages());
            if (!result.isSuccess()) {
                return buildFailureResult(result, insertFailedMsg, msgs, failedWithException);
            }
        }
        String messageText = this.dataService.getMessageText("Insert success",
                new Object[] { this.dataService.getLabel(table) });
        msgs.add(messageText);
        return new ApiActionResult(0, messageText, msgs);
    }

    public ApiActionResult updateRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            List<FieldModelBase> fields, TriggerDescribe requestTriggerr) {
        return updateRecordWithTrigger(table, data, fields, true, requestTriggerr);
    }

    public ApiActionResult updateRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            List<FieldModelBase> fields, boolean failedWithException, TriggerDescribe requestTriggerr) {
        this.dataService.prepareUpdate(table, data);
        if (this.dataService.doUpdateRecord(table, data, null, null, null) != 1)
            throw new ApplicationException("Update failed");
        TriggerDescribe trigger = table.getTrigger(TriggerDescribe.TriggerType.edit);
        List<String> msgs = new ArrayList<String>();
        String updateFailedMsg = this.dataService.getMessageText("Update failed",
                new Object[] { this.dataService.getLabel(table) });
        if (trigger != null && trigger.getApi() != null && requestTriggerr != null) {
            ApiResult result = fireTrigger(requestTriggerr, null);
            if (result.getMessages() != null) {
                msgs.addAll(result.getMessages());
                if (!result.isSuccess())
                    return buildFailureResult(result, updateFailedMsg, msgs, failedWithException);
            }
        }
        String updateSuccessMsg = this.dataService.getMessageText("Update success",
                new Object[] { this.dataService.getLabel(table) });
        msgs.add(this.dataService.getMessageText(updateSuccessMsg, new Object[] { this.dataService.getLabel(table) }));

        return new ApiActionResult(0, updateSuccessMsg, msgs);
    }

    public ApiActionResult updateImportRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            TriggerDescribe requestTriggerr, Set<String> updateColumns, Set<String> keyColumns) {
        this.dataService.prepareUpdate(table, data);
        TriggerDescribe trigger = table.getTrigger(TriggerDescribe.TriggerType.edit);
        List<String> msgs = new ArrayList<String>();
        String updateFailedMsg = this.dataService.getMessageText("Update failed",
                new Object[] { this.dataService.getLabel(table) });
        if (trigger != null && trigger.getApi() != null && requestTriggerr != null) {
            ApiResult result = fireTrigger(requestTriggerr, null);
            if (result.getMessages() != null) {
                msgs.addAll(result.getMessages());
                if (!result.isSuccess()) {
                    return buildFailureResult(result, updateFailedMsg, msgs, true);
                }
            }
        }
        if (this.dataService.doUpdateRecord(table, data, updateColumns, keyColumns, null) != 1)
            throw new ApplicationException("Update failed");
        String updateSuccessMsg = this.dataService.getMessageText("Update success",
                new Object[] { this.dataService.getLabel(table) });
        msgs.add(this.dataService.getMessageText(updateSuccessMsg, new Object[] { this.dataService.getLabel(table) }));

        return new ApiActionResult(0, updateSuccessMsg, msgs);
    }

    @SuppressWarnings({ "unused", "rawtypes" })
    public ApiActionResult insertImportRecordWithTrigger(TableDescribe table, Map<String, Object> data,
            boolean failedWithException) {
        BatchDescribe batch = this.systemDao.selectBatchDescByTable(table.getId());
        List<ApiDescribe> apis = ApiDescribe.parseStatement(batch.getCreate_trigger());
        ApiDescribe api = (apis == null) ? null : (ApiDescribe) apis.get(0);
        String uuid = genUuid();
        DatabaseDataMap param = new DatabaseDataMap(data.size());
        param.putAll(data);
        this.dataService.prepareInsert(table, data);

        this.dataService.doInsertRecord(table, data);

        String insertFailedMsg = this.dataService.getMessageText("Insert failed",
                new Object[] { this.dataService.getLabel(table) });
        List<String> msgs = new ArrayList<String>();
        ApiResult result = null;

        if (api != null) {
            if (api.getType() == ApiType.sp || api.getType() == ApiType.java) {
                List<DatabaseDataMap> params = new ArrayList<DatabaseDataMap>();
                result = doCallApi(uuid, api, param);
            }
            if (result.getMessages() != null)
                msgs.addAll(result.getMessages());
            if (!result.isSuccess()) {
                return buildFailureResult(result, insertFailedMsg, msgs, failedWithException);
            }
        }
        String messageText = this.dataService.getMessageText("Insert success",
                new Object[] { this.dataService.getLabel(table) });
        msgs.add(messageText);
        return new ApiActionResult(0, messageText, msgs);
    }

    public ApiActionResult deleteRecordWithTrigger(TableDescribe table, List<Map<String, Object>> data,
            TriggerDescribe requestTriggerr) {
        return deleteRecordWithTrigger(table, data, true, requestTriggerr);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ApiActionResult deleteRecordWithTrigger(TableDescribe table, List<Map<String, Object>> data,
            boolean failedWithException, TriggerDescribe requestTriggerr) {
        int ret = 0;
        TriggerDescribe trigger = table.getTrigger(TriggerDescribe.TriggerType.delete);
        String deleteFailedMsg = this.dataService.getMessageText("Delete failed",
                new Object[] { this.dataService.getLabel(table) });
        List<String> msgs = new ArrayList<String>();
        String messageText = this.dataService.getMessageText("Delete success",
                new Object[] { this.dataService.getLabel(table) });
        if (trigger != null && trigger.getApi() != null) {
            ApiResult result = fireTrigger(requestTriggerr, Integer.valueOf(1));
            if (result.getMessages() != null)
                msgs.addAll(result.getMessages());
            if (!result.isSuccess())
                return buildFailureResult(result, deleteFailedMsg, msgs, failedWithException);
        }
        for (int i = 0; i < data.size(); i++) {
            this.dataService.deleteRecord(table.getId(), (Map) data.get(i));
        }
        msgs.add(messageText);

        return new ApiActionResult(ret, messageText, msgs);
    }

    public void execApi(String urlId, Map<String, Object> param) {
        String uuid = genUuid();
        ApiDescribe api = new ApiDescribe();
        api.setId(urlId);
        DataMap data = new DataMap();
        for (Map.Entry<String, Object> entry : param.entrySet()) {
            data.put(entry.getKey(), entry.getValue());
        }
        ApiResult result = doCallApi(uuid, api, data);
        List<String> msgs = new ArrayList<String>();
        if (result.getMessages() != null)
            msgs.addAll(result.getMessages());
        if (!result.isSuccess())
            buildFailureResult(result, null, msgs, true);
    }

    public Object execOutInterface(String url, Map<String, Object> data) {
        String result = "";
        if (url != null) {
            if (url.indexOf("https") != -1) {
                result = HttpClientUtil.doPostSSL(url, data);
            } else {
                result = HttpClientUtil.doPost(url, data);
            }
        }

        return result;
    }
}
