/*    */ package com.g1extend.controller;
/*    */ 
/*    */ import com.g1extend.entity.Contract_renewal;
/*    */ import com.g1extend.entity.Labor_contract;
/*    */ import com.g1extend.service.ContractService;
/*    */ import com.g1extend.utils.HttpClientUtil;
/*    */ import java.util.ArrayList;
/*    */ import java.util.HashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import org.springframework.beans.factory.annotation.Autowired;
/*    */ import org.springframework.stereotype.Controller;
/*    */ import org.springframework.web.bind.annotation.RequestMapping;
/*    */ import org.springframework.web.bind.annotation.RequestParam;
/*    */ import org.springframework.web.bind.annotation.ResponseBody;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Controller
/*    */ @RequestMapping({"/contract"})
/*    */ public class ContractController
/*    */ {
/*    */   @Autowired
/*    */   private ContractService contractService;
/*    */   
/*    */   @RequestMapping({"/judgeContract"})
/*    */   @ResponseBody
/*    */   public String judgeContract(@RequestParam(value = "getDate", required = true) String getDate, HttpServletRequest request) {
/* 31 */     List<Labor_contract> list = this.contractService.getLaborContract(getDate);
/* 32 */     System.out.println(String.valueOf(list.toString()) + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
/* 33 */     int count = 0;
/* 34 */     String domain = this.contractService.getDomain();
/* 35 */     int port = request.getServerPort();
/* 36 */     String basePath = String.valueOf(request.getScheme()) + "://" + request.getServerName();
/* 37 */     if (port != 0 && port != 80 && port != 443) {
/* 38 */       basePath = String.valueOf(basePath) + ":" + port;
/*    */     }
/* 40 */     List<Contract_renewal> contract_renewal_list = new ArrayList<Contract_renewal>();
/*    */     
/* 42 */     for (Labor_contract labor_contract : list) {
/* 43 */       count = this.contractService.selectCountById(labor_contract.getEmployee_id());
/* 44 */       System.out.println(String.valueOf(count) + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
/* 45 */       Contract_renewal contract_renewal = new Contract_renewal();
/* 46 */       if (labor_contract.getEmployee_type().equals("Intern")) {
/* 47 */         contract_renewal = this.contractService.judgeContract(labor_contract.getEmployee_id(), 1);
/*    */       }
/* 49 */       else if (count == 1) {
/*    */         
/* 51 */         contract_renewal = this.contractService.judgeContract(labor_contract.getEmployee_id(), 2);
/* 52 */       } else if (count >= 2) {
/*    */         
/* 54 */         contract_renewal = this.contractService.judgeContract(labor_contract.getEmployee_id(), 0);
/*    */       } 
/*    */       
/* 57 */       if (!"".equals(contract_renewal.getId()) && contract_renewal.getId() != null) {
/* 58 */         contract_renewal_list.add(contract_renewal);
/* 59 */         Map<String, Object> params = new HashMap<String, Object>();
/* 60 */         params.put("tableId", "t_contract_renewal");
/* 61 */         params.put("domain", domain);
/* 62 */         params.put("dataId", contract_renewal.getId());
/*    */         try {
/* 64 */           String result = HttpClientUtil.doGetSSL(String.valueOf(basePath) + "/DX_STYLE/approve/outsideSubmitApprove.do", params);
/* 65 */           System.out.println("*****" + result);
/* 66 */         } catch (Exception e) {
/* 67 */           return "false";
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 72 */     submit();
/*    */     
/* 74 */     this.contractService.sendEmail(contract_renewal_list, getDate);
/* 75 */     return "SUCCESS";
/*    */   }
/*    */   
/*    */   private void submit() {
/* 79 */     int num = this.contractService.getNoSubmitNum();
/* 80 */     if (num == 0) {
/* 81 */       this.contractService.updateCreUser();
/*    */       return;
/*    */     } 
/*    */     try {
/* 85 */       Thread.sleep(10000L);
/* 86 */       submit();
/* 87 */     } catch (InterruptedException e) {
/*    */       
/* 89 */       e.printStackTrace();
/*    */     } 
/*    */   }
/*    */ }


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\controller\ContractController.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */