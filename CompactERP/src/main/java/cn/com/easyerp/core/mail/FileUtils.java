package cn.com.easyerp.core.mail;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.storage.StorageService;

@Service
public class FileUtils {
    @Autowired
    private StorageService storageService;
    @Autowired
    private DataService dataService;

    @SuppressWarnings({ "rawtypes" })
    public InputStream buildTxtFile(final TableDescribe table, final List<DatabaseDataMap> list,
            final String csvFileName) throws Exception {
        final List<DatabaseDataMap> keys = list;
        final File csvFile = this.storageService.tmp("export", ".txt");
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
                values.add((value == null) ? "" : value.toString().replaceAll("\\|", "-"));
            }
            if (!columnNamesExported) {
                columnNamesExported = true;
                buffer.write(Common.join((Collection) columnNames, "|", ""));
                buffer.newLine();
            }
            buffer.write(Common.join((Collection) values, "|", ""));
            buffer.newLine();
            values.clear();
        }
        buffer.flush();
        buffer.close();
        final FileInputStream FileInputStream = new FileInputStream(csvFile);
        return FileInputStream;
    }
}
