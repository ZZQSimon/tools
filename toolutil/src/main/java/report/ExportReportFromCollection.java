package report;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

/**
 * 
 * @author Simon Zhang
 * 
 * @param 导出excel表格
 */
public class ExportReportFromCollection<T> {
	
	/**
	 * @author Simon Zhang
	 * @param title			标题
	 * @param dateContent	第二行时间内容
	 * @param unit			单位信息   如：单位：元
	 * @param dataset		传递的数据集合
	 * @param out			输出流
	 */
	
	public void exportExcel(String title,String dateContent,String unit, Collection<T> dataset,
			OutputStream out,HttpServletRequest request) {
		// 声明一个工作薄
		try {
			// 首先检查数据看是否是正确的
			Iterator<T> its = dataset.iterator();
			if (dataset == null || !its.hasNext() || title == null
					|| out == null) {
				throw new Exception("传入的数据不对！");
			}
			// 取得实际泛型类
			T ts = (T) its.next();
			Class<?> objectClass = ts.getClass();
			WritableWorkbook book = Workbook.createWorkbook(out);
			// 生成一个表格
			WritableSheet sheet = book.createSheet(title, 0);
			// 获得变量
			Field filed[] = ts.getClass().getDeclaredFields();
			// 标题
			List<TitleObject> columnTitle = new ArrayList<TitleObject>();
			// 导出的字段的get方法
			List<Method> methodObj = new ArrayList<Method>();
			// 用于存放dateFormat
			Map<Integer, String> dateFormatMap = new HashMap<Integer, String>();
			// 填充类的信息
			// args[0] 所具有的列数 args[1]这个集合是否有父类
			int[] args = this.fillClassInfo(objectClass, filed, columnTitle,
					methodObj, sheet, dateFormatMap);

			// 行rowNum ;
			int rowNum = 0;
			// 填充excel内容
			this.fileExcelContent(title,dateContent,unit,ts, its, sheet, args, columnTitle,
					methodObj, rowNum, dateFormatMap,request);
			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void fileExcelContent(String title,String dateContent,String unit,T ts, Iterator<T> its,
			WritableSheet sheet, int[] args, List<TitleObject> columnTitle,
			List<Method> methodObj, int rowNum,
			Map<Integer, String> dateFormatMap,HttpServletRequest request) throws Exception {
		int dataSize = args[0];
		int isHaveParent = args[1];// 如果为1则为有父类
		Label label = null;
		// 产生表格标题行

		// --------------------------标题行---------------------------
		label = new Label(0, rowNum, title, ExcelStyle.getHeadStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, dataSize - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.HEAD_HEIGHT);// 设置高度
		rowNum++;

		// --------------------------时间行---------------------------
		label = new Label(0, rowNum, dateContent, ExcelStyle
				.getDateStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, dataSize - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.DATE_HEIGHT);// 设置高度
		rowNum++;
		// --------------------------单位行---------------------------
		label = new Label(0, rowNum, unit, ExcelStyle.getUnitStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, dataSize - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.UNIT_HEIGHT);// 设置高度
		rowNum++;
		// 设置exel列名称
		if (isHaveParent == 0) {// 如果没有父类的情况
			for (int i = 0; i < columnTitle.size(); i++) {
				TitleObject to = columnTitle.get(i);
				label = new Label(i, rowNum, to.getTitleChild().get(0),
						ExcelStyle.getColumnNameStyle());
				sheet.addCell(label);
			}
			sheet.setRowView(rowNum, ExcelStyle.COLUMNNAME_HEIGHT);// 设置高度
		} else {// 如果有父类的情况
			sheet.setRowView(rowNum, ExcelStyle.COLUMNNAME_PARENT);// 设置高度
			sheet.setRowView(rowNum + 1, ExcelStyle.COLUMNNAME_CHILD);// 设置高度
			int j = 0;// 记录列的实际情况
			for (int i = 0; i < columnTitle.size(); i++) {
				TitleObject to = columnTitle.get(i);
				// 首先判断父类是否为no，如果为no说明没有父类
				if ("no".equals(to.getTitleName())) {
					label = new Label(j, rowNum, to.getTitleChild().get(0),
							ExcelStyle.getColumnNameStyle());
					sheet.addCell(label);
					sheet.mergeCells(j, rowNum, j, rowNum + 1);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
					j++;
				} else {
					// 如果有父类
					String parentName = to.getTitleName();
					int childrenSize = to.getTitleChild().size();
					// 如果只有一个子类
					if (childrenSize == 1) {
						label = new Label(j, rowNum, to.getTitleChild().get(0),
								ExcelStyle.getColumnNameStyle());
						sheet.addCell(label);
						sheet.mergeCells(j, rowNum, j, rowNum + 1);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
						j++;
					} else {// 如果有多个子类
						label = new Label(j, rowNum, parentName, ExcelStyle
								.getColumnParentNameStyle());
						sheet.addCell(label);
						sheet.mergeCells(j, rowNum, j + childrenSize - 1,
								rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
						List<String> childTitle = to.getTitleChild();
						for (String childStr : childTitle) {
							label = new Label(j, rowNum + 1, childStr,
									ExcelStyle.getColumnChildNameStyle());
							sheet.addCell(label);
							j++;
						}
					}
				}
			}
		}
		if (isHaveParent == 1) {
			rowNum++;// 如果有父类，rowNum在加1
		}

		// 此集合用于缓存SimpleDateFormat对象，以提高程序性能
		Map<Integer, SimpleDateFormat> simpleDateFormatMap = new HashMap<Integer, SimpleDateFormat>();
		//首先写入第一条信息ts
		rowNum++;
		sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
		for (int k = 0; k < methodObj.size(); k++) {
			Method getMethod = methodObj.get(k);
			Object value = getMethod.invoke(ts, new Object[] {});
			String textValue = getValue(k, dateFormatMap,
					simpleDateFormatMap, value);
			label = new Label(k, rowNum, textValue, ExcelStyle
					.getBodyStyle());
			sheet.addCell(label);
		}
		// 循环整个集合
		while (its.hasNext()) {
			rowNum++;
			sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
			T t = (T) its.next();
			for (int k = 0; k < methodObj.size(); k++) {
				Method getMethod = methodObj.get(k);
				Object value = getMethod.invoke(t, new Object[] {});
				String textValue = getValue(k, dateFormatMap,
						simpleDateFormatMap, value);
				label = new Label(k, rowNum, textValue, ExcelStyle
						.getBodyStyle());
				sheet.addCell(label);
			}
		}
		//填充尾部
		int fLength=methodObj.size();
		int mergeNum=fLength/3;
		if(mergeNum<=0){
			mergeNum=1;
		}
		rowNum++;
		// --------------------------为空一行分割---------------------------
		label = new Label(0, rowNum, "", ExcelStyle.getHeadStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, fLength - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
		rowNum++;
		// --------------------------负责人签名列---------------------------
		label = new Label(0, rowNum, "负责人签名：", ExcelStyle.getBodyStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, mergeNum - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
		 // --------------------------打印人列---------------------------
		label = new Label(mergeNum, rowNum, "打印人：", ExcelStyle.getBodyStyle());
		sheet.addCell(label);
		sheet.mergeCells(mergeNum, rowNum, 2*mergeNum - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		 // --------------------------打印人列---------------------------
		label = new Label(2*mergeNum, rowNum, "打印时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), ExcelStyle.getBodyStyle());
		sheet.addCell(label);
		sheet.mergeCells(2*mergeNum, rowNum, fLength - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		rowNum++;
	}

	private int[] fillClassInfo(Class<?> objectClass, Field[] filed,
			List<TitleObject> columnTitle, List<Method> methodObj,
			WritableSheet sheet, Map<Integer, String> dateFormatMap)
			throws NoSuchMethodException {
		int args[] = new int[2];
		// 如果为0代表没有父类，如果为1则代码有父类
		int isHaveParent = 0;
		String columnParent = null;
		String columnName = null;
		String fieldname = null;
		String getMethodName = null;
		String dateFormatString = null;
		TitleObject to = null;
		int columnNum = 0;
		// 遍历整个filed
		for (int i = 0; i < filed.length; i++) {
			Field f = filed[i];
			ReportAnnotation exa = f.getAnnotation(ReportAnnotation.class);
			// 如果设置了annottion
			if (exa != null) {
				to = new TitleObject();
				// 得到时间format
				dateFormatString = exa.dateFormat();
				dateFormatMap.put(columnNum, dateFormatString);
				// 得到父标题
				columnParent = exa.columnParent();
				// 得到子标题
				columnName = exa.columnName();
				// 如果父标题为空
				if ("".equals(columnParent)) {
					// 把父类的名称设置为no
					to.setTitleName("no");
					// 直接往子类中添加
					to.getTitleChild().add(columnName);
					columnTitle.add(to);
				} else {
					boolean isHaveCommonParent = false;
					// 如果不为空先判断 columnTitle中是否有这个父类
					for (TitleObject titleObject : columnTitle) {
						// 如果有相匹配的父类
						if (titleObject.getTitleName().equals(columnParent)) {
							titleObject.getTitleChild().add(columnName);
							isHaveCommonParent = true;
							isHaveParent = 1;
							break;
						}
					}
					// 如果没匹配的父类
					if (!isHaveCommonParent) {
						to.setTitleName(columnParent);
						to.getTitleChild().add(columnName);
						columnTitle.add(to);
					}
				}

				// 设置列的宽度
				sheet.setColumnView(i, exa.width()/6);

				// 添加到需要导出的字段的方法
				fieldname = f.getName();
				getMethodName = "get" + fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1);

				Method getMethod = objectClass.getMethod(getMethodName,
						new Class[] {});

				methodObj.add(getMethod);
				// 列的数量增加
				columnNum++;
			}
		}
		// 所具有的列数
		args[0] = columnNum;
		// 这个集合是否有父类
		args[1] = isHaveParent;
		return args;
	}

	public String getValue(int k, Map<Integer, String> dateFormatMap,
			Map<Integer, SimpleDateFormat> simpleDateFormatMap, Object value) {
		String textValue = "";
		if (value == null)
			return textValue;

		if (value instanceof Boolean) {
			boolean bValue = (Boolean) value;
			textValue = "是";
			if (!bValue) {
				textValue = "否";
			}
		} else if (value instanceof Date) {
			Date date = (Date) value;
			SimpleDateFormat sdf = null;
			// 首先查询simpleDateFormatMap中是否已有simpleDateFormat
			SimpleDateFormat sdf1 = (SimpleDateFormat) simpleDateFormatMap.get(k);
			if (sdf1 != null) {
				sdf = sdf1;
			} else {
				sdf = new SimpleDateFormat(dateFormatMap.get(k));
				simpleDateFormatMap.put(k, sdf);
			}
			textValue = sdf.format(date);
		} else
			textValue = value.toString();

		return textValue;
	}


}
