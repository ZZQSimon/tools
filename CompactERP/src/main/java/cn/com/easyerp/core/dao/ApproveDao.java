package cn.com.easyerp.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.DepartmentDescribe;
import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.approve.ApproveBlockEventExec;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.approve.ApproveFlowNode;
import cn.com.easyerp.core.approve.ApproveFlowUser;
import cn.com.easyerp.core.approve.ApproveNodeToDb;
import cn.com.easyerp.core.approve.FlowBlock;
import cn.com.easyerp.core.approve.FlowBlockEditColumn;
import cn.com.easyerp.core.approve.FlowConditionDetail;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.approve.FlowLine;
import cn.com.easyerp.core.approve.GetApproveUserDescribe;
import cn.com.easyerp.core.approve.MyApproveAndWaitMeApprove;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.Table;
import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;

@Repository
public interface ApproveDao {
    List<Table> selectApproveTable();

    int addAuthGroup(@Param("authGroup") AuthGroup paramAuthGroup);

    List<AuthGroup> selectTemplate(@Param("type") int paramInt);

    List<FlowBlock> selectBlock(@Param("Block") FlowBlock paramFlowBlock);

    List<FlowEvent> selectEvent(@Param("block_id") String paramString);

    List<AuthGroup> selectApprover(@Param("AuthGroup") AuthGroup paramAuthGroup);

    List<FlowLine> selectLine(@Param("Block") FlowBlock paramFlowBlock, @Param("FlowLine") FlowLine paramFlowLine);

    List<FlowConditionDetail> selectLineDetail(@Param("Block") FlowBlock paramFlowBlock,
            @Param("FlowLine") FlowLine paramFlowLine,
            @Param("FlowConditionDetail") FlowConditionDetail paramFlowConditionDetail);

    int addBlock(@Param("Block") FlowBlock paramFlowBlock);

    int addApprover(@Param("AuthGroup") AuthGroup paramAuthGroup);

    int addLine(@Param("FlowLine") FlowLine paramFlowLine);

    int addFlowEvent(@Param("FlowEvent") FlowEvent paramFlowEvent);

    int addLineDetail(@Param("FlowConditionDetail") FlowConditionDetail paramFlowConditionDetail);

    int deleteBlock(@Param("Block") FlowBlock paramFlowBlock);

    int deleteApprover(@Param("Block") FlowBlock paramFlowBlock);

    int deleteLine(@Param("Block") FlowBlock paramFlowBlock);

    int deleteFlowConditionDetail(@Param("Block") FlowBlock paramFlowBlock);

    int selectSuperiorDept(@Param("deptId") String paramString1, @Param("userId") String paramString2,
            @Param("authGroup") List<AuthGroup> paramList);

    int deleteTemplate(@Param("authGroup") AuthGroup paramAuthGroup);

    int updateTemplate(@Param("authGroup") AuthGroup paramAuthGroup);

    int addApproveFlow(@Param("approveFlow") ApproveFlow paramApproveFlow);

    int addApproveFlowNode(@Param("approveFlowNode") ApproveFlowNode paramApproveFlowNode);

    int addApproveFlowUser(@Param("approveFlowUser") ApproveFlowUser paramApproveFlowUser);

    int addApproveFlowUsers(@Param("approveFlowUsers") List<ApproveFlowUser> paramList);

    int addApproveFlowNodes(@Param("approveFlowUser") ApproveFlowUser paramApproveFlowUser,
            @Param("belong_block") String paramString);

    int DeleteAddApproveFlowUser(@Param("approveId") String paramString1, @Param("block_id") String paramString2,
            @Param("sequence") int paramInt);

    int DeleteAddApproveFlowNodes(@Param("approveId") String paramString1, @Param("block_id") String paramString2,
            @Param("sequence") int paramInt);

    int updateIsAddApproveNade(@Param("approveId") String paramString1, @Param("blockId") String paramString2,
            @Param("is_addApproveNode") int paramInt);

    int updateApproveState(@Param("approveFlowUser") ApproveFlowUser paramApproveFlowUser,
            @Param("userId") String paramString);

