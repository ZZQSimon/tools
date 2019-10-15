 package cn.com.easyerp.core.view.form.index;
 
 import cn.com.easyerp.core.widget.WidgetModelBase;
 
 
 
 
 
 
 
 public class DesktopItemModel
   extends WidgetModelBase
 {
   private String id;
   private String name;
   private String item_type;
   private String bulletin_id;
   private String work_plan_id;
   private String date_reminder_id;
   private String chart_id;
   private String shortcut_id;
   private String list_id;
   private int start_col;
   private int end_col;
   private int start_row;
   private int end_row;
   private String owner;
   private String content;
   private String columns;
   private String sql;
   private String background_color;
   
   public DesktopItemModel() { super(""); }
 
 
 
   
   public String getBulletin_id() { return this.bulletin_id; }
 
 
 
   
   public void setBulletin_id(String bulletin_id) { this.bulletin_id = bulletin_id; }
 
 
 
   
   public String getWork_plan_id() { return this.work_plan_id; }
 
 
 
   
   public void setWork_plan_id(String work_plan_id) { this.work_plan_id = work_plan_id; }
 
 
 
   
   public String getDate_reminder_id() { return this.date_reminder_id; }
 
 
 
   
   public void setDate_reminder_id(String date_reminder_id) { this.date_reminder_id = date_reminder_id; }
 
 
 
   
   public String getShortcut_id() { return this.shortcut_id; }
 
 
 
   
   public void setShortcut_id(String shortcut_id) { this.shortcut_id = shortcut_id; }
 
 
 
   
   public String getContent() { return this.content; }
 
 
 
   
   public void setContent(String content) { this.content = content; }
 
 
 
   
   public String getColumns() { return this.columns; }
 
 
 
   
   public void setColumns(String columns) { this.columns = columns; }
 
 
 
   
   public String getSql() { return this.sql; }
 
 
 
   
   public void setSql(String sql) { this.sql = sql; }
 
 
 
 
   
   public String idPrefix() { return "m"; }
 
 
 
   
   public String getId() { return this.id; }
 
 
 
   
   public void setId(String id) { this.id = id; }
 
 
 
   
   public String getName() { return this.name; }
 
 
 
   
   public void setName(String name) { this.name = name; }
 
 
 
   
   public String getItem_type() { return this.item_type; }
 
 
 
   
   public void setItem_type(String item_type) { this.item_type = item_type; }
 
 
 
   
   public int getStart_col() { return this.start_col; }
 
 
 
   
   public void setStart_col(int start_col) { this.start_col = start_col; }
 
 
 
   
   public int getEnd_col() { return this.end_col; }
 
 
 
   
   public void setEnd_col(int end_col) { this.end_col = end_col; }
 
 
 
   
   public int getStart_row() { return this.start_row; }
 
 
 
   
   public void setStart_row(int start_row) { this.start_row = start_row; }
 
 
 
   
   public int getEnd_row() { return this.end_row; }
 
 
 
   
   public void setEnd_row(int end_row) { this.end_row = end_row; }
 
 
 
   
   public String getOwner() { return this.owner; }
 
 
 
   
   public void setOwner(String owner) { this.owner = owner; }
 
 
 
   
   public String getBackground_color() { return this.background_color; }
 
 
 
   
   public void setBackground_color(String background_color) { this.background_color = background_color; }
 
 
 
   
   public String getChart_id() { return this.chart_id; }
 
 
 
   
   public void setChart_id(String chart_id) { this.chart_id = chart_id; }
 
 
 
   
   public String getList_id() { return this.list_id; }
 
 
 
   
   public void setList_id(String list_id) { this.list_id = list_id; }
 }


