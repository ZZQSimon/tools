package cn.com.easyerp.addCompany;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AddCompanyDao;
import cn.com.easyerp.core.master.DxRoutingDataSource;

@Service
public class AddCompanyUtil {
    @Autowired
    private AddCompanyDao addCompanyDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;
    @Autowired
    private MasterDbService masterDbService;

    @Transactional
    public void initCompany(String companyId, String module) {
        Map<String, TableDescribe> tableDescCache = this.masterDbService.getTableDescCache(module);
        List<ProductView> productViews = this.masterDbService.getProductView(module);

        this.dxRoutingDataSource.reloadDataSource();

        List<String> userDatas = backupUserData(companyId, module, tableDescCache);

        initTable(companyId, module, tableDescCache);

        initProductView(companyId, productViews);

        if (userDatas != null && userDatas.size() != 0) {
            this.addCompanyDao.insertTableData(userDatas);
        }
        initTableData(companyId, module, tableDescCache);
    }

    private String buildTableSQL(String companyId, TableDescribe table) {
        if (table == null || table.getColumns() == null || table.getColumns().size() == 0 || table.getTable_type() != 0)
            return null;
        String sql = "DROP TABLE IF EXISTS `" + companyId + "`.`" + table.getId() + "`;";
        sql = sql + " CREATE TABLE if not exists ";
        sql = sql + "`" + companyId + "`.`" + table.getId() + "` (";

        for (ColumnDescribe column : table.getColumns()) {
            sql = sql + "`" + column.getColumn_name() + "` ";
            sql = sql + columnTypeToDb(column) + ",";
        }
        sql = sql.substring(0, sql.length() - 1);
        String[] idColumns = table.getIdColumns();
        if (idColumns != null && idColumns.length != 0) {
            sql = sql + ", PRIMARY KEY (";
            for (int i = 0; i < idColumns.length; i++) {
                sql = sql + "`" + idColumns[i] + "`,";
            }
            sql = sql.substring(0, sql.length() - 1);
            sql = sql + ")";
        }
        return sql + ") ENGINE=InnoDB DEFAULT CHARSET=utf8";
    }

    private String columnTypeToDb(ColumnDescribe column) {
        int MaxLen;
        int sumFlag;
        switch (column.getData_type()) {
        case 1:
        case 6:
        case 7:
        case 8:
        case 9:
        case 10:
        case 13:
        case 14:
        case 15:
            return "varchar(" + ((column.getMax_len() == null) ? "2000" : column.getMax_len()) + ")";
        case 2:
        case 5:
            return "int";
        case 3:
            sumFlag = (column.getSum_flag() == null) ? 0 : column.getSum_flag().intValue();
            MaxLen = (column.getMax_len() == null) ? 10 : column.getMax_len().intValue();
            return "decimal(" + MaxLen + "," + sumFlag + ")";
        case 4:
            return "DATE";
        case 11:
            return "TIME";
        case 12:
            return "DATETIME";
        }
        return "";
    }

    private int initTable(String companyId, String module, Map<String, TableDescribe> tableDescCache) {
        Map<String, String> tableSQL = new HashMap<String, String>();
        for (Map.Entry<String, TableDescribe> entry : tableDescCache.entrySet()) {
            if (module.equals(((TableDescribe) entry.getValue()).getModule())
                    || ((TableDescribe) entry.getValue()).getModule() == null
                    || "".equals(((TableDescribe) entry.getValue()).getModule())) {
                String sql = buildTableSQL(companyId, (TableDescribe) entry.getValue());
                if (sql != null)
                    tableSQL.put(entry.getKey(), sql);
            }
        }
        for (Map.Entry<String, String> entry : tableSQL.entrySet()) {
            if (entry.getValue() != null)
                this.addCompanyDao.execSql((String) entry.getValue());
        }
        return 1;
    }

    private List<String> backupUserData(String companyId, String module, Map<String, TableDescribe> tableDescCache) {
        Map<String, List<Map<String, Object>>> allTableDatas = new HashMap<String, List<Map<String, Object>>>();
        for (Map.Entry<String, TableDescribe> table : tableDescCache.entrySet()) {
            if ("c_".indexOf((String) table.getKey()) == 0 || "p_".indexOf((String) table.getKey()) == 0) {

                List<Map<String, Object>> tableDatas = this.addCompanyDao.selectUserTableData(module,
                        "`" + companyId + "`.`" + (String) table.getKey() + "`");
                if (tableDatas != null && tableDatas.size() != 0)
                    allTableDatas.put(table.getKey(), tableDatas);
            }
        }
        List<String> sqls = new ArrayList<String>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : allTableDatas.entrySet()) {
            for (Map<String, Object> datas : entry.getValue()) {
                sqls.add(buildInsertSQL(companyId, (String) entry.getKey(), datas));
            }
        }
        return sqls;
    }

    private int initTableData(String companyId, String module, Map<String, TableDescribe> tableDescCache) {
        Map<String, List<Map<String, Object>>> allTableDatas = new HashMap<String, List<Map<String, Object>>>();
        for (Map.Entry<String, TableDescribe> table : tableDescCache.entrySet()) {
            if ("m_user".equals(table.getKey())) {
                continue;
            }
            List<Map<String, Object>> tableDatas = null;
            if (((TableDescribe) table.getValue()).getTable_type() == 0)
                tableDatas = this.addCompanyDao.selectTableData(module, (String) table.getKey());
            if (tableDatas != null && tableDatas.size() != 0)
                allTableDatas.put(table.getKey(), tableDatas);
        }
        List<String> sqls = new ArrayList<String>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : allTableDatas.entrySet()) {
            for (Map<String, Object> datas : entry.getValue()) {
                sqls.add(buildInsertSQL(companyId, (String) entry.getKey(), datas));
            }
        }

        this.addCompanyDao.insertTableData(sqls);
        return 1;
    }

    private String buildInsertSQL(String companyId, String tableName, Map<String, Object> data) {
        String columns = "";
        String values = "";
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            columns = columns + "`" + (String) entry.getKey() + "`,";
            if (entry.getValue() == null) {
                values = values + " " + entry.getValue() + ",";
                continue;
            }
            String value = entry.getValue().toString();
            value = value.replace("'", "\\'");
            values = values + "'" + value + "',";
        }

        columns = columns.substring(0, columns.length() - 1);
        values = values.substring(0, values.length() - 1);
        return "insert into `" + companyId + "`.`" + tableName + "` (" + columns + ") values (" + values + ")";
    }

    private void initProductView(String companyId, List<ProductView> productViews) {
        List<String> productViewSqls = new ArrayList<String>();
        for (ProductView productView : productViews) {
            if (productView.getProduct() != null && !"".equals(productView.getProduct())) {
                productViewSqls.add("DROP PROCEDURE IF EXISTS `" + companyId + "`.`" + productView.getName() + "`;"
                        + " CREATE PROCEDURE `" + companyId + "`.`" + productView.getName() + "` "
                        + productView.getProduct());
            }
            if (productView.getView() != null && !"".equals(productView.getView())) {
                productViewSqls.add("DROP PROCEDURE IF EXISTS `" + companyId + "`." + productView.getName() + ";"
                        + " CREATE VIEW `" + companyId + "`.`" + productView.getName() + "` " + productView.getView());
            }
        }
        if (productViewSqls.size() != 0)
            for (String sql : productViewSqls)
                this.addCompanyDao.execSql(sql);
    }
}
