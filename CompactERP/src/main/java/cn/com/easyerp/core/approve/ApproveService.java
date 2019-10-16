package cn.com.easyerp.core.approve;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableApproveEventDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.UrlInterfaceDescribe;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.mail.MailDescribe;
import cn.com.easyerp.core.mail.MailService;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.form.detail.DetailRequestModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.core.widget.message.MessageService;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.weixin.WeChatService;

@Service
public class ApproveService {
    // private static final String APPROVE_SEND_MESSAGE_PASS =
    // "approve_send_message_pass";
    // private static final String APPROVE_SEND_MESSAGE_PASS_TITLE =
    // "approve_send_message_pass_title";
    // private static final String APPROVE_SEND_MESSAGE_REJECT =
    // "approve_send_message_reject";
    // private static final String APPROVE_SEND_MESSAGE_REJECT_TITLE =
    // "approve_send_message_reject_title";
    // private static final String APPROVE_STATUS_KEY = "approve_status";
    // private static final String OWNER_KEY = "owner";
    // private static final String CRE_USER_KEY = "cre_user";
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private DataService dataService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MailService mailService;
    // @Autowired
    // private TableDeployService tableDeployService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    @Qualifier("serviceNumber")
    private WeChatService weChatService;
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private AuthDao authDao;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void saveApproveUser(final String tableName, final Map<String, Object> filter,
            final DetailRequestModel request) {
        final TableDescribe tableDesc = this.cacheService.getTableDesc(tableName);
        final String[] idColumns = tableDesc.getIdColumns();
        if (idColumns == null || idColumns.length != 1) {
            throw new ApplicationException("table " + tableDesc.getId() + " has more key");
        }
        Integer maxSequence = this.approveDao.getMaxSequence(tableName, filter.get(idColumns[0]).toString());
        if (maxSequence == null) {
            maxSequence = 1;
        }
        final String approve_id = this.approveDao.getId("c_approve_flow", "");
        this.approveDao.saveApproveFlow(approve_id, tableName, filter.get(idColumns[0]).toString(), new Date());
        final List<FlowBlock> block = (List<FlowBlock>) this.cacheService.getFlowBlock(tableName);
        final List<FlowLine> line = (List<FlowLine>) this.cacheService.getFlowLine(tableName);
        final List<FlowConditionDetail> condition = (List<FlowConditionDetail>) this.cacheService
                .getFlowCondition(tableName);
        final FlowLine firstLine = this.approveDao.selectFirstLine(tableName);
        final String page_target_id = firstLine.getPage_target_id();
        final List<Map<String, String>> approveNodes = new ArrayList<Map<String, String>>();
        this.recursion(approve_id, page_target_id, block, line, condition, filter, approveNodes, tableDesc);
        int num = 1;
        ++maxSequence;
        List<String> users = null;
        final List<ApproveNodeToDb> approveNodeToDbs = new ArrayList<ApproveNodeToDb>();
        final List<ApproveFlowUser> approveFlowUsers = new ArrayList<ApproveFlowUser>();
        for (final Map<String, String> nodeMap : approveNodes) {
            final Map<String, AuthDetails> approveUsers = this.selectApproveUser(tableName,
                    nodeMap.get("page_target_id"), filter.get(idColumns[0]).toString(), filter);
            users = this.buildUntilBlock(nodeMap.get("page_target_id"), block, filter, approveUsers,
                    (users == null || users.size() == 0) ? null : users.get(users.size() - 1));
            if (users != null) {
                for (int i = 0; i < users.size(); ++i) {
                    final String untilBlockId = Common.generateShortUuid();
                    final ApproveNodeToDb approveNodeToDb = new ApproveNodeToDb();
                    approveNodeToDb.setApprove_id(approve_id);
                    approveNodeToDb.setBlock_id(untilBlockId);
                    approveNodeToDb.setNum(num++);
                    approveNodeToDb.setMaxSequence(maxSequence);
                    approveNodeToDb.setIs_approve_block(1);
                    approveNodeToDb.setIs_until_block(1);
                    approveNodeToDb.setBelong_block(nodeMap.get("page_target_id"));
                    approveNodeToDbs.add(approveNodeToDb);
                    final ApproveFlowUser approveFlowUser = new ApproveFlowUser();
                    approveFlowUser.setApprove_id(approve_id);
                    approveFlowUser.setBlock_id(untilBlockId);
                    approveFlowUser.setSeq(0);
                    approveFlowUser.setSequence(maxSequence);
                    approveFlowUser.setUserId(users.get(i));
                    approveFlowUser.setState("wait");
                    approveFlowUsers.add(approveFlowUser);
                }
            } else {
                int is_approve_block = 0;
                if (nodeMap.get("is_approve_block") != null && "1".equals(nodeMap.get("is_approve_block").toString())) {
                    is_approve_block = 1;
                }
                if (approveUsers == null || approveUsers.size() == 0) {
                    continue;
                }
                final ApproveNodeToDb approveNodeToDb2 = new ApproveNodeToDb();
                approveNodeToDb2.setApprove_id(nodeMap.get("approve_id"));
                approveNodeToDb2.setBlock_id(nodeMap.get("page_target_id"));
                approveNodeToDb2.setNum(num++);
                approveNodeToDb2.setMaxSequence(maxSequence);
                approveNodeToDb2.setIs_approve_block(is_approve_block);
                approveNodeToDb2.setIs_until_block(0);
                approveNodeToDb2.setBelong_block(null);
                approveNodeToDbs.add(approveNodeToDb2);
                for (final Map.Entry<String, AuthDetails> user : approveUsers.entrySet()) {
                    final ApproveFlowUser approveFlowUser2 = new ApproveFlowUser();
                    approveFlowUser2.setApprove_id(nodeMap.get("approve_id"));
                    approveFlowUser2.setBlock_id(nodeMap.get("page_target_id"));
                    approveFlowUser2.setSeq(0);
                    approveFlowUser2.setSequence(maxSequence);
                    approveFlowUser2.setUserId(user.getKey());
                    approveFlowUser2.setState("wait");
                    approveFlowUser2.setUser(user.getValue());
                    approveFlowUsers.add(approveFlowUser2);
                }
            }
        }
        if (approveFlowUsers.size() != 0) {
            this.approveDao.addApproveFlowUsers((List) approveFlowUsers);
        }
        if (approveNodeToDbs.size() != 0) {
            this.approveDao.saveApproveNodes((List) approveNodeToDbs);
        }
        if (request != null) {
            final Map<String, String> param = new HashMap<String, String>();
            param.put("approveId", approve_id);
            param.put("tableId", tableName);
            param.put("dataId", filter.get(idColumns[0]).toString());
            param.put("blockId", approveNodes.get(0).get("page_target_id"));
            final ApproveBlockEvent event = request.getApproveBlockEvent().get("submitEvent");
            this.approveRemind(param, AuthService.getCurrentUser().getId(), event);
        }
    }