    Integer getNeedAgreeCountPeople(@Param("table_id") String paramString1, @Param("block_id") String paramString2);

    int isLastNode(@Param("approveId") String paramString1, @Param("block_id") String paramString2);

    int getAlreadyAgreeCountPeople(@Param("block_id") String paramString1, @Param("approveId") String paramString2,
            @Param("sequence") int paramInt);

    int getAgreeCountPeople(@Param("block_id") String paramString1, @Param("approveId") String paramString2,
            @Param("sequence") int paramInt);

    int getActualNeedAgreeCountPeople(@Param("block_id") String paramString1, @Param("approveId") String paramString2,
            @Param("sequence") int paramInt);

    int getAddApproveAlreadyAgreeCountPeople(@Param("block_id") String paramString1,
            @Param("approveId") String paramString2, @Param("sequence") int paramInt);

    int updateApproveNodeState(@Param("approveFlowNode") ApproveFlowNode paramApproveFlowNode);

    int updateApproveFlowState(@Param("approveFlow") ApproveFlow paramApproveFlow);

    int updateColumnById(@Param("table_id") String paramString, @Param("data_id") Map<String, Object> paramMap1,
            @Param("column_value") Map<String, Object> paramMap2);

    int updateTableIsApproveState(@Param("table_id") String paramString1,
            @Param("is_approve_state") String paramString2);

    String getApproveId(@Param("table_id") String paramString1, @Param("data_id") String paramString2);

    ApproveFlow selectApproveByTable(@Param("table_id") String paramString1, @Param("data_id") String paramString2);

    int checkHasSubmitButton(@Param("table_id") String paramString1, @Param("data_id") String paramString2,
            @Param("maxSequence") Integer paramInteger);

    Integer getMaxSequence(@Param("table_id") String paramString1, @Param("data_id") String paramString2);

    Integer getSequenceByApproveId(@Param("approveId") String paramString);

    List<ApproveFlow> selectApproveFlow(@Param("approveFlow") ApproveFlow paramApproveFlow);

    List<ApproveFlowNode> selectApproveFlowNode(@Param("approveFlowNode") ApproveFlowNode paramApproveFlowNode);

    List<ApproveFlowUser> selectApproveFlowUser(@Param("approveFlowUser") ApproveFlowUser paramApproveFlowUser);

    String getId(@Param("tableName") String paramString1, @Param("parent_id") String paramString2);

    int saveApproveFlow(@Param("approve_id") String paramString1, @Param("table") String paramString2,
            @Param("someMining_id") String paramString3, @Param("date") Date paramDate);

    List<FlowBlock> selectBlocks(@Param("tableName") String paramString);

    List<FlowBlockEditColumn> selectBlockEditColumn();

    List<FlowLine> selectLines(@Param("tableName") String paramString);

    List<FlowConditionDetail> selectCondition(@Param("tableName") String paramString);

    FlowLine selectFirstLine(@Param("tableName") String paramString);

    int saveApproveNode(@Param("approve_id") String paramString1, @Param("block_id") String paramString2,
            @Param("seq") int paramInt1, @Param("maxSequence") int paramInt2, @Param("is_until_block") int paramInt3,
            @Param("is_approve_block") int paramInt4, @Param("belong_block") String paramString3);

    int saveApproveNodes(@Param("approveNodes") List<ApproveNodeToDb> paramList);

    List<Map<String, Object>> selectApproveUser(@Param("table_id") String paramString1,
            @Param("block_id") String paramString2, @Param("data_id") String paramString3);

    int selectCountApproveTable(@Param("tableName") String paramString);

    List<ApproveFlow> selectApproveByApproveIdAndDataId(@Param("approve_id") String paramString);

    List<ApproveFlowNode> selectApproveFlowNodeByApproveId(@Param("approve_id") String paramString);

    List<ApproveFlowUser> selectApproveFlowUserByApproveId(@Param("approve_id") String paramString);

    List<AuthDetails> selectApproveUsers(@Param("approve_id") String paramString);

