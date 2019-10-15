 package cn.com.easyerp.core.view.form.index;
 
 import java.util.List;

import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
 
 
 
 
 
 
 
 
 public class OpRecordModel
   extends RecordModel
 {
   private String table_id;
   private List<String> action_ids;
   private String status_now;
   private String status_col;
   private String key_value;
   private String cre_date;
   private String cre_user;
   private String disp_table_id;
   
   public OpRecordModel(List<FieldModelBase> fields) { super(fields); }
 
 
 
   
   public String getTable_id() { return this.table_id; }
 
 
 
   
   public void setTable_id(String table_id) { this.table_id = table_id; }
 
 
 
   
   public String getDisp_table_id() { return this.disp_table_id; }
 
 
 
   
   public void setDisp_table_id(String disp_table_id) { this.disp_table_id = disp_table_id; }
 
 
 
   
   public List<String> getAction_ids() { return this.action_ids; }
 
 
 
   
   public void setAction_ids(List<String> action_ids) { this.action_ids = action_ids; }
 
 
 
   
   public String getStatus_now() { return this.status_now; }
 
 
 
   
   public void setStatus_now(String status_now) { this.status_now = status_now; }
 
 
 
   
   public String getStatus_col() { return this.status_col; }
 
 
 
   
   public void setStatus_col(String status_col) { this.status_col = status_col; }
 
 
 
   
   public String getKey_value() { return this.key_value; }
 
 
 
   
   public void setKey_value(String key_value) { this.key_value = key_value; }
 
 
 
 
   
   public String idPrefix() { return "m"; }
 
 
 
   
   public String getCre_date() { return this.cre_date; }
 
 
 
   
   public void setCre_date(String cre_date) { this.cre_date = cre_date; }
 
 
 
   
   public String getCre_user() { return this.cre_user; }
 
 
 
   
   public void setCre_user(String cre_user) { this.cre_user = cre_user; }
 }


