 package cn.com.easyerp.core.sms;
 
 import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;

import cn.com.easyerp.core.cache.CacheService;
import cn.com.easyerp.core.cache.DxCache;
import cn.com.easyerp.core.dao.SystemDao;
import cn.com.easyerp.core.data.SystemParameter;
import cn.com.easyerp.core.master.DxRoutingDataSource;
 
 
 @Service("aliSmsServer")
 public class AliSmsService
   implements SmsService
 {
   @Autowired
   private CacheService cacheService;
   @Autowired
   private DxRoutingDataSource dxRoutingDataSource;
   @Autowired
   private SystemDao systemDao;
   final String product = "Dysmsapi";
   final String domain = "dysmsapi.aliyuncs.com";
   
   private String accessKeyId;
   private String accessKeySecret;
   
   private IAcsClient getAcsClient() throws ClientException {
     String currentDomainKey = this.dxRoutingDataSource.getDomainKey();
     DxCache cache = (DxCache)this.cacheService.getDxCache().get(currentDomainKey);
 
     
     SystemParameter param = (cache == null) ? this.systemDao.selectSystemParam_master() : this.cacheService.getSystemParam();
     this.accessKeyId = param.getSms_key();
     this.accessKeySecret = param.getSms_secret();
     
     DefaultProfile defaultProfile = DefaultProfile.getProfile("cn-hangzhou", this.accessKeyId, this.accessKeySecret);
     DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
     
     return new DefaultAcsClient(defaultProfile);
   }
   
   public boolean sendSms(SendSmsRequest request) {
     try {
       return sendSms(request, getAcsClient());
     } catch (ClientException e) {
       return false;
     } 
   }
 
 
 
   
   public boolean sendSms(SendSmsRequest request, IAcsClient acsClient) {
     try {
       SendSmsResponse sendSmsResponse = (SendSmsResponse)acsClient.getAcsResponse(request);
       if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))
         return true; 
       return false;
     } catch (Exception e) {
       return false;
     } 
   }
 }


