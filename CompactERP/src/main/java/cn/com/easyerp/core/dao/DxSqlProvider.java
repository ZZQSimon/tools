package cn.com.easyerp.core.dao;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.jdbc.SQL;

import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.common.StringReplacer;
import cn.com.easyerp.framework.common.StringReplacerCallback;

public class DxSqlProvider {
    public static final String SQL_KEY = "SQL_KEY";
    public static final String TABLE_KEY = "TABLE_KEY";
    public static final String TERM_KEY = "term";
    public static final String TOPS_KEY = "tops";
    public static final Pattern DX_FUNC_PATTERN;

    private static String replaceVars(String sql, final Map<String, Object> param) {
        sql = Common.replaceVars(sql, (Map) param, true);
        return StringReplacer.replace(sql, DxSqlProvider.DX_FUNC_PATTERN,
                (StringReplacerCallback) new FunctionReplacer(param));
    }

    public static String sqlDynamic(final Map<String, Object> param) {
        return replaceVars((String) param.get("SQL_KEY"), param);
    }

    public static String selectListFromCustomSql(final Map<String, Object> param) {
        final TableDescribe table = Common.getDataService().getTableDesc((String) param.get("TABLE_KEY"));
        boolean comma = false;
        final String col = table.getIdColumns()[table.getIdColumns().length - 1];
        final String name = table.getName_column();
        String term = (String) param.get("term");
        term = term.replace("'", "''");
        final StringBuilder sb = new StringBuilder("select ");
        sb.append(' ');
        for (final ColumnDescribe column : table.getColumns()) {
            if (!column.isVirtual()) {
                if (comma) {
                    sb.append(",");
                } else {
                    comma = true;
                }
                sb.append("`" + column.getColumn_name() + "`");
            }
        }
        sb.append(" from (").append(param.get("SQL_KEY")).append(") ");
        sb.append(param.get("TABLE_KEY"));
        if (param.get("where") != null) {
            sb.append(" where ");
            sb.append(param.get("where"));
            sb.append(" ");
        }
        sb.append(" order by case when  ");
        if (Common.notBlank(name)) {
            sb.append("`" + name + "`").append(" like '%").append(term).append("%' or ");
        }
        sb.append("`" + col + "`").append(" like '%").append(term).append("%' then 0 else 1 end, ")
                .append("`" + col + "`");
        sb.append(" limit 0,");
        sb.append(param.get("tops"));
        return replaceVars(sb.toString(), param);
    }

    public static String selectRecordFromCustomSql(final Map<String, Object> param) {
        final TableDescribe table = (TableDescribe) param.get("TABLE_KEY");
        param.put("term", param.get("term").toString().replace("'", "''"));
        final String sql = new SQL() {
            {
                for (final ColumnDescribe column : table.getColumns()) {
                    if (!column.isVirtual()) {
                        this.SELECT("`" + column.getColumn_name() + "`");
                    }
                }
                this.FROM("(" + param.get("SQL_KEY") + ") source");
                final String col = table.getIdColumns()[table.getIdColumns().length - 1];
                this.WHERE(String.format("%s = '%s'", col, param.get("term")));
            }
        }.toString();
        return replaceVars(sql, param);
    }

    static {
        DX_FUNC_PATTERN = Pattern
                .compile("@dx_([a-zA-Z0-9_]+)[ ]*\\([ ]*([a-zA-Z0-9_\\.]+)[ ]*,[ ]*([a-zA-Z0-9_]+)[ ]*\\)");
    }

    private static class FunctionReplacer implements StringReplacerCallback {
        private Map<String, Object> param;

        public FunctionReplacer(final Map<String, Object> param) {
            this.param = param;
        }

        public String replace(final Matcher matcher) {
            final String sqlName = matcher.group(2);
            final String paramName = matcher.group(3);
            final String group = matcher.group(1);
            switch (group) {
            case "in": {
                final List list = (List) this.param.get(paramName);
                if (list == null || list.size() == 0) {
                    return " 1 = 1 ";
                }
                StringBuilder sb = null;
                for (final Object val : list) {
                    if (sb == null) {
                        sb = new StringBuilder(" ").append(sqlName).append(" in (");
                    } else {
                        sb.append(',');
                    }
                    if (String.class.isInstance(val)) {
                        sb.append('\'');
                    }
                    sb.append(val.toString());
                    if (String.class.isInstance(val)) {
                        sb.append('\'');
                    }
                }
                sb.append(')');
                return sb.toString();
            }
            default: {
                return null;
            }
            }
        }
    }
}
