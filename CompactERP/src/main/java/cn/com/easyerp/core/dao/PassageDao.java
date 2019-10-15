package cn.com.easyerp.core.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import cn.com.easyerp.passage.PassageDescribe;
import cn.com.easyerp.passage.PassageRowDescribe;
import cn.com.easyerp.passage.PassageRowFormulaModel;

@Repository
public interface PassageDao {
    List<PassageDescribe> selectPassages();

    List<PassageRowDescribe> selectPassageRows();

    List<PassageRowFormulaModel> selectPassageRowFormulas();
}
