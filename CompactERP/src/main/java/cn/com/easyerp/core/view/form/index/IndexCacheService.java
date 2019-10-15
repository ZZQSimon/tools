 package cn.com.easyerp.core.view.form.index;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.CacheLoader;
import cn.com.easyerp.core.cache.OperationDescribe;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 @Service
 public class IndexCacheService
   extends CacheLoader<List<OperationDescribe>>
 {
   public static String CACHE_MODULE_NAME = "index";
 
 
 
   
   public String getKey() { return CACHE_MODULE_NAME; }
 
 
 
   
   public Map<String, List<OperationDescribe>> reload() {
     Map<String, List<OperationDescribe>> ret = new HashMap<String, List<OperationDescribe>>();
     for (OperationDescribe op : this.cacheService.getOperations().values()) {
       String key = Common.makeMapKey(op.getTable_id(), op.getColumn_name(), new String[] { op.getStatus_id_from() });
       List<OperationDescribe> ops = (List)ret.get(key);
       if (ops == null) {
         ops = new ArrayList<OperationDescribe>();
         ret.put(key, ops);
       } 
       ops.add(op);
     } 
     return ret;
   }
 }


