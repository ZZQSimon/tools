 package cn.com.easyerp.core.logger;
 
 import java.sql.Timestamp;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.LogDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.common.Common;
 
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
public class LogService {
	 public static enum LogType {
	   insert, edit, delete, operate, Import;
	 }
	 @Autowired
	 private DataService dataService;
	 @Autowired
	 private LogDao logDao;
	 @Autowired
	 private SystemDao systemDao;
   
	 public void log(String id, TableDescribe table, LogType type, Map<String, Object> map, String uid) {
		 Timestamp ts = Common.now();
		 log(id, table, type, map, uid, ts);
	 }
   
	 public void log(String id, TableDescribe table, LogType type, Map<String, Object> map, String uid, Timestamp ts) { this.logDao.insertLogNew(id, ts, uid, type, table.getId(), Common.toJson(map), "", ""); }
   
	 public void logTemp(String id, LogType type, String uid) {
		 Timestamp ts = Common.now();
		 logTemp(id, type, uid, ts);
	 }
   
	 public void logTemp(String id, LogType type, String uid, Timestamp ts) { 
		 this.logDao.insertLogTemp(id, ts, uid, type);
	 }
   
	 public String getMaxId() { return this.systemDao.getId("c_log", ""); }
   
	 public void updateLogNormal(String id, String target_id, String context) {
		 updateLogNormal(id, target_id, context, "success");
	 }
   
	 public void updateLogNormal(String id, String target_id, String context, String type) {
		 this.logDao.updateLogNormal(id, target_id, context, type);
	 }
   
	 public void updateLogException(String id, String target_id, String context, String exception) {
		 updateLogException(id, target_id, context, "fail", exception);
	 }
   
	 public void updateLogException(String id, String target_id, String context, String type, String exception) {
		 this.logDao.updateLogException(id, target_id, context, type, exception);
	 }
}