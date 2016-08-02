package report;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * SAX解析类
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
public class XMLSAXHandlerUtil {
	
	/**
	 * 
	 * 通过xml直接导出excel
	 * @param userName				操作人名称
	 * @param sheet					sheet对象
	 * @param startNum				开始位置
	 * @param endNum				结束位置
	 * @param rowNum				目前的行数
	 * @param dateFormatMap			日期format信息
	 * @param dateParserFormatMap	日期解析信息，当不为null是format和解析才有效
	 * @param isAvailFiledMap		有效的变量信息
	 * @param otherAvailFiledMap	参加计算的变量信息
	 * @param decodeFiledMap		decode计算的变量信息
	 * @param reportSessionStatus	数据综合信息
	 * @param filed					变量集合
	 * @return
	 */
	public static DefaultHandler getExportExcelSAXHandler(String userName,WritableSheet sheet,
			int startNum, int endNum, int rowNum,
			Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap,
			Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,
			Map<String,String> reportSessionStatus,Field[] filed) {
		return new ExportExcelSAXHandler(userName,sheet, startNum, endNum, rowNum,
				dateFormatMap, dateParserFormatMap, isAvailFiledMap, otherAvailFiledMap,
				 decodeFiledMap,reportSessionStatus,filed);

	}
	/**
	 * 
	 * 通过xml直接导出excel并填充reportSessionStatus
	 * @param userName				操作人名称
	 * @param sheet					sheet对象
	 * @param startNum				开始位置
	 * @param endNum				结束位置
	 * @param rowNum				目前的行数
	 * @param dateFormatMap			日期format信息
	 * @param dateParserFormatMap	日期解析信息，当不为null是format和解析才有效
	 * @param isAvailFiledMap		有效的变量信息
	 * @param otherAvailFiledMap	参加计算的变量信息
	 * @param decodeFiledMap		decode计算的变量信息
	 * @param reportSessionStatus	数据综合信息
	 * @param filed					变量集合
	 * @return
	 */
	public static DefaultHandler getExportExcelDetailSAXHandler(String userName,WritableSheet sheet,
			int startNum, int endNum, int rowNum,
			Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap,
			Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,
			Map<String,String> reportSessionStatus,Field[] filed) {
		return new ExportExcelDetailSAXHandler(userName,sheet, startNum, endNum, rowNum,
				dateFormatMap, dateParserFormatMap, isAvailFiledMap, otherAvailFiledMap,
				 decodeFiledMap,reportSessionStatus,filed);

	}
	
	/**
	 * 
	 * @param list
	 * @param reportSessionStatus
	 * @param startNum
	 * @param endNum
	 * @param dateFormatMap
	 * @param dateParserFormatMap
	 * @param isAvailFiledMap
	 * @param otherAvailFiledMap
	 * @param decodeFiledMap
	 * @param methodObj
	 * @param cl
	 * @return
	 */
	public static DefaultHandler getfillCollectionSAXHandler(List<Object> list,Map<String,String> reportSessionStatus,
			int startNum, int endNum, Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap, Map<String, 
			Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,
			Map<String, Method> methodObj,Field filed[],
			Class<?> cl) {
		return new FillCollectionSAXHandler(list,reportSessionStatus, startNum, endNum,
				dateFormatMap, dateParserFormatMap, isAvailFiledMap,otherAvailFiledMap,
				decodeFiledMap, methodObj,filed,
				cl);
	}
	/**
	 * 填充集合对象，并填充reportSessionStatus
	 * @param list
	 * @param reportSessionStatus
	 * @param startNum
	 * @param endNum
	 * @param dateFormatMap
	 * @param dateParserFormatMap
	 * @param isAvailFiledMap
	 * @param methodObj
	 * @param cl
	 * @return
	 */
	public static DefaultHandler getfillCollectionDetailSAXHandler(List<Object> list,Map<String,String> reportSessionStatus,
			int startNum, int endNum, Map<String, String> dateFormatMap,
			Map<String, String> dateParserFormatMap,
			Map<String, Field> isAvailFiledMap, 
			Map<String, Map<String,Integer>> otherAvailFiledMap,
			Map<String, Map<String,String>> decodeFiledMap,
			Map<String,Method> methodObj,Field filed[],
			Class<?> cl) {
		return new FillCollectionDetailSAXHandler(list,reportSessionStatus, startNum, endNum,
				dateFormatMap, dateParserFormatMap, isAvailFiledMap, otherAvailFiledMap,decodeFiledMap, methodObj,filed,
				cl);
	}
	/**
	 * 填充集合对象，并填充reportSessionStatus
	 * @author lyx
	 *
	 */
	static class FillCollectionDetailSAXHandler extends DefaultHandler {

		private Map<String, SimpleDateFormat> dateFormatCacheMap=null;
		private Map<String, SimpleDateFormat> dateParseFormatCacheMap=null;
		private Map<String, Field> isAvailFiledMap=null;
		private Map<String, Method> methodObj=null;
		private Map<String, String> dateParseFormatMap=null;
		private Map<String, String> dateFormatMap=null;
		private Map<String,String> reportSessionStatus=null;
		private Map<String,Double> reportDataCache=null;
		private Map<String,Double> fieldDataCache=null;
		private Map<String, Map<String,Integer>> otherAvailFiledMap=null;
		private Map<String, Map<String,String>> decodeFiledMap=null;
		private Field filed[]=null;
		private Class<?> cl;
		private int startNum;
		private int endNum;
		private int endNumSubOne;
		private int i = 0;
		private List<Object> list;
		
//		private int columnNum = 0;
		private boolean startParse=true;
		private boolean exportEnd=false;
		
