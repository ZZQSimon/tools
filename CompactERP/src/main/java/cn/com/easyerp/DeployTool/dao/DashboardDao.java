package cn.com.easyerp.DeployTool.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.DashboardDescribe;
import cn.com.easyerp.DeployTool.service.SubscribeDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.data.DatabaseDataMap;

@Repository
public interface DashboardDao {
    void addDashBoard(@Param("dash") DashboardDescribe paramDashboardDescribe);

    List<DashboardDescribe> getDashboards();

    List<DatabaseDataMap> getTableList(@Param("table") String paramString1, @Param("where") String paramString2,
            @Param("authCondition") String paramString3, @Param("orderBy") String paramString4,
            @Param("defaultCount") int paramInt);

    int getTableCount(@Param("table") String paramString1, @Param("where") String paramString2);

    List<DatabaseDataMap> getChartList();

    List<String> getUsers();

    void addSubscribe(@Param("dashboard_id") String paramString1, @Param("subscribe_id") String paramString2,
            @Param("subscriber") String paramString3, @Param("subscribe_status") String paramString4);

    List<DatabaseDataMap> getSubscribesBysubscriber(@Param("subscriber") String paramString);

    int updateSubscribe(@Param("sub") SubscribeDescribe paramSubscribeDescribe,
            @Param("subscriber") String paramString);

    int updateSubscribesSeq(@Param("sub") SubscribeDescribe paramSubscribeDescribe,
            @Param("subscriber") String paramString);

    void reloadStatus(@Param("subscriber") String paramString);

    List<DatabaseDataMap> getHomeSubscribes(@Param("subscriber") String paramString);

    List<Map<String, Object>> getSubscribes();

    int isExistsDashboard(@Param("dash") DashboardDescribe paramDashboardDescribe);

    void updateDashboard(@Param("dash") DashboardDescribe paramDashboardDescribe);

    void deleteDashboard(@Param("dashboard_id") String paramString);

    void deleteSubscribe(@Param("dashboard_id") String paramString);

    void cancelSubscribe(@Param("subscribe_id") String paramString);

    int existSubscribe(@Param("user") AuthDetails paramAuthDetails);

    int existTheSubscribe(@Param("user") AuthDetails paramAuthDetails, @Param("dahsboard_id") String paramString);

    void updateSubscribeStatus(@Param("dashboard_id") String paramString1, @Param("subscriber") String paramString2);
}
