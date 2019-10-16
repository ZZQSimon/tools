package cn.com.easyerp.core.view.form.detail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.approve.ApproveFlowNode;
import cn.com.easyerp.core.approve.ApproveFlowUser;
import cn.com.easyerp.core.approve.ApproveService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.evaluate.ModelService;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.core.widget.grid.GridService;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
import cn.com.easyerp.framework.enums.LogType;
import cn.com.easyerp.framework.exception.ApplicationException;

@Controller
@RequestMapping({ "/detail" })
public class DetailForm extends FormViewControllerBase {
    public static final String VIEW = "detail";
    @Autowired
    private DataService dataService;
    @Autowired
    private GridService gridService;
    @Autowired
    private DetailFormService service;
    @Autowired
    private AuthService authService;
    @Autowired
    private ViewService viewService;
    @Autowired
    private ApproveService approveService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private ApproveDao approveDao;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ModelService modelService;
    @Autowired
    private LogService logService;

    protected DetailFormService getBuilder() {
        return this.service;
    }

    @RequestMapping({ "/create.view" })
    public ModelAndView createView(@RequestBody DetailRequestModel request, AuthDetails user) {
        DetailFormModel form = this.service.createView(request.getTable(), request.getParent(), request.getFixedData(),
                user, getBuilder());

        this.modelService.buildModel(form);
        form.setPageId(request.getPageId());
        TableDescribe tableDesc = this.cacheService.getTableDesc(request.getTable());

        this.authService.assertAuth(user, tableDesc.getId(),
                tableDesc.getTrigger(TriggerDescribe.TriggerType.add).getAction_id(), null);

        if (tableDesc.getIs_approve() == 1) {
            String[] idColumns = tableDesc.getIdColumns();
            if (idColumns != null && idColumns.length == 1) {
                form.setHasSaveAndSubmitButton(true);
            }
            form.setFreeApprove(this.approveDao.selectCountApproveTable(form.getTableName()));
        }
        Map<String, Object> data = request.getParentId();
        if (null == data)
            data = new HashMap<String, Object>();
        this.dataService.prepareAutoKey(tableDesc, data);
        if (data.size() != 0)
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                FieldModelBase field = form.getField((String) entry.getKey());
                if (field != null) {
                    field.setValue(entry.getValue());
                }
            }
        return buildModelAndView(form);
    }

    @RequestMapping({ "/edit.view" })
    public ModelAndView editView(@RequestBody DetailRequestModel request, AuthDetails user) {
        DetailFormModel form = this.service.editView(request, user, getBuilder());

        this.modelService.buildModel(form);
        String tableName = request.getTable();
        TableDescribe tableDesc = this.cacheService.getTableDesc(tableName);

        FieldModelBase ownerColumn = (FieldModelBase) this.authService.getOwner(form.getFieldMap());

        if (tableDesc.getTriggers() != null) {
            boolean authCheck = this.service.editAuthCheck(user, tableDesc,
                    tableDesc.getTrigger(TriggerDescribe.TriggerType.view).getAction_id(),
                    (ownerColumn == null) ? null : ownerColumn.getValue(), form.getFieldMap());
            if (!authCheck) {
                throw new ApplicationException("access is denied");
            }
        }

        form.setIsOutPage(request.getIsOutPage());

        if (request.getIsIndexView() == 1) {
            form.setIsIndexView(request.getIsIndexView());
            Map<String, Object> indexParam = new HashMap<String, Object>();
            indexParam.put("url",
                    (request.getUrl() == null || "".equals(request.getUrl())) ? "/detail/edit.view" : request.getUrl());
            indexParam.put("table", request.getTable());
            indexParam.put("param", request.getParam());
            indexParam.put("readonly", Boolean.valueOf(request.isReadonly()));
            indexParam.put("isIndexView", Integer.valueOf(request.getIsIndexView()));
            form.setIsIndexViewParam(indexParam);
        }
        if (tableDesc.getIs_approve() != 1) {
            return buildModelAndView(form);
        }
        String[] idColumns = tableDesc.getIdColumns();
        if (idColumns == null || idColumns.length != 1)
            return buildModelAndView(form);
        if (request.getIsDialog() == 1) {
            return buildModelAndView(form);
        }
        form.setHasSaveAndSubmitButton(hasSaveAndSubmitButton(tableName, request.getParam()));
        ApproveFlow approveFlow = this.approveService.selectDetailApprove(tableName,
                request.getParam().get(idColumns[0]).toString());
        form.setApproveFlow(approveFlow);

        if (null == approveFlow) {
            form.setHasApproveBackButton(false);
        } else {
            if (null != form.getFieldMap().get("owner")
                    && null != ((FieldModelBase) form.getFieldMap().get("owner")).getValue()) {
                if (((FieldModelBase) form.getFieldMap().get("owner")).getValue().equals(user.getId())) {
                    int hasApproveBackButton = this.approveDao.hasApproveBackButton(approveFlow.getApprove_id());
                    if (0 == hasApproveBackButton) {
                        form.setHasApproveBackButton(true);
                    }
                }
            } else if (null != form.getFieldMap().get("cre_user")
                    && null != ((FieldModelBase) form.getFieldMap().get("cre_user")).getValue()
                    && ((FieldModelBase) form.getFieldMap().get("cre_user")).getValue().equals(user.getId())) {
                int hasApproveBackButton = this.approveDao.hasApproveBackButton(approveFlow.getApprove_id());
                if (0 == hasApproveBackButton) {
                    form.setHasApproveBackButton(true);
                }
            }

            List<ApproveFlowNode> approveFlowNodes = approveFlow.getApproveFlowNodes();
            if (null != approveFlowNodes) {
                for (int i = 0; i < approveFlowNodes.size(); i++) {
                    if (ApproveFlowNode.State.WAIT.equals(((ApproveFlowNode) approveFlowNodes.get(i)).getState())) {
                        List<ApproveFlowUser> approveFlowUsers = ((ApproveFlowNode) approveFlowNodes.get(i))
                                .getApproveFlowUsers();
                        if (null == approveFlowUsers)
                            break;
                        for (int j = 0; j < approveFlowUsers.size(); j++) {
                            if (user.getId().equals(((ApproveFlowUser) approveFlowUsers.get(j)).getUserId())) {
                                form.setHasEditButton(true);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            if (approveFlow.getState() == 3 && ownerColumn != null && ownerColumn.getValue() != null
                    && ownerColumn.getValue().equals(user.getId())) {
                form.setHasEditButton(true);
            }
        }

        List<ApproveFlowUser> rejectRecord = this.approveDao.getRejectRecord(tableName,
                request.getParam().get(idColumns[0]).toString());
        for (int i = 0; i < rejectRecord.size(); i++) {
            AuthDetails rejectUser = this.approveDao.selectUser(((ApproveFlowUser) rejectRecord.get(i)).getUserId());
            ((ApproveFlowUser) rejectRecord.get(i)).setUser(rejectUser);
        }
        form.setRejectRecord(rejectRecord);
        // FieldWithRefModel fieldWithRefModel = null;
        FieldModelBase owner = (FieldModelBase) form.getFieldMap().get("cre_user");
        String initator = "";
        if (owner != null) {
            if (owner instanceof FieldWithRefModel) {
                initator = (String) ((FieldWithRefModel) owner).getRef().get("name");
            } else {
                initator = (String) owner.getValue();
            }
            form.setInitator(initator);
            form.setInitiatorId((String) owner.getValue());
        }

        form.setByTheTime((approveFlow == null) ? null : approveFlow.getCreation_time());
        return buildModelAndView(form);
    }

    @RequestMapping({ "/userEdit.view" })
    public ModelAndView userEditView(@RequestBody DetailRequestModel request, AuthDetails user) {
        if (request.getParam() == null) {
            request.setParam(new ViewDataMap());
        }
        request.getParam().put("id", user.getId());
        DetailFormModel form = this.service.editView(request, user, getBuilder());

        this.modelService.buildModel(form);

        form.setIsIndexView(1);
        Map<String, Object> indexParam = new HashMap<String, Object>();
        indexParam.put("url", "/detail/userEdit.view");
        indexParam.put("table", request.getTable());

        indexParam.put("readonly", Boolean.valueOf(request.isReadonly()));
        indexParam.put("isIndexView", Integer.valueOf(request.getIsIndexView()));
        form.setUrl("/detail/userEdit.view");
        form.setIsIndexViewParam(indexParam);
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/getOldParam.do" })
    public ActionResult getOldParam(@RequestBody DetailRequestModel request) {
        DetailFormModel form = (DetailFormModel) request.getWidget();
        Map<String, Object> map = this.apiService.buildData(form.getFields());
        return new ActionResult(true, map);
    }

    @ResponseBody
    @RequestMapping({ "/getChildParam.do" })
    public ActionResult getChildParam(@RequestBody DetailRequestModel request) {
        DetailFormModel form = (DetailFormModel) request.getWidget();
        TableDescribe parentTable = this.dataService.getTableDesc(form.getTableName());
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());

        String[] parentIds = parentTable.getIdColumns();
        List<GridModel> children = form.getChildren();
        for (int i = 0; i < children.size(); i++) {
            TableDescribe table = this.dataService.getTableDesc(((GridModel) children.get(i)).getTable());
            String[] ids = table.getIdColumns();

            List<Map<String, Object>> childDatas = new ArrayList<Map<String, Object>>();
            List<RecordModel> records = ((GridModel) children.get(i)).getRecords();
            if (records != null) {

                for (int j = 0; j < records.size(); j++) {
                    DatabaseDataMap databaseDataMap1 = this.gridService.buildRecordData((RecordModel) records.get(i));
                    for (int k = 0; k < parentIds.length; k++) {
                        if (ids[0].equals(parentIds[k])) {
                            databaseDataMap1.put(ids[0], databaseDataMap.get(ids[0]));
                        }
                    }
                    childDatas.add(databaseDataMap1);
                }
                ((GridModel) children.get(i)).setChildDatas(childDatas);
            }
        }
        return new ActionResult(true, form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/edit.do" })
    public ActionResult edit(@RequestBody DetailRequestModel request, HttpServletRequest httpRequest,
            AuthDetails user) {
        String logId = this.logService.getMaxId();
        this.logService.logTemp(logId, LogType.edit, AuthService.getCurrentUserId());
        httpRequest.setAttribute("logId", logId);

        DetailFormModel form = (DetailFormModel) request.getWidget();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        httpRequest.setAttribute("tableId", table.getId());

        ActionResult result = this.service.edit(form, request.getParam(), request.getCalendarColorParam(),
                request.getCalendarColorStatus(), table, user, request.getTriggerRequestParams(),
                request.getChildTriggerRequestParams(), httpRequest);
        if (request.getIsSaveAndSubmit() == 1) {
            DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());
            this.approveService.saveApproveUser(form.getTableName(), databaseDataMap, request);

            this.approveService.updateApproveStatus(table.getId(), databaseDataMap.get(table.getIdColumns()[0]), "2");

            if (null != request.getApproveBlockEvent().get("submitEvent")) {
                this.approveService.approveApi((ApproveBlockEvent) request.getApproveBlockEvent().get("submitEvent"));
            }
        }
        if (request.getIsIndexView() == 1) {
            result.setData(request.getIsIndexViewParam());
        }
        String data = (String) httpRequest.getAttribute("data");
        this.logService.updateLogNormal(logId, table.getId(), data);

        return result;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    @ResponseBody
    @RequestMapping({ "/create.do" })
    public ActionResult create(@RequestBody DetailRequestModel request, HttpServletRequest httpRequest,
            AuthDetails user) {
        String logId = this.logService.getMaxId();
        this.logService.logTemp(logId, LogType.insert, AuthService.getCurrentUserId());
        httpRequest.setAttribute("logId", logId);

        DetailFormModel form = (DetailFormModel) request.getWidget();
        TableDescribe table = this.dataService.getTableDesc(form.getTableName());
        httpRequest.setAttribute("tableId", table.getId());

        ActionResult actionResult = null;
        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());
        httpRequest.setAttribute("data", Common.toJson(databaseDataMap));

        String[] idColumns = table.getIdColumns();
        actionResult = this.service.create(form, request.getParam(), request.getCalendarColorParam(), table, user,
                request.getTriggerRequestParams(), request.getChildTriggerRequestParams());

        Map<String, Object> data = (Map) actionResult.getData();
        try {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                databaseDataMap.put(entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
        }

        if (request.getIsSaveAndSubmit() == 1) {
            // boolean flag = false;
            if (this.approveDao.selectCountApproveTable(form.getTableName()) == 0 && request.getUserId() == null) {
                this.logService.updateLogException(logId, table.getId(), Common.toJson(databaseDataMap), "NoApprover");
                return new ActionResult(true, "NoApprover");
            }
        }

        String approve_id = null;
        if (request.getIsSaveAndSubmit() == 1) {
            // boolean flag = false;
            if (this.approveDao.selectCountApproveTable(form.getTableName()) == 0) {
                if (request.getUserId() != null && request.getUserId().size() > 0) {
                    Map<String, Object> userId = request.getUserId();
                    approve_id = this.approveDao.getId("c_approve_flow", "");
                    this.approveDao.saveApproveFlow(approve_id, form.getTableName(),
                            databaseDataMap.get(idColumns[0]).toString(), new Date());

                    Integer maxSequence = this.approveDao.getMaxSequence(form.getTableName(),
                            databaseDataMap.get(idColumns[0]).toString());
                    if (maxSequence == null) {
                        maxSequence = Integer.valueOf(1);
                    }
                    int num = 1;
                    maxSequence = Integer.valueOf(maxSequence.intValue() + 1);
                    for (Map.Entry<String, Object> users : userId.entrySet()) {
                        this.approveDao.saveApproveNode(approve_id, users.getValue().toString(), num++,
                                maxSequence.intValue(), 0, 1, null);
                        ApproveFlowUser approveFlowUser = new ApproveFlowUser();
                        approveFlowUser.setApprove_id(approve_id);
                        approveFlowUser.setBlock_id(users.getValue().toString());
                        approveFlowUser.setUserId((String) users.getKey());
                        approveFlowUser.setState("wait");
                        approveFlowUser.setSequence(maxSequence.intValue());
                        this.approveDao.addApproveFlowUser(approveFlowUser);
                    }
                    this.logService.updateLogNormal(logId, table.getId(), Common.toJson(databaseDataMap));

                    return actionResult;
                }
            } else {
                this.approveService.saveApproveUser(form.getTableName(), databaseDataMap, request);
            }

            if (null != request.getApproveBlockEvent().get("submitEvent")) {
                this.approveService.approveApi((ApproveBlockEvent) request.getApproveBlockEvent().get("submitEvent"));
            }

            this.approveService.updateApproveStatus(table.getId(), databaseDataMap.get(idColumns[0]), "2");
        }
        this.logService.updateLogNormal(logId, table.getId(), Common.toJson(databaseDataMap));

        return actionResult;
    }

    @ResponseBody
    @RequestMapping({ "/view.do" })
    public ActionResult switchEdit(@RequestBody DetailRequestModel request, AuthDetails user) {
        DetailFormModel form = (DetailFormModel) request.getWidget();
        form.switchToEditAction();

        DatabaseDataMap databaseDataMap = this.viewService.mapDataToDB(request.getParam());

        TableDescribe table = this.dataService.getTableDesc(form.getTableName());

        this.authService.assertAuth(user, table.getId(),
                table.getTrigger(TriggerDescribe.TriggerType.edit).getAction_id(),
                (String) this.authService.getOwner(databaseDataMap));
        return new ActionResult(true, "Save");
    }

    @RequestMapping({ "/next.view" })
    public ModelAndView next(@RequestBody DetailRequestModel request, AuthDetails user) {
        DetailFormModel form = (DetailFormModel) request.getWidget();
        switch (form.getAction()) {
        case create: {
            form = this.service.createView(form.getTableName(), form.getParent(), form.getFixedData(), user,
                    this.getBuilder());
            form.setPageId(request.getPageId());
            this.modelService.buildModel(form);
            final TableDescribe tableDesc = this.cacheService.getTableDesc(request.getTable());
            if (tableDesc.getIs_approve() == 1) {
                final String[] idColumns = tableDesc.getIdColumns();
                if (idColumns != null && idColumns.length == 1) {
                    form.setHasSaveAndSubmitButton(true);
                }
                form.setFreeApprove(this.approveDao.selectCountApproveTable(form.getTableName()));
            }
            Map<String, Object> data = request.getParentId();
            if (null == data) {
                data = new HashMap<String, Object>();
            }
            this.dataService.prepareAutoKey(tableDesc, data);
            if (data.size() != 0) {
                for (final Map.Entry<String, Object> entry : data.entrySet()) {
                    final FieldModelBase field = form.getField(entry.getKey());
                    if (field != null) {
                        field.setValue(entry.getValue());
                    }
                }
                break;
            }
            break;
        }
        case edit:
        case view: {
            form = this.service.editView(request, user, this.getBuilder());
            this.modelService.buildModel(form);
            break;
        }
        default: {
            throw new ApplicationException("internal exception");
        }
        }
        return this.buildModelAndView((FormModelBase) form);
    }

    @RequestMapping({ "/reload.view" })
    public ModelAndView reload(@RequestBody DetailRequestModel request, AuthDetails user) {
        DetailFormModel form = (DetailFormModel) request.getWidget();
        request.setReadonly((form.getAction() == ActionType.view));
        request.setHasNext(form.isHasNext());
        String tableName = form.getTableName();
        request.setTable(tableName);
        ViewDataMap map = new ViewDataMap();

        if (request.getIsIndexView() == 1) {
            form.setIsIndexView(request.getIsIndexView());
            Map<String, Object> indexParam = new HashMap<String, Object>();
            indexParam.put("url",
                    (request.getUrl() == null || "".equals(request.getUrl())) ? "/detail/edit.view" : request.getUrl());
            indexParam.put("table", request.getTable());
            if (request.getUrl() == null && !"/detail/userEdit.view".equals(request.getUrl()))
                indexParam.put("param", request.getParam());
            indexParam.put("readonly", Boolean.valueOf(request.isReadonly()));
            indexParam.put("isIndexView", Integer.valueOf(request.getIsIndexView()));
            form.setIsIndexViewParam(indexParam);
        }
        TableDescribe table = this.dataService.getTableDesc(tableName);
        for (String column : table.getIdColumns())
            map.put(column, form.getField(column).getValue());
        request.setParam(map);
        if (request.getUrl() != null && "/detail/userEdit.view".equals(request.getUrl())) {
            request.getParam().put("id", user.getId());
        }
        return editView(request, user);
    }

    private boolean hasSaveAndSubmitButton(String tableName, Map<String, Object> filter) {
        TableDescribe tableDesc = this.cacheService.getTableDesc(tableName);
        String[] idColumns = tableDesc.getIdColumns();
        if (idColumns == null || idColumns.length != 1) {
            return false;
        }

        Integer maxSequence = this.approveDao.getMaxSequence(tableName, filter.get(idColumns[0]).toString());
        if (maxSequence == null) {
            maxSequence = Integer.valueOf(1);
        }
        int checkHasSubmitButton = this.approveDao.checkHasSubmitButton(tableName, filter.get(idColumns[0]).toString(),
                maxSequence);
        if (checkHasSubmitButton != 0) {
            return true;
        }
        String approveId = this.approveDao.getApproveId(tableName, filter.get(idColumns[0]).toString());
        if (approveId != null && !"".equals(approveId)) {
            return false;
        }
        return true;
    }

    @ResponseBody
    @RequestMapping({ "/test.do" })
    public ActionResult test(@RequestBody DetailRequestModel request, AuthDetails user,
            HttpServletRequest requestServlet) {
        ModelAndView createView = createView(request, user);
        Object form = createView.getModel().get("form");
        ViewService.cacheForm((DetailFormModel) form, requestServlet);
        ViewDataMap createParam = new ViewDataMap();
        Map<String, FieldModelBase> fieldMap = ((DetailFormModel) form).getFieldMap();
        ViewDataMap param = request.getParam();
        for (Map.Entry<String, Object> fieldValue : param.entrySet()) {
            if (fieldMap.get(fieldValue.getKey()) == null)
                continue;
            String columnId = ((FieldModelBase) fieldMap.get(fieldValue.getKey())).getId();
            createParam.put(columnId, fieldValue.getValue());
        }
        request.setParam(createParam);
        String formId = ((DetailFormModel) form).getId();
        request.setId(formId);
        return create(request, requestServlet, user);
    }

    @ResponseBody
    @RequestMapping({ "/checkAuth.do" })
    public ActionResult test(@RequestBody DetailRequestModel request, AuthDetails user) {
        TableDescribe table = this.dataService.getTableDesc(request.getTable());

        if (!this.authService.optionAuth(user, table.getId(),
                table.getTrigger(TriggerDescribe.TriggerType.add).getAction_id(), "")) {
            return new ActionResult(true, Boolean.valueOf(false));
        }
        return new ActionResult(true, Boolean.valueOf(true));
    }
}
