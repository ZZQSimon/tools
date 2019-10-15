 package cn.com.easyerp.core.widget.message;
 
 import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import cn.com.easyerp.DeployTool.dao.MessageDao;
import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.core.view.FormViewControllerBase;
import cn.com.easyerp.framework.common.ActionResult;
 
 
 
 
 @Controller
 @RequestMapping({"/widget/message"})
 public class MessageController
   extends FormViewControllerBase
 {
   @Autowired
   private MessageService messageService;
   @Autowired
   private MessageDao messageDao;
   
   @RequestMapping({"/select.view"})
   public ModelAndView form(@RequestBody MessageRequestModel request) {
     MessageModel form = new MessageModel(request.getParent());
     return buildModelAndView(form);
   }
 
 
 
 
 
 
   
   @RequestMapping({"/initMessageTable.do"})
   @ResponseBody
   public ActionResult initMessageTable(@RequestBody MessageRequestModel request) {
     Map<String, Object> res = new HashMap<String, Object>();
     String receiver = AuthService.getCurrentUser().getId();
     //this.messageService; 
     int start = (request.getPageNumber() - 1) * 25;
     List<MessageDescribe> sysList = this.messageService.getSysMessage(start, receiver);
     List<MessageDescribe> cloudList = this.messageService.getCloudMessage(start, receiver);
     List<MessageDescribe> aboutmeList = this.messageService.getAboutmeMessage(start, receiver);
     //this.messageService; 
     int sysCnt = this.messageService.getCountByType("1", receiver);
     //this.messageService; 
     int cloudCnt = this.messageService.getCountByType("2", receiver);
     //this.messageService; 
     int aboutmeCnt = this.messageService.getCountByType("3", receiver);
     int sysUnread = this.messageService.getUnreadSysMessage(receiver);
     int cloudUnread = this.messageService.getUnreadCloudMessage(receiver);
     int aboutmeUnread = this.messageService.getUnreadAboutMessage(receiver);
     res.put("sysList", sysList);
     res.put("cloudList", cloudList);
     res.put("aboutmeList", aboutmeList);
     res.put("sysCnt", Integer.valueOf(sysCnt));
     res.put("cloudCnt", Integer.valueOf(cloudCnt));
     res.put("aboutmeCnt", Integer.valueOf(aboutmeCnt));
     res.put("sysUnread", Integer.valueOf(sysUnread));
     res.put("cloudUnread", Integer.valueOf(cloudUnread));
     res.put("aboutmeUnread", Integer.valueOf(aboutmeUnread));
     return new ActionResult(true, res);
   }
   
   @RequestMapping({"/refreshUnread.do"})
   @ResponseBody
   public ActionResult refreshUnread(@RequestBody MessageRequestModel request) {
     Map<String, Object> res = new HashMap<String, Object>();
     String receiver = AuthService.getCurrentUser().getId();
     int sysUnread = this.messageService.getUnreadSysMessage(receiver);
     int cloudUnread = this.messageService.getUnreadCloudMessage(receiver);
     int aboutmeUnread = this.messageService.getUnreadAboutMessage(receiver);
     res.put("sysUnread", Integer.valueOf(sysUnread));
     res.put("cloudUnread", Integer.valueOf(cloudUnread));
     res.put("aboutmeUnread", Integer.valueOf(aboutmeUnread));
     
     return new ActionResult(true, res);
   }
 
 
 
 
 
   
   @RequestMapping({"/getMessage.do"})
   @ResponseBody
   public ActionResult getMessage(@RequestBody MessageRequestModel request) {
     MessageDescribe message = this.messageService.getMessageById(request.getMessageId());
     this.messageService.updMessageByIdList(request.getMessages());
     return new ActionResult(true, message);
   }
 
 
 
 
 
   
   @RequestMapping({"/messageDetail.view"})
   @ResponseBody
   public ModelAndView messageDetail(@RequestBody MessageRequestModel request) {
     MessageDetailModel form = new MessageDetailModel(request.getParent());
     MessageDescribe message = this.messageService.getMessageById(request.getMessageId());
     this.messageService.updMessageByIdList(request.getMessages());
     form.setMessage(message);
     return buildModelAndView(form);
   }
 
 
 
 
 
 
   
   @RequestMapping({"/batchRead.do"})
   @ResponseBody
   public ActionResult batchRead(@RequestBody MessageRequestModel request) {
     Map<String, Object> data = new HashMap<String, Object>();
     try {
       this.messageService.updMessageByIdList(request.getMessages());
       data.put("ret", Boolean.valueOf(true));
       data.put("msg", "read messages succeed!");
     }
     catch (Exception e) {
       data.put("ret", Boolean.valueOf(false));
       data.put("msg", e.getStackTrace());
     } 
     
     return new ActionResult(true, data);
   }
 
 
 
 
 
   
   @RequestMapping({"/batchDelete.do"})
   @ResponseBody
   public ActionResult batchDelete(@RequestBody MessageRequestModel request) {
     Map<String, Object> data = new HashMap<String, Object>();
     try {
       this.messageService.delMessageByIdList(request.getMessages());
       data.put("ret", Boolean.valueOf(true));
       data.put("msg", "delete messages succeed!");
     } catch (Exception e) {
       data.put("ret", Boolean.valueOf(false));
       data.put("msg", e.getStackTrace());
     } 
     return new ActionResult(true, data);
   }
 
 
 
 
 
   
   @RequestMapping({"/reloadMessage.do"})
   @ResponseBody
   public ActionResult reloadMessage(@RequestBody MessageRequestModel request) {
     Map<String, Object> res = new HashMap<String, Object>();
     String receiver = AuthService.getCurrentUser().getId();
     //this.messageService; 
     int start = (request.getPageNumber() - 1) * 25;
     List<MessageDescribe> list = this.messageService.getMessageByType(request.getType(), start, receiver);
     int count = this.messageService.getCountByType(request.getType(), receiver);
     res.put("count", Integer.valueOf(count));
     res.put("list", list);
     return new ActionResult(true, res);
   }
   
   @RequestMapping({"/getUnreadCount.do"})
   @ResponseBody
   public ActionResult getUnreadCount(@RequestBody MessageRequestModel request) {
     String curUser = AuthService.getCurrentUser().getId();
     int unreadCount = this.messageService.getUnreadCount(curUser);
     return new ActionResult(true, Integer.valueOf(unreadCount));
   }
 }


