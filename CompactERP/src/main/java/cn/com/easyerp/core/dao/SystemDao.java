package cn.com.easyerp.core.dao;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.SelectProvider;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.api.ApiLog;
import cn.com.easyerp.core.authGroup.AuthDataGroup;
import cn.com.easyerp.core.authGroup.AuthDataGroupDetail;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.MenuGroup;
import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.AutoGenTableDesc;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.cache.CalenderEventConfigDescribe;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnDescribe;
import cn.com.easyerp.core.cache.ComplexColumnMapModel;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.OperationDescribe;
import cn.com.easyerp.core.cache.OperationRuleModel;
import cn.com.easyerp.core.cache.OrderByDescribe;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.cache.TableCheckRuleDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TableRenderModel;
import cn.com.easyerp.core.cache.TableShortcutDescribe;
import cn.com.easyerp.core.cache.TableViewStyle;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.data.CodeStrDescribe;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ReferenceModel;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.timedTask.entity.TimeTaskBusinessTimeDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskEventDescribe;
import cn.com.easyerp.core.timedTask.entity.TimeTaskSysTimeDescribe;
import cn.com.easyerp.core.widget.grid.dt.DataTablePagingParameterModel;
import cn.com.easyerp.core.widget.message.MessageTemplateModel;
import cn.com.easyerp.core.widget.message.TimmingMessageModel;
import cn.com.easyerp.framework.enums.LogType;

@Repository
public interface SystemDao {
    List<TableDescribe> selectTableDescribe(@Param("domain") String paramString);

    List<TableViewStyle> selectTableViewStyle();

    List<ColumnDescribe> selectColumnsDescribe(@Param("domain") String paramString);

    List<I18nDescribe> selectI18N();

    List<I18nDescribe> selectTableI18N();

    List<I18nDescribe> selectColumnI18N();

    List<I18nDescribe> selectDictI18N();

    List<I18nDescribe> selectDictNameI18N();

    List<I18nDescribe> selectMenuI18N();

    List<I18nDescribe> selectReportI18N();

    List<I18nDescribe> selectOtherI18N();

    List<OrderByDescribe> selectOrderBy();

    List<Map<String, Object>> selectDataWithCondition(@Param("table") String paramString1,
            @Param("columns") Collection<ColumnDescribe> paramCollection, @Param("keys") List<ColumnValue> paramList1,
            @Param("where") String paramString2, @Param("orderBy") String[] paramArrayOfString,
            @Param("refs") List<ReferenceModel> paramList2, @Param("authCondition") String paramString3,
            @Param("distinct") boolean paramBoolean1,
            @Param("paging") DataTablePagingParameterModel paramDataTablePagingParameterModel,
            @Param("encryptStr") String paramString4, @Param("isDetail") boolean paramBoolean2);

    int selectDataWithConditionCount(@Param("table") String paramString1,
            @Param("columns") Collection<String> paramCollection, @Param("keys") List<ColumnValue> paramList1,
            @Param("where") String paramString2, @Param("orderBy") String[] paramArrayOfString,
            @Param("refs") List<ReferenceModel> paramList2, @Param("authCondition") String paramString3,
            @Param("distinct") boolean paramBoolean,
            @Param("paging") DataTablePagingParameterModel paramDataTablePagingParameterModel);

    List<Map<String, Object>> selectDataWithKeys(@Param("table") String paramString1,
            @Param("columns") Collection<ColumnDescribe> paramCollection,
            @Param("keys") List<DatabaseDataMap> paramList1, @Param("refs") List<ReferenceModel> paramList2,
            @Param("encryptStr") String paramString2);

    int selectCountWithCondition(@Param("table") String paramString1, @Param("keys") List<ColumnValue> paramList,
            @Param("where") String paramString2, @Param("authCondition") String paramString3);

    List<Map<String, Object>> filterInputSelect(@Param("table") String paramString1,
            @Param("columns") List<ColumnDescribe> paramList1, @Param("refs") List<ReferenceModel> paramList2,
            @Param("label_column") String paramString2, @Param("value_column") String paramString3,
            @Param("term") String paramString4, @Param("top_count") int paramInt, @Param("where") String paramString5);

