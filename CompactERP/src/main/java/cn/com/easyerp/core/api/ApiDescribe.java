package cn.com.easyerp.core.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.framework.common.Common;

public class ApiDescribe {
    private static TypeReference<ApiDescribe> apiMapJsonRef = new TypeReference<ApiDescribe>() {
    };
    private String event_id;
    private ApiType type = ApiType.sp;
    private String id;
    private Map<String, Object> params = null;
    private String table_action_id;
    private String seq;
    private Integer event_type;
    private String event_name;
    private String event_param;
    private Integer is_using;
    private DatabaseDataMap requestParams;
    private List<DatabaseDataMap> listRequestParams;

    public String getTable_action_id() {
        return this.table_action_id;
    }

    public void setTable_action_id(String table_action_id) {
        this.table_action_id = table_action_id;
    }

    public String getSeq() {
        return this.seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public Integer getEvent_type() {
        return this.event_type;
    }

    public void setEvent_type(Integer event_type) {
        this.event_type = event_type;
    }

    public String getEvent_name() {
        return this.event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getEvent_param() {
        return this.event_param;
    }

    public void setEvent_param(String event_param) {
        Map<String, Object> eventParam = paseEventParam(event_param);
        this.event_param = event_param;
        this.params = eventParam;
    }

    private Map<String, Object> paseEventParam(String event_param) {
        Map<String, Object> map = new HashMap<String, Object>();

        if (event_param != null && !"".equals(event_param)) {
            try {
                JSONObject jsonObject = JSONObject.parseObject(event_param);
                for (Iterator<?> iter = jsonObject.keySet().iterator(); iter.hasNext();) {
                    String key = (String) iter.next();
                    String value = jsonObject.get(key).toString();
                    try {
                        Map<String, Object> resultMap = paseEventParam(value);
                        map.put(key, resultMap);
                    } catch (Exception e) {
                        map.put(key, value);
                    }
                }
            } catch (Exception e) {
                throw new ApplicationException("param \"" + event_param + "\" not json string");
            }
        }
        return map;
    }

    public Integer getIs_using() {
        return this.is_using;
    }

    public void setIs_using(Integer is_using) {
        this.is_using = is_using;
    }

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public static List<ApiDescribe> parseStatement(String statements) {
        List<ApiDescribe> ret = new ArrayList<ApiDescribe>();
        if (statements == null)
            return null;
        String s = statements.replaceAll("#", "\\$");

        ApiDescribe api = (ApiDescribe) Common.fromJson(s, apiMapJsonRef);
        ret.add(api);
        return ret;
    }

    public static List<ApiDescribe> parseStatement(Map<String, UrlInterfaceDescribe> UrlInterfaceCache,
            List<ActionEventDescribe> statements) {
        if (statements == null)
            return null;
        List<ApiDescribe> ret = new ArrayList<ApiDescribe>();
        for (ActionEventDescribe actionEventDescribe : statements) {

            ApiDescribe api = new ApiDescribe();
            api.setId(actionEventDescribe.getEvent_name());
            api.setEvent_id(actionEventDescribe.getEvent_id());
            if (actionEventDescribe.getEvent_type() == null) {
                continue;
            }
            if (actionEventDescribe.getEvent_type().intValue() == 3) {
                api.setType(ApiType.sp);
            } else if (actionEventDescribe.getEvent_type().intValue() == 2) {
                api.setType(ApiType.java);
            } else if (actionEventDescribe.getEvent_type().intValue() == 1) {
                api.setType(ApiType.URL);
            } else if (actionEventDescribe.getEvent_type().intValue() == 4) {
                api.setType(ApiType.outside_interface);
            } else if (actionEventDescribe.getEvent_type().intValue() == 5) {
                api.setType(ApiType.report);
            }
            api.setTable_action_id(actionEventDescribe.getTable_action_id());
            api.setEvent_name(actionEventDescribe.getEvent_name());
            api.setSeq(Integer.toString(actionEventDescribe.getSeq()));
            api.setEvent_type(actionEventDescribe.getEvent_type());
            api.setEvent_param(actionEventDescribe.getEvent_param());
            api.setIs_using(actionEventDescribe.getIs_using());

            ret.add(api);
        }
        return ret;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApiType getType() {
        return this.type;
    }

    public void setType(ApiType type) {
        this.type = type;
    }

    public Map<String, Object> getParams() {
        return this.params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public boolean defined(String paramName) {
        return (this.params != null && this.params.containsKey(paramName));
    }

    public DatabaseDataMap getRequestParams() {
        return this.requestParams;
    }

    public void setRequestParams(DatabaseDataMap requestParams) {
        this.requestParams = requestParams;
    }

    public List<DatabaseDataMap> getListRequestParams() {
        return this.listRequestParams;
    }

    public void setListRequestParams(List<DatabaseDataMap> listRequestParams) {
        this.listRequestParams = listRequestParams;
    }
}
