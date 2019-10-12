
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Simon
 **/
public class AnalyseSW {

    public static void main(String[] args) throws IOException {
        simplify(FileSrc);
        // analyse("C:\\Users\\neusoft\\Desktop\\bb.txt");
    }

    private static final String FileSrc = "C:\\Users\\neusoft\\Desktop\\as.txt";
    private static final String FileDir = "C:\\Users\\neusoft\\Desktop\\swFile.txt";
    private static final String Change_1 = " INFO com.neusoft.aclome.monitor.infrastructures.manager.schneider.SWManager - 根据序列号获取设备信息getDeviceBySerialNumber--:";
    private static final String Change_2 = ": ";
    private static final String Change_3 = " serialNumber--";
    private static final String Change_4 = "managerId--";

    public static void analyse(String fileSource) throws IOException {
        FileReader fr = new FileReader(fileSource);
        BufferedReader br = new BufferedReader(fr);

        Set<String> fqnSet = new HashSet<String>();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            fqnSet.add(line.substring(26, line.length() - 1));
        }
        for (String fqn : fqnSet) {
            // System.out.println(fqn);
            int count = count(fileSource, fqn);
            // succeed += 1;
            // System.out.println(fqn.trim());
            System.out.println("[" + fqn + "], 出现次数     <" + count + ">");
        }
        br.close();
    }

    public static void simplify(String fileSource) throws IOException {
        FileReader fr = new FileReader(fileSource);
        BufferedReader br = new BufferedReader(fr);
        Map<String, String> map = new HashMap<String, String>();
        int rows = 0;
        int row = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            rows++;

            if (line.trim().substring(0, 1).equals("E")) {
            } else {
                row++;
                String s = new String(line.getBytes("UTF-8"), "UTF-8").replaceAll(Change_1, "")
                        .replaceAll(Change_2, "@").replaceAll(Change_3, "   SN:").replaceAll(Change_4, "  mID:");
                String[] ss = s.split("@");
                // System.out.println(ss[1].substring(0, 25));
                map.put(ss[1].substring(0, 25), ss[1]);
            }
        }

        System.out.println("文件内总行数：" + rows);
        System.out.println("满足：" + row);
        System.out.println("map大小：" + map.size());
        String[] sa = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sa);
        for (String s : sa) {
            if (s.compareTo("[2015-07-31 10:00:00,000]") == 1) {
                filePlusByWriter(FileDir, map.get(s) + "\r\n");
            }
            // System.out.println(map.get(s));
        }
        br.close();
    }

    private static int count(String filename, String target) throws FileNotFoundException, IOException {
        FileReader fr = new FileReader(filename);
        BufferedReader br = new BufferedReader(fr);
        StringBuilder strb = new StringBuilder();
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            strb.append(line);
        }
        String result = strb.toString();
        int count = 0;
        int index = 0;
        while (true) {
            index = result.indexOf(target, index + 1);
            if (index > 0) {
                count++;
            } else {
                break;
            }
        }
        br.close();
        return count;
    }

    /**
     * 追加文件：使用FileWriter
     * 
     * @param fileName
     * @param content
     * @throws IOException
     */
    private static void filePlusByWriter(String fileName, String content) throws IOException {
        FileWriter writer = new FileWriter(fileName, true);
        writer.write(content);
        writer.close();
    }
}
