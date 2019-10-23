/*    */ package com.g1extend.controller;
/*    */ 
/*    */ import com.g1extend.entity.MonthlyIncentiveReport;
/*    */ import com.g1extend.service.ImportExcelService;
/*    */ import java.util.List;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.springframework.beans.factory.annotation.Autowired;
/*    */ import org.springframework.stereotype.Controller;
/*    */ import org.springframework.web.bind.annotation.RequestMapping;
/*    */ import org.springframework.web.bind.annotation.RequestMethod;
/*    */ import org.springframework.web.bind.annotation.RequestParam;
/*    */ import org.springframework.web.multipart.MultipartFile;
/*    */ import org.springframework.web.servlet.ModelAndView;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @RequestMapping({"/import"})
/*    */ public class ImportController
/*    */ {
/*    */   @Autowired
/*    */   private ImportExcelService importExcelService;
/*    */   
/*    */   @RequestMapping(value = {"readExcel.do"}, method = {RequestMethod.GET})
/*    */   public ModelAndView readExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
/* 30 */     String path = request.getSession().getServletContext().getContextPath();
/* 31 */     ModelAndView mv = new ModelAndView();
/* 32 */     List<MonthlyIncentiveReport> list = this.importExcelService.importExcel(path, file);
/*    */     
/* 34 */     if (file == null) {
/* 35 */       return null;
/*    */     }
/*    */     
/* 38 */     String fileName = file.getOriginalFilename();
/* 39 */     Long size = Long.valueOf(file.getSize());
/* 40 */     if ("".equals(fileName) || fileName == null || 0L == size.longValue()) {
/* 41 */       return null;
/*    */     }
/* 43 */     mv.addObject("type", "import");
/* 44 */     mv.addObject("secUserList", list);
/* 45 */     mv.setViewName("/success");
/* 46 */     return mv;
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\controller\ImportController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */