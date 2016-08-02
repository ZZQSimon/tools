package report;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;


import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;


/**
 * 报表操作类
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
public class ExportReport<T> {

	/**
	 * @author Simon Zhang
	 * @param list				需要填充的集合
	 * @param reportSessionStatus记录的综合信息
	 * @param xmlInputStream	输入流
	 * @param startNum			开始位置
	 * @param endNum			结束位置
	 * @param cl				类的类型
	 */
	public void fillCollectionDetailFromXML(List<Object> list,Map<String,String> reportSessionStatus, InputStream xmlInputStream,
			int startNum, int endNum, Class<?> cl) {


		try {
			// 取得实际泛型类
			Class<?> objectClass = cl;
			// 获得变量
			Field filed[] = objectClass.getDeclaredFields();
			// 获得有效注释变量
			
			Map<String, Field> isAvailFiledMap = new HashMap<String, Field>();
			// 获得包括计算在内的有效注释变量
			Map<String, Map<String,Integer>> otherAvailFiledMap = new HashMap<String, Map<String,Integer>>();
			//获得decode计算变量
			Map<String, Map<String,String>> decodeFiledMap = new HashMap<String, Map<String,String>>();
			// 导出的字段的set方法
			Map<String, Method> methodObj = new HashMap<String, Method>();
			// 用于存放dateFormat
			Map<String, String> dateFormatMap = new HashMap<String, String>();
			// 用于存放dateParserFormat
			Map<String, String> dateParseFormatMap = new HashMap<String, String>();
			// 填充类的信息
			fillSetMethodClassInfo(objectClass, filed, isAvailFiledMap,otherAvailFiledMap,decodeFiledMap,
					methodObj, dateFormatMap, dateParseFormatMap);
			// 开始解析xmlInputStream 把解析的结果放入list中
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			try {
				parser.parse(xmlInputStream, XMLSAXHandlerUtil.getfillCollectionDetailSAXHandler(list,reportSessionStatus, startNum, endNum,
								dateFormatMap, dateParseFormatMap,
								isAvailFiledMap,otherAvailFiledMap,decodeFiledMap, methodObj,filed ,cl));
			} catch (SAXException e) {
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * @author Simon Zhang
	 * @param list				需要填充的集合
	 * @param reportSessionStatus记录的综合信息
	 * @param xmlInputStream	输入流
	 * @param startNum			开始位置
	 * @param endNum			结束位置
	 * @param cl				类的类型
	 */
	public void fillCollectionFromXML(List<Object> list, Map<String,String> reportSessionStatus,InputStream xmlInputStream,
			int startNum, int endNum, Class<?> cl) {

		try {
			// 取得实际泛型类
			Class<?> objectClass = cl;
			// 获得变量
			Field filed[] = objectClass.getDeclaredFields();
			// 获得有效注释变量
			Map<String, Field> isAvailFiledMap = new HashMap<String, Field>();
			// 获得包括计算在内的有效注释变量
			Map<String, Map<String,Integer>> otherAvailFiledMap = new HashMap<String, Map<String,Integer>>();
			//获得decode计算变量
			Map<String, Map<String,String>> decodeFiledMap = new HashMap<String, Map<String,String>>();
			// 导出的字段的set方法
			Map<String, Method> methodObj = new HashMap<String, Method>();
			// 用于存放dateFormat
			Map<String, String> dateFormatMap = new HashMap<String, String>();
			// 用于存放dateParserFormat
			Map<String, String> dateParseFormatMap = new HashMap<String, String>();
			// 填充类的信息
			this.fillSetMethodClassInfo(objectClass, filed, isAvailFiledMap,otherAvailFiledMap,decodeFiledMap,
					methodObj, dateFormatMap, dateParseFormatMap);
			// 开始解析xmlInputStream 把解析的结果放入list中
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			try {
				parser.parse(xmlInputStream, XMLSAXHandlerUtil
						.getfillCollectionSAXHandler(list,reportSessionStatus, startNum, endNum,
								dateFormatMap, dateParseFormatMap,
								isAvailFiledMap,otherAvailFiledMap, decodeFiledMap,methodObj,filed, cl));
			} catch (SAXException e) {//捕获停止异常
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	/**
	 * @author Simon Zhang
	 * @param title 		标题
	 * @param dateContent 	报表的时间范围
	 * @param deptName		部门名称如，营业部门:餐饮中心。以:分割信息
	 * @param unit 			单位信息，单位：元
	 * @param userName 		打印人
	 * @param out 			输出流
	 * @param xmlInputStream 输入流
	 * @param startNum 		开始位置
	 * @param endNum 		结束位置
	 * @param reportSessionStatus 记录的综合统计信息
	 * @param cl
	 */
	public Object exportExcelDetailFromXML(String title, String dateContent,String deptName,
			String unit, String userName,OutputStream out, InputStream xmlInputStream,
			int startNum, int endNum,Map<String,String> reportSessionStatus, Class<?> cl) {

		// 声明一个工作薄
		try {
			// 取得实际泛型类
			Class<?> objectClass = cl;
			WritableWorkbook book = Workbook.createWorkbook(out);
			// 生成一个表格
			WritableSheet sheet = book.createSheet(title, 0);
			// 获得变量
			Field filed[] = objectClass.getDeclaredFields();
			// 获得有效注释变量
			Map<String, Field> isAvailFiledMap = new HashMap<String, Field>();
			// 获得包括计算在内的有效注释变量
			Map<String, Map<String,Integer>> otherAvailFiledMap = new HashMap<String, Map<String,Integer>>();
			//获得decode计算变量
			Map<String, Map<String,String>> decodeFiledMap = new HashMap<String, Map<String,String>>();
			// 标题
			List<TitleObject> columnTitle = new ArrayList<TitleObject>();
			// 导出的字段的get方法
			Map<String, Method> methodObj =new HashMap<String, Method>();
			// 用于存放dateFormat
			Map<String, String> dateFormatMap = new HashMap<String, String>();
			// 用于存放dateParserFormat
			Map<String, String> dateParseFormatMap = new HashMap<String, String>();
			// 填充类的信息
			// args[0] 所具有的列数 args[1]这个集合是否有父类
			int[] args = this.fillGetMethodClassInfo(objectClass, filed, columnTitle,
					methodObj, sheet, dateFormatMap, dateParseFormatMap,
					isAvailFiledMap,otherAvailFiledMap,decodeFiledMap);

			// 填充excel内容
			this.fileExcelContentDetailFromXML(title, dateContent, deptName,unit + "   ",userName,
					sheet, args, columnTitle, methodObj, dateFormatMap,
					dateParseFormatMap, isAvailFiledMap,otherAvailFiledMap,
					decodeFiledMap, xmlInputStream,
					startNum, endNum,reportSessionStatus,filed);
			book.write();
			book.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * @author Simon Zhang
	 * @param title 		标题
	 * @param dateContent 	报表的时间范围
	 * @param deptName		部门名称如，营业部门:餐饮中心。以:分割信息
	 * @param unit 			单位信息，单位：元
	 * @param userName 		打印人
	 * @param out 			输出流
	 * @param xmlInputStream 输入流
	 * @param startNum 		开始位置
	 * @param endNum 		结束位置
	 * @param reportSessionStatus 记录的综合统计信息
	 * @param cl
	 */
	public Object exportExcelFromXML(String title, String dateContent,String deptName,
			String unit,String userName, OutputStream out, InputStream xmlInputStream,
			int startNum, int endNum,Map<String,String> reportSessionStatus, Class<?> cl) {

		// 声明一个工作薄
		try {
			// 取得实际泛型类
			Class<?> objectClass = cl;
			WritableWorkbook book = Workbook.createWorkbook(out);
			// 生成一个表格
			WritableSheet sheet = book.createSheet(title, 0);
			// 获得变量
			Field filed[] = objectClass.getDeclaredFields();
			// 获得有效注释变量
			Map<String, Field> isAvailFiledMap = new HashMap<String, Field>();
			// 获得包括计算在内的有效注释变量
			Map<String, Map<String,Integer>> otherAvailFiledMap = new HashMap<String, Map<String,Integer>>();
			//获得decode计算变量
			Map<String, Map<String,String>> decodeFiledMap = new HashMap<String, Map<String,String>>();
			// 标题
			List<TitleObject> columnTitle = new ArrayList<TitleObject>();
			// 导出的字段的get方法
			Map<String, Method> methodObj = new HashMap<String, Method>();
			// 用于存放dateFormat
			Map<String, String> dateFormatMap = new HashMap<String, String>();
			// 用于存放dateParserFormat
			Map<String, String> dateParseFormatMap = new HashMap<String, String>();
			// 填充类的信息
			// args[0] 所具有的列数 args[1]这个集合是否有父类
			int[] args = this.fillGetMethodClassInfo(objectClass, filed, columnTitle,
					methodObj, sheet, dateFormatMap, dateParseFormatMap,
					isAvailFiledMap,otherAvailFiledMap,decodeFiledMap);

			// 填充excel内容
			this.fileExcelContentFromXML(title, dateContent, deptName,unit + "   ",userName,
					sheet, args, columnTitle, methodObj, dateFormatMap,
					dateParseFormatMap, isAvailFiledMap,otherAvailFiledMap,
					decodeFiledMap, xmlInputStream,
					startNum, endNum,reportSessionStatus,filed);
			book.write();
			book.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 
	 * @param objectClass
	 * @param filed
	 * @param isAvailFiledMap
	 * @param otherAvailFiledMap
	 * @param decodeFiledMap
	 * @param methodObj
	 * @param dateFormatMap
	 * @param dateParseFormatMap
	 * @throws NoSuchMethodException
	 */
	private void fillSetMethodClassInfo(Class<?> objectClass, Field[] filed,
			Map<String, Field> isAvailFiledMap,Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap, Map<String, Method> methodObj,
			Map<String, String> dateFormatMap,
			Map<String, String> dateParseFormatMap)
			throws NoSuchMethodException {
		String fieldname = null;
		String setMethodName = null;
		String dateFormatString = null;
		String dateParseFormatString = null;
		// 遍历整个filed
		for (int i = 0; i < filed.length; i++) {
			Field f = filed[i];
			ReportAnnotation exa = f.getAnnotation(ReportAnnotation.class);
			// 如果设置了annottion
			if (exa != null) {
				fieldname = f.getName();
				// 得到时间format
				dateFormatString = exa.dateFormat();
				// 填充日期format
				dateFormatMap.put(fieldname, dateFormatString);
				// 获得有效变量
				isAvailFiledMap.put(fieldname, f);
				String cal=exa.calculation();
				if(!cal.equals("null")){//需要计算
					otherAvailFiledMap.put(fieldname, getCalAtt(cal));
				}
				String decodeString=exa.decode();
				if(!decodeString.equals("null")){//需要decode计算
					decodeFiledMap.put(fieldname,getDecodeMap(decodeString));
				}
				// 填充日期解析格式
				dateParseFormatString = exa.dateParserFormat();
				dateParseFormatMap.put(fieldname, dateParseFormatString);
				
				// 添加到需要导出的字段的方法
				
				setMethodName = "set" + fieldname.substring(0, 1).toUpperCase()
						+ fieldname.substring(1);

				Method setMethod = objectClass.getMethod(setMethodName,
						new Class[] { f.getType() });

				methodObj.put(fieldname, setMethod); 
				// 列的数量增加
			}
		}
	} 
	/**
	 * 
	 * @param decodeString
	 * @return
	 */
	private static  Map<String, String> getDecodeMap(String decodeString) {
		Map<String, String> decodeMap=new HashMap<String, String>();
		String decodeStr[] = decodeString.split(",");
		boolean isCondition=true;
		String condition=null;
		int decodeLength=decodeStr.length;
		if(decodeLength%2==0){
		for (String str : decodeStr) {
			if(isCondition){
				condition = str; 
				isCondition=false;
			}else{
				decodeMap.put(condition, str);
				isCondition=true;
			}
		}
		}else{
			for (int i = 0; i < decodeLength; i++) {
				String str = decodeStr[i];
				if(isCondition){
					condition = str; 
					if(i==decodeLength-1){
						decodeMap.put("other",condition);
					}
					isCondition=false;
				}else{
					decodeMap.put(condition, str);
					isCondition=true;
				}
			}
		}
		return decodeMap;
	}
	/**
	 * 
	 * @param cal
	 * @return
	 */
	private static Map<String, Integer> getCalAtt(String cal) {
		Map<String, Integer> calAttMap=new HashMap<String, Integer>();
		char[] calChar=cal.toCharArray();
		int calLength=calChar.length;
		boolean first=false;
		StringBuilder sb=new StringBuilder();
		int operator=0;
		
		for (int i = 0; i < calLength; i++) {
			char c=calChar[i];
			if(first){
			if(c=='+'){
				calAttMap.put(sb.toString(), operator);
				operator=0;
				sb=new StringBuilder();
			}else if(c=='-'){
				calAttMap.put(sb.toString(), operator);
				operator=1;
				sb=new StringBuilder();
			}else if(c=='*'){
				calAttMap.put(sb.toString(), operator);
				operator=2;
				sb=new StringBuilder();
			}else if(c=='/'){
				calAttMap.put(sb.toString(), operator);
				operator=3;
				sb=new StringBuilder();
			}else{
				sb.append(c);
			}
			if(i==calLength-1){
				calAttMap.put(sb.toString(), operator);
			}
			}else{
				if(c=='+'||c=='-'){
					if(c=='+'){
						operator=0;
					}else if(c=='-'){
						operator=1;
					}
				}else{
					sb.append(c);
				}
			}
			first=true;
		}
		return calAttMap;
	}
	/**
	 * 
	 * @param title
	 * @param dateContent
	 * @param deptName
	 * @param unit
	 * @param userName
	 * @param sheet
	 * @param args
	 * @param columnTitle
	 * @param methodObj
	 * @param dateFormatMap
	 * @param dateParserFormatMap
	 * @param isAvailFiledMap
	 * @param otherAvailFiledMap
	 * @param decodeFiledMap
	 * @param xmlInputStream
	 * @param startNum
	 * @param endNum
	 * @param reportSessionStatus
	 * @param filed
	 * @throws Exception
	 */
	private void fileExcelContentDetailFromXML(String title, String dateContent,String deptName,
			String unit, String userName,WritableSheet sheet, int[] args,
			List<TitleObject> columnTitle, Map<String, Method>  methodObj,
			Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap, Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,InputStream xmlInputStream,
			int startNum, int endNum,Map<String,String> reportSessionStatus,Field[] filed) throws Exception {

		 int rowNum = fileExcelHead(title, dateContent,deptName, unit, sheet, args,
				columnTitle);
		// 开始解析xmlInputStream 把解析的结果放入sheet中
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		try {
			parser.parse(xmlInputStream,
					XMLSAXHandlerUtil.getExportExcelDetailSAXHandler(userName,sheet, startNum,
							endNum, rowNum, dateFormatMap, dateParserFormatMap,
							isAvailFiledMap,otherAvailFiledMap,
							decodeFiledMap,reportSessionStatus,filed));
		} catch (SAXException e) {
		}
	}
	
	/**
	 * 
	 * @param title
	 * @param dateContent
	 * @param deptName
	 * @param unit
	 * @param userName
	 * @param sheet
	 * @param args
	 * @param columnTitle
	 * @param methodObj
	 * @param dateFormatMap
	 * @param dateParserFormatMap
	 * @param isAvailFiledMap
	 * @param otherAvailFiledMap
	 * @param decodeFiledMap
	 * @param xmlInputStream
	 * @param startNum
	 * @param endNum
	 * @param reportSessionStatus
	 * @param filed
	 * @throws Exception
	 */
	private void fileExcelContentFromXML(String title, String dateContent,String deptName,
			String unit, String userName,WritableSheet sheet, int[] args,
			List<TitleObject> columnTitle, Map<String, Method>  methodObj,
			Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap, Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,InputStream xmlInputStream,
			int startNum, int endNum,Map<String,String> reportSessionStatus,Field[] filed) throws Exception {
		int rowNum = fileExcelHead(title, dateContent,deptName, unit, sheet, args,
				columnTitle);
		// 开始解析xmlInputStream 把解析的结果放入sheet中
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		try {
			parser.parse(xmlInputStream,
					XMLSAXHandlerUtil.getExportExcelSAXHandler(userName,sheet, startNum,
							endNum, rowNum, dateFormatMap, dateParserFormatMap,
							isAvailFiledMap,otherAvailFiledMap,
							decodeFiledMap,reportSessionStatus,filed));
		} catch (SAXException e) {
		}
	}
	/**
	 * 
	 * @param title
	 * @param dateContent
	 * @param deptName
	 * @param unit
	 * @param sheet
	 * @param args
	 * @param columnTitle
	 * @return
	 * @throws WriteException
	 * @throws RowsExceededException
	 */
	private int fileExcelHead(String title, String dateContent, String deptName,String unit,
			WritableSheet sheet, int[] args, List<TitleObject> columnTitle
			) throws WriteException, RowsExceededException {
		int rowNum=0;
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
		label = new Label(0, rowNum, dateContent, ExcelStyle.getDateStyle());
		sheet.addCell(label);
		sheet.mergeCells(0, rowNum, dataSize - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		sheet.setRowView(rowNum, ExcelStyle.DATE_HEIGHT);// 设置高度
		rowNum++;
		// --------------------------第三行---------------------------
		String deptStr[]=deptName.split(":");
		boolean isHaveDeptName=true;
		if(deptStr.length>1){
			if(deptStr[0]==null){
				deptStr[0]="";
				isHaveDeptName=false;
			}
			if(deptStr[1]==null){
				deptStr[1]="";
				isHaveDeptName=false;
			}
		}else{
			isHaveDeptName=false;
			deptStr=new String[2]; 
			deptStr[0]="";
			deptStr[1]="";
		}
		if(isHaveDeptName){
			deptStr[0]=deptStr[0]+"：";
		}
		// --------------------------营业部门名称：---------------------------
		label = new Label(0, rowNum, deptStr[0], ExcelStyle.getUnitNameStyle());
		sheet.addCell(label);
		sheet.setRowView(rowNum, ExcelStyle.UNIT_HEIGHT);// 设置高度
		// --------------------------餐饮中心---------------------------
		label = new Label(1, rowNum, deptStr[1], ExcelStyle.getUnitNameContentStyle());
		sheet.addCell(label);
		sheet.mergeCells(1, rowNum, dataSize - 2, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
		// --------------------------单位列---------------------------
		label = new Label(dataSize - 1, rowNum, unit, ExcelStyle.getUnitStyle());
		sheet.addCell(label);
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
		return rowNum;
	}

	/**
	 * 
	 * @param objectClass
	 * @param filed
	 * @param columnTitle
	 * @param methodObj
	 * @param sheet
	 * @param dateFormatMap
	 * @param dateParseFormatMap
	 * @param isAvailFiledMap
	 * @param otherAvailFiledMap
	 * @param decodeFiledMap
	 * @return
	 * @throws NoSuchMethodException
	 */
	private int[] fillGetMethodClassInfo(Class<?> objectClass, Field[] filed,
			List<TitleObject> columnTitle, Map<String, Method> methodObj,
			WritableSheet sheet, Map<String, String> dateFormatMap,
			Map<String, String> dateParseFormatMap,
			Map<String, Field> isAvailFiledMap,Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap) throws NoSuchMethodException {
		int args[] = new int[2];
		// 如果为0代表没有父类，如果为1则代码有父类
		int isHaveParent = 0;
		String columnParent = null;
		String columnName = null;
		String getMethodName = null;
		String dateFormatString = null;
		String dateParseFormatString = null;
		TitleObject to = null;
		// 所具有的列数
		args[0] = filed.length;
		// 遍历整个filed
		for (int i = 0; i < args[0]; i++) {
			Field f = filed[i];
			ReportAnnotation exa = f.getAnnotation(ReportAnnotation.class);
			String fName=f.getName();
			// 如果设置了annottion
			if (exa != null) {
				to = new TitleObject();
				// 得到时间format
				dateFormatString = exa.dateFormat();
				// 填充日期format
				dateFormatMap.put(fName, dateFormatString);
				// 获得有效变量
				isAvailFiledMap.put(fName, f);
				String cal=exa.calculation();
				if(!cal.equals("null")){//需要计算
					otherAvailFiledMap.put(fName, getCalAtt(cal));
				}
				String decodeString=exa.decode();
				if(!decodeString.equals("null")){//需要decode计算
					decodeFiledMap.put(fName,getDecodeMap(decodeString));
				}
				// 填充日期解析格式
				dateParseFormatString = exa.dateParserFormat();
				dateParseFormatMap.put(fName, dateParseFormatString);
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
				getMethodName = "get" + fName.substring(0, 1).toUpperCase()
						+ fName.substring(1);

				Method getMethod = objectClass.getMethod(getMethodName,
						new Class[] {});

				methodObj.put(fName, getMethod);
				// 列的数量增加
			}
		}

		// 这个集合是否有父类
		args[1] = isHaveParent;
		return args;
	}


}
