package cn.com.easyerp.tree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import cn.com.easyerp.core.view.TableBasedFormModel;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("tree")
public class TreeFormModel extends TableBasedFormModel {
    private List<FieldModelBase> fields;
    private int treeType;
    private String refTableName;
    private String parentidColumn;
    private List<FieldModelBase> fieldsCondition;
    private int searchMode;
    private boolean extend;
    private Map<String, RecordModel> nodeIdMap;
    private List<TreeNodeModel> nodes = new ArrayList<>();

    public TreeFormModel(String parent, List<FieldModelBase> fields, String tableName, String refTableName,
            int treeType, String parentidColumn, List<FieldModelBase> fieldsCondition, int searchMode, boolean extend) {
        super(ActionType.edit, parent, tableName);
        this.fields = fields;
        this.treeType = treeType;
        this.refTableName = refTableName;
        this.parentidColumn = parentidColumn;
        this.fieldsCondition = fieldsCondition;
        this.searchMode = searchMode;
        this.extend = extend;
        if (treeType == 2) {
            this.nodeIdMap = new HashMap<>();
        }
    }

    public List<FieldModelBase> getFields() {
        return this.fields;
    }

    public int getTreeType() {
        return this.treeType;
    }

    public String getRefTableName() {
        return this.refTableName;
    }

    public String getParentidColumn() {
        return this.parentidColumn;
    }

    public List<FieldModelBase> getFieldsCondition() {
        return this.fieldsCondition;
    }

    @JsonIgnore
    public int getSearchMode() {
        return this.searchMode;
    }

    public boolean isExtend() {
        return this.extend;
    }

    public RecordModel getRecord(String id) {
        return (RecordModel) this.nodeIdMap.get(id);
    }

    @JsonIgnore
    public List<TreeNodeModel> getNodes() {
        return this.nodes;
    }

    public void addNode(TreeNodeModel node) {
        this.nodes.add(node);
        if (this.treeType == 2) {
            this.nodeIdMap.put(node.getId(), node.getData());
        }
    }

    public void clearNodes() {
        this.nodes.clear();
    }
}
