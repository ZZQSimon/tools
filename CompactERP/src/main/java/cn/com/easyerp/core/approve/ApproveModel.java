package cn.com.easyerp.core.approve;

import java.util.List;

import cn.com.easyerp.DeployTool.service.TreeNode;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.Table;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.enums.ActionType;

@Widget("approve")
public class ApproveModel extends FormModelBase {
    private List<AuthGroup> authGroup;
    private Approve approve;
    private int count;
    private List<TreeNode> deptOrUserTree;
    private List<Table> approveTable;

    public List<TreeNode> getDeptOrUserTree() {
        return this.deptOrUserTree;
    }

    public void setDeptOrUserTree(List<TreeNode> deptOrUserTree) {
        this.deptOrUserTree = deptOrUserTree;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Approve getApprove() {
        return this.approve;
    }

    public void setApprove(Approve approve) {
        this.approve = approve;
    }

    public List<AuthGroup> getAuthGroup() {
        return this.authGroup;
    }

    public void setAuthGroup(List<AuthGroup> authGroup) {
        this.authGroup = authGroup;
    }

    public List<Table> getApproveTable() {
        return this.approveTable;
    }

    public void setApproveTable(List<Table> approveTable) {
        this.approveTable = approveTable;
    }

    protected ApproveModel(String parent) {
        super(ActionType.view, parent);
    }

    public String getTitle() {
        return "ApproveProcess";
    }
}
