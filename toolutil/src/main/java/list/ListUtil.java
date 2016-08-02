package list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ListUtil {
	/**
	 * list去除重复数据
	 * @param list
	 * @return
	 */
	public static List<Object> removeDuplicate(List<Object> list){
		List<Object> l = new ArrayList<Object>();
		
		return l;
	}
	
	
	public void sortList(List<Demo> list){
		Collections.sort(list,new DemoComparator());
	}
}
