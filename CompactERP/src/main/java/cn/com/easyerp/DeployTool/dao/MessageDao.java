package cn.com.easyerp.DeployTool.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import cn.com.easyerp.core.widget.message.MessageDescribe;

@Repository
public interface MessageDao {
  List<MessageDescribe> getMessageByType(@Param("type") String paramString1, @Param("pageNumber") int paramInt1, @Param("pageSize") int paramInt2, @Param("receiver") String paramString2);
  
  int getUnreadMessageByType(@Param("type") String paramString1, @Param("receiver") String paramString2);
  
  MessageDescribe getMessageById(@Param("receive_id") String paramString);
  
  void delMessageByIdList(@Param("ids") List<String> paramList);
  
  void updMessageByIdList(@Param("ids") List<String> paramList);
  
  int getCountByType(@Param("type") String paramString1, @Param("receiver") String paramString2);
  
  int getUnreadCount(@Param("receiver") String paramString);
  
  int addSendMessage(@Param("message") MessageDescribe paramMessageDescribe);
  
  void addReceiveMessage(@Param("message") MessageDescribe paramMessageDescribe, @Param("receive_id") String paramString);
}


