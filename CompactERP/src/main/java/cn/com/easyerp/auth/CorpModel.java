package cn.com.easyerp.auth;

import java.util.List;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("selectcorp")
public class CorpModel extends FormModelBase {
    private List<SysMasterDetails> corp;

    public List<SysMasterDetails> getCorp() {
        return this.corp;
    }

    public void setCorp(List<SysMasterDetails> corp) {
        this.corp = corp;
    }

    protected CorpModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "selectcorp";
    }
}
