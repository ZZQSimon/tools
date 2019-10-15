package cn.com.easyerp.core.view;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.core.ViewService;
import cn.com.easyerp.core.widget.Widget;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.enums.ActionType;
 
@Controller
@RequestMapping({"/form"})
public class FormController extends FormViewControllerBase {
	@Widget("empty")
	class EmptyFormModel extends FormModelBase {
		private String title;
		protected EmptyFormModel(String title) {
			super(ActionType.view, null);
			this.title = title;
		}
		public String getTitle() { return this.title; }
	}
	
	@Autowired
	private ViewService viewService;
   
	@ResponseBody
	@RequestMapping({"/close.do"})
	public ActionResult close(@RequestBody FormRequestModelBase request) {
		this.viewService.destroyFormCache(request.getId());
		return Common.ActionOk;
	}
   
	@RequestMapping({"/empty.view"})
	public ModelAndView empty() {
		EmptyFormModel form = new EmptyFormModel("empty");
		return buildModelAndView(form);
	}
}