    List<ApproveFlowUser> selectApproveUsersByBlockId(@Param("approveId") String paramString1,
            @Param("block_id") String paramString2, @Param("sequence") int paramInt);

    List<Map<String, Object>> selectBatchBlock(@Param("table_id") String paramString,
            @Param("data_ids") List<Object> paramList);

    List<ApproveFlowNode> selectBatchBlocks(@Param("table_id") String paramString1,
            @Param("user_id") String paramString2, @Param("id_column") String paramString3);

    AuthDetails selectUser(@Param("user_id") String paramString);

    List<AuthDetails> selectAllUser();

    List<DepartmentDescribe> selectAllDept();

    List<MyApproveAndWaitMeApprove> selectMyApprove(@Param("user_id") String paramString);

    List<MyApproveAndWaitMeApprove> waitMeApprove(@Param("user_id") String paramString);

    List<MyApproveAndWaitMeApprove> indexApprove(@Param("user_id") String paramString1,
            @Param("type") String paramString2);

    List<Map<String, Object>> selectDataById(@Param("tableName") String paramString,
            @Param("data") Map<String, Object> paramMap);

    int proc_approve(@Param("dataId") String paramString);

    Date selectApproveUserCreationTime(@Param("approveId") String paramString1, @Param("blockId") String paramString2,
            @Param("userId") String paramString3);

    String selectApproveStatus(@Param("approveId") String paramString1, @Param("blockId") String paramString2);

    Map<String, Object> selectCreUser(@Param("tableId") String paramString1, @Param("columnName") String paramString2,
            @Param("dataId") String paramString3);

    int selectIsAddApprove(@Param("approve_id") String paramString1, @Param("block_id") String paramString2,
            @Param("sequence") int paramInt, @Param("user_id") String paramString3);

    List<ApproveFlowUser> selectAllAddApproveUser(@Param("approve_id") String paramString1,
            @Param("block_id") String paramString2, @Param("sequence") int paramInt);

    Map<String, String> getEmailApprovalParam(@Param("table_id") String paramString1,
            @Param("data_id") String paramString2, @Param("user") String paramString3);

    List<Map<String, String>> getApprovers(@Param("approveId") String paramString1,
            @Param("blockId") String paramString2, @Param("domain") String paramString3);

    void addEmailApprove(@Param("email_approve_id") String paramString1, @Param("param") Map<String, String> paramMap,
            @Param("user") String paramString2);

    List<ApproveFlowUser> getRejectRecord(@Param("tableName") String paramString1, @Param("data") String paramString2);

    int takeBackApproveUser(@Param("approveId") String paramString1, @Param("blockId") String paramString2,
            @Param("user_Id") String paramString3, @Param("sequence") int paramInt);

    int takeBackApproveNode(@Param("approveId") String paramString1, @Param("blockId") String paramString2,
            @Param("sequence") int paramInt);

    ApproveFlowNode selectApproveFlowNodeByBlockId(@Param("approveId") String paramString1,
            @Param("blockId") String paramString2);

    List<ApproveFlowUser> selectNextBlockIdByFlowUser(@Param("approveId") String paramString1,
            @Param("blockId") String paramString2);

    I18nDescribe selectApproveNameI18N(@Param("approveName") String paramString);

    String selectParentUser(@Param("user_id") String paramString);

    int isApproveBack(@Param("approveId") String paramString);

    int hasApproveBackButton(@Param("approveId") String paramString);

    void deleteApproveFlowByApproveId(@Param("approveId") String paramString);

    void deleteApproveFlowNodeByApproveId(@Param("approveId") String paramString);

    void deleteApproveFlowUserByApproveId(@Param("approveId") String paramString);

    int ifAllow(@Param("approveId") String paramString);

    List<Map<String, String>> checkIsApprove(@Param("table_id") String paramString1,
            @Param("id_column") String paramString2, @Param("ids") List<Object> paramList1,
            @Param("approve_column") String paramString3, @Param("approve_status") List<String> paramList2);

    List<FlowEvent> selectApproveButtonEvent();

