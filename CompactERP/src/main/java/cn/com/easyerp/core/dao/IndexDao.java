package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.view.form.index.BullModel;
import cn.com.easyerp.core.view.form.index.DeskModel;
import cn.com.easyerp.core.view.form.index.DesktopItemModel;
import cn.com.easyerp.core.view.form.index.DesktopListModel;
import cn.com.easyerp.core.view.form.index.IndexStatusModel;
import cn.com.easyerp.core.view.form.index.ShortModel;
import cn.com.easyerp.core.view.form.index.WorkModel;

@Repository
public interface IndexDao {
    BullModel selectBullList(@Param("owner") String paramString1, @Param("id") String paramString2);

    ShortModel selectShortList(@Param("owner") String paramString1, @Param("id") String paramString2);

    DeskModel selectChartList(@Param("owner") String paramString1, @Param("id") String paramString2);

    WorkModel selectWorkList(@Param("owner") String paramString1, @Param("id") String paramString2);

    Map<String, Object> getReList(@Param("owner") String paramString1, @Param("id") String paramString2);

    String getValueBySql(@Param("sql") String paramString);

    List<DesktopItemModel> selectdata(@Param("owner") String paramString);

    List<Map<String, Object>> getSuserList(@Param("tableId") String paramString1,
            @Param("column_name") String paramString2, @Param("name_column") String paramString3,
            @Param("id") String paramString4, @Param("type") String paramString5);

    List<IndexStatusModel> selectActionLogData();

    String selectStatus(@Param("column_name") String paramString1, @Param("table") String paramString2,
            @Param("key") String paramString3, @Param("key_value") String paramString4);

    DesktopListModel selectDesktopList(@Param("list_id") String paramString);
}
