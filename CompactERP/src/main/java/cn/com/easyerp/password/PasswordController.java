package cn.com.easyerp.password;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormRequestModelBase;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.core.widget.FieldWithRepresentationModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.enums.ActionType;

@Controller
@RequestMapping({ "/changepassword" })
public class PasswordController extends FormViewControllerBase {
    public static final String NEW_PASSWORD = "NewPassword";
    public static final String OLD_PASSWORD = "OldPassword";
    public static final String REPEAT_PASSWORD = "RepeatPassword";
    public static final String _PASSEORD = "_password";
    @Autowired
    private DataService dataService;
    @Autowired
    private CacheService cacheService;

    @SuppressWarnings("rawtypes")
    @RequestMapping({ "/edit.view" })
    public ModelAndView view(@RequestBody FormRequestModelBase data) {
        FieldWithRepresentationModel NewPassword = new FieldWithRepresentationModel("m_user", "_password",
                "NewPassword", "********");

        FieldWithRepresentationModel OldPassword = new FieldWithRepresentationModel("m_user", "_password",
                "OldPassword", "********");

        FieldWithRepresentationModel RepeatPassword = new FieldWithRepresentationModel("m_user", "_password",
                "RepeatPassword", "********");

        PasswordFormModel form = new PasswordFormModel(ActionType.edit, data.getParent(), NewPassword, OldPassword,
                RepeatPassword, "m_user");

        return buildModelAndView(form);
    }

    @Transactional
    @ResponseBody
    @RequestMapping({ "/edit/{form_id}.do" })
    public ActionResult update(@RequestBody Map<String, String> param, @PathVariable String form_id) {
        // PasswordFormModel form = (PasswordFormModel)
        // ViewService.fetchFormModel(form_id);

        String userId = AuthService.getCurrentUserId();

        Map<String, String> retuenData = new HashMap<String, String>();

        AuthDetails user = this.dataService.loadUserById(userId);
        String oldpwd = user.get_password();
        if (!((String) param.get("OldPassword")).equals(oldpwd)) {
            String errorMsg = this.dataService.getMessageText("PASS_OLD_INVALID", new Object[0]);
            retuenData.put("msg", errorMsg);
            retuenData.put("ret", "false");
            return new ActionResult(true, retuenData);
        }

        if (((String) param.get("NewPassword")).length() < 6 || ((String) param.get("NewPassword")).length() > 20) {

            String errorMsg = this.dataService.getMessageText("PASS_NEW_INVALID", new Object[0]);

            retuenData.put("msg", errorMsg);
            retuenData.put("ret", "false");
            return new ActionResult(true, retuenData);
        }

        int pwd_strong_level = this.cacheService.getSystemParam().getPwd_strong_level();
        if (!this.dataService.checkPassword((String) param.get("NewPassword"), pwd_strong_level)) {
            String errorMsg = this.dataService.getMessageText("PASS_NEW_INVALID", new Object[0]);
            retuenData.put("msg", errorMsg);
            retuenData.put("ret", "false");
            return new ActionResult(true, retuenData);
        }

        if (!((String) param.get("NewPassword")).equals(param.get("RepeatPassword"))) {
            String errorMsg = this.dataService.getMessageText("PASS_NEW_UNMATCHED", new Object[0]);

            retuenData.put("msg", errorMsg);
            retuenData.put("ret", "false");
            return new ActionResult(true, retuenData);
        }

        if (this.dataService.updatePassword(userId, (String) param.get("NewPassword")) != 1) {
            String errorMsg = this.dataService.getMessageText("DCS-902", new Object[0]);

            retuenData.put("msg", errorMsg);
            retuenData.put("ret", "false");
            return new ActionResult(true, retuenData);
        }

        String successMsg = this.dataService.getMessageText("PASS_SUCCESS", new Object[0]);

        retuenData.put("msg", successMsg);
        retuenData.put("ret", "true");
        return new ActionResult(true, retuenData);
    }
}
