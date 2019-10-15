package cn.com.easyerp.core.widget.map;
 
 import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.style.ColumnMapViewStyle;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.MapFieldModel;
 
@Service
public class MapSelectService {
	 static enum UpdateType {
	   insert, update, delete;
	 }
   @Autowired
   private DataService dataService;
   
   private void doUpdate(TableDescribe table, Map<String, FieldModelBase> fields, Map<String, Object> data, UpdateType type) {
     for (ColumnDescribe column : table.getColumns()) {
       if (column.getViewStyle() == null || column.getViewStyle().getMap() == null)
         continue; 
       String columnName = column.getColumn_name();
 
       
       String value = (type == UpdateType.delete) ? (String)data.get(columnName) : (((MapFieldModel)fields.get(columnName)).isModified() ? (String)((FieldModelBase)fields.get(columnName)).getValue() : null);
       if (value == null)
         continue; 
       ColumnMapViewStyle map = column.getViewStyle().getMap();
       String inventory = map.getInventory();
       
       if (type != UpdateType.insert) {
         List<ColumnValue> values = new ArrayList<ColumnValue>();
         values.add(new ColumnValue(map.getKey(), value));
         this.dataService.deleteRecord(map.getInventory(), values);
       } 
       
       if (type == UpdateType.delete) {
         continue;
       }
       MapFieldModel f = (MapFieldModel)fields.get(columnName);
       for (Map.Entry<String, String> entry : f.getSelection().entrySet()) {
         Map<String, Object> pair = new HashMap<String, Object>();
         pair.put(map.getKey(), value);
         pair.put(map.getValue(), entry.getKey());
         this.dataService.insertRecord(inventory, pair);
       } 
     } 
   }
 
 
   
   public void inspectInsert(TableDescribe table, Map<String, FieldModelBase> fields) { doUpdate(table, fields, null, UpdateType.insert); }
 
 
 
   
   public void inspectUpdate(TableDescribe table, Map<String, FieldModelBase> fields) { doUpdate(table, fields, null, UpdateType.update); }
 
 
 
   
   public void inspectDelete(TableDescribe table, Map<String, Object> data) { doUpdate(table, null, data, UpdateType.delete); }
 }


