package net;

/**
*服务器端代码
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class JJServer {
	public static void main(String[] args) throws IOException {
	ServerSocket ss = new ServerSocket(8888);
		try
		{
			
			Socket s = ss.accept();
			System.out.println("有客户端请求连接,客户端ip地址:"+s.getInetAddress().getHostAddress()+",远程端口:"+s.getPort()+",本地端口:"+s.getLocalPort());
			BufferedReader in  = new BufferedReader(
					new InputStreamReader(
					s.getInputStream()));
			PrintWriter out = new PrintWriter(
							new BufferedWriter(
							new OutputStreamWriter(s.getOutputStream())),true);

			while(true)
			{
				String str = in.readLine();//读取一行

				if(str.equalsIgnoreCase("exit")) break;
				System.out.println("接受客户端数据:"+str);
				out.println("服务器响应:"+str);
			}
			System.out.println("服务器退出");
		}
		finally
		{
			ss.close();//省略了一些流等的关闭
		}
	}
}

