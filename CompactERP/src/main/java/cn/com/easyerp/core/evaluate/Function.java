 package cn.com.easyerp.core.evaluate;
 import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.evaluate.DataModel.ChildModel;
import cn.com.easyerp.core.evaluate.DataModel.DataModel;
import cn.com.easyerp.core.evaluate.DataModel.Model;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.widget.FieldModelBase;
 
 @Service
 public class Function {
   private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
   private static SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd"); @Autowired
   private SystemDao systemDao;
   @Autowired
   private CacheService cacheService;
   
   public int sumFunction(String funVal, Model cacheModel) {
     if (cacheModel == null)
       return 0; 
     if (funVal == null || "".equals(funVal))
       return 0; 
     String[] values = funVal.split("\\.");
     if (values.length != 2)
       return 0; 
     Map<String, ChildModel> children = cacheModel.getChildren();
     if (children == null || children.size() == 0)
       return 0; 
     int sum = 0;
     for (Map.Entry<String, ChildModel> child : children.entrySet()) {
       if (values[0].equals(((ChildModel)child.getValue()).getTableName())) {
         List<DataModel> childrenDatas = ((ChildModel)child.getValue()).getChildrenDatas();
         if (childrenDatas == null || childrenDatas.size() == 0) {
           return 0;
         }
         for (int i = 0; i < childrenDatas.size(); i++) {
           if (!"delete".equals(((DataModel)childrenDatas.get(i)).getStatus())) {
             
             if (!((DataModel)childrenDatas.get(i)).getData().containsKey(values[1])) {
               throw new ApplicationException("no child key: " + values[1]);
             }
             Object value = ((FieldModelBase)((DataModel)childrenDatas.get(i)).getData().get(values[1])).getValue();
             if (value == null) {
               value = Integer.valueOf(0);
             } else if (value instanceof Integer) {
               sum += ((Integer)value).intValue();
             } else if (value instanceof Double) {
               sum = (int)(sum + ((Double)value).doubleValue());
             } else if (value instanceof BigDecimal) {
               sum = (int)(sum + ((BigDecimal)value).doubleValue());
             } else {
               sum += Integer.parseInt(value.toString());
             } 
           } 
         }  return sum;
       } 
     } 
     
     return 0;
   }
   public String maxFunction(String funVal, Model cacheModel) {
     if (cacheModel == null)
       return null; 
     if (funVal == null || "".equals(funVal))
       return null; 
     String[] values = funVal.split(".");
     if (values.length != 2)
       return null; 
     Map<String, ChildModel> children = cacheModel.getChildren();
     if (children == null || children.size() == 0)
       return null; 
     Integer max = null;
     for (Map.Entry<String, ChildModel> child : children.entrySet()) {
       if (values[0].equals(((ChildModel)child.getValue()).getTableName())) {
         List<DataModel> childrenDatas = ((ChildModel)child.getValue()).getChildrenDatas();
         if (childrenDatas == null || childrenDatas.size() == 0) {
           return null;
         }
         for (int i = 0; i < childrenDatas.size(); i++) {
           if (!((DataModel)childrenDatas.get(i)).getData().containsKey(values[1])) {
             throw new ApplicationException("no child key: " + values[1]);
           }
           Object value = ((FieldModelBase)((DataModel)childrenDatas.get(i)).getData().get(values[1])).getValue();
           if (value == null)
             value = Integer.valueOf(0); 
           int intVal = Integer.parseInt(value.toString());
           if (max == null)
             max = Integer.valueOf(intVal); 
           if (max.intValue() < intVal) {
             max = Integer.valueOf(intVal);
           }
         } 
         return (max == null) ? null : max.toString();
       } 
     } 
     
     return null;
   }
   public String minFunction(String funVal, Model cacheModel) {
     if (cacheModel == null)
       return null; 
     if (funVal == null || "".equals(funVal))
       return null; 
     String[] values = funVal.split(".");
     if (values.length != 2)
       return null; 
     Map<String, ChildModel> children = cacheModel.getChildren();
     if (children == null || children.size() == 0)
       return null; 
     Integer min = null;
     for (Map.Entry<String, ChildModel> child : children.entrySet()) {
       if (values[0].equals(((ChildModel)child.getValue()).getTableName())) {
         List<DataModel> childrenDatas = ((ChildModel)child.getValue()).getChildrenDatas();
         if (childrenDatas == null || childrenDatas.size() == 0) {
           return null;
         }
         for (int i = 0; i < childrenDatas.size(); i++) {
           if (!((DataModel)childrenDatas.get(i)).getData().containsKey(values[1])) {
             throw new ApplicationException("no child key: " + values[1]);
           }
           Object value = ((FieldModelBase)((DataModel)childrenDatas.get(i)).getData().get(values[1])).getValue();
           if (value == null)
             value = Integer.valueOf(0); 
           int intVal = Integer.parseInt(value.toString());
           if (min == null)
             min = Integer.valueOf(intVal); 
           if (min.intValue() > intVal) {
             min = Integer.valueOf(intVal);
           }
         } 
         return (min == null) ? null : min.toString();
       } 
     } 
     
     return null;
   }
   public String uniqueFunction(String funVal, Model cacheModel) {
     if (cacheModel == null)
       return "false"; 
     if (funVal == null || "".equals(funVal))
       return "false"; 
     String[] values = funVal.split(".");
     if (values.length != 2)
       return "false"; 
     Map<String, ChildModel> children = cacheModel.getChildren();
     if (children == null || children.size() == 0)
       return "false"; 
     Set<Object> valueSet = new HashSet<Object>();
     for (Map.Entry<String, ChildModel> child : children.entrySet()) {
       if (values[0].equals(((ChildModel)child.getValue()).getTableName())) {
         List<DataModel> childrenDatas = ((ChildModel)child.getValue()).getChildrenDatas();
         if (childrenDatas == null || childrenDatas.size() == 0) {
           return "true";
         }
         for (int i = 0; i < childrenDatas.size(); i++) {
           if (!((DataModel)childrenDatas.get(i)).getData().containsKey(values[1])) {
             throw new ApplicationException("no child key: " + values[1]);
           }
           Object value = ((FieldModelBase)((DataModel)childrenDatas.get(i)).getData().get(values[1])).getValue();
           if (value == null)
             value = ""; 
           if (valueSet.contains(value)) {
             return "false";
           }
           valueSet.add(value);
         } 
         
         return "true";
       } 
     } 
     
     return "false";
   }
 
   
   public String uniqueCountFunction(String funVal, Model cacheModel) { return ""; }
   
   public int countFunction(String funVal, Model cacheModel) {
     if (cacheModel == null)
       return 0; 
     if (funVal == null || "".equals(funVal))
       return 0; 
     String[] values = funVal.split(".");
     if (values.length != 1)
       return 0; 
     Map<String, ChildModel> children = cacheModel.getChildren();
     if (children == null || children.size() == 0)
       return 0; 
     for (Map.Entry<String, ChildModel> child : children.entrySet()) {
       if (values[0].equals(((ChildModel)child.getValue()).getTableName())) {
         List<DataModel> childrenDatas = ((ChildModel)child.getValue()).getChildrenDatas();
         if (childrenDatas == null || childrenDatas.size() == 0) {
           return 0;
         }
         return childrenDatas.size();
       } 
     } 
     
     return 0;
   }
   public String sqlFunction(String funVal, Model cacheModel) {
     funVal = funVal.replace("&lt;", "<");
     funVal = funVal.replace("&gt;", ">");
     Object valueMap = this.systemDao.execEvalSql(funVal);
     if (valueMap != null && valueMap instanceof Map) {
       Object[] values = ((Map)valueMap).values().toArray();
       if (values.length > 0) {
         if (values[0] instanceof String) {
           return "'" + values[0] + "'";
         }
         return values[0] + "";
       } 
     } 
     
     return "''";
   }
   public String dateFunction(String funVal, Model cacheModel) {
     if (funVal == null || "".equals(funVal)) {
       return (new Date()).getTime() + "";
     }
     try {
       Date date = formatter.parse(funVal);
       return date.getTime() + "";
     } catch (Exception e) {
       try {
         Date date = formatter1.parse(funVal);
         return date.getTime() + "";
       } catch (Exception a) {
 
 
         
         return funVal;
       } 
     } 
   }
   public String dateTextFunction(String funVal, Model cacheModel) { return ""; }
 
 
   
   public String origFunction(String funVal, Model cacheModel) { return ""; }
 
 
   
   public String dictFunction(String funVal, Model cacheModel) { return ""; }
 
   
   public String domainKeyFunction(String funVal, Model cacheModel) { return this.cacheService.getDomainKey(); }
 }


