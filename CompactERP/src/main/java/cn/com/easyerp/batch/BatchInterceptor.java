package cn.com.easyerp.batch;

import java.util.List;

import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.framework.common.ApiActionResult;

public interface BatchInterceptor<T> {
    void init(BatchDescribe<T> paramBatchDescribe);

    ApiActionResult intercept(BatchDescribe<T> paramBatchDescribe, BatchFormRequestModel paramBatchFormRequestModel);

    BatchFormModel form(BatchFormRequestModel paramBatchFormRequestModel, String paramString,
            List<FieldModelBase> paramList, BatchDescribe<T> paramBatchDescribe, int paramInt);
}
