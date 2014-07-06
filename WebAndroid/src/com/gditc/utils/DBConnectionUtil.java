package com.gditc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class DBConnectionUtil {
	private static String user;
	private static String password;
	private static String url;
	private static String driver;

	static {
		try {
			ClassLoader classLoader = DBConnectionUtil.class.getClassLoader();
			InputStream is = classLoader.getResourceAsStream("config/props/db.properties");
			Properties props = new Properties();
			props.load(is);
			url = props.getProperty("url");
			user = props.getProperty("user");
			password = props.getProperty("password");
			driver = props.getProperty("driver");
			// 注册驱动
			Class.forName(driver);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("找不到驱动");
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("加载properties文件错误");
		}
	}

	/**
	 * 获取连接
	 * @return
	 * @throws Exception
	 */
	public static Connection getConnection() throws Exception {
		return DriverManager.getConnection(url, user, password);
	}

	/**
	 * 关闭连接
	 * @param conn
	 * @param pstmt
	 * @param rs
	 * @throws Exception
	 */
	public static void close(Connection conn, PreparedStatement pstmt, ResultSet rs) throws Exception {
		if (rs != null)
			rs.close();
		if (pstmt != null)
			pstmt.close();
		if (conn != null)
			conn.close();
	}
}