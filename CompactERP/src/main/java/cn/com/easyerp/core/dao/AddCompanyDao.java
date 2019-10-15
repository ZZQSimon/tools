package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.addCompany.ProductView;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;

@Repository
public interface AddCompanyDao {
    int createDB(@Param("DBName") String paramString);

    int execSql(@Param("sql") String paramString);

    List<Map<String, Object>> selectTableData(@Param("module") String paramString1,
            @Param("table") String paramString2);

    List<Map<String, Object>> selectUserTableData(@Param("module") String paramString1,
            @Param("table") String paramString2);

    void insertTableData(@Param("sqls") List<String> paramList);

    List<TableDescribe> selectTableDescribe(@Param("module") String paramString);

    List<Map<String, Object>> selectMenu(@Param("module") String paramString);

    List<ColumnDescribe> selectColumnsDescribe(@Param("module") String paramString);

    List<ProductView> selectProductView(@Param("module") String paramString);
}
