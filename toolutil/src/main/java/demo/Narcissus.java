package demo;
/**
 * 扫描三位自然数100～999，输出打印该数组中的所有水仙花数
 * 
 * 水仙花数，是指各位数字的三次方之和等于该数本身的三位自然数
 * 
 * 作者 ：Jackeen Zhang 
 * 
 * 时间：2011.06.19
 */
public class Narcissus {
	private static int a,b, c;
	public static void main(String args[]){
		for(int i = 100;i<1000;i++){
			
			a = (int)(i/100);					//获取水仙花数百位a
			b = (int)((i-a*100)/10);			//获取水仙花数十位b
			c = (int)(i-100*a-10*b);			//获取水仙花数个位c

			int m  = 0;							//m记录a.b.c的立方和
			m = a*a*a+b*b*b+c*c*c;				//
			if(m==i){							//比较条件是否满足
				System.out.println(i+" 是水仙花数！");			//满足条件则输出
			}
		}
	}
}
