package report;
/**
 * 报表配置类
 * @createDate 2011-8-22
 * @author Simon Zhang
 * @version 1.0.0
 * @modifyAuthor 
 * @modifyDate
 */
public final class ReportConfig {
	public static final String REPORT_REPORT_XML_STAUTS="file_report_stauts";
	public static final String EXPORT_REPORT_XML_SUCCESS="1";
	public static final String EXPORT_REPORT_XML_FALSE="0";
	public static final String EXPORT_REPORT_TOTALACCOUNT="totalAccount";
	public static final String EXPORT_REPORT_XML_NAME="xmlFile_name";
	public static final String EXPORT_REPORT_FILE_MARKER="file_marker";
	public static String EXPORT_REPORT_FILE_PATH="C:/cache_file_webonecard/";
	public static String logPath = "C:/cache_file_webonecard/01";
	static{
		if(System.getProperty("os.name").indexOf("Linux")!=-1||System.getProperty("os.name").indexOf("linux")!=-1){//如果当前系统为linux
			logPath = logPath.substring(2);
			EXPORT_REPORT_FILE_PATH="/opt"+logPath+"/";
		}else{//如果当前系统为window
			EXPORT_REPORT_FILE_PATH=logPath+"/";
		}
	}
	
	
}
