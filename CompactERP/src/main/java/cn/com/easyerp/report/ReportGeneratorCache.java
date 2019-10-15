package cn.com.easyerp.report;

import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;

class ReportGeneratorCache {
    ReportPageConfigModel config;
    FormulaEvaluator evaluator;
    short cellDateFormat;
    List<Integer> headRows;
    Workbook wb;
    Sheet sheet;
    Map<String, String> sqlMap;
    String where;
    List<CellRangeAddress> ranges;
    int templateRows;
    int footRows;
    String subTable;
    int detailEnd = -1;
    int detailStart = -1;
    int detailRows;
    List<PagePrintingInfo> ppis;
    int currentPage;
}