    List<ApproveBlockEvent> selectApproveBlockEvent();

    ApproveBlockEvent getBlockEvent(@Param("eventId") String paramString);

    Map<String, String> getStartNodeBlock(@Param("tableId") String paramString);

    int updateFlowEventIsExec(@Param("approve_id") String paramString1, @Param("block_id") String paramString2,
            @Param("flow_event_id") String paramString3, @Param("is_exec") int paramInt);

    List<ApproveBlockEventExec> selectApproveBlockIsExec(@Param("approve_id") String paramString);

    int addActionPrerequistie(@Param("actionPrerequistie") ActionPrerequistieDescribe paramActionPrerequistieDescribe);

    int addActionEvent(@Param("actionEvent") ActionEventDescribe paramActionEventDescribe);

    int deleteActionEvent(@Param("actionEvent") ActionEventDescribe paramActionEventDescribe);

    List<Map<String, Object>> getApproveDataByTableDataUser(@Param("table_id") String paramString1,
            @Param("data_id") Object paramObject, @Param("user_id") String paramString2);

    List<Map<String, Object>> getApproveDataByTableData(@Param("table_id") String paramString,
            @Param("data_id") Object paramObject);

    int hasStatus(@Param("approve_id") String paramString1, @Param("block_id") String paramString2,
            @Param("sequence") int paramInt, @Param("status") String paramString3);

    List<Map<String, String>> getApproversByCuiban(@Param("approve_id") String paramString1,
            @Param("state") String paramString2, @Param("domain") String paramString3);

    int deleteApproveBlockEvent(@Param("approveBlockEvent") ApproveBlockEvent paramApproveBlockEvent);

    int insertApproveBlockEvent(@Param("approveBlockEvent") ApproveBlockEvent paramApproveBlockEvent);

    List<AuthGroup> selectAuthGroup(@Param("tableName") String paramString1, @Param("blockId") String paramString2);

    List<AuthDetails> selectUsers();

    List<AuthDetails> selectUserById(@Param("userId") String paramString);

    List<AuthDetails> selectDeptRoleUsers(@Param("dept") String paramString1, @Param("role") String paramString2);

    List<AuthDetails> selectRoleUsers(@Param("role") String paramString);

    List<AuthDetails> selectDeptUsers(@Param("dept") String paramString);

    List<AuthDetails> selectDeptRela0Users(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectDeptRela0RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject,
            @Param("role") String paramString3);

    List<AuthDetails> selectDeptRela1Users(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectDeptRela1RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject,
            @Param("role") String paramString3);

    List<AuthDetails> selectDeptRela2Users(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectDeptRela2RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject,
            @Param("role") String paramString3);

    List<AuthDetails> selectUserRela0RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectUserRela1RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectUserRela2RoleUsers(@Param("tableName") String paramString1,
            @Param("idColumn") String paramString2, @Param("dataId") Object paramObject);

    List<AuthDetails> selectApproveUsersByDescribe(
            @Param("approveUserDescribes") List<GetApproveUserDescribe> paramList,
            @Param("tableName") String paramString1, @Param("idColumn") String paramString2,
            @Param("dataId") Object paramObject);

    String getEventidByBlockid(@Param("tableId") String paramString1, @Param("blockId") String paramString2);

    String getApproveByDataid(@Param("tableId") String paramString1, @Param("dataId") String paramString2);

    String getNextBlock(@Param("approveId") String paramString1, @Param("blockId") String paramString2);

    ApproveBlockEvent getBlockEventByType(@Param("tableId") String paramString1, @Param("blockId") String paramString2,
            @Param("event_type") String paramString3);

    Map<String, Object> getTableData(@Param("table") String paramString, @Param("idMap") Map<String, Object> paramMap);

    List<Map<String, Object>> checkBatchApproveSubmit(@Param("tableName") String paramString,
            @Param("ids") List<Object> paramList);

    Map<Object, Object> selectDataParam(@Param("tableId") String paramString1, @Param("id_column") String paramString2,
            @Param("dataId") String paramString3);
}
