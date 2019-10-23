/*     */ package com.g1extend.utils;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.MultiFormatWriter;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.util.Hashtable;
/*     */ import javax.imageio.ImageIO;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class QRCodeUtil
/*     */ {
/*  21 */   private static int width = 300;
/*  22 */   private static int height = 300;
/*  23 */   private static int onColor = -16777216;
/*  24 */   private static int offColor = -1;
/*  25 */   private static int margin = 1;
/*  26 */   private static ErrorCorrectionLevel level = ErrorCorrectionLevel.L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage generateQRImage(QRCodeParams params) {
/*     */     try {
/*  37 */       initData(params);
/*  38 */       String imgPath = params.getFilePath();
/*  39 */       String imgName = params.getFileName();
/*  40 */       String txt = params.getTxt();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  47 */       return generateQRImage(txt, imgPath, imgName, params.getSuffixName());
/*  48 */     } catch (Exception e) {
/*  49 */       e.printStackTrace();
/*  50 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage generateQRImage(String txt, String imgPath, String imgName, String suffix) {
/*  64 */     File filePath = new File(imgPath);
/*  65 */     if (!filePath.exists()) {
/*  66 */       filePath.mkdirs();
/*     */     }
/*  68 */     File imageFile = new File(imgPath, imgName);
/*     */     
/*  70 */     Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
/*     */     
/*  72 */     hints.put(EncodeHintType.ERROR_CORRECTION, level);
/*     */     
/*  74 */     hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
/*     */     
/*     */     try {
/*  77 */       MatrixToImageConfig config = new MatrixToImageConfig(onColor, offColor);
/*  78 */       BitMatrix bitMatrix = (new MultiFormatWriter()).encode(txt, BarcodeFormat.QR_CODE, width, height, hints);
/*  79 */       bitMatrix = deleteWhite(bitMatrix);
/*  80 */       BufferedImage image = toBufferedImage(bitMatrix, config);
/*  81 */       ImageIO.write(image, suffix, imageFile);
/*  82 */       return image;
/*  83 */     } catch (Exception e) {
/*  84 */       e.printStackTrace();
/*  85 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void initData(QRCodeParams params) {
/*  93 */     if (params.getWidth() != null) {
/*  94 */       width = params.getWidth().intValue();
/*     */     }
/*  96 */     if (params.getHeight() != null) {
/*  97 */       height = params.getHeight().intValue();
/*     */     }
/*  99 */     if (params.getOnColor() != null) {
/* 100 */       onColor = params.getOnColor().intValue();
/*     */     }
/* 102 */     if (params.getOffColor() != null) {
/* 103 */       offColor = params.getOffColor().intValue();
/*     */     }
/* 105 */     if (params.getLevel() != null) {
/* 106 */       level = params.getLevel();
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static BitMatrix deleteWhite(BitMatrix matrix) {
/* 112 */     int[] rec = matrix.getEnclosingRectangle();
/* 113 */     int resWidth = rec[2] + 1;
/* 114 */     int resHeight = rec[3] + 1;
/*     */     
/* 116 */     BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
/* 117 */     resMatrix.clear();
/* 118 */     for (int i = 0; i < resWidth; i++) {
/* 119 */       for (int j = 0; j < resHeight; j++) {
/* 120 */         if (matrix.get(i + rec[0], j + rec[1]))
/* 121 */           resMatrix.set(i, j); 
/*     */       } 
/*     */     } 
/* 124 */     return resMatrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public static BufferedImage toBufferedImage(BitMatrix matrix, MatrixToImageConfig config) {
/* 129 */     int width = matrix.getWidth();
/* 130 */     int height = matrix.getHeight();
/* 131 */     BufferedImage image = new BufferedImage(width, height, config.getBufferedImageColorModel());
/* 132 */     int onColor = config.getPixelOnColor();
/* 133 */     int offColor = config.getPixelOffColor();
/* 134 */     for (int x = 0; x < width; x++) {
/* 135 */       for (int y = 0; y < height; y++) {
/* 136 */         image.setRGB(x, y, matrix.get(x, y) ? onColor : offColor);
/*     */       }
/*     */     } 
/* 139 */     return image;
/*     */   }
/*     */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1exten\\utils\QRCodeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */