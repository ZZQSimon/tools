package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.Url;

@Repository
public interface UrlDao {
    List<Url> findAllUrl();

    void addUrl(@Param("list") List<Url> paramList);

    void updUrl(@Param("list") List<Url> paramList);

    void deleteUrl(@Param("list") List<Url> paramList);
}
