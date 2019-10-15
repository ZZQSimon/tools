package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ExternalFormDao {
    List<Map<String, Object>> selectExternalForm(@Param("tableId") String paramString,
            @Param("condition") Map<String, Object> paramMap);

    int saveExternalForm(@Param("tableId") String paramString, @Param("condition") Map<String, Object> paramMap1,
            @Param("dataMap") Map<String, Object> paramMap2);

    String selectLanguage(@Param("database") String paramString);
}
