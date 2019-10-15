package cn.com.easyerp.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.aspose.cells.License;
import com.aspose.cells.Workbook;

public class Excel2Pdf {
    public static boolean getLicense() {
        boolean result = false;
        try {
            InputStream is = Excel2Pdf.class.getClassLoader().getResourceAsStream("license.xml");
            License aposeLic = new License();
            aposeLic.setLicense(is);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void excel2pdf(String Address) {
        if (!getLicense()) {
            return;
        }
        try {
            File pdfFile = new File("C:\\Users\\Wayne\\Desktop\\test.pdf");
            Workbook wb = new Workbook(Address);
            FileOutputStream fileOS = new FileOutputStream(pdfFile);
            wb.save(fileOS, 13);
            fileOS.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static File excel2pdf(String excelPath, String pdfPath) {
        if (!getLicense()) {
            return null;
        }
        FileOutputStream fileOS = null;
        try {
            File pdfFile = new File(pdfPath);
            Workbook wb = new Workbook(excelPath);
            fileOS = new FileOutputStream(pdfFile);
            wb.save(fileOS, 13);
            return new File(pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOS != null) {
                try {
                    fileOS.close();
                } catch (IOException e) {

                }
            }
        }
        return null;
    }
}