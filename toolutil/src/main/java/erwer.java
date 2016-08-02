import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;

public class erwer {
	public static void main(String[] args) {
		
		String s = "sda0,sda1,sda2,sda3,sda4,sda5,sda6,sda7,sda8,sda9";
String[] sa = s.split(",");
List<String> ls = new ArrayList<String>();
for(String t : sa){
	ls.add(t);
}
		System.out.println(JSONArray.toJSONString(ls));
		List<String> l = new ArrayList<String>();
		for(int i=0;i<10;i++){
			l.add("sda"+i);
		}
		System.out.println(JSONArray.toJSONString(l));
	}
}
