 package cn.com.easyerp.DeployTool.view;
 
 import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.view.FormRequestModelBase;
 
 public class ChartReportRequestModel extends FormRequestModelBase<ChartReportModel> {
   private ReportModel report;
   
   public ReportModel getReport() { return this.report; }
   
   private int type;
   
   public void setReport(ReportModel report) { this.report = report; }
 
 
   
   public int getType() { return this.type; }
 
 
   
   public void setType(int type) { this.type = type; }
 }


