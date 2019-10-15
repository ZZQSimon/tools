package cn.com.easyerp.core.view.form.detail;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.approve.ApproveFlowUser;
import cn.com.easyerp.core.companyCalendar.CalendarEvent;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("detail")
public class DetailFormModel extends TableBasedFormModel {
    private List<FieldModelBase> fields;
    private List<GridModel> children;
    private int cols;
    private boolean hasNext = false;
    private Set<String> noAuthColumns = null;

    private Set<String> readonlyColumns = null;

    private Map<String, FieldModelBase> fieldMap;
    private ViewDataMap fixedData;
    private ApproveFlow approveFlow;
    private boolean hasSaveAndSubmitButton;
    private int isIndexView;
    private String url;
    private Map<String, Object> isIndexViewParam;
    private List<AuthDetails> getAllUser;
    private int isOutPage;
    private List<ApproveFlowUser> rejectRecord;
    private List<GridModel> refChildTables;
    private String initator;
    private Date byTheTime;
    private String pageId = "";
    private String initiatorId;
    private List<CalendarEvent> calendarEvents;
    private int freeApprove = 1;

    private boolean hasApproveBackButton;

    private boolean hasEditButton;

    public String getInitator() {
        return this.initator;
    }

    public void setInitator(String initator) {
        this.initator = initator;
    }

    public Date getByTheTime() {
        return this.byTheTime;
    }

    public void setByTheTime(Date byTheTime) {
        this.byTheTime = byTheTime;
    }

    public List<ApproveFlowUser> getRejectRecord() {
        return this.rejectRecord;
    }

    public void setRejectRecord(List<ApproveFlowUser> rejectRecord) {
        this.rejectRecord = rejectRecord;
    }

    public List<AuthDetails> getGetAllUser() {
        return this.getAllUser;
    }

    public void setGetAllUser(List<AuthDetails> getAllUser) {
        this.getAllUser = getAllUser;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public DetailFormModel(ActionType action, String parent, String tableName, List<FieldModelBase> fields,
            List<GridModel> children, int cols) {
        super(action, parent, tableName);
        this.fields = fields;
        this.cols = cols;
        this.children = children;
        if (children != null)
            for (GridModel child : children)
                child.setParent(getId());
        for (FieldModelBase field : fields)
            field.setParent(getId());
        this.fieldMap = new HashMap<>(fields.size());
        for (FieldModelBase field : fields) {
            this.fieldMap.put(field.getColumn(), field);
        }
    }

    @JsonIgnore
    public List<FieldModelBase> getFields() {
        return this.fields;
    }

    public FieldModelBase getField(String column) {
        return (FieldModelBase) this.fieldMap.get(column);
    }

    @JsonIgnore
    public Map<String, FieldModelBase> getFieldMap() {
        return this.fieldMap;
    }

    public List<GridModel> getChildren() {
        return this.children;
    }

    public int getCols() {
        return this.cols;
    }

    public boolean isHasNext() {
        return this.hasNext;
    }

    public void setHasNext(boolean hasNext) {
        this.hasNext = hasNext;
    }

    @JsonIgnore
    public Set<String> getNoAuthColumns() {
        return this.noAuthColumns;
    }

    public void setNoAuthColumns(Set<String> noAuthColumns) {
        this.noAuthColumns = noAuthColumns;
    }

    public Set<String> getReadonlyColumns() {
        return this.readonlyColumns;
    }

    public void setReadonlyColumns(Set<String> readonlyColumns) {
        this.readonlyColumns = readonlyColumns;
    }

    public ViewDataMap getFixedData() {
        return this.fixedData;
    }

    public void setFixedData(ViewDataMap fixedData) {
        this.fixedData = fixedData;
    }

    public String getButtonText() {
        return (getAction() == ActionType.view) ? "Edit" : "Save";
    }

    public void switchToEditAction() {
        setAction(ActionType.edit);
    }

    protected String urlBase() {
        return "/" + getWidgetName();
    }

    public String getSubmitUrl() {
        return urlBase() + "/" + getAction() + ".do";
    }

    public String getNextUrl() {
        return urlBase() + "/next.view";
    }

    public ApproveFlow getApproveFlow() {
        return this.approveFlow;
    }

    public void setApproveFlow(ApproveFlow approveFlow) {
        this.approveFlow = approveFlow;
    }

    public boolean isHasSaveAndSubmitButton() {
        return this.hasSaveAndSubmitButton;
    }

    public void setHasSaveAndSubmitButton(boolean hasSaveAndSubmitButton) {
        this.hasSaveAndSubmitButton = hasSaveAndSubmitButton;
    }

    public int getIsIndexView() {
        return this.isIndexView;
    }

    public void setIsIndexView(int isIndexView) {
        this.isIndexView = isIndexView;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getIsIndexViewParam() {
        return this.isIndexViewParam;
    }

    public void setIsIndexViewParam(Map<String, Object> isIndexViewParam) {
        this.isIndexViewParam = isIndexViewParam;
    }

    public int getIsOutPage() {
        return this.isOutPage;
    }

    public void setIsOutPage(int isOutPage) {
        this.isOutPage = isOutPage;
    }

    public List<GridModel> getRefChildTables() {
        return this.refChildTables;
    }

    public void setRefChildTables(List<GridModel> refChildTables) {
        this.refChildTables = refChildTables;
    }

    public String getPageId() {
        return this.pageId;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public String getInitiatorId() {
        return this.initiatorId;
    }

    public void setInitiatorId(String initiatorId) {
        this.initiatorId = initiatorId;
    }

    public List<CalendarEvent> getCalendarEvents() {
        return this.calendarEvents;
    }

    public void setCalendarEvents(List<CalendarEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public int getFreeApprove() {
        return this.freeApprove;
    }

    public void setFreeApprove(int freeApprove) {
        this.freeApprove = freeApprove;
    }

    public boolean isHasApproveBackButton() {
        return this.hasApproveBackButton;
    }

    public void setHasApproveBackButton(boolean hasApproveBackButton) {
        this.hasApproveBackButton = hasApproveBackButton;
    }

    public boolean isHasEditButton() {
        return this.hasEditButton;
    }

    public void setHasEditButton(boolean hasEditButton) {
        this.hasEditButton = hasEditButton;
    }
}
