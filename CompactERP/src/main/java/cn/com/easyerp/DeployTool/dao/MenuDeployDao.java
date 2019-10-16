package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.MenuDeployDetails;

@Repository
public interface MenuDeployDao {
    List<MenuDeployDetails> selectMenu(@Param("userId") String paramString);

    List<MenuDeployDetails> selectMenuById(@Param("id") String paramString);

    boolean addMenuDeploy(@Param("menuDeploy") MenuDeployDetails paramMenuDeployDetails);

    boolean addPage(@Param("menuDeploy") MenuDeployDetails paramMenuDeployDetails);

    boolean addInternational(@Param("international") MenuDeployDetails paramMenuDeployDetails);

    boolean deleteMenuDeploy(@Param("id") String paramString);

    boolean deleteMenuDeployById(@Param("id") String paramString);

    boolean deletePage(@Param("id") String paramString);

    boolean updateMenuDeploy(@Param("menuDeploy") MenuDeployDetails paramMenuDeployDetails);

    boolean updateInternational(@Param("international") MenuDeployDetails paramMenuDeployDetails);
}
