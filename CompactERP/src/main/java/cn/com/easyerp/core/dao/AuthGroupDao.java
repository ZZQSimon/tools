package cn.com.easyerp.core.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.authGroup.AuthDataGroupDetail;
import cn.com.easyerp.core.authGroup.AuthGroup;
import cn.com.easyerp.core.authGroup.MenuGroup;
import cn.com.easyerp.core.authGroup.Table;
import cn.com.easyerp.core.authGroup.TableAction;
import cn.com.easyerp.core.widget.menu.MenuModel;

@Repository
public interface AuthGroupDao {
    List<MenuGroup> selectMenuName();

    List<MenuGroup> selectMenu(@Param("menuGroupId") String paramString);

    List<MenuModel> selectAllMenu();

    List<MenuModel> selectAllLeafMenu();

    List<Table> selectTable();

    int deleteMenuGroup(@Param("menuGroupId") String paramString);

    int addMenuGroup(@Param("menuGroupId") String paramString, @Param("menuId") List<String> paramList);

    int addAuthGroup(@Param("authGroup") List<AuthGroup> paramList);

    List<AuthGroup> selectMember(@Param("authGroup") AuthGroup paramAuthGroup);

    int deleteAuthGroup(@Param("authGroup") AuthGroup paramAuthGroup);

    int deleteAuthDataGroup(@Param("authGroup") AuthGroup paramAuthGroup);

    int updateMenuGroup(@Param("menuGroupId") String paramString1, @Param("upMenuGroupId") String paramString2);

    int updateAuthGroup(@Param("authGroup") AuthGroup paramAuthGroup, @Param("upMenuGroupId") String paramString);

    List<Table> selectTableByColumn();

    List<AuthGroup> selectTemplate(@Param("type") int paramInt);

    List<TableAction> selectAllCheckBox(@Param("table_id") String paramString);

    List<AuthDataGroupDetail> selectAuthDataGroup(@Param("data_group_id") String paramString);

    Integer addAuthDataGroup(@Param("authDataGroup") AuthDataGroupDetail paramAuthDataGroupDetail);

    Integer addAuthDataGroupDetail(@Param("authDataGroupDetail") AuthDataGroupDetail paramAuthDataGroupDetail);

    Integer selectImportUpdate(@Param("authGroup") AuthGroup paramAuthGroup);

    Integer selectImportInsert(@Param("authGroup") AuthGroup paramAuthGroup);
}
