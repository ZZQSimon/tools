package cn.com.easyerp.core.timedTask;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.ReferenceModel;
import cn.com.easyerp.core.evaluate.Formula;
import cn.com.easyerp.core.evaluate.FormulaService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.timedTask.entity.TimeTaskBusinessTimeDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskEventDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskSysTimeDescribe;
import cn.com.easyerp.framework.common.Common;

@Service
public class SysTimedTaskService {
    public Map<String, Map<String, TimeTaskDescribe>> timeTaskDescribeCache = new HashMap<>();
    private static final long TIME_TASK_SPACR_TIME = 60000L;
    public Map<String, Map<String, TableDescribe>> tableCache = new HashMap<>();
    public Map<String, Map<String, UrlInterfaceDescribe>> urlCache = new HashMap<>();
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private DataService dataService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private Formula formula;
    @Autowired
    private TimeTaskProperties timeTaskProperties;
    @Autowired
    private FormulaService formulaService;

    @PostConstruct
    public void init() {
        List<String> domains = this.dxRoutingDataSource.getList();
        if (domains != null && domains.size() != 0)
            for (String domain : domains) {
                reload(domain);
            }
    }

    public Map<String, TimeTaskDescribe> getTimeTask(String domain) {
        List<TimeTaskDescribe> timeTaskDescribes = this.systemDao.selectTimeTask(domain);
        if (timeTaskDescribes == null) {
            return null;
        }
        Map<String, TimeTaskDescribe> timeTaskDescribeMap = new HashMap<String, TimeTaskDescribe>();
        for (TimeTaskDescribe timeTaskDescribe : timeTaskDescribes)
            timeTaskDescribeMap.put(timeTaskDescribe.getTask_id(), timeTaskDescribe);
        List<TimeTaskSysTimeDescribe> timeTaskSysTimeDescribes = this.systemDao.selectTimeTaskSysTime(domain);
        if (timeTaskSysTimeDescribes != null)
            for (TimeTaskSysTimeDescribe timeTaskSysTime : timeTaskSysTimeDescribes) {
                TimeTaskDescribe timeTask = (TimeTaskDescribe) timeTaskDescribeMap.get(timeTaskSysTime.getTask_id());
                if (timeTask != null) {
                    ((TimeTaskDescribe) timeTaskDescribeMap.get(timeTaskSysTime.getTask_id()))
                            .addTimeTaskSysTimeDescribes(timeTaskSysTime);
                }
            }
        List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimeDescribes = this.systemDao
                .selectTimeTaskBusinessTime(domain);
        if (timeTaskBusinessTimeDescribes != null)
            for (TimeTaskBusinessTimeDescribe timeTaskBusinessTime : timeTaskBusinessTimeDescribes) {
                TimeTaskDescribe timeTask = (TimeTaskDescribe) timeTaskDescribeMap
                        .get(timeTaskBusinessTime.getTask_id());
                if (timeTask != null) {
                    ((TimeTaskDescribe) timeTaskDescribeMap.get(timeTaskBusinessTime.getTask_id()))
                            .addTimeTaskBusinessTimeDescribes(timeTaskBusinessTime);
                }
            }
        List<TimeTaskEventDescribe> timeTaskEventDescribes = this.systemDao.selectTimeTaskEvent(domain);
        if (timeTaskEventDescribes != null)
            for (TimeTaskEventDescribe timeTaskEvent : timeTaskEventDescribes) {
                TimeTaskDescribe timeTask = (TimeTaskDescribe) timeTaskDescribeMap.get(timeTaskEvent.getTask_id());
                if (timeTask != null) {
                    ((TimeTaskDescribe) timeTaskDescribeMap.get(timeTaskEvent.getTask_id()))
                            .addTimeTaskEventDescribes(timeTaskEvent);
                }
            }
        return timeTaskDescribeMap;
    }