    Map<String, Object> selectOldRecord(@Param("table") TableDescribe paramTableDescribe,
            @Param("keys") List<ColumnValue> paramList, @Param("where") String paramString);

    int updateRecord(@Param("table") String paramString1, @Param("values") List<ColumnValue> paramList1,
            @Param("keys") List<ColumnValue> paramList2, @Param("where") String paramString2,
            @Param("encryptStr") String paramString3);

    int updateBatchRecord(@Param("updateSql") List<String> paramList);

    int insertRecord(@Param("table") String paramString1, @Param("values") List<ColumnValue> paramList,
            @Param("encryptStr") String paramString2);

    int insertBatchRecord(@Param("sqls") List<String> paramList);

    int deleteRecords(@Param("table") String paramString, @Param("key") List<ColumnValue> paramList);

    @SelectProvider(type = cn.com.easyerp.core.dao.DxSqlProvider.class, method = "selectListFromCustomSql")
    List<Map<String, Object>> selectListFromCustomSql(Map<String, ?> paramMap);

    @SelectProvider(type = cn.com.easyerp.core.dao.DxSqlProvider.class, method = "selectRecordFromCustomSql")
    Map<String, Object> selectRecordFromCustomSql(Map<String, ?> paramMap);

    @SelectProvider(type = cn.com.easyerp.core.dao.DxSqlProvider.class, method = "sqlDynamic")
    List<Map<String, Object>> selectDynamicQueryList(Map<String, ?> paramMap);

    List<String> selectChildrenTable(@Param("table") String paramString);

    SystemParameter selectSystemParam();

    SystemParameter selectSystemParam_master();

    Map<String, Object> selectBizParam(@Param("table") TableDescribe paramTableDescribe);

    List<TableCheckRuleDescribe> selectTableCheckRules();

    List<OperationDescribe> selectTableOperations();

    List<OperationRuleModel> selectTableOperationRules();

    List<ComplexColumnDescribe> selectComplexColumn();

    List<ComplexColumnMapModel> selectComplexColumnMap();

    List<TriggerDescribe> selectTableTriggers();

    List<TableShortcutDescribe> selectTableShortcuts();

    @SuppressWarnings({ "rawtypes" })
    List<BatchDescribe> selectBatchDesc();

    @SuppressWarnings({ "rawtypes" })
    BatchDescribe selectBatchDescByTable(@Param("table") String paramString);

    int selectRecordCountByValues(@Param("table") String paramString, @Param("values") List<ColumnValue> paramList);

    int callApi(@Param("uuid") String paramString1, @Param("name") String paramString2,
            @Param("args") List<Object> paramList);

    int callApiTimeTask(@Param("uuid") String paramString1, @Param("name") String paramString2,
            @Param("args") List<Object> paramList, @Param("domain") String paramString3);

    int callBatchApi(@Param("apiSqls") List<String> paramList);

    void callStoredProcedure(@Param("name") String paramString, @Param("args") List<Object> paramList);

    List<ApiLog> selectApiLogs(@Param("uuid") String paramString);

    List<ApiLog> selectApiLogsTimeTask(@Param("uuid") String paramString1, @Param("domain") String paramString2);

    List<ApiLog> selectBatchApiLogs(@Param("sql") String paramString);

    List<AutoGenTableDesc> selectAutoGen();

    List<TableRenderModel> selectTableRender();

    List<LinkedHashMap<String, Object>> selectExportData(@Param("table") TableDescribe paramTableDescribe,
            @Param("keys") List<DatabaseDataMap> paramList);

    int updatePassword(@Param("uid") String paramString1, @Param("password") String paramString2,
            @Param("encryptStr") String paramString3);

    List<DatabaseDataMap> selectImportData(@Param("table") TableDescribe paramTableDescribe,
            @Param("uid") String paramString, @Param("refs") List<ReferenceModel> paramList);

    List<DatabaseDataMap> selectAllData(@Param("table") String paramString,
            @Param("refs") List<ReferenceModel> paramList);

    int insertImportData(@Param("table") String paramString, @Param("data") Map<String, ?> paramMap);

