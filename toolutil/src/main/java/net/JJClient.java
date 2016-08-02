package net;

/**
*客户端的代码
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
public class JJClient {
	public static void main(String[] args) throws IOException {
		Socket s = new Socket("127.0.0.1",8888);
	try
		{
			BufferedReader in  = new BufferedReader(
					new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(
							new BufferedWriter(
							new OutputStreamWriter(s.getOutputStream())),true);
			while(true)
			{
				//这里从键盘采集一行数据
			BufferedReader inkey = new BufferedReader(new InputStreamReader(System.in));
				System.out.print("从键盘输入数据:");
				String str = inkey.readLine();//读取一行
				out.println(str);
				if(str.equalsIgnoreCase("exit")) break;
				//				out.flush();
				String msg = in.readLine();
				System.out.println("从服务器收到数据:"+msg);
			}
			System.out.println("客户端退出");
		}
		finally
		{
			s.close();//省略了一些流等的关闭
		}
	}
}
			