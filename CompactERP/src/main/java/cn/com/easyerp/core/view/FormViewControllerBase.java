package cn.com.easyerp.core.view;

import org.springframework.web.servlet.ModelAndView;

public class FormViewControllerBase {
    protected ModelAndView buildModelAndView(FormModelBase model) {
        return new ModelAndView(model.getView(), "form", model);
    }
}
