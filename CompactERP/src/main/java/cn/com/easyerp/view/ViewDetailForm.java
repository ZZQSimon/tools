package cn.com.easyerp.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.com.easyerp.core.view.form.detail.DetailForm;
import cn.com.easyerp.core.view.form.detail.DetailFormService;

@Controller
@RequestMapping({ "/view" })
public class ViewDetailForm extends DetailForm {
    @Autowired
    private DetailFormService builder;

    protected DetailFormService getBuilder() {
        return this.builder;
    }
}
