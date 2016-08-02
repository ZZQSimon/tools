package net;

/**
 * 
 */
import java.net.*;
import java.io.*;
/**
 * @author Administrator
 *
 */
public class TcpServer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			ServerSocket ss = new ServerSocket(8001);
			Socket s = ss.accept();
			System.out.println("Client connected");
			
			OutputStream os = s.getOutputStream();
			String s1 = "Welcome to Server " + s.getLocalAddress().getHostName();
			os.write(s1.getBytes());
			
			InputStream is = s.getInputStream();
			byte[] buf = new byte[1024];
			int len = is.read(buf);
			String s2 = new String(buf, 0, len);
			System.out.println(s2);
			
			os.close();
			is.close();
			s.close();
			ss.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
