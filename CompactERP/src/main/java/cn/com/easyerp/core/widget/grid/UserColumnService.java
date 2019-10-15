 package cn.com.easyerp.core.widget.grid;
 
 import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.UserColumnDao;
 
 
 
 
 @Service
 public class UserColumnService
 {
   @Autowired
   private UserColumnDao userColumnDao;
   
   public List<UserColumn> getUserColumn(AuthDetails user, TableDescribe table) { return this.userColumnDao.selectUserColumnByTableName(table.getId(), user.getId()); }
 
   
   public void insertUserColumn(List<UserColumn> UserColumns, AuthDetails user) { this.userColumnDao.insertUserColumn(UserColumns, user.getId()); }
 
   
   void deleteUserColumn(TableDescribe table, AuthDetails user, List<UserColumn> UserColumns) { this.userColumnDao.deleteUserColumn(table.getId(), user.getId()); }
 }


