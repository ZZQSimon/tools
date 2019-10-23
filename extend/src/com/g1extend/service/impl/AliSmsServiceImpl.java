package com.g1extend.service.impl;

import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.g1extend.expection.ApplicationException;
import com.g1extend.service.SmsService;
import com.g1extend.utils.common;

@Service("aliSmsServer")
public class AliSmsServiceImpl implements SmsService {
    private static final Log log = LogFactory.getLog(AliSmsServiceImpl.class);

    final String product = "Dysmsapi";
    final String domain = "dysmsapi.aliyuncs.com";

    private String accessKeyId;
    private String accessKeySecret;

    private IAcsClient getAcsClient() throws ClientException {
        Properties param = common.getSystemParam();
        this.accessKeyId = param.getProperty("sms_key");
        this.accessKeySecret = param.getProperty("sms_secret");
        log.info("accessKeyId : " + accessKeyId);
        log.info("accessKeySecret : " + accessKeySecret);
        DefaultProfile defaultProfile = DefaultProfile.getProfile("cn-hangzhou", this.accessKeyId,
                this.accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dysmsapi", "dysmsapi.aliyuncs.com");
        return new DefaultAcsClient(defaultProfile);
    }

    public boolean sendSms(SendSmsRequest request) {
        try {
            return sendSms(request, getAcsClient());
        } catch (ClientException e) {
            throw new ApplicationException("link Sms server failed");
        }
    }

    public boolean sendSms(SendSmsRequest request, IAcsClient acsClient) {
        try {
            SendSmsResponse sendSmsResponse = (SendSmsResponse) acsClient.getAcsResponse(request);
            log.info("-------SendSmsResponse-------" + JSONObject.toJSONString(sendSmsResponse));
            if (sendSmsResponse.getCode() != null && sendSmsResponse.getCode().equals("OK"))
                return true;
            return false;
        } catch (Exception e) {
            throw new ApplicationException("send sms failed");
        }
    }
}
