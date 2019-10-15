 package cn.com.easyerp.generate;
 
 import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.cache.AutoGenTableDesc;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 public class AutoGenerateFilterModel
   extends FilterModel
 {
   public AutoGenerateFilterModel(AutoGenTableDesc ag) {
     super(ag.getRef_view());
     List<ColumnDescribe> columns = new ArrayList<ColumnDescribe>();
     TableDescribe view = Common.getDataService().getTableDesc(ag.getRef_view());
     for (ColumnDescribe column : view.getColumns()) {
       if (column.getIs_condition().intValue() == 1)
         columns.add(column); 
     }  setColumns(columns);
   }
 
 
 
   
   @JsonIgnore
   public String getType() { return "div"; }
 
 
 
 
   
   public boolean hasReset() { return false; }
 }


