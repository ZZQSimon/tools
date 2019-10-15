 package cn.com.easyerp.tg;
 import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewMapIteratorProcessor;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.TriggerDescribe;
import cn.com.easyerp.core.dao.ColumnValue;
import cn.com.easyerp.core.dao.TreeDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ReferenceModel;
import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.view.form.detail.DetailFormModel;
import cn.com.easyerp.core.view.form.detail.DetailFormService;
import cn.com.easyerp.core.view.form.detail.DetailRequestModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.tree.TreeNodeModel;
 
 @Controller
 @RequestMapping({"/tg"})
 public class TreeGridController extends FormViewControllerBase {
	 private static final String BOM_TREE_ROOT_ID = "BOM_ROOT";
	 private static final String BOM_TREE_FN_NAME = "BOM_TREE_FN";
	 private static final TableDescribe BOM_TREE_FN_TABLE_DESC;

	 public enum TreeType{
		 simple, bom;
	 }

	 private static class TreeCache
	  {
	    List<TreeNodeModel> nodes;
	    
	    List<Map<String, Object>> mapNodes;
	    TableDescribe table;
	    List<String> keyColumns;
	    DatabaseDataMap values;
	    
	    public List<Map<String, Object>> getMapNodes() { return this.mapNodes; }
	    String where; String begin; String end; String child; String parent;
	    TreeGridFormModel form;
	    
	    public void setMapNodes(List<Map<String, Object>> mapNodes) { this.mapNodes = mapNodes; }
	    
	    TreeCache(List<TreeNodeModel> nodes, TableDescribe table, DatabaseDataMap values, TreeGridFormModel form) {
	      this.mapNodes = new ArrayList<>();
	      this.nodes = nodes;
	      this.table = table;
	      this.values = values;
	      this.form = form;
	      this.keyColumns = new ArrayList<>();
	      for (String column : table.getIdColumns()) {
	        if (!column.equals(table.getChildren_id_column()) && !column.equals(table.getParent_id_column()))
	        {
	          this.keyColumns.add(column);
	        }
	      } 
	    }
	  }
   @Autowired
   private CacheService cache;
   
   static  {
	   ColumnDescribe id = new ColumnDescribe();
	   id.setColumn_name("id");
	   ColumnDescribe rn = new ColumnDescribe();
	   rn.setColumn_name("rn");
	   List<ColumnDescribe> columns = new ArrayList<ColumnDescribe>();
	   columns.add(id);
	   columns.add(rn);
     
	   BOM_TREE_FN_TABLE_DESC = new TableDescribe("BOM_TREE_FN", "id", columns);
   }
   
   @Autowired
   private DataService dataService;
   @Autowired
   private ViewService viewService;
   @Autowired
   private DetailFormService formService;
   @Autowired
   private TreeDao treeDao;
   @Autowired
   private AuthService authService;
   
   @RequestMapping({"/table.view"})
   public ModelAndView view(@RequestBody TreeGridFromRequestModel request) {
     String tableName = request.getTable();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     Set<String> fieldsNeeded = new HashSet<String>();
     TreeType type = tableName.equals(table.getColumn(table.getParent_id_column()).getRef_table_name()) ? TreeType.simple : TreeType.bom;
     
     for (String column : table.getColumnNames()) {
       if (table.getColumn(column).getIs_condition().intValue() <= 0)
         continue; 
       if (column.equals(table.getChildren_id_column()))
         if (type == TreeType.simple) {
           column = table.getParent_id_column();
         } else {
           continue;
         }   fieldsNeeded.add(column);
     } 
     FilterModel.FilterMode mode = (type == TreeType.simple) ? FilterModel.FilterMode.std : FilterModel.FilterMode.match;
     Map<String, FilterDescribe> filters = (request.getFilter() == null) ? null : request.getFilter().getFilters();
     FilterModel filter = new FilterModel(tableName, "OK", fieldsNeeded, null, filters, mode);
     
     TreeGridFormModel form = new TreeGridFormModel(request.getParent(), tableName, filter, type, request.isLoad());
     return buildModelAndView(form);
   }
   
   @ResponseBody
   @RequestMapping({"/list.do"})
   public ActionResult list(@RequestBody TreeGridFromRequestModel request) throws IOException {
     TreeGridFormModel form = (TreeGridFormModel)request.getWidget();
     String tableName = form.getTableName();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     ViewDataMap data = new ViewDataMap();
     for (Map.Entry<String, FilterDescribe> entry : request.getFilter().getFilters().entrySet()) {
       Object value = ((FilterDescribe)entry.getValue()).getValue();
       if (value != null && value instanceof ArrayList) {
         value = ((ArrayList)value).get(0);
       }
       data.put(entry.getKey(), value);
     } 
     
     form.setFilterData(data);
     
     List<TreeNodeModel> nodes = doLoad(form, table);
     return new ActionResult(true, nodes);
   }
   
   private List<TreeNodeModel> doLoad(TreeGridFormModel form, TableDescribe table) {
	   final List<TreeNodeModel> nodes = new ArrayList<TreeNodeModel>();
       final DatabaseDataMap values = this.viewService.convertToDBValues(table, form.getFilterData());
       final TreeCache cache = new TreeCache(nodes, table, values, form);
       switch (form.getType()) {
           case simple: {
               this.dumpSimpleTree(cache);
               break;
           }
           case bom: {
               this.dumpBomTree(cache);
               break;
           }
       }
       ViewService.cacheRequestWidgets((FormModelBase)form);
       final List<Map<String, Object>> mapNodes = cache.getMapNodes();
       final List<TreeNodeModel> nodes2 = cache.nodes;
       return nodes2;
   }
 
   
   @ResponseBody
   @RequestMapping({"/reload.do"})
   public ActionResult reload(@RequestBody TreeGridFromRequestModel request) throws IOException {
     TreeGridFormModel form = (TreeGridFormModel)request.getWidget();
     String tableName = form.getTableName();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     List<TreeNodeModel> nodes = doLoad(form, table);
     return new ActionResult(true, nodes);
   }
   
   @RequestMapping({"/create.view"})
   public ModelAndView createView(@RequestBody TreeGridFromRequestModel request, AuthDetails user) {
     TreeGridFormModel form = (TreeGridFormModel)ViewService.fetchFormModel(request.getParent());
     TableDescribe table = this.dataService.getTableDesc(form.getTableName());
     ViewDataMap data = new ViewDataMap();
     if (form.getFilterData() != null)
       data.putAll(form.getFilterData()); 
     if (Common.notBlank(table.getValid_date_cols())) {
       String name = Common.split(table.getValid_date_cols(), ",")[0];
       data.remove(name);
     } 
     if (request.getFixedData() != null)
       data.putAll(request.getFixedData()); 
     DetailFormModel detailForm = this.formService.createView(form.getTableName(), form.getId(), data, user);
     detailForm.setHideButtons(request.getHideButtons());
     return buildModelAndView(detailForm);
   }
   
   private void recursiveCheck(TreeCache c, String parent) {
     c.values.put(c.table.getChildren_id_column(), parent);
     List<DatabaseDataMap> list = this.treeDao.selectParent(c.table, c.values, c.where);
     for (DatabaseDataMap data : list) {
       parent = (String)data.get(c.table.getParent_id_column());
       if (c.child.equals(parent))
         throw new ApplicationException(this.dataService.getMessageText("recursive check failed", new Object[] { parent })); 
       recursiveCheck(c, parent);
     } 
   }
 
   
   private String buildCheckWhere(String tableName, String begin, String end, String begin_column, String end_column) { return "(" + begin + " between " + tableName + "." + begin_column + " and " + tableName + "." + end_column + " or " + end + " between " + tableName + "." + begin_column + " and " + tableName + "." + end_column + " or (" + begin + " <= " + tableName + "." + begin_column + " and " + end + " >= " + tableName + "." + end_column + "))"; }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping({"/create.do"})
   public ActionResult create(@RequestBody DetailRequestModel request, AuthDetails user) {
	   final DetailFormModel detailForm = (DetailFormModel)request.getWidget();
       final TreeGridFormModel treeForm = (TreeGridFormModel)ViewService.fetchFormModel(detailForm.getParent());
       final String tableName = detailForm.getTableName();
       final TableDescribe table = this.dataService.getTableDesc(tableName);
       if (treeForm.getType() == TreeType.bom) {
           final TreeCache cache = new TreeCache(null, table, null, treeForm);
           final String[] validData = Common.split(table.getValid_date_cols(), ",", true);
           final boolean withValidDate = validData.length == 2;
           final Set<String> needed = new HashSet<String>(Arrays.asList(table.getIdColumns()));
           cache.values = this.viewService.mapDataToDB((Map)request.getParam(), (Collection)needed, (ViewMapIteratorProcessor)new ViewMapIteratorProcessor() {
               public boolean process(final String column, final Object value, final FieldModelBase field) {
                   if (withValidDate) {
                       if (validData[0].equals(column)) {
                           cache.begin = TreeGridController.this.dataService.makeDateConvertSql(Common.convertToDateText((Date)value));
                           return false;
                       }
                       if (validData[1].equals(column)) {
                           cache.end = TreeGridController.this.dataService.makeDateConvertSql(Common.convertToDateText((Date)value));
                           return false;
                       }
                   }
                   if (cache.table.getChildren_id_column().equals(column)) {
                       cache.child = (String)value;
                       return false;
                   }
                   if (cache.table.getParent_id_column().equals(column)) {
                       cache.parent = (String)value;
                       return false;
                   }
                   return true;
               }
           });
           if (cache.child.equals(cache.parent)) {
               throw new ApplicationException("child id same with parent id");
           }
           cache.where = (withValidDate ? this.buildCheckWhere(tableName, cache.begin, cache.end, validData[0], validData[1]) : null);
           this.recursiveCheck(cache, cache.parent);
       }
       final ActionResult ret = this.formService.create(detailForm, request.getParam(), request.getCalendarColorParam(), table, user, request.getTriggerRequestParams(), null);
       final Map<String, Object> data = this.dataService.selectRecordByKey(tableName, (Map<String, Object>)ret.getData());
       final RecordModel record = new RecordModel(this.dataService.buildModel(table, data));
       record.setParent(treeForm.getId());
       final String name = this.getNodeName(treeForm.getType(), table, record);
       final TreeNodeModel node = new TreeNodeModel(record.getId(), (String)null, name);
       node.setData(record);
       ret.setData((Object)node);
       ViewService.cacheRequestWidgets((FormModelBase)treeForm);
       return ret;
   }
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
 
   
   @Transactional
   @ResponseBody
   @RequestMapping("/delete.do")
   public ActionResult delete(@RequestBody TreeGridFromRequestModel request, AuthDetails user) {
     TreeGridFormModel form = (TreeGridFormModel)request.getWidget();
     String tableName = form.getTableName();
     TableDescribe table = this.dataService.getTableDesc(tableName);
     for (String id : request.getIds()) {
       RecordModel record = (RecordModel)ViewService.fetchWidgetModel(id);
       Map<String, FieldModelBase> fieldMap = record.getFieldMap();
       FieldModelBase field = (FieldModelBase)this.authService.getOwner(fieldMap);
       if (field != null)
       {
         this.authService.assertAuth(user, table.getId(), table
             .getTrigger(TriggerDescribe.TriggerType.delete).getAction_id(), (String)field
             .getValue());
       }
 
       
       if (form.getType() == TreeType.bom) {
         this.dataService.deleteRecord(tableName, record.getValueMap()); continue;
       } 
       String column = table.getChildren_id_column();
       String value = (String)((FieldModelBase)record.getFieldMap().get(column)).getValue();
       List<String> columns = new ArrayList<String>();
       columns.add(column);
       
       List<ColumnValue> selectKeys = new ArrayList<ColumnValue>();
       selectKeys.add(new ColumnValue(table.getParent_id_column(), value));
       List<ColumnValue> deleteKeys = new ArrayList<ColumnValue>();
       deleteKeys.add(new ColumnValue(table.getChildren_id_column(), value));
       deleteNode(table, columns, selectKeys, deleteKeys);
     } 
     
     return Common.ActionOk;
   }
   
   private void deleteNode(TableDescribe table, List<String> columns, List<ColumnValue> selectKeys, List<ColumnValue> deleteKeys) {
     this.dataService.deleteRecord(table.getId(), deleteKeys);
     ((ColumnValue)selectKeys.get(0)).setValue(((ColumnValue)deleteKeys.get(0)).getValue());
     List<Map<String, Object>> list = this.dataService.selectRecordsByValues(table.getId(), columns, selectKeys, null);
     for (Map<String, Object> map : list) {
       ((ColumnValue)deleteKeys.get(0)).setValue(map.get(table.getChildren_id_column()));
       deleteNode(table, columns, selectKeys, deleteKeys);
     } 
   }
   
   private List<ReferenceModel> buildTreeFuncRef(TreeCache c) {
     String tableName = c.table.getId();
     String pic = c.table.getParent_id_column();
     String val = (String)c.values.remove(pic);
     if (Common.isBlank(val)) {
       val = "";
     }
     String cic = c.table.getChildren_id_column();
     ReferenceModel ref = new ReferenceModel(c.table.getColumn(cic), "BOM_TREE_FN", BOM_TREE_FN_TABLE_DESC, "select * from fn_tree_build('" + tableName + "', '" + val + "', 'query_all')", "inner join");
 
 
     
     List<ReferenceModel> refs = new ArrayList<ReferenceModel>();
     refs.add(ref);
     return refs;
   }
   
   private void dumpSimpleTree(TreeCache c) {
     List<ReferenceModel> refs = null;
     
     String where = null;
 
     
     List<Map<String, Object>> ret = this.dataService.selectRecordsByValues(c.table.getId(), null, DataService.mapToCV(c.values), where, null, refs);
     dumpTreeNodes(c, ret, "#");
   }
   
   private String where(TreeCache c) {
     String where = "";
     String ref_table_sql = "";
     
     List<ColumnDescribe> columns = c.table.getColumns();
     int table_name_index = -1;
     for (ColumnDescribe ColumnDescribe : columns) {
       ref_table_sql = ColumnDescribe.getRef_table_sql();
       TableDescribe tableDesc = this.cache.getTableDesc(ColumnDescribe.getRef_table_name());
       if (ref_table_sql != null && !"".equals(ref_table_sql)) {
         if (tableDesc.getIdColumns().length > 1) {
           where = where + " exists ( select * from( " + ref_table_sql;
           where = where + ") filter_sql_tree where 1=1 ";
           String id_column = "";
           String[] id_columns = tableDesc.getIdColumns();
           for (int i = 0; i < id_columns.length; i++) {
             id_column = id_columns[i];
             where = where + " and filter_sql_tree." + id_column;
             where = where + "=" + ColumnDescribe.getRef_table_name() + "_" + table_name_index + "." + id_column;
           } 
           where = where + ")";
         } 
         table_name_index++;
       } 
     } 
     where = where.replace("${", c.table.getId() + ".");
     where = where.replace("#{", c.table.getId() + ".");
     where = where.replace("}", "");
     return "".equals(where) ? null : where;
   }
   private void dumpBomTree(TreeCache c) {
     String tableName = c.table.getId();
     String valid_date_cols = c.table.getValid_date_cols();
     if (Common.notBlank(valid_date_cols)) {
       String[] cols = Common.split(valid_date_cols, ",");
       Date date = (Date)c.values.remove(cols[0]);
       String dateVar = this.dataService.makeDateConvertSql(Common.convertToDateText(date));
       c.where = tableName + "." + cols[0] + " <= " + dateVar + " and " + tableName + "." + cols[1] + " >= " + dateVar;
     } 
     
     String refTableName = c.table.getColumn(c.table.getParent_id_column()).getRef_table_name();
     TableDescribe refTable = this.dataService.getTableDesc(refTableName);
     Map<String, Object> keys = new HashMap<String, Object>();
     keys.put(refTable.getIdColumns()[0], c.values.get(c.table.getParent_id_column()));
     Map<String, Object> record = this.dataService.selectRecordByKey(refTableName, keys);
     TreeNodeModel node = new TreeNodeModel("BOM_ROOT", "#", (String)record.get(refTable.getName_column()));
     c.nodes.add(node);
     List<Map<String, Object>> list = this.dataService.selectRecordsByValues(c.table.getId(), null, c.values, c.where);
     dumpTreeNodes(c, list, "BOM_ROOT", true);
   }
   
   private String getNodeName(TreeType type, TableDescribe table, RecordModel record) {
     if (type == TreeType.simple) {
       return (String)((FieldModelBase)record.getFieldMap().get(table.getName_column())).getValue();
     }
     FieldModelBase field = (FieldModelBase)record.getFieldMap().get(table.getChildren_id_column());
     if (FieldWithRefModel.class.isInstance(field)) {
       return this.viewService.name((FieldWithRefModel)field);
     }
     return ViewService.text(this.dataService.getColumnDesc(field), field.getValue());
   }
 
   
   private void dumpTreeNodes(TreeCache c, List<Map<String, Object>> list, String treeParent, boolean recursive) {
     Map<String, String> idCache = null;
     if (!recursive) {
       idCache = new HashMap<String, String>();
     }
     for (Map<String, Object> map : list) {
       String parent; for (String column : c.keyColumns)
         c.values.put(column, map.get(column)); 
       RecordModel record = new RecordModel(this.dataService.buildModel(c.table, map));
       record.setParent(c.form.getId());
       String childrenId = record.getId();
       String name = getNodeName(c.form.getType(), c.table, record);
       
       if (recursive) {
         parent = treeParent;
       } else {
         parent = (String)map.get(c.table.getParent_id_column());
         String child = (String)map.get(c.table.getChildren_id_column());
         if (Common.isBlank(parent)) {
           parent = "#";
         } else {
           
           parent = (String)idCache.get(parent);
           if (parent == null || "".equals(parent))
             parent = "#"; 
         } 
         idCache.put(child, childrenId);
       } 
       TreeNodeModel node = new TreeNodeModel(childrenId, parent, name);
       node.setData(record);
       c.nodes.add(node);
       if (recursive) {
         c.values.put(c.table.getParent_id_column(), map.get(c.table.getChildren_id_column()));
         dumpTreeNodes(c, this.dataService.selectRecordsByValues(c.table.getId(), null, c.values, c.where), childrenId, true);
       } 
     } 
   }
 
   
   private void dumpTreeNodes(TreeCache c, List<Map<String, Object>> list, String treeParent) {
     Map<String, String> idCache = new HashMap<String, String>();
     Map<String, RecordModel> records = new HashMap<String, RecordModel>();
     for (Map<String, Object> map : list) {
       RecordModel record = new RecordModel(this.dataService.buildModel(c.table, map));
       record.setParent(c.form.getId());
       String childrenId = record.getId();
       String child = (String)map.get(c.table.getChildren_id_column());
       idCache.put(child, childrenId);
       records.put(child, record);
     } 
     dumpChildrenTreeNodesNew(c, list, records, idCache, null);
     for (Map<String, Object> map : list) {
       for (String column : c.keyColumns) {
         c.values.put(column, map.get(column));
       }
       String children_id_column = (String)map.get(c.table.getChildren_id_column());
       RecordModel record = (RecordModel)records.get(children_id_column);
       String childrenId = (String)idCache.get(children_id_column);
       String name = (String)map.get(c.table.getName_column());
       
       String parent = (String)map.get(c.table.getParent_id_column());
       if (Common.isBlank(parent)) {
         parent = "#";
       } else {
         
         parent = (String)idCache.get(parent);
         if (parent == null || "".equals(parent))
           parent = "#"; 
       } 
       TreeNodeModel node = new TreeNodeModel(childrenId, parent, name);
       node.setData(record);
     } 
   }
 
   
   private TreeNodeModel dumpChildrenTreeNodes(TreeCache c, List<Map<String, Object>> list, Map<String, RecordModel> records, Map<String, String> idCache, TreeNodeModel node) {
     for (Map<String, Object> map : list) {
       for (String column : c.keyColumns) {
         c.values.put(column, map.get(column));
       }
       String parent = (String)map.get(c.table.getParent_id_column());
       parent = (String)idCache.get(parent);
       String name = (String)map.get(c.table.getName_column());
       String children_id_column = (String)map.get(c.table.getChildren_id_column());
       RecordModel record = (RecordModel)records.get(children_id_column);
       if ((parent == null || "".equals(parent)) && node == null) {
         TreeNodeModel treeNodeModel = dumpChildrenTreeNodes(c, list, records, idCache, new TreeNodeModel(record.getId(), record.getParent(), name));
         c.nodes.add(treeNodeModel); continue;
       }  if (node != null && parent != null && parent.equals(node.getId())) {
         TreeNodeModel treeChildNodeMode = new TreeNodeModel(record.getId(), record.getParent(), name);
         node.addChildren(dumpChildrenTreeNodes(c, list, records, idCache, treeChildNodeMode));
       } 
     } 
     return node;
   }
 
 
   
   private Map<String, Object> dumpChildrenTreeNodesNew(TreeCache c, List<Map<String, Object>> list, Map<String, RecordModel> records, Map<String, String> idCache, Map<String, Object> node) {
     for (Map<String, Object> map : list) {
       for (String column : c.keyColumns) {
         c.values.put(column, map.get(column));
       }
       String parent = (String)map.get(c.table.getParent_id_column());
       parent = (String)idCache.get(parent);
       String name = (String)map.get(c.table.getName_column());
       String children_id_column = (String)map.get(c.table.getChildren_id_column());
       RecordModel record = (RecordModel)records.get(children_id_column);
       if ((parent == null || "".equals(parent)) && node == null) {
         DatabaseDataMap valueMapToTree = record.getValueMap();
         valueMapToTree.put("tree_node_id", record.getId());
         valueMapToTree.put("data", record);
         c.mapNodes.add(dumpChildrenTreeNodesNew(c, list, records, idCache, valueMapToTree)); continue;
       }  if (node != null && parent != null && parent.equals(node.get("tree_node_id"))) {
         DatabaseDataMap childValueMap = record.getValueMap();
         childValueMap.put("tree_node_id", record.getId());
         childValueMap.put("_parentId", record.getParent());
         childValueMap.put("data", record);
         if (node.get("children") == null) {
           node.put("children", new ArrayList<>());
         }
         ((ArrayList)node.get("children")).add(dumpChildrenTreeNodesNew(c, list, records, idCache, childValueMap));
       } 
     } 
     return node;
   }
 }


