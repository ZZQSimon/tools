package cn.com.easyerp.core.data;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;

public class ReferenceModel {
    private ColumnDescribe column;
    private String alias;
    private TableDescribe child;
    private String query;
    private String join = "left join";

    public ReferenceModel(ColumnDescribe column, String alias, TableDescribe child) {
        this(column, alias, child, null, null);
    }

    public ReferenceModel(ColumnDescribe column, String alias, TableDescribe child, String query, String join) {
        this.column = column;
        this.alias = alias;
        this.child = child;
        this.query = query;
        if (join != null) {
            this.join = join;
        }
    }

    public ColumnDescribe getColumn() {
        return this.column;
    }

    public String getAlias() {
        return this.alias;
    }

    public TableDescribe getChild() {
        return this.child;
    }

    public String getQuery() {
        return this.query;
    }

    public String getJoin() {
        return this.join;
    }
}