    int copyTableUsingTemplate(@Param("template") String paramString1, @Param("target") String paramString2);

    int dropTable(@Param("table") String paramString);

    int insertLog(@Param("id") String id, @Param("ts") Timestamp paramTimestamp, @Param("user_id") String paramString1,
            @Param("type") LogType paramLogType, @Param("target_id") String paramString2,
            @Param("context") String paramString3);

    List<Map<String, Object>> selectDistinctValue(@Param("table") TableDescribe paramTableDescribe,
            @Param("distinctColumns") List<String> paramList, @Param("where") String paramString);

    List<Map<String, Object>> selectViewGridValue(@Param("table") TableDescribe paramTableDescribe,
            @Param("where") String paramString);

    List<Map<String, Object>> execSql(@Param("sql") String paramString);

    List<ActionEventDescribe> selectActionEvent();

    List<ActionPrerequistieDescribe> selectActionPrerequistie();

    void updateActionLogCurrentNo(@Param("current_no") String paramString);

    List<AuthGroup> selectAuthGroup();

    List<AuthDataGroup> selectAuthDataGroup();

    List<AuthDataGroupDetail> selectAuthDataGroupDetail();

    List<MenuGroup> selectMenuGroup();

    List<UrlInterfaceDescribe> selectUrlInterface(@Param("domain") String paramString);

    List<DatabaseDataMap> selectAllDataWithCondition(@Param("table") String paramString1,
            @Param("columns") Collection<String> paramCollection, @Param("where") String paramString2);

    String selectDomainLanguage(@Param("dbName") String paramString1, @Param("domain") String paramString2);

    List<TableApproveEventDescribe> selectAllApproveEvent();

    List<Map<String, String>> selectRefChildTable(@Param("tableName") String paramString);

    List<String> getMobileMenuModule();

    List<Map<String, Object>> getSubMobileMenu(@Param("module") String paramString);

    List<Map<String, Object>> getMobileListGroup(@Param("table") TableDescribe paramTableDescribe);

    List<TimeTaskDescribe> selectTimeTask(@Param("domain") String paramString);

    List<TimeTaskSysTimeDescribe> selectTimeTaskSysTime(@Param("domain") String paramString);

    List<TimeTaskBusinessTimeDescribe> selectTimeTaskBusinessTime(@Param("domain") String paramString);

    List<TimeTaskEventDescribe> selectTimeTaskEvent(@Param("domain") String paramString);

    int selectCountBusiness(@Param("param") Map<String, Object> paramMap, @Param("table") String paramString1,
            @Param("column") String paramString2, @Param("domain") String paramString3);

    List<Map<String, Object>> selectBusiness(@Param("param") Map<String, Object> paramMap,
            @Param("table") String paramString1, @Param("columnName") String paramString2,
            @Param("refs") List<ReferenceModel> paramList);

    MessageTemplateModel getTemplateById(@Param("templateId") String paramString);

    Object execEvalSql(@Param("sql") String paramString);

    AuthDetails getUserById(@Param("id") String paramString1, @Param("domain") String paramString2);

    int insertTimeTaskLog(@Param("uuid") String paramString1, @Param("apiName") String paramString2,
            @Param("param") String paramString3, @Param("date") Date paramDate, @Param("domain") String paramString4);

    List<TimmingMessageModel> getTimingMessage();

    void execTimingResult(@Param("status") int paramInt1, @Param("result") int paramInt2,
            @Param("id") String paramString);

    String getId(@Param("tableName") String paramString1, @Param("parent_id") String paramString2);

    int setCodeStr(@Param("codeSt") CodeStrDescribe paramCodeStrDescribe);

    CodeStrDescribe getCodeStr(@Param("uuid") String paramString);

    List<DatabaseDataMap> execDynamicProcess(@Param("name") String paramString1, @Param("uuid") String paramString2);

    List<Map<String, Object>> selectDataByKey(@Param("tableName") String paramString,
            @Param("ids") Map<String, Object> paramMap);

    List<CalenderEventConfigDescribe> selectCalendarEventConfigs();
}
