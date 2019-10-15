 package cn.com.easyerp.core.mail;
 
 import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.http.MethodType;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.com.easyerp.DeployTool.dao.TableDeployDao;
import cn.com.easyerp.auth.AuthDetails;
import cn.com.easyerp.core.api.ApiResult;
import cn.com.easyerp.core.api.annotations.Api;
import cn.com.easyerp.core.api.annotations.ApiMethod;
import cn.com.easyerp.core.api.annotations.ApiParam;
import cn.com.easyerp.core.approve.ApproveFlow;
import cn.com.easyerp.core.approve.ApproveService;
import cn.com.easyerp.core.approve.FlowEvent;
import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.CityDescribe;
import cn.com.easyerp.core.cache.TableDescribe;
import cn.com.easyerp.core.dao.AdidasDao;
import cn.com.easyerp.core.dao.ApproveDao;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.DataService;
import cn.com.easyerp.core.data.DatabaseDataMap;
import cn.com.easyerp.core.data.ExportService;
import cn.com.easyerp.core.exception.ApplicationException;
import cn.com.easyerp.core.mail.adidas.AdidasService;
import cn.com.easyerp.core.mail.adidas.CodeStr;
import cn.com.easyerp.core.mail.adidas.EntryManage;
import cn.com.easyerp.core.mail.adidas.ManageVO;
import cn.com.easyerp.core.master.DxRoutingDataSource;
import cn.com.easyerp.core.sms.AliSmsService;
import cn.com.easyerp.core.widget.message.MessageDescribe;
import cn.com.easyerp.core.widget.message.MessageService;
import cn.com.easyerp.core.widget.message.TimmingMessageModel;
import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.weixin.WeChatService;
 
 @Service
 @Api
 public class SystemDefaultApiService
 {
   @Autowired
   private MailService mailService;
   @Autowired
   private MessageService messageService;
   @Autowired
   @Qualifier("serviceNumber")
   private WeChatService weChatService;
   @Autowired
   private SystemDao systemDao;
   @Autowired
   private ApproveDao approveDao;
   @Autowired
   private CacheService cacheService;
   @Autowired
   private DataService dataService;
   
   @ApiMethod("SEND_SMS")
   public ApiResult sendSms(@ApiParam("UUID") List<String> uuid, @ApiParam("sms_entry_id") List<String> sms_entry_id, @ApiParam("phone") List<String> phone, @ApiParam("sign_name") List<String> sign_name, @ApiParam("template_param") List<String> template_param) {
     List<String> message = new ArrayList<String>();
     if (sms_entry_id == null) {
       message.add("sms_entry_id is empty");
       return new ApiResult(0, message);
     } 
     SendSmsRequest sms = new SendSmsRequest();
     sms.setMethod(MethodType.POST);
     sms.setPhoneNumbers((String)phone.get(0));
     sms.setSignName((String)sign_name.get(0));
     sms.setTemplateCode((String)sms_entry_id.get(0));
     sms.setTemplateParam((String)template_param.get(0));
     try {
       boolean smsStatus = this.aliSmsService.sendSms(sms);
       if (smsStatus) {
         return new ApiResult(0, message);
       }
       message.add("sms error");
       return new ApiResult(1, message);
     }
     catch (Exception e) {
       return new ApiResult(0, message);
     }  } @Autowired private ApproveService approveService; @Autowired private TableDeployDao tableDeployDao; @Autowired
   private DxRoutingDataSource dxRoutingDataSource; @Autowired
   private ExportService exportService; @Autowired
   private AdidasService adidasService; @Autowired
   private AdidasDao adidasDao; @Autowired
   private FileUtils filUtils; @Autowired
   private AliSmsService aliSmsService; @ApiMethod("CHECK_PASSWORD")
   public ApiResult check_password(@ApiParam("UUID") List<String> uuid, @ApiParam("pwd") List<String> pwd, @ApiParam("id") List<String> id, @ApiParam("domain") List<String> domain) { List<String> message = new ArrayList<String>();
     int pwd_strong_level = this.cacheService.getSystemParam().getPwd_strong_level();
     if (!this.dataService.checkPassword((String)pwd.get(0), pwd_strong_level)) {
       String errorMsg = this.dataService.getMessageText("PASS_NEW_INVALID", new Object[0]);
       message.add(errorMsg);
       return new ApiResult(1, message);
     } 
     this.tableDeployDao.changePassword((String)uuid.get(0), (String)id.get(0), (String)pwd.get(0), (String)domain.get(0));
     message.add("success");
     return new ApiResult(0, message); }
   
   @ApiMethod("DEFAULT_SENDMAIL_API")
   public ApiResult defaultSendmail(@ApiParam("UUID") List<String> uuid, @ApiParam("title") List<String> title, @ApiParam("context") List<String> context, @ApiParam("receivers") List<String> receivers) {
     List<String> message = new ArrayList<String>();
     try {
       MailDescribe mail = new MailDescribe((String)title.get(0), (String)context.get(0));
       mail.setRecipients((String)receivers.get(0));
       mail.setCC("");
       this.mailService.send(mail);
       message.add("send mail succeed");
       return new ApiResult(0, message);
     } catch (Exception e) {
       message.add(e.getMessage());
       e.getStackTrace();
       return new ApiResult(1, message);
     } 
   }
   
   @ApiMethod("DEFAULT_SENMESSAGE_API")
   public ApiResult defaultSendmessage(@ApiParam("UUID") List<String> uuid, @ApiParam("title") List<String> title, @ApiParam("context") List<String> context, @ApiParam("type") List<String> type, @ApiParam("sender") List<String> sender, @ApiParam("receivers") List<String> receivers) {
     List<String> message = new ArrayList<String>();
     try {
       MessageDescribe msg = new MessageDescribe();
       msg.setTitle((String)title.get(0));
       msg.setContent((String)context.get(0));
       msg.setType(String.valueOf(type.get(0)));
       msg.setSender((String)sender.get(0));
       List<String> recs = Arrays.asList(((String)receivers.get(0)).split(","));
       this.messageService.addMessages(msg, recs);
       message.add("send message succeed");
       return new ApiResult(0, message);
     } catch (Exception e) {
       message.add(e.getMessage());
       e.getStackTrace();
       return new ApiResult(1, message);
     } 
   }
   
   @ApiMethod("DEFAULT_WEIXIN_MESSAGE_API")
   public ApiResult defaultWeiXinMessage(@ApiParam("UUID") List<String> uuid, @ApiParam("user") List<String> user, @ApiParam("template_id") List<String> template_id, @ApiParam("param") List<String> param) {
     List<String> message = new ArrayList<String>();
     Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
     ObjectMapper objMap = new ObjectMapper();
     try {
       Map<String, Object> map = (Map)objMap.readValue(((String)param.get(0)).replaceAll("'", "\""), Map.class);
       for (Map.Entry<String, Object> entry : map.entrySet()) {
         Map<String, Object> data = new HashMap<String, Object>();
         data.put("value", entry.getValue());
         datas.put(entry.getKey(), data);
       } 
       this.weChatService.sendTextMessage(String.valueOf(template_id.get(0)), (String)user.get(0), datas, "");
     } catch (Exception e) {
       e.printStackTrace();
       message.add("operation failed");
       return new ApiResult(0, message);
     } 
     return new ApiResult(1, message);
   }
   
   @ApiMethod("TIMING_WEIXIN_MESSAGE_API")
   public ApiResult timingWeiXinMessage(@ApiParam("UUID") List<String> uuid, @ApiParam("template_id") List<String> template_id) {
     List<String> message = new ArrayList<String>();
     Map<String, Map<String, Object>> datas = new HashMap<String, Map<String, Object>>();
     List<TimmingMessageModel> timings = this.systemDao.getTimingMessage();
     
     for (TimmingMessageModel timing : timings) {
       try {
         datas.clear();
         String param = timing.getParam();
         String[] params = param.split("&");
         for (String p : params) {
           if (p.split("=")[0] != null && p.split("=").length == 2) {
             Map<String, Object> data = new HashMap<String, Object>();
             data.put("value", p.split("=")[1]);
             datas.put(p.split("=")[0], data);
           } 
         } 
         this.weChatService.sendTextMessage(timing.getTemplate_id(), timing.getReceiver(), datas, "");
         this.systemDao.execTimingResult(TimmingMessageModel.PERFOMED, TimmingMessageModel.SUCCEED, timing.getId());
       } catch (Exception e) {
         this.systemDao.execTimingResult(TimmingMessageModel.PERFOMED, TimmingMessageModel.FAILED, timing.getId());
       } 
     } 
     return new ApiResult(1, message);
   }
   
   @ApiMethod("DEFAULT_DELETE_WECHAT_USER_API")
   public ApiResult deleteWechatUser(@ApiParam("UUID") List<String> uuid, @ApiParam("users") List<String> users) {
     List<String> message = new ArrayList<String>();
     try {
       this.weChatService.deleteUser(users);
     } catch (Exception e) {
       e.printStackTrace();
       message.add("operation failed");
       return new ApiResult(0, message);
     } 
     return new ApiResult(1, message);
   }
   
   @ApiMethod("DEFAULT_UPLOAD_BY_FTP_API")
   public ApiResult uploadByFtpApi(@ApiParam("UUID") List<String> uuid, @ApiParam("apiName") List<String> apiName, @ApiParam("filePath") List<String> filePath, @ApiParam("_table") List<String> _table) {
     List<String> message = new ArrayList<String>();
     TableDescribe tab = this.dataService.getTableDesc((String)_table.get(0));
     List<DatabaseDataMap> records = this.systemDao.execDynamicProcess((String)apiName.get(0), (String)uuid.get(0));
     String csvFileName = "Employee_Import_" + Common.defaultDateFormat.format(new Date()).replaceAll("-", "") + ".txt";
     try {
       InputStream in = this.filUtils.buildTxtFile(tab, records, csvFileName);
       Map<String, InputStream> ins = new HashMap<String, InputStream>();
       ins.put(csvFileName, in);
       
       this.cacheService.getSftpServer().uploadFile((String)filePath.get(0), ins);
     } catch (Exception e) {
       e.printStackTrace();
       message.add("generate csv file failed");
       return new ApiResult(1, message);
     } 
     return new ApiResult(0, message);
   }
   
   @ApiMethod("AUTOMATIC_APPROVE_SUBMIT")
   public ApiResult automatic_approve_submit(@ApiParam("UUID") List<String> uuid, @ApiParam("tableId") List<String> tableId, @ApiParam("dataId") List<String> dataId, @ApiParam("domain") List<String> domain) {
     List<String> message = new ArrayList<String>();
     if (null == domain || "".equals(domain.get(0))) {
       message.add("no domain");
       return new ApiResult(1, message);
     } 
     this.dxRoutingDataSource.setDomainKey((String)domain.get(0));
     for (int i = 0; i < tableId.size(); i++) {
       TableDescribe table = this.cacheService.getTableDesc((String)tableId.get(i));
       if (null == table) {
         message.add("no table: " + (String)tableId.get(i));
         return new ApiResult(1, message);
       } 
       if (null == table.getIdColumns() || 1 != table.getIdColumns().length) {
         message.add("table: " + (String)tableId.get(i) + " has bad id column ");
         return new ApiResult(1, message);
       } 
       Map<String, Object> dataIdMap = new HashMap<String, Object>();
       dataIdMap.put(table.getIdColumns()[0], dataId.get(i));
       List<Map<String, Object>> data = this.approveDao.selectDataById((String)tableId.get(i), dataIdMap);
       if (null == data || data.size() != 1) {
         message.add("submit approve false");
         return new ApiResult(1, message);
       } 
       try {
         this.approveService.saveApproveUser((String)tableId.get(i), (Map)data.get(i), null);
       } catch (Exception e) {
         message.add(e.getMessage());
         return new ApiResult(1, message);
       } 
       
       this.approveService.updateApproveStatus((String)tableId.get(i), ((Map)data.get(i)).get(table.getIdColumns()[0]), ApproveFlow.ApproveStatus.PROGRESS);
       message.add("success");
     } 
     return new ApiResult(0, message);
   }
   
   @ApiMethod("AUTOMATIC_APPROVE_API")
   public ApiResult automatic_approve(@ApiParam("UUID") List<String> uuid, @ApiParam("tableId") List<String> tableId, @ApiParam("dataId") List<String> dataId, @ApiParam("user_Id") List<String> user_Id, @ApiParam("approveReason") List<String> approveReason, @ApiParam("domain") List<String> domain, @ApiParam("type") List<String> type) {
     System.out.println("1111111222222222223333333333333444444444");
     List<String> message = new ArrayList<String>();
     if (null == domain.get(0) || "".equals(domain.get(0))) {
       message.add("no domain");
       return new ApiResult(1, message);
     } 
     this.dxRoutingDataSource.setActiveDomainKey((String)domain.get(0));
     for (int i = 0; i < uuid.size(); i++) {
       TableDescribe table = this.cacheService.getTableDesc((String)tableId.get(i));
       if (null == table) {
         message.add("no table: " + (String)tableId.get(i));
         return new ApiResult(1, message);
       } 
       if (null == table.getIdColumns() || 1 != table.getIdColumns().length) {
         message.add("table: " + (String)tableId.get(i) + " has bad id column ");
         return new ApiResult(1, message);
       } 
       if (null == type.get(i) || "".equals(type.get(i))) {
         message.add("no approve type");
         return new ApiResult(1, message);
       } 
       List<Map<String, Object>> approveData = this.approveDao.getApproveDataByTableData((String)tableId.get(i), dataId.get(i));
       
       if (null == approveData || approveData.size() == 0) {
         message.add("data is not in approve");
         return new ApiResult(1, message);
       } 
       String approveId = ((Map)approveData.get(0)).get("approve_id").toString();
       String blockId = ((Map)approveData.get(0)).get("block_id").toString();
       FlowEvent flowEvent = new FlowEvent();
       flowEvent.setBlock_id(blockId);
       AuthDetails user = new AuthDetails();
       if (user_Id == null) {
         
         if (approveData.size() == 1) {
           Object nodeUser = ((Map)approveData.get(0)).get("user");
           user.setId((nodeUser == null) ? "" : nodeUser.toString());
         } else {
           user.setId("CIICLOCAL");
         } 
       } else {
         user.setId((String)user_Id.get(i));
       } 
       switch ((String)type.get(i)) {
         case "agree":
           flowEvent.setEvent_type(FlowEvent.EventType.AGREE_EVENT);
           this.approveService.approvePass(approveId, blockId, (String)tableId.get(i), (String)approveReason
               .get(i), dataId.get(i), flowEvent, null, null, user);
           break;
         case "reject":
           flowEvent.setEvent_type(FlowEvent.EventType.REJECT_EVENT);
           this.approveService.approveReject(approveId, blockId, (String)tableId.get(i), (String)approveReason
               .get(i), dataId.get(i), flowEvent, null, null, user);
           break;
         case "disagree":
           flowEvent.setEvent_type(FlowEvent.EventType.DISAGREE_EVENT);
           this.approveService.approveDisagree(approveId, blockId, (String)tableId.get(i), (String)approveReason
               .get(i), dataId.get(i), flowEvent, null, null, user);
           break;
         case "hold":
           flowEvent.setEvent_type(FlowEvent.EventType.RETAIN_EVENT);
           this.approveService.approveHold(approveId, blockId, (String)tableId.get(i), (String)approveReason
               .get(i), dataId.get(i), flowEvent, null, null, user);
           break;
         case "termination":
           flowEvent.setEvent_type(FlowEvent.EventType.TERMINATION_EVENT);
           this.approveService.approveTermination(approveId, blockId, (String)tableId.get(i), (String)approveReason
               .get(i), dataId.get(i), flowEvent, null, null, user);
           break;
       } 
     } 
     return new ApiResult(0, message);
   }
   
   @ApiMethod("GC_QIEKU")
   public ApiResult gc_qieku(@ApiParam("UUID") List<String> uuid, @ApiParam("entry_id") List<String> entry_id) {
     String outerDataBase = this.adidasDao.selectOuterDataBase();
     String localDataBase = this.adidasDao.selectLocalDataBase();
     List<String> messages = new ArrayList<String>();
     try {
       if (null == entry_id.get(0) || "".equals(entry_id.get(0))) {
         messages.add((String)entry_id.get(0) + "鐨勫�间负绌猴紒");
         return new ApiResult(0, messages);
       } 
       EntryManage entryManage = this.adidasDao.findEntryManageByEntryId(entry_id.get(0));
       CodeStr codeStr = this.adidasDao.findCodeStrByNewestData();
       this.dxRoutingDataSource.setDomainKey(outerDataBase);
       messages = this.adidasService.addGcPreOnboarding(entryManage, codeStr, entry_id.get(0));
     } catch (Exception e) {
       throw new ApplicationException("aaaaaaa" + e.getMessage() + "bbbbbbbbbbbbbb");
     }
     finally {
       this.dxRoutingDataSource.setDomainKey(localDataBase);
     } 
     return new ApiResult(0, messages);
   }
   
   @ApiMethod("adidas_outer_update")
   public ApiResult adidas_outer_update(@ApiParam("UUID") List<String> uuid, @ApiParam("entry_id") List<String> entry_id) {
     System.out.println("^^^^^^^^^dingshirenwu^^^^^^^^^^^");
     String outerDataBase = this.adidasDao.selectOuterDataBase();
     String localDataBase = this.adidasDao.selectLocalDataBase();
     List<String> messages = new ArrayList<String>();
     List<ManageVO> idList = this.adidasDao.findInnerDataId();
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     try {
       messages = this.adidasService.updateDataByEntryManage(uuid, idList, outerDataBase, localDataBase);
     } catch (Exception e) {
       e.printStackTrace();
       messages.add("澶辫触锛侊紒锛�");
     }
     finally {
       this.dxRoutingDataSource.setActiveDomainKey(localDataBase);
     } 
     return new ApiResult(0, messages);
   }
   
   @ApiMethod("adidas_zhongzhi_confirm_api")
   public ApiResult adidas_zhongzhi_confirm(@ApiParam("UUID") List<String> uuid, @ApiParam("entry_id") List<String> entry_id) {
     List<String> messages = new ArrayList<String>();
     String outerDataBase = this.adidasDao.selectOuterDataBase();
     String localDataBase = this.adidasDao.selectLocalDataBase();
     List<ManageVO> voList = this.adidasDao.findInnerDataId();
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     try {
       messages = this.adidasService.adidasZhongZhiConfirm(uuid, voList, outerDataBase, localDataBase);
     } catch (Exception e) {
       messages.add("绯荤粺寮傚父");
     } finally {
       this.dxRoutingDataSource.setActiveDomainKey(localDataBase);
     } 
     return new ApiResult(0, messages);
   }
   
   @ApiMethod("local_zhongzhi_confirm")
   public ApiResult local_zhongzhi_confirm(@ApiParam("UUID") List<String> uuid, @ApiParam("v_id") List<String> ids, @ApiParam("v_shop_id") List<String> shop_ids) {
     List<String> messages = new ArrayList<String>();
     String outerDataBase = this.adidasDao.selectOuterDataBase();
     String localDataBase = this.adidasDao.selectLocalDataBase();
     if (ids.size() == 0 || shop_ids.size() == 0) {
       messages.add("鑾峰彇鍙傛暟涓虹┖");
       return new ApiResult(0, messages);
     } 
     this.dxRoutingDataSource.setDomainKey(outerDataBase);
     try {
       messages = this.adidasService.DeleteOuterDataByIdAndName(ids, shop_ids);
       messages.add("鍒犻櫎鎴愬姛锛�");
     } catch (Exception e) {
       messages.add("绯荤粺寮傚父");
     } finally {
       this.dxRoutingDataSource.setDomainKey(localDataBase);
     } 
     return new ApiResult(0, messages);
   }
   
   @ApiMethod("adidas_synchronous_city")
   public ApiResult adidas_synchronous_city(@ApiParam("UUID") List<String> uuid) {
     String outerDataBase = this.adidasDao.selectOuterDataBase();
     String localDataBase = this.adidasDao.selectLocalDataBase();
     
     List<CityDescribe> cityDescribe = this.adidasDao.selectCity();
     this.dxRoutingDataSource.setActiveDomainKey(outerDataBase);
     List<String> messages = this.adidasService.deleteInsertCity(cityDescribe, outerDataBase, localDataBase);
     return new ApiResult(0, messages);
   }
}