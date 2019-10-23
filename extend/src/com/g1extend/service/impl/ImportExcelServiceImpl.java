package com.g1extend.service.impl;

import com.g1extend.entity.MonthlyIncentiveReport;
import com.g1extend.mapper.ImportDao;
import com.g1extend.service.ImportExcelService;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class ImportExcelServiceImpl implements ImportExcelService {
    @Autowired
    private ImportDao importDao;

    public List<MonthlyIncentiveReport> importExcel(String path, MultipartFile file) {
        /* 35 */ List<MonthlyIncentiveReport> list = new ArrayList<MonthlyIncentiveReport>();
        /* 36 */ String fileName = file.getOriginalFilename();
        /* 37 */ String suffix = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        /* 38 */ String dd = (new SimpleDateFormat("yyyy-MM")).format(new Date());
        /* 39 */ String filePath = "upload" + dd + fileName;
        try {
            /* 41 */ File files = new File(String.valueOf(path) + filePath);
            /* 42 */ if (files.exists()) {
                /* 43 */ files.delete();
                /* 44 */ files.mkdirs();
            } else {
                /* 46 */ files.mkdirs();
            }
            /* 48 */ file.transferTo(files);
            /* 49 */ if ("xls".equals(suffix) || "XLS".equals(suffix)) {
                /* 50 */ list = importXls(file);
                /* 51 */ this.importDao.importFile(list);
                /* 52 */ } else if ("xlsx".equals(suffix) || "XLSX".equals(suffix)) {
                /* 53 */ list = importXlsx(file);
                /* 54 */ this.importDao.importFile(list);
            }
            /* 56 */ } catch (Exception e) {
            /* 57 */ e.printStackTrace();
        }
        /* 59 */ return list;
    }

    private List<MonthlyIncentiveReport> importXlsx(MultipartFile file) {
        /* 63 */ List<MonthlyIncentiveReport> secUserList = new ArrayList<MonthlyIncentiveReport>();
        /* 65 */ InputStream is = null;
        /* 66 */ XSSFWorkbook xWorkbook = null;
        try {
            /* 68 */ is = new FileInputStream(file.toString());
            /* 69 */ xWorkbook = new XSSFWorkbook(is);
            /* 70 */ XSSFSheet xSheet = xWorkbook.getSheetAt(0);

            /* 72 */ if (xSheet != null) {
                /* 73 */ for (int i = 0; i < xSheet.getPhysicalNumberOfRows(); i++) {
                    /* 74 */ MonthlyIncentiveReport su = new MonthlyIncentiveReport();
                    /* 75 */ XSSFRow xRow = xSheet.getRow(i);

                    /* 77 */ su.setMonthly(xRow.getCell(1).toString());
                    /* 78 */ su.setArea(xRow.getCell(2).toString());
                    /* 79 */ su.setShop_en_name(xRow.getCell(3).toString());
                    /* 80 */ su.setType(xRow.getCell(4).toString());
                    /* 81 */ su.setCity(xRow.getCell(5).toString());
                    /* 82 */ su.setEmp_id(xRow.getCell(6).toString());
                    /* 83 */ su.setCn_name(xRow.getCell(7).toString());
                    /* 84 */ su.setRank(xRow.getCell(8).toString());
                    /* 85 */ su.setIn_date(xRow.getCell(9).getDateCellValue());
                    /* 86 */ su.setOut_date(xRow.getCell(10).getDateCellValue());
                    /* 87 */ su.setSalary(xRow.getCell(11).getNumericCellValue());
                    /* 88 */ su.setKPI(xRow.getCell(12).getNumericCellValue());
                    /* 89 */ su.setPayout_ratio(xRow.getCell(13).getNumericCellValue());
                    /* 90 */ su.setBonus(xRow.getCell(14).getNumericCellValue());
                    /* 91 */ su.setCre_user(xRow.getCell(15).toString());
                    /* 92 */ su.setCre_date(xRow.getCell(16).getDateCellValue());
                    /* 93 */ su.setUpd_user(xRow.getCell(17).toString());
                    /* 94 */ su.setUpd_date(xRow.getCell(18).getDateCellValue());
                    /* 95 */ secUserList.add(su);
                }
            }
            /* 98 */ } catch (Exception e) {
            /* 99 */ e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return secUserList;
    }

    private List<MonthlyIncentiveReport> importXls(MultipartFile file) {
        List<MonthlyIncentiveReport> secUserList = new ArrayList<MonthlyIncentiveReport>();
        InputStream is = null;
        HSSFWorkbook hWorkbook = null;
        try {
            is = new FileInputStream(file.toString());
            hWorkbook = new HSSFWorkbook(is);
            HSSFSheet hSheet = hWorkbook.getSheetAt(0);

            if (hSheet != null) {
                for (int i = 0; i < hSheet.getPhysicalNumberOfRows(); i++) {
                    MonthlyIncentiveReport su = new MonthlyIncentiveReport();
                    HSSFRow hRow = hSheet.getRow(i);

                    su.setMonthly(hRow.getCell(1).toString());
                    su.setArea(hRow.getCell(2).toString());
                    su.setShop_en_name(hRow.getCell(3).toString());
                    su.setType(hRow.getCell(4).toString());
                    su.setCity(hRow.getCell(5).toString());
                    su.setEmp_id(hRow.getCell(6).toString());
                    su.setCn_name(hRow.getCell(7).toString());
                    su.setRank(hRow.getCell(8).toString());
                    su.setIn_date(hRow.getCell(9).getDateCellValue());
                    su.setOut_date(hRow.getCell(10).getDateCellValue());
                    su.setSalary(hRow.getCell(11).getNumericCellValue());
                    su.setKPI(hRow.getCell(12).getNumericCellValue());
                    su.setPayout_ratio(hRow.getCell(13).getNumericCellValue());
                    su.setBonus(hRow.getCell(14).getNumericCellValue());
                    su.setCre_user(hRow.getCell(15).toString());
                    su.setCre_date(hRow.getCell(16).getDateCellValue());
                    su.setUpd_user(hRow.getCell(17).toString());
                    su.setUpd_date(hRow.getCell(18).getDateCellValue());
                    secUserList.add(su);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return secUserList;
    }
}

/*
 * Location: E:\zzc\guxingbiao\adidas项目\外部短信接口代码\extend
 * 10.145.109.61\WEB-INF\classes\!\com\g1extend\service\impl\
 * ImportExcelServiceImpl.class Java compiler version: 8 (52.0) JD-Core Version:
 * 1.0.6
 */