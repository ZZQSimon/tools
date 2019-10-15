package cn.com.easyerp.core.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.framework.common.Common;

@Service
public class FilterService {
    @Autowired
    private DataService dataService;

    private String[] makeMultiAlias(final String tableName, final String column) {
        final String[] ret = Common.split(column, ",");
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = this.makeAlias(tableName, ret[i]);
        }
        return ret;
    }

    private String makeAlias(final String tableName, final String column) {
        return tableName + "." + column;
    }

    public String toWhere(final FilterRequestModel filter) {
        if (filter == null) {
            return null;
        }
        return this.toWhere(filter.getTableName(), filter.getFilters());
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String toWhere(final String tableName, final Map<String, FilterDescribe> filters) {
        if (filters == null) {
            return null;
        }
        final TableDescribe table = this.dataService.getTableDesc(tableName);
        String where = null;
        for (final Map.Entry<String, FilterDescribe> entry : filters.entrySet()) {
            final String column = entry.getKey();
            if (!"search".equals(column)) {
                String alias = null;
                String[] aliases = null;
                if (column.contains(",")) {
                    aliases = this.makeMultiAlias(tableName, column);
                } else {
                    alias = this.makeAlias(tableName, column);
                }
                final FilterDescribe data = entry.getValue();
                String condition = null;
                Object from = null;
                Object to = null;
                final ColumnDescribe desc = table.getColumn(column);
                switch (data.getType()) {
                case in: {
                    if (aliases != null) {
                        for (final String vstr : (List<String>) data.getValue()) {
                            if (condition == null) {
                                condition = "((";
                            } else {
                                condition += "or(";
                            }
                            final String[] values = Common.split(vstr, ",");
                            for (int i = 0; i < values.length; ++i) {
                                if (i > 0) {
                                    condition += " and ";
                                }
                                condition = condition + aliases[i] + " = '" + values[i] + "'";
                            }
                            condition += ")";
                        }
                        condition += ")";
                        break;
                    }
                    if (data.getValue() instanceof String) {
                        final ArrayList dataValue = (ArrayList) Common.fromJson(data.getValue().toString(),
                                (TypeReference) new TypeReference<ArrayList>() {
                                });
                        condition = alias + " in (" + Common.join((Collection) dataValue) + ")";
                        break;
                    }
                    condition = alias + " in (" + Common.join((Collection) data.getValue()) + ")";
                    break;
                }
                case like: {
                    condition = alias + " like '%" + data.getValue() + "%'";
                    break;
                }
                case between: {
                    if (desc.getData_type() == 4) {
                        alias = "date_format(" + alias + ", '%Y-%m-%d')";
                        if (data.getFrom() != null) {
                            from = Common.convertToDateText((long) data.getFrom());
                        }
                        if (data.getTo() != null) {
                            to = Common.convertToDateText((long) data.getTo());
                        }
                    } else if (desc.getData_type() == 12) {
                        alias = "date_format(" + alias + ", '%Y-%m-%d %H:%i:%s')";
                        if (data.getFrom() != null) {
                            from = Common.convertToDatetimeText((long) data.getFrom());
                        }
                        if (data.getTo() != null) {
                            to = Common.convertToDatetimeText((long) data.getTo() + 86400000L - 1L);
                        }
                    } else {
                        from = data.getFrom();
                        to = data.getTo();
                    }
                    if (from != null && to != null) {
                        condition = alias + " between '" + from + "' and '" + to + "'";
                        break;
                    }
                    if (from != null) {
                        condition = alias + " >= '" + from + "'";
                        break;
                    }
                    condition = alias + " <= '" + to + "'";
                    break;
                }
                case range: {
                    Object val;
                    if (desc.getData_type() == 4) {
                        from = "date_format(" + data.getFrom() + ", '%Y-%m-%d')";
                        to = "date_format(" + data.getTo() + ", '%Y-%m-%d')";
                        val = Common.convertToDateText((long) data.getValue());
                    } else if (desc.getData_type() == 12) {
                        from = "date_format(" + data.getFrom() + ", '%Y-%m-%d %H:%i:%s')";
                        to = "date_format(" + data.getTo() + ", '%Y-%m-%d %H:%i:%s')";
                        val = Common.convertToDatetimeText((long) data.getValue());
                    } else {
                        val = data.getValue();
                        from = data.getFrom();
                        to = data.getTo();
                    }
                    condition = "'" + val + "' between " + from + " and " + to;
                    break;
                }
                case eq: {
                    condition = alias + " = '" + ViewService.convertToDBValue(desc, data.getValue()) + "'";
                    break;
                }
                case tree: {
                    condition = alias + this.buildTreeCondition(desc.getRef_table_name(), (String) data.getValue());
                    break;
                }
                case query: {
                    condition = alias + this.buildQueryCondition(data);
                    break;
                }
                }
                if (where == null) {
                    where = condition;
                } else {
                    where = where + " and " + condition;
                }
            }
        }
        return where;
    }

    private String buildTreeCondition(final String treeTable, final String value) {
        return " in (select id from fn_tree_build('" + treeTable + "', '" + value + "', 'query'))";
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String buildQueryCondition(final FilterDescribe data) {
        String sql = data.getSql();
        if (sql != null) {
            sql = sql.replaceAll("&lt;", "<");
            sql = sql.replaceAll("&gt;", ">");
        }
        return " in (" + Common.replaceVars(sql, (Map) data.getValue(), true) + ")";
    }
}
