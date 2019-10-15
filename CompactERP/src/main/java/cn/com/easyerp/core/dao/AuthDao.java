package cn.com.easyerp.core.dao;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.auth.AuthConfigDescribe;
import cn.com.easyerp.auth.AuthControlDescribe;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthDetailsAdditional;
import cn.com.easyerp.auth.AuthOption;
import cn.com.easyerp.auth.DepartmentDescribe;
import cn.com.easyerp.auth.SysMasterDetails;
import cn.com.easyerp.auth.weixin.CompanyWXData;
import cn.com.easyerp.core.cache.DictionaryDescribe;
import cn.com.easyerp.core.widget.menu.MenuModel;
import cn.com.easyerp.core.widget.menu.PageModel;

@Repository
public interface AuthDao {
    AuthDetails selectUser(@Param("id") String paramString1, @Param("mobile") boolean paramBoolean,
            @Param("encryptStr") String paramString2);

    List<AuthDetailsAdditional> selectUserAdditional(@Param("user_id") String paramString);

    AuthDetails selectSysUser(@Param("id") String paramString1, @Param("mobile") boolean paramBoolean,
            @Param("domain") String paramString2);

    int selectSysUserCnt(@Param("id") String paramString1, @Param("mobile") boolean paramBoolean,
            @Param("domain") String paramString2);

    List<Map<String, Object>> selectUserCompany(@Param("id") String paramString);

    List<AuthDetails> selectUsers(@Param("ids") Set<String> paramSet);

    List<String> selectUserDepts(@Param("id") String paramString);

    List<String> selectUserRoles(@Param("id") String paramString);

    List<MenuModel> selectMenu();

    List<PageModel> selectPages();

    List<AuthControlDescribe> selectAuthCtrl();

    List<AuthConfigDescribe> selectAuthConfig();

    List<AuthOption> selectOptions(@Param("table") String paramString);

    int updateAuthControl(@Param("id") String paramString1, @Param("relationship") String paramString2);

    List<AuthControlDescribe> selectAuthEditCtrl(@Param("notMenu") boolean paramBoolean);

    List<AuthConfigDescribe> selectAuthMenuConfig();

    int deleteAuthMenuConfig(@Param("controls") List<AuthControlDescribe> paramList);

    int insertAuthMenuConfig(@Param("controlId") String paramString1, @Param("menuId") String paramString2);

    int saveUser(@Param("authDetail") AuthDetails paramAuthDetails, @Param("date") Date paramDate);

    int saveCompanyUser(@Param("authDetail") AuthDetails paramAuthDetails, @Param("date") Date paramDate);

    int saveCompanyUser_admin(@Param("authDetail") AuthDetails paramAuthDetails, @Param("date") Date paramDate,
            @Param("companyId") String paramString);

    List<SysMasterDetails> selectCorp();

    int saveCompany(@Param("corp") SysMasterDetails paramSysMasterDetails);

    int saveUserCompany(@Param("telephone") String paramString1, @Param("companyId") String paramString2);

    int updateSysMaster(@Param("userId") String paramString1, @Param("masterId") String paramString2,
            @Param("companyId") String paramString3);

    List<SysMasterDetails> selecctSysMaster(@Param("name") String paramString);

    String getFilepath();

    String getDomainName();

    int findUserCompany(@Param("userId") String paramString1, @Param("companyId") String paramString2);

    int saveUserInfo(@Param("mobile") String paramString1, @Param("email") String paramString2,
            @Param("id") String paramString3, @Param("sex") String paramString4,
            @Param("telePhone") String paramString5, @Param("entryDate") Date paramDate1,
            @Param("date") Date paramDate2);

    int saveCompanyUserInfo(@Param("mobile") String paramString1, @Param("email") String paramString2,
            @Param("id") String paramString3, @Param("sex") String paramString4,
            @Param("telePhone") String paramString5, @Param("entryDate") Date paramDate1,
            @Param("date") Date paramDate2);

    int updateSysPassword(@Param("user") AuthDetails paramAuthDetails, @Param("password") String paramString1,
            @Param("domain") String paramString2);

    int updatePassword(@Param("user") AuthDetails paramAuthDetails, @Param("password") String paramString1,
            @Param("domain") String paramString2);

    CompanyWXData getCompanyWXData(@Param("master_id") String paramString);

    List<String> getUserCompanyName(@Param("mobile") String paramString);

    String getCompanyName(@Param("name") String paramString);

    DepartmentDescribe loadDept(@Param("dept_id") String paramString);

    String selectCompanyLanguage(@Param("company") String paramString);

    String selectCompanyDefaultLanguage();

    String selectLoginInternational(@Param("language") String paramString1,
            @Param("internationalId") String paramString2);

    List<Map<String, Object>> selectLoginInternationals(@Param("language") String paramString,
            @Param("internationalIds") List<String> paramList);

    String selectLoginCss(@Param("param") String paramString);

    List<AuthDetails> selectUserByOpenid(@Param("openid") String paramString1, @Param("domain") String paramString2);

    List<AuthDetails> selectUserBySapid(@Param("sapid") String paramString1, @Param("domain") String paramString2);

    AuthDetails selectUserByid(@Param("id") String paramString);

    AuthDetails checkLoginByPassword(@Param("id") String paramString1, @Param("_password") String paramString2,
            @Param("domain") String paramString3);

    void updateOpenid(@Param("id") String paramString1, @Param("openid") String paramString2,
            @Param("photo") String paramString3);

    List<String> getOpenids(@Param("userids") List<String> paramList);

    String getDmainByOpenid(@Param("openid") String paramString);

    List<String> userCompanys(@Param("username") String paramString);

    void updateSysOpenid(@Param("id") String paramString1, @Param("openid") String paramString2,
            @Param("domain") String paramString3);

    Map<String, Object> ldapParam();

    AuthDetails selectUserByLdap(@Param("ldapUserName") String paramString1, @Param("encryptStr") String paramString2);

    List<Map<String, Object>> selectDataGroup(@Param("tableName") String paramString,
            @Param("authDataGroup") Map<String, String> paramMap1, @Param("ids") Map<String, Object> paramMap2);

    List<Integer> selectApproveData(@Param("tableName") String paramString1, @Param("userId") String paramString2,
            @Param("id") Object paramObject);

    List<DictionaryDescribe> selectLanguage(@Param("language") String paramString);
}
