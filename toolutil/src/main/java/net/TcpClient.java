package net;

/**
 * 
 */
import java.io.*;
import java.net.*;
/**
 * @author Administrator
 *
 */
public class TcpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Socket s = new Socket("127.0.0.1", 8001);
			System.out.println("Connect Server success");
			
			InputStream is = s.getInputStream();
			byte[] buf = new byte[1024];
			int len = is.read(buf);
			String s1 = new String(buf, 0, len);
			System.out.println(s1);
			
			OutputStream os = s.getOutputStream();
			String s2 = "I'm client " + s.getLocalAddress().getHostAddress();
			os.write(s2.getBytes());
			
			is.close();
			os.close();
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
