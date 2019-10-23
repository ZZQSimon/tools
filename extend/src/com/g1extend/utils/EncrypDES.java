/*     */ package com.g1extend.utils;
/*     */ 
/*     */ import com.sun.crypto.provider.SunJCE;
/*     */ import java.security.Key;
/*     */ import java.security.Security;
/*     */ import javax.crypto.Cipher;
/*     */ import javax.crypto.spec.SecretKeySpec;
/*     */ 
/*     */ 
/*     */ public class EncrypDES
/*     */ {
/*  12 */   private static String strDefaultKey = "national";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Cipher encryptCipher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Cipher decryptCipher;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String byteArr2HexStr(byte[] arrB) throws Exception {
/*  31 */     int iLen = arrB.length;
/*     */     
/*  33 */     StringBuffer sb = new StringBuffer(iLen * 2);
/*  34 */     for (int i = 0; i < iLen; i++) {
/*  35 */       int intTmp = arrB[i];
/*     */       
/*  37 */       while (intTmp < 0) {
/*  38 */         intTmp += 256;
/*     */       }
/*     */       
/*  41 */       if (intTmp < 16) {
/*  42 */         sb.append("0");
/*     */       }
/*  44 */       sb.append(Integer.toString(intTmp, 16));
/*     */     } 
/*  46 */     return sb.toString();
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
/*     */ 
/*     */   
/*     */   public static byte[] hexStr2ByteArr(String strIn) throws Exception {
/*  61 */     byte[] arrB = strIn.getBytes();
/*  62 */     int iLen = arrB.length;
/*     */ 
/*     */     
/*  65 */     byte[] arrOut = new byte[iLen / 2];
/*  66 */     for (int i = 0; i < iLen; i += 2) {
/*  67 */       String strTmp = new String(arrB, i, 2);
/*  68 */       arrOut[i / 2] = (byte)Integer.parseInt(strTmp, 16);
/*     */     } 
/*  70 */     return arrOut;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   public EncrypDES() throws Exception { this(strDefaultKey); }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EncrypDES(String strKey) throws Exception {
/*     */     this.encryptCipher = null;
/*     */     this.decryptCipher = null;
/*  90 */     Security.addProvider(new SunJCE());
/*  91 */     Key key = getKey(strKey.getBytes());
/*     */     
/*  93 */     this.encryptCipher = Cipher.getInstance("DES");
/*  94 */     this.encryptCipher.init(1, key);
/*     */     
/*  96 */     this.decryptCipher = Cipher.getInstance("DES");
/*  97 */     this.decryptCipher.init(2, key);
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
/* 109 */   public byte[] encrypt(byte[] arrB) throws Exception { return this.encryptCipher.doFinal(arrB); }
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
/*     */   
/* 121 */   public String encrypt(String strIn) throws Exception { return byteArr2HexStr(encrypt(strIn.getBytes())); }
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
/*     */   
/* 133 */   public byte[] decrypt(byte[] arrB) throws Exception { return this.decryptCipher.doFinal(arrB); }
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
/*     */   
/* 145 */   public String decrypt(String strIn) throws Exception { return new String(decrypt(hexStr2ByteArr(strIn))); }
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
/*     */   
/*     */   private Key getKey(byte[] arrBTmp) throws Exception {
/* 158 */     byte[] arrB = new byte[8];
/*     */ 
/*     */     
/* 161 */     for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
/* 162 */       arrB[i] = arrBTmp[i];
/*     */     }
/*     */ 
/*     */     
/* 166 */     return new SecretKeySpec(arrB, "DES");
/*     */   }
/*     */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1exten\\utils\EncrypDES.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */