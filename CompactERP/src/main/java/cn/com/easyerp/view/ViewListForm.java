package cn.com.easyerp.view;

import java.util.Set;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.easyerp.core.cache.style.TableViewStyle;
import cn.com.easyerp.core.filter.FilterModel;
import cn.com.easyerp.core.view.form.list.ListForm;
import cn.com.easyerp.core.view.form.list.ListFormModel;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Controller
@RequestMapping({ "/view" })
public class ViewListForm extends ListForm {
    public static ViewListFormModel create(String parent, String tableName, FilterModel filter, GridModel grid,
            Set<TableViewStyle.Button> hideButtons, boolean load, boolean control) {
        return new ViewListFormModel(parent, tableName, filter, grid, hideButtons, load, control);
    }

    protected ListFormModel createForm(ActionType action, String parent, String tableName, FilterModel filter,
            GridModel grid, Set<TableViewStyle.Button> hideButtons, boolean load, boolean control) {
        return create(parent, tableName, filter, grid, hideButtons, load, control);
    }
}