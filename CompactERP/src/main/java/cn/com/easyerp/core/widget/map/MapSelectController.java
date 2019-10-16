package cn.com.easyerp.core.widget.map;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.cache.ColumnDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.cache.style.ColumnMapViewStyle;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.MapFieldModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/widget/map" })
public class MapSelectController extends FormViewControllerBase {
    @Autowired
    private DataService dataService;
    @Autowired
    private SystemDao systemDao;
    // @Autowired
    // private FilterService filterService;
    // private static final String MAP_SIZE = "8";

    @SuppressWarnings({ "rawtypes" })
    @RequestMapping({ "/select.view" })
    public ModelAndView form(@RequestBody MapSelectRequestModel request) {
        MapFieldModel field = (MapFieldModel) ViewService.fetchFieldModel(request.getParent());
        ColumnDescribe desc = this.dataService.getColumnDesc(field);
        ColumnMapViewStyle map = desc.getViewStyle().getMap();
        String tableName = map.getTarget();

        String filter = makeExecSQL(map.getFilter(), tableName, request.getFilterData());

        List<Map<String, Object>> list = this.systemDao.execSql(filter);

        LinkedHashMap<String, String> all = new LinkedHashMap<String, String>(list.size());
        String nameColumn = map.getTargetName();
        String idColumn = map.getTargetId();
        for (Map item : list) {
            String key = (String) item.get(idColumn);
            String value = Common.notBlank(nameColumn) ? (String) item.get(nameColumn) : null;
            all.put(key, value);
        }
        LinkedHashSet<String> selection = new LinkedHashSet<String>(field.getSelection().keySet());

        TableDescribe target = this.dataService.getTableDesc(tableName);

        MapSelectFormModel form = new MapSelectFormModel(request.getParent(), selection, all,
                this.dataService.i18nString(target.getColumn(idColumn).getI18n()),
                Common.notBlank(nameColumn) ? this.dataService.i18nString(target.getColumn(nameColumn).getI18n())
                        : null);
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/save.do" })
    public ActionResult save(@RequestBody MapSelectRequestModel request) {
        MapFieldModel field = (MapFieldModel) ViewService.fetchFieldModel(request.getId());
        if (Common.isBlank((String) field.getValue()))
            field.setValue(field.getId());
        field.setSelection(request.getSelection());
        return Common.ActionOk;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private String makeExecSQL(String filter, String tableName, Map<String, Object> filterData) {
        String result = filter;
        TableDescribe tableDesc = this.dataService.getTableDesc(tableName);
        if (Common.notBlank(filter)) {
            Iterator<Map.Entry<String, Object>> iterator = filterData.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, Object> next = (Map.Entry) iterator.next();
                String key = (String) next.getKey();
                String value = next.getValue().toString();
                if (!"input_map_filter".equals(key)) {
                    result = result.replace("${" + key + "}", "'" + value + "'");
                    result = result.replace("#{" + key + "}", "'" + value + "'");
                    continue;
                }
                result = "select * from (" + result + ") t_filter where t_filter."
                        + tableDesc.getIdColumns()[tableDesc.getIdColumns().length - 1];
                result = result + " like '%" + value + "%'";
                result = result + " or t_filter." + tableDesc.getName_column() + " like '%" + value + "%'";
            }

            result = result + " limit 0, " + "8";
        }
        return result;
    }
}
