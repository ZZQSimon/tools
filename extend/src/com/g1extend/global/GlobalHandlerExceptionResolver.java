/*    */ package com.g1extend.global;
/*    */ 
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
/*    */ import org.springframework.web.servlet.HandlerExceptionResolver;
/*    */ import org.springframework.web.servlet.ModelAndView;
/*    */ 
/*    */ 
/*    */ public class GlobalHandlerExceptionResolver
/*    */   implements HandlerExceptionResolver
/*    */ {
/*    */   public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse resp, Object o, Exception ex) {
/* 13 */     boolean isAsync = (req.getHeader("X-Requested-With") != null && "XMLHttpRequest".equals(req.getHeader("X-Requested-With").toString()));
/*    */     
/* 15 */     ModelAndView modelAndView = new ModelAndView();
/* 16 */     modelAndView.setViewName("/500");
/*    */     
/* 18 */     return modelAndView;
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\global\GlobalHandlerExceptionResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */