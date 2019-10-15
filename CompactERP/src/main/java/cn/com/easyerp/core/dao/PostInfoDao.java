package cn.com.easyerp.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PostInfoDao {
    List<Map<String, Object>> selectPostInfo();

    int addPostInfo(@Param("id") String paramString1, @Param("name") String paramString2,
            @Param("userId") String paramString3, @Param("date") Date paramDate);

    int deletePostInfo(@Param("id") String paramString);

    int selectPostById(@Param("id") String paramString);

    String getPostInfoId(@Param("table") String paramString1, @Param("parentTable") String paramString2);
}
