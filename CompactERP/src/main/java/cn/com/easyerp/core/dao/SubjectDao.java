package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectDao {
    List<Map<String, Object>> selectSubject(@Param("subjectID") String paramString);

    int addBackground(@Param("backgroundImg") String paramString);
}
