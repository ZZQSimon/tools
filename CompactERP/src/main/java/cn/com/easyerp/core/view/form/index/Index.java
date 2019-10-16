package cn.com.easyerp.core.view.form.index;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.service.DashboardService;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.approve.ApproveService;
import cn.com.easyerp.core.approve.MyApproveAndWaitMeApprove;
import cn.com.easyerp.core.cache.OperationDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.IndexDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.velocity.DxToolService;
import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.grid.RecordModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
public class Index extends FormViewControllerBase {
    @Autowired
    private AuthService authService;
    @Autowired
    private IndexService indexService;
    @Autowired
    private DataService dataService;
    @Autowired
    private IndexDao indexDao;

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @RequestMapping({ "/index.view" })
    public ModelAndView index(AuthDetails user, HttpSession session) throws ParseException {
        int heightY;
        this.dashService.checkUserDashboard(user);

        List<DesktopItemModel> personDesktops = this.indexDao.selectdata(user.getId());
        DataModel dataModel = desktop(personDesktops, user, true);
        List<Map<String, Object>> personDesk = dataModel.getDesktopDate();
        Map<String, DesktopListModel> listMap = dataModel.getListMap();
        int personDeskY = dataModel.getHeightY();
        Map<String, Object> map = new HashMap<String, Object>();

        List<DesktopItemModel> commonDesktops = this.indexDao.selectdata("");
        dataModel = desktop(commonDesktops, user, false);
        listMap.putAll(dataModel.getListMap());
        List<Map<String, Object>> commonDesk = dataModel.getDesktopDate();
        int commonDeskY = dataModel.getHeightY();

        if (personDeskY >= commonDeskY) {
            heightY = (personDeskY + 1) * 155;
        } else {
            heightY = (commonDeskY + 1) * 155;
        }

        map.put("other", Integer.valueOf(personDeskY * 155));
        personDesk.add(map);
        map = new HashMap<String, Object>();
        map.put("other", Integer.valueOf(commonDeskY * 155));
        commonDesk.add(map);

        IndexModel form = new IndexModel(commonDesk, personDesk, heightY);
        form.setListMap(listMap);
        form.setSysCompanyName((session.getAttribute("SysCompanyName") == null) ? ""
                : session.getAttribute("SysCompanyName").toString());
        form.setSysLogo((session.getAttribute("SysLogo") == null) ? "" : session.getAttribute("SysLogo").toString());
        form.setInterior_logo((session.getAttribute("interior_logo") == null) ? ""
                : session.getAttribute("interior_logo").toString());

        Object emailDetail = session.getAttribute("emailDetailParam");
        if (emailDetail != null) {
            form.setEmailDetail((Map) emailDetail);
        }
        return buildModelAndView(form);
    }

    @Autowired
    private IndexCacheService indexCacheService;
    @Autowired
    private DxToolService dxToolService;
    @Autowired
    private ApproveService approveService;
    // @Autowired
    // private SystemDao systemDao;
    @Autowired
    private DashboardService dashService;

