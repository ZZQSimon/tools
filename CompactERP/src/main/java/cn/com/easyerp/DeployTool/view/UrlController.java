package cn.com.easyerp.DeployTool.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.UrlDao;
import cn.com.easyerp.DeployTool.service.Url;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/url" })
public class UrlController extends FormViewControllerBase {
    @Autowired
    private UrlDao urlDao;

    @RequestMapping({ "/url.view" })
    public ModelAndView url(@RequestBody UrlRequestModel request) {
        UrlModel model = new UrlModel(request.getParent());
        return buildModelAndView(model);
    }

    @ResponseBody
    @RequestMapping({ "/findAllUrl.do" })
    public ActionResult findAll() {
        int count;
        Map<String, Object> map = new HashMap<String, Object>();
        List<Url> list = this.urlDao.findAllUrl();
        if (list.size() == 0) {
            count = 0;
        } else {
            count = list.size();
        }
        map.put("count", Integer.valueOf(count));
        map.put("list", list);
        return new ActionResult(true, map);
    }

    @RequestMapping({ "/saveUrl.do" })
    @ResponseBody
    public ActionResult saveUrl(@RequestBody UrlRequestModel model) {
        boolean flag = true;
        List<Url> list = new ArrayList<Url>();

        try {
            if (model.getInsert().size() > 0) {
                for (Url newUrl : model.getInsert()) {
                    Url url = new Url();
                    url.setMemo(newUrl.getMemo());
                    url.setModule(newUrl.getModule());
                    url.setName(newUrl.getName());
                    url.setParam(newUrl.getParam());
                    url.setRouter_url(newUrl.getRouter_url());
                    url.setUrl(newUrl.getUrl());
                    list.add(url);
                }
                this.urlDao.addUrl(list);
            } else if (model.getUpdate().size() > 0) {
                for (Url newUrl : model.getUpdate()) {
                    Url url = new Url();
                    url.setId(newUrl.getId());
                    url.setMemo(newUrl.getMemo());
                    url.setModule(newUrl.getModule());
                    url.setName(newUrl.getName());
                    url.setParam(newUrl.getParam());
                    url.setRouter_url(newUrl.getRouter_url());
                    url.setUrl(newUrl.getUrl());
                    list.add(url);
                }
                this.urlDao.updUrl(list);
            } else if (model.getDeleted().size() > 0) {
                for (Url newUrl : model.getDeleted()) {
                    Url url = new Url();
                    url.setId(newUrl.getId());
                    list.add(url);
                }
                this.urlDao.deleteUrl(list);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return new ActionResult(true, Boolean.valueOf(flag));
    }
}
