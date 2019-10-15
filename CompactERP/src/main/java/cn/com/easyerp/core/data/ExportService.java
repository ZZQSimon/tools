package cn.com.easyerp.core.data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;

@Service
public class ExportService {
    @Autowired
    private SystemDao systemDao;
    @Autowired
    private StorageService storageService;
    @Autowired
    private AuthService authService;
    @Autowired
    private DataService dataService;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private CacheService cacheService;

    public ActionResult exportToCsv(TableDescribe table, List<String> rids, String csvFileName, AuthDetails user)
            throws IOException {
        return exportToCsv(table, rids, csvFileName, user, null);
    }

    public ActionResult exportToCsv(TableDescribe table, List<String> ids, String csvFileName, AuthDetails user,
            ExportRecordGetter getter) throws IOException {
        List<ColumnDescribe> cds = table.getColumns();

        File csvFile = this.storageService.tmp("export", "csv");
        FileOutputStream fos = new FileOutputStream(csvFile);
        OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        fos.write(new byte[] { -17, -69, -65 });
        BufferedWriter buffer = new BufferedWriter(osw, 'Ѐ');

        boolean columnNamesExported = false;
        List<String> columnNames = new ArrayList<String>();
        List<String> values = new ArrayList<String>();
        Set<String> noAuthColumns = this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R);
        for (Map<String, Object> record : selectExportData(table, ids, getter)) {
            for (Map.Entry<String, Object> entry : record.entrySet()) {
                if (!isEditorInputData(cds, (String) entry.getKey())) {
                    if (!columnNamesExported) {
                        String column = (String) entry.getKey();
                        if (noAuthColumns != null && noAuthColumns.contains(column))
                            continue;
                        columnNames.add(
                                (table.getExport_sql() == null) ? this.dataService.getLabel(table.getColumn(column))
                                        : column);
                    }
                    Object value = entry.getValue();
                    values.add((value == null) ? "" : value.toString());
                }
            }

            if (!columnNamesExported) {
                columnNamesExported = true;
                buffer.write(Common.join(columnNames, ",", "\""));
                buffer.newLine();
            }
            buffer.write(Common.join(values, ",", "\""));
            buffer.newLine();
            values.clear();
        }

        buffer.flush();
        buffer.close();
        FileInputStream fileName = new FileInputStream(csvFile);
        int fileSize = fileName.available();