		public FillCollectionDetailSAXHandler(List<Object> list,Map<String,String> reportSessionStatus, int startNum, int endNum,
				Map<String, String> dateFormatMap,
				Map<String, String> dateParserFormatMap,
				Map<String, Field> isAvailFiledMap,
				Map<String, Map<String,Integer>> otherAvailFiledMap, 
				Map<String, Map<String,String>> decodeFiledMap,
				Map<String,Method> methodObj,Field filed[],
				Class<?> cl) {
			this.dateFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.dateParseFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.reportDataCache=new HashMap<String, Double>();
			this.fieldDataCache=new HashMap<String, Double>();
			this.startNum = startNum;
			this.endNum = endNum;
			this.endNumSubOne=endNum-1;
			this.isAvailFiledMap = isAvailFiledMap;
			this.methodObj = methodObj;
			this.cl = cl;
			this.list = list;
			this.dateFormatMap = dateFormatMap;
			this.dateParseFormatMap = dateParserFormatMap;
			this.reportSessionStatus=reportSessionStatus;
			this.otherAvailFiledMap=otherAvailFiledMap;
			this.decodeFiledMap=decodeFiledMap;
			this.filed=filed;
			
		}
		public void endDocument(){
			
		}
		public void startDocument(){
			Map<String, Integer> datamap=null;
			Set<String> key2=null;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	datamap=otherAvailFiledMap.get((String)it.next());
			        	key2 = datamap.keySet();
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	String fName=(String)it2.next();
				        	reportDataCache.put(fName, 0d);
				        }
				    }
		}
		
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try {
				String dataType=null;
					if ( i >= startNum &&startParse&&attributes.getLength() > 0&&endNum>0) {
						Object obj = cl.newInstance();// 实例化类
						for (int i = 0; i < attributes.getLength(); i++) {
							String name = attributes.getQName(i).trim();
							String value = attributes.getValue(i).trim();
							Field f = isAvailFiledMap.get(name);
					
							if (f != null && name.trim().equals(f.getName())) {
								String fName=f.getName();
								dataType=f.getType().toString();
								//判断数据类型，如果是double类型就进行计算
								if(dataType.equals("double")){
									Double data=reportDataCache.get(fName);
									fieldDataCache.put(name,Double.valueOf(value));
									if(data!=null){
										reportDataCache.put(fName, MathUtil.add(Double.valueOf(value), data.doubleValue()));
									}else{
										reportDataCache.put(fName, Double.valueOf(value));
									}
								}
								Method setMethod = methodObj.get(name);
								String dateParserFormatStr = dateParseFormatMap.get(name);
								// 如果不为空，则说明需要启用解析时间字符串
								if (!"null".equals(dateParserFormatStr)) {
									String dateFormatStr = dateFormatMap.get(name);
									SimpleDateFormat dateParseFormat = null;
									SimpleDateFormat dateFormat = null;
									try {
										// 如果解析和格式化的字符不一样
										if (!dateParserFormatStr.equals(dateFormatStr)) {
											dateParseFormat = dateParseFormatCacheMap.get(name);
											// 如果解析format不等于空
											if (dateParseFormat != null) {
												dateFormat = dateFormatCacheMap.get(name);
												value = dateFormat.format(dateParseFormat.parse(value));
											} else {
												dateParseFormat = new SimpleDateFormat(dateParserFormatStr);
												dateFormat = new SimpleDateFormat(dateFormatStr);
												// 放入缓存
												dateParseFormatCacheMap.put(name, dateParseFormat);
												dateFormatCacheMap.put(name,dateFormat);
												value = dateFormat.format(dateParseFormat.parse(value));
											}
										}
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								Map<String, String> decodeFiled=decodeFiledMap.get(name);
								if(decodeFiled!=null){
									String decodeValue=decodeFiled.get(value);
									if(decodeValue!=null){
										value=decodeValue;
									}else{
										value=decodeFiled.get("other");
									}
								}
								if(f.getType().toString().equals("double")){
									setMethod.invoke(obj, new Object[] { Double.parseDouble(value) });
									}else{
										setMethod.invoke(obj, new Object[] { value });
									}
//								columnNum++;
							}else{//如果和变量名称没有匹配的
								Double data=reportDataCache.get(name);
								
								if(data!=null){//如果不等于空，说明有数据计算
									fieldDataCache.put(name,Double.valueOf(value));
									if(data.doubleValue()==0d){
										reportDataCache.put(name, Double.valueOf(value));
									}else{
									reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data.doubleValue()));
									}
								}
							}
						}
						//把reportDataCache中的计算数据提取出来
						calData(list,obj);
						list.add(obj);
				}else{//如果startParse=false;
					if(attributes.getLength() > 0){
						for (int i = 0; i < attributes.getLength(); i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							Field f = isAvailFiledMap.get(name);
							
							if (f != null && name.trim().equals(f.getName())) {
								String fName=f.getName();
								dataType=f.getType().toString();
								//判断数据类型，如果是double类型就进行计算
								if(dataType.equals("double")){
									Double data=reportDataCache.get(fName);
									if(data!=null){
										reportDataCache.put(fName, MathUtil.add(Double.valueOf(value), data.doubleValue()));
										}else{
										reportDataCache.put(fName, Double.valueOf(value));
									}
								}
							}else{//如果和变量名称没有匹配的
								Double data=reportDataCache.get(name);
								if(data!=null){
									if(data.doubleValue()==0d){
										reportDataCache.put(name, Double.valueOf(value));
									}else{
									reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data.doubleValue()));
									}
								}
							}
						
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void calData(List<Object> list,Object obj) throws  Exception {
			Map<String, Integer> datamap=null;
			Set<String> key2=null;
			String name=null;
			String fieldName=null;
			int operator=0;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	fieldName=(String)it.next();
			        	datamap=otherAvailFiledMap.get(fieldName);
			        	Method setMethod=methodObj.get(fieldName);
			        	key2 = datamap.keySet();
			        	double sum=0d;
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	name=(String)it2.next();
				        	operator=datamap.get(name);
				        	Double d=fieldDataCache.get(name);
				        	//只支持加减
				        	
				        	if(operator==0){//加
				        		sum=MathUtil.add(sum, d);
							}else if(operator==1){//减
								sum=MathUtil.sub(sum, d);
							}
				        	
				        }
				        setMethod.invoke(obj, new Object[] { new Double(sum) }); 
				      }
			        
		}
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(qName.equals("root")){
				//当结束文档的时候
				reportSessionStatus.put("totalAccount", String.valueOf(i));
				Object obj = null;// 实例化类
				Map<String, Integer> datamap=null;
				Set<String> key2=null;
				String name=null;
				String fieldName=null;
				int operator=0;
					 Set<String> key = otherAvailFiledMap.keySet();
				        for (Iterator<String> it = key.iterator(); it.hasNext();) {
				        	fieldName=(String)it.next();
				        	datamap=otherAvailFiledMap.get(fieldName);
				        	key2 = datamap.keySet();
				        	double sum=0d;
					        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
					        	name=(String)it2.next();
					        	operator=datamap.get(name);
					        	Double d=reportDataCache.get(name);
					        	//只支持加减
					        	if(operator==0){//加
					        		sum=MathUtil.add(sum, d);
								}else if(operator==1){//减
									sum=MathUtil.sub(sum, d);
								}
					        }
					        reportDataCache.put(fieldName, sum);
					      }
				      //放入到reportSessionStatus
						Set<String> reportDatakey = reportDataCache.keySet();
				        for (Iterator<String> it = reportDatakey.iterator(); it.hasNext();) {
				            String s = (String)it.next() ;
				            reportSessionStatus.put(s,String.valueOf(reportDataCache.get(s)));
				        }
				if(!exportEnd){
					try {
						obj = cl.newInstance();
						Set<String> availFiledkey = isAvailFiledMap.keySet();
				        for (Iterator<String> it = availFiledkey.iterator(); it.hasNext();) {
				        	String s = (String)it.next() ;
				            Method setMethod = methodObj.get(s);
				            Double dataSum=reportDataCache.get(s);
						          if(dataSum!=null&&setMethod!=null){
						        	setMethod.invoke(obj, new Object[] { dataSum.doubleValue() });
				            }
				        }
					if(this.filed[0].getType()==String.class){
						String filedFirstName=this.filed[0].getName();
						Method setMethod = methodObj.get(filedFirstName);
						setMethod.invoke(obj, new Object[] { "总计："});
					}
					
			        
					 if(this.filed[1].getType()==String.class){
							String filedFirstName=this.filed[1].getName();
							Method setMethod = methodObj.get(filedFirstName);
							setMethod.invoke(obj, new Object[] { "共"+reportSessionStatus.get("totalAccount")+"条数据"});
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					list.add(obj);
				}
				
				//抛异常终止
			 throw new SAXException();
			}
			if (i >=endNumSubOne) {
				startParse=false;
			}else{
				startParse=true;
			}
			if(i>=endNum){
				exportEnd=true;
			}
			if(qName.equals("entity")){
				i++;
			}
		}
	
	}
	/**
	 * 
	 * 通过reportSessionStatus来填充集合
	 * 如果需要的数据包含最后一条，需要在原来totalAccount基础上加1
	 * @author lyx
	 *
	 */
	static class FillCollectionSAXHandler extends DefaultHandler {
		private Map<String, SimpleDateFormat> dateFormatCacheMap=null;
		private Map<String, SimpleDateFormat> dateParseFormatCacheMap=null;
		private Map<String, Field> isAvailFiledMap=null;
		private Map<String, Method> methodObj=null;
		private Map<String, String> dateParseFormatMap=null;
		private Map<String, String> dateFormatMap=null;
		private Map<String,String> reportSessionStatus=null;
		private Map<String, Map<String,Integer>> otherAvailFiledMap=null;
		private Map<String,Double> fieldDataCache=null;
		private Map<String, Map<String,String>> decodeFiledMap=null;
		private Field filed[]=null;
		
		private Class<?> cl;
		private int startNum;
		private int endNum;
		private int i = 0;
		private List<Object> list;
//		private int columnNum = 0;
		private int dataNum;
		public FillCollectionSAXHandler(List<Object> list,
				Map<String,String> reportSessionStatus, int startNum, int endNum,
				Map<String, String> dateFormatMap,
				Map<String, String> dateParserFormatMap,
				Map<String, Field> isAvailFiledMap,
				Map<String, Map<String,Integer>> otherAvailFiledMap,
				Map<String, Map<String,String>> decodeFiledMap,
				Map<String, Method> methodObj,Field filed[],
				Class<?> cl) {
			this.dateFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.dateParseFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.fieldDataCache=new HashMap<String, Double>();
			this.startNum = startNum;
			this.endNum = endNum-1;
			this.isAvailFiledMap = isAvailFiledMap;
			this.methodObj = methodObj;
			this.cl = cl;
			this.list = list;
			this.dateFormatMap = dateFormatMap;
			this.dateParseFormatMap = dateParserFormatMap;
			this.reportSessionStatus=reportSessionStatus;
			this.otherAvailFiledMap=otherAvailFiledMap;
			this.decodeFiledMap=decodeFiledMap;
			this.filed=filed;
			this.dataNum=Integer.parseInt(reportSessionStatus.get("totalAccount"))-1;
		}

		public void startDocument(){
			Map<String, Integer> datamap=null;
			Set<String> key2=null;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	datamap=otherAvailFiledMap.get((String)it.next());
			        	key2 = datamap.keySet();
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	fieldDataCache.put((String)it2.next(), 0d);
				        }
				    }
		}
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			try {
				if (i >= startNum && attributes.getLength() > 0&&endNum>=0) {
					
					Object obj = cl.newInstance();// 实例化类
					for (int i = 0; i < attributes.getLength(); i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						Field f = isAvailFiledMap.get(name);
						if (f != null && name.trim().equals(f.getName())) {
							Method setMethod = methodObj.get(name);
							String dateParserFormatStr = dateParseFormatMap.get(name);
							// 如果不为空，则说明需要启用解析时间字符串
							if (!"null".equals(dateParserFormatStr)) {
								String dateFormatStr = dateFormatMap.get(name);
								SimpleDateFormat dateParseFormat = null;
								SimpleDateFormat dateFormat = null;
								try {
									// 如果解析和格式化的字符不一样
									if (!dateParserFormatStr.equals(dateFormatStr)) {
										dateParseFormat = dateParseFormatCacheMap.get(name);
										// 如果解析format不等于空
										if (dateParseFormat != null) {
											dateFormat = dateFormatCacheMap.get(name);
											value = dateFormat.format(dateParseFormat.parse(value));
										} else {
											dateParseFormat = new SimpleDateFormat(dateParserFormatStr);
											dateFormat = new SimpleDateFormat(dateFormatStr);
											// 放入缓存
											dateParseFormatCacheMap.put(name, dateParseFormat);
											dateFormatCacheMap.put(name,dateFormat);
											value = dateFormat.format(dateParseFormat.parse(value));
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							}
							Map<String, String> decodeFiled=decodeFiledMap.get(name);
							if(decodeFiled!=null){
								String decodeValue=decodeFiled.get(value);
								if(decodeValue!=null){
									value=decodeValue;
								}else{
									value=decodeFiled.get("other");
								}
							}
							if(f.getType().toString().equals("double")){
								fieldDataCache.put(name,Double.valueOf(value));
								setMethod.invoke(obj, new Object[] { Double.parseDouble(value) });
								}else{
									setMethod.invoke(obj, new Object[] { value });
								}
//							columnNum++;
						}else{//如果和变量名称没有匹配的
							Double data=fieldDataCache.get(name);
							
							if(data!=null){//如果不等于空，说明有数据计算
								fieldDataCache.put(name,Double.valueOf(value));
							}
						}
					}
					//把fieldDataCache中的计算数据提取出来
					calData(list,obj);
					list.add(obj);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void calData(List<Object> list, Object obj) throws Exception {

			Map<String, Integer> datamap=null;
			Set<String> key2=null;
			String name=null;
			String fieldName=null;
			int operator=0;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	fieldName=(String)it.next();
			        	datamap=otherAvailFiledMap.get(fieldName);
			        	Method setMethod=methodObj.get(fieldName);
			        	key2 = datamap.keySet();
			        	double sum=0d;
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	name=(String)it2.next();
				        	operator=datamap.get(name);
				        	Double d=fieldDataCache.get(name);
				        	//只支持加减
				        	if(operator==0){//加
				        		sum=MathUtil.add(sum, d);
							}else if(operator==1){//减
								sum=MathUtil.sub(sum, d);
							}
				        	
				        }
				        setMethod.invoke(obj, new Object[] { new Double(sum) }); 
				      }
			        
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(qName.equals("root")||dataNum<=i){

				//当结束文档的时候
				Object obj=null;
				try {
					obj = cl.newInstance();
					Set<String> key = isAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			            String s = (String)it.next() ;
			            Field f=isAvailFiledMap.get(s);
			            Method setMethod = methodObj.get(s);
			            String dataSum=reportSessionStatus.get(s);
					    if(dataSum!=null){
					        String typeStr=f.getType().toString();
					        if(typeStr.equals("double")){
					        	setMethod.invoke(obj, new Object[] {Double.parseDouble(dataSum)});
					        }
			            }
			        }
			        if(this.filed[0].getType()==String.class){
						String filedFirstName=this.filed[0].getName();
						Method setMethod = methodObj.get(filedFirstName);
						setMethod.invoke(obj, new Object[] { "总计："});
					}
			        if(this.filed[1].getType()==String.class){
						String filedFirstName=this.filed[1].getName();
						Method setMethod = methodObj.get(filedFirstName);
						setMethod.invoke(obj, new Object[] { "共"+reportSessionStatus.get("totalAccount")+"条数据"});
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				list.add(obj);
			throw new SAXException();
			}
			if(i >= endNum){
				throw new SAXException();
			}
//			columnNum = 0;
			if(qName.equals("entity")){
				i++;
			}
		}
	}
	
	/**
	 * 通过xml直接解析导出excel文档,并填充reportSessionStatus
	 * @author lyx
	 *
	 */
	static class ExportExcelDetailSAXHandler extends DefaultHandler {

		private Map<String,Double> fieldDataCache=null;
		private Map<String, SimpleDateFormat> dateFormatCacheMap = null;
		private Map<String, SimpleDateFormat> dateParseFormatCacheMap = null;
		private Map<String, Field> isAvailFiledMap = null;
		private Map<String, String> dateFormatMap = null;
		private Map<String, String> dateParserFormatMap = null;
		private Map<String,String> reportSessionStatus=null;
		private Map<Integer,Integer> sortMap=null;
		private Map<String, Map<String,Integer>> otherAvailFiledMap=null;
		private Map<String, Map<String,String>> decodeFiledMap=null;
		private Map<String,Integer> calculateFiledLocationMap=null;
		private Map<String,Double> reportDataCache=null;
		
		private WritableSheet sheet = null;
		private int i = 0;
		private int startNum = 0;
		private int endNum = 0;
		private int rowNum = 0;
		private boolean isFirstParse=false;
		private boolean startParse=false;
		private boolean exportEndDoc=true;
		private boolean exportEndDoc2=false;
		private Field filed[]=null;
		private String userName=null;
		/**
		 * 用于记录列数
		 */
		//private int columnNum = 0;
		
		public ExportExcelDetailSAXHandler(String userName,WritableSheet sheet, int startNum,
				int endNum, int rowNum, Map<String, String> dateFormatMap,
				Map<String, String> dateParserFormatMap,
				Map<String, Field> isAvailFiledMap,Map<String, Map<String,Integer>> otherAvailFiledMap,
				Map<String, Map<String,String>> decodeFiledMap,Map<String,String> reportSessionStatus,Field[] filed) {
			this.reportDataCache=new HashMap<String, Double>();
			this.dateFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.dateParseFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.fieldDataCache=new HashMap<String, Double>();
			this.calculateFiledLocationMap=new HashMap<String, Integer>();
			this.sortMap=new HashMap<Integer, Integer>();
			this.isAvailFiledMap = isAvailFiledMap;
			this.sheet = sheet;
			this.startNum = startNum;
			this.endNum = endNum - 1;
			this.rowNum = rowNum;
			this.dateFormatMap = dateFormatMap;
			this.dateParserFormatMap = dateParserFormatMap;
			this.reportSessionStatus=reportSessionStatus;
			this.filed=filed;
			this.userName=userName;
			this.otherAvailFiledMap=otherAvailFiledMap;
			this.decodeFiledMap=decodeFiledMap;
			this.rowNum++;
			if (i >= startNum&&i <= endNum&&startNum<endNum) {
				startParse=true;
			}
		}
		public void startDocument(){
			Map<String, Integer> datamap=null;
			Set<String> key2=null;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	datamap=otherAvailFiledMap.get((String)it.next());
			        	key2 = datamap.keySet();
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	fieldDataCache.put((String)it2.next(), 0d);
				        }
				    }
			        int fieldLength=filed.length;
			        String fName=null;
			        for (int i = 0; i < fieldLength; i++) {
			        	fName=filed[i].getName();
						if(otherAvailFiledMap.get(fName)!=null){
							calculateFiledLocationMap.put(fName, i);
						}
					}
		}
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			Label label = null;
			try {
				Field f=null;
				String textValue=null;
				int attLength=attributes.getLength();
				if(attLength>0){
					if(!isFirstParse){//如果是第一解析
						sortAttribute(attributes);
						isFirstParse=true;
						}
				}
				if (i >= startNum &&startParse&&attLength > 0&&endNum>0) {
					
					for (int i = 0; i <attLength; i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						// 如果属性名称和有效变量名称相同才计算
						f=isAvailFiledMap.get(name);
						if (f!=null&&name.equals(f.getName())) {
							if(f.getType().toString().equals("double")){
								fieldDataCache.put(name,Double.valueOf(value));
								Double data=reportDataCache.get(f.getName());
								if(data!=null){
									reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data.doubleValue()));
								}else{
									reportDataCache.put(name, Double.valueOf(value));
								}
							}
							textValue = getValue(name,value);
							label = new Label(sortMap.get(i), rowNum, textValue,ExcelStyle.getBodyStyle());
							sheet.addCell(label);
						}else{//如果和变量名称没有匹配的
							Double data=fieldDataCache.get(name);
							
							if(data!=null){//如果不等于空，说明有数据计算
								Double data2=reportDataCache.get(name);
								if(data2!=null){
									reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data2.doubleValue()));
								}else{
									reportDataCache.put(name, Double.valueOf(value));
								}
								fieldDataCache.put(name,Double.valueOf(value));
							}
						}
					}
					calculateData();
					sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
					rowNum++;
				}else{//当不需要放入sheet的情况
					if(attLength>0){
						for (int i = 0; i <attLength; i++) {
							String name = attributes.getQName(i);
							String value = attributes.getValue(i);
							// 如果属性名称和有效变量名称相同才计算
							f=isAvailFiledMap.get(name);
							if (f!=null&&name.equals(f.getName())) {
								if(f.getType().toString().equals("double")){
									
									Double data=reportDataCache.get(f.getName());
									if(data!=null){
										reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data.doubleValue()));
									}else{
										reportDataCache.put(name, Double.valueOf(value));
									}
								}
							}else{//如果和变量名称没有匹配的
								Double data=fieldDataCache.get(name);
								if(data!=null){//如果不等于空，说明有数据计算
									Double data2=reportDataCache.get(name);
									if(data2!=null){
										reportDataCache.put(name, MathUtil.add(Double.valueOf(value), data2.doubleValue()));
									}else{
										reportDataCache.put(name, Double.valueOf(value));
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void calculateData() throws  Exception {

			Map<String, Integer> datamap=null;
			Set<String> key2=null;
			String name=null;
			String fieldName=null;
			int operator=0;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	fieldName=(String)it.next();
			        	datamap=otherAvailFiledMap.get(fieldName);
			        	key2 = datamap.keySet();
			        	double sum=0d;
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	name=(String)it2.next();
				        	operator=datamap.get(name);
				        	Double d=fieldDataCache.get(name);
				        	//只支持加减
				        	if(operator==0){//加
				        		sum=MathUtil.add(sum, d);
							}else if(operator==1){//减
								sum=MathUtil.sub(sum, d);
							}
				        }
						sheet.addCell(new Label(calculateFiledLocationMap.get(fieldName), rowNum, String.valueOf(sum),ExcelStyle.getBodyStyle()));
				      }
		}
		private void sortAttribute(Attributes attributes) {
			Field f=null;
			int fLength=filed.length;
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				// 如果属性名称和有效变量名称相同才计算
				for (int j = 0; j < fLength; j++) {
					f=filed[j];
					if(f.getName().equals(name)){
						sortMap.put(i, j);
					}
				}
				}
			
		}
		private String getValue(String name,String value) {
			SimpleDateFormat dateParseFormat = null;
			SimpleDateFormat dateFormat = null;
			Map<String, String> decodeFiled=null;
			// 如果解析方式不为null
			String dateParserFormatStr = dateParserFormatMap.get(name);
			String dateFormatStr = dateFormatMap.get(name);
			decodeFiled=decodeFiledMap.get(name);
			
			if(decodeFiled!=null){
				String decodeValue=decodeFiled.get(value);
				if(decodeValue!=null){
					value=decodeValue;
				}else{
					value=decodeFiled.get("other");
				}
			}else if (!"null".equals(dateParserFormatStr)) {// 如果解析不为空，说明为日期类型
				try {
					if (!dateParserFormatStr.equals(dateFormatStr)) {// 如果解析和格式化的字符不一样
						dateParseFormat = dateParseFormatCacheMap.get(name);
						// 如果解析format不等于空
						if (dateParseFormat != null) {
							dateFormat = dateFormatCacheMap.get(name);
							value = dateFormat.format(dateParseFormat
									.parse(value));
						} else {
							dateParseFormat = new SimpleDateFormat(
									dateParserFormatStr);
							dateFormat = new SimpleDateFormat(dateFormatStr);
							// 放入缓存
							dateParseFormatCacheMap.put(name,
									dateParseFormat);
							dateFormatCacheMap.put(name, dateFormat);
							value = dateFormat.format(dateParseFormat
									.parse(value));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return value;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(qName.equals("root")){
				Map<String, Integer> datamap=null;
				Set<String> key2=null;
				String name=null;
				String fieldName=null;
				int operator=0;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	fieldName=(String)it.next();
			        	datamap=otherAvailFiledMap.get(fieldName);
			        	key2 = datamap.keySet();
			        	double sum=0d;
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	name=(String)it2.next();
				        	operator=datamap.get(name);
				        	Double d=reportDataCache.get(name);
				        	//只支持加减
				        	if(d!=null&&d.doubleValue()!=0){
				        		if(operator==0){//加
					        		sum=MathUtil.add(sum, d);
								}else if(operator==1){//减
									sum=MathUtil.sub(sum, d);
								}
					        	
				        	}
				        }
				        reportDataCache.put(fieldName, sum);
				      }
			      //放入到reportSessionStatus
					Set<String> reportDatakey = reportDataCache.keySet();
			        for (Iterator<String> it = reportDatakey.iterator(); it.hasNext();) {
			            String s = (String)it.next() ;
			            reportSessionStatus.put(s,String.valueOf(reportDataCache.get(s)));
			        }
			        reportSessionStatus.put("totalAccount", String.valueOf(i));
				if(exportEndDoc){
					//填充文档尾
					exportEndDoc2=true;
					Label label = null;
					int fLength=0;
					try {
						//填充汇总行
						fLength=filed.length;
						String dataSum=null;
						String fName=null;
						for (int j = 0; j < fLength; j++) {
							fName=filed[j].getName();
							dataSum=reportSessionStatus.get(fName);
							if(dataSum==null){
								dataSum="";
							}
								if(j==0){
									sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度 
						        	   label = new Label(j, rowNum, "总计：",ExcelStyle.getBodyStyle());
										sheet.addCell(label);
								}else if(j==1){
									if(dataSum.equals("")){
							        	   label = new Label(j, rowNum, "共"+reportSessionStatus.get("totalAccount")+"条数据",ExcelStyle.getBodyStyle());
											sheet.addCell(label);
									}else{
							        	   label = new Label(j, rowNum, dataSum,ExcelStyle.getBodyStyle());
											sheet.addCell(label);
									}
								}else{
						        	   label = new Label(j, rowNum, dataSum,ExcelStyle.getBodyStyle());
										sheet.addCell(label);
								}
						}
				        rowNum++;
				        //填充底部
				        fillEndDoc(fLength);
					} catch (Exception e) {
						e.printStackTrace();
					} 
				
				}
			}
			// 元素结束
			//System.out.println("i:"+i+"   startNum:"+startNum+"   endNum:"+endNum);
			if (i <=endNum-1) {
				startParse=true;
			}else{
				try {
					if(i==endNum+1&&!exportEndDoc2){
						exportEndDoc=false;
						fillEndDoc(filed.length);
					}
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				startParse=false;
				
			}
				i++;
		}
		private void fillEndDoc(int fLength) throws WriteException,
				RowsExceededException {
			Label label;
			int mergeNum=fLength/3;
			if(mergeNum<=0){
				mergeNum=1;
			}
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
			label = new Label(mergeNum, rowNum, "打印人："+userName, ExcelStyle.getBodyStyle());
			sheet.addCell(label);
			sheet.mergeCells(mergeNum, rowNum, 2*mergeNum - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
			 // --------------------------打印人列---------------------------
			label = new Label(2*mergeNum, rowNum, "打印时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), ExcelStyle.getBodyStyle());
			sheet.addCell(label);
			sheet.mergeCells(2*mergeNum, rowNum, fLength - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
			rowNum++;
		}
	
	}
	/**
	 * 通过xml直接解析导出excel文档
	 * @author lyx
	 *
	 */
	static class ExportExcelSAXHandler extends DefaultHandler {
		private Map<String,Double> fieldDataCache=null;
		private Map<String, SimpleDateFormat> dateFormatCacheMap = null;
		private Map<String, SimpleDateFormat> dateParseFormatCacheMap = null;
		private Map<String, Field> isAvailFiledMap = null;
		private Map<String, String> dateFormatMap = null;
		private Map<String, String> dateParserFormatMap = null;
		private Map<String,String> reportSessionStatus=null;
		private Map<Integer,Integer> sortMap=null;
		private Map<String, Map<String,Integer>> otherAvailFiledMap=null;
		private Map<String, Map<String,String>> decodeFiledMap=null;
		private Map<String,Integer> calculateFiledLocationMap=null;
		
		private WritableSheet sheet = null;
		private int i = 0;
		private int startNum = 0;
		private int endNum = 0;
		private int rowNum = 0;
		private int dataNum=0;
		private boolean isFirstParse=false;
		private Field filed[]=null;
		private String userName=null;
		private boolean exportEndDoc=false;
		/**
		 * 用于记录列数
		 */
		//private int columnNum = 0;
		
		public ExportExcelSAXHandler(String userName,WritableSheet sheet, int startNum,
				int endNum, int rowNum, Map<String, String> dateFormatMap,
				Map<String, String> dateParserFormatMap,
				Map<String, Field> isAvailFiledMap,
				Map<String, Map<String,Integer>> otherAvailFiledMap,
				Map<String, Map<String,String>> decodeFiledMap,
				Map<String,String> reportSessionStatus,Field[] filed) {
			this.dateFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.dateParseFormatCacheMap = new HashMap<String, SimpleDateFormat>();
			this.fieldDataCache=new HashMap<String, Double>();
			this.calculateFiledLocationMap=new HashMap<String, Integer>();
			this.sortMap=new HashMap<Integer, Integer>();
			this.isAvailFiledMap = isAvailFiledMap;
			this.sheet = sheet;
			this.startNum = startNum;
			this.endNum = endNum - 1;
			this.rowNum = rowNum;
			this.dateFormatMap = dateFormatMap;
			this.dateParserFormatMap = dateParserFormatMap;
			this.reportSessionStatus=reportSessionStatus;
			this.filed=filed;
			this.userName=userName;
			this.otherAvailFiledMap=otherAvailFiledMap;
			this.decodeFiledMap=decodeFiledMap;
			this.dataNum=Integer.parseInt(reportSessionStatus.get("totalAccount"))-1;
			this.rowNum++;
		}
		public void startDocument(){
			Map<String, Integer> datamap=null;
			Set<String> key2=null;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	datamap=otherAvailFiledMap.get((String)it.next());
			        	key2 = datamap.keySet();
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	fieldDataCache.put((String)it2.next(), 0d);
				        }
				    }
			        int fieldLength=filed.length;
			        String fName=null;
			        for (int i = 0; i < fieldLength; i++) {
			        	fName=filed[i].getName();
						if(otherAvailFiledMap.get(fName)!=null){
							calculateFiledLocationMap.put(fName, i);
						}
					}
		}
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			Label label = null;
			try {
				Field f=null;
				String textValue=null;
				if (i >= startNum && attributes.getLength() > 0&&endNum>=0) {
					if(!isFirstParse){//如果是第一解析
					sortAttribute(attributes);
					isFirstParse=true;
					}
					for (int i = 0; i < attributes.getLength(); i++) {
						String name = attributes.getQName(i);
						String value = attributes.getValue(i);
						// 如果属性名称和有效变量名称相同才计算
						f=isAvailFiledMap.get(name);
						if (f!=null&&name.equals(f.getName())) {
							textValue = getValue(name,value);
							//判断数据类型，如果是double类型就进行计算
							if(f.getType().toString().equals("double")){
								fieldDataCache.put(name,Double.valueOf(value));
							}
							label = new Label(sortMap.get(i), rowNum, textValue,ExcelStyle.getBodyStyle());
							sheet.addCell(label);
						}else{//如果和变量名称没有匹配的
							Double data=fieldDataCache.get(name);
							
							if(data!=null){//如果不等于空，说明有数据计算
								fieldDataCache.put(name,Double.valueOf(value));
							}
						}
					}
					calculateData();
					sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度
					rowNum++;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		private void calculateData() throws  Exception {

			Map<String, Integer> datamap=null;
			Set<String> key2=null;
			String name=null;
			String fieldName=null;
			int operator=0;
				 Set<String> key = otherAvailFiledMap.keySet();
			        for (Iterator<String> it = key.iterator(); it.hasNext();) {
			        	fieldName=(String)it.next();
			        	datamap=otherAvailFiledMap.get(fieldName);
			        	key2 = datamap.keySet();
			        	double sum=0d;
				        for (Iterator<String> it2 = key2.iterator(); it2.hasNext();) {
				        	name=(String)it2.next();
				        	operator=datamap.get(name);
				        	Double d=fieldDataCache.get(name);
				        	//只支持加减
				        	if(operator==0){//加
				        		sum=MathUtil.add(sum, d);
							}else if(operator==1){//减
								sum=MathUtil.sub(sum, d);
							}
				        }
						sheet.addCell(new Label(calculateFiledLocationMap.get(fieldName), rowNum, String.valueOf(sum),ExcelStyle.getBodyStyle()));
				      }
		}
		private void sortAttribute(Attributes attributes) {
			Field f=null;
			int fLength=filed.length;
			for (int i = 0; i < attributes.getLength(); i++) {
				String name = attributes.getQName(i);
				// 如果属性名称和有效变量名称相同才计算
				for (int j = 0; j < fLength; j++) {
					f=filed[j];
					if(f.getName().equals(name)){
						sortMap.put(i, j);
					}
				}
				}
			
		}
		private String getValue(String name,String value) {
			SimpleDateFormat dateParseFormat = null;
			SimpleDateFormat dateFormat = null;
			Map<String, String> decodeFiled=null;
			// 如果解析方式不为null
			String dateParserFormatStr = dateParserFormatMap.get(name);
			String dateFormatStr = dateFormatMap.get(name);
			decodeFiled=decodeFiledMap.get(name);
			
			if(decodeFiled!=null){
				String decodeValue=decodeFiled.get(value);
				if(decodeValue!=null){
					value=decodeValue;
				}else{
					value=decodeFiled.get("other");
				}
			}else if (!"null".equals(dateParserFormatStr)) {// 如果解析不为空，说明为日期类型
				try {
					if (!dateParserFormatStr.equals(dateFormatStr)) {// 如果解析和格式化的字符不一样
						dateParseFormat = dateParseFormatCacheMap.get(name);
						// 如果解析format不等于空
						if (dateParseFormat != null) {
							dateFormat = dateFormatCacheMap.get(name);
							value = dateFormat.format(dateParseFormat
									.parse(value));
						} else {
							dateParseFormat = new SimpleDateFormat(
									dateParserFormatStr);
							dateFormat = new SimpleDateFormat(dateFormatStr);
							// 放入缓存
							dateParseFormatCacheMap.put(name,
									dateParseFormat);
							dateFormatCacheMap.put(name, dateFormat);
							value = dateFormat.format(dateParseFormat
									.parse(value));
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return value;
		}

		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if(qName.equals("root")||dataNum<=i){
				exportEndDoc=true;
				//填充文档尾
				Label label = null;
				int fLength=0;
				try {
					//填充汇总行
					fLength=filed.length;
					String dataSum=null;
					String fName=null;
					for (int j = 0; j < fLength; j++) {
						fName=filed[j].getName();
						dataSum=reportSessionStatus.get(fName);
						if(dataSum==null){
							dataSum="";
						}
							if(j==0){
								sheet.setRowView(rowNum, ExcelStyle.BODY_HEIGHT);// 设置高度 
					        	   label = new Label(j, rowNum, "总计：",ExcelStyle.getBodyStyle());
									sheet.addCell(label);
							}else if(j==1){
								if(dataSum.equals("")){
						        	   label = new Label(j, rowNum, "共"+reportSessionStatus.get("totalAccount")+"条数据",ExcelStyle.getBodyStyle());
										sheet.addCell(label);
								}else{
						        	   label = new Label(j, rowNum, dataSum,ExcelStyle.getBodyStyle());
										sheet.addCell(label);
								}
							}else{
					        	   label = new Label(j, rowNum, dataSum,ExcelStyle.getBodyStyle());
									sheet.addCell(label);
							}
					}
			        rowNum++;
			        fillEndDoc(fLength);
			        
				} catch (Exception e) {
					e.printStackTrace();
				} 
				throw new SAXException();
			}
			// 元素结束
			if (i == endNum&&!exportEndDoc) {
				try {
					fillEndDoc(filed.length);
				} catch (Exception e) {
					e.printStackTrace();
				}
				throw new SAXException();
			}
			if(qName.equals("entity")){
				i++;
			}
		}
		private void fillEndDoc(int fLength) throws WriteException,
				RowsExceededException {
			Label label;
			int mergeNum=fLength/3;
			if(mergeNum<=0){
				mergeNum=1;
			}
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
			label = new Label(mergeNum, rowNum, "打印人："+userName, ExcelStyle.getBodyStyle());
			sheet.addCell(label);
			sheet.mergeCells(mergeNum, rowNum, 2*mergeNum - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
			 // --------------------------打印人列---------------------------
			label = new Label(2*mergeNum, rowNum, "打印时间："+new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), ExcelStyle.getBodyStyle());
			sheet.addCell(label);
			sheet.mergeCells(2*mergeNum, rowNum, fLength - 1, rowNum);// 合并单元格，参数格式（开始列，开始行，结束列，结束行）
			rowNum++;
		}
	}

}
