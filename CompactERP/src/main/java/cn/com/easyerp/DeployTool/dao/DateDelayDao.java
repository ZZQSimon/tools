package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.DeployTool.service.Passage;
import cn.com.easyerp.DeployTool.service.PassageRow;
import cn.com.easyerp.DeployTool.service.PassageRowFormula;

@Repository
public interface DateDelayDao {
    List<Passage> selectPassage();

    List<PassageRow> selectPassageRow();

    List<PassageRowFormula> selectPassageRowFormula();

    void addDateDelay(@Param("passageList") List<Passage> paramList);

    void updDateDelay(@Param("passageList") List<Passage> paramList);

    void delDateDelay(@Param("passageList") List<Passage> paramList);

    void addPassageRow(@Param("passageRowList") List<PassageRow> paramList);

    void updPassageRow(@Param("passageRowList") List<PassageRow> paramList);

    void delPassageRow(@Param("passageRowList") List<PassageRow> paramList);

    void addPassageRowFormula(@Param("passageRowFormulaList") List<PassageRowFormula> paramList);

    void updPassageRowFormula(@Param("passageRowFormulaList") List<PassageRowFormula> paramList);

    void delPassageRowFormula(@Param("passageRowFormulaList") List<PassageRowFormula> paramList);
}
