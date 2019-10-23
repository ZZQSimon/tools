/*    */ package com.g1extend.utils;
/*    */ 
/*    */ import java.text.ParseException;
/*    */ import java.text.SimpleDateFormat;
/*    */ import java.util.Date;
/*    */ import org.springframework.core.convert.converter.Converter;
/*    */ 
/*    */ public class DateConvert
/*    */   extends Object
/*    */   implements Converter<String, Date>
/*    */ {
/*    */   public Date convert(String stringDate) {
/* 13 */     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
/*    */     try {
/* 15 */       return simpleDateFormat.parse(stringDate);
/* 16 */     } catch (ParseException e) {
/* 17 */       e.printStackTrace();
/*    */       
/* 19 */       return null;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1exten\\utils\DateConvert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */