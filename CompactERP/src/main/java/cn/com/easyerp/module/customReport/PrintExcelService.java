package cn.com.easyerp.module.customReport;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.module.customReport.CustomData;
import cn.com.easyerp.module.customReport.CustomDataDescribe;
import cn.com.easyerp.module.customReport.PrintExcelService;
import cn.com.easyerp.storage.StorageService;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFClientAnchor;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFPatriarch;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.ClientAnchor.AnchorType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jfree.chart.ChartColor;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.RectangleEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PrintExcelService {
    private static final String LEAVING_TYPE_COLUMN = "Leaving Type";

    public ActionResult excelPrint(List<CustomData> data, List<String> allMonth) {
        if (null == data || data.size() == 0 || allMonth == null || allMonth.size() == 0) {
            throw new ApplicationException("have no data");
        }

        try (HSSFWorkbook workbook = new HSSFWorkbook();) {
            HSSFSheet sheet = workbook.createSheet();

            List<Map<String, Object>> selectColumn = selectColumn((CustomData) data.get(0));
            if (selectColumn.size() == 0) {
                throw new ApplicationException("no choose any type");
            }

            createMonthRow(workbook, sheet, allMonth, selectColumn);

            createHeadRow(workbook, sheet, allMonth, selectColumn);

            createDataRow(workbook, sheet, data, allMonth, selectColumn);

            createPicture(workbook, sheet, allMonth, data);
            String filename = "rate.xls";
            String uuid = UUID.randomUUID().toString();
            String templatePath = this.storageService.templatePath(uuid, filename);

            try (OutputStream outputStream1 = new FileOutputStream(templatePath);) {
                workbook.write(outputStream1);
                outputStream1.flush();
                return new ActionResult(true, templatePath);
            }
        } catch (Exception e) {
            throw new ApplicationException(e.getMessage());
        }

    }

    @Autowired
    private StorageService storageService;

    @Autowired
    private CacheService cacheService;

    private void createMonthRow(HSSFWorkbook workbook, HSSFSheet sheet, List<String> allMonth,
            List<Map<String, Object>> selectColumn) {
        HSSFCellStyle headMonthStyle = headMonthStyle(workbook);
        HSSFRow row0 = sheet.createRow(0);

        row0.setHeight((short) 500);

        for (int i = 0; i < allMonth.size(); i++) {
            HSSFCell cell0 = row0.createCell(selectColumn.size() + 4 * i);
            cell0.setCellValue(new HSSFRichTextString((String) allMonth.get(i)));
            cell0.setCellStyle(headMonthStyle);
            CellRangeAddress range = new CellRangeAddress(0, 0, selectColumn.size() + 4 * i,
                    selectColumn.size() + 4 * i + 3);
            sheet.addMergedRegion(range);
            if (i == allMonth.size() - 1) {
                HSSFCell cellCount = row0.createCell(selectColumn.size() + 4 * i + 4);
                cellCount.setCellValue(new HSSFRichTextString("TTL"));
                cellCount.setCellStyle(headMonthStyle);
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private void createHeadRow(HSSFWorkbook workbook, HSSFSheet sheet, List<String> allMonth,
            List<Map<String, Object>> selectColumn) {
        HSSFCellStyle headSelectStyle = headSelectStyle(workbook);
        HSSFCellStyle headStyle = headStyle(workbook);
        HSSFRow row1 = sheet.createRow(1);
        row1.setHeight((short) 800);
        if (selectColumn.size() != 0) {
            for (int i = 0; i < selectColumn.size(); i++) {

                sheet.setColumnWidth(i, ((Integer) ((Map) selectColumn.get(i)).get("width")).intValue());

                HSSFCell cellSelect = row1.createCell(i);
                cellSelect.setCellValue(new HSSFRichTextString(((Map) selectColumn.get(i)).get("name").toString()));
                cellSelect.setCellStyle(headSelectStyle);
            }
        }
        List<Map<String, Object>> monthColumn = monthColumn();
        int cellNum = selectColumn.size();
        for (int i = 0; i < allMonth.size(); i++) {
            for (int j = 0; j < monthColumn.size(); j++) {

                sheet.setColumnWidth(cellNum, ((Integer) ((Map) monthColumn.get(j)).get("width")).intValue());

                HSSFCell cellSelect = row1.createCell(cellNum);
                cellSelect.setCellValue(new HSSFRichTextString(((Map) monthColumn.get(j)).get("name").toString()));
                cellSelect.setCellStyle(headStyle);
                cellNum++;
            }
            if (i == allMonth.size() - 1) {

                sheet.setColumnWidth(cellNum, 8000);
                HSSFCell cellSelect = row1.createCell(cellNum);
                cellSelect.setCellValue(new HSSFRichTextString("TTl Attrition rate"));
                cellSelect.setCellStyle(headStyle);
            }
        }
    }

    @SuppressWarnings({ "rawtypes" })
    private void createDataRow(HSSFWorkbook workbook, HSSFSheet sheet, List<CustomData> data, List<String> allMonth,
            List<Map<String, Object>> selectColumn) {
        String tableName = CustomReportController.FILTER_TABLE;
        TableDescribe table = this.cacheService.getTableDesc(tableName);
        String dic_id = table.getColumn(LEAVING_TYPE_COLUMN).getDic_id();
        Map<String, I18nDescribe> dict = this.cacheService.getDict(dic_id);
        HSSFCellStyle rateStyle = rateStyle(workbook);
        int dataRowCount = 2;
        for (CustomData customData : data) {

            int currentCellNum = 0;

            HSSFRow dataRow = sheet.createRow(dataRowCount++);
            dataRow.setHeight((short) 300);

            for (int i = 0; i < selectColumn.size(); i++) {
                String language_id, text;
                I18nDescribe keyI18N;
                HSSFCell cellData = dataRow.createCell(i);
                switch (((Map) selectColumn.get(i)).get("name").toString()) {
                case "Region":
                    cellData.setCellValue(new HSSFRichTextString(customData.getRegion()));
                    break;
                case "Store Type":
                    cellData.setCellValue(new HSSFRichTextString(customData.getStore_Type()));
                    break;
                case "Grade":
                    cellData.setCellValue(new HSSFRichTextString(customData.getSum_grade()));
                    break;
                case "Employee Type":
                    cellData.setCellValue(new HSSFRichTextString(customData.getEmployeeType()));
                    break;
                case LEAVING_TYPE_COLUMN:
                    keyI18N = (I18nDescribe) dict.get(customData.getLeavingType());
                    text = "";
                    language_id = AuthService.getCurrentUser().getLanguage_id();
                    if (language_id == null) {
                        text = keyI18N.getCn();
                    }
                    switch (language_id) {
                    case "cn":
                        text = keyI18N.getCn();
                        break;
                    case "en":
                        text = keyI18N.getEn();
                        break;
                    case "jp":
                        text = keyI18N.getJp();
                        break;
                    default:
                        text = keyI18N.getCn();
                        break;
                    }
                    cellData.setCellValue(new HSSFRichTextString(text));
                    break;
                case "Store":
                    cellData.setCellValue(new HSSFRichTextString(customData.getStore()));
                    break;
                }
                currentCellNum++;
            }

            for (int i = 0; i < allMonth.size(); i++) {
                if (customData.getCustomDataDescribes() == null) {
                    currentCellNum += 4;
                } else {

                    CustomDataDescribe monthData = (CustomDataDescribe) customData.getCustomDataDescribes()
                            .get(allMonth.get(i));
                    if (monthData == null) {
                        currentCellNum += 4;
                    } else {

                        HSSFCell monthDataCellBegin = dataRow.createCell(currentCellNum);
                        monthDataCellBegin.setCellValue(monthData.getBeginningHC());
                        currentCellNum++;
                        HSSFCell monthDataCellExit = dataRow.createCell(currentCellNum);
                        monthDataCellExit.setCellValue(monthData.getExit());
                        currentCellNum++;
                        HSSFCell monthDataCellEnd = dataRow.createCell(currentCellNum);
                        monthDataCellEnd.setCellValue(monthData.getMonthEndHC());
                        currentCellNum++;

                        HSSFCell monthDataCellRate = dataRow.createCell(currentCellNum);
                        String cellName1 = getCellName(dataRowCount - 1, currentCellNum - 2);
                        String cellName2 = getCellName(dataRowCount - 1, currentCellNum - 3);
                        String cellName3 = getCellName(dataRowCount - 1, currentCellNum - 1);
                        monthDataCellRate.setCellFormula(cellName1 + "/(" + cellName2 + "+" + cellName3 + ")*2");
                        monthDataCellRate.setCellStyle(rateStyle);
                        currentCellNum++;
                    }
                }
            }
            HSSFCell rateSumCell = dataRow.createCell(currentCellNum);
            String sumFormula = "SUM(";
            for (int i = 0; i < allMonth.size(); i++) {
                sumFormula = sumFormula + getCellName(dataRowCount - 1, selectColumn.size() + 4 * i + 3) + ",";
            }
            sumFormula = sumFormula.substring(0, sumFormula.length() - 1) + ")";
            rateSumCell.setCellFormula(sumFormula);
            rateSumCell.setCellStyle(rateStyle);
        }

        createTotalRow(workbook, sheet, dataRowCount, allMonth, selectColumn);
    }

    private void createTotalRow(HSSFWorkbook workbook, HSSFSheet sheet, int row, List<String> allMonth,
            List<Map<String, Object>> selectColumn) {
        HSSFCellStyle rateStyle = rateStyle(workbook);

        HSSFRow totalRow = sheet.createRow(row);
        totalRow.setHeight((short) 300);
        HSSFCell totalCell = totalRow.createCell(selectColumn.size() - 1);
        totalCell.setCellValue(new HSSFRichTextString("Total"));
        for (int i = 0; i < allMonth.size(); i++) {

            HSSFCell cellBegin = totalRow.createCell(selectColumn.size() + 4 * i);
            String cellNameBegin = getCellName(2, selectColumn.size() + 4 * i);
            String cellNameEnd = getCellName(row - 1, selectColumn.size() + 4 * i);
            cellBegin.setCellFormula("SUM(" + cellNameBegin + ":" + cellNameEnd + ")");

            HSSFCell cellExit = totalRow.createCell(selectColumn.size() + 4 * i + 1);
            String cellNamecellExitBegin = getCellName(2, selectColumn.size() + 4 * i + 1);
            String cellNamecellExitEnd = getCellName(row - 1, selectColumn.size() + 4 * i + 1);
            cellExit.setCellFormula("SUM(" + cellNamecellExitBegin + ":" + cellNamecellExitEnd + ")");

            HSSFCell cellEnd = totalRow.createCell(selectColumn.size() + 4 * i + 2);
            String cellNameEndBegin = getCellName(2, selectColumn.size() + 4 * i + 2);
            String cellNameEndEnd = getCellName(row - 1, selectColumn.size() + 4 * i + 2);
            cellEnd.setCellFormula("SUM(" + cellNameEndBegin + ":" + cellNameEndEnd + ")");

            HSSFCell monthDataCellRate = totalRow.createCell(selectColumn.size() + 4 * i + 3);
            String cellName1 = getCellName(row, selectColumn.size() + 4 * i + 3 - 2);
            String cellName2 = getCellName(row, selectColumn.size() + 4 * i + 3 - 3);
            String cellName3 = getCellName(row, selectColumn.size() + 4 * i + 3 - 1);
            monthDataCellRate.setCellFormula(cellName1 + "/(" + cellName2 + "+" + cellName3 + ")*2");
            monthDataCellRate.setCellStyle(rateStyle);
        }
    }

    private void createPicture(HSSFWorkbook workbook, HSSFSheet sheet, List<String> allMonth, List<CustomData> data) {
        List<String> timeList = buildX(allMonth);
        List<Double> dataListAll = null, dataListFTE = null, dataListIntern = null;
        if (((CustomData) data.get(0)).getEmployeeType() == null
                || "".equals(((CustomData) data.get(0)).getEmployeeType())) {
            dataListAll = buildPictureData(allMonth, data);
        } else {
            dataListFTE = buildPictureData(allMonth, data, "FTE");
            dataListIntern = buildPictureData(allMonth, data, "Intern");
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < timeList.size(); i++) {
            String time = (String) timeList.get(i);
            if (dataListAll != null && dataListAll.size() != 0) {
                dataset.addValue((Number) dataListAll.get(i), "all", time);
            }
            if (dataListFTE != null && dataListFTE.size() != 0) {
                dataset.addValue((Number) dataListFTE.get(i), "FTE", time);
            }
            if (dataListIntern != null && dataListIntern.size() != 0) {
                dataset.addValue((Number) dataListIntern.get(i), "Intern", time);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart("鍥捐〃鏍囬", "鏈堜唤", "绂昏亴鐜�", dataset, PlotOrientation.VERTICAL,
                true, true, true);

        Font titleFont = new Font("榛戜綋", 1, 25);
        Font xfont = new Font("榛戜綋", 1, 25);
        Font labelFont = new Font("榛戜綋", 1, 25);

        chart.getLegend().setItemFont(new Font("榛戜綋", 1, 25));

        chart.setTitle(new TextTitle(chart.getTitle().getText(), titleFont));

        CategoryPlot plot = chart.getCategoryPlot();

        LineAndShapeRenderer lasp = (LineAndShapeRenderer) plot.getRenderer();

        lasp.setBaseShapesVisible(true);
        lasp.setBaseItemLabelsVisible(true);

        lasp.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        lasp.setBaseItemLabelFont(new Font("瀹嬩綋", 1, 15));
        plot.setRenderer(lasp);

        lasp.setDrawOutlines(true);

        lasp.setUseFillPaint(false);
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        renderer.setSeriesStroke(0, new BasicStroke(1.0F));
        renderer.setSeriesPaint(0, new Color('脪', 105, 30));
        renderer.setSeriesStroke(1, new BasicStroke(1.0F));
        renderer.setSeriesPaint(1, new Color(0, '驴', '每'));

        lasp.setSeriesOutlineStroke(0, new BasicStroke(0.025F));
        lasp.setSeriesOutlineStroke(1, new BasicStroke(0.05F));

        plot.setDomainGridlinePaint(Color.gray);
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.gray);
        plot.setRangeGridlinesVisible(true);

        CategoryAxis domainAxis = plot.getDomainAxis();

        domainAxis.setAxisLineVisible(false);

        domainAxis.setLabelFont(xfont);

        domainAxis.setTickLabelFont(labelFont);

        NumberAxis numAxis = (NumberAxis) plot.getRangeAxis();
        numAxis.setTickUnit(new NumberTickUnit(0.1D));

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setLabelFont(xfont);

        rangeAxis.setAxisLineVisible(false);

        rangeAxis.setTickLabelFont(labelFont);
        rangeAxis.setFixedDimension(0.0D);
        CategoryPlot cp = chart.getCategoryPlot();

        cp.setBackgroundPaint(ChartColor.WHITE);
        cp.setRangeGridlinePaint(ChartColor.GRAY);

        LegendTitle legendTitle = new LegendTitle(chart.getPlot());
        legendTitle.setPosition(RectangleEdge.BOTTOM);

        ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
        try {
            ChartUtilities.writeChartAsPNG(byteArrayOut, chart, 800, 600);
            String fileSavePath = "rate.png";
            BufferedImage bufferImg = ImageIO.read(new File(fileSavePath));
            ImageIO.write(bufferImg, "png", byteArrayOut);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HSSFPatriarch patriarch = sheet.createDrawingPatriarch();

        // HSSFClientAnchor anchor = new HSSFClientAnchor(0, 0, 0, 0, 0,
        // (short)(data.size() + 10), 12, (short)(data.size() + 50));
        HSSFClientAnchor anchor = new HSSFClientAnchor();
        anchor.setDx1(0);
        anchor.setDy1(0);
        anchor.setDx2(0);
        anchor.setDy2(0);
        anchor.setCol1(0);
        anchor.setRow1(data.size() + 10);
        anchor.setCol2(12);
        anchor.setRow2(data.size() + 50);
        anchor.setAnchorType(AnchorType.DONT_MOVE_AND_RESIZE);

        patriarch.createPicture(anchor, workbook.addPicture(byteArrayOut.toByteArray(), 6));
    }

    private List<String> buildX(List<String> allMonth) {
        return allMonth;
    }

    private List<Double> buildPictureData(List<String> allMonth, List<CustomData> data) {
        return buildPictureData(allMonth, data, null);
    }

    private List<Double> buildPictureData(List<String> allMonth, List<CustomData> data, String type) {
        Map<String, Double> dataMap = new HashMap<String, Double>();
        for (CustomData customData : data) {
            if (type != null && !type.equals(customData.getEmployeeType())) {
                continue;
            }

            Map<String, CustomDataDescribe> customDataDescribes = customData.getCustomDataDescribes();
            if (customDataDescribes == null) {
                continue;
            }
            for (Map.Entry<String, CustomDataDescribe> entry : customDataDescribes.entrySet()) {
                if (dataMap.get(entry.getKey()) == null) {
                    dataMap.put(entry.getKey(), ((CustomDataDescribe) entry.getValue()).getAttritionRate());
                    continue;
                }
                dataMap.put(entry.getKey(),
                        Double.valueOf(((CustomDataDescribe) entry.getValue()).getAttritionRate().doubleValue()
                                + ((Double) dataMap.get(entry.getKey())).doubleValue()));
            }
        }

        List<Double> resultData = new ArrayList<Double>();
        for (String month : allMonth) {
            if (dataMap.get(month) == null) {
                resultData.add(null);
                continue;
            }
            resultData.add(dataMap.get(month));
        }

        return resultData;
    }

    private List<Map<String, Object>> selectColumn(CustomData data) {
        List<Map<String, Object>> selectColumn = new ArrayList<Map<String, Object>>();
        if (null != data.getRegion()) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", "Region");
            column.put("width", Integer.valueOf(4000));
            selectColumn.add(column);
        }
        if (null != data.getStore_Type()) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", "Store Type");
            column.put("width", Integer.valueOf(8000));
            selectColumn.add(column);
        }
        if (null != data.getEmployeeType()) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", "Employee Type");
            column.put("width", Integer.valueOf(4000));
            selectColumn.add(column);
        }
        if (null != data.getSum_grade() && !"all".equals(data.getSum_grade())) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", "Grade");
            column.put("width", Integer.valueOf(4000));
            selectColumn.add(column);
        }
        if (null != data.getLeavingType() && !"all".equals(data.getLeavingType())) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", LEAVING_TYPE_COLUMN);
            column.put("width", Integer.valueOf(5000));
            selectColumn.add(column);
        }
        if (null != data.getStore()) {
            Map<String, Object> column = new HashMap<String, Object>();
            column.put("name", "Store");
            column.put("width", Integer.valueOf(4000));
            selectColumn.add(column);
        }
        return selectColumn;
    }

    private List<Map<String, Object>> monthColumn() {
        List<Map<String, Object>> monthColumn = new ArrayList<Map<String, Object>>();
        Map<String, Object> oneMonthBeginning = new HashMap<String, Object>();
        oneMonthBeginning.put("name", "Beginning HC");
        oneMonthBeginning.put("width", Integer.valueOf(8000));
        monthColumn.add(oneMonthBeginning);
        Map<String, Object> oneMonthExit = new HashMap<String, Object>();
        oneMonthExit.put("name", "Exit");
        oneMonthExit.put("width", Integer.valueOf(4000));
        monthColumn.add(oneMonthExit);
        Map<String, Object> oneMonthEnd = new HashMap<String, Object>();
        oneMonthEnd.put("name", "Month-end HC");
        oneMonthEnd.put("width", Integer.valueOf(8000));
        monthColumn.add(oneMonthEnd);
        Map<String, Object> oneMonthAttrition = new HashMap<String, Object>();
        oneMonthAttrition.put("name", "Attrition rate");
        oneMonthAttrition.put("width", Integer.valueOf(8000));
        monthColumn.add(oneMonthAttrition);
        return monthColumn;
    }

    private HSSFCellStyle headMonthStyle(HSSFWorkbook workbook) {
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("榛戜綋");
        headfont.setFontHeightInPoints((short) 22);
        // headfont.setBoldweight((short) 700);
        headfont.setBold(true);

        HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HorizontalAlignment.CENTER);
        headstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headstyle.setBorderBottom(BorderStyle.THIN);
        headstyle.setBorderLeft(BorderStyle.THIN);
        headstyle.setBorderRight(BorderStyle.THIN);
        headstyle.setBorderTop(BorderStyle.THIN);
        headstyle.setLocked(true);
        return headstyle;
    }

    private HSSFCellStyle headSelectStyle(HSSFWorkbook workbook) {
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("榛戜綋");
        headfont.setFontHeightInPoints((short) 14);
        // headfont.setBoldweight((short) 700);
        headfont.setBold(true);

        HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HorizontalAlignment.CENTER);
        headstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headstyle.setBorderBottom(BorderStyle.THIN);
        headstyle.setBorderLeft(BorderStyle.THIN);
        headstyle.setBorderRight(BorderStyle.THIN);
        headstyle.setBorderTop(BorderStyle.THIN);
        headstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headstyle.setFillForegroundColor((short) 13);
        headstyle.setLocked(true);
        return headstyle;
    }

    private HSSFCellStyle headStyle(HSSFWorkbook workbook) {
        HSSFFont headfont = workbook.createFont();
        headfont.setFontName("榛戜綋");
        headfont.setFontHeightInPoints((short) 14);
        // headfont.setBoldweight((short) 700);
        headfont.setBold(true);

        HSSFCellStyle headstyle = workbook.createCellStyle();
        headstyle.setFont(headfont);
        headstyle.setAlignment(HorizontalAlignment.CENTER);
        headstyle.setVerticalAlignment(VerticalAlignment.CENTER);
        headstyle.setBorderBottom(BorderStyle.THIN);
        headstyle.setBorderLeft(BorderStyle.THIN);
        headstyle.setBorderRight(BorderStyle.THIN);
        headstyle.setBorderTop(BorderStyle.THIN);
        headstyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headstyle.setFillForegroundColor((short) 17);
        headstyle.setLocked(true);
        headstyle.setWrapText(true);
        return headstyle;
    }

    private HSSFCellStyle rateStyle(HSSFWorkbook workbook) {
        HSSFCellStyle rateStyle = workbook.createCellStyle();
        rateStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0.00%"));
        return rateStyle;
    }

    @SuppressWarnings("unused")
    private HSSFCellStyle dataStyle(HSSFWorkbook workbook) {
        HSSFCellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        return dataStyle;
    }

    public static String getCellName(int row, int col) {
        String result = "";
        row++;
        int base = 65;
        if (col < 26) {
            result = (char) (base + col) + "" + row;
        } else {
            int first = col / 25;
            int second = col % 25;
            result = (char) (base + first) + "" + (char) (base + second) + "" + row;
        }
        return result;
    }
}
