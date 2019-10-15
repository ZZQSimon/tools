package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.cache.CityDescribe;
import cn.com.easyerp.core.mail.adidas.CodeStr;
import cn.com.easyerp.core.mail.adidas.EntryManage;
import cn.com.easyerp.core.mail.adidas.ManageVO;

@Repository
public interface AdidasDao {
    EntryManage findEntryManageByEntryId(@Param("entry_id") Object paramObject);

    List<EntryManage> findEntryManageByEntryIds(@Param("entry_ids") List<Object> paramList);

    CodeStr findCodeStrByNewestData();

    void deleteEntryManageByEntryId(@Param("entry_id") Object paramObject);

    void insertEntryManage(@Param("manage") EntryManage paramEntryManage);

    void insertCodeStr(@Param("cs") CodeStr paramCodeStr);

    void delEntryManageById(@Param("id") String paramString1, @Param("shop_id") String paramString2);

    void updateEntryManageById(@Param("list") List<EntryManage> paramList);

    List<EntryManage> getDataById();

    List<EntryManage> getDataById_flag();

    List<ManageVO> findInnerDataId();

    void updateManageFlag(@Param("list") List<EntryManage> paramList);

    List<EntryManage> findEntryManageByFlagAndZhongZhiFlag();

    Integer findData(@Param("id") String paramString1, @Param("shop_id") String paramString2);

    void delEntryManageByIdAndFlag(@Param("id") String paramString1, @Param("shop_id") String paramString2);

    String selectOuterDataBase();

    String selectLocalDataBase();

    List<CityDescribe> selectCity();

    void deleteCity();

    int insertCity(@Param("city") List<CityDescribe> paramList);

    void deleteDayEmpManage();
}
