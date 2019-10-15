Fpackage cn.com.easyerp.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.*;
import org.apache.poi.util.*;
import org.hibernate.type.ImageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.zxing.*;
import com.google.zxing.qrcode.decoder.*;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.chart.ReportModel;
import cn.com.easyerp.core.api.ApiDescribe;
import cn.com.easyerp.core.api.ApiService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.ChartDao;
import cn.com.easyerp.core.data.DataMap;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.filter.FilterDescribe;
import cn.com.easyerp.core.filter.FilterRequestModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;
import net.glxn.qrgen.core.image.*;
import net.glxn.qrgen.javase.*;

@Service("defaultReportService")
public class ReportServiceDefault<T> implements ReportService<T>
{
	private static final DateFormat REPORT_FILE_TIMESTAMP = new SimpleDateFormat("yyMMddHHmmss");
	  private static final String REPORT_CELL_DATE_FORMAT = "yyyy-MM-dd";
	  private static final Pattern PRINT_AREA_PATTERN = Pattern.compile(".*!(\\$[A-Z]+\\$[0-9]+:\\$[A-Z]+\\$)([0-9]+)");
	  private static final Pattern QR_TAG_PATTERN = Pattern.compile("qr(:([\\d]+)x([\\d]+))?");
	  private static final Pattern QR_TAG_ATTR_PATTERN = Pattern.compile("([\\d]+)x([\\d]+)");
	  private static final Pattern CELL_RANGE_PATTERN = Pattern.compile("([0-9]+)");
	  private static final Pattern TABLE_CELL_PATTERN = Pattern.compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.<>]+)?\\}$");
	  
	  private static final int TABLE_CELL_TAG_IDX = 2;
	  private static final int TABLE_CELL_ATTR_IDX = 4;
	  private static final int TABLE_CELL_MARK_IDX = 5;
	  private static final int TABLE_CELL_ID_IDX = 6;
	  private static final Pattern SUB_TABLE_CELL_PATTERN = Pattern.compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.]+)\\.([^.]+)\\}$");
	  
    private static final int SUB_TABLE_CELL_TAG_IDX = 2;
    private static final int SUB_TABLE_CELL_ATTR_IDX = 4;
    private static final int SUB_TABLE_CELL_MARK_IDX = 5;
    private static final int SUB_TABLE_CELL_TABLE_IDX = 6;
    private static final int SUB_TABLE_CELL_COLUMN_IDX = 7;
    private static final int REPORT_TYPE_EXCEL = 0;
    private static final int REPORT_TYPE_PDF = 1;
    private static final int REPORT_TYPE_WORD = 2;
    private static final Map<String, Integer> PICTURE_TYPE_MAP;
    private static TypeReference<ReportPageConfigModel> reportConfigJsonRef = new TypeReference<ReportPageConfigModel>() {};
    @Autowired
    private DataService dataService;
    @Autowired
    private StorageService storageService;
    @Autowired
    private ApiService apiService;
    @Autowired
    private ChartDao chartDao;
    @Autowired
    private WordTemplateService wordTemplateService;
    
    static  { PICTURE_TYPE_MAP = new HashMap<>();
	    PICTURE_TYPE_MAP.put("jpg", Integer.valueOf(5));
	    PICTURE_TYPE_MAP.put("emf", Integer.valueOf(2));
	    PICTURE_TYPE_MAP.put("dib", Integer.valueOf(7));
	    PICTURE_TYPE_MAP.put("pict", Integer.valueOf(4));
	    PICTURE_TYPE_MAP.put("png", Integer.valueOf(6));
	    PICTURE_TYPE_MAP.put("wmf", Integer.valueOf(3)); }

    @Override
    public void init(final ReportModel<T> report) {
    }
    
    @Override
    public ReportFormModel form(final String parent, final String report_id, final String tableName, final String title, final ReportFormRequestModel data) {
        return new ReportFormModel(parent, report_id, tableName, title);
    }
    
    private void callApi(final ApiDescribe api, final List<DatabaseDataMap> data, final String uuid) {
        for (final DatabaseDataMap dd : data) {
            this.apiService.exec(api, (DataMap)this.apiService.buildRawApiParam(api, (Map)dd), uuid);
        }
    }
    
    @Override
    public ActionResult print(final ReportModel<T> report, String where, final AuthDetails user, final ReportFormRequestModel request) throws IOException {
        final String filter = "${filter}";
        Map<String, String> sqlMap = new HashMap<String, String>();
        String SQL = null;
        switch (report.getReport_disp_type()) {
            case 0: {
                if (Common.isBlank(where)) {
                    SQL = report.getSql().replace(filter, report.getTable_id());
                    break;
                }
                final String sqlString = "(select * from " + report.getTable_id() + " where " + where + ")";
                SQL = report.getSql().replace(filter, sqlString);
                break;
            }
            case 1:
            case 2: {
                sqlMap = (Map<String, String>)Common.stringMapJson(report.getSql());
                if (Common.isBlank(where)) {
                    SQL = sqlMap.get(report.getTable_id()).replace(filter, report.getTable_id());
                    break;
                }
                if ("true".equals(sqlMap.get("procedure"))) {
                    SQL = sqlMap.get(report.getTable_id());
                    where = "'" + where.replaceAll("'", "''") + "'";
                    break;
                }
                final String sqlString = "(select * from " + report.getTable_id() + " where " + where + ")";
                SQL = sqlMap.get(report.getTable_id()).replace(filter, sqlString);
                break;
            }
            default: {
                throw new ApplicationException("not supported");
            }
        }
        final TableDescribe table = this.dataService.getTableDesc(report.getTable_id());
        ApiDescribe api = report.getPre_api();
        String uuid = null;
        List<DatabaseDataMap> data = null;
        if (api != null) {
            data = this.chartDao.selectEffectedRecord(table, where);
            uuid = this.apiService.genUuid();
            this.callApi(api, data, uuid);
        }
        final List<Map<String, Object>> resultList = this.dataService.dynamicQueryList(SQL, request.getFilterParam());
        if (null == resultList || 0 == resultList.size()) {
            throw new ApplicationException(this.dataService.getMessageText("No_Data_Now", new Object[0]));
        }
        final File template = this.storageService.template(report.getId(), report.getFile_name(), user);
        if (!template.exists()) {
            throw new ApplicationException("UploadFile_NotFound: \"" + template.getAbsolutePath() + "\"");
        }
        final FileInputStream templateStream = new FileInputStream(template);
        String filename = report.getFile_name().substring(0, report.getFile_name().length() - 4);
        String tempName = "";
        File file = null;
        OutputStream os = null;
        Label_0874: {
            switch (report.getReport_disp_type()) {
                case 0:
                case 1: {
                    file = this.storageService.tmp(filename, ".xls");
                    tempName = file.getName();
                    os = new FileOutputStream(file);
                    this.execlWrite(sqlMap, resultList, where, templateStream, os, request.getFilterParam());
                    break;
                }
                case 2: {
                    final ReportGeneratorCache cache = new ReportGeneratorCache();
                    cache.sqlMap = sqlMap;
                    cache.where = where;
                    cache.config = (ReportPageConfigModel)Common.fromJson(report.getSql2(), (TypeReference)ReportServiceDefault.reportConfigJsonRef);
                    switch (report.getReport_file_type()) {
                        case 0:
                        case 1: {
                            file = this.storageService.tmp(filename, ".xls");
                            os = new FileOutputStream(file);
                            this.generateExcel(cache, resultList, templateStream, os, request.getFilterParam());
                            break Label_0874;
                        }
                        case 2: {
                            if (resultList.size() == 1) {
                                file = this.storageService.tmp(filename, ".doc");
                                os = new FileOutputStream(file);
                                this.wordTemplateService.process(cache, resultList.get(0), templateStream, os);
                                break Label_0874;
                            }
                            file = this.storageService.tmp(filename, ".zip");
                            os = new ZipOutputStream(new FileOutputStream(file));
                            this.wordTemplateService.process(cache, resultList, template, (ZipOutputStream)os);
                            break Label_0874;
                        }
                        default: {
                            throw new ApplicationException("unsupported report file type: " + report.getReport_file_type());
                        }
                    }
                }
                default: {
                    throw new ApplicationException("unkonwn report display type");
                }
            }
        }
        os.flush();
        os.close();
        FileInputStream fileInputStream = null;
        int fileSize = 0;
        switch (report.getReport_file_type()) {
            case 0: {
                fileInputStream = new FileInputStream(file.getAbsolutePath());
                fileSize = fileInputStream.available();
                filename = this.makeReportFileName(report.getFile_name(), "xls");
                break;
            }
            case 1: {
                final File pdf = this.storageService.convertXlsToPdf(file);
                fileInputStream = new FileInputStream(pdf);
                fileSize = fileInputStream.available();
                filename = this.makeReportFileName(report.getFile_name(), "pdf");
                break;
            }
            case 2: {
                fileInputStream = new FileInputStream(file.getAbsolutePath());
                fileSize = fileInputStream.available();
                filename = this.makeReportFileName(report.getFile_name(), (resultList.size() == 1) ? "doc" : "zip");
                break;
            }
            default: {
                throw new ApplicationException("not supported");
            }
        }
        final ActionResult result = this.storageService.createDownload(filename, this.storageService.getTmpFilePath(tempName), (InputStream)fileInputStream, fileSize);
        api = report.getApi();
        if (api != null) {
            if (uuid == null) {
                uuid = this.apiService.genUuid();
                data = this.chartDao.selectEffectedRecord(table, where);
            }
            this.callApi(api, data, uuid);
        }
        return result;
    }
    
    private void execlWrite(final Map<String, String> sqlMap, final List<Map<String, Object>> resultList, final String where, final FileInputStream fileInput, final OutputStream os, final Map<String, Object> filterParam) throws IOException {
        final HSSFWorkbook wb = new HSSFWorkbook((InputStream)fileInput);
        final FormulaEvaluator evaluator = (FormulaEvaluator)wb.getCreationHelper().createFormulaEvaluator();
        final HSSFSheet sheet = wb.getSheetAt(0);
        final int numMergedRegions = sheet.getNumMergedRegions();
        final CreationHelper createHelper = (CreationHelper)wb.getCreationHelper();
        final short cellDateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd");
        int flg = 0;
        final List<Integer> pageStartRow = new ArrayList<Integer>();
        final List<Integer> pageEndRow = new ArrayList<Integer>();
        final String pageArea = wb.getPrintArea(0);
        if (pageArea == null) {
            throw new ApplicationException("no print area defined in excel.");
        }
        final String pageRows = pageArea.substring(pageArea.lastIndexOf("$") + 1);
        final List<Integer> pageSize = new ArrayList<Integer>();
        final List<Integer> pageSizeBreak = new ArrayList<Integer>();
        final int rownum = Integer.parseInt(pageRows);
        int insertRow = 0;
        int rowst = 0;
        int rowen = 0;
        if (!"".equals(pageArea) && null != pageArea) {
            for (int k = 0; k < resultList.size() - 1; ++k) {
                final int rownumstar = rownum * (k + 1);
                sheet.shiftRows(rownumstar, sheet.getLastRowNum() + rownum, rownum, true, false);
                for (int i = 0; i < rownum; ++i) {
                    final HSSFRow row = sheet.getRow(i);
                    if (null != row) {
                        final int cols = row.getLastCellNum();
                        final HSSFRow newRow = sheet.createRow(rownumstar + i);
                        newRow.setHeight(row.getHeight());
                        for (int j = 0; j < cols; ++j) {
                            final HSSFCell cell = row.getCell(j);
                            final HSSFCell newCell = newRow.createCell(j);
                            if (null != cell) {
                                newCell.setCellType(cell.getCellType());
                                newCell.setCellStyle(cell.getCellStyle());
                                if (1 == cell.getCellType()) {
                                    newCell.setCellValue(cell.getStringCellValue());
                                }
                                else if (2 == cell.getCellType()) {
                                    final String cellF = cell.getCellFormula();
                                    if (cellF.startsWith("SUM")) {
                                        final Matcher matcher = ReportServiceDefault.CELL_RANGE_PATTERN.matcher(cellF);
                                        final StringBuffer sb = new StringBuffer();
                                        while (matcher.find()) {
                                            final int idx = Integer.valueOf(matcher.group(1));
                                            matcher.appendReplacement(sb, String.valueOf(idx + rownumstar));
                                        }
                                        matcher.appendTail(sb);
                                        newCell.setCellFormula(sb.toString());
                                    }
                                    else {
                                        newCell.setCellFormula(cell.getCellFormula());
                                    }
                                }
                            }
                        }
                    }
                }
                for (int l = 0; l < numMergedRegions; ++l) {
                    final CellRangeAddress cra = sheet.getMergedRegion(l);
                    final CellRangeAddress newCellRangeAddress = new CellRangeAddress(cra.getFirstRow() + rownumstar, cra.getLastRow() + rownumstar, cra.getFirstColumn(), cra.getLastColumn());
                    sheet.addMergedRegion(newCellRangeAddress);
                }
            }
        }
        for (final Map<String, Object> map : resultList) {
            if (Common.isBlank(where)) {
                map.put("filter", "1 = 1");
            }
            else {
                map.put("filter", where);
            }
            if (filterParam != null) {
                map.putAll(filterParam);
            }
            rowst = 0;
            rowen = 0;
            final int start = flg;
            pageStartRow.add(start);
            final int end = rownum + start;
            int rowPage = 0;
            for (int n = start; n <= end; ++n) {
                final HSSFRow row = sheet.getRow(n);
                if (null != row) {
                    for (int cols = row.getLastCellNum(), m = 0; m < cols; ++m) {
                        final HSSFCell cell = row.getCell(m);
                        if (null != cell && 1 == cell.getCellType()) {
                            if ("rowstart".equals(cell.getRichStringCellValue().toString())) {
                                rowst = n;
                                break;
                            }
                            if ("rowend".equals(cell.getRichStringCellValue().toString())) {
                                rowen = n;
                                break;
                            }
                        }
                    }
                }
                if (rowst != 0 && rowen != 0) {
                    break;
                }
            }
            int detailRowNum = 1;
            if (rowen != 0) {
                detailRowNum = rowen - rowst + 1;
            }
            for (int i2 = start; i2 <= end; ++i2) {
                HSSFRow row = sheet.getRow(i2);
                if (null != row) {
                    for (int cols = row.getLastCellNum(), j2 = 0; j2 < cols; ++j2) {
                        HSSFCell cell = row.getCell(j2);
                        if (null != cell && 1 == cell.getCellType()) {
                            final String cellValue = cell.getStringCellValue();
                            if (cellValue.contains("${") && cellValue.endsWith("}") && cellValue.contains(".")) {
                                String subTable = cellValue.substring(cellValue.lastIndexOf("{") + 1, cellValue.lastIndexOf("."));
                                if (subTable.indexOf("<qr") == 0) {
                                    subTable = subTable.substring(subTable.indexOf(62) + 1);
                                }
                                String subSql = sqlMap.get(subTable);
                                subSql = subSql.replaceAll("\\$\\{filter}", (String) map.get("filter"));
                                final List<Map<String, Object>> subresultList = this.dataService.dynamicQueryList(subSql, map);
                                pageSize.add(subresultList.size());
                                if (null != subresultList && 0 != subresultList.size()) {
                                    if (rowst + detailRowNum <= sheet.getLastRowNum()) {
                                        sheet.shiftRows(rowst + detailRowNum, sheet.getLastRowNum(), (subresultList.size() - 1) * detailRowNum, true, false);
                                    }
                                    insertRow = (subresultList.size() - 1) * detailRowNum;
                                    for (int k2 = 1; k2 < subresultList.size(); ++k2) {
                                        for (int detailrow = 0; detailrow < detailRowNum; ++detailrow) {
                                            row = sheet.getRow(rowst + detailrow);
                                            cols = row.getLastCellNum();
                                            final HSSFRow newRow2 = sheet.createRow(rowst + k2 * detailRowNum + detailrow);
                                            for (int m2 = 0; m2 < cols; ++m2) {
                                                cell = row.getCell(m2);
                                                if (cell != null) {
                                                    final HSSFCell newCell2 = newRow2.createCell(m2);
                                                    newCell2.setCellType(cell.getCellType());
                                                    newCell2.setCellStyle(cell.getCellStyle());
                                                    if (1 == cell.getCellType()) {
                                                        newCell2.setCellValue(cell.getStringCellValue());
                                                    }
                                                    else if (2 == cell.getCellType()) {
                                                        newCell2.setCellFormula(cell.getCellFormula());
                                                    }
                                                }
                                            }
                                        }
                                        final int detailst = rowst - start;
                                        final int detailen = detailst + detailRowNum;
                                        for (int idx2 = 0; idx2 < numMergedRegions; ++idx2) {
                                            final CellRangeAddress cra2 = sheet.getMergedRegion(idx2);
                                            if (cra2.getFirstRow() >= detailst && cra2.getFirstRow() <= detailen) {
                                                final CellRangeAddress newCellRangeAddress2 = new CellRangeAddress(cra2.getFirstRow() + start + k2 * detailRowNum, cra2.getLastRow() + start + k2 * detailRowNum, cra2.getFirstColumn(), cra2.getLastColumn());
                                                sheet.addMergedRegion(newCellRangeAddress2);
                                            }
                                        }
                                    }
                                }
                                else {
                                    insertRow = 0;
                                    for (int nucols = row.getLastCellNum(), z = 0; z < nucols; ++z) {
                                        cell = row.getCell(z);
                                        cell.setCellValue("");
                                    }
                                }
                                rowPage = 1;
                            }
                        }
                        if (rowPage == 1) {
                            break;
                        }
                    }
                }
                if (rowPage == 1) {
                    break;
                }
            }
            if (rowPage == 0) {
                pageSize.add(0);
            }
            flg = end + insertRow;
            pageEndRow.add(flg - 1);
        }
        int subindex = 0;
        int first = 0;
        final Set<Short> hiddenColumn = new HashSet<Short>();
        for (int y = 0; y < resultList.size(); ++y) {
            final Map<String, ReportGroupModel> groupMap = new HashMap<String, ReportGroupModel>();
            final Map<String, Object> map2 = resultList.get(y);
            if (filterParam != null) {
                map2.putAll(filterParam);
            }
            final int start = pageStartRow.get(y);
            for (int end = pageEndRow.get(y), i3 = start; i3 <= end; ++i3) {
                final HSSFRow row = sheet.getRow(i3);
                if (null != row) {
                    for (int cols = row.getLastCellNum(), j3 = 0; j3 < cols; ++j3) {
                        final HSSFCell cell = row.getCell(j3);
                        if (null != cell) {
                            if (1 == cell.getCellType()) {
                                if ("rowstart".equals(cell.getRichStringCellValue().toString())) {
                                    if (first < 1) {
                                        subindex = 0;
                                        ++first;
                                    }
                                    else {
                                        ++subindex;
                                    }
                                }
                                final String cellValue2 = cell.getStringCellValue();
                                if (cellValue2.startsWith("${") && cellValue2.endsWith("}") && !cellValue2.contains(".")) {
                                    String hostValue = cellValue2.substring(cellValue2.lastIndexOf("{") + 1, cellValue2.lastIndexOf("}"));
                                    if (hostValue.contains("<image>")) {
                                        hostValue = hostValue.substring(hostValue.lastIndexOf("<image>") + 7);
                                        final Object valObject = map2.get(hostValue);
                                        final String file = this.storageService.absolutePath(Common.string(valObject));
                                        final File fileName = new File(file);
                                        if (fileName.exists() && fileName.isFile()) {
                                            String picturetype = "";
                                            if (file.contains(".")) {
                                                picturetype = file.substring(file.lastIndexOf(".") + 1);
                                            }
                                            int picture_type = 0;
                                            if (picturetype.toLowerCase().equals("jpg")) {
                                                picture_type = 5;
                                            }
                                            else if (picturetype.toLowerCase().equals("emf")) {
                                                picture_type = 2;
                                            }
                                            else if (picturetype.toLowerCase().equals("dib")) {
                                                picture_type = 7;
                                            }
                                            else if (picturetype.toLowerCase().equals("pict")) {
                                                picture_type = 4;
                                            }
                                            else if (picturetype.toLowerCase().equals("png")) {
                                                picture_type = 6;
                                            }
                                            else if (picturetype.toLowerCase().equals("wmf")) {
                                                picture_type = 3;
                                            }
                                            final InputStream in = new FileInputStream(fileName);
                                            final byte[] bytes = IOUtils.toByteArray(in);
                                            final int pictureIdx = wb.addPicture(bytes, picture_type);
                                            final CreationHelper helper = (CreationHelper)wb.getCreationHelper();
                                            final Drawing drawing = (Drawing)sheet.createDrawingPatriarch();
                                            final ClientAnchor anchor = helper.createClientAnchor();
                                            anchor.setRow1(i3);
                                            anchor.setCol1(j3);
                                            final Picture pict = drawing.createPicture(anchor, pictureIdx);
                                            pict.resize();
                                        }
                                        cell.setCellValue("");
                                    }
                                    else if (hostValue.indexOf("<qr") == 0) {
                                        final int pos = hostValue.indexOf(46);
                                        String fieldName;
                                        if (pos > 0) {
                                            fieldName = hostValue.substring(pos + 1);
                                        }
                                        else {
                                            fieldName = hostValue.substring(hostValue.indexOf(">") + 1);
                                        }
                                        this.setQrImg(sheet, i3, j3, cell, hostValue, map2.get(fieldName).toString());
                                    }
                                    else {
                                        this.setCellData((Cell)cell, map2.get(hostValue), cellDateFormat);
                                    }
                                }
                                else if (cellValue2.contains("${") && cellValue2.endsWith("}") && cellValue2.contains(".")) {
                                    final Pattern pattern = Pattern.compile("\\$\\{biz\\.([^\\.]*)\\.([^\\}.]*)\\}");
                                    final Matcher matcher2 = pattern.matcher(cellValue2);
                                    if (matcher2.find()) {
                                        final String tableName = matcher2.group(1);
                                        final String columnName = matcher2.group(2);
                                        cell.setCellValue(this.dataService.getBizParam(tableName, columnName).toString());
                                    }
                                    else {
                                        final ReportGroupModel cellModel = new ReportGroupModel();
                                        final String subValue = cellValue2.substring(cellValue2.lastIndexOf(".") + 1, cellValue2.lastIndexOf("}"));
                                        String subTable2 = cellValue2.substring(cellValue2.lastIndexOf("{") + 1, cellValue2.lastIndexOf("."));
                                        String qrText = null;
                                        if (subTable2.indexOf("<qr") == 0) {
                                            qrText = subTable2;
                                            subTable2 = subTable2.substring(subTable2.indexOf(62) + 1);
                                        }
                                        String subSql2 = sqlMap.get(subTable2);
                                        subSql2 = subSql2.replaceAll("\\$\\{filter}", (String) map2.get("filter"));
                                        final List<Map<String, Object>> subresultList = this.dataService.dynamicQueryList(subSql2, map2);
                                        if (null != subresultList && 0 != subresultList.size()) {
                                            final Object valObject2 = subresultList.get(subindex).get(subValue);
                                            if (!subresultList.get(subindex).containsKey(subValue)) {
                                                hiddenColumn.add((short)j3);
                                            }
                                            if (qrText == null || valObject2 == null) {
                                                this.setCellData((Cell)cell, valObject2, cellDateFormat);
                                            }
                                            else {
                                                this.setQrImg(sheet, i3, j3, cell, qrText, valObject2.toString());
                                            }
                                            if (cellValue2.contains("<group>")) {
                                                final String key = subValue + valObject2;
                                                if (groupMap.containsKey(key)) {
                                                    groupMap.get(key).setEndrow(i3);
                                                    cell.setCellValue("");
                                                }
                                                else {
                                                    cellModel.setCol(j3);
                                                    cellModel.setStartrow(i3);
                                                    cellModel.setEndrow(i3);
                                                    groupMap.put(key, cellModel);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            else if (2 == cell.getCellType()) {
                                evaluator.evaluateFormulaCell((Cell)cell);
                            }
                        }
                    }
                }
            }
            subindex = 0;
            first = 0;
            for (final String key2 : groupMap.keySet()) {
                final ReportGroupModel groupVal = groupMap.get(key2);
                sheet.addMergedRegion(new CellRangeAddress(groupVal.getStartrow(), groupVal.getEndrow(), groupVal.getCol(), groupVal.getCol()));
            }
        }
        int detailRowNum = 1;
        if (rowen != 0) {
            detailRowNum = rowen - rowst + 1;
        }
        int insrow = 0;
        int psize = 1;
        for (final int pz : pageSize) {
            if (psize == 1) {
                if (pz > 1) {
                    insrow = (pz - 1) * detailRowNum + rownum - 1;
                    pageSizeBreak.add(insrow);
                }
                else {
                    insrow = rownum - 1;
                    pageSizeBreak.add(insrow);
                }
                psize = 0;
            }
            else if (pz > 1) {
                insrow = insrow + rownum + (pz - 1) * detailRowNum;
                pageSizeBreak.add(insrow);
            }
            else {
                insrow += rownum;
                pageSizeBreak.add(insrow);
            }
        }
        for (int i4 = 0; i4 < numMergedRegions; ++i4) {
            final CellRangeAddress cra3 = sheet.getMergedRegion(i4);
            final HSSFRow row = sheet.getRow(cra3.getFirstRow());
            final HSSFCell cell = row.getCell(0);
            if (null != cell && 1 == cell.getCellType() && !"rowhead".equals(cell.getRichStringCellValue().toString()) && hiddenColumn.contains((short)cra3.getFirstColumn())) {
                for (int j4 = cra3.getFirstColumn() + 1; j4 <= cra3.getLastColumn(); ++j4) {
                    hiddenColumn.add((short)j4);
                }
            }
        }
        hiddenColumn.add((short) 0);
        for (final Short column : hiddenColumn) {
            sheet.setColumnHidden((int)column, true);
        }
        final String lastrow = pageArea.substring(pageArea.lastIndexOf("!") + 1, pageArea.lastIndexOf("$") + 1) + String.valueOf(insrow + 1);
        wb.setPrintArea(0, lastrow);
        for (final int k2 : pageSizeBreak) {
            sheet.setRowBreak(k2);
        }
        wb.write(os);
    }
    
    private void setCellData(final Cell cell, final Object val, final short cellDateFormat) {
        if (val == null) {
            cell.setCellValue("");
            return;
        }
        if (String.class.isInstance(val)) {
            cell.setCellValue((String)val);
        }
        else if (Integer.class.isInstance(val)) {
            cell.setCellValue((double)(int)val);
        }
        else if (Long.class.isInstance(val)) {
            cell.setCellValue((double)(long)val);
        }
        else if (Double.class.isInstance(val)) {
            cell.setCellValue((double)val);
        }
        else if (Date.class.isInstance(val)) {
            cell.getCellStyle().setDataFormat(cellDateFormat);
            cell.setCellValue((Date)val);
        }
        else {
            cell.setCellValue(val.toString());
        }
    }
    
    private void setQrImg(final Sheet sheet, final int row, final int col, final Cell cell, final QRImageInfo info) {
        final String code = Common.rightPad(info.code, 50, ' ');
        if (code != null && !"".equals(code.trim())) {
            final byte[] img = QRCode.from(code).withHint(EncodeHintType.MARGIN, (Object)0).withErrorCorrection(ErrorCorrectionLevel.Q).withSize(info.w, info.h).to(ImageType.PNG).stream().toByteArray();
            this.setImage(sheet, row, col, 6, img);
            cell.setCellValue("");
        }
        else {
            cell.setCellValue("");
        }
    }
    
    private void setQrImg(final HSSFSheet sheet, final int row, final int col, final HSSFCell cell, final String text, final String qrCode) {
        final int codePos = text.indexOf(62);
        int w = 32;
        int h = 32;
        final String tag = text.substring(1, codePos);
        final Matcher matcher = ReportServiceDefault.QR_TAG_PATTERN.matcher(tag);
        if (!matcher.find()) {
            throw new ApplicationException("incorrect qr tag: " + tag);
        }
        if (matcher.group(2) != null) {
            w = Integer.valueOf(matcher.group(2));
        }
        if (matcher.group(3) != null) {
            h = Integer.valueOf(matcher.group(3));
        }
        this.setQrImg((Sheet)sheet, row, col, (Cell)cell, new QRImageInfo(w, h, qrCode));
    }
    
    private void setImage(final Sheet sheet, final int row, final int col, final int type, final byte[] img) {
        final Workbook book = sheet.getWorkbook();
        final int pictureIdx = book.addPicture(img, type);
        final CreationHelper helper = book.getCreationHelper();
        final Drawing drawing = sheet.createDrawingPatriarch();
        final ClientAnchor anchor = helper.createClientAnchor();
        anchor.setRow1(row);
        anchor.setCol1(col);
        final Picture pict = drawing.createPicture(anchor, pictureIdx);
        pict.resize();
    }
    
    private String makeReportFileName(final String name, final String ext) {
        final int pos = name.lastIndexOf(46);
        return name.substring(0, pos) + '(' + ReportServiceDefault.REPORT_FILE_TIMESTAMP.format(new Date()) + ")." + ext;
    }
    
    private void generateExcel(final ReportGeneratorCache cache, final List<Map<String, Object>> list, final InputStream is, final OutputStream os, final Map<String, Object> filterParam) throws IOException {
        final Workbook wb = (Workbook)new HSSFWorkbook(is);
        final Sheet sheet = wb.getSheetAt(0);
        final CreationHelper createHelper = wb.getCreationHelper();
        final short cellDateFormat = createHelper.createDataFormat().getFormat("yyyy-MM-dd");
        final FormulaEvaluator evaluator = wb.getCreationHelper().createFormulaEvaluator();
        cache.cellDateFormat = cellDateFormat;
        cache.evaluator = evaluator;
        cache.wb = wb;
        cache.sheet = sheet;
        final String pageArea = wb.getPrintArea(0);
        if (pageArea == null) {
            throw new ApplicationException("no print area defined in excel.");
        }
        final Matcher matcher = ReportServiceDefault.PRINT_AREA_PATTERN.matcher(pageArea);
        if (!matcher.find()) {
            throw new ApplicationException("incorrect print area defined in excel.");
        }
        final int int1 = Integer.parseInt(matcher.group(2));
        cache.templateRows = int1;
        final int end = int1;
        final int mergedRegions = sheet.getNumMergedRegions();
        cache.ranges = new ArrayList<CellRangeAddress>(mergedRegions);
        for (int i = 0; i < mergedRegions; ++i) {
            final CellRangeAddress cra = sheet.getMergedRegion(i);
            if (cra.getFirstRow() < end) {
                cache.ranges.add(cra);
            }
        }
        final List<PagePrintingInfo> setupPages = this.setupPages(cache, list, end, filterParam);
        cache.ppis = setupPages;
        final List<PagePrintingInfo> ppis = setupPages;
        for (int j = 0; j < ppis.size(); ++j) {
            final PagePrintingInfo ppi = ppis.get(j);
            cache.currentPage = j;
            this.fillPage(cache, ppi);
        }
        int totalRows = 0;
        for (int k = 0; k < ppis.size(); ++k) {
            final PagePrintingInfo ppi2 = ppis.get(k);
            if (k > 0) {
                sheet.setRowBreak(ppi2.pageStart - 1);
            }
            for (final int bp : ppi2.breaks) {
                sheet.setRowBreak(bp - 1);
            }
            totalRows += ppi2.pageRows;
        }
        wb.setPrintArea(0, matcher.group(1) + totalRows);
        wb.write(os);
    }
    
    private int insertHead(final ReportGeneratorCache cache, final PagePrintingInfo ppi, final int start, final List<CellRangeAddress> rangeToMerge) {
        int count = 0;
        for (final int r : cache.headRows) {
            final Row row = cache.sheet.getRow(ppi.pageStart + r);
            if (row == null) {
                continue;
            }
            this.copyRow(cache, row, start + count, ppi.pageStart, rangeToMerge);
            ++count;
        }
        ppi.breaks.add(start);
        return start + count;
    }
    
    private void clearRow(final Row row) {
        for (int i = 0; i < row.getLastCellNum(); ++i) {
            row.getCell(i).setCellValue("");
        }
    }
    
    private void setupFoot(final ReportGeneratorCache cache, final PagePrintingInfo ppi) {
        final int footStart = ppi.pageStart + ppi.pageRows - cache.footRows;
        final int templateFootStart = cache.templateRows - cache.footRows;
        for (int i = 0; i < cache.footRows; ++i) {
            for (final CellRangeAddress cellRangeAddress : cache.ranges) {
                if (cellRangeAddress.getFirstRow() == i + templateFootStart) {
                    final CellRangeAddress newCellRangeAddress = new CellRangeAddress(footStart + i, footStart + i + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                    cache.sheet.addMergedRegion(newCellRangeAddress);
                }
            }
        }
    }
    
    private int setupDetail(final ReportGeneratorCache cache, final PagePrintingInfo ppi) {
        final int sourceStart = ppi.pageStart + cache.detailStart;
        final List<CellRangeAddress> rangeToMerge = new ArrayList<CellRangeAddress>();
        final Sheet sheet = cache.sheet;
        final int headSize = cache.headRows.size();
        int lines1 = 0;
        int lines2 = 0;
        if (headSize > 0) {
            if (cache.config == null || cache.config.getDetail() == null) {
                throw new ApplicationException("head defined, but no detail lines defined");
            }
            lines1 = cache.config.getDetail().getLines1();
            lines2 = cache.config.getDetail().getLines2();
        }
        int nextStart = sourceStart + cache.detailRows;
        final int rows = ppi.details.size();
        for (int i = 1; i < rows; ++i) {
            if (headSize > 0) {
                if (i == lines1) {
                    nextStart = this.insertHead(cache, ppi, nextStart, rangeToMerge);
                }
                else if ((i - lines1) % lines2 == 0) {
                    nextStart = this.insertHead(cache, ppi, nextStart, rangeToMerge);
                }
            }
            for (int j = 0; j < cache.detailRows; ++j) {
                this.copyRow(cache, sheet.getRow(sourceStart + j), nextStart + j, ppi.pageStart, rangeToMerge);
            }
            nextStart += cache.detailRows;
        }
        if (headSize > 0 && cache.footRows > 0 && rows > lines1 && (rows - lines1) % lines2 == 0) {
            nextStart = this.insertHead(cache, ppi, nextStart, rangeToMerge);
        }
        for (final CellRangeAddress cra : rangeToMerge) {
            sheet.addMergedRegion(cra);
        }
        return nextStart - sourceStart - cache.detailRows;
    }
    
    private void fillPage(final ReportGeneratorCache cache, final PagePrintingInfo ppi) {
        for (int i = ppi.pageStart; i < ppi.pageStart + ppi.pageRows; ++i) {
            final Row row = cache.sheet.getRow(i);
            if (row != null) {
                this.fillRow(cache, row, ppi);
            }
        }
        for (final Map.Entry<String, DetailGroup> entry : ppi.groups.entrySet()) {
            final DetailGroup g = entry.getValue();
            final CellRangeAddress cra = new CellRangeAddress(g.start, g.end, g.col, g.col);
            cache.sheet.addMergedRegion(cra);
        }
    }
    
    private void fillRow(final ReportGeneratorCache cache, final Row row, final PagePrintingInfo ppi) {
        Cell cell = row.getCell(0);
        if (cell != null && cell.getCellType() == 1 && "rowstart".equals(cell.getStringCellValue())) {
            ++ppi.detailIndex;
        }
        for (int i = 1; i < row.getLastCellNum(); ++i) {
            cell = row.getCell(i);
            if (cell != null) {
                switch (cell.getCellType()) {
                    case 1: {
                        this.fillCell(cache, cell, ppi);
                        break;
                    }
                    case 2: {
                        cache.evaluator.evaluateFormulaCell(cell);
                        break;
                    }
                }
            }
        }
    }
    
    private void fillCell(final ReportGeneratorCache cache, final Cell cell, final PagePrintingInfo ppi) {
        final String varStr = cell.getStringCellValue();
        Matcher matcher = ReportServiceDefault.TABLE_CELL_PATTERN.matcher(varStr);
        if (matcher.find()) {
            final String tag = (matcher.group(2) == null) ? "std" : matcher.group(2);
            final char mark = (matcher.group(5) == null) ? '\0' : matcher.group(5).charAt(0);
            final String attr = matcher.group(4);
            final String var = matcher.group(6);
            this.setTableValue(cache, cell, tag, mark, attr, var, ppi);
            return;
        }
        matcher = ReportServiceDefault.SUB_TABLE_CELL_PATTERN.matcher(varStr);
        if (matcher.find()) {
            final String tag = (matcher.group(2) == null) ? "std" : matcher.group(2);
            final char mark = (matcher.group(5) == null) ? '\0' : matcher.group(5).charAt(0);
            final String attr = matcher.group(4);
            final String table = matcher.group(6);
            final String column = matcher.group(7);
            this.setSubTableValue(cache, cell, tag, mark, attr, table, column, ppi);
        }
    }
    
    private QRImageInfo buildQRImageInfo(final String attr, final String code) {
        int w = 32;
        int h = 32;
        if (attr != null) {
            final Matcher matcher = ReportServiceDefault.QR_TAG_ATTR_PATTERN.matcher(attr);
            if (matcher.find()) {
                w = Integer.parseInt(matcher.group(1));
                h = Integer.parseInt(matcher.group(2));
            }
        }
        return new QRImageInfo(w, h, code);
    }
    
    private void setTableValue(final ReportGeneratorCache cache, final Cell cell, final String tag, final char mark, String attr, final String var, final PagePrintingInfo ppi) {
        final Object val = ppi.data.get(var);
        final int row = cell.getRowIndex();
        final int col = cell.getColumnIndex();
        switch (tag) {
            case "std": {
                this.setCellData(cell, val, cache.cellDateFormat);
                break;
            }
            case "image": {
                cell.setCellValue("");
                final String text = (String) ppi.data.get(var);
                final byte[] bytes = this.storageService.loadFileToByteArray(text);
                if (bytes == null) {
                    break;
                }
                final String ext = text.substring(text.lastIndexOf(46) + 1);
                final int type = ReportServiceDefault.PICTURE_TYPE_MAP.get(ext);
                this.setImage(cache.sheet, row, col, type, bytes);
                break;
            }
            case "qr": {
                final String text = (String) ppi.data.get(var);
                this.setQrImg(cache.sheet, row, col, cell, this.buildQRImageInfo(attr, text));
                break;
            }
            case "page": {
                if (attr == null) {
                    attr = "number";
                }
                final String s = attr;
                switch (s) {
                    case "count": {
                        this.setCellData(cell, ppi.breaks.size() + 1, cache.cellDateFormat);
                        break;
                    }
                    default: {
                        int i;
                        for (i = 0; i < ppi.breaks.size(); ++i) {
                            if (row < ppi.breaks.get(i)) {
                                this.setCellData(cell, i + 1, cache.cellDateFormat);
                                break;
                            }
                        }
                        if (i == ppi.breaks.size()) {
                            this.setCellData(cell, i + 1, cache.cellDateFormat);
                            break;
                        }
                        break;
                    }
                }
                break;
            }
            case "row": {
                final int detailIndex = (ppi.detailIndex <= 0) ? 0 : ppi.detailIndex;
                int index;
                if (mark == '+') {
                    index = 0;
                    for (int i = 0; i < cache.currentPage; ++i) {
                        index += cache.ppis.get(i).details.size();
                    }
                    index += detailIndex;
                }
                else {
                    index = detailIndex;
                }
                ++index;
                this.setCellData(cell, index, cache.cellDateFormat);
                break;
            }
        }
    }
    
    private void setSubTableValue(final ReportGeneratorCache cache, final Cell cell, final String tag, final char mark, final String attr, final String table, final String column, final PagePrintingInfo ppi) {
        if (ppi.details.size() == 0) {
            return;
        }
        final int row = cell.getRowIndex();
        final int col = cell.getColumnIndex();
        final int detailIndex = (ppi.detailIndex == -1) ? 0 : ppi.detailIndex;
        switch (tag) {
            case "std": {
                final Object val = ppi.details.get(detailIndex).get(column);
                this.setCellData(cell, val, cache.cellDateFormat);
                break;
            }
            case "biz": {
                cell.setCellValue(this.dataService.getBizParam(table, column).toString());
                break;
            }
            case "qr": {
                final Object val = ppi.details.get(detailIndex).get(column);
                this.setQrImg(cache.sheet, row, col, cell, this.buildQRImageInfo(attr, (String)val));
                break;
            }
            case "group": {
                final Object val = ppi.details.get(detailIndex).get(column);
                DetailGroup group = ppi.groups.get(column);
                if (group == null) {
                    ppi.groups.put(column, group = new DetailGroup(val, col, row));
                }
                group.end = row;
                break;
            }
            case "sum": {
                final DataType type = this.getDataType(ppi.details, column);
                this.setCellData(cell, this.sum(ppi.details, column, type), cache.cellDateFormat);
                break;
            }
        }
    }
    
    private DataType getDataType(final List<Map<String, Object>> list, final String key) {
        for (final Map<String, Object> map : list) {
            final Object obj = map.get(key);
            if (obj == null) {
                continue;
            }
            if (Double.class.isInstance(obj)) {
                return DataType.DOUBLE;
            }
            if (Integer.class.isInstance(obj)) {
                return DataType.INTEGER;
            }
            if (Long.class.isInstance(obj)) {
                map.put(key, ((Long)obj).intValue());
                return DataType.INTEGER;
            }
            throw new ApplicationException("not supported data type");
        }
        return DataType.UNKNOWN;
    }
    
    private Object sum(final List<Map<String, Object>> data, final String key, final DataType type) {
        switch (type) {
            case INTEGER: {
                int i = 0;
                for (final Map<String, Object> map : data) {
                    final Object obj = map.get(key);
                    if (obj != null) {
                        if (Long.class.isInstance(obj)) {
                            i += ((Long)obj).intValue();
                        }
                        else {
                            i += (int)obj;
                        }
                    }
                }
                return i;
            }
            case DOUBLE: {
                double d = 0.0;
                for (final Map<String, Object> map2 : data) {
                    final Object obj = map2.get(key);
                    if (obj != null) {
                        d += (double)obj;
                    }
                }
                return d;
            }
            case UNKNOWN: {
                return "0";
            }
            default: {
                return "N/A";
            }
        }
    }
    
    private void copyRow(final ReportGeneratorCache cache, final Row sourceRow, final int destinationRowNum, final int sourceDeltaFrom, final List<CellRangeAddress> rangeToMerge) {
        final Sheet sheet = cache.sheet;
        Row newRow = sheet.getRow(destinationRowNum);
        if (newRow != null) {
            sheet.shiftRows(destinationRowNum, sheet.getLastRowNum(), 1, true, false);
        }
        newRow = sheet.createRow(destinationRowNum);
        newRow.setHeight(sourceRow.getHeight());
        for (int i = 0; i < sourceRow.getLastCellNum(); ++i) {
            final Cell oldCell = sourceRow.getCell(i);
            final Cell newCell = newRow.createCell(i);
            if (oldCell != null) {
                newCell.setCellStyle(oldCell.getCellStyle());
                if (newCell.getCellComment() != null) {
                    newCell.setCellComment(oldCell.getCellComment());
                }
                if (oldCell.getHyperlink() != null) {
                    newCell.setHyperlink(oldCell.getHyperlink());
                }
                newCell.setCellType(oldCell.getCellType());
                switch (oldCell.getCellType()) {
                    case 3: {
                        newCell.setCellValue(oldCell.getStringCellValue());
                        break;
                    }
                    case 4: {
                        newCell.setCellValue(oldCell.getBooleanCellValue());
                        break;
                    }
                    case 5: {
                        newCell.setCellErrorValue(oldCell.getErrorCellValue());
                        break;
                    }
                    case 2: {
                        newCell.setCellFormula(oldCell.getCellFormula());
                        break;
                    }
                    case 0: {
                        newCell.setCellValue(oldCell.getNumericCellValue());
                        break;
                    }
                    case 1: {
                        newCell.setCellValue(oldCell.getRichStringCellValue());
                        break;
                    }
                }
            }
        }
        if (rangeToMerge == null) {
            return;
        }
        for (final CellRangeAddress cellRangeAddress : cache.ranges) {
            if (cellRangeAddress.getFirstRow() == sourceRow.getRowNum() - sourceDeltaFrom) {
                final int rowNum = newRow.getRowNum();
                final CellRangeAddress newCellRangeAddress = new CellRangeAddress(rowNum, rowNum + (cellRangeAddress.getLastRow() - cellRangeAddress.getFirstRow()), cellRangeAddress.getFirstColumn(), cellRangeAddress.getLastColumn());
                rangeToMerge.add(newCellRangeAddress);
            }
        }
    }
    
    private List<Map<String, Object>> getDetailsData(final ReportGeneratorCache cache, final Map<String, Object> data) {
        String subSql = cache.sqlMap.get(cache.subTable);
        subSql = subSql.replaceAll("\\$\\{filter\\}", Common.isBlank(cache.where) ? "1=1" : cache.where);
        return this.dataService.dynamicQueryList(subSql, data);
    }
    
    private List<Map<String, Object>> setupPageCache(final ReportGeneratorCache cache, final Map<String, Object> data, final int lastRow) {
        final Sheet sheet = cache.sheet;
        cache.headRows = new ArrayList<Integer>();
        List<Map<String, Object>> details = null;
        for (int r = 0; r < lastRow; ++r) {
            final Row row = sheet.getRow(r);
            if (row != null) {
                row.setHeight(row.getHeight());
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == 1) {
                    final String stringCellValue = cell.getStringCellValue();
                    switch (stringCellValue) {
                        case "head": {
                            cache.headRows.add(r);
                            break;
                        }
                        case "rowstart": {
                            cache.detailStart = r;
                            break;
                        }
                        case "rowend": {
                            cache.detailEnd = r;
                            break;
                        }
                    }
                }
                if (cache.subTable == null) {
                    final short lastCellNum = row.getLastCellNum();
                    for (int j = 1; j < lastCellNum; ++j) {
                        cell = row.getCell(j);
                        if (cell != null) {
                            if (cell.getCellType() == 1) {
                                final Matcher matcher = ReportServiceDefault.SUB_TABLE_CELL_PATTERN.matcher(cell.getStringCellValue());
                                if (matcher.find()) {
                                    cache.subTable = matcher.group(6);
                                    details = this.getDetailsData(cache, data);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (cache.detailStart == -1) {
            throw new ApplicationException("no row start defined");
        }
        if (cache.detailEnd == -1) {
            cache.detailEnd = cache.detailStart;
        }
        cache.detailRows = cache.detailEnd - cache.detailStart + 1;
        cache.footRows = lastRow - 1 - cache.detailEnd;
        return details;
    }
    
    private List<PagePrintingInfo> setupPages(final ReportGeneratorCache cache, final List<Map<String, Object>> list, final int lastRow, final Map<String, Object> filterParam) {
        final Map<String, Object> param = list.get(0);
        param.putAll(filterParam);
        List<Map<String, Object>> details = this.setupPageCache(cache, param, lastRow);
        final Sheet sheet = cache.sheet;
        final int pages = list.size();
        final List<PagePrintingInfo> ppis = new ArrayList<PagePrintingInfo>(pages);
        PagePrintingInfo ppi = new PagePrintingInfo(0, param, 0, lastRow);
        ppi.details = details;
        final PagePrintingInfo pagePrintingInfo = ppi;
        pagePrintingInfo.pageRows += this.setupDetail(cache, ppi);
        if (ppi.details != null && ppi.details.size() > 1) {
            this.setupFoot(cache, ppi);
        }
        ppis.add(ppi);
        if (pages == 1) {
            return ppis;
        }
        int rowIndex = ppi.pageRows;
        for (int p = 1; p < pages; ++p) {
            int rows = 0;
            ppi = new PagePrintingInfo(p, list.get(p), rowIndex, lastRow);
            ppi.details = this.getDetailsData(cache, list.get(p));
            details = this.getDetailsData(cache, list.get(p));
            final int skipDetailRows = (details != null && details.size() > 1) ? ((details.size() - 1) * cache.detailRows) : 0;
            final List<CellRangeAddress> rangeToMerge = new ArrayList<CellRangeAddress>();
            for (int r = 0; r < ppi.pageRows; ++r) {
                final Row row = sheet.getRow(r);
                if (row != null) {
                    this.copyRow(cache, row, rowIndex, 0, (r > cache.detailEnd) ? null : rangeToMerge);
                }
                if (rows == cache.detailEnd) {
                    r += skipDetailRows;
                    rowIndex += skipDetailRows;
                }
                if (cache.config.getDetail().getLines1() == 0 && r == cache.detailStart) {
                    ppi.breaks.add(rowIndex);
                }
                ++rowIndex;
                ++rows;
            }
            for (final CellRangeAddress cra : rangeToMerge) {
                sheet.addMergedRegion(cra);
            }
            final PagePrintingInfo pagePrintingInfo2 = ppi;
            pagePrintingInfo2.pageRows += this.setupDetail(cache, ppi);
            this.setupFoot(cache, ppi);
            ppis.add(ppi);
            rowIndex = rowIndex + details.size() - 1;
        }
        return ppis;
    }
    
    @Override
    public void checkParam(final ReportFormRequestModel request) {
        if (request == null) {
            return;
        }
        final FilterRequestModel filter = request.getFilter();
        if (filter != null && filter.getTableName() != null && !this.dataService.sqlParamCheck(filter.getTableName())) {
            throw new ApplicationException("unlawful parameters");
        }
        if (filter.getFilters() != null) {
            final Map<String, FilterDescribe> filters = filter.getFilters();
            if (filters.get("search") != null && filters.get("search").getValue() != null && !this.dataService.sqlParamCheck(filters.get("search").getValue().toString())) {
                filters.get("search").setValue(StringEscapeUtils.escapeSql(filters.get("search").getValue().toString()));
            }
            for (final Map.Entry<String, FilterDescribe> entry : filters.entrySet()) {
                if (!this.dataService.sqlParamCheck(entry.getKey())) {
                    throw new ApplicationException("unlawful parameters");
                }
                if (entry.getValue().getValue() == null) {
                    continue;
                }
                filter.getFilters().get(entry.getKey()).setValue(StringEscapeUtils.escapeSql(entry.getValue().getValue().toString()));
            }
        }
        if (request.getFilterParam() != null) {
            for (final Map.Entry<String, Object> entry2 : request.getFilterParam().entrySet()) {
                if (!this.dataService.sqlParamCheck(entry2.getKey())) {
                    throw new ApplicationException("unlawful parameters");
                }
            }
        }
    }
    
    /*static {
        REPORT_FILE_TIMESTAMP = new SimpleDateFormat("yyMMddHHmmss");
        PRINT_AREA_PATTERN = Pattern.compile(".*!(\\$[A-Z]+\\$[0-9]+:\\$[A-Z]+\\$)([0-9]+)");
        QR_TAG_PATTERN = Pattern.compile("qr(:([\\d]+)x([\\d]+))?");
        QR_TAG_ATTR_PATTERN = Pattern.compile("([\\d]+)x([\\d]+)");
        CELL_RANGE_PATTERN = Pattern.compile("([0-9]+)");
        TABLE_CELL_PATTERN = Pattern.compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.<>]+)?\\}$");
        SUB_TABLE_CELL_PATTERN = Pattern.compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.]+)\\.([^.]+)\\}$");
        ReportServiceDefault.reportConfigJsonRef = new TypeReference<ReportPageConfigModel>() {};
        (PICTURE_TYPE_MAP = new HashMap<String, Integer>()).put("jpg", 5);
        ReportServiceDefault.PICTURE_TYPE_MAP.put("emf", 2);
        ReportServiceDefault.PICTURE_TYPE_MAP.put("dib", 7);
        ReportServiceDefault.PICTURE_TYPE_MAP.put("pict", 4);
        ReportServiceDefault.PICTURE_TYPE_MAP.put("png", 6);
        ReportServiceDefault.PICTURE_TYPE_MAP.put("wmf", 3);
    }*/
    
    private enum DataType
    {
        INTEGER, 
        DOUBLE, 
        UNKNOWN;
    }
    
    private static class QRImageInfo
    {
        int w;
        int h;
        String code;
        
        QRImageInfo(final int w, final int h, final String code) {
            this.w = w;
            this.h = h;
            this.code = code;
        }
    }
}
