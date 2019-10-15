package cn.com.easyerp.core.sms;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;

public interface SmsService {
  boolean sendSms(SendSmsRequest paramSendSmsRequest);
}


