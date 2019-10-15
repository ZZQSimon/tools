 package cn.com.easyerp.passage;
 
 import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 @Widget("passage")
 public class PassageFormModel
   extends TableBasedFormModel
 {
   private String mainIds;
   private String passageId;
   private String type;
   private boolean hasAppend;
   private boolean searchAll;
   private FilterModel filter;
   private String startDate;
   private String endDate;
   private String groupCols;
   private List<ColumnDescribe> headColumns;
   private List<String> headPassColumns;
   private List<FieldModelBase> searchConditions;
   private Map<String, String> searchConditionsKey;
   private String filterSql;
   private List<Long> headPassColumns_date;
   private String saveMode;
   
   public PassageFormModel(ActionType action, String passageId, String tableName, String mainIds, String type, boolean hasAppend, boolean searchAll, String startDate, String endDate, String groupCols, List<ColumnDescribe> headColumns, List<FieldModelBase> searchConditions, Map<String, String> searchConditionsKey, String filterSql, List<String> headPassColumns, String saveMode) {
     super(action, null, tableName);
     this.mainIds = mainIds;
     this.passageId = passageId;
     this.type = type;
     this.hasAppend = hasAppend;
     this.searchAll = searchAll;
     this.startDate = startDate;
     this.endDate = endDate;
     this.filter = createFilter(tableName, searchConditions, hasAppend);
     this.filter.setParent(getId());
     this.groupCols = groupCols;
     this.headColumns = headColumns;
     this.searchConditions = searchConditions;
     this.searchConditionsKey = searchConditionsKey;
     this.filterSql = filterSql;
     this.headPassColumns = headPassColumns;
     this.saveMode = saveMode;
   }
 
   
   public String getMainIds() { return this.mainIds; }
 
 
   
   public String getPassageId() { return this.passageId; }
 
 
   
   public String getType() { return this.type; }
 
 
   
   public boolean isHasAppend() { return this.hasAppend; }
 
 
   
   public boolean isSearchAll() { return this.searchAll; }
 
 
   
   public String getStartDate() { return this.startDate; }
 
 
   
   public String getEndDate() { return this.endDate; }
 
 
   
   public FilterModel getFilter() { return this.filter; }
 
 
   
   public String getGroupCols() { return this.groupCols; }
 
 
   
   public List<ColumnDescribe> getHeadColumns() { return this.headColumns; }
 
 
   
   public List<String> getHeadPassColumns() { return this.headPassColumns; }
 
 
   
   public List<FieldModelBase> getSearchConditions() { return this.searchConditions; }
 
 
   
   public Map<String, String> getSearchConditionsKey() { return this.searchConditionsKey; }
 
 
   
   public String getFilterSql() { return this.filterSql; }
 
 
   
   public String getSaveMode() { return this.saveMode; }
 
 
   
   @JsonIgnore
   public List<Long> getHeadPassColumns_date() { return this.headPassColumns_date; }
 
 
   
   @JsonIgnore
   public void setHeadPassColumns_date(List<Long> headPassColumns_date) { this.headPassColumns_date = headPassColumns_date; }
 
   
   private FilterModel createFilter(String tableName, List<FieldModelBase> searchConditions, boolean hasAppend) {
     Set<String> needed = new HashSet<String>();
     
     DataService dataService = Common.getDataService();
     TableDescribe tableDesc = dataService.getTableDesc(tableName);
     for (ColumnDescribe column : tableDesc.getColumns()) {
       String columnName = column.getColumn_name();
       needed.add(columnName);
       for (FieldModelBase field : searchConditions) {
         if (tableName.equals(field.getTable()) && columnName
           .equals(field.getColumn())) {
           needed.remove(columnName);
         }
       } 
     } 
 
     
     FilterModel filter = new FilterModel(tableName, "Search", needed);
     
     if (hasAppend) {
       filter.addButton("add", "Add");
     }
     
     return filter;
   }
 }


