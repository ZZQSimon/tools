package cn.com.easyerp.core.dao;

import java.sql.Timestamp;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.framework.enums.LogType;

@Repository
public interface LogDao {
    int insertLogNew(@Param("id") String paramString1, @Param("ts") Timestamp paramTimestamp,
            @Param("user_id") String paramString2, @Param("type") LogType paramLogType,
            @Param("target_id") String paramString3, @Param("context") String paramString4,
            @Param("status") String paramString5, @Param("exception") String paramString6);

    int insertLogTemp(@Param("id") String paramString1, @Param("ts") Timestamp paramTimestamp,
            @Param("user_id") String paramString2, @Param("type") LogType paramLogType);

    int updateLogNormal(@Param("id") String paramString1, @Param("target_id") String paramString2,
            @Param("context") String paramString3, @Param("status") String paramString4);

    int updateLogException(@Param("id") String paramString1, @Param("target_id") String paramString2,
            @Param("context") String paramString3, @Param("status") String paramString4,
            @Param("exception") String paramString5);
}
