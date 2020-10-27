package cn.yusys.greenplum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:�����Զ������ݿ����ӳ�
 * @date 2019��3��13��
 */
public class TestJDBCGPConnPool {
	static Connection connection = null;
	static Statement statement = null;
	static ResultSet resultSet = null;
     public static void main(String[] args) {
    	 JdbcFromGPUtil jdbcFromGPUtil = new JdbcFromGPUtil();
		try {
			connection = jdbcFromGPUtil.getConnection();
			statement = connection.createStatement();
			resultSet = statement.executeQuery("select * from bilibili limit 20");
			while (resultSet.next()) {
				 // ��ӡ�������λ����Ϣ(��γ��)
				 System.out.println(resultSet.getString(1));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				// �黹���������ӳ�(statement��resultSetӦ���ڹ黹���ӷ����н�����ʾ�ر� ��Ȼ���������Ӷ��󴴽���ᱨ��OutOfMemoryException)
				jdbcFromGPUtil.closeConnection(connection, statement, resultSet);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
