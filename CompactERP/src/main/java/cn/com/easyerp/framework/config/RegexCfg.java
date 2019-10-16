/**
	* Title: RegexCfg.java <br>
	* Description:[] <br>
	* Copyright (c)  2018<br>
	* Company: <br>
	* @Date 2019.10.16 <br>
	* 
	* @author Simon Zhang
	* @version V1.0
	*/
package cn.com.easyerp.framework.config;

import java.util.regex.Pattern;

/**
 * @ClassName: RegexCfg <br>
 * @Description: [] <br>
 * @date 2019.10.16 <br>
 * 
 * @author Simon Zhang
 */

public interface RegexCfg {
    public static final Pattern PRINT_AREA_PATTERN = Pattern.compile(".*!(\\$[A-Z]+\\$[0-9]+:\\$[A-Z]+\\$)([0-9]+)");
    public static final Pattern QR_TAG_PATTERN = Pattern.compile("qr(:([\\d]+)x([\\d]+))?");
    public static final Pattern QR_TAG_ATTR_PATTERN = Pattern.compile("([\\d]+)x([\\d]+)");
    public static final Pattern CELL_RANGE_PATTERN = Pattern.compile("([0-9]+)");
    public static final Pattern TABLE_CELL_PATTERN = Pattern
            .compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.<>]+)?\\}$");
    public static final Pattern SUB_TABLE_CELL_PATTERN = Pattern
            .compile("^\\$\\{(<([^:]*)(:([^*+]*)([*+])?)?>)?([^.]+)\\.([^.]+)\\}$");

}
