package cn.com.easyerp.DeployTool.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.DeployTool.service.CustomLayout;
import cn.com.easyerp.DeployTool.service.TableDeployService;
import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/tableDeploy" })
public class TableDeployController extends FormViewControllerBase {
    @Autowired
    private TableDeployDao tableDeployDao;
    @Autowired
    private TableDeployService tableDeployService;
    @Autowired
    private CacheService cacheService;
    @Autowired
    private DataService dataService;

    @RequestMapping({ "/tableDeploy.view" })
    public ModelAndView url(@RequestBody TableDeployRequestModel request) {
        TableDeployModel form = new TableDeployModel(request.getParent());
        form.setTableName("c_table");
        form.setTabIcon(request.getTabIcon());
        return buildModelAndView(form);
    }

    @RequestMapping({ "/selectTableDeploy.do" })
    @ResponseBody
    public ActionResult selectTableDeploy(AuthDetails auth) {
        List<TreeNode> list = new ArrayList<TreeNode>();
        List<Map<String, String>> maps = this.tableDeployDao.selectBasicDeploy(auth.getLanguage_id());
        if (auth.getId().equals("Super")) {
            list.add(new TreeNode("0", "deploy_sys_table", "#", getBasic(maps, "deploy_sys_table")));
        }
        list.add(new TreeNode("5", "deploy_base_table", "#", getBasic(maps, "deploy_base_table")));
        list.add(new TreeNode("4", "deploy_professional_table", "#", getBasic(maps, "deploy_professional_table")));
        list.add(new TreeNode("1", "deploy_view_table", "#", getBasic(maps, "deploy_view_table")));

        Map<String, TableDescribe> tableDescCache = this.cacheService.getTableDescCache();
        for (Map.Entry<String, TableDescribe> entry : tableDescCache.entrySet()) {
            TreeNode node = new TreeNode();
            if (auth.getId().equals("Super") && ((TableDescribe) entry.getValue()).getTable_type() == 0) {
                TableDescribe parentTable = this.cacheService
                        .getTableDesc(((TableDescribe) entry.getValue()).getParent_id());
                if (parentTable == null || parentTable.getTable_type() != 0) {
                    node.setId((String) entry.getKey() + "_0");
                    node.setParent("0");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                } else {
                    node.setId((String) entry.getKey() + "_0");
                    node.setParent(parentTable.getId() + "_0");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                }
            }

            if (((TableDescribe) entry.getValue()).getTable_type() == 1) {
                TableDescribe parentTable = this.cacheService
                        .getTableDesc(((TableDescribe) entry.getValue()).getParent_id());
                if (parentTable == null || parentTable.getTable_type() != 1) {
                    node.setId((String) entry.getKey() + "_1");
                    node.setParent("1");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                } else {
                    node.setId((String) entry.getKey() + "_1");
                    node.setParent(parentTable.getId() + "_1");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                }
            } else if (((TableDescribe) entry.getValue()).getTable_type() == 5) {
                TableDescribe parentTable = this.cacheService
                        .getTableDesc(((TableDescribe) entry.getValue()).getParent_id());
                if (parentTable == null || parentTable.getTable_type() != 5) {
                    node.setId((String) entry.getKey() + "_5");
                    node.setParent("5");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                } else {
                    node.setId((String) entry.getKey() + "_5");
                    node.setParent(parentTable.getId() + "_5");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                }
            } else if (((TableDescribe) entry.getValue()).getTable_type() == 4) {
                TableDescribe parentTable = this.cacheService
                        .getTableDesc(((TableDescribe) entry.getValue()).getParent_id());
                if (parentTable == null || parentTable.getTable_type() != 4) {
                    node.setId((String) entry.getKey() + "_4");
                    node.setParent("4");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                } else {
                    node.setId((String) entry.getKey() + "_4");
                    node.setParent(parentTable.getId() + "_4");
                    node.setTable_id((String) entry.getKey());
                    node.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                }
            } else if (((TableDescribe) entry.getValue()).getTable_type() != 0) {

            }

            if (node.getId() != null && !"".equals(node.getId()))
                list.add(node);
            if (((TableDescribe) entry.getValue()).getParent_id() != null
                    && !"".equals(((TableDescribe) entry.getValue()).getParent_id())) {
                TableDescribe parentTable = this.cacheService
                        .getTableDesc(((TableDescribe) entry.getValue()).getParent_id());
                if (parentTable == null) {
                    throw new ApplicationException(
                            "parent table :" + ((TableDescribe) entry.getValue()).getParent_id() + " does not exists");
                }
                if (((TableDescribe) entry.getValue()).getTable_type() != 0
                        && ((TableDescribe) entry.getValue()).getTable_type() != parentTable.getTable_type()) {
                    TreeNode childrenNode = new TreeNode();
                    String id = (String) entry.getKey() + "__" + ((TableDescribe) entry.getValue()).getTable_type();
                    childrenNode.setId(id);
                    childrenNode.setTable_id((String) entry.getKey());
                    childrenNode.setParent(parentTable.getId() + "_" + parentTable.getTable_type());
                    childrenNode.setText(this.dataService.i18nString(((TableDescribe) entry.getValue()).getI18n()));
                    list.add(childrenNode);
                }
            }
        }

        return new ActionResult(true, list);
    }

    @RequestMapping({ "/selectCustomLayout.do" })
    @ResponseBody
    public ActionResult selectCustomLayout(@RequestBody TableDeployRequestModel request) {
        List<CustomLayout> customLayoutList = this.tableDeployDao.selectCustomLayout(request.getTable_id());
        return new ActionResult(true, customLayoutList);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/CRUTable.do" })
    public ActionResult CRUTable(@RequestBody TableDeployRequestModel request) {
        this.tableDeployService.CRUTable(request);
        return new ActionResult(true);
    }

    @SuppressWarnings({ "rawtypes" })
    private String getBasic(List<Map<String, String>> maps, String str) {
        for (int i = 0; i < maps.size(); i++) {
            if (str.equals(((Map) maps.get(i)).get("id"))) {
                return (String) ((Map) maps.get(i)).get("lan");
            }
        }
        return "";
    }

    @SuppressWarnings("unused")
    private String getType(String type) {
        String t = "";
        switch (type) {
        case "c_":
            t = "deploy_sys_table";
            break;
        case "m_":
            t = "deploy_base_table";
            break;
        case "t_":
            t = "deploy_professional_table";
            break;
        case "v_":
            t = "deploy_view_table";
            break;
        case "a_":
            t = "deploy_api_table";
            break;
        }

        return t;
    }
}
