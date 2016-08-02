package caipiao;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CPUtils {

	public static void main(String[] args) throws IOException {
//		System.out.println(ShuangSeQiu(10));
		System.out.println(DaLeTou(10));
//		output2File(FileSrc,10,1);
		delete(FileSrc);
		output2File(FileSrc,10,2);
		delete(FileSortByBefore);
		sortByBefore(FileSrc, FileSortByBefore);
		delete(FileSortByAfter);
		sortByAfter(FileSrc, FileSortByAfter,2);
	}
	private static final String File = "E:\\cp\\";

	private static final String FileSrc = File + "s1.txt";
	private static final String FileSortByBefore = File + "before.txt";
	private static final String FileSortByAfter = File + "after.txt";


	public static String DaLeTou(int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(random(35, 5)).append("- ").append(random(12, 2))
					.append("\n");
		}
		return sb.toString();
	}
	public static String ShuangSeQiu(int times) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < times; i++) {
			sb.append(random(33, 6)).append("- ").append(random(16, 1))
			.append("\n");
		}
		return sb.toString();
	}

	protected static String random(int totals, int count) {
		Set<Integer> set = new HashSet<Integer>();
		for (int i = 0; i < count; i++) {
			set.add((int) ((totals) * Math.random() + 1));
			if (set.size() == i) {
				i = i - 1;
			}
		}
		Integer[] h = set.toArray(new Integer[count]);
		Arrays.sort(h);
		return Integer2String(h);
	}

	private static String Integer2String(Integer[] a) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < a.length; i++) {
			if (a[i] < 10) {
				sb.append("0" + a[i] + " ");
			} else {
				sb.append(a[i] + " ");
			}
		}
		return sb.toString();
	}

	public static void output2File(String FileSrc ,int times,int type) throws IOException {
		for (int i = 0; i < times; i++) {
			if(type==1){
				filePlusByWriter(FileSrc, random(33, 6)+"- "+random(16, 1) + "\r\n");
			}else if(type==2){
				filePlusByWriter(FileSrc, random(35, 5)+"- "+random(12, 2) + "\r\n");
			}else if(type==3){
				
			}else if(type==4){
				
			}else if(type==5){
				
			}else{
				
			}
		}
	}
	
	public static void sortByAfter(String FileSrc, String FileDir,int type)
			throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> map = new HashMap<String, String>();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
			String s=null;
			if(type==1){
				s= line.substring(20) + line.substring(18, 20) + line.substring(0, 18);
			}else if(type==2){
				s= line.substring(17) + line.substring(15, 17) + line.substring(0, 15);
			}else if(type==3){
				
			}else if(type==4){
				
			}else if(type==5){
				
			}else{
				
			}
			 
			map.put(s, s);
		}
		String[] sArray = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sArray);
		for (String s : sArray) {
			filePlusByWriter(FileDir, map.get(s) + "\r\n");
		}
	}
	public static void sortByBefore(String FileSrc, String FileDir)
			throws IOException {
		FileReader fr = new FileReader(FileSrc);
		BufferedReader br = new BufferedReader(fr);
		Map<String, String> map = new HashMap<String, String>();
		while (true) {
			String line = br.readLine();
			if (line == null) {
				break;
			}
//			String s = line.substring(25) + line.substring(0, 25);
			map.put(line, line);
		}
		String[] sArray = map.keySet().toArray(new String[map.size()]);
		Arrays.sort(sArray);
		for (String s : sArray) {
			filePlusByWriter(FileDir, map.get(s) + "\r\n");
		}
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
	/**
	 * 删除文件
	 * 
	 * @param FileSrc
	 * @param content
	 * @throws IOException
	 */
	private static void delete(String FileSrc)throws IOException {
		java.io.File f = new java.io.File(FileSrc); // 输入要删除的文件位置
		if(f.exists())
		f.delete(); 
	}
}
