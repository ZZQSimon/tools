 package cn.com.easyerp.DeployTool.view;
 
 import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.DictionaryDao;
import cn.com.easyerp.DeployTool.service.DictionaryAddDetails;
import cn.com.easyerp.DeployTool.service.DictionaryDetails;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 @Controller
 @RequestMapping({"/dictionary"})
 public class DictionaryController
   extends FormViewControllerBase
 {
   @RequestMapping({"/dictionary.view"})
   public ModelAndView dictionary(@RequestBody DictionaryRequestModel request) {
     DictionaryModel form = new DictionaryModel(request.getParent());
     return buildModelAndView(form);
   } @Autowired
   private DictionaryDao dictionaryDao;
   @ResponseBody
   @RequestMapping({"/initDic.do"})
   public ActionResult initDic(AuthDetails user) {
     Map<String, Object> map = new HashMap<String, Object>();
     Map<String, Object> mapToOne = new HashMap<String, Object>();
     List<DictionaryDetails> dictionaryList = this.dictionaryDao.selectDictionary(AuthService.getCurrentUser().getLanguage_id(), user.getId());
     List<DictionaryDetails> dictionaryToOne = this.dictionaryDao.dictionaryById(((DictionaryDetails)dictionaryList.get(0)).getDic_id(), user.getId());
     mapToOne.put("total", Integer.valueOf(dictionaryToOne.size()));
     mapToOne.put("rows", dictionaryToOne);
     map.put("dictionaryList", dictionaryList);
     map.put("dictionaryById", mapToOne);
     return new ActionResult(true, map);
   }
   
   @ResponseBody
   @RequestMapping({"/retrieveDic.do"})
   public ActionResult retrieveDic(@RequestBody DictionaryRequestModel request, AuthDetails user) {
     String retrieveValue = request.getRetrieveValue();
     List<DictionaryDetails> dictionaryList = null;
     if ("".equals(retrieveValue) || retrieveValue == null) {
       dictionaryList = this.dictionaryDao.selectDictionary(AuthService.getCurrentUser().getLanguage_id(), user.getId());
     } else {
       dictionaryList = this.dictionaryDao.retrieveDictionary("%" + retrieveValue + "%", AuthService.getCurrentUser().getLanguage_id(), user.getId());
     } 
     return new ActionResult(true, dictionaryList);
   }
   
   @ResponseBody
   @RequestMapping({"/dictionaryById.do"})
   public ActionResult dictionaryById(@RequestBody DictionaryRequestModel request, AuthDetails user) {
     String dicId = request.getDicId();
     Map<String, Object> map = new HashMap<String, Object>();
     List<DictionaryDetails> dictionaryList = this.dictionaryDao.dictionaryById(dicId, user.getId());
     map.put("total", Integer.valueOf(dictionaryList.size()));
     map.put("rows", dictionaryList);
     return new ActionResult(true, map);
   }
   
   @ResponseBody
   @RequestMapping({"/saveDictionary.do"})
   public ActionResult saveDictionary(@RequestBody DictionaryRequestModel request) {
     List<DictionaryAddDetails> dictionaryAddList = request.getDictionaryAdd();
     for (int i = 0; i < dictionaryAddList.size(); i++) {
       this.dictionaryDao.deleteDicById(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicId());
       List<DictionaryDetails> dictionaryList = ((DictionaryAddDetails)dictionaryAddList.get(i)).getDicList();
       for (int j = 0; j < dictionaryList.size(); j++) {
         dictionaryList.get(j);
         if (j == 0) {
           DictionaryDetails dictionary = new DictionaryDetails();
           dictionary.setKey_international(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicId().toLowerCase());
           if (AuthService.getCurrentUser().getLanguage_id().equals("cn")) {
             dictionary.setCn(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicName());
           }
           if (AuthService.getCurrentUser().getLanguage_id().equals("en")) {
             dictionary.setEn(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicName());
           }
           if (AuthService.getCurrentUser().getLanguage_id().equals("jp")) {
             dictionary.setJp(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicName());
           }
           if (AuthService.getCurrentUser().getLanguage_id().equals("other1")) {
             dictionary.setOther1(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicName());
           }
           if (AuthService.getCurrentUser().getLanguage_id().equals("other2")) {
             dictionary.setOther2(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicName());
           }
           try {
             this.dictionaryDao.addInternational(dictionary);
           } catch (Exception e) {}
         } 
 
         
         DictionaryDetails dictionary = (DictionaryDetails)dictionaryList.get(j);
         ((DictionaryAddDetails)dictionaryAddList.get(i)).setDicId(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicId());
         dictionary.setSeq(j + 1);
         dictionary.setId_international(((DictionaryAddDetails)dictionaryAddList.get(i)).getDicId().toLowerCase());
         if ("".equals(dictionary.getKey_international()) || dictionary.getKey_international() == null) {
           String[] uuids = UUID.randomUUID().toString().split("-");
           String uuid = uuids[0].toLowerCase() + uuids[1].toLowerCase();
           dictionary.setKey_international(uuid);
           this.dictionaryDao.addInternational(dictionary);
         } 
         this.dictionaryDao.addDictionary(dictionary);
       } 
     } 
     return new ActionResult(true);
   }
   
   @ResponseBody
   @RequestMapping({"/deleteDic.do"})
   public ActionResult deleteDic(@RequestBody DictionaryRequestModel request) {
     List<DictionaryAddDetails> dictionaryDeList = request.getDictionaryAdd();
     for (int i = 0; i < dictionaryDeList.size(); i++) {
       this.dictionaryDao.deleteDicById(((DictionaryAddDetails)dictionaryDeList.get(i)).getDicId());
     }
     return new ActionResult(true, "删除成功！");
   }
 }


