package cn.com.easyerp.core.widget.grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.widget.WidgetModelBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.tree.TreeNodeModel;

@Controller
@RequestMapping({ "/leftSelect" })
public class GridLeftSelectController {
    @Autowired
    private SystemDao systemDao;

    @SuppressWarnings("rawtypes")
    @ResponseBody
    @RequestMapping({ "/gridLeftSelect.do" })
    public ActionResult gridLeftSelect(@RequestBody GridLeftRequestModel request, AuthDetails user) {
        WidgetModelBase form = request.getWidget();
        GridLeftModel glm = new GridLeftModel(request.getTableName());
        TableDescribe tableDesc = this.cache.getTableDesc(request.getTableName());
        glm.setColumn(request.getColumn());
        if (request.getColumn() == null || "".equals(request.getColumn())) {
            return new ActionResult(true, null);
        }
        getLeftSelectRecodes(form, tableDesc, request.getColumn(), glm);
        if (request.getLeftSelectFilters() != null) {
            buildTreeNodeSelected(glm, (FilterDescribe) request.getLeftSelectFilters().get(request.getColumn()));
        }
        return new ActionResult(true, glm);
    }

    @Autowired
    private CacheService cache;
    @Autowired
    private DataService dataService;

    private void getLeftSelectRecodes(WidgetModelBase form, TableDescribe table, String column, GridLeftModel glm) {
        ColumnDescribe groupColumn = table.getColumn(column);
        if (groupColumn.getDic_id() != null && !"".equals(groupColumn.getDic_id())) {

            String dic_id = groupColumn.getDic_id();
            Map<String, I18nDescribe> dict = this.cache.getDict(dic_id);
            List<TreeNodeModel> treeNodeModels = dumpDictNodes(dict);
            glm.setLeftNodes(treeNodeModels);
            glm.setType("2");
        } else if (groupColumn.getRef_table_name() != null && !"".equals(groupColumn.getRef_table_name())) {

            String ref_table_name = groupColumn.getRef_table_name();
            TableDescribe refTable = this.cache.getTableDesc(ref_table_name);
            String parent_id_column = refTable.getParent_id_column();
            String children_id_column = refTable.getChildren_id_column();
            List<TreeNodeModel> treeNodeModels = null;
            if (parent_id_column != null && !"".equals(parent_id_column) && children_id_column != null
                    && !"".equals(children_id_column)) {

                treeNodeModels = dumpTreeNodes(form, refTable, this.systemDao.selectViewGridValue(refTable, null),
                        column);
            } else {

                String where = "exists (select 1 from " + table.getId() + " where ";
                where = where + refTable.getId() + "." + refTable.getIdColumns()[refTable.getIdColumns().length - 1];
                where = where + " = " + table.getId() + "." + column + ")";
                treeNodeModels = dumpNodes(form, refTable, this.systemDao.selectViewGridValue(refTable, where), column,
                        true);
            }
            glm.setLeftNodes(treeNodeModels);
            glm.setType("3");
        } else if (groupColumn.getDic_id() == null || "".equals(groupColumn.getDic_id())
                || groupColumn.getRef_table_name() == null || "".equals(groupColumn.getRef_table_name())) {

            List<String> distinctColumn = new ArrayList<String>();
            distinctColumn.add(column);
            List<Map<String, Object>> recodes = this.systemDao.selectDistinctValue(table, distinctColumn, null);
            List<TreeNodeModel> treeNodeModels = dumpNodes(form, table, recodes, column, false);
            glm.setLeftNodes(treeNodeModels);
            glm.setType("3");
        }
    }

    private List<TreeNodeModel> dumpTreeNodes(WidgetModelBase form, TableDescribe table, List<Map<String, Object>> list,
            String column) {
        List<TreeNodeModel> nodes = new ArrayList<TreeNodeModel>();
        if (table.getIdColumns() != null && table.getIdColumns().length > 1) {
            throw new ApplicationException("不支持多主键树");
        }
        for (Map<String, Object> map : list) {
            String childrenId = (String) map.get(table.getChildren_id_column());
            String name = this.dataService.buildNameExpression(table, map).toString();

            String parent = (String) map.get(table.getParent_id_column());
            if (Common.isBlank(parent)) {
                parent = "#";

            } else if (parent == null || "".equals(parent)) {
                parent = "#";
            }
            TreeNodeModel node = new TreeNodeModel(childrenId, parent, name);
            nodes.add(node);
        }
        return nodes;
    }

    private List<TreeNodeModel> dumpNodes(WidgetModelBase form, TableDescribe table, List<Map<String, Object>> list,
            String column, boolean is_ref) {
        List<TreeNodeModel> nodes = new ArrayList<TreeNodeModel>();
        for (Map<String, Object> map : list) {
            String childrenId;
            if (table.getIdColumns() == null || table.getIdColumns().length == 0) {
                return null;
            }
            if (is_ref) {
                childrenId = (String) map.get(table.getIdColumns()[table.getIdColumns().length - 1]);
            } else {
                childrenId = map.get(column).toString();
            }
            String name = this.dataService.buildNameExpression(table, map).toString();
            TreeNodeModel node = new TreeNodeModel(childrenId, "#", name);
            nodes.add(node);
        }
        return nodes;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<TreeNodeModel> dumpDictNodes(Map<String, I18nDescribe> dict) {
        List<TreeNodeModel> nodes = new ArrayList<TreeNodeModel>();
        Set<Map.Entry<String, I18nDescribe>> entries = dict.entrySet();
        Iterator<Map.Entry<String, I18nDescribe>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, I18nDescribe> next = (Map.Entry) iterator.next();
            I18nDescribe value = (I18nDescribe) next.getValue();
            String i18nText = this.dataService.i18nText(value);
            TreeNodeModel node = new TreeNodeModel(value.getKey2(), "#", (i18nText == null) ? "" : i18nText);
            nodes.add(node);
        }

        return nodes;
    }

    @SuppressWarnings("rawtypes")
    private void buildTreeNodeSelected(GridLeftModel glm, FilterDescribe filter) {
        if (filter == null || filter.getValue() == null || glm.getLeftNodes() == null
                || glm.getLeftNodes().size() == 0) {
            return;
        }
        for (TreeNodeModel node : glm.getLeftNodes()) {
            if (((ArrayList) filter.getValue()).contains(node.getId()))
                node.addSelected();
        }
    }
}
