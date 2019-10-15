package cn.com.easyerp.core.data;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AutoKeyDao;
import cn.com.easyerp.framework.common.Common;

@Service
public class AutoKeyService {
    @Autowired
    private CacheService cacheService;
    @Autowired
    private AutoKeyDao autoKeyDao;

    private Object[] makeKeyArray(TableDescribe table, Map<String, Object> data, boolean isParent) {
        String[] keys;
        if (isParent) {
            keys = this.cacheService.getTableDesc(table.getParent_id()).getIdColumns();
        } else {
            keys = table.getIdColumns();
        }
        int length = isParent ? keys.length : (keys.length - 1);
        Object[] array = new Object[length];
        for (int i = 0; i < length; i++)
            array[i] = data.get(keys[i]);
        return array;
    }

    public String update(TableDescribe table) {
        return update(table, null);
    }

    public String update(TableDescribe table, Map<String, Object> data) {
        String parentKey = Common.join(makeKeyArray(table, data, false));
        AutoKeyDaoModel param = new AutoKeyDaoModel(table.getId(), parentKey, Integer.valueOf(1), null);
        this.autoKeyDao.updateId(param);
        return param.getNext_id();
    }

    public int getIndex(TableDescribe table, Map<String, Object> parentData) {
        String tableName = table.getId();
        String parentKey = (parentData == null) ? "" : Common.join(makeKeyArray(table, parentData, true));
        AutoKeyDaoModel param = new AutoKeyDaoModel(tableName, parentKey, Integer.valueOf(0), null);
        this.autoKeyDao.getId(param);
        return param.getNext_no().intValue();
    }

    public AutoKeyDaoModel getIndexData(TableDescribe table, Map<String, Object> parentData, Integer current) {
        String tableName = table.getId();
        String parentKey = Common.join(makeKeyArray(table, parentData, (parentData != null)));
        AutoKeyDaoModel param = new AutoKeyDaoModel(tableName, parentKey, Integer.valueOf(0), current);
        this.autoKeyDao.getId(param);
        return param;
    }
}
