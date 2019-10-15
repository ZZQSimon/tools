package cn.com.easyerp.core.approve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.Table;
import cn.com.easyerp.core.cache.ActionEventDescribe;
import cn.com.easyerp.core.cache.ActionPrerequistieDescribe;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.evaluate.FormulaService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/approve" })
public class ApproveController extends FormViewControllerBase {
    private static final String APPROVE_SEND_MESSAGE_PASS = "approve_send_message_pass";
    private static final String APPROVE_SEND_MESSAGE_PASS_TITLE = "approve_send_message_pass_title";
    private static final String APPROVE_SEND_MESSAGE_REJECT = "approve_send_message_reject";
    private static final String APPROVE_SEND_MESSAGE_REJECT_TITLE = "approve_send_message_reject_title";
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private CacheService cacheService;

    @RequestMapping({ "/approve.view" })
    public ModelAndView approve(@RequestBody ApproveRequestModel request) {
        ApproveModel form = new ApproveModel(request.getParent());
        List<Table> approveTable = this.approveDao.selectApproveTable();
        for (Table listtable : approveTable) {
            I18nDescribe tableI18n = this.cacheService.getTableI18n(listtable.getId());
            String i18nString = this.dataService.i18nString(tableI18n);
            if (tableI18n != null) {
                if (i18nString == null) {
                    listtable.setName("");
                    continue;
                }
                listtable.setName(i18nString);
            }
        }

        form.setApproveTable(approveTable);
        return buildModelAndView(form);
    }

    @Autowired
    private DataService dataService;
    @Autowired
    private ApproveService approveService;
    @Autowired
    private TableDeployDao tableDeployDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private ViewService viewService;
    @Autowired
    private FormulaService formulaService;

    @ResponseBody
    @RequestMapping({ "/selectApprove.do" })
    public ActionResult selectApprove(@RequestBody ApproveRequestModel request) {
        ApproveModel form = new ApproveModel(request.getParent());
        String tableId = request.getTableId();
        FlowBlock flowBlock = new FlowBlock();
        flowBlock.setTable_id(tableId);
        AuthGroup authGroup = new AuthGroup();
        authGroup.setTable(tableId);
        FlowLine flowLine = new FlowLine();
        FlowConditionDetail flowConditionDetail = new FlowConditionDetail();
        List<FlowBlock> approveBlock = this.approveDao.selectBlock(flowBlock);

        for (int i = 0; i < approveBlock.size(); i++) {
            I18nDescribe approveNameI18N = this.approveDao
                    .selectApproveNameI18N(((FlowBlock) approveBlock.get(i)).getApprove_name());
            ((FlowBlock) approveBlock.get(i)).setApprove_name_I18N(approveNameI18N);
        }
        List<List<AuthGroup>> authgroups = new ArrayList<List<AuthGroup>>();
        String block = "";
        List<AuthGroup> authgroup = null;
        int count = 0;
        List<AuthGroup> selectApprover = this.approveDao.selectApprover(authGroup);
        for (int i = 0; i < selectApprover.size(); i++) {
            if (!block.equals(((AuthGroup) selectApprover.get(i)).getBlock())) {
                block = ((AuthGroup) selectApprover.get(i)).getBlock();
                count = 0;
                if (authgroup != null) {
                    authgroups.add(authgroup);
                }
            }
            if (count == 0) {
                authgroup = new ArrayList<AuthGroup>();
                authgroup.add(selectApprover.get(i));
                count++;
            } else {
                authgroup.add(selectApprover.get(i));
                count++;
            }
            if (i == selectApprover.size() - 1) {
                authgroups.add(authgroup);
            }
        }

        List<FlowLine> approveLine = this.approveDao.selectLine(flowBlock, flowLine);
        List<FlowConditionDetail> approveFlowConditionDetail = this.approveDao.selectLineDetail(flowBlock, flowLine,
                flowConditionDetail);

        Approve approve = new Approve();
        approve.setFlowBlock(approveBlock);
        approve.setAuthGroup(authgroups);
        approve.setFlowLine(approveLine);
        approve.setFlowConditionDetail(approveFlowConditionDetail);
        approve.setApproveButtonEvent(this.cacheService.getTableDesc(tableId).getApproveButtonEvent());
        approve.setApproveBlockEvent(this.cacheService.getTableDesc(tableId).getApproveBlockEvent());
        approve.setInitApproveButtonEvent(this.cacheService.getTableDesc(tableId).getApproveButtonEvent());
        approve.setInitApproveBlockEvent(this.cacheService.getTableDesc(tableId).getApproveBlockEvent());

        List<ColumnDescribe> coulmns = this.cacheService.getTableDesc(tableId).getColumns();
        List<TreeNode> treeData = deptOrUserTree(coulmns);
        form.setDeptOrUserTree(treeData);

        form.setApprove(approve);
        return new ActionResult(true, form);
    }

