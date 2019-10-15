 package cn.com.easyerp.operation;
 
 import java.util.List;

import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 
 
 @Widget("operation")
 public class OperationFormModel
   extends DetailFormModel
 {
   private String operationId;
   private int seq;
   private String event_id;
   private String column;
   
   public String getEvent_id() { return this.event_id; }
 
 
   
   public void setEvent_id(String event_id) { this.event_id = event_id; }
 
 
 
   
   public OperationFormModel(ActionType action, String parent, TriggerDescribe tableAction, String column, List<FieldModelBase> fields) {
     super(action, parent, tableAction.getAction_id(), fields, null, 1);
     this.operationId = tableAction.getAction_id();
     this.column = column;
   }
 
   
   public OperationFormModel(ActionType action, String parent, TriggerDescribe tableAction, String column, List<FieldModelBase> fields, String tableName) {
     super(action, parent, tableName, fields, null, 1);
     this.operationId = tableAction.getAction_id();
     this.column = column;
   }
 
   
   public int getSeq() { return this.seq; }
 
 
   
   public void setSeq(int seq) { this.seq = seq; }
 
 
   
   public void setOperationId(String operationId) { this.operationId = operationId; }
 
 
 
   
   public String getOperationId() { return this.operationId; }
 
 
 
   
   public String getColumn() { return this.column; }
 
 
 
 
   
   public String getButtonText() { return "Exec"; }
 
 
 
 
   
   protected String getView() { return "detail"; }
 }


