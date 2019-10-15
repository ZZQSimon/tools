package cn.com.easyerp.module.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface Tree2Dao {
  String itemNameSelect(@Param("item_id") String paramString);
  
  List<Map<String, Object>> receiveAndProSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> checkResultSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> productionDetailLotNoDownSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> productionDetailLotNoUpSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> productionDetailDownSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> productionDetailUpSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> keepSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> stockAdjustSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> transferAccountSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> transferAccountAfterSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> fromKeepSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> toKeepSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  List<Map<String, Object>> lotNoUpSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("parent_item_id") String paramString3, @Param("parent_process_no") String paramString4, @Param("parent_lot_no") String paramString5);
  
  List<Map<String, Object>> lotNoDownSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("parent_item_id") String paramString3, @Param("parent_process_no") String paramString4, @Param("parent_lot_no") String paramString5);
  
  String userQuantityDownSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("parent_item_id") String paramString3, @Param("parent_process_no") String paramString4, @Param("parent_lot_no") String paramString5);
  
  String userQuantityUpSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no_list") String paramString3);
  
  String userQuantityLotUpSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3);
  
  String userQuantityLotDownSelect(@Param("item_id") String paramString1, @Param("process_no") String paramString2, @Param("lot_no") String paramString3, @Param("product_Detail_Id") String paramString4);
}


