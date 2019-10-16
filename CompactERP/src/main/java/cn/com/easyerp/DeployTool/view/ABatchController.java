package cn.com.easyerp.DeployTool.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.ABatchDao;
import cn.com.easyerp.DeployTool.service.ABatch;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;

@Controller
@RequestMapping({ "/abatch" })
public class ABatchController extends FormViewControllerBase {
    @Autowired
    private ABatchDao aBatchDao;

    @RequestMapping({ "/abatch.view" })
    public ModelAndView batch(@RequestBody BatchRequestModel request) {
        BatchModel form = new BatchModel(request.getParent());
        return buildModelAndView(form);
    }

    @ResponseBody
    @RequestMapping({ "/selectBatch.do" })
    public ActionResult selectBatch() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            int count;
            List<ABatch> list = this.aBatchDao.selectBatch();

            if (list == null) {
                count = 0;
            } else {
                count = list.size();
            }
            map.put("list", list);
            map.put("count", Integer.valueOf(count));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ActionResult(true, map);
    }

    @RequestMapping(value = { "/saveBatch.do" }, method = { RequestMethod.POST })
    @ResponseBody
    public ActionResult saveBatch(@RequestBody BatchRequestModel model) {
        boolean flag = true;
        try {
            List<ABatch> list = new ArrayList<ABatch>();

            if (model.getInsert().size() > 0) {
                for (ABatch aBatch : model.getInsert()) {
                    ABatch batch = new ABatch();
                    batch.setInterceptor_service(aBatch.getInterceptor_service());
                    batch.setMemo(aBatch.getMemo());
                    batch.setModule(aBatch.getModule());
                    batch.setService_param(aBatch.getService_param());
                    batch.setStatement(aBatch.getStatement());
                    list.add(batch);
                }
                this.aBatchDao.addBatch(list);

            } else if (model.getUpdate().size() > 0) {
                for (ABatch aBatch : model.getUpdate()) {
                    ABatch batch = new ABatch();
                    batch.setBatch_id(aBatch.getBatch_id());
                    batch.setInterceptor_service(aBatch.getInterceptor_service());
                    batch.setMemo(aBatch.getMemo());
                    batch.setModule(aBatch.getModule());
                    batch.setService_param(aBatch.getService_param());
                    batch.setStatement(aBatch.getStatement());
                    list.add(batch);
                }
                this.aBatchDao.updBatch(list);

            } else if (model.getDeleted().size() > 0) {
                for (ABatch aBatch : model.getDeleted()) {
                    ABatch batch = new ABatch();
                    batch.setBatch_id(aBatch.getBatch_id());
                    list.add(batch);
                }
                this.aBatchDao.DeleteBatch(list);
            }
            flag = true;
        } catch (Exception e) {
            e.printStackTrace();
            flag = false;
        }
        return new ActionResult(true, Boolean.valueOf(flag));
    }
}
