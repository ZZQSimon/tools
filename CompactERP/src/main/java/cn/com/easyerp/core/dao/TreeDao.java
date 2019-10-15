package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.tree.TreeNodeModel;

@Repository
public interface TreeDao {
    List<TreeNodeModel> selectTree(@Param("table") TableDescribe paramTableDescribe, @Param("node") String paramString1,
            @Param("status") String paramString2);

    List<DatabaseDataMap> selectParent(@Param("table") TableDescribe paramTableDescribe,
            @Param("keys") DatabaseDataMap paramDatabaseDataMap, @Param("where") String paramString);
}