        return this.storageService.createDownload(csvFileName, fileName, fileSize);
    }

    public List<Map<String, Object>> selectExportData(TableDescribe table, List<String> ids,
            ExportRecordGetter getter) {
        List<DatabaseDataMap> keys = new ArrayList<DatabaseDataMap>();
        for (String id : ids) {

            RecordModel record = (getter == null) ? (RecordModel) ViewService.fetchWidgetModel(id) : getter.get(id);
            if (record == null)
                continue;
            DatabaseDataMap values = record.getValueMap();
            DatabaseDataMap keyMap = new DatabaseDataMap();
            for (String keyColumn : table.getIdColumns())
                keyMap.put(keyColumn, values.get(keyColumn));
            keys.add(keyMap);
        }

        List<ColumnDescribe> columns = table.getColumns();
        Set<ColumnDescribe> columnList = new HashSet<ColumnDescribe>(columns.size());
        for (ColumnDescribe column : columns) {
            if (!column.isVirtual())
                columnList.add(column);
        }
        List<ReferenceModel> refs = this.dataService.makeRefModels(table);
        String encrypt_str = this.cacheService.getDecryptKey(this.dxRoutingDataSource.getDomainKey());

        List<Map<String, Object>> records = new ArrayList<Map<String, Object>>(keys.size());
        List<DatabaseDataMap> subSet = new ArrayList<DatabaseDataMap>();
        int keyCount = table.getIdColumns().length;
        int paramCount = 0;
        if (table.getExport_sql() != null)
            paramCount = Common.sqlBindParameterCount(table.getExport_sql());
        for (DatabaseDataMap map : keys) {
            if ((subSet.size() + 1) * keyCount + paramCount >= 2000) {

                records.addAll(this.systemDao.selectDataWithKeys(table.getId(), columnList, subSet, refs, encrypt_str));
                subSet.clear();
            }
            subSet.add(map);
        }
        if (subSet.size() > 0) {
            records.addAll(this.systemDao.selectDataWithKeys(table.getId(), columnList, subSet, refs, encrypt_str));
        }
        List<ColumnDescribe> refcols = new ArrayList<ColumnDescribe>();
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();

        for (ColumnDescribe col : table.getColumns()) {
            if (!Common.isBlank(col.getRef_table_name()) || !Common.isBlank(col.getDic_id())) {
                refcols.add(col);
            }
        }
        for (Map<String, Object> record : this.dataService.makeRefData(records)) {
            for (ColumnDescribe col : refcols) {
                String key = col.getColumn_name();
                if (!Common.isBlank(col.getDic_id())) {
                    record.put(key, this.dataService.getDictText(col.getDic_id(),
                            (record.get(key) == null) ? null : record.get(key).toString()));
                    continue;
                }
                record.put(key, this.dataService.buildNameExpression(
                        this.dataService.getTableDesc(col.getRef_table_name()), (Map) record.get(key + ".ref")));
            }

            data.add(record);
        }
        return data;
    }

    public ActionResult exportAllToXls(final TableDescribe table, final List<Map<String, Object>> list,
            final String xlsFileName, final AuthDetails user, final ExportRecordGetter getter) throws IOException {
        final HSSFWorkbook wb = new HSSFWorkbook();
        final HSSFSheet sheet = wb.createSheet(this.dataService.i18nString(table.getI18n()));
        sheet.setDefaultColumnWidth(11);
        final HSSFRow row0 = sheet.createRow(0);
        int rowNum = 1;
        final HSSFCellStyle dateStyle = wb.createCellStyle();
        final HSSFCellStyle dateTimeStyle = wb.createCellStyle();
        final CreationHelper creationHelper = (CreationHelper) wb.getCreationHelper();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        dateTimeStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd HH:mm"));
        final List<Map<String, Object>> keys = list;
        if (keys.size() == 0) {
            return new ActionResult(false, (Object) "no export data");
        }
        boolean columnNamesExported = false;
        final List<String> columnNames = new ArrayList<String>();
        final List<ExportColumn> values = new ArrayList<ExportColumn>();
        final List<ColumnDescribe> columns = (List<ColumnDescribe>) table.getColumns();
        Collections.sort(columns, new Comparator<ColumnDescribe>() {
            @Override
            public int compare(final ColumnDescribe c1, final ColumnDescribe c2) {
                final int s1 = c1.getSeq();
                final int s2 = c2.getSeq();
                return s1 - s2;
            }
        });
        final Set<String> noAuthColumns = this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R);
        final List<String> columnsName = new ArrayList<String>();
        for (final ColumnDescribe column : columns) {
            if (column.getData_type() != 15) {
                if (noAuthColumns != null && noAuthColumns.contains(column.getColumn_name())) {
                    continue;
                }
                final String refTable = column.getRef_table_name();
                final TableDescribe refTableDesc = this.dataService.getTableDesc(refTable);
                if (refTable == null || refTable.equals("") || refTableDesc.getName_expression_publicity() == null
                        || refTableDesc.getName_expression_publicity().equals("")) {
                    columnsName.add(column.getColumn_name());
                } else {
                    final String originCol = column.getColumn_name();
                    final String[] refIds = this.dataService.getTableDesc(refTable).getIdColumns();
                    final String refId = (refIds == null) ? null : refIds[0];
                    final String refName = this.dataService.getTableDesc(refTable).getName_column();
                    final String nameEx = table.getName_expression_publicity();
                    boolean refIdHidden = true;
                    boolean refNameHidden = true;
                    if (refId != null) {
                        refIdHidden = this.dataService.getColumnDesc(refTable, refId).isHidden();
                    }
                    if (refName != null) {
                        refNameHidden = this.dataService.getColumnDesc(refTable, refName).isHidden();
                    }
                    if (!refIdHidden) {
                        columnsName.add(originCol + '\n' + refTable + '\n' + refId);
                    }
                    if (!refNameHidden) {
                        columnsName.add(originCol + '\n' + refTable + '\n' + refName);
                    } else {
                        if (nameEx == null || nameEx.equals("")) {
                            continue;
                        }
                        columnsName.add(originCol + '\n' + refTable + '\n' + nameEx);
                    }
                }
            }
        }
        for (final Map<String, Object> record : keys) {
            for (final String key : columnsName) {
                if (key.contains("\n")) {
                    final String[] splitted = key.split("\n");
                    final String originCol2 = splitted[0];
                    final String refTableName = splitted[1];
                    final String refColName = splitted[2];
                    final String realOriginCol = this.dataService.getLabel(table.getColumn(originCol2));
                    final TableDescribe refTable2 = this.dataService.getTableDesc(refTableName);
                    if (this.isEditorInputData(columns, originCol2)) {
                        continue;
                    }
                    if (!columnNamesExported) {
                        if (noAuthColumns != null && noAuthColumns.contains(originCol2)) {
                            continue;
                        }
                        if (refTable2.getColumn(refColName) != null) {
                            columnNames.add((refTable2.getExport_sql() == null)
                                    ? (realOriginCol + "-" + this.dataService.getLabel(refTable2.getColumn(refColName)))
                                    : (realOriginCol + "-" + refColName));
                        }
                    }
                    final Object value = ((Map<String, Object>) record.get(originCol2 + ".ref")).get(refColName);
                    values.add(
                            new ExportColumn(refTable2.getColumn(refColName), (value == null) ? "" : value.toString()));
                } else {
                    if (this.isEditorInputData(columns, key)) {
                        continue;
                    }
                    if (!columnNamesExported) {
                        final String column2 = key;
                        if (noAuthColumns != null && noAuthColumns.contains(column2)) {
                            continue;
                        }
                        columnNames.add(
                                (table.getExport_sql() == null) ? this.dataService.getLabel(table.getColumn(column2))
                                        : column2);
                    }
                    final Object value2 = record.get(key);
                    values.add(new ExportColumn(table.getColumn(key), (value2 == null) ? "" : value2.toString()));
                }
            }
            if (!columnNamesExported) {
                columnNamesExported = true;
            }
            int cellNum = 0;
            final HSSFRow row2 = sheet.createRow(rowNum++);
            Date date = new Date();
            for (final ExportColumn col : values) {
                final HSSFCell cell = row2.createCell(cellNum++);
                if (col.getColumn() == null) {
                    continue;
                }
                final int dataType = col.getColumn().getData_type();
                if (dataType == 4 || dataType == 12) {
                    if (col.getValue() == null || col.getValue().equals("")) {
                        cell.setCellValue(col.getValue());
                    } else {
                        final SimpleDateFormat oldSDF = (dataType == 4) ? new SimpleDateFormat("yyyy-MM-dd")
                                : new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            date = oldSDF.parse(col.getValue());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (dataType == 4) {
                            cell.setCellStyle(dateStyle);
                        } else if (dataType == 12) {
                            cell.setCellStyle(dateTimeStyle);
                        }
                        cell.setCellValue(date);
                    }
                } else {
                    cell.setCellValue(col.getValue());
                }
            }
            values.clear();
        }
        int cellNum2 = 0;
        for (final String key2 : columnNames) {
            row0.createCell(cellNum2++).setCellValue(key2);
        }
        final File xlsFile = this.storageService.tmp("export", "xls");
        wb.write(xlsFile);
        final FileInputStream fileName = new FileInputStream(xlsFile);
        final int fileSize = fileName.available();
        return this.storageService.createDownload(xlsFileName, (InputStream) fileName, fileSize);
    }

    public ActionResult exportAllToExcel(final TableDescribe table, final List<Map<String, Object>> list,
            final String csvFileName, final AuthDetails user, final ExportRecordGetter getter) throws IOException {
        final List<Map<String, Object>> keys = list;
        if (keys.size() == 0) {
            return new ActionResult(false, (Object) "no export data");
        }
        final File csvFile = this.storageService.tmp("export", ".xlsx");
        final FileOutputStream fos = new FileOutputStream(csvFile);
        final OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        fos.write(new byte[] { -17, -69, -65 });
        final BufferedWriter buffer = new BufferedWriter(osw, 1024);
        boolean columnNamesExported = false;
        final List<String> columnNames = new ArrayList<String>();
        final List<ExportColumn> values = new ArrayList<ExportColumn>();
        final List<ColumnDescribe> columns = (List<ColumnDescribe>) table.getColumns();
        Collections.sort(columns, new Comparator<ColumnDescribe>() {
            @Override
            public int compare(final ColumnDescribe c1, final ColumnDescribe c2) {
                final int s1 = c1.getSeq();
                final int s2 = c2.getSeq();
                return s1 - s2;
            }
        });
        final List<String> columnsName = new ArrayList<String>();
        for (final ColumnDescribe column : columns) {
            if (column.getData_type() != 15) {
                columnsName.add(column.getColumn_name());
            }
        }
        final Set<String> noAuthColumns = this.authService.noAuthColumns(table, user, AuthConfigDescribe.Type.R);
        for (final Map<String, Object> record : keys) {
            for (final String key : columnsName) {
                if (!this.isEditorInputData(columns, key)) {
                    if (!columnNamesExported) {
                        final String column2 = key;
                        if (noAuthColumns != null && noAuthColumns.contains(column2)) {
                            continue;
                        }
                        columnNames.add(
                                (table.getExport_sql() == null) ? this.dataService.getLabel(table.getColumn(column2))
                                        : column2);
                    }
                    final Object value = record.get(key);
                    values.add(
                            new ExportColumn(table.getColumn(key), (value == null) ? "" : ("\t" + value.toString())));
                }
            }
            if (!columnNamesExported) {
                columnNamesExported = true;
                buffer.write(Common.join((Collection) columnNames, ",", "\""));
                buffer.newLine();
            }
            buffer.write(Common.joinValue((List) values, ",", "\""));
            buffer.newLine();
            values.clear();
        }
        buffer.flush();
        buffer.close();
        final FileInputStream fileName = new FileInputStream(csvFile);
        final int fileSize = fileName.available();
        return this.storageService.createDownload(csvFileName, (InputStream) fileName, fileSize);
    }

    public InputStream ftpEport(final TableDescribe table, final List<DatabaseDataMap> list, final String csvFileName)
            throws Exception {
        final List<DatabaseDataMap> keys = list;
        final File csvFile = this.storageService.tmp("export", "csv");
        final FileOutputStream fos = new FileOutputStream(csvFile);
        final OutputStreamWriter osw = new OutputStreamWriter(fos, "utf-8");
        fos.write(new byte[] { -17, -69, -65 });
        final BufferedWriter buffer = new BufferedWriter(osw, 1024);
        boolean columnNamesExported = false;
        final List<String> columnNames = new ArrayList<String>();
        final List<String> values = new ArrayList<String>();
        final List<ColumnDescribe> columns = (List<ColumnDescribe>) table.getColumns();
        Collections.sort(columns, new Comparator<ColumnDescribe>() {
            @Override
            public int compare(final ColumnDescribe c1, final ColumnDescribe c2) {
                final int s1 = c1.getSeq();
                final int s2 = c2.getSeq();
                return s1 - s2;
            }
        });
        final List<String> columnsName = new ArrayList<String>();
        for (final ColumnDescribe column : columns) {
            if (column.getData_type() != 15) {
                columnsName.add(column.getColumn_name());
            }
        }
        for (final Map<String, Object> record : keys) {
            for (final String key : columnsName) {
                if (!columnNamesExported) {
                    final String column2 = key;
                    columnNames.add(this.dataService.getLabel(table.getColumn(column2)));
                }
                final Object value = record.get(key);
                values.add((value == null) ? "" : value.toString());
            }
            if (!columnNamesExported) {
                columnNamesExported = true;
                buffer.write(Common.join((Collection) columnNames, ",", "\""));
                buffer.newLine();
            }
            buffer.write(Common.join((Collection) values, ",", "\""));
            buffer.newLine();
            values.clear();
        }
        buffer.flush();
        buffer.close();
        final FileInputStream FileInputStream = new FileInputStream(csvFile);
        return FileInputStream;
    }

    private boolean isEditorInputData(final List<ColumnDescribe> columns, final String column_name) {
        for (final ColumnDescribe column : columns) {
            if (column.getColumn_name().equals(column_name) && (column.getData_type() == 15 || column.isHidden())) {
                return true;
            }
        }
        return false;
    }
}
