package cn.com.easyerp.ril;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.StdGridModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("ril")
public class ReferenceInputListFormModel extends TableBasedFormModel {
    private List<FieldModelBase> sourceFilters;
    private String source;
    private Map<String, String> mapping;
    private StdGridModel grid;
    private Map<String, Object> filterData;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected ReferenceInputListFormModel(ReferenceInputListFormRequestModel request,
            List<FieldModelBase> sourceFilters, StdGridModel grid) {
        super(ActionType.edit, null, request.getTable());
        this.grid = grid;
        this.sourceFilters = sourceFilters;
        this.source = request.getSource();
        this.mapping = request.getMapping();
    }

    public String getSource() {
        return this.source;
    }

    @JsonIgnore
    public List<FieldModelBase> getSourceFilters() {
        return this.sourceFilters;
    }

    public Map<String, String> getMapping() {
        return this.mapping;
    }

    public StdGridModel getGrid() {
        return this.grid;
    }

    public Map<String, Object> getFilterData() {
        return this.filterData;
    }

    public void setFilterData(Map<String, Object> filterData) {
        this.filterData = filterData;
    }
}
