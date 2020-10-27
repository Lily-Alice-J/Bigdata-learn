package cn.yusys.greenplum;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:����ʹ��JDBC����GreenPlum�ֲ�ʽ���ݿ�
 * @date 2019��3��13��
 */
public class TestGreenPlumJDBC {
	static Statement gpStatement = null;
	static Connection gpConnection = null;
	static ResultSet gpResultSet = null;
	public static void main(String[] args) throws Exception {
		  try {
			  // 1.��ȡ��������
			Class.forName("com.pivotal.jdbc.GreenplumDriver");
			  // 2.��ȡ����
			gpConnection = DriverManager.getConnection("jdbc:pivotal:greenplum://192.168.25.140:5432;DatabaseName=tutorial", "gpadmin", "gpadmin");
			  // 3.������ѯ����
			gpStatement = gpConnection.createStatement();
			  // 4.��дsql
			gpResultSet = gpStatement.executeQuery("select count(1) from products");
			while (gpResultSet.next()) {
				System.out.println(gpResultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (gpResultSet != null) {
				gpResultSet.close();
			}
			if (gpStatement != null) {
				gpStatement.close();
			}
			if (gpConnection != null) {
				gpConnection.close();
			}
		}
	}
}
