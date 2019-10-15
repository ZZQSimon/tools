package cn.com.easyerp.batch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.dao.UrlInterfaceDao;
import cn.com.easyerp.DeployTool.service.ImportDeployService;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ApiActionResult;

@Service
public class BatchService<T> implements BatchInterceptor<T> {
    private static final String BATCH_SUCCESS_MSG = "batch success";
    private static final String BATCH_FAILED_MSG = "batch failed";
    @Autowired
    protected ApiService apiService;
    @Autowired
    protected DataService dataService;
    @Autowired
    protected ViewService viewService;
    @Autowired
    protected UrlInterfaceDao urlDao;
    @Autowired
    protected ImportDeployService importService;

    @Override
    public void init(final BatchDescribe<T> batch) {
    }

    @Override
    public ApiActionResult intercept(final BatchDescribe<T> batch, final BatchFormRequestModel request) {
        ApiDescribe api = new ApiDescribe();
        api = ((request.getImport_type() == 2) ? batch.getUpdate_api() : batch.getApi());
        final DatabaseDataMap data = this.apiService.buildParams(api, request.getParam(),
                ((BatchFormModel) request.getWidget()).getId());
        final String apiId = this.urlDao.getUrlIdByUrl(api.getId());
        api.setId(apiId);
        final String batchaId = batch.getBatch_id();
        final String table = this.importService.getTableIdByBatchId(batchaId);
        final String sysApiName = ("import_" + table).toLowerCase();
        this.importService.callSysImportApi(sysApiName, AuthService.getCurrentUser().getId());
        ApiResult result = new ApiResult(0, (List) new ArrayList<>());
        result = this.apiService.exec(api, (DataMap) data);
        if (!result.isSuccess()) {
            return new ApiActionResult(result.getResult(),
                    this.dataService.getMessageText("batch failed", new Object[0]), result.getMessages());
        }
        return new ApiActionResult(result.getResult(), this.dataService.getMessageText("batch success", new Object[0]),
                result.getMessages());
    }

    @Override
    public BatchFormModel form(final BatchFormRequestModel request, final String parent,
            final List<FieldModelBase> fields, final BatchDescribe<T> batch, final int cols) {
        return new BatchFormModel(parent, fields, batch, cols);
    }
}
