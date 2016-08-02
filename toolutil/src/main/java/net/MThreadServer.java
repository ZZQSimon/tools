package net;

/**
*基于多线程的Socket编程
*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
public class MThreadServer {
	public static void main(String[] args) throws IOException {
	ServerSocket ss = new ServerSocket(8888);
		try {
			while (true) {
				Socket s = ss.accept();
				System.out.println("有客户端请求连接,客户端ip地址:"
						+ s.getInetAddress().getHostAddress() + ",远程端口:"
						+ s.getPort() + ",本地端口:" + s.getLocalPort());
				MThreadSocket mts = new MThreadSocket(s);
				Thread t = new Thread(mts);
				t.start();
			}
		} finally {
			ss.close();// 省略了一些流等的关闭
		}
	}
}
class MThreadSocket implements Runnable {
	Socket s = null;
	MThreadSocket(Socket socket) {
		s = socket;
	}
	public void run() {
		BufferedReader in;
		try {
			in = new BufferedReader(new InputStreamReader(s.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(s.getOutputStream())), true);
			while (true) {
				String str = in.readLine();// 读取一行
				if (str.equalsIgnoreCase("exit"))
				{
					System.exit(0);
					break;
				}
				System.out.println("接受客户端数据:" + str);
				out.println("服务器响应:" + str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
