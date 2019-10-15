package cn.com.easyerp.core.widget.inputSelect;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.TreeDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.widget.FieldWithRefModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.tree.TreeNodeModel;

@Controller
@RequestMapping({ "/widget/tree" })
public class TreeSelectController {
    @Autowired
    private DataService dataService;
    @Autowired
    private TreeDao dao;

    @ResponseBody
    @RequestMapping({ "/load.do" })
    public ActionResult tree(@RequestBody TreeSelectRequestModel request) {
        FieldWithRefModel field = (FieldWithRefModel) request.getWidget();
        ColumnDescribe desc = this.dataService.getColumnDesc(field);
        String status = (request.getType() == TreeSelectRequestModel.TreeType.edit) ? "build_edit" : "build_filter";
        TableDescribe table = this.dataService.getTableDesc(desc.getRef_table_name());
        String node = request.getNode();
        if ("#".equals(node))
            node = "";
        List<TreeNodeModel> ret = this.dao.selectTree(table, node, status);
        return new ActionResult(true, ret);
    }
}
