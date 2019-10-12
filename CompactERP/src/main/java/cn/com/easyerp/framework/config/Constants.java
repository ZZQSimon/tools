package cn.com.easyerp.framework.config;

import java.io.File;

/**
 * 系统级别常量类
 * 
 * @author Simon Zhang
 */
public class Constants {
    public static final String separator = File.separator;

    /** 文件处理时候，换行符标志 \r */
    public static final String newliner = "\r";
    /** 文件处理时候，换行符标志 \n */
    public static final String newlinen = "\n";
    /** CSV文件文本分割符 */
    public static final char CSV_SPLIT = ',';
    /** 文件拓展名 .xml */
    public final static String _xml = ".xml";
    /** 文件拓展名 .txt */
    public final static String _txt = ".txt";
    /** 文件拓展名 无拓展名文件 */
    public final static String _none = "";

}