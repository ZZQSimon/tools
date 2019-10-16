package cn.com.easyerp.core.view.form.detail;

import java.util.List;

import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.grid.GridModel;
import cn.com.easyerp.framework.enums.ActionType;

public interface DetailFormBuilder {
    DetailFormModel build(ActionType paramActionType, String paramString1, String paramString2,
            List<FieldModelBase> paramList1, List<GridModel> paramList2, int paramInt, ViewDataMap paramViewDataMap);
}
