 package cn.com.easyerp.core.view.form.index;
 
 import cn.com.easyerp.core.widget.WidgetModelBase;
 
 
 
 public class WorkModel
   extends WidgetModelBase
 {
   private String id;
   private String name;
   private int end_col;
   private int start_col;
   private int end_row;
   private int start_row;
   private String itemType;
   private String start_date;
   private String end_date;
   private String plan;
   private String sql;
   private String background_color;
   private String time;
   private String timeNow;
   private String timeEnd;
   private String plan_Number;
   private String sql_Number;
   private int height;
   private int width;
   
   public int getHeight() { return this.height; }
 
 
   
   public void setHeight(int height) { this.height = height; }
 
 
   
   public int getWidth() { return this.width; }
 
 
   
   public void setWidth(int width) { this.width = width; }
 
 
 
 
 
 
 
   
   public WorkModel() { super(""); }
 
 
 
 
   
   public String idPrefix() { return "m"; }
 
 
   
   public String getId() { return this.id; }
 
 
   
   public void setId(String id) { this.id = id; }
 
 
   
   public String getStart_date() { return this.start_date; }
 
 
   
   public void setStart_date(String start_date) { this.start_date = start_date; }
 
 
   
   public String getEnd_date() { return this.end_date; }
 
 
   
   public void setEnd_date(String end_date) { this.end_date = end_date; }
 
 
   
   public String getPlan() { return this.plan; }
 
 
   
   public void setPlan(String plan) { this.plan = plan; }
 
 
   
   public String getSql() { return this.sql; }
 
 
   
   public void setSql(String sql) { this.sql = sql; }
 
 
   
   public int getEnd_col() { return this.end_col; }
 
 
   
   public void setEnd_col(int end_col) { this.end_col = end_col; }
 
 
   
   public int getStart_col() { return this.start_col; }
 
 
   
   public void setStart_col(int start_col) { this.start_col = start_col; }
 
 
   
   public int getEnd_row() { return this.end_row; }
 
 
   
   public void setEnd_row(int end_row) { this.end_row = end_row; }
 
 
   
   public int getStart_row() { return this.start_row; }
 
 
   
   public void setStart_row(int start_row) { this.start_row = start_row; }
 
 
   
   public String getItemType() { return this.itemType; }
 
 
   
   public void setItemType(String itemType) { this.itemType = itemType; }
 
   
   public String getName() { return this.name; }
 
 
   
   public void setName(String name) { this.name = name; }
 
 
   
   public String getBackground_color() { return this.background_color; }
 
 
   
   public void setBackground_color(String background_color) { this.background_color = background_color; }
 
 
   
   public String getTimeNow() { return this.timeNow; }
 
 
   
   public void setTimeNow(String timeNow) { this.timeNow = timeNow; }
 
 
   
   public String getTimeEnd() { return this.timeEnd; }
 
 
   
   public void setTimeEnd(String timeEnd) { this.timeEnd = timeEnd; }
 
 
   
   public String getPlan_Number() { return this.plan_Number; }
 
 
   
   public void setPlan_Number(String plan_Number) { this.plan_Number = plan_Number; }
 
 
   
   public String getSql_Number() { return this.sql_Number; }
 
 
   
   public void setSql_Number(String sql_Number) { this.sql_Number = sql_Number; }
 
 
   
   public String getTime() { return this.time; }
 
 
   
   public void setTime(String time) { this.time = time; }
 }