    public List<TreeNode> deptOrUserTree(List<ColumnDescribe> coulmns) {
        List<TreeNode> treeData = new ArrayList<TreeNode>();
        for (int i = 0; i < coulmns.size(); i++) {
            TreeNode node = new TreeNode();
            node.setParent("#");
            node.setId(((ColumnDescribe) coulmns.get(i)).getColumn_name() + "%"
                    + ((ColumnDescribe) coulmns.get(i)).getTable_id() + i);
            I18nDescribe I18n = this.cacheService
                    .getMsgI18n(((ColumnDescribe) coulmns.get(i)).getInternational_id().toLowerCase());
            if (I18n != null) {
                node.setText(this.dataService.i18nString(I18n));
            } else {
                node.setText(((ColumnDescribe) coulmns.get(i)).getColumn_name());
            }
            treeData.add(node);
            if (!"".equals(((ColumnDescribe) coulmns.get(i)).getRef_table_name())
                    && ((ColumnDescribe) coulmns.get(i)).getRef_table_name() != null) {
                List<ColumnDescribe> ref_coulmns = this.cacheService
                        .getTableDesc(((ColumnDescribe) coulmns.get(i)).getRef_table_name()).getColumns();
                for (int j = 0; j < ref_coulmns.size(); j++) {
                    TreeNode nodes = new TreeNode();
                    nodes.setParent(((ColumnDescribe) coulmns.get(i)).getColumn_name() + "%"
                            + ((ColumnDescribe) coulmns.get(i)).getTable_id() + i);
                    nodes.setId(((ColumnDescribe) ref_coulmns.get(j)).getColumn_name() + "%"
                            + ((ColumnDescribe) coulmns.get(i)).getRef_table_name() + i);
                    I18nDescribe I18ns = this.cacheService
                            .getMsgI18n(((ColumnDescribe) ref_coulmns.get(j)).getInternational_id().toLowerCase());
                    if (I18ns != null) {
                        nodes.setText(this.dataService.i18nString(I18ns));
                    } else {
                        nodes.setText(((ColumnDescribe) coulmns.get(j)).getColumn_name());
                    }
                    treeData.add(nodes);
                }
            }
        }
        return treeData;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @RequestMapping({ "/saveApprove.do" })
    public ActionResult saveApprove(@RequestBody ApproveRequestModel request) {
        String result = "保存成功";
        if (request.getFlowBlock().size() <= 0) {
            FlowBlock flowBlock2 = new FlowBlock();
            flowBlock2.setTable_id(request.getTableId());
            this.approveDao.deleteBlock(flowBlock2);
            this.approveDao.deleteLine(flowBlock2);
            this.approveDao.deleteFlowConditionDetail(flowBlock2);
            this.approveDao.deleteApprover(flowBlock2);
            this.approveDao.updateTableIsApproveState(request.getTableId(), "0");
            return new ActionResult(true, result);
        }
        for (FlowBlock flowBlock2 : request.getFlowBlock()) {
            this.approveDao.deleteBlock(flowBlock2);
            this.approveDao.deleteLine(flowBlock2);
            this.approveDao.deleteFlowConditionDetail(flowBlock2);
            this.approveDao.deleteApprover(flowBlock2);
        }

        for (FlowBlock flowBlock : request.getFlowBlock()) {
            if ("".equals(flowBlock.getApprove_name()) || flowBlock.getApprove_name() == null) {
                I18nDescribe blockI18N = flowBlock.getApprove_name_I18N();
                String[] uuids = UUID.randomUUID().toString().split("-");
                String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
                blockI18N.setInternational_id(uuid);
                flowBlock.setApprove_name(uuid);
                this.tableDeployDao.addI18N(blockI18N);
            }
            if (this.approveDao.addBlock(flowBlock) > 0) {
                result = "保存成功";
            } else {

                result = "保存失败";
                return new ActionResult(true, result);
            }

            if (request.getSaveApproveBlockEvent() != null
                    && request.getSaveApproveBlockEvent().get(flowBlock.getBlock_id()) != null) {
                Map<String, ApproveBlockEvent> approveBlockEventMap = (Map) request.getSaveApproveBlockEvent()
                        .get(flowBlock.getBlock_id());
                for (Map.Entry<String, ApproveBlockEvent> approveBlockEvent : approveBlockEventMap.entrySet()) {
                    this.approveDao.insertApproveBlockEvent((ApproveBlockEvent) approveBlockEvent.getValue());
                    if (((ApproveBlockEvent) approveBlockEvent.getValue()).getEvent() != null) {
                        Map<String, ActionEventDescribe> event = ((ApproveBlockEvent) approveBlockEvent.getValue())
                                .getEvent();
                        for (Map.Entry<String, ActionEventDescribe> blockEvent : event.entrySet()) {
                            this.approveDao.addActionEvent((ActionEventDescribe) blockEvent.getValue());
                        }
                    }
                }
            }

            if (request.getSaveApproveButtonEvent() != null
                    && request.getSaveApproveButtonEvent().get(flowBlock.getBlock_id()) != null) {
                Map<String, FlowEvent> flowEventMap = (Map) request.getSaveApproveButtonEvent()
                        .get(flowBlock.getBlock_id());
                for (Map.Entry<String, FlowEvent> flowEvent : flowEventMap.entrySet()) {
                    if (((FlowEvent) flowEvent.getValue()).getInternational_id() == null
                            || "".equals(((FlowEvent) flowEvent.getValue()).getInternational_id())) {
                        I18nDescribe blockI18N = ((FlowEvent) flowEvent.getValue()).getI18n();
                        String[] uuids = UUID.randomUUID().toString().split("-");
                        String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
                        blockI18N.setInternational_id(uuid);
                        ((FlowEvent) flowEvent.getValue()).setInternational_id(uuid);
                        this.tableDeployDao.addI18N(blockI18N);
                    }
                    this.approveDao.addFlowEvent((FlowEvent) flowEvent.getValue());

                    if (((FlowEvent) flowEvent.getValue()).getEvent() != null
                            || "".equals(((FlowEvent) flowEvent.getValue()).getEvent())) {
                        int actionEventSeq = 1;
                        for (Map.Entry<String, ActionEventDescribe> entryEvent : ((FlowEvent) flowEvent.getValue())
                                .getEvent().entrySet()) {
                            ((ActionEventDescribe) entryEvent.getValue()).setSeq(actionEventSeq);
                            this.approveDao.addActionEvent((ActionEventDescribe) entryEvent.getValue());
                            actionEventSeq++;
                        }
                    }
                }
            }
        }

        for (AuthGroup authGroup : request.getAuthGroup()) {
            if (this.approveDao.addApprover(authGroup) > 0) {
                result = "保存成功";
                continue;
            }
            result = "保存失败";
            return new ActionResult(true, result);
        }

        for (FlowLine flowLine : request.getFlowLine()) {
            for (FlowBlock flowBlock : request.getFlowBlock()) {
                if (flowLine.getPage_source_id().equals(flowBlock.getBlock_id())) {
                    flowLine.setSource_text(flowBlock.getText());
                }
                if (flowLine.getPage_target_id().equals(flowBlock.getBlock_id())) {
                    flowLine.setTarget_text(flowBlock.getText());
                }
            }
            if (this.approveDao.addLine(flowLine) > 0) {
                result = "保存成功";
                continue;
            }
            result = "保存失败";
            return new ActionResult(true, result);
        }

        for (FlowConditionDetail flowConditionDetail : request.getFlowConditionDetail()) {
            if (this.approveDao.addLineDetail(flowConditionDetail) > 0) {
                result = "保存成功";
                continue;
            }
            result = "保存失败";
            return new ActionResult(true, result);
        }

        this.approveDao.updateTableIsApproveState(request.getTableId(), "1");
        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/saveTemplate.do" })
    public ActionResult saveTemplate(@RequestBody ApproveRequestModel request) {
        String result = "保存成功";
        if (!"".equals(Integer.valueOf(((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele()))
                && ((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() != 0) {
            if (((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() == 1) {
                this.approveDao.updateTemplate((AuthGroup) request.getAuthGroup().get(0));
            } else if (((AuthGroup) request.getAuthGroup().get(0)).getEditOrdetele() == 2) {
                this.approveDao.deleteTemplate((AuthGroup) request.getAuthGroup().get(0));
            }
        } else {
            this.approveDao.deleteTemplate((AuthGroup) request.getAuthGroup().get(0));
            for (AuthGroup authGroup : request.getAuthGroup()) {
                if (this.approveDao.addAuthGroup(authGroup) > 0) {
                    result = "保存成功";
                    continue;
                }
                result = "保存失败";
            }
        }

        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/lookAllTemplate.do" })
    public ActionResult lookAllTemplate(@RequestBody ApproveRequestModel request) {
        ApproveModel form = new ApproveModel(request.getParent());
        for (AuthGroup authGroup : request.getAuthGroup()) {
            List<AuthGroup> authGroupMember = this.approveDao.selectTemplate(authGroup.getType());
            form.setAuthGroup(authGroupMember);
        }
        return new ActionResult(true, form);
    }

    @ResponseBody
    @RequestMapping({ "/validateOneApprove.do" })
    public ActionResult validateOneApprove(@RequestBody ApproveRequestModel request, AuthDetails user) {
        ApproveModel form = new ApproveModel(request.getParent());
        System.out.println(user.getDepartment_id());
        int count = this.approveDao.selectSuperiorDept(user.getDepartment_id(), user.getId(), request.getAuthGroup());
        System.out.println(count);
        form.setCount(count);
        return new ActionResult(true, form);
    }

    @ResponseBody
    @RequestMapping({ "/validateAllApprove.do" })
    public ActionResult validateAllApprove(@RequestBody ApproveRequestModel request, AuthDetails user) {
        ApproveModel form = new ApproveModel(request.getParent());
        System.out.println(user.getDepartment_id());
        int count = this.approveDao.selectSuperiorDept(user.getDepartment_id(), user.getId(), request.getAuthGroup());
        System.out.println(count);
        form.setCount(count);
        return new ActionResult(true, form);
    }

    @ResponseBody
    @RequestMapping({ "/approveData.do" })
    public ActionResult getIndexApproveFormData(@RequestBody ApproveRequestModel request, AuthDetails user) {
        TableDescribe tableDesc = this.cacheService.getTableDesc(request.getTableId());
        if (tableDesc == null || tableDesc.getIdColumns().length != 1) {
            throw new ApplicationException("table error");
        }
        Map<String, Object> data = this.approveDao.selectCreUser(request.getTableId(), tableDesc.getIdColumns()[0],
                request.getDataId());
        return new ActionResult(true, data);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/batchApprove.do" })
    public ActionResult batchApprove(@RequestBody ApproveRequestModel request, AuthDetails user) {
        Map<String, FlowEvent> batchFlowEvents = request.getBatchFlowEvents();

        Map<String, Map<String, ApproveBlockEvent>> batchBlockEvents = request.getBatchBlockEvents();
        for (Map.Entry<String, FlowEvent> flowEvent : batchFlowEvents.entrySet()) {
            checkApproveButtonEvent(((FlowEvent) flowEvent.getValue()).getCondition(), "", request.getTableId(),
                    ((FlowEvent) flowEvent.getValue()).getData_id().toString());
            switch (((FlowEvent) flowEvent.getValue()).getEvent_type()) {
            case "agreeEvent":
                this.approveService.approvePass((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user);
                continue;
            case "rejectEvent":
                this.approveService.approveReject((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user);
                continue;
            case "disagreeEvent":
                this.approveService.approveDisagree((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user);
                continue;
            case "retainEvent":
                this.approveService.approveHold((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user);
                continue;
            case "workEvent":
                this.approveService.approveWorkNode((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user, false);
                continue;
            case "finishEvent":
                this.approveService.approveWorkNode((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user, true);
                continue;
            case "terminationEvent":
                this.approveService.approveTermination((String) flowEvent.getKey(),
                        ((FlowEvent) flowEvent.getValue()).getBlock_id(),
                        ((FlowEvent) flowEvent.getValue()).getTable_id(), request.getApproveReason(),
                        ((FlowEvent) flowEvent.getValue()).getData_id(), (FlowEvent) flowEvent.getValue(),
                        (Map) batchBlockEvents.get(flowEvent.getKey()), null, user);
                continue;
            }
            throw new ApplicationException("has no approve type");
        }

        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/getBatchBlock.do" })
    public ActionResult getBatchBlock(@RequestBody ApproveRequestModel request, AuthDetails user) {
        Map<String, Object> batchApprove = this.approveService.getBatchApprove(request.getTableId(), user);

        return new ActionResult(true, batchApprove);
    }

    public void checkApproveButtonEvent(List<ActionPrerequistieDescribe> action_prerequistie, String formId,
            String tableId, String dataId) {
        if (!"".equals(action_prerequistie) && action_prerequistie != null) {
            for (int i = 0; i < action_prerequistie.size(); i++) {
                if (((ActionPrerequistieDescribe) action_prerequistie.get(i)).getIs_using().intValue() == 1
                        && "1".equals(((ActionPrerequistieDescribe) action_prerequistie.get(i)).getLevel())) {
                    Object evaluate = "";
                    Object param = "";
                    if ("".equals(formId)) {
                        String id_column = this.cacheService.getTableDesc(tableId).getId_column();
                        param = this.approveDao.selectDataParam(tableId, id_column, dataId);
                        evaluate = this.formulaService.evaluate(
                                ((ActionPrerequistieDescribe) action_prerequistie.get(i)).getCheck_condition(), param);
                    } else {
                        evaluate = this.formulaService.evaluate(formId,
                                ((ActionPrerequistieDescribe) action_prerequistie.get(i)).getCheck_condition());
                    }
                    if (evaluate instanceof Boolean) {
                        if (((Boolean) evaluate).booleanValue()) {
                            String error_msg_id = ((ActionPrerequistieDescribe) action_prerequistie.get(i))
                                    .getViolate_msg_international_id();
                            I18nDescribe msgI18n = this.cacheService.getMsgI18n(error_msg_id);
                            String[] msgParams = null;
                            if (((ActionPrerequistieDescribe) action_prerequistie.get(i))
                                    .getViolate_msg_param() != null) {
                                Object msgParam = "";
                                if ("".equals(formId)) {
                                    msgParam = this.formulaService
                                            .evaluate(((ActionPrerequistieDescribe) action_prerequistie.get(i))
                                                    .getViolate_msg_param(), param);
                                } else {
                                    msgParam = this.formulaService.evaluate(formId,
                                            ((ActionPrerequistieDescribe) action_prerequistie.get(i))
                                                    .getViolate_msg_param());
                                }
                                if (msgParam != null) {
                                    msgParams = msgParam.toString().split(",");
                                }
                            }
                            String messageText = this.dataService.getMessageText(msgI18n.getInternational_id(),
                                    msgParams);
                            throw new ApplicationException(messageText);
                        }
                    } else {
                        throw new ApplicationException("formula is error: "
                                + ((ActionPrerequistieDescribe) action_prerequistie.get(i)).getCheck_condition());
                    }
                }
            }
        }
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/approve.do" })
    public ActionResult approve(@RequestBody ApproveRequestModel request, AuthDetails user) {
        int isApproveBack = this.approveDao.isApproveBack(request.getApproveId());
        if (0 == isApproveBack)
            throw new ApplicationException("approve is back");
        FlowEvent flowEvent = request.getFlowEvent();
        if (null == flowEvent)
            return new ActionResult(false, "no approve");
        List<ActionPrerequistieDescribe> action_prerequistie = flowEvent.getCondition();
        checkApproveButtonEvent(action_prerequistie, "", request.getTableId(), request.getDataId());
        String msg = "";
        switch (flowEvent.getEvent_type()) {
        case "agreeEvent":
            this.approveService.approvePass(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user);
            msg = "approve success";

            return new ActionResult(true, "approve success");
        case "rejectEvent":
            this.approveService.approveReject(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user);
            msg = "reject success";
            return new ActionResult(true, "approve success");
        case "disagreeEvent":
            this.approveService.approveDisagree(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user);
            msg = "disagree success";
            return new ActionResult(true, "approve success");
        case "retainEvent":
            this.approveService.approveHold(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user);
            msg = "retain success";
            return new ActionResult(true, "approve success");
        case "workEvent":
            this.approveService.approveWorkNode(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user, false);
            msg = "work success";
            return new ActionResult(true, "approve success");
        case "finishEvent":
            this.approveService.approveWorkNode(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user, true);
            msg = "finish success";
            return new ActionResult(true, "approve success");
        case "terminationEvent":
            this.approveService.approveTermination(request.getApproveId(), request.getBlockId(), request.getTableId(),
                    request.getApproveReason(), request.getDataId(), request.getFlowEvent(),
                    request.getApproveBlockEvent(), request.getApproveEvent(), user);
            msg = "termination success";
            return new ActionResult(true, "approve success");
        }
        throw new ApplicationException("has no approve type");
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/approveSubmit.do" })
    public ActionResult approveSubmit(@RequestBody ApproveRequestModel request, AuthDetails user) {
        List<ViewDataMap> params = request.getParam();
        String tableName = request.getTableId();
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        if (params != null) {

            for (int i = 0; i < params.size(); i++) {
                DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB((Map) params.get(i));
                this.approveService.saveApproveUser(tableName, databaseDataMap, null);

                this.approveService.updateApproveStatus(table.getId(), databaseDataMap.get(table.getIdColumns()[0]),
                        "2");

                if (null != request.getFlowEvent())
                    this.approveService.approveApi(request.getFlowEvent());
            }
        }
        return new ActionResult(true, "success");
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/batchApproveSubmit.do" })
    public ActionResult batchApproveSubmit(@RequestBody ApproveRequestModel request) {
        Map<String, FlowEvent> batchFlowEvents = request.getBatchFlowEvents();
        return this.approveService.batchApproveSubmit(request.getTableId(), batchFlowEvents);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/checkBatchApproveSubmit.do" })
    public ActionResult checkBatchApproveSubmit(@RequestBody ApproveRequestModel request) {
        return this.approveService.checkBatchApproveSubmit(request.getTableId(), request.getRecordIds());
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/approveBack.do" })
    public ActionResult approveBack(@RequestBody ApproveRequestModel request, AuthDetails user) {
        int hasApproveBackButton = this.approveDao.hasApproveBackButton(request.getApproveId());
        if (0 != hasApproveBackButton)
            throw new ApplicationException("approve is begin");
        this.approveDao.deleteApproveFlowByApproveId(request.getApproveId());
        this.approveDao.deleteApproveFlowNodeByApproveId(request.getApproveId());
        this.approveDao.deleteApproveFlowUserByApproveId(request.getApproveId());
        this.approveService.updateApproveStatus(request.getTableId(), request.getDataId(),
                ApproveFlow.ApproveStatus.WAIT);
        return new ActionResult(true, "success");
    }

    @ResponseBody
    @RequestMapping({ "/initAddApproveTree.do" })
    public ActionResult initAddApproveTree() {
        return new ActionResult(true, this.dataService.buildUserTree());
    }

    @ResponseBody
    @RequestMapping({ "/addApprove.do" })
    public ActionResult countersign(@RequestBody ApproveRequestModel request) {
        if ("".equals(request.getNextBlockId()) || request.getNextBlockId() == null
                || "isUpdateAddApprove".equals(request.getNextBlockId())) {
            if (!this.approveService.approveBlockState(request.getApproveId(), request.getBlockId())) {
                throw new ApplicationException("approve status is change");

            }
        } else if (!this.approveService.approveBlockState(request.getApproveId(), request.getNextBlockId())) {
            throw new ApplicationException("approve status is change");
        }

        Map<String, Object> userId = request.getUserId();
        Integer sequence = this.approveDao.getSequenceByApproveId(request.getApproveId());
        if ("".equals(request.getNextBlockId()) || request.getNextBlockId() == null) {

            List<ApproveFlowUser> addApproveFlowUsers = this.approveDao.selectAllAddApproveUser(request.getApproveId(),
                    request.getBlockId(), sequence.intValue());
            if (addApproveFlowUsers != null && addApproveFlowUsers.size() != 0) {
                throw new ApplicationException("is add approve by other user");
            }
        } else {

            this.approveDao.DeleteAddApproveFlowUser(request.getApproveId(), request.getBlockId(), sequence.intValue());
            this.approveDao.DeleteAddApproveFlowNodes(request.getApproveId(), request.getBlockId(),
                    sequence.intValue());
        }
        float seq = 1.0F;
        if (userId.size() > 0 && userId != null) {
            for (Map.Entry<String, Object> user : userId.entrySet()) {
                ApproveFlowUser approveFlowUser = new ApproveFlowUser();
                approveFlowUser.setApprove_id(request.getApproveId());
                approveFlowUser.setBlock_id(user.getValue().toString());
                approveFlowUser.setUserId((String) user.getKey());
                approveFlowUser.setState("wait");
                approveFlowUser.setSequence(sequence.intValue());
                approveFlowUser.setCreation_time(null);
                approveFlowUser.setIs_default_approve(1);
                approveFlowUser.setIs_add_approve(1);
                approveFlowUser.setAdd_approve_seq(request.getNode_seq() + seq / 100.0F);
                this.approveDao.addApproveFlowUser(approveFlowUser);
                this.approveDao.addApproveFlowNodes(approveFlowUser, request.getBlockId());
                seq++;
            }
            this.approveDao.updateIsAddApproveNade(request.getApproveId(), request.getBlockId(), 1);
        } else {
            this.approveDao.updateIsAddApproveNade(request.getApproveId(), request.getBlockId(), 0);
        }
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/takeBackApprove.do" })
    public ActionResult takeBackApprove(@RequestBody ApproveRequestModel request) {
        String nextBlockId = request.getNextBlockId();
        if ("".equals(nextBlockId) || nextBlockId == null) {
            ApproveFlowNode approveFlowNode = this.approveDao.selectApproveFlowNodeByBlockId(request.getApproveId(),
                    request.getBlockId());
            if (approveFlowNode.getState().equals("wait")) {
                Integer sequence = this.approveDao.getSequenceByApproveId(request.getApproveId());
                this.approveDao.takeBackApproveUser(request.getApproveId(), request.getBlockId(), request.getUser_Id(),
                        sequence.intValue());
                this.approveDao.takeBackApproveNode(request.getApproveId(), request.getBlockId(), sequence.intValue());

                this.approveService.updateApproveStatus(request.getTableId(), request.getDataId(), "2");
            } else {
                throw new ApplicationException("approve status is change");
            }
        } else {
            ApproveFlowNode approveFlowNode = this.approveDao.selectApproveFlowNodeByBlockId(request.getApproveId(),
                    nextBlockId);
            if (approveFlowNode.getState().equals("wait")) {
                boolean flag = true;
                List<ApproveFlowUser> approveFlowUsers = this.approveDao
                        .selectNextBlockIdByFlowUser(request.getApproveId(), nextBlockId);
                for (ApproveFlowUser approveFlowUser : approveFlowUsers) {
                    if (!approveFlowUser.getState().equals("wait")) {
                        flag = false;
                        break;
                    }
                }
                if (flag) {
                    Integer sequence = this.approveDao.getSequenceByApproveId(request.getApproveId());
                    this.approveDao.takeBackApproveUser(request.getApproveId(), request.getBlockId(),
                            request.getUser_Id(), sequence.intValue());
                    this.approveDao.takeBackApproveNode(request.getApproveId(), request.getBlockId(),
                            sequence.intValue());

                    this.approveService.updateApproveStatus(request.getTableId(), request.getDataId(), "2");
                }
            } else {
                throw new ApplicationException("approve status is change");
            }
        }
        this.approveService.updateApproveStatus(request.getTableId(), request.getDataId(), "2");
        return new ActionResult(true);
    }

    @ResponseBody
    @RequestMapping({ "/cuibanApprove.do" })
    public ActionResult cuibanApprove(@RequestBody ApproveRequestModel request, AuthDetails user) {
        Map<String, Object> ret = new HashMap<String, Object>();
        Map<String, String> data = new HashMap<String, String>();
        data.put("approveId", request.getApproveId());
        data.put("dataId", request.getDataId());
        data.put("tableId", request.getTableId());

        this.approveService.approveCuibanRemind(data, user.getId());
        return new ActionResult(true, ret);
    }

    @ResponseBody
    @RequestMapping({ "/outsideSubmitApprove.do" })
    public ActionResult outsideSubmitApprove(String domain, String tableId, String dataId) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null == domain || "".equals(domain)) {
            result.put("status", "false");
            result.put("msg", "no domain");
            return new ActionResult(false, result);
        }
        this.dxRoutingDataSource.setDomainKey(domain);
        TableDescribe table = this.cacheService.getTableDesc(tableId);
        if (null == table) {
            result.put("status", "false");
            result.put("msg", "no table: " + tableId);
            return new ActionResult(false, result);
        }
        if (null == table.getIdColumns() || 1 != table.getIdColumns().length) {
            result.put("status", "false");
            result.put("msg", "table: " + tableId + " has bad id column ");
            return new ActionResult(false, result);
        }
        Map<String, Object> dataIdSelect = new HashMap<String, Object>();
        dataIdSelect.put(table.getIdColumns()[0], dataId.toString());
        List<Map<String, Object>> data = this.approveDao.selectDataById(tableId, dataIdSelect);
        if (null == data || data.size() != 1) {
            result.put("status", "false");
            result.put("msg", "submit approve false");
            return new ActionResult(false, result);
        }
        this.approveService.saveApproveUser(tableId, (Map) data.get(0), null);

        this.approveService.updateApproveStatus(tableId, ((Map) data.get(0)).get(table.getIdColumns()[0]), "2");
        result.put("status", "true");
        return new ActionResult(true, result);
    }

    @ResponseBody
    @RequestMapping({ "/outsideApprove.do" })
    public ActionResult outsideApprove(String domain, String tableId, Object dataId, String userId, String type,
            String approveReason) {
        Map<String, Object> result = new HashMap<String, Object>();
        if (null == domain || "".equals(domain)) {
            result.put("status", "false");
            result.put("msg", "no domain");
            return new ActionResult(false, result);
        }
        this.dxRoutingDataSource.setDomainKey(domain);
        TableDescribe table = this.cacheService.getTableDesc(tableId);
        if (null == table) {
            result.put("status", "false");
            result.put("msg", "no table: " + tableId);
            return new ActionResult(false, result);
        }
        if (null == table.getIdColumns() || 1 != table.getIdColumns().length) {
            result.put("status", "false");
            result.put("msg", "table: " + tableId + " has bad id column ");
            return new ActionResult(false, result);
        }
        if (null == type || "".equals(type)) {
            result.put("status", "false");
            result.put("msg", "no approve type");
            return new ActionResult(false, result);
        }
        List<Map<String, Object>> approveData = this.approveDao.getApproveDataByTableDataUser(tableId, dataId, userId);
        if (null == approveData || 1 != approveData.size()) {
            result.put("status", "false");
            result.put("msg", "data is bad");
            return new ActionResult(false, result);
        }
        String approveId = ((Map) approveData.get(0)).get("approve_id").toString();
        String blockId = ((Map) approveData.get(0)).get("block_id").toString();
        FlowEvent flowEvent = null;
        Map<String, Map<String, FlowEvent>> approveBlockEvent = table.getApproveButtonEvent();
        if (null == approveBlockEvent || null == approveBlockEvent.get(blockId)) {
            result.put("status", "false");
            result.put("msg", "has no approve");
            return new ActionResult(false, result);
        }
        for (Map.Entry<String, FlowEvent> entry : (approveBlockEvent.get(blockId)).entrySet()) {
            flowEvent = (FlowEvent) entry.getValue();
        }
        switch (type) {
        case "agree":
            this.approveService.approvePass(approveId, blockId, tableId, approveReason, dataId, flowEvent, null, null,
                    null);
            break;

        case "reject":
            this.approveService.approveReject(approveId, blockId, tableId, approveReason, dataId, flowEvent, null, null,
                    null);
            break;

        case "disagree":
            this.approveService.approveDisagree(approveId, blockId, tableId, approveReason, dataId, flowEvent, null,
                    null, null);
            break;

        case "hold":
            this.approveService.approveHold(approveId, blockId, tableId, approveReason, dataId, flowEvent, null, null,
                    null);
            break;
        }

        return new ActionResult(true, result);
    }
}
