package cn.com.easyerp.batch;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.ImportDeployService;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/batch" })
public class BatchController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    @SuppressWarnings({ "rawtypes" })
    private BatchService batchService;
    @Autowired
    private ImportDeployService importService;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/batch.view" })
    public ModelAndView view(@RequestBody final BatchFormRequestModel request) {
        final String batchId = this.importService.getImportDeploy(request.getTable()).getBatch_id();
        final BatchDescribe batch = this.dataService.getBatch(batchId);
        final ApiDescribe api = (request.getImport_type() == 2) ? batch.getUpdate_api() : batch.getApi();
        final String apiName = api.getId();
        final TableDescribe table = this.dataService.getTableDesc(apiName);
        BatchInterceptor interceptor = batch.getInterceptor();
        if (interceptor == null) {
            interceptor = this.batchService;
        }
        final BatchFormModel form = interceptor.form(request, request.getParent(),
                this.dataService.buildEmptyDataRecord(apiName, (Map) null), batch, table.getDetail_disp_cols());
        form.setImport_type(request.getImport_type());
        return this.buildModelAndView((FormModelBase) form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Transactional
    @ResponseBody
    @RequestMapping({ "/batch.do" })
    public ActionResult exec(@RequestBody final BatchFormRequestModel request) {
        final BatchDescribe batch = this.dataService.getBatch(((BatchFormModel) request.getWidget()).getBatchId());
        BatchInterceptor interceptor = batch.getInterceptor();
        if (interceptor == null) {
            interceptor = this.batchService;
        }
        final ApiActionResult result = interceptor.intercept(batch, request);
        if (!result.isSuccess()) {
            Common.rollback();
        }
        return (ActionResult) result;
    }
}
