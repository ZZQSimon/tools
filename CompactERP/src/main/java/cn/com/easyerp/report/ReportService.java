package cn.com.easyerp.report;

import java.io.IOException;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.framework.common.ActionResult;

public interface ReportService<T> {
    void init(ReportModel<T> paramReportModel);

    @SuppressWarnings("rawtypes")
    ReportFormModel form(String paramString1, String paramString2, String paramString3, String paramString4,
            ReportFormRequestModel paramReportFormRequestModel);

    @SuppressWarnings("rawtypes")
    ActionResult print(ReportModel<T> paramReportModel, String paramString, AuthDetails paramAuthDetails,
            ReportFormRequestModel paramReportFormRequestModel) throws IOException;

    @SuppressWarnings("rawtypes")
    void checkParam(ReportFormRequestModel paramReportFormRequestModel);
}
