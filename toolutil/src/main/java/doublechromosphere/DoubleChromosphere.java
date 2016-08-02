package doublechromosphere;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DoubleChromosphere {
	int[] dcno = new int[7];

	static int total = (33 * 32 * 31 * 30 * 29 * 28) / (6 * 5 * 4 * 3 * 2) * 16;

	static List<int[]> dcnoList = new ArrayList<int[]>(total);

	public static void init() throws IOException {
		int count = 0;
		for (int n0 = 1; n0 < 29; n0++) {
			int[] dc = new int[7];
			dc[0] = n0;
			for (int n1 = 2; n1 < 30; n1++) {
				if (n1 > n0) {
					dc[1] = n1;
					for (int n2 = 3; n2 < 31; n2++) {
						if (n2 > n1) {
							dc[2] = n2;
							for (int n3 = 4; n3 < 32; n3++) {
								if (n3 > n2) {
									dc[3] = n3;
									for (int n4 = 5; n4 < 33; n4++) {
										if (n4 > n3) {
											dc[4] = n4;
											for (int n5 = 6; n5 < 34; n5++) {
												if (n5 > n4) {
													dc[5] = n5;
//													for (int n6 = 1; n6 < 17; n6++) {
//														dc[6] = n6;
////														dcnoList.add(dc);
//													}
													System.out.println(++count);
													filePlusByWriter(
															"C:\\Users\\neusoft\\Desktop\\sss.txt",
															toString(dc)
															+ "\r\n");
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
			// break;
		}
	}

	public static String toString(int[] dc) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < dc.length; i++) {
			if (i == dc.length - 1) {
				sb.append("+").append(dc[i]);
			} else {
				sb.append(dc[i]).append(" ");
			}
		}
		return sb.toString();
	}

	/**
	 * 追加文件：使用FileWriter
	 * 
	 * @param fileName
	 * @param content
	 * @throws IOException
	 */
	private static void filePlusByWriter(String fileName, String content)
			throws IOException {
		FileWriter writer = new FileWriter(fileName, true);
		writer.write(content);
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		System.out.println(total);
		init();
	}

}
