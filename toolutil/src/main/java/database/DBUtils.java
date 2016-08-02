package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 根据数据操作有关的工具类 方法一 ： 获取数据库的连接 方法二 ： 关闭数据库的连接
 */
public class DBUtils {
	private static Properties p = new Properties();
	static {
		System.out.println("====加载数据库配置文件······");
		try {
			StringBuffer str = new StringBuffer();
			str.append(System.getProperty("user.dir")).append(
					"\\init.properties");
			FileInputStream init = new FileInputStream(str.toString());
			p.load(init);
			System.out.println("====配置文件加载成功");
		} catch (IOException e) {
			System.out.println("====配置文件加载失败====\n" + e.getMessage());
		}
	}

	public static Connection getConnection() {
		Connection con = null;
		System.out.println("====提取配置文件中的数据库连接项······");
		String dbtype = p.getProperty("dbtype").trim();
		String dbname = p.getProperty("dbname").trim();
		String username = p.getProperty("username").trim();
		String password = p.getProperty("password").trim();
		String ip = p.getProperty("ip").trim();
		String port = p.getProperty("port").trim();
		System.out.println("====提取配置文件中数据库连接项完成");
		if (dbtype.equalsIgnoreCase("oracle")) {
			System.out.println("====连接Oracle数据库，加载驱动类······");
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver");
				System.out.println("====Oracle驱动类加载完成");
			} catch (Exception e) {
				System.out.println("====Oracle数据库驱动类加载失败====\n" + e.getMessage());
			}
			StringBuffer sql = new StringBuffer();
			sql.append("jdbc:oracle:thin:@");
			sql.append(ip);
			sql.append(":");
			sql.append(port);
			sql.append(":");
			sql.append(dbname);
			try {
				con = DriverManager.getConnection(sql.toString(), username,
						password);
				System.out.println("====成功连接到Oracle数据库");
			} catch (SQLException e) {
				System.out.println("====获取Oracle数据库连接失败====\n" + e.getMessage());
			}
		}
		if (dbtype.equalsIgnoreCase("mysql")) {
			System.out.println("====连接MYSQL数据库，加载驱动类······");
			try {
				Class.forName("org.gjt.mm.mysql.Driver");
				System.out.println("====MYSQL的数据库驱动类加载完成====");
			} catch (Exception e) {
				System.out.println("====MYSQL数据库驱动类加载失败====\n" + e.getMessage());
			}
			StringBuffer sql = new StringBuffer();
			sql.append("jdbc:mysql://");
			sql.append(ip);
			sql.append(":");
			sql.append(port);
			sql.append("/");
			sql.append(dbname);
			sql.append("?user=");
			sql.append(username);
			sql.append("&password=");
			sql.append(password);
			sql.append("&useUnicode=true&characterEncoding=utf-8");
			try {
				con = DriverManager.getConnection(sql.toString());
				System.out.println("====成功连接到MYSQL数据库");
			} catch (SQLException e) {
				System.out.println("====获取MYSQL数据库连接失败====\n" + e.getMessage());
			}
		}
		return con;
	}

	public static void close(Connection connection,
			PreparedStatement preparestatement, ResultSet resaultset) {
		System.out.println("====关闭数据库连接······");
		boolean br = (resaultset != null);
		boolean bp = (preparestatement != null);
		boolean bc = (connection != null);
		try {
			if (br & bp & bc) {
				resaultset.close();
				preparestatement.close();
				connection.close();
			} else if (bp & bc) {
				preparestatement.close();
				connection.close();
			} else if (br & bc) {
				resaultset.close();
				connection.close();
			} else if (br & bp) {
				resaultset.close();
				preparestatement.close();
			} else if (br) {
				resaultset.close();
			} else if (bp) {
				preparestatement.close();
			} else if (bc) {
				connection.close();
			} else {

			}
			System.out.println("====数据库连接成功关闭");
		} catch (Exception e) {
			System.out.println("====关闭数据库时候错误====\n" + e.getMessage());
		}
	}
}
