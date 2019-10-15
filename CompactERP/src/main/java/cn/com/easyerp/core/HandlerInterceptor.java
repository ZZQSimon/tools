 package cn.com.easyerp.core;
 
 import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import cn.com.easyerp.core.view.FormModelBase;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 public class HandlerInterceptor
   extends HandlerInterceptorAdapter
 {
   private static final String FORCE_RELOAD_KEY = "FORCE_RELOAD_KEY";
   
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
     if (Common.getSessionObject("FORCE_RELOAD_KEY") == null) {
       Common.putSessionObject("FORCE_RELOAD_KEY", Boolean.valueOf(true));
       if (!"GET".equals(request.getMethod())) {
         Common.writeSessionTimeoutResult(response);
         return false;
       } 
     } 
     
     return super.preHandle(request, response, handler);
   }
 
 
 
 
 
 
 
 
 
 
 
 
   
   public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
     super.postHandle(request, response, handler, modelAndView);
     if (modelAndView == null)
       return; 
     FormModelBase model = (FormModelBase)modelAndView.getModel().get("form");
     
     if (model != null)
       ViewService.cacheForm(model, request); 
   }
 }