    public void recursion(final String approve_id, final String page_target_id, final List<FlowBlock> block,
            final List<FlowLine> line, final List<FlowConditionDetail> condition, final Map<String, Object> filter,
            final List<Map<String, String>> approveNodes, final TableDescribe tableDesc) {
        if (page_target_id == null) {
            return;
        }
        boolean isConditionNode = false;
        FlowBlock conditionBlock = null;
        FlowBlock nowBlock = null;
        for (final FlowBlock Block : block) {
            if (page_target_id.equals(Block.getBlock_id())) {
                if ("conditionNode".equals(Block.getType())) {
                    isConditionNode = true;
                    conditionBlock = Block;
                    break;
                }
                nowBlock = Block;
            }
        }
        if (isConditionNode) {
            final List<FlowLine> ifLine = new ArrayList<FlowLine>();
            for (final FlowLine Line : line) {
                if (page_target_id.equals(Line.getPage_source_id())) {
                    ifLine.add(Line);
                }
            }
            int ifLineCount = 0;
            boolean ifLineFlag = false;
            for (final FlowLine flowLine : ifLine) {
                ++ifLineCount;
                final List<FlowConditionDetail> ifCondition = new ArrayList<FlowConditionDetail>();
                for (final FlowConditionDetail flowCondition : condition) {
                    if (flowCondition.getConnection_id().equals(flowLine.getConnection_id())) {
                        ifCondition.add(flowCondition);
                    }
                }
                boolean isConditionFalg = true;
                for (final FlowConditionDetail flowConditionDetail : ifCondition) {
                    final String conditionValue = this.getConditionValue(filter, flowConditionDetail.getColumn_name());
                    if (!this.checkconditionValue(conditionValue, flowConditionDetail.getSymbol(),
                            flowConditionDetail.getValue(),
                            tableDesc.getColumn(flowConditionDetail.getColumn_name()))) {
                        isConditionFalg = false;
                    }
                    if (this.checkconditionValue(conditionValue, flowConditionDetail.getSymbol(),
                            flowConditionDetail.getValue(),
                            tableDesc.getColumn(flowConditionDetail.getColumn_name()))) {
                        ifLineFlag = true;
                    }
                }
                if (isConditionFalg) {
                    this.recursion(approve_id, flowLine.getPage_target_id(), block, line, condition, filter,
                            approveNodes, tableDesc);
                    break;
                }
            }
            if (ifLineCount == ifLine.size() && !ifLineFlag) {
                throw new ApplicationException("block " + conditionBlock.getText() + " has not success condition");
            }
        } else {
            boolean isEndNode = true;
            for (final FlowBlock flowBlock : block) {
                if (page_target_id.equals(flowBlock.getBlock_id()) && "endNode".equals(flowBlock.getType())) {
                    isEndNode = false;
                }
            }
            if (isEndNode) {
                final Map<String, String> nodeMap = new HashMap<String, String>();
                nodeMap.put("approve_id", approve_id);
                nodeMap.put("page_target_id", page_target_id);
                if (nowBlock != null) {
                    nodeMap.put("is_approve_block", nowBlock.getIs_approve_block() + "");
                }
                approveNodes.add(nodeMap);
            }
            FlowLine targetFlowLine = null;
            for (final FlowLine flowLine2 : line) {
                if (page_target_id.equals(flowLine2.getPage_source_id())) {
                    targetFlowLine = flowLine2;
                    break;
                }
            }
            if (targetFlowLine != null) {
                this.recursion(approve_id, targetFlowLine.getPage_target_id(), block, line, condition, filter,
                        approveNodes, tableDesc);
            }
        }
    }

    private String getConditionValue(final Map<String, Object> filter, final String column) {
        if ("cre_user".equals(column) || "upd_user".equals(column)) {
            return AuthService.getCurrentUserId();
        }
        return (filter.get(column) == null) ? "" : filter.get(column).toString();
    }

    public boolean checkconditionValue(String conditionValue, final String symbol, String value,
            final ColumnDescribe column) {
        if (Common.isBlank(conditionValue)) {
            conditionValue = "0";
        }
        if (Common.isBlank(value)) {
            value = "0";
        }
        final boolean flag = false;
        if ("=".equals(symbol)) {
            return conditionValue.equals(value);
        }
        if ("<".equals(symbol)) {
            if (column.getData_type() == 2) {
                return Integer.parseInt(conditionValue) < Integer.parseInt(value);
            }
            return column.getData_type() == 3 && Double.parseDouble(conditionValue) < Double.parseDouble(value);
        } else if (">".equals(symbol)) {
            if (column.getData_type() == 2) {
                return Integer.parseInt(conditionValue) > Integer.parseInt(value);
            }
            return column.getData_type() == 3 && Double.parseDouble(conditionValue) > Double.parseDouble(value);
        } else if ("<=".equals(symbol)) {
            if (column.getData_type() == 2) {
                return Integer.parseInt(conditionValue) <= Integer.parseInt(value);
            }
            return column.getData_type() == 3 && Double.parseDouble(conditionValue) <= Double.parseDouble(value);
        } else if (">=".equals(symbol)) {
            if (column.getData_type() == 2) {
                return Integer.parseInt(conditionValue) >= Integer.parseInt(value);
            }
            return column.getData_type() == 3 && Double.parseDouble(conditionValue) >= Double.parseDouble(value);
        } else {
            if (!"!=".equals(symbol)) {
                return flag;
            }
            if (column.getData_type() == 2) {
                return Integer.parseInt(conditionValue) != Integer.parseInt(value);
            }
            if (column.getData_type() == 3) {
                return Double.parseDouble(conditionValue) != Double.parseDouble(value);
            }
            return !conditionValue.equals(value);
        }
    }

    private List<String> buildUntilBlock(final String blockId, final List<FlowBlock> blocks,
            final Map<String, Object> filter, final Map<String, AuthDetails> approveUsers, final String beforeUser) {
        if (blocks == null) {
            return null;
        }
        String owner = "";
        final List<String> users = new ArrayList<String>();
        for (final FlowBlock flowBlock : blocks) {
            if (blockId.equals(flowBlock.getBlock_id()) && flowBlock.getIs_until_block() == 1) {
                if (beforeUser != null) {
                    owner = beforeUser;
                } else {
                    if (filter.get("owner") == null || "".equals(filter.get("owner"))) {
                        if (filter.get("cre_user") == null) {
                            return users;
                        }
                        owner = filter.get("cre_user").toString();
                    } else {
                        owner = filter.get("owner").toString();
                    }
                    if (owner == null || "".equals(owner)) {
                        owner = AuthService.getCurrentUserId();
                    }
                }
                if (approveUsers.containsKey(owner)) {
                    return users;
                }
                this.recursionUntilBlock(approveUsers, owner, users, 1);
                return users;
            }
        }
        return null;
    }

    private void recursionUntilBlock(final Map<String, AuthDetails> approveUsers, final String owner,
            final List<String> users, int count) {
        if (approveUsers == null || approveUsers.size() == 0) {
            throw new ApplicationException("no until user");
        }
        if (++count > 20) {
            throw new ApplicationException("user tree is bad");
        }
        final String parentUser = this.approveDao.selectParentUser(owner);
        if (parentUser == null || "".equals(parentUser)) {
            throw new ApplicationException("until user not found");
        }
        for (final Map.Entry<String, AuthDetails> user : approveUsers.entrySet()) {
            if (parentUser.equals(user.getKey())) {
                users.add(parentUser);
                return;
            }
        }
        users.add(parentUser);
        this.recursionUntilBlock(approveUsers, parentUser, users, count);
    }

    @SuppressWarnings("unused")
    private Object paseValue(final ColumnDescribe column, final String value, final String conditionValue,
            final String symbol) {
        switch (column.getData_type()) {
        default: {
            return null;
        }
        }
    }

    public Map<String, Object> getBatchApprove(final String tableName, final AuthDetails user) {
        final TableDescribe table = this.cacheService.getTableDesc(tableName);
        if (table.getIdColumns() == null || table.getIdColumns().length != 1) {
            throw new ApplicationException("table: " + tableName + " has bad id column");
        }
        final List<ApproveFlowNode> approveFlowNodes = (List<ApproveFlowNode>) this.approveDao
                .selectBatchBlocks(tableName, user.getId(), table.getIdColumns()[0]);
        final FlowBlock flowBlock = new FlowBlock();
        flowBlock.setTable_id(tableName);
        final List<FlowBlock> flowBlocks = (List<FlowBlock>) this.approveDao.selectBlock(flowBlock);
        final Map<String, FlowBlock> blockMap = new HashMap<String, FlowBlock>();
        for (final FlowBlock block : flowBlocks) {
            blockMap.put(block.getBlock_id(), block);
        }
        final Map<String, I18nDescribe> nodesI18N = new HashMap<String, I18nDescribe>();
        final Map<String, List<ApproveFlowNode>> nodeList = new HashMap<String, List<ApproveFlowNode>>();
        for (final ApproveFlowNode node : approveFlowNodes) {
            if (null != node.getBelong_block() && !"".equals(node.getBelong_block())) {
                if (!blockMap.containsKey(node.getBelong_block())) {
                    continue;
                }
                if (!nodesI18N.containsKey(node.getBelong_block())) {
                    nodesI18N.put(node.getBelong_block(),
                            this.cacheService.getMsgI18n(blockMap.get(node.getBelong_block()).getApprove_name()));
                }
                if (null == nodeList.get(node.getBelong_block())) {
                    nodeList.put(node.getBelong_block(), new ArrayList<ApproveFlowNode>());
                }
                nodeList.get(node.getBelong_block()).add(node);
            } else {
                if (!blockMap.containsKey(node.getBlock_id())) {
                    continue;
                }
                if (!nodesI18N.containsKey(node.getBlock_id())) {
                    nodesI18N.put(node.getBlock_id(),
                            this.cacheService.getMsgI18n(blockMap.get(node.getBlock_id()).getApprove_name()));
                }
                if (null == nodeList.get(node.getBlock_id())) {
                    nodeList.put(node.getBlock_id(), new ArrayList<ApproveFlowNode>());
                }
                nodeList.get(node.getBlock_id()).add(node);
            }
        }
        final Map<String, Object> nodes = new HashMap<String, Object>();
        nodes.put("nodesI18N", nodesI18N);
        nodes.put("nodeList", nodeList);
        return nodes;
    }

    public ApproveFlow selectDetailApprove(final String table_id, final String data_id) {
        final ApproveFlow approveFlow = this.approveDao.selectApproveByTable(table_id, data_id);
        if (approveFlow == null) {
            return null;
        }
        final String approveId = approveFlow.getApprove_id();
        final String userId = AuthService.getCurrentUserId();
        final List<ApproveFlowNode> approveFlowNodes = (List<ApproveFlowNode>) this.approveDao
                .selectApproveFlowNodeByApproveId(approveId);
        final Map<String, AuthDetails> approveUsers = new HashMap<String, AuthDetails>();
        for (final AuthDetails user : this.approveDao.selectApproveUsers(approveId)) {
            approveUsers.put(user.getId(), user);
        }
        ApproveFlowNode beforeNode = null;
        int nodeCount = 0;
        boolean firstWaitBlock = true;
        final List<ApproveFlowNode> vmNodes = new ArrayList<ApproveFlowNode>();
        final Map<String, List<ApproveFlowUser>> blockApproveAllUsersMap = new HashMap<String, List<ApproveFlowUser>>();
        final List<ApproveFlowUser> blockApproveAllUsers = (List<ApproveFlowUser>) this.approveDao
                .selectApproveUsersByBlockId(approveId, (String) null, 0);
        if (blockApproveAllUsers != null) {
            for (final ApproveFlowUser flowUser : blockApproveAllUsers) {
                if (blockApproveAllUsersMap.get(flowUser.getBlock_id()) == null) {
                    blockApproveAllUsersMap.put(flowUser.getBlock_id(), new ArrayList<ApproveFlowUser>());
                }
                blockApproveAllUsersMap.get(flowUser.getBlock_id()).add(flowUser);
            }
        }
        for (final ApproveFlowNode approveFlowNode : approveFlowNodes) {
            final List<ApproveFlowUser> blockApproveUsers = blockApproveAllUsersMap.get(approveFlowNode.getBlock_id());
            if (blockApproveUsers == null) {
                continue;
            }
            boolean hasBackFlag = true;
            final List<ApproveFlowUser> approveFlowBlockUsers = new ArrayList<ApproveFlowUser>();
            for (int i = 0; i < blockApproveUsers.size(); ++i) {
                if ("wait".equals(approveFlowNode.getState()) || !"wait".equals(blockApproveUsers.get(i).getState())) {
                    final ApproveFlowUser approveUser = new ApproveFlowUser();
                    approveUser.setUser(approveUsers.get(blockApproveUsers.get(i).getUserId()));
                    approveUser.setApprove_id(approveFlow.getApprove_id());
                    approveUser.setBlock_id(approveFlowNode.getBlock_id());
                    approveUser.setRemark(
                            (blockApproveUsers.get(i).getRemark() == null) ? "" : blockApproveUsers.get(i).getRemark());
                    approveUser.setState(blockApproveUsers.get(i).getState());
                    approveUser.setUserId(blockApproveUsers.get(i).getUserId());
                    approveUser.setFlow_event_id(blockApproveUsers.get(i).getFlow_event_id());
                    approveUser.setCreation_time(blockApproveUsers.get(i).getCreation_time());
                    if ("termination".equals(blockApproveUsers.get(i).getState())
                            || "reject".equals(blockApproveUsers.get(i).getState())
                            || "hold".equals(blockApproveUsers.get(i).getState())) {
                        hasBackFlag = false;
                    }
                    if ("wait".equals(approveFlowNode.getState())
                            && (null == beforeNode || !"hold".equals(beforeNode.getState())) && firstWaitBlock
                            && userId.equals(blockApproveUsers.get(i).getUserId())) {
                        approveUser.setHasApproveButton(true);
                    }
                    approveFlowBlockUsers.add(approveUser);
                }
            }
            if ("wait".equals(approveFlowNode.getState())) {
                firstWaitBlock = false;
            }
            if (beforeNode != null && hasBackFlag && "wait".equals(approveFlowNode.getState())) {
                final List<ApproveFlowUser> approveFlowUsers = beforeNode.getApproveFlowUsers();
                if (approveFlowUsers != null) {
                    for (final ApproveFlowUser user2 : approveFlowUsers) {
                        if (userId.equals(user2.getUserId()) && ("agree".equals(user2.getState())
                                || "hold".equals(user2.getState()) || "disagree".equals(user2.getState()))) {
                            user2.setTakeBack(true);
                        }
                    }
                }
            }
            if ("wait".equals(approveFlowNode.getState()) && nodeCount++ == approveFlowNodes.size() - 1) {
                for (final ApproveFlowUser user3 : approveFlowBlockUsers) {
                    if (userId.equals(user3.getUserId())
                            && ("finish".equals(user3.getState()) || "hold".equals(user3.getState()))) {
                        user3.setTakeBack(true);
                    }
                }
            }
            beforeNode = approveFlowNode;
            approveFlowNode.setApproveFlowUsers(approveFlowBlockUsers);
            if ("reject".equals(approveFlowNode.getState())) {
                approveFlow.setApproveOver("over");
            }
            vmNodes.add(approveFlowNode);
            if ("reject".equals(approveFlowNode.getState())) {
                break;
            }
            if ("termination".equals(approveFlowNode.getState())) {
                break;
            }
        }
        if ("finish".equals(approveFlowNodes.get(approveFlowNodes.size() - 1).getState())) {
            approveFlow.setApproveOver("over");
        }
        approveFlow.setApproveFlowNodes(vmNodes);
        this.buildApproveBlockIsExec(approveFlow);
        return approveFlow;
    }

    private void buildApproveBlockIsExec(final ApproveFlow approveFlow) {
        final List<ApproveBlockEventExec> approveBlockEventExecs = (List<ApproveBlockEventExec>) this.approveDao
                .selectApproveBlockIsExec(approveFlow.getApprove_id());
        if (null != approveBlockEventExecs) {
            for (final ApproveBlockEventExec approveBlockEventExec : approveBlockEventExecs) {
                approveFlow.addBlockEventIsExec(approveBlockEventExec);
            }
        }
    }

    public List<MyApproveAndWaitMeApprove> selectMyApprove(final AuthDetails user) {
        final List<MyApproveAndWaitMeApprove> myApproveAndWaitMeApproves = (List<MyApproveAndWaitMeApprove>) this.approveDao
                .indexApprove(user.getId(), "2");
        return myApproveAndWaitMeApproves;
    }

    public List<MyApproveAndWaitMeApprove> selectWaitMeApprove(final AuthDetails user) {
        final List<MyApproveAndWaitMeApprove> myApproveAndWaitMeApproves = (List<MyApproveAndWaitMeApprove>) this.approveDao
                .indexApprove(user.getId(), "1");
        return myApproveAndWaitMeApproves;
    }

    public void approveApi(final FlowEvent flowEvent) {
        if (null == flowEvent || null == flowEvent.getEvent()) {
            return;
        }
        final Map<String, ActionEventDescribe> events = flowEvent.getEvent();
        for (final Map.Entry<String, ActionEventDescribe> event : events.entrySet()) {
            final UrlInterfaceDescribe urlInterface = this.cacheService
                    .getUrlInterface(event.getValue().getEvent_name());
            if (urlInterface == null) {
                continue;
            }
            switch (urlInterface.getType()) {
            case 1: {
                continue;
            }
            case 2: {
                this.apiService.execApi(event.getValue().getEvent_name(), event.getValue().getRequestParam());
                continue;
            }
            case 3: {
                this.apiService.execApi(event.getValue().getEvent_name(), event.getValue().getRequestParam());
            }
            case 4: {
                continue;
            }
            }
        }
    }

    public void approveApi(final ApproveBlockEvent approveBlockEvent) {
        if (null == approveBlockEvent || null == approveBlockEvent.getEvent()) {
            return;
        }
        final Map<String, ActionEventDescribe> events = approveBlockEvent.getEvent();
        for (final Map.Entry<String, ActionEventDescribe> event : events.entrySet()) {
            final UrlInterfaceDescribe urlInterface = this.cacheService
                    .getUrlInterface(event.getValue().getEvent_name());
            if (urlInterface == null) {
                continue;
            }
            switch (urlInterface.getType()) {
            case 1: {
                continue;
            }
            case 2: {
                this.apiService.execApi(event.getValue().getEvent_name(), event.getValue().getRequestParam());
                continue;
            }
            case 3: {
                this.apiService.execApi(event.getValue().getEvent_name(), event.getValue().getRequestParam());
                continue;
            }
            case 4: {
                this.apiService.execApi(event.getValue().getEvent_name(), event.getValue().getRequestParam());
                continue;
            }
            }
        }
    }

    public void approveLastApi(final List<TableApproveEventDescribe> approveEvent) {
        if (approveEvent == null || approveEvent.size() == 0) {
            return;
        }
        for (final TableApproveEventDescribe event : approveEvent) {
            final UrlInterfaceDescribe urlInterface = this.cacheService.getUrlInterface(event.getEvent_id());
            if (urlInterface == null) {
                continue;
            }
            switch (urlInterface.getType()) {
            case 1: {
                continue;
            }
            case 2: {
                this.apiService.execApi(event.getEvent_id(), event.getRequestParam());
                continue;
            }
            case 3: {
                this.apiService.execApi(event.getEvent_id(), event.getRequestParam());
            }
            case 4: {
                continue;
            }
            }
        }
    }

