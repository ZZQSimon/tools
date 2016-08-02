
public class Ceshi {
	public static void run (int s, int e){
		for(int i=s; i<=e;i++){
			int a = i/10;
			int b = i%10;
			System.out.println("s-a-b = "+ i+"-("+a+"+"+b+") = "+(i-(a+b)));
		}
	}
	
	public static void main(String[] args) {
		Ceshi.run(10, 99);
	}
}
