package cn.com.easyerp.core.widget.grid;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.logger.LogService;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.LogType;

@Controller
@RequestMapping({ "/widget/grid" })
public class GridController {
    @Autowired
    private GridService gridService;
    @Autowired
    private LogService logService;

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @RequestMapping({ "page.do" })
    public ActionResult page(@RequestBody GridRequestModel param, AuthDetails user) {
        GridModel grid = (GridModel) ViewService.fetchWidgetModel(param.getId());

        this.gridService.checkParam(grid, param);
        return new ActionResult(true, this.gridService.page(grid, param, user));
    }

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @RequestMapping({ "list.do" })
    public ActionResult filterList(@RequestBody GridRequestModel param, AuthDetails user) {
        GridModel gm = (GridModel) ViewService.fetchWidgetModel(param.getId());

        return new ActionResult(true, this.gridService.list(gm, param, user));
    }

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @RequestMapping({ "count.do" })
    public ActionResult filterCount(@RequestBody GridRequestModel param, AuthDetails user) {
        GridModel gm = (GridModel) ViewService.fetchWidgetModel(param.getId());
        return new ActionResult(true, Integer.valueOf(this.gridService.count(gm, param, user)));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Transactional
    @ResponseBody
    @RequestMapping({ "/delete.do" })
    public ActionResult delete(@RequestBody GridRequestModel request, HttpServletRequest httpRequest,
            AuthDetails user) {
        String logId = this.logService.getMaxId();
        this.logService.logTemp(logId, LogType.delete, AuthService.getCurrentUserId());
        httpRequest.setAttribute("logId", logId);

        List<String> recordIds = request.getIds();
        GridModel gm = (GridModel) ViewService.fetchWidgetModel(request.getId());
        return this.gridService.delete(gm, recordIds, user, request.getTriggerRequestParams(),
                request.getChildTriggerRequestParams(), httpRequest);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @RequestMapping({ "/auto.do" })
    public ActionResult auto(@RequestBody GridRequestModel request) {
        GridModel gm = (GridModel) ViewService.fetchWidgetModel(request.getId());
        this.gridService.autoGen(gm, request.getParam());
        return Common.ActionOk;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @ResponseBody
    @RequestMapping({ "/reorder.do" })
    public ActionResult reorder(@RequestBody GridRequestModel request) {
        GridModel gm = (GridModel) ViewService.fetchWidgetModel(request.getId());
        this.gridService.reorder(gm, request.getReorder());
        return Common.ActionOk;
    }

    @SuppressWarnings({ "rawtypes" })
    @ResponseBody
    @RequestMapping({ "key.do" })
    public ActionResult filterKeyList(@RequestBody GridRequestModel param, AuthDetails user) {
        return new ActionResult(true, this.gridService.filterKeyList(param));
    }
}
