package cn.com.easyerp.addCompany;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AddCompanyDao;
import cn.com.easyerp.core.master.DxRoutingDataSource;

@Service
public class MasterDbService {
    private Map<String, Map<String, TableDescribe>> tableDescCache;
    private Map<String, List<ProductView>> productViewsCache;

    public void initMasterDBData(String module) {
        List<ProductView> productViews;
        Map<String, TableDescribe> sysTable;
        String domainKey = this.dxRoutingDataSource.getDomainKey();
        this.dxRoutingDataSource.setDomainKey(DxRoutingDataSource.DX_DEFAULT_DATASOURCE);

        if (this.tableDescCache == null)
            this.tableDescCache = new HashMap<>();
        if (this.tableDescCache.get(module) == null) {
            sysTable = new HashMap<String, TableDescribe>();
        } else {
            sysTable = new HashMap<String, TableDescribe>();
        }
        List<TableDescribe> tables = this.addCompanyDao.selectTableDescribe(module);
        for (TableDescribe table : tables) {
            sysTable.put(table.getId(), table);
        }

        List<ColumnDescribe> columnDescribes = this.addCompanyDao.selectColumnsDescribe(module);
        for (ColumnDescribe column : columnDescribes) {
            String table_id = column.getTable_id();
            TableDescribe table = (TableDescribe) sysTable.get(table_id);

            if (column.getIs_id_column() == 1)
                table.addId_column(column.getColumn_name());
        }
        for (ColumnDescribe column : columnDescribes) {
            String table_id = column.getTable_id();
            TableDescribe table = (TableDescribe) sysTable.get(table_id);
            table.addColumn(column);
        }
        this.tableDescCache.put(module, sysTable);

        if (this.productViewsCache == null)
            this.productViewsCache = new HashMap<>();
        if (this.productViewsCache.get(module) == null) {
            productViews = this.addCompanyDao.selectProductView(module);
        } else {
            productViews = this.addCompanyDao.selectProductView(module);
        }
        this.productViewsCache.put(module, productViews);

        this.dxRoutingDataSource.setDomainKey(domainKey);
    }

    @Autowired
    private AddCompanyDao addCompanyDao;
    @Autowired
    private DxRoutingDataSource dxRoutingDataSource;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, TableDescribe> getTableDescCache(String module) {
        if (this.tableDescCache == null || this.tableDescCache.get(module) == null)
            initMasterDBData(module);
        return (Map) this.tableDescCache.get(module);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<ProductView> getProductView(String module) {
        if (this.productViewsCache == null || this.productViewsCache.get(module) == null)
            initMasterDBData(module);
        return (List) this.productViewsCache.get(module);
    }
}
