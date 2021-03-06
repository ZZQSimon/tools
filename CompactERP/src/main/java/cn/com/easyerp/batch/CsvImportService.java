package cn.com.easyerp.batch;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.LogType;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.storage.StorageService;

@Service("csv-import")
public class CsvImportService extends BatchService<CsvImportParamModel> {
    private static TypeReference<CsvImportParamModel> CsvImportParamModelRef = new TypeReference<CsvImportParamModel>() {
    };
    @Autowired
    private StorageService storageService;
    @Autowired
    private LogService logService;

    public void init(BatchDescribe<CsvImportParamModel> batch) {
        batch.setData(Common.fromJson(batch.getService_param(), CsvImportParamModelRef));
    }

    public BatchFormModel form(BatchFormRequestModel request, String parent, List<FieldModelBase> fields,
            BatchDescribe<CsvImportParamModel> batch, int cols) {
        return new CsvImportFormModel(parent, fields, batch, cols);
    }

    public ApiActionResult intercept(BatchDescribe<CsvImportParamModel> batch, BatchFormRequestModel request) {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        CsvImportFormModel form = (CsvImportFormModel) request.getWidget();
        FileFieldModel file = (FileFieldModel) form.getField(((CsvImportParamModel) batch.getData()).getColumn());
        FileItem fi = this.storageService.getUploadFile(file.getUuid());
        String suffix = fi.getName().substring(fi.getName().lastIndexOf('.') + 1);
        try {
            String tempTable;
            if (!"csv".equals(suffix)) {
                tempTable = importExcel(batch, fi.getInputStream(), AuthService.getCurrentUserId(), suffix,
                        httpRequest);
            } else {
                tempTable = importCSV(batch, fi.getInputStream(), AuthService.getCurrentUserId(), httpRequest);
            }
            ApiActionResult result = super.intercept(batch, request);
            if (result.isSuccess()) {

                form.setTempTable(tempTable);
                result.setNoPopup(true);
            }
            return result;
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private String importCSV(BatchDescribe<CsvImportParamModel> batch, InputStream is, String uid,
            HttpServletRequest httpRequest) throws IOException {
        String logId = this.logService.getMaxId();
        this.logService.logTemp(logId, LogType.Import, AuthService.getCurrentUserId());
        httpRequest.setAttribute("logId", logId);

        CsvImportParamModel param = (CsvImportParamModel) batch.getData();
        String tableId = param.getTable();
        httpRequest.setAttribute("tableId", tableId);

        Reader in = new InputStreamReader(new BOMInputStream(is), Charset.forName(param.getCharset()));
        CSVParser parse = CSVFormat.DEFAULT.withDelimiter(param.getDelimiter()).withQuote(param.getQuote())
                .withHeader(new String[0]).parse(in);
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        int id = 0;
        int space = 0;
        String tmpId = param.getTmpId();
        for (CSVRecord record : parse) {
            Map rec = record.toMap();
            space = 0;
            for (Map.Entry<String, String> entry : record.toMap().entrySet()) {
                if ("".equals(((String) entry.getValue()).trim()))
                    space++;
                rec.put(((String) entry.getKey()).replaceAll(" ", "_"), rec.remove(entry.getKey()));
            }
            if (space != rec.size()) {
                data.add(rec);
                if (tmpId != null) {
                    rec.put(tmpId, Integer.valueOf(++id));
                }
            }
        }
        return this.dataService.insertImportData(param.getTable(), data, uid, httpRequest);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String importExcel(BatchDescribe<CsvImportParamModel> batch, InputStream is, String uid, String suffix,
            HttpServletRequest httpRequest) throws IOException {
        final String logId = this.logService.getMaxId();
        this.logService.logTemp(logId, LogType.Import, AuthService.getCurrentUserId());
        httpRequest.setAttribute("logId", (Object) logId);
        final CsvImportParamModel param = (CsvImportParamModel) batch.getData();
        final String tableId = param.getTable();
        httpRequest.setAttribute("tableId", (Object) tableId);

        try (Workbook workbook = (Workbook) ("xls".equals(suffix) ? new HSSFWorkbook(is) : new XSSFWorkbook(is));) {
            FormulaEvaluator formulaEvaluator = null;
            formulaEvaluator = (FormulaEvaluator) ("xls".equals(suffix)
                    ? new HSSFFormulaEvaluator((HSSFWorkbook) workbook)
                    : new XSSFFormulaEvaluator((XSSFWorkbook) workbook));
            final List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
            int id = 0;
            final String tmpId = param.getTmpId();
            Sheet sheet = null;
            final int start = 0;
            int space = 0;
            String value = "";
            if (workbook.getNumberOfSheets() != 0) {
                sheet = workbook.getSheetAt(0);
                final Row head = sheet.getRow(start);
                for (int j = start + 1; j < sheet.getLastRowNum() + 1; ++j) {
                    final Row row = sheet.getRow(j);
                    if (row != null) {
                        space = 0;
                        final Map rec = new HashMap<>();
                        for (int k = 0; k < row.getLastCellNum(); ++k) {
                            value = this.getCellValue(row.getCell(k), formulaEvaluator);
                            if (value != null && "".equals(value)) {
                                value = null;
                            }
                            if ("".equals(value) || value == null) {
                                ++space;
                            }
                            if (head.getCell(k) != null && head != null) {
                                rec.put(head.getCell(k).toString().replaceAll(" ", "_"), value);
                            }
                        }
                        if (space != row.getLastCellNum()) {
                            if (tmpId != null) {
                                rec.put(tmpId, ++id);
                            }
                            data.add(rec);
                        }
                    }
                }
            }
            return this.dataService.insertImportData(param.getTable(), (List) data, uid, httpRequest);
        }
    }

    private String getCellValue(Cell cell, FormulaEvaluator formulaEvaluator) {
        String cellValue = null;
        if (null != cell) {
            CellValue value;
            switch (cell.getCellType()) {
            case NUMERIC:
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    Date date = cell.getDateCellValue();
                    cellValue = Common.defaultDateTimeFormat.format(date);
                } else {
                    // cell.setCellType(CellType.STRING);
                    cellValue = cell.getStringCellValue() + "";
                }
                return cellValue;
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return cell.getBooleanCellValue() + "";
            case FORMULA: {
                value = formulaEvaluator.evaluate(cell);
                if (value != null)
                    switch (value.getCellType()) {
                    case BOOLEAN:
                        cellValue = String.valueOf(value.getBooleanValue());
                        break;
                    case NUMERIC:
                        cellValue = String.valueOf(cell.getNumericCellValue());
                        break;
                    case STRING:
                        cellValue = value.getStringValue();
                        break;
                    case BLANK:
                        cellValue = null;
                        break;
                    case ERROR:
                        cellValue = null;
                        break;
                    default:
                        cellValue = null;
                        break;
                    }
                return cellValue;
            }
            case BLANK:
                return null;
            case ERROR:
                return null;
            default:
                break;
            }
            cellValue = null;
        }
        return cellValue;
    }

    public static void main(String[] argv) throws IOException {
        HttpServletRequest httpRequest = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();
        CsvImportService service = new CsvImportService();
        BatchDescribe<CsvImportParamModel> batch = new BatchDescribe<CsvImportParamModel>();
        CsvImportParamModel param = new CsvImportParamModel();
        param.setCharset("UTF8");
        batch.setData(param);
        service.importCSV(batch, new FileInputStream("/home/zl/tmp/data.csv"), "test", httpRequest);
    }
}