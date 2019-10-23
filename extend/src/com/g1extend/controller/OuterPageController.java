/*     */ package com.g1extend.controller;
/*     */ 
/*     */ import com.g1extend.entity.EntryVO;
/*     */ import com.g1extend.service.OuterPageService;
/*     */ import com.g1extend.utils.HttpClientUtil;
/*     */ import com.g1extend.utils.common;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.util.HashMap;
/*     */ import javax.servlet.ServletOutputStream;
/*     */ import javax.servlet.http.HttpServletRequest;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import org.json.JSONObject;
/*     */ import org.springframework.beans.factory.annotation.Autowired;
/*     */ import org.springframework.stereotype.Controller;
/*     */ import org.springframework.web.bind.annotation.RequestMapping;
/*     */ import org.springframework.web.bind.annotation.ResponseBody;
/*     */ import org.springframework.web.servlet.ModelAndView;
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
/*     */ @Controller
/*     */ @RequestMapping({"/friendlyPage"})
/*     */ public class OuterPageController
/*     */ {
/*     */   @Autowired
/*     */   private OuterPageService outerPageService;
/*     */   
/*     */   @RequestMapping({"/getFriendlyPage.do"})
/*     */   public ModelAndView getPage(String name, String city, String contracts, String phone, String address, String entry_type, String remark, HttpServletResponse response) throws UnsupportedEncodingException {
/*  41 */     ModelAndView mv = new ModelAndView();
/*  42 */     String dataBase = this.outerPageService.selectDataBase();
/*     */     
/*  44 */     HashMap<String, Object> typeParams = new HashMap<String, Object>();
/*  45 */     HashMap<String, Object> nameParams = new HashMap<String, Object>();
/*  46 */     HashMap<String, Object> cityParams = new HashMap<String, Object>();
/*  47 */     HashMap<String, Object> contractParams = new HashMap<String, Object>();
/*  48 */     HashMap<String, Object> phonesParams = new HashMap<String, Object>();
/*  49 */     HashMap<String, Object> addresssParams = new HashMap<String, Object>();
/*  50 */     HashMap<String, Object> remarkParams = new HashMap<String, Object>();
/*  51 */     typeParams.put("str", new String(entry_type.getBytes("ISO8859-1"), "UTF-8"));
/*  52 */     typeParams.put("domain", dataBase);
/*  53 */     nameParams.put("str", new String(name.getBytes("ISO8859-1"), "UTF-8"));
/*  54 */     nameParams.put("domain", dataBase);
/*  55 */     cityParams.put("str", new String(city.getBytes("ISO8859-1"), "UTF-8"));
/*  56 */     cityParams.put("domain", dataBase);
/*  57 */     contractParams.put("str", new String(contracts.getBytes("ISO8859-1"), "UTF-8"));
/*  58 */     contractParams.put("domain", dataBase);
/*  59 */     phonesParams.put("str", new String(phone.getBytes("ISO8859-1"), "UTF-8"));
/*  60 */     phonesParams.put("domain", dataBase);
/*  61 */     addresssParams.put("str", new String(address.getBytes("ISO8859-1"), "UTF-8"));
/*  62 */     addresssParams.put("domain", dataBase);
/*  63 */     remarkParams.put("str", new String(remark.getBytes("ISO8859-1"), "UTF-8"));
/*  64 */     remarkParams.put("domain", dataBase);
/*  65 */     EntryVO vo = new EntryVO();
/*  66 */     String types = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", typeParams);
/*  67 */     String names = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", nameParams);
/*  68 */     String citys = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", cityParams);
/*  69 */     String contract = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", contractParams);
/*  70 */     String phones = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", phonesParams);
/*  71 */     String addresss = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", addresssParams);
/*  72 */     String remarks = HttpClientUtil.doGetSSL("https://hris-uat.adidas.com.cn/adidas/data/decodeStrOut.do", remarkParams);
/*  73 */     if (names == null) {
/*  74 */       names = "";
/*     */     }
/*  76 */     if (citys == null)
/*  77 */       citys = ""; 
/*  78 */     if (contract == null)
/*  79 */       contract = ""; 
/*  80 */     if (phones == null)
/*  81 */       phones = ""; 
/*  82 */     if (addresss == null)
/*  83 */       addresss = ""; 
/*  84 */     if (remarks == null) {
/*  85 */       remarks = "";
/*     */     }
/*  87 */     JSONObject typeJsonObject = new JSONObject(types);
/*  88 */     JSONObject nameJsonObject = new JSONObject(names);
/*  89 */     JSONObject citysJsonObject = new JSONObject(citys);
/*  90 */     JSONObject contractJsonObject = new JSONObject(contract);
/*  91 */     JSONObject phonesJsonObject = new JSONObject(phones);
/*  92 */     JSONObject addresssJsonObject = new JSONObject(addresss);
/*  93 */     JSONObject remarksJsonObject = new JSONObject(remarks);
/*  94 */     String nameObject = nameJsonObject.getString("data");
/*  95 */     String citysObject = citysJsonObject.getString("data");
/*  96 */     String contractObject = contractJsonObject.getString("data");
/*  97 */     String phonesObject = phonesJsonObject.getString("data");
/*  98 */     String addresssObject = addresssJsonObject.getString("data");
/*  99 */     String remarksObject = remarksJsonObject.getString("data");
/* 100 */     String typeObject = typeJsonObject.getString("data");
/*     */     
/* 102 */     if (!"".equals(remarksObject) && remarksObject != null) {
/* 103 */       vo.setName(nameObject);
/* 104 */       vo.setCity(citysObject);
/* 105 */       vo.setEntry_type(typeObject);
/* 106 */       vo.setContracts(contractObject);
/* 107 */       vo.setAddress(addresssObject);
/* 108 */       vo.setPhone(phonesObject);
/* 109 */       vo.setRemark(remarksObject);
/* 110 */       mv.addObject("vo", vo);
/* 111 */       mv.setViewName("/cancelEntry");
/* 112 */       return mv;
/*     */     } 
/*     */     
/* 115 */     if (typeObject.equals("Intern") || typeObject.equals("实习生")) {
/* 116 */       vo.setName(nameObject);
/* 117 */       vo.setCity(citysObject);
/* 118 */       vo.setEntry_type(typeObject);
/* 119 */       vo.setContracts(contractObject);
/* 120 */       vo.setAddress(addresssObject);
/* 121 */       vo.setPhone(phonesObject);
/* 122 */       mv.addObject("vo", vo);
/* 123 */       mv.setViewName("/Internship");
/* 124 */       return mv;
/*     */     } 
/* 126 */     vo.setName(nameObject);
/* 127 */     vo.setCity(citysObject);
/* 128 */     vo.setEntry_type(typeObject);
/* 129 */     vo.setContracts(contractObject);
/* 130 */     vo.setAddress(addresssObject);
/* 131 */     vo.setPhone(phonesObject);
/* 132 */     mv.addObject("vo", vo);
/* 133 */     mv.setViewName("/formal");
/* 134 */     return mv;
/*     */   }
/*     */ 
/*     */   
/*     */   @RequestMapping({"/download.do"})
/*     */   @ResponseBody
/*     */   public void download(HttpServletRequest request, HttpServletResponse response) {
/* 141 */     String filepath = request.getRealPath("/WEB-INF/html/formal.jsp");
/*     */     try {
/* 143 */       File file = new File(filepath);
/* 144 */       String fileName = filepath.substring(filepath.lastIndexOf(File.separator) + 1);
/* 145 */       fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
/* 146 */       response.setContentType("application/msword");
/* 147 */       response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
/* 148 */       String len = String.valueOf(file.length());
/* 149 */       response.setHeader("Content-Length", len);
/* 150 */       ServletOutputStream servletOutputStream = response.getOutputStream();
/* 151 */       FileInputStream in = new FileInputStream(file);
/* 152 */       byte[] b = new byte[1024];
/*     */       int n;
/* 154 */       while ((n = in.read(b)) != -1) {
/* 155 */         servletOutputStream.write(b, 0, n);
/*     */       }
/* 157 */       in.close();
/* 158 */       servletOutputStream.close();
/* 159 */     } catch (Exception e) {
/* 160 */       e.printStackTrace();
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
/*     */   @RequestMapping({"/downloadHtml"})
/*     */   @ResponseBody
/*     */   public String downloadHtml(String text) throws IOException {
/* 174 */     String path = "c:/donwFile.html";
/* 175 */     common.saveFile(path, text);
/*     */     
/* 177 */     return "http访问文件路径";
/*     */   }
/*     */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\controller\OuterPageController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */