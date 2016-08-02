package demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Filecopy {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		File f=new File("D:\\aa.txt");
		File f1=new File("D:\\bb.txt");
		try {
			FileInputStream i=new FileInputStream(f);
			FileOutputStream o=new FileOutputStream(f1);
			while(i.available()>0)
			{
				int b;
				b=i.read();
				o.write((char)b);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
