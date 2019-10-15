package cn.com.easyerp.module.dao;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.module.importer.ImporterCustDaoModel;
import cn.com.easyerp.module.importer.ImporterExcgDaoModel;

@Repository
public interface ImporterDao {
  int insertTSaleOrder(@Param("id") String paramString1, @Param("import_date") String paramString2, @Param("order_date") String paramString3, @Param("customer_id") String paramString4, @Param("receiver_id") String paramString5, @Param("request_id") String paramString6, @Param("receive_indicate_date") String paramString7, @Param("classes") String paramString8, @Param("deliver_date") String paramString9, @Param("receive_time") String paramString10, @Param("delivery_no") String paramString11, @Param("receive_warehouse") String paramString12, @Param("out_warehouse") String paramString13, @Param("status") String paramString14, @Param("currency_H") String paramString15, @Param("exchange_rate") double paramDouble1, @Param("exchange_type") String paramString16, @Param("amount_H") double paramDouble2, @Param("home_amount") double paramDouble3, @Param("cre_user") String paramString17);
  
  int insertTSaleOrderDetail(@Param("id") String paramString1, @Param("sale_order_detail_id") String paramString2, @Param("item_id") String paramString3, @Param("customer_item_id") String paramString4, @Param("trade_type") String paramString5, @Param("quantity") double paramDouble1, @Param("unit_price") double paramDouble2, @Param("amount_D") double paramDouble3, @Param("currency_D") String paramString6, @Param("tax") double paramDouble4, @Param("unit") String paramString7, @Param("tax_type") String paramString8, @Param("voucher_id") String paramString9, @Param("cre_user") String paramString10);
  
  int updateTSaleOrderDetail(@Param("item_id") String paramString1, @Param("customer_item_id") String paramString2, @Param("trade_type") String paramString3, @Param("quantity") double paramDouble1, @Param("unit_price") double paramDouble2, @Param("amount_D") double paramDouble3, @Param("currency_D") String paramString4, @Param("tax") double paramDouble4, @Param("unit") String paramString5, @Param("tax_type") String paramString6, @Param("voucher_id") String paramString7, @Param("upd_user") String paramString8, @Param("id") String paramString9);
  
  int updateHdrAmount(@Param("id") String paramString1, @Param("amount_H") double paramDouble, @Param("upd_user") String paramString2);
  
  String selectCustomerId(@Param("typeid") int paramInt);
  
  double selectSumAmount(@Param("id") String paramString);
  
  String voucherIdIsExist(@Param("voucher_id") String paramString1, @Param("customer_id") String paramString2);
  
  String selectDeliverDate(@Param("receive_indicate_date") String paramString1, @Param("customer_id") String paramString2);
  
  ImporterCustDaoModel selectCustomerDetail(@Param("customer_id") String paramString);
  
  String selectItemId(@Param("customer_item_id") String paramString1, @Param("customer_id") String paramString2);
  
  ImporterExcgDaoModel selectExchangeDetail(@Param("exchangecurrency") String paramString);
  
  String selectCustomerItemId(@Param("item_id") String paramString1, @Param("customer_id") String paramString2);
  
  double selectTaxrate(@Param("taxrate_id") String paramString);
  
  double selectUnitPrice(@Param("customer_id") String paramString1, @Param("item_id") String paramString2, @Param("import_date") String paramString3);
  
  String selectBaseUnit(@Param("item_id") String paramString);
  
  int receiverIdIsExist(@Param("receiver_id") String paramString);
}


