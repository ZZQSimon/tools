 package cn.com.easyerp.passage;
 
 import java.util.ArrayList;
import java.util.List;
 
 
 
 
 
 
 public class PassageRowDescribe
 {
   private String id;
   private String passage_id;
   private String disp_name_key;
   private String upd_statement;
   private String edit_cond;
   private String edit_part_bg_color;
   private String edit_part_fg_color;
   private String unedit_part_bg_color;
   private String unedit_part_fg_color;
   private int decimal_digit;
   private boolean total_row;
   private String detail_sql;
   private List<PassageRowFormulaModel> formulas = new ArrayList<>();
 
 
   
   public String getId() { return this.id; }
 
 
 
   
   public void setId(String id) { this.id = id; }
 
 
 
   
   public String getPassage_id() { return this.passage_id; }
 
 
 
   
   public void setPassage_id(String passage_id) { this.passage_id = passage_id; }
 
 
 
   
   public String getDisp_name_key() { return this.disp_name_key; }
 
 
 
   
   public void setDisp_name_key(String disp_name_key) { this.disp_name_key = disp_name_key; }
 
 
 
   
   public String getUpd_statement() { return this.upd_statement; }
 
 
 
   
   public void setUpd_statement(String upd_statement) { this.upd_statement = upd_statement; }
 
 
 
   
   public String getEdit_cond() { return this.edit_cond; }
 
 
 
   
   public void setEdit_cond(String edit_cond) { this.edit_cond = edit_cond; }
 
 
 
   
   public String getEdit_part_bg_color() { return this.edit_part_bg_color; }
 
 
 
   
   public void setEdit_part_bg_color(String edit_part_bg_color) { this.edit_part_bg_color = edit_part_bg_color; }
 
 
 
   
   public String getEdit_part_fg_color() { return this.edit_part_fg_color; }
 
 
 
   
   public void setEdit_part_fg_color(String edit_part_fg_color) { this.edit_part_fg_color = edit_part_fg_color; }
 
 
 
   
   public String getUnedit_part_bg_color() { return this.unedit_part_bg_color; }
 
 
 
   
   public void setUnedit_part_bg_color(String unedit_part_bg_color) { this.unedit_part_bg_color = unedit_part_bg_color; }
 
 
 
   
   public String getUnedit_part_fg_color() { return this.unedit_part_fg_color; }
 
 
 
   
   public void setUnedit_part_fg_color(String unedit_part_fg_color) { this.unedit_part_fg_color = unedit_part_fg_color; }
 
 
 
   
   public int getDecimal_digit() { return this.decimal_digit; }
 
 
 
   
   public void setDecimal_digit(int decimal_digit) { this.decimal_digit = decimal_digit; }
 
 
 
   
   public boolean getTotal_row() { return this.total_row; }
 
 
 
   
   public void setTotal_row(boolean total_row) { this.total_row = total_row; }
 
 
 
   
   public String getDetail_sql() { return this.detail_sql; }
 
 
 
   
   public void setDetail_sql(String detail_sql) { this.detail_sql = detail_sql; }
 
 
 
   
   public List<PassageRowFormulaModel> getFormulas() { return this.formulas; }
 
 
 
   
   public void addFormula(PassageRowFormulaModel formula) { this.formulas.add(formula); }
 }


