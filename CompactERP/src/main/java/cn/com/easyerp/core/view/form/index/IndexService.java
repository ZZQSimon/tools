 package cn.com.easyerp.core.view.form.index;
 
 import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.dao.IndexDao;
 
 
 
 
 @Service
 public class IndexService
 {
   @Autowired
   private IndexDao indexDao;
   
   public BullModel selectBullList(String owner, String id) {
     BullModel bul = this.indexDao.selectBullList(owner, id);
     if (bul != null) {
       String send_time = bul.getSend_time().substring(0, 10);
       bul.setSend_time(send_time);
     } 
     
     return bul;
   }
 
   
   public ShortModel selectShortList(String owner, String id) { return this.indexDao.selectShortList(owner, id); }
 
 
 
   
   public DeskModel selectChartList(String owner, String id) { return this.indexDao.selectChartList(owner, id); }
 
 
   
   public WorkModel selectWorkList(String owner, String id) {
     WorkModel wor = this.indexDao.selectWorkList(owner, id);
     if (wor != null) {
       String start = wor.getStart_date().substring(0, 10);
       String end = wor.getEnd_date().substring(0, 10);
       wor.setStart_date(start);
       wor.setEnd_date(end);
     } 
     
     return wor;
   }
 
   
   public Map<String, Object> getReList(String owner, String id) { return this.indexDao.getReList(owner, id); }
 
 
   
   public String getValueBySql(String sql) { return this.indexDao.getValueBySql(sql); }
 
 
   
   public List<Map<String, Object>> getSuserList(String tableId, String column_name, String name_column, String id, String type) { return this.indexDao.getSuserList(tableId, column_name, name_column, id, type); }
 
 
   
   public IndexDao getIndexDao() { return this.indexDao; }
 
 
   
   public void setIndexDao(IndexDao indexDao) { this.indexDao = indexDao; }
 }


