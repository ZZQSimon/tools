package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import javax.websocket.server.PathParam;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.International;
import cn.com.easyerp.core.data.DatabaseDataMap;

@Repository
public interface InternationalDao {
    Integer getInternationalCount();

    void addInternational(@PathParam("list") List<International> paramList);

    void updInternational(@PathParam("list") List<International> paramList);

    void delInternational(@PathParam("list") List<International> paramList);

    List<International> selectLikeInternational(@Param("params") String paramString,
            @Param("start") Integer paramInteger1, @Param("end") Integer paramInteger2);

    int countInternational();

    List<International> retrievalInter(@Param("retrievalValue") String paramString1,
            @Param("language_id") String paramString2);

    int saveRetrievalInter(@Param("international") International paramInternational);

    int deleteRetrievalInter(@Param("international") International paramInternational);

    void I18NReplace(@Param("findContext") String paramString1, @Param("replaceWith") String paramString2,
            @Param("scope") String paramString3);

    List<International> selectInternational(@Param("start") Integer paramInteger1, @Param("end") Integer paramInteger2);

    void I18nUpdate(@Param("i18n") International paramInternational);

    int isExistsi18n(@Param("international_id") String paramString);

    List<DatabaseDataMap> selectI18nWithCondition(@Param("params") String paramString);
}
