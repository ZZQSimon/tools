 package cn.com.easyerp.module.interfaces;
 
 import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.SqlRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
 
 
 @Service
 public class GenerateService
 {
   public void initModel(GenerateModel model) {
     SqlSession session = this.sqlSessionFactory.openSession();
     SqlRunner runner = new SqlRunner(session.getConnection());
     Map<String, List<Map<String, Object>>> exportType = getExportTypeList(runner);
     model.setTypeList(exportType);
     
     session.close();
   }
 
   
   @Autowired
   SqlSessionFactory sqlSessionFactory;
 
   
   private Map<String, List<Map<String, Object>>> getExportTypeList(SqlRunner runner) {
     Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
     
     try {
       String dynamicSql = "select key2, cn from sys_international where type=3 and key1='U_DataType'";
       List<Map<String, Object>> type = runner.selectAll(dynamicSql, new Object[0]);
       
       dynamicSql = "select ID as code,DocName as name from U_DATA_MAPPING_H where DataTypeCode=?";
       List<String> params = new ArrayList<String>();
       for (int i = 0; i < type.size(); i++) {
         Map<String, Object> typeRecord = (Map)type.get(i);
         params.clear();
         params.add((String)typeRecord.get("KEY2"));
         result.put((String)typeRecord.get("CN"), runner.selectAll(dynamicSql, params.toArray()));
       } 
     } catch (SQLException e) {
       e.printStackTrace();
     } 
     
     return result;
   }
 
 
 
 
   
   public String processExport(List<String> type, String start, String end) {
     SqlSession session = this.sqlSessionFactory.openSession();
     SqlRunner runner = new SqlRunner(session.getConnection());
     
     String dynamicSql = "{call t_export_data (?, ?, ?)}";
     List<Object> params = new ArrayList<Object>();
     
     for (int i = 0; i < type.size(); i++) {
       params.clear();
       params.add(type.get(i));
       params.add(start);
       params.add(end);
       int result = 0;
       try {
         result = runner.update(dynamicSql, params.toArray());
       } catch (SQLException e) {
         e.printStackTrace();
         session.rollback();
         session.close();
         return "11";
       } 
     } 
 
 
 
 
 
 
 
     
     session.commit();
     session.close();
     return "0";
   }
 }


