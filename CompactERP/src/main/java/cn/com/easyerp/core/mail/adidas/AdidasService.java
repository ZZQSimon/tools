package cn.com.easyerp.core.mail.adidas;

import java.util.List;

import cn.com.easyerp.core.cache.CityDescribe;

public interface AdidasService {
    List<String> addGcPreOnboarding(EntryManage paramEntryManage, CodeStr paramCodeStr, Object paramObject);

    List<String> updateDataByEntryManage(List<String> paramList1, List<ManageVO> paramList2, String paramString1,
            String paramString2);

    List<String> adidasZhongZhiConfirm(List<String> paramList1, List<ManageVO> paramList2, String paramString1,
            String paramString2);

    List<String> DeleteOuterDataByIdAndName(List<String> paramList1, List<String> paramList2);

    List<String> deleteInsertCity(List<CityDescribe> paramList, String paramString1, String paramString2);
}
