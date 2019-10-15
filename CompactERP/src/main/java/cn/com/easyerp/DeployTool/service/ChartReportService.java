 package cn.com.easyerp.DeployTool.service;
 
 import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.dao.ChartReportDao;
import cn.com.easyerp.DeployTool.dao.InternationalDao;
import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
 
 @Service
 public class ChartReportService
 {
   @Autowired
   private ChartReportDao reportDao;
   @Autowired
   private TableDeployDao tableDeployDao;
   @Autowired
   private InternationalDao internationalDao;
   
   public List<ReportModel> getReportsByType(int report_type) { return this.reportDao.getReportsByType(report_type); }
 
   
   public ReportModel saveReport(ReportModel report, AuthDetails user) {
     International i18n = new International();
     String i18n_id = this.tableDeployDao.getId("c_international", "");
     i18n.setInternational_id(i18n_id);
     switch (user.getLanguage_id()) {
       case "cn":
         i18n.setCn(report.getInternational_id());
         break;
       case "en":
         i18n.setEn(report.getInternational_id());
         break;
       case "jp":
         i18n.setJp(report.getInternational_id());
         break;
     } 
     this.internationalDao.saveRetrievalInter(i18n);
     report.setInternational_id(i18n_id);
     if (report.getId() == null || "".equals(report.getId())) {
       String id = "C_" + this.tableDeployDao.getId("c_report", "").replaceFirst("^0*", "");
       report.setId(id);
       this.reportDao.saveReport(report);
     } else {
       this.reportDao.updateReport(report);
     } 
     
     return report;
   }
   
   public void deleteReport(ReportModel report) {
     this.reportDao.deleteReport(report);
     this.reportDao.deleteI18n(report);
   }
 }


