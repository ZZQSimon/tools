/*    */ package com.g1extend.utils;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class MatrixToImageConfig
/*    */ {
/*    */   public static final int BLACK = -16777216;
/*    */   public static final int WHITE = -1;
/*    */   private final int onColor;
/*    */   private final int offColor;
/*    */   
/* 17 */   public MatrixToImageConfig() { this(-16777216, -1); }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MatrixToImageConfig(int onColor, int offColor) {
/* 25 */     this.onColor = onColor;
/* 26 */     this.offColor = offColor;
/*    */   }
/*    */ 
/*    */   
/* 30 */   public int getPixelOnColor() { return this.onColor; }
/*    */ 
/*    */ 
/*    */   
/* 34 */   public int getPixelOffColor() { return this.offColor; }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 39 */   int getBufferedImageColorModel() { return (this.onColor == -16777216 && this.offColor == -1) ? 12 : 1; }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1exten\\utils\MatrixToImageConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */