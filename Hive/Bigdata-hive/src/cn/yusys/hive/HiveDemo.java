package cn.yusys.hive;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:��Hive���ж�ȡ����
 * @date 2018��11��20��
 */
public class HiveDemo {
	public static void main(String[] args) {
		   try {
			   // ע����
			Class.forName("org.apache.hive.jdbc.HiveDriver");
			Connection connection = DriverManager.getConnection("jdbc:hive2://192.168.25.136:10000/shizhan","root","199799");
			connection.setAutoCommit(false);
			Statement statement = connection.createStatement();
			String sql = "select count(1) from weblogs";
			ResultSet resultSet = statement.executeQuery(sql);
			while (resultSet.next()) {
				System.out.println(resultSet.getInt(1));
				/**
                 *��ʱ������Խ���Hive���ж�ȡ������ͳ�Ƶ����ݴ�Ž�MySqL��Ȼ������ǰ��չʾ
                 **/
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
