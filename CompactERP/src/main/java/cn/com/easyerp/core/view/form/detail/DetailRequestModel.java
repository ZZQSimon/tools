 package cn.com.easyerp.core.view.form.detail;
 
 import java.util.Map;

import cn.com.easyerp.core.approve.ApproveBlockEvent;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.view.TableFormRequestModel;
 
 
 
 
 
 
 public class DetailRequestModel
   extends TableFormRequestModel<DetailFormModel>
 {
   private ViewDataMap param;
   private ViewDataMap calendarColorParam;
   private ViewDataMap calendarColorStatus;
   private boolean readonly = false;
   private boolean hasNext = false;
   private ViewDataMap fixedData;
   private int isSaveAndSubmit;
   private TriggerDescribe triggerRequestParams;
   private Map<String, TriggerDescribe> childTriggerRequestParams;
   private int isIndexView;
   private String url;
   private Map<String, Object> isIndexViewParam;
   private int isDialog;
   private int isOutPage;
   private Map<String, Object> parentId;
   private String pageId = "1";
   
   private Map<String, Object> userId;
   
   private Map<String, ApproveBlockEvent> approveBlockEvent;
   
   public Map<String, Object> getUserId() { return this.userId; }
 
 
   
   public void setUserId(Map<String, Object> userId) { this.userId = userId; }
 
 
 
   
   public ViewDataMap getParam() { return this.param; }
 
 
 
   
   public void setParam(ViewDataMap param) { this.param = param; }
 
 
   
   public ViewDataMap getCalendarColorParam() { return this.calendarColorParam; }
 
 
   
   public void setCalendarColorParam(ViewDataMap calendarColorParam) { this.calendarColorParam = calendarColorParam; }
 
 
   
   public ViewDataMap getCalendarColorStatus() { return this.calendarColorStatus; }
 
 
   
   public void setCalendarColorStatus(ViewDataMap calendarColorStatus) { this.calendarColorStatus = calendarColorStatus; }
 
 
 
   
   public boolean isReadonly() { return this.readonly; }
 
 
 
   
   public void setReadonly(boolean readonly) { this.readonly = readonly; }
 
 
 
   
   public boolean isHasNext() { return this.hasNext; }
 
 
 
   
   public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
 
 
 
   
   public ViewDataMap getFixedData() { return this.fixedData; }
 
 
 
   
   public void setFixedData(ViewDataMap fixedData) { this.fixedData = fixedData; }
 
 
   
   public int getIsSaveAndSubmit() { return this.isSaveAndSubmit; }
 
 
   
   public void setIsSaveAndSubmit(int isSaveAndSubmit) { this.isSaveAndSubmit = isSaveAndSubmit; }
 
 
   
   public TriggerDescribe getTriggerRequestParams() { return this.triggerRequestParams; }
 
 
   
   public void setTriggerRequestParams(TriggerDescribe triggerRequestParams) { this.triggerRequestParams = triggerRequestParams; }
 
 
   
   public Map<String, TriggerDescribe> getChildTriggerRequestParams() { return this.childTriggerRequestParams; }
 
 
   
   public void setChildTriggerRequestParams(Map<String, TriggerDescribe> childTriggerRequestParams) { this.childTriggerRequestParams = childTriggerRequestParams; }
 
 
   
   public int getIsIndexView() { return this.isIndexView; }
 
 
   
   public void setIsIndexView(int isIndexView) { this.isIndexView = isIndexView; }
 
 
   
   public String getUrl() { return this.url; }
 
 
   
   public void setUrl(String url) { this.url = url; }
 
 
   
   public Map<String, Object> getIsIndexViewParam() { return this.isIndexViewParam; }
 
 
   
   public void setIsIndexViewParam(Map<String, Object> isIndexViewParam) { this.isIndexViewParam = isIndexViewParam; }
 
 
   
   public int getIsDialog() { return this.isDialog; }
 
 
   
   public void setIsDialog(int isDialog) { this.isDialog = isDialog; }
 
 
   
   public int getIsOutPage() { return this.isOutPage; }
 
 
   
   public void setIsOutPage(int isOutPage) { this.isOutPage = isOutPage; }
 
 
   
   public Map<String, Object> getParentId() { return this.parentId; }
 
 
   
   public void setParentId(Map<String, Object> parentId) { this.parentId = parentId; }
 
 
   
   public String getPageId() { return this.pageId; }
 
 
   
   public void setPageId(String pageId) { this.pageId = pageId; }
 
 
   
   public Map<String, ApproveBlockEvent> getApproveBlockEvent() { return this.approveBlockEvent; }
 
 
   
   public void setApproveBlockEvent(Map<String, ApproveBlockEvent> approveBlockEvent) { this.approveBlockEvent = approveBlockEvent; }
 }


