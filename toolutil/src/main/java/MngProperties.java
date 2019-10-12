import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MngProperties {

    private static final String FileSrc = "E:\\messageResource_zh_CN.txt";
    private static final String FileDir = "E:\\messageResource_zh_CN_1.txt";
    private static final String FileDirCHG = "E:\\messageResource_zh_CN_2.txt";

    public static void runChange(String FileSrc) throws IOException {
        System.out.println("runChange...");
        FileReader fr = new FileReader(FileSrc);
        BufferedReader br = new BufferedReader(fr);
        Map<String, String> map = new HashMap<String, String>();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            line = trimCHG(line);
            map.put(line, line);
        }
        String[] sArray = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sArray);
        for (String s : sArray) {
            filePlusByWriter(FileDirCHG, map.get(s) + "\r\n");
        }
        br.close();
    }

    public static void run(String FileSrc) throws IOException {
        System.out.println("Run...");
        FileReader fr = new FileReader(FileSrc);
        BufferedReader br = new BufferedReader(fr);
        Map<String, String> map = new HashMap<String, String>();
        int rows = 0;
        int row = 0;
        int rowr = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            rows++;
            if (line.length() == 0 || line.substring(0, 1).equals("#")) {
                // System.out.println(line);
                row++;
            } else {
                rowr++;
                line = trim(line);
                map.put(line, line);
            }
        }
        String[] sArray = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sArray);
        for (String s : sArray) {
            filePlusByWriter(FileDir, map.get(s) + "\r\n");
        }
        System.out.println(row);
        System.out.println(rowr);
        System.out.println(rows);
        br.close();
    }

    private static String trim(String str) {
        String[] s = str.split("=");
        return s[0].trim() + "=" + s[1].trim();
    }

    private static String trimCHG(String str) {
        String[] s = str.split("=");
        return s[1].trim() + "=" + s[0].trim();
    }

    private static void filePlusByWriter(String fileName, String content) throws IOException {
        FileWriter writer = new FileWriter(fileName, true);
        writer.write(content);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        MngProperties.run(FileSrc);
        MngProperties.runChange(FileDir);
    }
}
