 package cn.com.easyerp.passage;
 
 import java.util.ArrayList;
import java.util.List;
 
 
 
 
 
 
 public class PassageDescribe
 {
   private String id;
   private String name;
   private int type;
   private String main_table;
   private String group_cols;
   private String disp_cols;
   private String filter_tables;
   private String filter_sql;
   private List<PassageRowDescribe> rows = new ArrayList<>();
 
 
   
   public String getId() { return this.id; }
 
 
 
   
   public void setId(String id) { this.id = id; }
 
 
 
   
   public String getName() { return this.name; }
 
 
 
   
   public void setName(String name) { this.name = name; }
 
 
 
   
   public int getType() { return this.type; }
 
 
 
   
   public void setType(int type) { this.type = type; }
 
 
 
   
   public String getMain_table() { return this.main_table; }
 
 
 
   
   public void setMain_table(String main_table) { this.main_table = main_table; }
 
 
 
   
   public String getGroup_cols() { return this.group_cols; }
 
 
 
   
   public void setGroup_cols(String group_cols) { this.group_cols = group_cols; }
 
 
 
   
   public String getDisp_cols() { return this.disp_cols; }
 
 
 
   
   public void setDisp_cols(String disp_cols) { this.disp_cols = disp_cols; }
 
 
 
   
   public String getFilter_tables() { return this.filter_tables; }
 
 
 
   
   public void setFilter_tables(String filter_tables) { this.filter_tables = filter_tables; }
 
 
 
   
   public String getFilter_sql() { return this.filter_sql; }
 
 
 
   
   public void setFilter_sql(String filter_sql) { this.filter_sql = filter_sql; }
 
 
 
   
   public void addRow(PassageRowDescribe row) { this.rows.add(row); }
 
 
 
   
   public List<PassageRowDescribe> getRows() { return this.rows; }
 }


