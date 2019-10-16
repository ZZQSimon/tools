package cn.com.easyerp.module.orion;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.com.easyerp.auth.AuthService;
import cn.com.easyerp.batch.BatchFormModel;
import cn.com.easyerp.batch.BatchFormRequestModel;
import cn.com.easyerp.batch.BatchService;
import cn.com.easyerp.core.cache.BatchDescribe;
import cn.com.easyerp.core.widget.FieldModelBase;
import cn.com.easyerp.core.widget.FileFieldModel;
import cn.com.easyerp.framework.common.ApiActionResult;
import cn.com.easyerp.framework.exception.ApplicationException;
import cn.com.easyerp.module.dao.OrionModuleDao;
import cn.com.easyerp.storage.StorageService;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Service("orion-import")
public class OrionImportInterceptor extends BatchService<Object> {
    private static Pattern DETAIL_REG_EXP = Pattern
            .compile("(k[ao]) \\((.*)\\) \\((.*)\\) \\((.*)\\) \\((.*)\\) ([0-9]+) ([0-9]+) \\((.*)\\) ([0-9]+)");

    @Autowired
    private StorageService storageService;
    @Autowired
    private OrionModuleDao dao;

    public static void main(String[] args) throws IOException {
        OrionImportInterceptor interceptor = new OrionImportInterceptor();
        interceptor.importFile("c252.g3", new FileInputStream(new File("/home/zl/works/dx-ywh/doc/c252.g3")), "test");
    }

    public void init(BatchDescribe batch) {
    }

    public ApiActionResult intercept(BatchDescribe batch, BatchFormRequestModel request) {
        OrionImportFormModel form = (OrionImportFormModel) request.getWidget();
        FileFieldModel file = (FileFieldModel) form.getFields().get(0);
        FileItem fi = this.storageService.getUploadFile(file.getUuid());
        try {
            importFile(fi.getName(), fi.getInputStream(), AuthService.getCurrentUserId());
        } catch (IOException e) {
            throw new ApplicationException(e);
        }

        return super.intercept(batch, request);
    }

    public BatchFormModel form(BatchFormRequestModel request, String parent, List<FieldModelBase> fields,
            BatchDescribe<Object> batch, int cols) {
        return new OrionImportFormModel(parent, fields, batch, cols);
    }

    private void importFile(String name, InputStream is, String user) throws IOException {
        this.dao.clearData(user);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, "shift_jis"));

        String[] common = null;
        String[] kaHeader = null;
        String[] koHeader = null;
        int lineNo = 0;
        String line;
        while ((line = br.readLine()) != null) {
            lineNo++;
            if (line.startsWith("--"))
                continue;
            if (line.startsWith("##")) {
                String[] data = line.substring(2).trim().replace("(", "").replace(")", "").split(" ");
                if (data.length == 0) {
                    continue;
                }
                switch (data[0]) {
                case "共通":
                    common = data;

                    if (common.length != 8)
                        throw new ApplicationException(this.dataService.getMessageText("bad format",
                                new Object[] { Integer.valueOf(lineNo) }));
                    break;
                case "加工":
                    kaHeader = data;
                    break;
                case "購入":
                    koHeader = data;
                    break;
                }
                continue;
            }
            Matcher matcher = DETAIL_REG_EXP.matcher(line);
            if (!matcher.matches()) {
                continue;
            }
            String[] data = new String[9];
            for (int i = 0; i < 9; i++)
                data[i] = matcher.group(i + 1);
            if (matcher.group(1).equals("ka")) {

                if (!insertProcess(name, data, common, kaHeader, user))
                    throw new ApplicationException(
                            this.dataService.getMessageText("bad format", new Object[] { Integer.valueOf(lineNo) }));
                continue;
            }
            if (matcher.group(1).equals("ko")) {
                if (!insertProcess(name, data, common, koHeader, user))
                    throw new ApplicationException(
                            this.dataService.getMessageText("bad format", new Object[] { Integer.valueOf(lineNo) }));
            }
        }
        br.close();
    }

    private boolean insertProcess(String name, String[] data, String[] common, String[] header, String user) {
        if (data == null || data.length != 9)
            return false;
        if (common == null)
            return false;
        this.dao.insert(name, common[0], common[1], common[2], common[3], common[4], common[5], common[6], common[7],
                header[0], header[1], header[2], header[3], header[4], data[0], data[1], data[2], data[3], data[4],
                data[5], data[6], data[7], data[8], user, user);

        return true;
    }
}
