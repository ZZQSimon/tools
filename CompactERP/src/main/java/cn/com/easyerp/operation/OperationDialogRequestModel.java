package cn.com.easyerp.operation;

import cn.com.easyerp.core.data.ViewDataMap;
import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.widget.WidgetModelBase;

public class OperationDialogRequestModel<T extends WidgetModelBase> extends FormRequestModelBase<T> {
    private String column;
    private String method;
    private int seq;
    private String event_id;
    private ViewDataMap params;

    public String getEvent_id() {
        return this.event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public int getSeq() {
        return this.seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getColumn() {
        return this.column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getMethod() {
        return this.method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ViewDataMap getParams() {
        return this.params;
    }

    public void setParams(ViewDataMap params) {
        this.params = params;
    }
}
