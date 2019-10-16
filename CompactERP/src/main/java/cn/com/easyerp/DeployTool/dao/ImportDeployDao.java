package cn.com.easyerp.DeployTool.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.view.ImportDeployModel;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.I18nDescribe;
import cn.com.easyerp.core.cache.TableDescribe;

@Repository
public interface ImportDeployDao {
    List<Map<String, String>> selectBasicDeploy(@Param("lan") String paramString);

    ImportDeployModel getImportDeploy(@Param("tableName") String paramString);

    int isExistsImport(@Param("tableName") String paramString);

    int saveImportDeploy(@Param("import") ImportDeployModel paramImportDeployModel);

    TableDescribe getTableDesctribeById(@Param("tableName") String paramString);

    I18nDescribe getI18NByid(@Param("tableName") String paramString);

    void insertI18N(@Param("il8n") I18nDescribe paramI18nDescribe);

    void insertTable(@Param("table") TableDescribe paramTableDescribe);

    List<ColumnDescribe> selectColumnDescribeByTable(@Param("tableName") String paramString);

    void addColumn(@Param("col") ColumnDescribe paramColumnDescribe);

    void createTable(@Param("sql") String paramString);

    void delImportDeploy(@Param("tableName") String paramString);

    void delTable(@Param("tableName") String paramString);

    void delTableI18n(@Param("tableName") String paramString);

    void delColumns(@Param("tableName") String paramString);

    void addI18n(@Param("international_id") String paramString1, @Param("cn") String paramString2);

    List<String> getI18nsByTable(@Param("tableName") String paramString);

    void delI18nWithList(@Param("list") List<String> paramList);

    int dropTable(@Param("table") String paramString);

    void createImportProcedure(@Param("sql") String paramString);

    String getTableIdByBatchId(@Param("batchId") String paramString);

    void callSysImportApi(@Param("apiName") String paramString1, @Param("userId") String paramString2);

    void updateImportDeploy(@Param("import") ImportDeployModel paramImportDeployModel);

    void updTableIsImport(@Param("table") String paramString1, @Param("is_import") String paramString2);
}
