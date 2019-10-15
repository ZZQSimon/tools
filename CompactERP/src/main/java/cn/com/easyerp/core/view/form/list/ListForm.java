 package cn.com.easyerp.core.view.form.list;
 
 import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;
import cn.com.easyerp.view.ViewListForm;
 
 
 
 @Controller
 @RequestMapping({"/list"})
 public class ListForm
   extends FormViewControllerBase
 {
   @Autowired
   private AuthService authService;
   @Autowired
   private DataService dataService;
   @Autowired
   private SystemDao systemDao;
   @Autowired
   private CacheService cacheService;
   
   @RequestMapping({"/table.view"})
   public ModelAndView view(@RequestBody ListFormRequestModel request, AuthDetails user) {
     ListFormModel form;
     String tableName = request.getTable();
     
     GridModel grid = new GridModel(tableName, null);
     grid.setListing(true);
     
     grid.setUserColumns(this.dataService.getUserColumn(user));
     
     TableDescribe table = this.dataService.getTableDesc(tableName);
     grid.setNoAuthColumns(this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R));
     boolean hasSum = false;
     for (ColumnDescribe column : table.getColumns()) {
       if (column.getSum_flag() != null) {
         hasSum = true; break;
       } 
     } 
     grid.setHasSum(hasSum);
 
 
     
     String filterTable = (table.getViewStyle() != null && table.getViewStyle().getFilter() != null) ? table.getViewStyle().getFilter().getReference() : tableName;
     
     FilterModel filter = new FilterModel(filterTable, "Search", null, request.getFixedData(), request.getDefaultFilters());
     Set<TableViewStyle.Button> hideButtons = request.getHideButtons();
     
     if (table.asView() && request.getAction() != ActionType.select) {
       form = ViewListForm.create(request.getParent(), tableName, filter, grid, hideButtons, request.isLoad(), request.isControl());
     } else {
       form = createForm(request.getAction(), request.getParent(), tableName, filter, grid, hideButtons, request.isLoad(), request.isControl());
     }  form.setLeftSelect(request.isLeftSelect());
     form.setLeftSelectFilters(request.getLeftSelectFilters());
     form.setTabIcon(request.getTabIcon());
     return buildModelAndView(form);
   }
 
 
   
   protected ListFormModel createForm(ActionType action, String parent, String tableName, FilterModel filter, GridModel grid, Set<TableViewStyle.Button> hideButtons, boolean load, boolean control) { return new ListFormModel(action, parent, tableName, filter, grid, hideButtons, load, control); }
 
 
   
   @RequestMapping({"/mobile/table.view"})
   public ModelAndView mobileView(@RequestBody mobileTableRequestModel request, AuthDetails user) {
     GridModel grid = new GridModel(request.getTable(), null);
     mobileTableModel form = new mobileTableModel(request.getParent());
     TableDescribe table = this.dataService.getTableDesc(request.getTable());
     if (table.getMobile_list_group() != null && !"".equals(table.getMobile_list_group())) {
       form.setGroups(this.systemDao.getMobileListGroup(table));
     }
     grid.setListing(true);
     form.setGrid(grid);
     form.setTable(request.getTable());
     form.setGroup_column(table.getMobile_list_group());
     return buildModelAndView(form);
   }
 }


