 package cn.com.easyerp.core.widget.message;
 
 import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.DeployTool.dao.MessageDao;
import cn.com.easyerp.core.dao.AuthDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.framework.common.Common;
 
 
 
 
 
 
 
 
 
 
 
 @Service
 public class MessageService
 {
   @Autowired
   private MessageDao messageDao;
   @Autowired
   private AuthDao authDao;
   @Autowired
   private SystemDao systemDao;
   public static final String MESSAGE_TYPE_SYS = "1";
   public static final String MESSAGE_TYPE_CLOUD = "2";
   public static final String MESSAGE_TYPE_ABOUTME = "3";
   public static final int MESSAGE_PAGE_SIZE = 25;
   
   public List<MessageDescribe> getSysMessage(int pageNumber, String receiver) { return this.messageDao.getMessageByType("1", pageNumber, 25, receiver); }
 
 
   
   public List<MessageDescribe> getCloudMessage(int pageNumber, String receiver) { return this.messageDao.getMessageByType("2", pageNumber, 25, receiver); }
 
 
   
   public List<MessageDescribe> getAboutmeMessage(int pageNumber, String receiver) { return this.messageDao.getMessageByType("3", pageNumber, 25, receiver); }
 
 
   
   public int getUnreadSysMessage(String receiver) { return this.messageDao.getUnreadMessageByType("1", receiver); }
 
 
   
   public int getUnreadCloudMessage(String receiver) { return this.messageDao.getUnreadMessageByType("2", receiver); }
 
 
   
   public int getUnreadAboutMessage(String receiver) { return this.messageDao.getUnreadMessageByType("3", receiver); }
 
 
   
   public MessageDescribe getMessageById(String receive_id) { return this.messageDao.getMessageById(receive_id); }
 
 
   
   public void updMessageByIdList(List<String> messages) { this.messageDao.updMessageByIdList(messages); }
 
 
   
   public void delMessageByIdList(List<String> messages) { this.messageDao.delMessageByIdList(messages); }
 
 
   
   public int getCountByType(String type, String receiver) { return this.messageDao.getCountByType(type, receiver); }
 
   
   public int getUnreadCount(String receiver) { return this.messageDao.getUnreadCount(receiver); }
 
   
   public void addMessage(MessageDescribe message) {
     this.messageDao.addSendMessage(message);
     this.messageDao.addReceiveMessage(message, message.getUUID());
   }
 
   
   private int addSendMessage(MessageDescribe message) { return this.messageDao.addSendMessage(message); }
 
   
   private void addReceiveMessage(MessageDescribe message, List<String> receivers) {
     for (String reveiver : receivers) {
       message.setReceiver(reveiver);
       this.messageDao.addReceiveMessage(message, message.getUUID());
     } 
   }
   
   public void addMessages(MessageDescribe message, List<String> receivers) {
     addSendMessage(message);
     addReceiveMessage(message, receivers);
   }
 
   
   public List<MessageDescribe> getMessageByType(String type, int start, String receiver) { return this.messageDao.getMessageByType(type, start, 25, receiver); }
 
 
   
   public void sendTextMessage(String templateId, String touser, Map<String, Map<String, Object>> datas, String url) { addMessage(buildMessage(templateId, datas, url, touser)); }
 
 
   
   private MessageDescribe buildMessage(String templateId, Map<String, Map<String, Object>> datas, String urlParam, String touser) {
     MessageTemplateModel template = this.systemDao.getTemplateById(templateId);
     String body = template.getTemplate();
 
     
     body = body.replace("{now}", Common.defaultDateFormat.format(new Date()));
     for (Map.Entry<String, Map<String, Object>> data : datas.entrySet()) {
       String key = (String)data.getKey();
       String value = String.valueOf(((Map)data.getValue()).get("value"));
       body = body.replace("{" + key + "}", value);
     } 
 
 
 
 
     
     return new MessageDescribe(template.getTitle(), body, touser, "3");
   }
 }


