package utils;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;


/**
 * JSON单元类
 * 
 * @createDate Mar 28, 2011 10:39:30 AM
 * @author zy
 * @version 1.0.0
 * @modifyAuthor
 * @modifyDate
 */
@SuppressWarnings("unchecked")
public class JsonUtil {

	/**
	 * 转换成JSON数据格式返回前台，DataGrid接收
	 * 
	 * @param List
	 *            查询当前页显示的结果集合
	 * @param totalCount
	 *            查询的记录总数
	 * @return
	 */
	public static String parseTableJson(List<?> list, int totalCount) {

		// 定义返回值
		StringWriter result = new StringWriter();

		// 定义JSON
		JSONArray object = new JSONArray();

		// 加入数据
		for (int i = 0; i < list.size(); i++) {
			object.add(list.get(i));
		}
		try {
			StringWriter out = new StringWriter();
			object.writeJSONString(out);
			JSONObject objectJson = new JSONObject();
			// 设置记录总数
			objectJson.put("total", totalCount);
			// 设置行记录
			objectJson.put("rows", out.getBuffer());
			// 结果集转换成JSON格式
			objectJson.writeJSONString(result);
		} catch (IOException e) {
			System.out.println("解析成JSON格式失败，请查看具体错误信息");
		}
		return result.toString();
	}

}
