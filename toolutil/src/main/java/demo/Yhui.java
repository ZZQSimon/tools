package demo;

public class Yhui{
	static final int H=10;
		public static void main(String[] args){
			int[][] YH =new int[H][];
			
			for(int i=0;i<H;i++)
			{
				YH[i] = new int[i+1];
			}
			//System.out.println("ssds");

				for(int i=0;i<H;i++)
				{
					for(int j=0;j<i;j++)
					{
						if(j==0) {YH[i][j]=1;}
						else if(j==i) {YH[i][j]=1;}
						else { YH[i][j]=YH[i-1][j-1]+YH[i-1][j]; }
					}
				}
				for(int i=0;i<H;i++)
				{
					for(int j=0;j<i;j++)
					{
						System.out.print("  "+YH[i][j]+"  ");
					}
				System.out.println();
				}


		}
}