package cn.com.easyerp.module.interfaces;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/generate" })
public class GenerateController extends FormViewControllerBase {
    // @Autowired
    // private AuthService authService;
    @Autowired
    private GenerateService generateService;

    @RequestMapping({ "/generate.view" })
    public ModelAndView view() {
        GenerateModel model = new GenerateModel(null, null);
        this.generateService.initModel(model);

        return new ModelAndView("generate", "form", model);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @ResponseBody
    @RequestMapping(value = { "/generate.do" }, method = { RequestMethod.POST })
    public ActionResult importAction(@RequestBody Map<String, Object> params) {
        try {
            String status = this.generateService.processExport((List) params.get("type"), (String) params.get("start"),
                    (String) params.get("end"));

            return new ActionResult(true, status);
        } catch (Exception e) {
            e.printStackTrace();
            return new ActionResult(false, "出现错误！错误为：" + e.getMessage());
        }
    }
}
