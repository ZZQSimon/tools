package cn.com.easyerp.module.interfaces.export;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

public class ExportUtility {
    public String runTemplate(DataSource source, String tamplatePath, String tamplateFilename) {
        Properties properties = new Properties();

        properties.setProperty("resource.loader", "class");

        properties.setProperty("class.resource.loader.class",
                "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

        VelocityEngine engine = new VelocityEngine(properties);
        VelocityContext context = new VelocityContext();
        StringWriter writer = new StringWriter();
        FileReader reader = null;
        try {
            reader = new FileReader(tamplatePath + tamplateFilename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return "FileNotFound";
        }

        context.put("drl", source);
        engine.evaluate(context, writer, "drl", reader);
        return writer.toString();
    }

    public boolean saveXML(String xml, String targetPath, String targetFilename) {
        File target = new File(targetPath + targetFilename);
        if (!target.exists()) {
            try {
                target.createNewFile();
            } catch (IOException e) {

                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(target);
            OutputStreamWriter osr = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osr);
            bw.write(xml);
            bw.flush();
            bw.close();
            osr.close();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
