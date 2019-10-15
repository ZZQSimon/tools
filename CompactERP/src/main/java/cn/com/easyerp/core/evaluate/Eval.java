 package cn.com.easyerp.core.evaluate;
 
 import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.dao.SystemDao;
 
 
 
 
 
 
 
 
 
 
 @Service
 public class Eval
 {
   @Autowired
   private SystemDao systemDao;
   public static String OPTS = "+-*/%><][!|&=#";
   public static String FUNCTION_PATTERN = "(dxf\\..*|[a-zA-Z0-9_]+)\\((.*)\\)$";
   public Object calculate(String expression) throws Exception {
     try {
       Stack Opts = new Stack();
       Stack Values = new Stack();
       String exp = expression + "#";
       int nCount = exp.length();
       Opts.push("#");
       String temp = "", optOut = "", optIn = "", value1 = "", value2 = "", optTemp = "", opt = "", temp1 = "";
       int nFun = 0;
       boolean isFun = false;
       for (int i = 0; i < nCount; ) {
         int nTemp = 0;
         opt = exp.substring(i, i + 1);
         isFun = false;
         temp1 = "";
         while (i < nCount) {
           if (!temp1.equals("")) {
             if (opt.equals("(")) {
               nFun++;
               isFun = true;
             } else if (opt.equals(")")) {
               nFun--;
             } 
           }
           if (nFun > 0 || (!isFun && isValue(opt))) {
             temp1 = temp1 + opt;
             nTemp++;
             opt = exp.substring(i + nTemp, i + nTemp + 1); continue;
           } 
           if (isFun) {
             temp1 = temp1 + opt;
             nTemp++;
           } 
         } 
 
         
         if (temp1.equals("")) {
           temp = opt;
         } else {
           temp = temp1;
         } 
         if (nTemp > 0) {
           i = i + nTemp - 1;
         }
         temp = temp.trim();
         
         if (isValue(temp)) {
           temp = getValue(temp);
           Values.push(temp);
           i++; continue;
         } 
         optIn = Opts.pop().toString();
         int nIn = getOptPriorityIn(optIn);
         int nOut = getOptPriorityOut(temp);
         if (nIn == nOut) {
           i++; continue;
         }  if (nIn > nOut) {
           String ret = "";
           value1 = Values.pop().toString();
           value2 = Values.pop().toString();
           ret = String.valueOf(calValue(value2, optIn, value1));
           Values.push(ret); continue;
         }  if (nIn < nOut) {
           Opts.push(optIn);
           Opts.push(temp);
           i++;
         } 
       } 
       
       return Values.pop();
     } catch (Exception e) {
       e.printStackTrace();
       throw new Exception("表达式" + expression + "格式非法!");
     } 
   }
   
   private int getOptPriorityOut(String opt) throws Exception {
     if (opt.equals("+"))
       return 1; 
     if (opt.equals("-"))
       return 2; 
     if (opt.equals("*"))
       return 5; 
     if (opt.equals("/"))
       return 6; 
     if (opt.equals("%"))
       return 7; 
     if (opt.equals(">"))
       return 11; 
     if (opt.equals("<"))
       return 12; 
     if (opt.equals("]"))
       return 13; 
     if (opt.equals("["))
       return 14; 
     if (opt.equals("!"))
       return 15; 
     if (opt.equals("|"))
       return 16; 
     if (opt.equals("&"))
       return 23; 
     if (opt.equals("="))
       return 25; 
     if (opt.equals("#"))
       return 0; 
     if (opt.equals("("))
       return 1000; 
     if (opt.equals(")")) {
       return -1000;
     }
     throw new Exception("运算符" + opt + "非法!");
   }
   
   private int getOptPriorityIn(String opt) throws Exception {
     if (opt.equals("+"))
       return 3; 
     if (opt.equals("-"))
       return 4; 
     if (opt.equals("*"))
       return 8; 
     if (opt.equals("/"))
       return 9; 
     if (opt.equals("%"))
       return 10; 
     if (opt.equals(">"))
       return 17; 
     if (opt.equals("<"))
       return 18; 
     if (opt.equals("]"))
       return 19; 
     if (opt.equals("["))
       return 20; 
     if (opt.equals("!"))
       return 21; 
     if (opt.equals("|"))
       return 22; 
     if (opt.equals("&"))
       return 24; 
     if (opt.equals("="))
       return 26; 
     if (opt.equals("("))
       return -1000; 
     if (opt.equals(")"))
       return 1000; 
     if (opt.equals("#")) {
       return 0;
     }
     throw new Exception("运算符" + opt + "非法！");
   }
   
   private boolean isValue(String cValue) {
     String notValue = OPTS + "()";
     return (notValue.indexOf(cValue) == -1);
   }
 
   
   private boolean isOpt(String value) { return (OPTS.indexOf(value) >= 0); }
 
   
   private Object calValue(String value1, String opt, String value2) throws Exception {
     try {
       double dbValue1 = 0.0D, dbValue2 = 0.0D;
       try {
         dbValue1 = Double.valueOf(value1).doubleValue();
         dbValue2 = Double.valueOf(value2).doubleValue();
       } catch (Exception e) {
         if (opt.equals("+"))
           return value1 + value2; 
         if (opt.equals("=")) {
           if (value1.equals(value2))
             return Integer.valueOf(1); 
           return Integer.valueOf(0);
         } 
         return "";
       } 
       long lg = 0L;
       if (opt.equals("+"))
         return Double.valueOf(dbValue1 + dbValue2); 
       if (opt.equals("-"))
         return Double.valueOf(dbValue1 - dbValue2); 
       if (opt.equals("*"))
         return Double.valueOf(dbValue1 * dbValue2); 
       if (opt.equals("/"))
         return Double.valueOf(dbValue1 / dbValue2); 
       if (opt.equals("%")) {
         lg = (long)(dbValue1 / dbValue2);
         return Double.valueOf(dbValue1 - lg * dbValue2);
       }  if (opt.equals(">")) {
         if (dbValue1 > dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("<")) {
         if (dbValue1 < dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("]")) {
         if (dbValue1 >= dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("[")) {
         if (dbValue1 <= dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("!")) {
         if (dbValue1 != dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("|")) {
         if (dbValue1 > 0.0D || dbValue2 > 0.0D) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("&")) {
         if (dbValue1 > 0.0D && dbValue2 > 0.0D) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       }  if (opt.equals("=")) {
         if (dbValue1 == dbValue2) {
           return Integer.valueOf(1);
         }
         return Integer.valueOf(0);
       } 
     } catch (Exception e) {
       throw new Exception("值" + value1 + "或" + value2 + "在进行" + opt + "运算时非法！");
     } 
     throw new Exception("运算符" + opt + "非法！");
   }
   
   private String getValue(String oldValue) throws Exception {
     String reg = FUNCTION_PATTERN;
     if (isFunctionCal(oldValue)) {
       Pattern p = Pattern.compile(reg);
       Matcher m = p.matcher(oldValue);
       m.find();
       return calFunction(m.group(1), m.group(2));
     } 
     return oldValue;
   }
   
   private boolean isFunctionCal(String value) {
     String reg = FUNCTION_PATTERN;
     return value.matches(reg);
   }
 
   
   private String calFunction(String function, String value) throws Exception {
     String lowerFun = function.toLowerCase();
     value = value.trim();
     if (value.length() > 0 && (
       "'".equals(value.substring(0, 1)) || "\"".equals(value.substring(0, 1))) && ("'"
       .equals(value.substring(value.length() - 1, value.length())) || "\"".equals(value.substring(value.length() - 1, value.length())))) {
       value = value.substring(1, value.length() - 1);
     }
     
     double db = 0.0D;
     try {
       try {
         if ("sql(".indexOf(value) == -1)
         { db = Double.valueOf(value).doubleValue(); }
         else
         { db = Double.valueOf(getValue(value)).doubleValue(); } 
       } catch (Exception e) {
         return calG1Function(function, value);
       } 
       if (lowerFun.equals("log"))
         return String.valueOf(Math.log(db)); 
       if (lowerFun.equals("square"))
         return String.valueOf(Math.pow(db, 2.0D)); 
       if (lowerFun.equals("sqrt"))
         return String.valueOf(Math.sqrt(db)); 
       if (lowerFun.equals("sin"))
         return String.valueOf(Math.sin(db)); 
       if (lowerFun.equals("asin"))
         return String.valueOf(Math.asin(db)); 
       if (lowerFun.equals("cos"))
         return String.valueOf(Math.cos(db)); 
       if (lowerFun.equals("tan"))
         return String.valueOf(Math.tan(db)); 
       if (lowerFun.equals("atan"))
         return String.valueOf(Math.atan(db)); 
       if (lowerFun.equals("ceil"))
         return String.valueOf(Math.ceil(db)); 
       if (lowerFun.equals("exp")) {
         return String.valueOf(Math.exp(db));
       }
     } catch (Exception e) {
       throw new Exception("函数" + function + "值" + value + "非法!");
     } 
     throw new Exception("函数" + function + "不支持！");
   }
   private String calG1Function(String function, String value) throws Exception {
     String lowerFun = function.toLowerCase();
     try {
       if (lowerFun.equals("dxf.sql"))
         return execSQL(value); 
       if (!lowerFun.equals("dxf.sum"))
       {
         if (!lowerFun.equals("dxf.max"))
         {
           if (!lowerFun.equals("dxf.min"))
           {
             if (!lowerFun.equals("dxf.unique"))
             {
               if (!lowerFun.equals("dxf.uniqueCount"))
               {
                 if (!lowerFun.equals("dxf.count"))
                 {
                   if (lowerFun.equals("dxf.date")) {
                     List<String> values = spiltStr(value);
                     if (values == null) {
                       return (new Date()).getTime() + "";
                     }
                     switch ((String)values.get(0)) {
                     
                     } 
 
 
 
 
 
                   
                   } else if (!lowerFun.equals("dxf.dateText")) {
                     
                     if (!lowerFun.equals("dxf.dict"))
                     {
                       if (!lowerFun.equals("dxf.columnLabel"))
                       {
                         if (lowerFun.equals("dxf.orig")); }  } 
                   }  }  }  }  }  } 
       }
     } catch (Exception e) {}
 
 
     
     return null;
   }
   private List<String> spiltStr(String value) {
     if (value == null)
       return null; 
     String[] values = value.split(",");
     List<String> split = new ArrayList<String>();
     for (int i = 0; i < values.length; i++) {
       String tmp = values[i].trim();
       if (tmp.length() > 0 && (
         "'".equals(tmp.substring(0, 1)) || "\"".equals(tmp.substring(0, 1))) && ("'"
         .equals(tmp.substring(tmp.length() - 1, tmp.length())) || "\"".equals(tmp.substring(tmp.length() - 1, tmp.length())))) {
         tmp = tmp.substring(1, tmp.length() - 1);
       }
       
       split.add(tmp);
     } 
     return (split.size() == 0) ? null : split;
   }
   private String execSQL(String sql) throws Exception {
     Object valueMap = this.systemDao.execEvalSql(sql);
     if (valueMap != null && valueMap instanceof Map) {
       Object[] values = ((Map)valueMap).values().toArray();
       if (values.length > 0) {
         if (values[0] instanceof String) {
           return "'" + values[0] + "'";
         }
         return values[0] + "";
       } 
     } 
     
     return null;
   }
 }


