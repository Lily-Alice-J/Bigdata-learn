package cn.yusys.greenplum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:��greenplum���ݿ��з�������洢��Mysql���ݿ�
 * @date 2019��3��16��
 */
public class Bilibili {
	static Connection connection = null;
	static Statement statement = null;
	static ResultSet resultSet = null;
	static Connection mysqlConnection = null;
	static Statement mysqlStatement = null;
     public static void main(String[] args) {
    	 JdbcFromGPUtil jdbcFromGPUtil = new JdbcFromGPUtil();
    	 mysqlConnection = new ConnectionPool().getConnection();
		try {
			connection = jdbcFromGPUtil.getConnection();
			statement = connection.createStatement();
			mysqlStatement = mysqlConnection.createStatement();
			mysqlConnection.setAutoCommit(false);
			resultSet = statement.executeQuery("select * from bilibili order by view desc limit 100");
			int beforeTime = (int) System.currentTimeMillis();
			while (resultSet.next()) {
				 String sql = "insert into bilibili_pro(aid,views,danmaku,reply,favorite,coin,share) VALUES ('"+resultSet.getInt(1)+"','"+resultSet.getInt(2)+"','"+resultSet.getInt(3)+"','"+resultSet.getInt(4)+"',"
				 		+ "'"+resultSet.getInt(5)+"','"+resultSet.getInt(6)+"','"+resultSet.getInt(7)+"')";
				 mysqlStatement.execute(sql);
				 mysqlConnection.commit();
			}
			System.out.println(((int)System.currentTimeMillis() - beforeTime)+"ms");
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				// �黹���������ӳ�(statement��resultSetӦ���ڹ黹���ӷ����н�����ʾ�ر� ��Ȼ���������Ӷ��󴴽���ᱨ��OutOfMemoryException)
				jdbcFromGPUtil.closeConnection(connection, statement, resultSet);
				if (mysqlStatement != null) {
					mysqlStatement.close();
				}
				new ConnectionPool().returnConnection(mysqlConnection);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}