    @RequestMapping({ "/selectApproveDatas.view" })
    public ModelAndView selectApproveDatas(AuthDetails user) {
        IndexApproveModel form = new IndexApproveModel();
        List<MyApproveAndWaitMeApprove> myApprove = this.approveService.selectMyApprove(user);
        List<MyApproveAndWaitMeApprove> waitMeApprove = this.approveService.selectWaitMeApprove(user);
        form.setMyApprove(myApprove);
        form.setWaitMeApprove(waitMeApprove);
        if (myApprove == null) {
            form.setMyApproveSize(0);
        } else {
            form.setMyApproveSize(myApprove.size());
        }
        if (waitMeApprove == null) {
            form.setWaitMeApproveSize(0);
        } else {
            form.setWaitMeApproveSize(waitMeApprove.size());
        }
        return buildModelAndView(form);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public MatterDeskTopModel matterItem(AuthDetails user) {
        MatterDeskTopModel ret = new MatterDeskTopModel();

        List<OpRecordModel> records = new ArrayList<OpRecordModel>();

        List<IndexStatusModel> datas = this.indexDao.selectActionLogData();

        Map<String, AuthDetails> userCache = new HashMap<String, AuthDetails>();
        Set<String> uids = new HashSet<String>();
        for (IndexStatusModel data : datas)
            uids.add(data.getCre_user_id());
        if (uids.size() > 0) {
            for (AuthDetails u : this.authService.loadUsers(uids)) {
                userCache.put(u.getId(), u);
            }
        }
        Map<String, Map<String, Map<String, IndexStatusModel>>> dataMap = new HashMap<String, Map<String, Map<String, IndexStatusModel>>>();
        for (IndexStatusModel data : datas) {
            String tableName = data.getTable_id();
            String columnName = data.getStatus_col();

            String key = Common.makeMapKey(tableName, columnName, new String[] { data.getStatus_now() });
            List<OperationDescribe> ops = (List) this.indexCacheService.getEntry(key);
            List<String> action_ids = new ArrayList<String>();
            for (OperationDescribe op : ops) {
                if (op.isBacklog_event()) {
                    AuthDetails u = (AuthDetails) userCache.get(data.getCre_user_id());
                    if (this.authService.optionAuth(user, op.getTable_id(), op.getId(), u.getId()))
                        action_ids.add(op.getId());
                }
            }
            if (action_ids.size() == 0)
                continue;
            data.setAction_ids(action_ids);
            Map<String, Map<String, IndexStatusModel>> tableMap = (Map) dataMap.get(data.getTable_id());
            if (tableMap == null)
                dataMap.put(data.getTable_id(), tableMap = new HashMap<String, Map<String, IndexStatusModel>>());
            String keyValue = data.getKey_value();
            Map<String, IndexStatusModel> valueMap = (Map) tableMap.get(keyValue);
            if (valueMap == null)
                tableMap.put(keyValue, valueMap = new HashMap<String, IndexStatusModel>());
            valueMap.put(data.getStatus_col(), data);
        }

        for (Map.Entry<String, Map<String, Map<String, IndexStatusModel>>> tableEntry : dataMap.entrySet()) {
            String tableName = (String) tableEntry.getKey();
            TableDescribe table = this.dataService.getTableDesc(tableName);
            if (table.getTable_type() == 1) {
                tableName = table.getView_main_table();
                table = this.dataService.getTableDesc(tableName);
            }
            String keyColumn = table.getIdColumns()[0];

            Map<String, Map<String, IndexStatusModel>> valueMap = (Map) tableEntry.getValue();
            String where = tableName + '.' + keyColumn + " in (" + Common.join(valueMap.keySet()) + ")";

            List<Map<String, Object>> dataList = this.dataService.selectRecordsByValues(tableName, null, (Map) null,
                    where);

            for (Map<String, Object> map : dataList) {
                for (IndexStatusModel model : (valueMap.get(map.get(keyColumn).toString())).values()) {
                    RecordModel keyModel = new RecordModel(this.dataService.buildModel(tableName, map));
                    OpRecordModel record = new OpRecordModel(keyModel.getFields());

                    String statusCol = model.getStatus_col();
                    record.setStatus_col(statusCol);
                    record.setStatus_now(map.get(statusCol).toString());
                    record.setTable_id(tableName);
                    record.setKey_value(map.get(keyColumn).toString());
                    record.setCre_date(model.getCre_date());
                    record.setCre_user(model.getCre_user());
                    record.setAction_ids(model.getAction_ids());
                    record.setDisp_table_id(tableName);
                    records.add(record);
                }
            }
        }
        ret.setMatterDesk(records);
        return ret;
    }

    @ResponseBody
    @RequestMapping({ "/index/refreshTodo.do" })
    public ActionResult reloadTodo(AuthDetails user, @RequestBody FormRequestModelBase<IndexModel> request,
            HttpServletRequest req, HttpServletResponse resp) {
        MatterDeskTopModel matter = matterItem(user);
        MatterDeskTopModel orig = null;
        IndexModel form = (IndexModel) request.getWidget();
        for (Map<String, Object> map : form.getCommonDesk()) {
            if (map.get("matter") != null) {
                orig = (MatterDeskTopModel) map.get("matter");
                orig.setMatterDesk(matter.getMatterDesk());
            }
        }
        Map<String, Object> map = new HashMap<String, Object>(3);
        map.put("matter", orig);
        map.put("key", "common");
        String html = this.dxToolService.macro("dxMatter", map, req, resp);

        Map<String, Object> ret = new HashMap<String, Object>(2);
        ret.put("html", html);
        ret.put("matter", matter);

        ViewService.cacheRequestWidgets(form);
        return new ActionResult(true, ret);
    }

    public DataModel desktop(List<DesktopItemModel> desktops, AuthDetails user, boolean personal)
            throws ParseException {
        int heigthY = 145;
        int widthX = 245;
        int maxRow = 0;
        List<Map<String, Object>> redata = new ArrayList<Map<String, Object>>();
        Map<String, DesktopListModel> listMap = new HashMap<String, DesktopListModel>();
        String userId = personal ? user.getId() : "";
        for (DesktopItemModel desktop : desktops) {
            Map<String, Object> map = new HashMap<String, Object>();

            if (desktop.getEnd_row() + 1 > maxRow) {
                maxRow = desktop.getEnd_row() + 1;
            }
            if (!desktop.getBulletin_id().isEmpty()) {

                BullModel bull = this.indexService.selectBullList(userId, desktop.getBulletin_id());
                if (bull != null) {
                    int start_col = bull.getStart_col() * (widthX + 10) + 10;
                    int start_row = bull.getStart_row() * (heigthY + 10);
                    bull.setHeight((bull.getEnd_row() - bull.getStart_row() + 1) * heigthY
                            + HeightAndWidth(bull.getEnd_row(), bull.getStart_row()));
                    bull.setWidth((bull.getEnd_col() - bull.getStart_col() + 1) * widthX
                            + HeightAndWidth(bull.getEnd_col(), bull.getStart_col()));
                    bull.setStart_col(start_col);
                    bull.setStart_row(start_row);
                    map.put("bull", bull);
                }
            } else if (!desktop.getWork_plan_id().isEmpty()) {

                WorkModel work = this.indexService.selectWorkList(userId, desktop.getWork_plan_id());
                if (work != null) {
                    map.put("work", workReset(work));
                }
            } else if (!desktop.getDate_reminder_id().isEmpty()) {

                Map<String, Object> relist = this.indexService.getReList(userId, desktop.getDate_reminder_id());
                if (relist != null) {
                    int start_col = ((Integer) relist.get("start_col")).intValue() * (widthX + 10) + 10;
                    int start_row = ((Integer) relist.get("start_row")).intValue() * (heigthY + 10);
                    relist.put("height",
                            Integer.valueOf((((Integer) relist.get("end_row")).intValue()
                                    - ((Integer) relist.get("start_row")).intValue() + 1) * heigthY
                                    + HeightAndWidth(((Integer) relist.get("end_row")).intValue(),
                                            ((Integer) relist.get("start_row")).intValue())));
                    relist.put("width",
                            Integer.valueOf((((Integer) relist.get("end_col")).intValue()
                                    - ((Integer) relist.get("start_col")).intValue() + 1) * widthX
                                    + HeightAndWidth(((Integer) relist.get("end_col")).intValue(),
                                            ((Integer) relist.get("start_col")).intValue())));
                    relist.put("start_col", Integer.valueOf(start_col));
                    relist.put("start_row", Integer.valueOf(start_row));
                    map.put("relist", listReset(relist));
                }
            } else if (!desktop.getChart_id().isEmpty()) {

                DeskModel chart = this.indexService.selectChartList(userId, desktop.getChart_id());
                if (chart != null) {
                    int start_col = chart.getStart_col() * (widthX + 10) + 10;
                    int start_row = chart.getStart_row() * (heigthY + 10);
                    chart.setHeight((chart.getEnd_row() - chart.getStart_row() + 1) * heigthY
                            + HeightAndWidth(chart.getEnd_row(), chart.getStart_row()));
                    chart.setWidth((chart.getEnd_col() - chart.getStart_col() + 1) * widthX
                            + HeightAndWidth(chart.getEnd_col(), chart.getStart_col()));
                    chart.setStart_col(start_col);
                    chart.setStart_row(start_row);
                    map.put("chart", chart);
                }
            } else if (!desktop.getShortcut_id().isEmpty()) {

                ShortModel shct = this.indexService.selectShortList(userId, desktop.getShortcut_id());
                if (shct != null) {
                    int start_col = shct.getStart_col() * (widthX + 10) + 10;
                    int start_row = shct.getStart_row() * (heigthY + 10);
                    shct.setHeight((shct.getEnd_row() - shct.getStart_row() + 1) * heigthY
                            + HeightAndWidth(shct.getEnd_row(), shct.getStart_row()));
                    shct.setWidth((shct.getEnd_col() - shct.getStart_col() + 1) * widthX
                            + HeightAndWidth(shct.getEnd_col(), shct.getStart_col()));
                    shct.setStart_col(start_col);
                    shct.setStart_row(start_row);
                    map.put("shct", shct);
                }
            } else if ("60".equals(desktop.getItem_type())) {
                int start_col = desktop.getStart_col() * (widthX + 10) + 10;
                int start_row = desktop.getStart_row() * (heigthY + 10);
                MatterDeskTopModel matter = matterItem(user);
                matter.setStart_col(start_col);
                matter.setStart_row(start_row);
                matter.setHeight((desktop.getEnd_row() - desktop.getStart_row() + 1) * heigthY
                        + HeightAndWidth(desktop.getEnd_row(), desktop.getStart_row()));
                matter.setWidth((desktop.getEnd_col() - desktop.getStart_col() + 1) * widthX
                        + HeightAndWidth(desktop.getEnd_col(), desktop.getStart_col()));
                matter.setBackground_color(desktop.getBackground_color());
                map.put("matter", matter);
            } else if ("70".equals(desktop.getItem_type())) {
                DesktopListModel list = this.indexDao.selectDesktopList(desktop.getList_id());
                list.extend(desktop);
                map.put("list", list);
                listMap.put(list.getId(), list);
            }
            redata.add(map);
        }

        DataModel dataModel = new DataModel();
        dataModel.setDesktopDate(redata);
        dataModel.setHeightY(maxRow);
        dataModel.setListMap(listMap);
        return dataModel;
    }

    public int HeightAndWidth(int end, int start) {
        if (end - start > 0) {
            return (end - start) * 10;
        }
        return 0;
    }

    public Map<String, Object> listReset(Map<String, Object> relist) {
        List<Map<String, Object>> listReminder = this.indexService.getSuserList(relist.get("table_id").toString(),
                relist.get("column_name").toString(), relist.get("name_column").toString(), relist.get("id").toString(),
                relist.get("type").toString());
        List<Map<String, String>> listData = new ArrayList<Map<String, String>>();
        for (Map<String, Object> mapData : listReminder) {
            Map<String, String> data = new HashMap<String, String>();

            Object objName = mapData.get(relist.get("name_column"));
            Object objTime = mapData.get(relist.get("column_name"));
            String time = objTime.toString().substring(0, 10);
            data.put("time", time);
            data.put("name", objName.toString());
            listData.add(data);
        }
        relist.put("listDate", listData);
        return relist;
    }

    public WorkModel workReset(WorkModel work) throws ParseException {
        int height = 145;
        int width = 245;

        int start_col = work.getStart_col() * (width + 10) + 10;
        int start_row = work.getStart_row() * (height + 10);
        work.setHeight((work.getEnd_row() - work.getStart_row() + 1) * height
                + HeightAndWidth(work.getEnd_row(), work.getStart_row()));
        work.setWidth((work.getEnd_col() - work.getStart_col() + 1) * width
                + HeightAndWidth(work.getEnd_col(), work.getStart_col()));
        work.setStart_col(start_col);
        work.setStart_row(start_row);

        String sq = this.indexService.getValueBySql(work.getSql());
        if (sq == null) {
            sq = "0";
        }
        work.setSql(sq);
        double s = Double.valueOf(work.getSql()).doubleValue();
        if (work.getPlan() == null) {
            work.setPlan("0");
        }
        double p = Double.valueOf(work.getPlan()).doubleValue();
        String sql = String.valueOf(s / p * 100.0D);
        BigDecimal bigsql = new BigDecimal(sql);
        BigDecimal pla = new BigDecimal(p);
        BigDecimal bigplan = new BigDecimal("100.00");
        String plan = String.valueOf(bigplan.subtract(bigsql.setScale(2, 4)));

        work.setSql_Number(bigsql.setScale(2, 4).toString());
        work.setPlan_Number(plan);
        work.setPlan(pla.setScale(2, 4).toString());
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String time = format.format(date);
        work.setTime(time);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        long end = df.parse(work.getEnd_date()).getTime();
        long start = df.parse(work.getStart_date()).getTime();
        long now = df.parse(time).getTime();
        long se = (end - start) / 86400000L;
        long sn = (now - start) / 86400000L;
        String timeNow = String.valueOf(sn * 100L / se);
        String timeEnd = String.valueOf(100L - sn * 100L / se);
        work.setTimeEnd(timeEnd);
        work.setTimeNow(timeNow);

        return work;
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/selectStatus.do" })
    public ActionResult grid(@RequestBody Map<String, Object> param) {
        String tableName = param.get("table").toString();
        TableDescribe table = this.dataService.getTableDesc(tableName);
        if (1 == table.getTable_type()) {
            tableName = table.getView_main_table();
            table = this.dataService.getTableDesc(table.getView_main_table());
        }
        String key = table.getIdColumns()[0];
        String statusName = this.indexDao.selectStatus(param.get("column_name").toString(), tableName, key,
                param.get("key_value").toString());

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("statusName", statusName);

        return new ActionResult(true, map);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @RequestMapping({ "/mobileIndex.view" })
    public ModelAndView mobileIndex(AuthDetails user, String state, HttpSession session) {
        user.setIsMobileLogin(1);
        IndexModel form = new IndexModel();
        Map<String, List<AuthDetails.UserMenu>> mobileMenu = new HashMap<String, List<AuthDetails.UserMenu>>();
        for (AuthDetails.UserMenu menu : user.getMoblieMenu()) {
            mobileMenu.put(menu.getInternational_id(), menu.getSub());
        }

        List<MyApproveAndWaitMeApprove> myApprove = this.approveService.selectMyApprove(user);
        List<MyApproveAndWaitMeApprove> waitMeApprove = this.approveService.selectWaitMeApprove(user);
        form.setMyApprove(myApprove);
        form.setWaitMeApprove(waitMeApprove);
        if (myApprove == null) {
            form.setMyApproveSize(0);
        } else {
            form.setMyApproveSize(myApprove.size());
        }
        if (waitMeApprove == null) {
            form.setWaitMeApproveSize(0);
        } else {
            form.setWaitMeApproveSize(waitMeApprove.size());
        }

        form.setMobileMenu(mobileMenu);
        form.setApproveParam((Map) session.getAttribute("approveParam"));
        session.removeAttribute("approveParam");
        return new ModelAndView("mobileIndex", "form", form);
    }

    @ResponseBody
    @RequestMapping({ "/mobileApproveFlush.do" })
    public ActionResult mobileApproveFlush(AuthDetails user) {
        Map<String, Object> approveData = new HashMap<String, Object>();
        List<MyApproveAndWaitMeApprove> myApprove = this.approveService.selectMyApprove(user);
        List<MyApproveAndWaitMeApprove> waitMeApprove = this.approveService.selectWaitMeApprove(user);
        approveData.put("myApprove", myApprove);
        approveData.put("waitMeApprove", waitMeApprove);
        if (myApprove == null) {
            approveData.put("myApproveSize", Integer.valueOf(0));
        } else {
            approveData.put("myApproveSize", Integer.valueOf(myApprove.size()));
        }
        if (waitMeApprove == null) {
            approveData.put("waitMeApproveSize", Integer.valueOf(0));
        } else {
            approveData.put("waitMeApproveSize", Integer.valueOf(waitMeApprove.size()));
        }
        return new ActionResult(true, approveData);
    }
}
