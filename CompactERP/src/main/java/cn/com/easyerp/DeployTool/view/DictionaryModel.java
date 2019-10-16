package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("dictionary")
public class DictionaryModel extends FormModelBase {
    protected DictionaryModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "dictionary";
    }
}
