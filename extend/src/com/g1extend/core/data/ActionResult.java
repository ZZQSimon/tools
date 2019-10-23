/*    */ package com.g1extend.core.data;
/*    */ 
/*    */ 
/*    */ public class ActionResult
/*    */ {
/*    */   private String result;
/*    */   private Object data;
/*    */   
/*    */   public ActionResult(String result, Object data) {
/* 10 */     this.result = result;
/* 11 */     this.data = data;
/*    */   }
/*    */ 
/*    */   
/* 15 */   public String getResult() { return this.result; }
/*    */ 
/*    */ 
/*    */   
/* 19 */   public void setResult(String result) { this.result = result; }
/*    */ 
/*    */ 
/*    */   
/* 23 */   public Object getData() { return this.data; }
/*    */ 
/*    */ 
/*    */   
/* 27 */   public void setData(Object data) { this.data = data; }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\core\data\ActionResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */