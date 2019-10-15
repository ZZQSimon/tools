 package cn.com.easyerp.passage;
 
 import java.util.List;
import java.util.Map;

import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 public class PassageRecordModel
   extends WidgetModelBase
 {
   private String mainId;
   private Map<String, String> mainIds;
   private List<FieldModelBase> fields;
   private List<PassageFieldModel> passageFields;
   private String passageRowId;
   private String dispNameKey;
   private String updStatement;
   private String editCond;
   private boolean totalRow;
   private boolean totalCol;
   private String editPartBgColor;
   private String editPartFgColor;
   private String uneditPartBgColor;
   private String uneditPartFgColor;
   private int decimalDigit;
   private boolean detailSQL;
   private List<Map<String, Object>> resultList;
   
   public PassageRecordModel(String mainId, Map<String, String> mainIds, List<FieldModelBase> fields, List<PassageFieldModel> passageFields, String passageRowId, String dispNameKey, String updStatement, String editCond, boolean totalRow, boolean totalCol, String editPartBgColor, String editPartFgColor, String uneditPartBgColor, String uneditPartFgColor, int decimalDigit, boolean detailSQL, List<Map<String, Object>> resultList) {
     this.mainId = mainId;
     this.mainIds = mainIds;
     this.fields = fields;
     this.passageFields = passageFields;
     this.passageRowId = passageRowId;
     this.dispNameKey = dispNameKey;
     this.updStatement = updStatement;
     this.editCond = editCond;
     this.totalRow = totalRow;
     this.totalCol = totalCol;
     this.editPartBgColor = editPartBgColor;
     this.editPartFgColor = editPartFgColor;
     this.uneditPartBgColor = uneditPartBgColor;
     this.uneditPartFgColor = uneditPartFgColor;
     this.decimalDigit = decimalDigit;
     this.detailSQL = detailSQL;
     this.resultList = resultList;
   }
 
   
   public String getMainId() { return this.mainId; }
 
 
   
   public Map<String, String> getMainIds() { return this.mainIds; }
 
 
   
   public List<FieldModelBase> getFields() { return this.fields; }
 
 
   
   public List<PassageFieldModel> getPassageFields() { return this.passageFields; }
 
 
   
   public String getPassageRowId() { return this.passageRowId; }
 
 
   
   public String getDispNameKey() { return this.dispNameKey; }
 
 
   
   public String getUpdStatement() { return this.updStatement; }
 
 
   
   public String getEditCond() { return this.editCond; }
 
 
   
   public boolean isTotalRow() { return this.totalRow; }
 
 
   
   public boolean isTotalCol() { return this.totalCol; }
 
 
   
   public String geteditPartBgColor() { return this.editPartBgColor; }
 
 
   
   public String geteditPartFgColor() { return this.editPartFgColor; }
 
 
   
   public String getUneditPartBgColor() { return this.uneditPartBgColor; }
 
 
   
   public String getUneditPartFgColor() { return this.uneditPartFgColor; }
 
 
   
   public int getDecimalDigit() { return this.decimalDigit; }
 
 
   
   public boolean isDetailSQL() { return this.detailSQL; }
 
 
   
   public List<Map<String, Object>> getResultList() { return this.resultList; }
 }


