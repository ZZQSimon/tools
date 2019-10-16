package cn.com.easyerp.DeployTool.view;

import cn.com.easyerp.core.view.FormRequestModelBase;

public class ImportDeployRequestModel extends FormRequestModelBase<ImportDeployModel> {
    private ImportDeployModel importDeploy;

    public ImportDeployModel getImportDeploy() {
        return this.importDeploy;
    }

    public void setImportDeploy(ImportDeployModel importDeploy) {
        this.importDeploy = importDeploy;
    }
}
