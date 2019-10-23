package com.g1extend.service;

import com.g1extend.entity.EmailTemplate;
import com.g1extend.entity.ProbationExpire;
import java.util.Date;
import java.util.List;

public interface ProbationRemindService {
  List<ProbationExpire> getALLProbationExpire(Date paramDate);
  
  String getDirectSupervisorEmail(String paramString);
  
  EmailTemplate getEmailTemplate();
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\ProbationRemindService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */