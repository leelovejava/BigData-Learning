package com.leelovejava.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * hive jdbc客户端
 */
public class HiveJdbcClient {

	private static String driverName = "org.apache.hive.jdbc.HiveDriver";

	public static void main(String[] args) throws SQLException {
		try {
			Class.forName(driverName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// ss -nal 查看端口是否打开  10000
		// 启动服务: hiveserver2
		Connection conn = DriverManager.getConnection("jdbc:hive2://hadoop000:10000/test", "root", "root");
		Statement stmt = conn.createStatement();
		String sql = "select * from psn3 limit 5";
		ResultSet res = stmt.executeQuery(sql);
		while (res.next()) {
			System.out.println(res.getString(1) + "-" + res.getString("name"));
		}
	}
}
