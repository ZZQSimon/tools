package cn.com.easyerp.passage;

import java.util.List;
import java.util.Map;

public class PassageFieldModel {
    private String id;
    private int level;
    private String cond;
    private int type;
    private String formula;
    private boolean sql;
    private List<Map<String, Object>> resultList;

    public PassageFieldModel(String id, int level, String cond, int type, String formula, boolean sql,
            List<Map<String, Object>> resultList) {
        this.id = id;
        this.level = level;
        this.cond = cond;
        this.type = type;
        this.formula = formula;
        this.sql = sql;
        this.resultList = resultList;
    }

    public String getId() {
        return this.id;
    }

    public int getLevel() {
        return this.level;
    }

    public String getCond() {
        return this.cond;
    }

    public int getType() {
        return this.type;
    }

    public String getFormula() {
        return this.formula;
    }

    public boolean isSQL() {
        return this.sql;
    }

    public List<Map<String, Object>> getResultList() {
        return this.resultList;
    }
}
