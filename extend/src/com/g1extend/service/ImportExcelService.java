package com.g1extend.service;

import com.g1extend.entity.MonthlyIncentiveReport;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImportExcelService {
  List<MonthlyIncentiveReport> importExcel(String paramString, MultipartFile paramMultipartFile);
}


/* Location:              E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\ImportExcelService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.6
 */