package cn.com.easyerp.core.widget.file;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.framework.common.ActionResult;
import cn.com.easyerp.framework.common.Common;

@Controller
@RequestMapping({ "/widget/file" })
public class FileController {
    @ResponseBody
    @RequestMapping({ "update.do" })
    public ActionResult update(@RequestBody FileRequestModel request) {
        ((FileFieldModel) request.getWidget()).setValue(request.getValue());
        return Common.ActionOk;
    }
}
