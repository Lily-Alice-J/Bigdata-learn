package cn.yusys.greenplum;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * greenplum��ȡ���ݿ����ӹ�����
 */
public class JdbcFromGPUtil {
    private ConnPool connPool = new ConnPool();
    /**
     * ��ȡ����
     */
    public  Connection getConnection() throws SQLException{
        return connPool.getConnection();
    }
   /**
    * �ر�����
 * @throws SQLException 
    */
    public void closeConnection(Connection conn,Statement st,ResultSet re) throws SQLException {
    	try {
			connPool.closeConnection(conn, st, re);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}