    public void approvePass(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user) {
        if (!this.approveBlockState(approveId, blockId)) {
            throw new ApplicationException("approve status is change");
        }
        final ApproveFlowUser approveFlowUser = new ApproveFlowUser();
        Integer sequence = this.approveDao.getSequenceByApproveId(approveId);
        if (sequence == null) {
            sequence = 1;
        }
        if (this.approveDao.selectCountApproveTable(tableId) == 0) {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            if (FlowEvent.EventType.AGREE_EVENT.equals(flowEvent.getEvent_type())
                    || FlowEvent.EventType.FINISH_EVENT.equals(flowEvent.getEvent_type())) {
                approveFlowUser.setState(ApproveFlowUser.State.AGREE);
            } else if (FlowEvent.EventType.DISAGREE_EVENT.equals(flowEvent.getEvent_type())) {
                approveFlowUser.setState(ApproveFlowUser.State.DISAGREE);
            }
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, (String) null);
        } else {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setUser(user);
            if (FlowEvent.EventType.AGREE_EVENT.equals(flowEvent.getEvent_type())
                    || FlowEvent.EventType.FINISH_EVENT.equals(flowEvent.getEvent_type())) {
                approveFlowUser.setState(ApproveFlowUser.State.AGREE);
            } else if (FlowEvent.EventType.DISAGREE_EVENT.equals(flowEvent.getEvent_type())) {
                approveFlowUser.setState(ApproveFlowUser.State.DISAGREE);
            }
            approveFlowUser.setRemark(approveReason);
            approveFlowUser.setCreation_time(new Date());
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setIs_default_approve(1);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        }
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", flowEvent.getEvent_type());
        final ApproveBlockEvent event = (approveBlockEvent == null)
                ? this.approveDao.getBlockEventByType(tableId, blockId, "agreeEvent")
                : approveBlockEvent.get("agreeEvent");
        Integer needCount = this.approveDao.getNeedAgreeCountPeople(tableId, blockId);
        if (needCount == null) {
            needCount = 0;
        }
        final int alresdyCount = this.approveDao.getAlreadyAgreeCountPeople(blockId, approveId, (int) sequence);
        final int agreeCount = this.approveDao.getAgreeCountPeople(blockId, approveId, (int) sequence);
        final int actualCount = this.approveDao.getActualNeedAgreeCountPeople(blockId, approveId, (int) sequence);
        boolean isFinish = false;
        if (needCount == 0) {
            if (actualCount == agreeCount) {
                final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
                approveFlowNode.setApprove_id(approveId);
                approveFlowNode.setBlock_id(blockId);
                approveFlowNode.setState(ApproveFlowNode.State.FINISH);
                this.approveDao.updateApproveNodeState(approveFlowNode);
                isFinish = true;
                if (this.approveDao.isLastNode(approveId, blockId) != 0) {
                    final ApproveFlow approveFlow = new ApproveFlow();
                    approveFlow.setApprove_id(approveId);
                    approveFlow.setTable_id(tableId);
                    approveFlow.setState(4);
                    approveFlow.setEnd_time(new Date());
                    this.approveDao.updateApproveFlowState(approveFlow);
                    this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.FINISH);
                    this.approveLastApi(approveEvent);
                    data.put("approveReason", approveReason);
                    this.approveResultRemind(data, user.getId(), event);
                } else {
                    this.approveRemind(data, user.getId(), event);
                }
                if (null != approveBlockEvent) {
                    this.approveApi(approveBlockEvent.get(ApproveBlockEvent.EventType.AGREE_EVENT));
                }
            } else {
                this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.PROGRESS);
            }
        } else if (needCount <= agreeCount) {
            final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
            approveFlowNode.setApprove_id(approveId);
            approveFlowNode.setBlock_id(blockId);
            approveFlowNode.setState(ApproveFlowNode.State.FINISH);
            this.approveDao.updateApproveNodeState(approveFlowNode);
            isFinish = true;
            if (this.approveDao.isLastNode(approveId, blockId) != 0) {
                final ApproveFlow approveFlow = new ApproveFlow();
                approveFlow.setApprove_id(approveId);
                approveFlow.setTable_id(tableId);
                approveFlow.setState(4);
                approveFlow.setEnd_time(new Date());
                this.approveDao.updateApproveFlowState(approveFlow);
                this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.FINISH);
                this.approveLastApi(approveEvent);
                data.put("approveReason", approveReason);
                this.approveResultRemind(data, user.getId(), event);
            } else {
                this.approveRemind(data, user.getId(), event);
            }
            if (null != approveBlockEvent) {
                this.approveApi(approveBlockEvent.get(ApproveBlockEvent.EventType.AGREE_EVENT));
            }
        } else {
            this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.PROGRESS);
        }
        this.holdAndNoPass(approveId, blockId, tableId, approveReason, dataId, sequence, flowEvent, approveBlockEvent,
                approveEvent, user, isFinish, actualCount, alresdyCount);
        this.approveApi(flowEvent);
    }

    public void approveReject(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user) {
        if (!this.approveBlockState(approveId, blockId)) {
            throw new ApplicationException("approve status is change");
        }
        final ApproveFlowUser approveFlowUser = new ApproveFlowUser();
        Integer sequence = this.approveDao.getSequenceByApproveId(approveId);
        if (sequence == null) {
            sequence = 1;
        }
        if (this.approveDao.selectCountApproveTable(tableId) == 0) {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setState(ApproveFlowUser.State.REJECT);
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        } else {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setUser(user);
            approveFlowUser.setState(ApproveFlowUser.State.REJECT);
            approveFlowUser.setRemark(approveReason);
            approveFlowUser.setCreation_time(new Date());
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setIs_default_approve(1);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        }
        final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
        approveFlowNode.setApprove_id(approveId);
        approveFlowNode.setBlock_id(blockId);
        approveFlowNode.setState(ApproveFlowNode.State.REJECT);
        this.approveDao.updateApproveNodeState(approveFlowNode);
        final ApproveFlow approveFlow = new ApproveFlow();
        approveFlow.setApprove_id(approveId);
        approveFlow.setTable_id(tableId);
        approveFlow.setState(3);
        approveFlow.setEnd_time(new Date());
        this.approveDao.updateApproveFlowState(approveFlow);
        this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.REJECT);
        if (null != approveBlockEvent) {
            this.approveApi(approveBlockEvent.get(ApproveBlockEvent.EventType.REJECT_EVENT));
        }
        this.approveApi(flowEvent);
        this.approveLastApi(approveEvent);
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", approveBlockEvent.get("rejectEvent").getEvent_type());
        data.put("approveReason", approveReason);
        final ApproveBlockEvent event = (approveBlockEvent == null)
                ? this.approveDao.getBlockEventByType(tableId, blockId, "rejectEvent")
                : approveBlockEvent.get("rejectEvent");
        this.approveResultRemind(data, user.getId(), event);
    }

    public void approveDisagree(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user) {
        this.approvePass(approveId, blockId, tableId, approveReason, dataId, flowEvent, approveBlockEvent, approveEvent,
                user);
    }

    public void approveHold(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user) {
        if (!this.approveBlockState(approveId, blockId)) {
            throw new ApplicationException("approve status is change");
        }
        final ApproveFlowUser approveFlowUser = new ApproveFlowUser();
        Integer sequence = this.approveDao.getSequenceByApproveId(approveId);
        if (sequence == null) {
            sequence = 1;
        }
        if (this.approveDao.selectCountApproveTable(tableId) == 0) {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setState(ApproveFlowUser.State.HOLD);
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, (String) null);
        } else {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setUser(user);
            approveFlowUser.setState(ApproveFlowUser.State.HOLD);
            approveFlowUser.setRemark(approveReason);
            approveFlowUser.setCreation_time(new Date());
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setIs_default_approve(1);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        }
        final int alresdyCount = this.approveDao.getAlreadyAgreeCountPeople(blockId, approveId, (int) sequence);
        final int actualCount = this.approveDao.getActualNeedAgreeCountPeople(blockId, approveId, (int) sequence);
        if (actualCount == alresdyCount) {
            this.holdAndNoPass(approveId, blockId, tableId, approveReason, dataId, sequence, flowEvent,
                    approveBlockEvent, approveEvent, user, false, actualCount, alresdyCount);
        } else {
            this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.PROGRESS);
        }
        this.approveApi(flowEvent);
    }

    public void approveWorkNode(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user, final boolean isFinish) {
        if (!isFinish) {
            this.approveApi(flowEvent);
            if (1 != flowEvent.getIs_exec()) {
                this.approveDao.updateFlowEventIsExec(approveId, blockId, flowEvent.getFlow_event_id(), 1);
            }
        } else {
            this.approvePass(approveId, blockId, tableId, approveReason, dataId, flowEvent, approveBlockEvent,
                    approveEvent, user);
        }
    }

    public void approveTermination(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user) {
        if (!this.approveBlockState(approveId, blockId)) {
            throw new ApplicationException("approve status is change");
        }
        final ApproveFlowUser approveFlowUser = new ApproveFlowUser();
        Integer sequence = this.approveDao.getSequenceByApproveId(approveId);
        if (sequence == null) {
            sequence = 1;
        }
        if (this.approveDao.selectCountApproveTable(tableId) == 0) {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setState(ApproveFlowUser.State.TERMINATION);
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        } else {
            approveFlowUser.setApprove_id(approveId);
            approveFlowUser.setBlock_id(blockId);
            approveFlowUser.setUser(user);
            approveFlowUser.setState(ApproveFlowUser.State.TERMINATION);
            approveFlowUser.setRemark(approveReason);
            approveFlowUser.setCreation_time(new Date());
            approveFlowUser.setSequence(sequence);
            approveFlowUser.setIs_default_approve(1);
            approveFlowUser.setFlow_event_id(flowEvent.getFlow_event_id());
            this.approveDao.updateApproveState(approveFlowUser, user.getId());
        }
        final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
        approveFlowNode.setApprove_id(approveId);
        approveFlowNode.setBlock_id(blockId);
        approveFlowNode.setState(ApproveFlowNode.State.TERMINATION);
        this.approveDao.updateApproveNodeState(approveFlowNode);
        final ApproveFlow approveFlow = new ApproveFlow();
        approveFlow.setApprove_id(approveId);
        approveFlow.setTable_id(tableId);
        approveFlow.setState(7);
        approveFlow.setEnd_time(new Date());
        this.approveDao.updateApproveFlowState(approveFlow);
        this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.TERMINATION);
        if (null != approveBlockEvent) {
            this.approveApi(approveBlockEvent.get(ApproveBlockEvent.EventType.TERMINATION_EVENT));
        }
        this.approveApi(flowEvent);
        this.approveLastApi(approveEvent);
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", "terminationEvent");
        data.put("approveReason", approveReason);
        this.approveResultRemind(data, user.getId(), approveBlockEvent.get("terminationEvent"));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateApproveStatus(final String tableId, final Object dataId, final String type) {
        final TableDescribe table = this.cacheService.getTableDesc(tableId);
        if (table == null) {
            return;
        }
        final String[] idColumns = table.getIdColumns();
        if (idColumns == null) {
            return;
        }
        final Map<String, Object> idMap = new HashMap<String, Object>();
        idMap.put(idColumns[0], dataId);
        final Map<String, Object> valueMap = new HashMap<String, Object>();
        valueMap.put("approve_status", type);
        this.approveDao.updateColumnById(tableId, (Map) idMap, (Map) valueMap);
    }

    private void holdAndNoPass(final String approveId, final String blockId, final String tableId,
            final String approveReason, final Object dataId, final int sequence, final FlowEvent flowEvent,
            final Map<String, ApproveBlockEvent> approveBlockEvent, final List<TableApproveEventDescribe> approveEvent,
            final AuthDetails user, final boolean isFinish, final int actualCount, final int alresdyCount) {
        final int hold = this.approveDao.hasStatus(approveId, blockId, sequence, "hold");
        if (!isFinish && actualCount == alresdyCount && 0 != hold) {
            final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
            approveFlowNode.setApprove_id(approveId);
            approveFlowNode.setBlock_id(blockId);
            approveFlowNode.setState(ApproveFlowNode.State.HOLD);
            this.approveDao.updateApproveNodeState(approveFlowNode);
            if (this.approveDao.isLastNode(approveId, blockId) != 0) {
                final ApproveFlow approveFlow = new ApproveFlow();
                approveFlow.setApprove_id(approveId);
                approveFlow.setTable_id(tableId);
                approveFlow.setState(6);
                approveFlow.setEnd_time(new Date());
                this.approveDao.updateApproveFlowState(approveFlow);
                this.approveLastApi(approveEvent);
            }
            this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.HOLD);
        } else if (!isFinish && actualCount == alresdyCount && 0 == hold) {
            final FlowBlock block = this.cacheService.getBlock(tableId, blockId);
            if (null != block && null != block.getNotPassType()) {
                final String notPassType = block.getNotPassType();
                switch (notPassType) {
                case "pass": {
                    this.passApprove(approveId, blockId, tableId, dataId, approveBlockEvent);
                    break;
                }
                case "reject": {
                    this.rejectApprove(approveId, blockId, tableId, dataId, approveBlockEvent);
                    break;
                }
                case "termination": {
                    this.terminationApprove(approveId, blockId, tableId, dataId, approveBlockEvent);
                    break;
                }
                }
            }
        }
    }

    private void passApprove(final String approveId, final String blockId, final String tableId, final Object dataId,
            final Map<String, ApproveBlockEvent> approveBlockEvent) {
        final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
        approveFlowNode.setApprove_id(approveId);
        approveFlowNode.setBlock_id(blockId);
        approveFlowNode.setState(ApproveFlowNode.State.FINISH);
        this.approveDao.updateApproveNodeState(approveFlowNode);
        // final ApproveBlockEvent flowEvent = approveBlockEvent.get("");
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", "pass");
        if (this.approveDao.isLastNode(approveId, blockId) != 0) {
            final ApproveFlow approveFlow = new ApproveFlow();
            approveFlow.setApprove_id(approveId);
            approveFlow.setTable_id(tableId);
            approveFlow.setState(4);
            approveFlow.setEnd_time(new Date());
            this.approveDao.updateApproveFlowState(approveFlow);
            this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.FINISH);
            data.put("approveReason", "");
            this.approveResultRemind(data, AuthService.getCurrentUserId(), approveBlockEvent.get("agreeEvent"));
        } else {
            this.approveRemind(data, AuthService.getCurrentUserId(), approveBlockEvent.get("agreeEvent"));
        }
        if (null != approveBlockEvent) {
            this.approveApi(approveBlockEvent.get("agreeEvent"));
        }
    }

    private void rejectApprove(final String approveId, final String blockId, final String tableId, final Object dataId,
            final Map<String, ApproveBlockEvent> approveBlockEvent) {
        final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
        approveFlowNode.setApprove_id(approveId);
        approveFlowNode.setBlock_id(blockId);
        approveFlowNode.setState(ApproveFlowNode.State.REJECT);
        this.approveDao.updateApproveNodeState(approveFlowNode);
        final ApproveFlow approveFlow = new ApproveFlow();
        approveFlow.setApprove_id(approveId);
        approveFlow.setTable_id(tableId);
        approveFlow.setState(3);
        approveFlow.setEnd_time(new Date());
        this.approveDao.updateApproveFlowState(approveFlow);
        this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.REJECT);
        if (null != approveBlockEvent) {
            this.approveApi(approveBlockEvent.get("rejectEvent"));
        }
        final ApproveBlockEvent flowEvent = approveBlockEvent.get("");
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", flowEvent.getEvent_type());
        data.put("approveReason", "");
        this.approveResultRemind(data, AuthService.getCurrentUserId(), approveBlockEvent.get("rejectEvent"));
    }

    private void terminationApprove(final String approveId, final String blockId, final String tableId,
            final Object dataId, final Map<String, ApproveBlockEvent> approveBlockEvent) {
        final ApproveFlowNode approveFlowNode = new ApproveFlowNode();
        approveFlowNode.setApprove_id(approveId);
        approveFlowNode.setBlock_id(blockId);
        approveFlowNode.setState(ApproveFlowNode.State.TERMINATION);
        this.approveDao.updateApproveNodeState(approveFlowNode);
        final ApproveFlow approveFlow = new ApproveFlow();
        approveFlow.setApprove_id(approveId);
        approveFlow.setTable_id(tableId);
        approveFlow.setState(7);
        approveFlow.setEnd_time(new Date());
        this.approveDao.updateApproveFlowState(approveFlow);
        this.updateApproveStatus(tableId, dataId, ApproveFlow.ApproveStatus.TERMINATION);
        if (null != approveBlockEvent) {
            this.approveApi(approveBlockEvent.get("terminationEvent"));
        }
        final ApproveBlockEvent flowEvent = approveBlockEvent.get("");
        final Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", approveId);
        data.put("blockId", blockId);
        data.put("tableId", tableId);
        data.put("dataId", dataId.toString());
        data.put("type", flowEvent.getEvent_type());
        data.put("approveReason", "");
        this.approveResultRemind(data, AuthService.getCurrentUserId(), approveBlockEvent.get("terminationEvent"));
    }

    public boolean approveBlockState(final String approveId, final String blockId) {
        final String approveStatus = this.approveDao.selectApproveStatus(approveId, blockId);
        return "wait".equals(approveStatus);
    }

    public void approveResultRemind(final Map<String, String> data, final String userId,
            final ApproveBlockEvent event) {
        if (event == null) {
            return;
        }
        final AuthDetails user = this.authDao.selectUserByid(userId);
        final TableDescribe tableDesc = this.cacheService.getTableDesc((String) data.get("tableId"));
        if (tableDesc == null || tableDesc.getIdColumns().length != 1) {
            throw new ApplicationException("table error");
        }
        final Map<String, Object> selectCreUser = (Map<String, Object>) this.approveDao
                .selectCreUser((String) data.get("tableId"), tableDesc.getIdColumns()[0], (String) data.get("dataId"));
        if (selectCreUser == null || selectCreUser.get("cre_user") == null) {
            return;
        }
        final String tableName = this.dataService.i18nString(tableDesc.getI18n());
        final Map<String, String> param = new HashMap<String, String>();
        param.put("first", "\u5ba1\u6279\u7ed3\u679c!");
        param.put("keyword1", tableName);
        param.put("keyword2", data.get("dataId"));
        param.put("keyword3", this.dataService.getMessageText((String) data.get("type"), new Object[0]));
        param.put("keyword4", data.get("approveReason"));
        param.put("keyword5", user.getName());
        param.put("keyword6", selectCreUser.get("upd_date").toString());
        param.put("remark", "\u70b9\u51fb \u67e5\u770b\u8be6\u60c5>>");
        final Map<String, Object> emailDetail = new HashMap<String, Object>();
        final Map<String, Object> detailParam = new HashMap<String, Object>();
        detailParam.put(tableDesc.getIdColumns()[0], data.get("dataId"));
        emailDetail.put("domain", this.dxRoutingDataSource.getDomainKey());
        emailDetail.put("table", data.get("tableId"));
        emailDetail.put("param", detailParam);
        final String domain = this.dxRoutingDataSource.getDomainKey();
        final AuthDetails applicant = this.systemDao.getUserById(selectCreUser.get("cre_user").toString(), domain);
        final Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                ApproveService.this.dxRoutingDataSource.setActiveDomainKey(domain);
                if (1 == event.getEmail() && event != null) {
                    ApproveService.this.approveEmailRemind(param, applicant, emailDetail);
                }
                if (1 == event.getSms() && event != null) {
                    ApproveService.this.approveWeixinRemind(param, applicant, "");
                }
                ApproveService.this.approveSysMsgRemid(param, applicant, data.get("tableId"), data.get("dataId"));
            }
        });
        td.start();
    }

    public void approveRemind(final Map<String, String> data, final String userId, final ApproveBlockEvent event) {
        if (event == null) {
            return;
        }
        final AuthDetails user = this.authDao.selectUserByid(userId);
        final TableDescribe tableDesc = this.cacheService.getTableDesc((String) data.get("tableId"));
        if (tableDesc == null || tableDesc.getIdColumns().length != 1) {
            throw new ApplicationException("table error");
        }
        final Map<String, Object> selectCreUser = (Map<String, Object>) this.approveDao
                .selectCreUser((String) data.get("tableId"), tableDesc.getIdColumns()[0], (String) data.get("dataId"));
        if (selectCreUser == null || selectCreUser.get("cre_user") == null) {
            return;
        }
        final String tableName = this.dataService.i18nString(tableDesc.getI18n());
        final String domain = this.dxRoutingDataSource.getDomainKey();
        final List<Map<String, String>> approvers;
        if ("submitEvent".equals(event.getEvent_type())) {
            approvers = (List<Map<String, String>>) this.approveDao.getApprovers((String) data.get("approveId"),
                    (String) data.get("blockId"), domain);
        } else {
            final String blockId = this.approveDao.getNextBlock((String) data.get("approveId"),
                    (String) data.get("blockId"));
            approvers = (List<Map<String, String>>) this.approveDao.getApprovers((String) data.get("approveId"),
                    blockId, domain);
        }
        final AuthDetails applicant = this.systemDao.getUserById(selectCreUser.get("cre_user").toString(),
                this.dxRoutingDataSource.getDomainKey());
        final Map<String, String> param = new HashMap<String, String>();
        param.put("first", "{name},\u60a8\u597d!\u60a8\u6709\u4e00\u4e2a\u65b0\u7684\u5f85\u5ba1\u6279\u4e8b\u9879!");
        param.put("keyword1", tableName);
        param.put("keyword2", data.get("dataId"));
        param.put("keyword3", selectCreUser.get("upd_date").toString());
        param.put("keyword4", (applicant.getName() == null) ? "" : applicant.getName());
        param.put("keyword5", (user.getDepartment_id() == null) ? "" : user.getDepartment_id());
        param.put("remark", "\u8bf7\u53ca\u65f6\u70b9\u51fb\u672c\u6d88\u606f\u8fdb\u884c\u5ba1\u6279");
        final Thread td = new Thread(new Runnable() {
            @Override
            public void run() {
                ApproveService.this.dxRoutingDataSource.setActiveDomainKey(domain);
                if (1 == event.getEmail() && event != null) {
                    final Map<String, Object> emailDetail = new HashMap<String, Object>();
                    final Map<String, Object> detailParam = new HashMap<String, Object>();
                    detailParam.put(tableDesc.getIdColumns()[0], data.get("dataId"));
                    emailDetail.put("domain", domain);
                    emailDetail.put("table", data.get("tableId"));
                    emailDetail.put("param", detailParam);
                    ApproveService.this.approveEmailRemind(param, approvers, emailDetail);
                }
                if (1 == event.getSms() && event != null) {
                    final String baseParam = "&table=" + data.get("tableId") + "&dataid=" + data.get("dataId")
                            + "&domain=" + domain;
                    ApproveService.this.approveWeixinRemind(param, approvers, baseParam);
                }
                ApproveService.this.approveSysMsgRemid(param, approvers, data.get("tableId"), data.get("dataId"));
            }
        });
        td.start();
    }

    public void approveCuibanRemind(final Map<String, String> data, final String userId) {
        final AuthDetails user = this.authDao.selectUserByid(userId);
        final TableDescribe tableDesc = this.cacheService.getTableDesc((String) data.get("tableId"));
        if (tableDesc == null || tableDesc.getIdColumns().length != 1) {
            throw new ApplicationException("table error");
        }
        final Map<String, Object> selectCreUser = (Map<String, Object>) this.approveDao
                .selectCreUser((String) data.get("tableId"), tableDesc.getIdColumns()[0], (String) data.get("dataId"));
        if (selectCreUser == null || selectCreUser.get("cre_user") == null) {
            return;
        }
        final String tableName = this.dataService.i18nString(tableDesc.getI18n());
        List<Map<String, String>> approvers = null;
        approvers = (List<Map<String, String>>) this.approveDao.getApproversByCuiban((String) data.get("approveId"),
                "hold", this.dxRoutingDataSource.getDomainKey());
        if (approvers.size() == 0) {
            approvers = (List<Map<String, String>>) this.approveDao.getApproversByCuiban((String) data.get("approveId"),
                    "wait", this.dxRoutingDataSource.getDomainKey());
        }
        final Map<String, String> param = new HashMap<String, String>();
        param.put("first", "\u60a8\u6536\u5230\u4e00\u4e2a\u5ba1\u6279\u50ac\u529e\u63d0\u9192!");
        param.put("keyword1", tableName);
        param.put("keyword2", data.get("dataId"));
        param.put("keyword3", user.getName());
        param.put("keyword4", Common.defaultDateTimeFormat.format(new Date()));
        param.put("remark", "\u70b9\u51fb\u8be6\u60c5\u53ef\u5feb\u901f\u529e\u7406");
        final String baseParam = "&table=" + data.get("tableId") + "&dataid=" + data.get("dataId");
        this.WeixinCuibanRemind(param, approvers, baseParam);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void approveWeixinRemind(final Map<String, String> param, final List<Map<String, String>> approvers,
            final String baseParam) {
        final Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
        for (final Map.Entry<String, String> entry : param.entrySet()) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", entry.getValue());
            datas.put(entry.getKey(), data);
        }
        for (final Map<String, String> approver : approvers) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", approver.get("name")
                    + ",\u60a8\u597d!\u60a8\u6709\u4e00\u4e2a\u65b0\u7684\u5f85\u5ba1\u6279\u4e8b\u9879!");
            datas.put("first", data);
            final String urlParam = "?openid=" + approver.get("openid") + baseParam;
            this.weChatService.sendTextMessage("5", (String) approver.get("id"), (Map) datas, urlParam);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void approveWeixinRemind(final Map<String, String> param, final AuthDetails applicant,
            final String baseParam) {
        final Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
        for (final Map.Entry<String, String> entry : param.entrySet()) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", entry.getValue());
            datas.put(entry.getKey(), data);
        }
        this.weChatService.sendTextMessage("4", applicant.getId(), (Map) datas, "");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void WeixinCuibanRemind(final Map<String, String> param, final List<Map<String, String>> approvers,
            final String baseParam) {
        final Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
        for (final Map.Entry<String, String> entry : param.entrySet()) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", entry.getValue());
            datas.put(entry.getKey(), data);
        }
        for (final Map<String, String> approver : approvers) {
            final String urlParam = "?openid=" + approver.get("openid") + baseParam;
            this.weChatService.sendTextMessage("6", (String) approver.get("id"), (Map) datas, urlParam);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void approveSysMsgRemid(final Map<String, String> param, final AuthDetails applicant, final String tableId,
            final String dataId) {
        final String url = "<a href='#' class='link-approve'  table-id='" + tableId + "' data-id='" + dataId + "')'>"
                + param.get("remark") + "</a>";
        param.put("remark", url);
        final Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
        for (final Map.Entry<String, String> entry : param.entrySet()) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", entry.getValue());
            datas.put(entry.getKey(), data);
        }
        this.messageService.sendTextMessage("4", applicant.getId(), (Map) datas, "");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void approveSysMsgRemid(final Map<String, String> param, final List<Map<String, String>> approvers,
            final String tableId, final String dataId) {
        final String url = "<a href='#' class='link-approve'  table-id='" + tableId + "' data-id='" + dataId + "')'>"
                + param.get("remark") + "</a>";
        param.put("remark", url);
        final Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
        for (final Map.Entry<String, String> entry : param.entrySet()) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", entry.getValue());
            datas.put(entry.getKey(), data);
        }
        for (final Map<String, String> approver : approvers) {
            final Map<String, Object> data = new HashMap<String, Object>();
            data.put("value", approver.get("name")
                    + ",\u60a8\u597d!\u60a8\u6709\u4e00\u4e2a\u65b0\u7684\u5f85\u5ba1\u6279\u4e8b\u9879!");
            datas.put("first", data);
            this.messageService.sendTextMessage("5", (String) approver.get("id"), (Map) datas, url);
        }
    }

    public void approveEmailRemind(final Map<String, String> param, final AuthDetails applicant,
            final Map<String, Object> emailDetail) {
        String FileContent = "";
        try (final FileInputStream fis = new FileInputStream(
                this.dataService.getRootPath() + "/WEB-INF/velocity/ApproveResultTemplate.html");
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                final BufferedReader br = new BufferedReader(isr);) {

            String line = null;
            while ((line = br.readLine()) != null) {
                FileContent += line;
                FileContent += "\r\n";
            }
            FileContent = FileContent.replace("${first}", param.get("first"));
            FileContent = FileContent.replace("${title}", param.get("keyword1"));
            FileContent = FileContent.replace("${data_id}", param.get("keyword2"));
            FileContent = FileContent.replace("${result}", param.get("keyword3"));
            FileContent = FileContent.replace("${reason}", param.get("keyword4"));
            FileContent = FileContent.replace("${approver}", param.get("keyword5"));
            FileContent = FileContent.replace("${date}", param.get("keyword6"));
            final MailDescribe mail = new MailDescribe("\u5ba1\u6279\u7ed3\u679c\u63d0\u9192", FileContent);
            mail.setRecipients(applicant.getEmail());
            mail.setCC("");
            this.mailService.sendHtmlEmail(mail);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void approveEmailRemind(final Map<String, String> param, final List<Map<String, String>> approvers,
            final Map<String, Object> emailDetail) {
        String FileContent = "";
        try (final FileInputStream fis = new FileInputStream(
                this.dataService.getRootPath() + "/WEB-INF/velocity/ApproveRemindTemplate.html");
                final InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
                final BufferedReader br = new BufferedReader(isr);) {

            String line = null;
            while ((line = br.readLine()) != null) {
                FileContent += line;
                FileContent += "\r\n";
            }
            FileContent = FileContent.replace("${first}", param.get("first"));
            FileContent = FileContent.replace("${title}", param.get("keyword1"));
            FileContent = FileContent.replace("${data_id}", param.get("keyword2"));
            FileContent = FileContent.replace("${cre_date}", param.get("keyword3"));
            FileContent = FileContent.replace("${applicant}", param.get("keyword4"));
            FileContent = FileContent.replace("${department}", param.get("keyword5"));
            for (final Map<String, String> recipient : approvers) {
                emailDetail.put("user_name", recipient.get("id"));
                emailDetail.put("pwd", this.dataService.getDecode((String) recipient.get("_password")));
                final ObjectMapper mapper = new ObjectMapper();
                String paramJson;
                try {
                    paramJson = mapper.writeValueAsString((Object) emailDetail);
                } catch (JsonProcessingException e2) {
                    throw new ApplicationException("Json conversion exception");
                }
                FileContent = FileContent.replace("${url}",
                        this.dataService.getProjectUrl() + "?emailDetail=" + this.dataService.getEncryption(paramJson));
                FileContent = FileContent.replace("{name}", recipient.get("name"));
                final MailDescribe mail = new MailDescribe("\u5ba1\u6279\u63d0\u9192", FileContent);
                mail.setRecipients((String) recipient.get("email"));
                mail.setCC("");
                this.mailService.sendHtmlEmail(mail);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, String> getStartNodeBlock(final String tableId) {
        return (Map<String, String>) this.approveDao.getStartNodeBlock(tableId);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Map<String, AuthDetails> selectApproveUser(final String tableName, final String blockId,
            final Object dataId, final Map<String, Object> filter) {
        final TableDescribe table = this.cacheService.getTableDesc(tableName);
        if (null == table || null == table.getIdColumns() || table.getIdColumns().length == 0) {
            return null;
        }
        final String idColumn = table.getIdColumns()[0];
        final List<AuthGroup> authGroups = (List<AuthGroup>) this.cacheService.getApproveUser(tableName, blockId);
        if (null == authGroups) {
            return null;
        }
        final List<GetApproveUserDescribe> approveUserDescribes = this.approveUserDescribes(authGroups);
        List<AuthDetails> users = new ArrayList<AuthDetails>();
        if (approveUserDescribes != null) {
            if (approveUserDescribes.size() != 0) {
                users = (List<AuthDetails>) this.approveDao.selectApproveUsersByDescribe((List) approveUserDescribes,
                        tableName, idColumn, dataId);
            }
        }
        for (final AuthGroup authGroup : authGroups) {
            if (authGroup.getDepartment() != null && authGroup.getDepartment().indexOf(".") != -1) {
                final String refDept = this.selectRef(table, authGroup.getDepartment(), filter);
                if ("*".equals(authGroup.getRole())) {
                    users.addAll(this.approveDao.selectRoleUsers(refDept));
                } else if (authGroup.getRole().indexOf(".") != -1) {
                    final String refRole = this.selectRef(table, authGroup.getRole(), filter);
                    users.addAll(this.approveDao.selectDeptRoleUsers(refDept, refRole));
                } else {
                    users.addAll(this.approveDao.selectDeptRoleUsers(refDept, authGroup.getRole()));
                }
            }
        }
        if (null == users) {
            return null;
        }
        final Map<String, AuthDetails> resultUsers = new HashMap<String, AuthDetails>();
        for (final AuthDetails user : users) {
            resultUsers.put(user.getId(), user);
        }
        return resultUsers;
    }

    private List<GetApproveUserDescribe> approveUserDescribes(final List<AuthGroup> authGroups) {
        if (authGroups == null) {
            return null;
        }
        List<GetApproveUserDescribe> users = new ArrayList<GetApproveUserDescribe>();
        for (final AuthGroup authGroup : authGroups) {
            if ("*".equals(authGroup.getUser())
                    || ("*".equals(authGroup.getDepartment()) && "*".equals(authGroup.getRole()))) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(1);
                users = new ArrayList<GetApproveUserDescribe>();
                users.add(user);
                break;
            }
            if (!Common.isBlank(authGroup.getDepartment())) {
                if (authGroup.getDepartment().indexOf(".") != -1) {
                    continue;
                }
                if (authGroup.getRole().indexOf(".") != -1) {
                    continue;
                }
            }
            if (!Common.isBlank(authGroup.getDepartment()) && !Common.isBlank(authGroup.getRole())
                    && !"*".equals(authGroup.getDepartment()) && !"*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(2);
                user.setDept(authGroup.getDepartment());
                user.setRole(authGroup.getRole());
                users.add(user);
            }
            if ("*".equals(authGroup.getDepartment()) && !"*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(4);
                user.setRole(authGroup.getRole());
                users.add(user);
            }
            if (!"*".equals(authGroup.getDepartment()) && "*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(3);
                user.setDept(authGroup.getDepartment());
                users.add(user);
            }
            if (!Common.isBlank(authGroup.getUser())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(8);
                user.setUser(authGroup.getUser());
                users.add(user);
            }
            if (Common.isBlank(authGroup.getDepartment_relation()) && Common.isBlank(authGroup.getUser_relation())) {
                continue;
            }
            if ("0".equals(authGroup.getDepartment_relation()) && !"*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(5);
                user.setDept_rela(0);
                user.setRole(authGroup.getRole());
                users.add(user);
            } else if ("0".equals(authGroup.getDepartment_relation()) && "*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(6);
                user.setDept_rela(0);
                users.add(user);
            } else if ("1".equals(authGroup.getDepartment_relation()) && !"*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(5);
                user.setDept_rela(1);
                user.setRole(authGroup.getRole());
                users.add(user);
            } else if ("1".equals(authGroup.getDepartment_relation()) && "*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(6);
                user.setDept_rela(1);
                users.add(user);
            } else if ("2".equals(authGroup.getDepartment_relation()) && !"*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(5);
                user.setDept_rela(2);
                user.setRole(authGroup.getRole());
                users.add(user);
            } else if ("2".equals(authGroup.getDepartment_relation()) && "*".equals(authGroup.getRole())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(6);
                user.setDept_rela(2);
                users.add(user);
            }
            if ("0".equals(authGroup.getUser_relation())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(7);
                user.setUser_rela(0);
                users.add(user);
            } else if ("1".equals(authGroup.getUser_relation())) {
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(7);
                user.setUser_rela(1);
                users.add(user);
            } else {
                if (!"2".equals(authGroup.getUser_relation())) {
                    continue;
                }
                final GetApproveUserDescribe user = new GetApproveUserDescribe();
                user.setType(7);
                user.setUser_rela(2);
                users.add(user);
            }
        }
        return users;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String selectRef(final TableDescribe table, final String refStr, final Map<String, Object> filter) {
        final String[] refColumnStr = refStr.split("\\.");
        final ColumnDescribe refColumn = table.getColumn(refColumnStr[0]);
        final Object refDataId = filter.get(refColumnStr[0]);
        if (refColumn == null || refDataId == null) {
            throw new ApplicationException("column: " + refColumnStr[0] + " does not exists!");
        }
        if (refColumn.getRef_table_name() == null || "".equals(refColumn.getRef_table_name())) {
            throw new ApplicationException("column: " + refColumnStr[0] + " has no ref table!");
        }
        final TableDescribe refTable = this.cacheService.getTableDesc(refColumn.getRef_table_name());
        if (refTable == null) {
            throw new ApplicationException("ref table: " + refColumnStr[0] + " does not exists!");
        }
        final String[] refIdColumns = refTable.getIdColumns();
        if (refIdColumns == null || refIdColumns.length == 0) {
            throw new ApplicationException("ref table: " + refColumnStr[0] + " has no id column!");
        }
        final Map<String, Object> idMap = new HashMap<String, Object>();
        idMap.put(refIdColumns[0], refDataId);
        final Map<String, Object> refTableData = (Map<String, Object>) this.approveDao.getTableData(refTable.getId(),
                (Map) idMap);
        if (refTableData == null || refTableData.get(refColumnStr[1]) == null) {
            throw new ApplicationException("ref data does not exists");
        }
        return refTableData.get(refColumnStr[1]).toString();
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ActionResult checkBatchApproveSubmit(final String tableName, final List<String> recordIds) {
        final TableDescribe tableDesc = this.dataService.getTableDesc(tableName);
        if (1 != tableDesc.getIs_approve() || null == recordIds || null == tableDesc.getIdColumns()
                || 1 != tableDesc.getIdColumns().length) {
            throw new ApplicationException("can not submit approve");
        }
        final List<Object> ids = new ArrayList<Object>();
        for (final String id : recordIds) {
            final RecordModel record = (RecordModel) ViewService.fetchWidgetModel(id);
            final Object value = record.getFieldMap().get(tableDesc.getIdColumns()[0]).getValue();
            ids.add(value);
        }
        final List<Map<String, Object>> map = (List<Map<String, Object>>) this.approveDao
                .checkBatchApproveSubmit(tableName, (List) ids);
        if (map != null && map.size() != 0) {
            final String messageText = this.dataService.getMessageText("data can not be submit",
                    new Object[] { map.get(0).get("data_id") });
            throw new ApplicationException(messageText);
        }
        return new ActionResult(true, (Object) true);
    }

    public ActionResult batchApproveSubmit(final String tableName, final Map<String, FlowEvent> batchFlowEvents) {
        final TableDescribe table = this.cacheService.getTableDesc(tableName);
        for (final Map.Entry<String, FlowEvent> entry : batchFlowEvents.entrySet()) {
            final RecordModel record = (RecordModel) ViewService.fetchWidgetModel((String) entry.getKey());
            final Map<String, Object> data = new HashMap<String, Object>();
            final List<FieldModelBase> fields = (List<FieldModelBase>) record.getFields();
            for (int i = 0; i < fields.size(); ++i) {
                data.put(fields.get(i).getColumn(), fields.get(i).getValue());
            }
            this.saveApproveUser(tableName, data, null);
            this.updateApproveStatus(table.getId(), data.get(table.getIdColumns()[0]), "2");
            if (null != entry.getValue()) {
                this.approveApi(entry.getValue());
            }
        }
        return new ActionResult(true, (Object) "success");
    }
}
