package cn.com.easyerp.core.mail.adidas;

import java.util.List;

public interface OuterAdidasService {
  List<EntryManage> updateAdidasData(List<String> paramList1, List<EntryManage> paramList2, String paramString1, String paramString2);
  
  List<EntryManage> updateAdidasDataByZhongZhi(List<String> paramList1, List<EntryManage> paramList2, String paramString1, String paramString2);
}


