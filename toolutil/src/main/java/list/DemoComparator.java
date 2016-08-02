package list;

import java.util.Comparator;


public class DemoComparator implements Comparator<Object> {

	public int compare(Object  o1,Object  o2) {
		Demo cp1 = (Demo)o1;
		Demo cp2 = (Demo)o2;
		
		int flag = 0;
//		if(Integer.parseInt(cp1.getOrder()) > Integer.parseInt(cp2.getOrder())){
//			flag = 1;
//		}else if(Integer.parseInt(cp1.getOrder()) < Integer.parseInt(cp2.getOrder())){
//			flag = -1;
//		}else{
//			
//		}
		if(cp1.getOrder() > cp2.getOrder()){
			flag = 1;
		}else if(cp1.getOrder() < cp2.getOrder()){
			flag = -1;
		}else{
			
		}
		return flag;
	}
}