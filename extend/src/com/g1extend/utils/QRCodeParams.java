/*    */ package com.g1extend.utils;
/*    */ 
/*    */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*    */ 
/*    */ public class QRCodeParams {
/*    */   private String txt;
/*    */   private String qrCodeUrl;
/*    */   private String filePath;
/*    */   private String fileName;
/*    */   private String logoPath;
/* 11 */   private Integer width = Integer.valueOf(300);
/* 12 */   private Integer height = Integer.valueOf(300);
/* 13 */   private Integer onColor = Integer.valueOf(-16777216);
/* 14 */   private Integer offColor = Integer.valueOf(-1);
/* 15 */   private Integer margin = Integer.valueOf(1);
/* 16 */   private ErrorCorrectionLevel level = ErrorCorrectionLevel.L;
/*    */ 
/*    */   
/* 19 */   public String getTxt() { return this.txt; }
/*    */ 
/*    */   
/* 22 */   public void setTxt(String txt) { this.txt = txt; }
/*    */ 
/*    */   
/* 25 */   public String getFilePath() { return this.filePath; }
/*    */ 
/*    */   
/* 28 */   public void setFilePath(String filePath) { this.filePath = filePath; }
/*    */ 
/*    */   
/* 31 */   public String getFileName() { return this.fileName; }
/*    */ 
/*    */   
/* 34 */   public void setFileName(String fileName) { this.fileName = fileName; }
/*    */ 
/*    */   
/* 37 */   public Integer getWidth() { return this.width; }
/*    */ 
/*    */   
/* 40 */   public void setWidth(Integer width) { this.width = width; }
/*    */ 
/*    */   
/* 43 */   public Integer getHeight() { return this.height; }
/*    */ 
/*    */   
/* 46 */   public void setHeight(Integer height) { this.height = height; }
/*    */ 
/*    */   
/* 49 */   public String getQrCodeUrl() { return this.qrCodeUrl; }
/*    */ 
/*    */   
/* 52 */   public void setQrCodeUrl(String qrCodeUrl) { this.qrCodeUrl = qrCodeUrl; }
/*    */ 
/*    */   
/* 55 */   public String getLogoPath() { return this.logoPath; }
/*    */ 
/*    */   
/* 58 */   public void setLogoPath(String logoPath) { this.logoPath = logoPath; }
/*    */ 
/*    */   
/* 61 */   public Integer getOnColor() { return this.onColor; }
/*    */ 
/*    */   
/* 64 */   public void setOnColor(Integer onColor) { this.onColor = onColor; }
/*    */ 
/*    */   
/* 67 */   public Integer getOffColor() { return this.offColor; }
/*    */ 
/*    */   
/* 70 */   public void setOffColor(Integer offColor) { this.offColor = offColor; }
/*    */ 
/*    */   
/* 73 */   public ErrorCorrectionLevel getLevel() { return this.level; }
/*    */ 
/*    */   
/* 76 */   public void setLevel(ErrorCorrectionLevel level) { this.level = level; }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getSuffixName() {
/* 84 */     String imgName = getFileName();
/* 85 */     if (imgName != null && !"".equals(imgName)) {
/* 86 */       return this.fileName.substring(this.fileName.lastIndexOf(".") + 1);
/*    */     }
/*    */     
/* 89 */     return "";
/*    */   }
/*    */   
/* 92 */   public Integer getMargin() { return this.margin; }
/*    */ 
/*    */   
/* 95 */   public void setMargin(Integer margin) { this.margin = margin; }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1exten\\utils\QRCodeParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */