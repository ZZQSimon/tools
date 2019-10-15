package cn.com.easyerp.tree;

import cn.com.easyerp.core.data.ExportRecordGetter;
import cn.com.easyerp.core.widget.grid.RecordModel;

public class TreeRecordGetter implements ExportRecordGetter {
    private TreeFormModel form;

    public TreeRecordGetter(TreeFormModel form) {
        this.form = form;
    }

    public RecordModel get(String id) {
        return this.form.getRecord(id);
    }
}
