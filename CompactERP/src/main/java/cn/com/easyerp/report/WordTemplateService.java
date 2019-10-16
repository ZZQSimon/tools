package cn.com.easyerp.report;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.CharacterRun;
import org.apache.poi.hwpf.usermodel.Paragraph;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Section;
import org.springframework.stereotype.Service;

import cn.com.easyerp.framework.common.Common;
import cn.com.easyerp.framework.exception.ApplicationException;

@Service
public class WordTemplateService {
    public static final String WORD_FILE_NAME_COLUMN = "filename";
    // @Autowired
    // private StorageService storageService;

    public static void main(String[] args) throws IOException {
        HWPFDocument doc = new HWPFDocument(new FileInputStream("/home/zl/tmp/a.doc"));
        WordTemplateService service = new WordTemplateService();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("aaa", "1111");
        map.put("bbb", "22222");
        map.put("ccc", "33333");
        service.process(doc, map);
        doc.write(new File("/home/zl/tmp/a2.doc"));
    }

    public HWPFDocument process(HWPFDocument doc, Map<String, Object> map) {
        Range r1 = doc.getRange();

        for (int i = 0; i < r1.numSections(); i++) {
            Section s = r1.getSection(i);
            for (int x = 0; x < s.numParagraphs(); x++) {
                Paragraph p = s.getParagraph(x);
                for (int z = 0; z < p.numCharacterRuns(); z++) {
                    CharacterRun run = p.getCharacterRun(z);
                    String text = run.text();
                    text = Common.replaceVars(text, map, false);
                    run.replaceText(text, false);
                }
            }
        }
        return doc;
    }

    public void process(ReportGeneratorCache cache, Map<String, Object> map, FileInputStream templateStream,
            OutputStream os) {
        try {
            HWPFDocument doc = process(new HWPFDocument(templateStream), map);
            doc.write(os);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String setFileName(Set<String> fns, String fn) {
        int idx = 1;
        String rfn = fn;
        while (true) {
            if (!fns.contains(rfn)) {
                fns.add(rfn);
                return rfn;
            }
            rfn = fn + "(" + ++idx + ")";
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void process(ReportGeneratorCache cache, List<Map<String, Object>> list, File template,
            ZipOutputStream zos) {
        try {
            Set<String> fns = new HashSet<String>();
            for (int i = 0; i < list.size(); i++) {
                Map<String, Object> map = (Map) list.get(i);
                HWPFDocument doc = process(new HWPFDocument(new FileInputStream(template)), map);
                String fn = (String) map.get("filename");
                if (fn != null) {
                    fn = setFileName(fns, fn);
                } else {
                    fn = String.valueOf(i + 1);
                }
                ZipEntry ze = new ZipEntry(fn + ".doc");
                zos.putNextEntry(ze);
                doc.write(zos);
                zos.flush();
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }
}
