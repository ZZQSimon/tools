package cn.com.easyerp.module.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.module.orion.OrderDetailModel;

@Repository
public interface OrionModuleDao {
    void clearData(@Param("uid") String paramString);

    void insert(@Param("file_name") String paramString1, @Param("common_0") String paramString2,
            @Param("common_1") String paramString3, @Param("common_2") String paramString4,
            @Param("common_3") String paramString5, @Param("common_4") String paramString6,
            @Param("common_5") String paramString7, @Param("common_6") String paramString8,
            @Param("common_7") String paramString9, @Param("title_0") String paramString10,
            @Param("title_1") String paramString11, @Param("title_2") String paramString12,
            @Param("title_3") String paramString13, @Param("title_4") String paramString14,
            @Param("details_0") String paramString15, @Param("details_1") String paramString16,
            @Param("details_2") String paramString17, @Param("details_3") String paramString18,
            @Param("details_4") String paramString19, @Param("details_5") String paramString20,
            @Param("details_6") String paramString21, @Param("details_7") String paramString22,
            @Param("details_8") String paramString23, @Param("cre_user") String paramString24,
            @Param("upd_user") String paramString25);

    List<OrderDetailModel> getOrderDetailList(@Param("where") String paramString);

    List<OrderDetailModel> getOrderDetailViewList(@Param("where") String paramString);
}
