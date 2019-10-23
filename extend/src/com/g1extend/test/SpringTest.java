/*    */ package com.g1extend.test;
/*    */ 
/*    */ import org.springframework.context.support.ClassPathXmlApplicationContext;
/*    */ 
/*    */ 
/*    */ public class SpringTest
/*    */ {
/*    */   public static void main(String[] args) {
/*  9 */     ClassPathXmlApplicationContext classPathXmlApplicationContext = new ClassPathXmlApplicationContext("config/spring-common.xml");
/* 10 */     Object userMapper = classPathXmlApplicationContext.getBean("userMapper");
/* 11 */     System.out.println(userMapper);
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\test\SpringTest.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */