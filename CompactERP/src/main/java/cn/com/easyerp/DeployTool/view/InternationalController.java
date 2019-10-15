 package cn.com.easyerp.DeployTool.view;
 
 import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.InternationalDao;
import cn.com.easyerp.DeployTool.service.International;
import cn.com.easyerp.DeployTool.service.InternationalService;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 @Controller
 @RequestMapping({"/international"})
 public class InternationalController
   extends FormViewControllerBase
 {
   @Autowired
   private InternationalDao internationalDao;
   @Autowired
   private InternationalService il8lService;
   @Autowired
   private DataService dataService;
   
   @RequestMapping({"/international.view"})
   public ModelAndView datedelay(@RequestBody InternationalRequestModel request) {
     InternationalModel model = new InternationalModel(request.getParent());
     return buildModelAndView(model);
   }
 
 
 
 
 
   
   @RequestMapping({"/selectInternational.do"})
   @ResponseBody
   public ActionResult selectInternational(@RequestBody InternationalRequestModel request) {
     Integer start = Integer.valueOf((request.getPageNumber() - 1) * request.getPageSize() + 1);
     Integer end = Integer.valueOf(request.getPageSize());
     
     Map<String, Object> map = new HashMap<String, Object>();
 
     
     List<International> list = this.internationalDao.selectInternational(start, end);
     int count = this.internationalDao.getInternationalCount().intValue();
     
     map.put("list", list);
     map.put("count", Integer.valueOf(count));
     return new ActionResult(true, map);
   }
 
 
 
 
   
   @RequestMapping({"/selectLikeInternational.do"})
   @ResponseBody
   public ActionResult selectLikeInternational(@RequestBody InternationalRequestModel request) {
     Integer start = Integer.valueOf((request.getPageNumber() - 1) * request.getPageSize());
     Integer end = Integer.valueOf(request.getPageSize());
     
     Map<String, Object> map = new HashMap<String, Object>();
     int count = this.internationalDao.countInternational();
     
     if (null != request.getParams() && "" != request.getParams()) {
       String params = request.getParams();
       List<International> list = this.internationalDao.selectLikeInternational("%" + params + "%", start, end);
       map.put("list", list);
     } else {
       List<International> list = this.internationalDao.selectInternational(start, end);
       map.put("list", list);
     } 
     map.put("count", Integer.valueOf(count));
     return new ActionResult(true, map);
   }
 
 
 
 
 
   
   @RequestMapping({"/saveInternational.do"})
   @ResponseBody
   public ActionResult saveInternational(@RequestBody InternationalRequestModel requestModel) throws Exception {
     boolean flag = true;
     List<International> list = new ArrayList<International>();
     for (International international : requestModel.getInsert()) {
       if (null == international.getInternational_id() || international.getInternational_id().equals("")) {
         throw new Exception("ID can not be null");
       }
     } 
     try {
       if (requestModel.getInsert().size() > 0) {
         for (International international : requestModel.getInsert()) {
           International inter = new International();
           inter.setInternational_id(international.getInternational_id());
           inter.setType(international.getType());
           inter.setCn(international.getCn());
           inter.setEn(international.getEn());
           inter.setJp(international.getJp());
           inter.setOther1(international.getOther1());
           inter.setOther2(international.getOther2());
           inter.setModule(international.getModule());
           inter.setCre_date(new Date());
           list.add(inter);
         } 
         this.internationalDao.addInternational(list);
       } else if (requestModel.getUpdate().size() > 0) {
         for (International international : requestModel.getUpdate()) {
           International inter = new International();
           inter.setInternational_id(international.getInternational_id());
           inter.setType(international.getType());
           inter.setCn(international.getCn());
           inter.setEn(international.getEn());
           inter.setJp(international.getJp());
           inter.setOther1(international.getOther1());
           inter.setOther2(international.getOther2());
           inter.setModule(international.getModule());
           inter.setCre_date(new Date());
           list.add(inter);
         } 
         this.internationalDao.updInternational(list);
       } else if (requestModel.getDeleted().size() > 0) {
         for (International international : requestModel.getDeleted()) {
           International inter = new International();
           inter.setInternational_id(international.getInternational_id());
           list.add(inter);
         } 
         this.internationalDao.delInternational(list);
       } 
       flag = true;
     } catch (Exception e) {
       e.printStackTrace();
       flag = false;
     } 
     return new ActionResult(true, Boolean.valueOf(flag));
   }
   
   @RequestMapping({"/retrievalInter.do"})
   @ResponseBody
   public ActionResult retrievalInter(@RequestBody InternationalRequestModel requestModel) {
     String retrievalValue = requestModel.getRetrieveValue();
     if (retrievalValue == null || "".equals(retrievalValue)) {
       return new ActionResult(true, "");
     }
     List<International> list = this.internationalDao.retrievalInter("%" + retrievalValue + "%", 
         AuthService.getCurrentUser().getLanguage_id());
     return new ActionResult(true, list);
   }
   
   @RequestMapping({"/saveRetrievalInter.do"})
   @ResponseBody
   public ActionResult saveRetrievalInter(@RequestBody InternationalRequestModel requestModel) {
     for (International international : requestModel.getInsert()) {
       this.internationalDao.deleteRetrievalInter(international);
       this.internationalDao.saveRetrievalInter(international);
     } 
     return new ActionResult(true, "保存成功");
   }
 
   
   @RequestMapping({"/replace.do"})
   @ResponseBody
   public ActionResult I18NReplace(@RequestBody InternationalRequestModel requestModel) {
     try {
       this.internationalDao.I18NReplace(requestModel.getFindContext(), requestModel.getReplaceWith(), requestModel.getScope());
       return new ActionResult(true, "replace succeed!");
     } catch (Exception e) {
       return new ActionResult(false, "replace failed!");
     } 
   }
 
   
   @ResponseBody
   @RequestMapping(value = {"/upload.do"}, method = {RequestMethod.POST})
   public ActionResult upload(HttpServletRequest req) {
     boolean flag = this.il8lService.process(req);
     String msg = (flag == true) ? "import internationalData succeed!" : "import internationalData faild!";
     return new ActionResult(flag, msg);
   }
 
   
   @ResponseBody
   @RequestMapping({"/export.do"})
   public ActionResult exportI18n(@RequestBody InternationalRequestModel requestModel) throws IOException {
     String csvFileName = "c_international.csv";
     List<String> columnNames = new ArrayList<String>();
     
     columnNames.add(this.dataService.getMessageText("international_id", new Object[0]));
     columnNames.add(this.dataService.getMessageText("cn", new Object[0]));
     columnNames.add(this.dataService.getMessageText("en", new Object[0]));
     columnNames.add(this.dataService.getMessageText("jp", new Object[0]));
     columnNames.add(this.dataService.getMessageText("other1", new Object[0]));
     columnNames.add(this.dataService.getMessageText("other2", new Object[0]));
     
     String params = "%" + requestModel.getParams() + "%";
     return this.il8lService.export(csvFileName, columnNames, params);
   }
 }


