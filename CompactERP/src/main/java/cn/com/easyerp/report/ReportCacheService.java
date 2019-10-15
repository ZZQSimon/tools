package cn.com.easyerp.report;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.ApplicationContextProvider;
import cn.com.easyerp.core.cache.CacheLoader;
import cn.com.easyerp.core.dao.ChartDao;

@Service
public class ReportCacheService extends CacheLoader<ReportModel> {
    private static final int REPORT_TYPE_TABLE = 4;
    private static final String CACHE_KEY = "report";
    @Autowired
    private ChartDao chartDao;

    public String getKey() {
        return "report";
    }

    public Map<String, ReportModel> reload() {
        Map<String, ReportModel> map = new HashMap<String, ReportModel>();
        for (ReportModel chart : this.chartDao.selectChart()) {
            String service_name = chart.getService_name();
            if (service_name != null) {
                ReportService service = (ReportService) ApplicationContextProvider.getBean(service_name);
                chart.setService(service);
                service.init(chart);
            }
            map.put(chart.getId(), chart);
        }

        return map;
    }

    public Object export(Map<String, ReportModel> cache) {
        Map<String, ReportModel> ret = new HashMap<String, ReportModel>(cache.size());
        for (Map.Entry<String, ReportModel> entry : cache.entrySet()) {
            if (((ReportModel) entry.getValue()).getReport_type() == 4)
                ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    public ReportModel getChartById(String report_id) {
        return this.chartDao.getChartById(report_id);
    }
}
