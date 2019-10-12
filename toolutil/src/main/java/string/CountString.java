package string;

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
public class CountString {

    private static String fileSrc = "E:\\rizhi0803\\s1.txt";
    private static String fileDirc = "E:\\rizhi0803\\s2.txt";

    public static void main(String[] args) throws IOException {
        // split4OutPut("C:\\Users\\neusoft\\Desktop\\111.txt");
        // outPut("C:\\Users\\neusoft\\Desktop\\111.txt");
        // take("C:\\Users\\neusoft\\Desktop\\aa.txt");
        getDatabus(fileSrc);
        anlDatabus(fileDirc);
        // takeDataBUS("E:\\rizhi0803\\t1.txt");
        // momo("E:\\rizhi0803\\qqq.txt");
    }

    public static void momo(String fileSource) throws IOException {
        FileReader fr = new FileReader(fileSource);
        BufferedReader br = new BufferedReader(fr);
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }
            // System.out.println(line.indexOf("."));
            System.out.println(line.substring(line.indexOf(".") + 1));
            // String[] ss = line.substring(line.indexOf(".")+1).split("-");
            // System.out.println(ss[0]);
        }
        br.close();
    }

    public static void takeDataBUS(String fileSource) throws IOException {
        FileReader fr = new FileReader(fileSource);
        BufferedReader br = new BufferedReader(fr);
        Map<String, String> map = new HashMap<String, String>();
        int rows = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            rows++;

            if (line.trim().substring(0, 1).equals("E")) {
            } else {
                String s = new String(line.getBytes("UTF-8"), "UTF-8")
                        .replaceAll("INFO com.neusoft.aplus.databus.modelbus.collector.ModelBusCollector - >>>", "");
                s = s.replaceAll(": ", "@");
                String[] ss = s.split("@");
                System.out.println(ss[1].substring(0, 25));
                map.put(ss[1].substring(0, 25), ss[1]);
            }
        }

        System.out.println("文件内总行数：" + rows);
        String[] sa = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sa);
        for (String s : sa) {
            // System.out.println(map.get(s));
            filePlusByWriter("E:\\rizhi0803\\t2.txt", map.get(s) + "\r\n");
        }
        br.close();
    }

    public static void anlDatabus(String fileSource) throws IOException {
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
        System.out.println("问题设备个数 :" + fqnSet.size());
        for (String fqn : fqnSet) {
            // System.out.println(fqn);
            int count = count(fileSource, fqn);
            // succeed += 1;
            // System.out.println(fqn.trim());
            // System.out.println("[" + fqn + "], 出现次数 <" + count + ">");
            // System.out.println(fqn);
            // System.out.println(count);
            System.out.println("<" + count + ">--" + "[" + fqn + "] ");

        }
        br.close();
    }

    public static void getDatabus(String fileSource) throws IOException {
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
                String s = new String(line.getBytes("UTF-8"), "UTF-8")
                        .replaceAll(" ERROR com.neusoft.aplus.databus.modelbus.collector.ModelBusCollector - 获取设备", "")
                        .replaceAll("设备监控数据失败！失败原因： 采集时产生异常：  code :11 message: null", "]");
                // System.out.println(s);
                s = s.replaceAll(": ", "@");
                String[] ss = s.split("@");
                // System.out.println(s);
                // System.out.println(ss[1]);
                map.put(ss[1], ss[1]);
                // map.put(s, s);
            }
        }

        System.out.println("文件内总行数：" + rows);
        System.out.println("满足：" + row);
        System.out.println("map大小：" + map.size());
        String[] sa = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sa);
        for (String s : sa) {
            // System.out.println(map.get(s));
            filePlusByWriter(fileDirc, map.get(s) + "\r\n");
            // int h = Integer.parseInt(map.get(s).substring(15, 17));
            // if((h>=0&&h<5)||(h>27&&h<35)||(h>57&&h<60)){
            // filePlusByWriter("E:\\rizhi0803\\s2.txt", map.get(s)+ "\r\n");
            // }else{
            //// System.out.println(h);
            // }
        }
        br.close();
    }

    public static void split4OutPut(String fileSource) {
        int succeed = 0;
        for (String fqn : getArray(fileSource)) {
            try {
                String[] a2 = fqn.trim().split("]");
                System.out.println(a2[1].trim());
                succeed += 1;
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
        }
        System.out.println("总个数:" + succeed + "个");
    }

    public static void outPut(String fileSource) {
        try {
            // int failed = 0;
            // int succeed = 0;
            for (String fqn : getArray(fileSource)) {
                int count = count(fileSource, fqn);
                // succeed += 1;
                // System.out.println(fqn.trim());
                System.out.println("[" + fqn + "], 出现次数     <" + count + ">");
                // if (count == 37) {
                // failed += 1;
                // } else {
                // succeed += 1;
                // }
            }
            // System.out.println("偶尔没有数据的设备有：" + succeed + "个");
            // System.out.println("一直没有数据的设备有：" + failed + "个");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 统计字符串target在文件filename中出现的次数
     * 
     * @param filename
     * @param target
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
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

    private static String[] getArray(String fileSource) {
        // read file content from file
        Set<String> fqnSet = new HashSet<String>();
        FileReader reader = null;
        BufferedReader br = null;
        try {
            reader = new FileReader(fileSource);
            br = new BufferedReader(reader);
            String str = null;
            while ((str = br.readLine()) != null) {
                fqnSet.add(str);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fqnSet.toArray(new String[fqnSet.size()]);
    }

    public static void take(String fileSource) throws IOException {
        FileReader fr = new FileReader(fileSource);
        BufferedReader br = new BufferedReader(fr);
        Map<String, String> map = new HashMap<String, String>();
        int rows = 0;
        while (true) {
            String line = br.readLine();
            if (line == null) {
                break;
            }

            rows++;

            if (line.trim().substring(0, 1).equals("E")) {
            } else {
                String s = line.replaceAll("INFO com.neusoft.aplus.databus.modelbus.collector.ModelBusCollector - >>>",
                        "");
                String[] ss = s.replaceAll("6个", " 6个").replaceAll("7个", " 7个").replaceAll("8个", " 8个")
                        .replaceAll("9个", " 9个").split("@");
                map.put(ss[1].substring(63, 82), ss[1]);
            }
        }

        System.out.println("文件内总行数：" + rows);
        String[] sa = map.keySet().toArray(new String[map.size()]);
        Arrays.sort(sa);
        for (String s : sa) {
            // System.out.println(map.get(s));
            filePlusByWriter("C:\\Users\\neusoft\\Desktop\\bb.txt", map.get(s) + "\r\n");
        }
        br.close();
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
