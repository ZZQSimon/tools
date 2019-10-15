 package cn.com.easyerp.core.widget.message;
 
 import java.util.List;

import cn.com.easyerp.core.view.FormRequestModelBase;
 
 
 
 public class MessageRequestModel
   extends FormRequestModelBase<MessageModel>
 {
   private int pageNumber;
   private int pageSize;
   private List<String> messages;
   private String messageId;
   private String type;
   
   public int getPageNumber() { return this.pageNumber; }
 
   
   public void setPageNumber(int pageNumber) { this.pageNumber = pageNumber; }
 
   
   public int getPageSize() { return this.pageSize; }
 
   
   public void setPageSize(int pageSize) { this.pageSize = pageSize; }
 
   
   public List<String> getMessages() { return this.messages; }
 
   
   public void setMessages(List<String> messages) { this.messages = messages; }
 
   
   public String getMessageId() { return this.messageId; }
 
   
   public void setMessageId(String messageId) { this.messageId = messageId; }
 
   
   public String getType() { return this.type; }
 
   
   public void setType(String type) { this.type = type; }
 }