    public Map<String, TableDescribe> getTable(String domain) {
        List<TableDescribe> tables = this.systemDao.selectTableDescribe(domain);
        Map<String, TableDescribe> tablesMap = new HashMap<String, TableDescribe>();
        for (TableDescribe table : tables) {
            tablesMap.put(table.getId(), table);
        }
        List<ColumnDescribe> columnDescribes = this.systemDao.selectColumnsDescribe(domain);

        for (ColumnDescribe column : columnDescribes) {
            if (column.getUrl_id() != null && !"".equals(column.getUrl_id())) {
                UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) ((Map) this.urlCache.get(domain))
                        .get(column.getUrl_id());
                if (urlInterfaceDescribe != null) {
                    urlInterfaceDescribe.addUrlParam(column);
                }
            }
            String table_id = column.getTable_id();
            TableDescribe table = (TableDescribe) tablesMap.get(table_id);

            if (table != null) {
                if (column.getIs_id_column() == 1)
                    table.addId_column(column.getColumn_name());
                table.addColumn(column);
            }
        }

        return tablesMap;
    }

    public Map<String, UrlInterfaceDescribe> getUrl(String domain) {
        Map<String, UrlInterfaceDescribe> urlInterfaceMap = new HashMap<String, UrlInterfaceDescribe>();
        for (UrlInterfaceDescribe urlInterface : this.systemDao.selectUrlInterface(domain)) {
            urlInterfaceMap.put(urlInterface.getId(), urlInterface);
        }
        return urlInterfaceMap;
    }

    public void reload() {
        reload(this.dxRoutingDataSource.getDomainKey());
    }

    public void reload(String domain) {
        try {
            this.timeTaskDescribeCache.put(domain, getTimeTask(domain));
            this.urlCache.put(domain, getUrl(domain));
            this.tableCache.put(domain, getTable(domain));
        } catch (Exception e) {
        }
    }

    public void execTask() {
        try {
            if (!this.timeTaskProperties.getIsExec())
                return;
            if (this.timeTaskDescribeCache.size() == 0) {
                return;
            }
            for (Map.Entry<String, Map<String, TimeTaskDescribe>> timeTasks : this.timeTaskDescribeCache.entrySet()) {
                Object contains = this.timeTaskProperties.getDomains().get(timeTasks.getKey());
                if (null == this.timeTaskProperties.getDomains() || null == contains) {
                    continue;
                }
                for (Map.Entry<String, TimeTaskDescribe> timeTask : (timeTasks.getValue()).entrySet()) {
                    if (((TimeTaskDescribe) timeTask.getValue()).getTimeTaskSysTimeDescribes() != null) {
                        execSysTimeTask((String) timeTasks.getKey(), (TimeTaskDescribe) timeTask.getValue());
                    }
                    if (((TimeTaskDescribe) timeTask.getValue()).getTimeTaskBusinessTimeDescribes() != null) {
                        execBusinessTimeTask((String) timeTasks.getKey(), (TimeTaskDescribe) timeTask.getValue());
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execSysTimeTask(final String domain, final TimeTaskDescribe timeTask) {
        try {
            final Date nowDate = new Date();
            final List<TimeTaskSysTimeDescribe> timeTaskSysTimes = timeTask.getTimeTaskSysTimeDescribes();
            for (int i = 0; i < timeTaskSysTimes.size(); ++i) {
                TimeTaskSysTimeDescribe timeTaskSysTimeDescribe = timeTaskSysTimes.get(i);
                if (timeTaskSysTimes.get(i).getIs_using() == 1) {
                    if (timeTaskSysTimes.get(i).getEnd_date() == null
                            || timeTaskSysTimes.get(i).getEnd_date().getTime() >= nowDate.getTime()) {
                        if (timeTaskSysTimes.get(i).getIs_loop() != 0
                                || timeTaskSysTimes.get(i).getBegin_date().getTime() >= nowDate.getTime()) {
                            if (timeTaskSysTimes.get(i).getSpace() != 0
                                    || timeTaskSysTimes.get(i).getBegin_date().getTime() >= nowDate.getTime()) {
                                final String loop_type = timeTaskSysTimes.get(i).getLoop_type();
                                switch (loop_type) {
                                case "min":
                                case "hour":
                                case "day": {
                                    if (this.isExec(timeTaskSysTimes.get(i), nowDate)) {
                                        final List<TimeTaskEventDescribe> timeTaskEvents = timeTask
                                                .getTimeTaskEventDescribes();
                                        if (timeTaskEvents != null) {
                                            for (final TimeTaskEventDescribe timeTaskEvent : timeTaskEvents) {
                                                this.exec(domain, timeTaskEvent, null);
                                            }
                                        }
                                        break;
                                    }
                                    break;
                                }
                                case "mouth":
                                case "year":
                                case "week": {
                                    if (!this.isExec(timeTaskSysTimes.get(i), nowDate, true)) {
                                        break;
                                    }
                                    final List<TimeTaskEventDescribe> timeTaskEvents = timeTask
                                            .getTimeTaskEventDescribes();
                                    if (timeTaskEvents != null) {
                                        for (final TimeTaskEventDescribe timeTaskEvent : timeTaskEvents) {
                                            this.exec(domain, timeTaskEvent, null);
                                        }
                                        break;
                                    }
                                    break;
                                }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void execBusinessTimeTask(String domain, TimeTaskDescribe timeTask) {
        try {
            Date nowDate = new Date();
            List<TimeTaskBusinessTimeDescribe> timeTaskBusinessTimes = timeTask.getTimeTaskBusinessTimeDescribes();

            for (TimeTaskBusinessTimeDescribe timeTaskBusiness : timeTaskBusinessTimes) {
                if (timeTaskBusiness.getIs_using() != 1)
                    continue;
                TableDescribe table = (TableDescribe) ((Map) this.tableCache.get(domain))
                        .get(timeTaskBusiness.getTable());
                if (isExec(timeTaskBusiness, nowDate, domain)) {
                    Map<String, Object> param = buildBusinessSQLParam(timeTaskBusiness, nowDate, domain);
                    List<ReferenceModel> refs = this.dataService.makeRefModels(table);

                    List<Map<String, Object>> businessDatas = this.systemDao.selectBusiness(param,
                            timeTaskBusiness.getTable(), timeTaskBusiness.getColumn(), refs);
                    List<Map<String, Object>> maps = makeRefData(businessDatas);
                    if (businessDatas != null) {
                        List<TimeTaskEventDescribe> timeTaskEvents = timeTask.getTimeTaskEventDescribes();
                        if (timeTaskEvents == null || timeTaskEvents.size() == 0)
                            continue;
                        for (Map<String, Object> bussinessData : maps) {
                            for (TimeTaskEventDescribe timeTaskEvent : timeTaskEvents) {
                                exec(domain, timeTaskEvent, bussinessData);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ApiResult exec(String domain, TimeTaskEventDescribe timeTaskEvent, Map<String, Object> businessData) {
        try {
            Map<String, Object> data = buildApiParam(timeTaskEvent, businessData);
            String event_url_id = timeTaskEvent.getEvent_url_id();
            UrlInterfaceDescribe urlInterfaceDescribe = (UrlInterfaceDescribe) ((Map) this.urlCache.get(domain))
                    .get(event_url_id);
            String apiName = urlInterfaceDescribe.getUrl();

            TableDescribe apiTable = (TableDescribe) ((Map) this.tableCache.get(domain)).get(apiName);
            String uuid = this.apiService.genUuid();
            DataMap paramData = new DataMap();
            if (data != null) {
                for (Map.Entry<String, Object> apiData : data.entrySet()) {
                    paramData.put(apiData.getKey(), apiData.getValue());
                }
            }
            this.systemDao.insertTimeTaskLog(uuid, apiName, Common.toJson(paramData), new Date(), domain);
            this.dxRoutingDataSource.setActiveDomainKey(domain);
            return exec(uuid, apiName, paramData, urlInterfaceDescribe, apiTable, domain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Transactional
    private ApiResult exec(String uuid, String apiName, DataMap paramData, UrlInterfaceDescribe urlInterfaceDescribe,
            TableDescribe apiTable, String domain) {
        try {
            return this.apiService.doCallApi(uuid, apiName, paramData, urlInterfaceDescribe, apiTable, domain);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isExec(TimeTaskSysTimeDescribe timeTaskSysTime, Date nowDate) {
        try {
            long leadTime = buildLeadTime(timeTaskSysTime.getBegin_date(), timeTaskSysTime.getLead(),
                    timeTaskSysTime.getLead_type());
            if (timeTaskSysTime.getIs_loop() == 0) {
                if (timeTaskSysTime.getBegin_date().getTime() > nowDate.getTime() - 60000L - leadTime
                        && timeTaskSysTime.getBegin_date().getTime() < nowDate.getTime() + 60000L - leadTime) {
                    return true;
                }
                return false;
            }

            long spaceTime = buildSpaceTime(timeTaskSysTime);
            if (spaceTime == 0L && timeTaskSysTime.getBegin_date().getTime() < nowDate.getTime() - leadTime)
                return false;
            if (spaceTime == 0L && timeTaskSysTime.getBegin_date().getTime() > nowDate.getTime() - 60000L - leadTime
                    && timeTaskSysTime.getBegin_date().getTime() < nowDate.getTime() + 60000L - leadTime) {
                return true;
            }

            long result = (nowDate.getTime() + leadTime - timeTaskSysTime.getBegin_date().getTime()) % spaceTime;
            if (result > -60000L && result < 60000L) {
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isExec(TimeTaskSysTimeDescribe timeTaskSysTime, Date nowDate, boolean isYear) {
        try {
            long timeSub, leadTime = buildLeadTime(timeTaskSysTime.getBegin_date(), timeTaskSysTime.getLead(),
                    timeTaskSysTime.getLead_type());
            if (timeTaskSysTime.getIs_loop() == 0) {
                if (timeTaskSysTime.getBegin_date().getTime() > nowDate.getTime() - 60000L - leadTime
                        && timeTaskSysTime.getBegin_date().getTime() < nowDate.getTime() + 60000L - leadTime) {
                    return true;
                }
                return false;
            }
            Calendar gcBeginDate = Calendar.getInstance();
            gcBeginDate.setTime(timeTaskSysTime.getBegin_date());
            Calendar gcNowDate = Calendar.getInstance();
            gcNowDate.setTime(nowDate);
            int sub = 0;

            switch (timeTaskSysTime.getLoop_type()) {
            case "mouth":
                sub = gcBeginDate.get(1) - gcNowDate.get(1);
                gcBeginDate.add(1, 0 - sub);
                sub = gcBeginDate.get(2) - gcNowDate.get(2);
                gcBeginDate.add(2, 0 - sub);
                timeSub = gcBeginDate.getTime().getTime() - gcNowDate.getTime().getTime() - leadTime;
                if (timeSub > -60000L && timeSub < 60000L) {
                    return true;
                }
                return false;
            case "year":
                sub = gcBeginDate.get(1) - gcNowDate.get(1);
                gcBeginDate.add(1, 0 - sub);
                timeSub = gcBeginDate.getTime().getTime() - gcNowDate.getTime().getTime() - leadTime;
                if (timeSub > -60000L && timeSub < 60000L) {
                    return true;
                }
                return false;
            case "week":
                sub = gcBeginDate.get(1) - gcNowDate.get(1);
                gcBeginDate.add(1, 0 - sub);
                sub = gcBeginDate.get(2) - gcNowDate.get(2);
                gcBeginDate.add(2, 0 - sub);
                sub = gcBeginDate.get(4) - gcNowDate.get(4);
                gcBeginDate.add(4, 0 - sub);
                timeSub = gcBeginDate.getTime().getTime() - gcNowDate.getTime().getTime() - leadTime;
                if (timeSub > -60000L && timeSub < 60000L) {
                    return true;
                }
                return false;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean isExec(TimeTaskBusinessTimeDescribe timeTaskBusiness, Date nowDate, String domain) {
        try {
            Map<String, Object> param = buildBusinessSQLParam(timeTaskBusiness, nowDate, domain);
            this.dxRoutingDataSource.setActiveDomainKey(domain);
            int countBusiness = this.systemDao.selectCountBusiness(param, timeTaskBusiness.getTable(),
                    timeTaskBusiness.getColumn(), domain);
            if (countBusiness == 0) {
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private long buildSpaceTime(TimeTaskSysTimeDescribe timeTaskSysTime) {
        try {
            if (timeTaskSysTime.getSpace() == 0) {
                return 0L;
            }
            switch (timeTaskSysTime.getLoop_type()) {
            case "min":
                return (timeTaskSysTime.getSpace() * 60 * 1000);
            case "hour":
                return (timeTaskSysTime.getSpace() * 60 * 60 * 1000);
            case "day":
                return (timeTaskSysTime.getSpace() * 24 * 60 * 60 * 1000);
            }
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private long buildLeadTime(Date beginDate, int lead, String leadType) {
        try {
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(beginDate);
            if (lead == 0) {
                return 0L;
            }
            switch (leadType) {
            case "min":
                return (lead * 60 * 1000);
            case "hour":
                return (lead * 60 * 60 * 1000);
            case "day":
                return (lead * 24 * 60 * 60 * 1000);
            case "mouth":
                gc.add(2, lead);
                return beginDate.getTime() - gc.getTime().getTime();
            case "year":
                gc.add(1, lead);
                return beginDate.getTime() - gc.getTime().getTime();
            case "week":
                gc.add(3, lead);
                return beginDate.getTime() - gc.getTime().getTime();
            }
            return 0L;
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    private Map<String, Object> buildBusinessSQLParam(TimeTaskBusinessTimeDescribe timeTaskBusiness, Date nowDate,
            String domain) {
        try {
            long leadTime = buildLeadTime(nowDate, timeTaskBusiness.getLead(), timeTaskBusiness.getLead_type());
            Map<String, Object> param = new HashMap<String, Object>();
            param.put("beginDate", new Date(nowDate.getTime() - leadTime - 60000L));
            param.put("endDate", new Date(nowDate.getTime() - leadTime + 60000L));
            if (timeTaskBusiness.getFilter_sql() == null || "".equals(timeTaskBusiness.getFilter_sql())) {
                param.put("filter", "select * from " + timeTaskBusiness.getTable());
            } else {
                param.put("filter", timeTaskBusiness.getFilter_sql());
            }
            return param;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Map<String, Object> buildApiParam(TimeTaskEventDescribe timeTaskEven, Object data) {
        return recursionParam(timeTaskEven.getParam(), data);
    }

    private Map<String, Object> recursionParam(Map<String, Object> param, Object data) {
        try {
            Map<String, Object> resultParam = new HashMap<String, Object>();
            for (Map.Entry<String, Object> entry : param.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    resultParam.put(entry.getKey(), recursionParam((Map) entry.getValue(), data));
                    continue;
                }
                if (entry.getValue() != null) {
                    resultParam.put(entry.getKey(), this.formulaService.evaluate(entry.getValue().toString(), data));
                }
            }
            return resultParam;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<Map<String, Object>> makeRefData(List<Map<String, Object>> records) {
        try {
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
                for (Map.Entry<String, Object> entry : rec.entrySet()) {

                    if (((String) entry.getKey()).contains(".")) {
                        String[] names = ((String) entry.getKey()).split("\\.");
                        rec.put(names[0], entry.getValue());
                    }
                }
            }

            return ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
