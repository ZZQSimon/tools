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
public class AnalyseDataBUS {

	public static void main(String[] args) throws IOException {
		doError();
		doInfo();
		// another("E:\\rizhi0806\\t1.txt");
	}

	public static void doError() throws IOException {
		simplifyError(FileSrc_ERR);
		analyseError(FileDir_ERR);
		sortFqnFile(FileDir_ERR, FileSort);
	}

	public static void doInfo() throws IOException {
		simplifyInfo(FileSrc_Info);
	}

	private static final String File = "E:\\cp\\";

	private static final String FileSrc_ERR = File + "s1.txt";
	private static final String FileDir_ERR = File + "hh\\s2.txt";
	private static final String FileSort = File + "hh\\s3.txt";

	private static final String FileSrc_Info = File + "t1.txt";
	private static final String FileDir_Info = File + "hh\\t2.txt";

	private static final String FileX = File + "hh\\xx.txt";

	private static final String Root = "E";

	private static final String RPLF = ": ";
	private static final String SPLIT = "@";

	private static final String CHGF1 = " ERROR com.neusoft.aplus.databus.modelbus.collector.ModelBusCollector - 获取设备";
	private static final String CHGT1 = "";

	private static final String CHGF2 = "设备监控数据失败！失败原因： 采集时产生异常：  code :11 message: null";
	private static final String CHGT2 = "]";

	private static final String CHGF3 = "";
	private static final String CHGT3 = "";

	private static final String CHGF4 = "";
	private static final String CHGT4 = "";

	private static final String CHGF1I = "INFO com.neusoft.aplus.databus.modelbus.collector.ModelBusCollector - >>>";
	private static final String CHGT1I = "";

	private static final String CHGF2I = "";
	private static final String CHGT2I = "";

	private static final String CHGF3I = "";
	private static final String CHGT3I = "";

	private static final String CHGF4I = "";
	private static final String CHGT4I = "";

	/**
	 * 分析简化多余的日志内容
	 * 
	 * @param FileSrc
	 * @throws IOException
	 */
	public static void simplifyInfo(String FileSrc) throws IOException {
		FileReader fr = new FileReader(FileSrc);
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
						.replaceAll(CHGF1I, CHGT1I).replaceAll(CHGF2I, CHGT2I)
						.replaceAll(CHGF3I, CHGT3I).replaceAll(CHGF4I, CHGT4I);
				s = s.replaceAll(RPLF, SPLIT);
				String[] ss = s.split(SPLIT);
				// System.out.println(ss[1].substring(0, 25));
				map.put(ss[1].substring(0, 25), ss[1]);
			}
		}

		System.out.println("文件内总行数：" + rows);
		String[] sa = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sa);
		for (String s : sa) {
			// System.out.println(map.get(s));
			filePlusByWriter(FileDir_Info, map.get(s) + "\r\n");
		}
	}

	/**
	 * 输出分析结果
	 * 
	 * @param FileSrc
	 * @throws IOException
	 */
	public static void analyseError(String FileSrc) throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> map = new HashMap<String, String>();
		Set<String> fqnSet = new HashSet<String>();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			fqnSet.add(line.substring(26, line.length() - 1));
		}
		System.out.println("analyse >>> 问题设备个数 :" + fqnSet.size());
		int total = 0;
		for (String fqn : fqnSet) {
			int count = count(FileSrc, fqn);
			total += count;
			String str = "";
			if (count < 1000 && count >= 100) {
				str = "0" + count + "[" + fqn + "] ";
			} else if (count < 100 && count >= 10) {
				str = "00" + count + "[" + fqn + "] ";
			} else if (count < 10 && count >= 1) {
				str = "000" + count + "[" + fqn + "] ";
			} else {
				str = count + "[" + fqn + "] ";
			}
			map.put(str, "[" + fqn + "] " + ">>(" + count + ")次");
			// System.out.println("<" + count + ">--" + "[" + fqn + "] ");
		}
		String[] sa = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sa);
		for (String s : sa) {
			// System.out.println(map.get(s));
			filePlusByWriter(FileX, map.get(s) + "\r\n");
		}

		System.out.println("analyse >>> count总数: " + total);
	}

	/**
	 * 分析简化多余的日志内容
	 * 
	 * @param FileSrc
	 * @throws IOException
	 */
	public static void simplifyError(String FileSrc) throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> map = new HashMap<String, String>();
		int rows = 0;// 总行数
		int row = 0;// 有效行数
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			rows++;
			if (line.trim().substring(0, 1).equals(Root)) {
			} else {
				row++;
				String s = new String(line.getBytes("UTF-8"), "UTF-8")
						.replaceAll(CHGF1, CHGT1).replaceAll(CHGF2, CHGT2)
						.replaceAll(CHGF3, CHGT3).replaceAll(CHGF4, CHGT4);
				s = s.replaceAll(RPLF, SPLIT);
				String[] sArray = s.split(SPLIT);
				map.put(sArray[1], sArray[1]);

				// System.out.println(s);
				// System.out.println(ss[1]);
				// map.put(s, s);
			}
		}

		System.out.println("simplify>>>文件内总行数：" + rows);
		System.out.println("simplify>>>满足：" + row);
		System.out.println("simplify>>>map大小：" + map.size());

		String[] sArray = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sArray);
		for (String s : sArray) {
			filePlusByWriter(FileDir_ERR, map.get(s) + "\r\n");
		}
	}

	/**
	 * 根据fqn对采集失败的时间进行排序
	 * 
	 * @param FileSrc
	 * @param FileDir
	 * @throws IOException
	 */
	public static void sortFqnFile(String FileSrc, String FileDir)
			throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> map = new HashMap<String, String>();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			String s = line.substring(25) + line.substring(0, 25);
			map.put(s, s);
		}
		String[] sArray = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sArray);
		for (String s : sArray) {
			filePlusByWriter(FileDir, map.get(s) + "\r\n");
		}
	}

	/**
	 * 统计字符串target在文件FileSrc中出现的次数
	 * 
	 * @param FileSrc
	 * @param target
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static int count(String FileSrc, String target)
			throws FileNotFoundException, IOException {
		FileReader fr = new FileReader(FileSrc);
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

	public static void another(String FileSrc) throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		// Map<String, String> map = new HashMap<String, String>();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			String[] sArray = line.split(SPLIT);
			// System.out.println(sArray[1]);
			System.out.println(sArray[1].substring(sArray[1].indexOf(".") + 1));
			// String[] ss =
			// sArray[1].substring(sArray[1].indexOf(".")+1).split("-");
			// System.out.println(ss[0]);

			// String s = line.substring(25) + line.substring(0, 25);
			// map.put(s, s);
		}
		// String[] sArray = map.keySet().toArray(new String[map.size()]);
		// Arrays.sort(sArray);
		// for (String s : sArray) {
		// filePlusByWriter(FileDir, map.get(s)+ "\r\n");
		// }
	}

	/**
	 * 追加文件：使用FileWriter
	 * 
	 * @param FileSrc
	 * @param content
	 * @throws IOException
	 */
	private static void filePlusByWriter(String FileSrc, String content)
			throws IOException {
		FileWriter writer = new FileWriter(FileSrc, true);
		writer.write(content);
		writer.close();
	}
}
