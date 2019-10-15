package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.widget.grid.UserColumn;

@Repository
public interface UserColumnDao {
    List<UserColumn> selectUserColumnByTableName(@Param("tableName") String paramString1,
            @Param("userId") String paramString2);

    void deleteUserColumn(@Param("tableName") String paramString1, @Param("userId") String paramString2);

    void insertUserColumn(@Param("UserColumns") List<UserColumn> paramList, @Param("userId") String paramString);

    List<UserColumn> selectUserColumnByUser(@Param("userId") String paramString);
}
