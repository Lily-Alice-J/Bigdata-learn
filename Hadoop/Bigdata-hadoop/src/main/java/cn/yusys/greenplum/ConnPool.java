package cn.yusys.greenplum;

import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * @author Tangzhi mail:tangzhi8023@gmail.com
 * @description:�Զ������ݿ����ӳ�
 * @date 2019��3��13��
 */
public class ConnPool implements DataSource{
    // 1.ʹ��LinkedListģ��
	private static LinkedList<Connection> linkedList = new LinkedList<Connection>();
	// 2.�ھ�̬������м������ݿ����������ļ�
	static {
		InputStream inputStream = ConnPool.class.getClassLoader().getResourceAsStream("db.properties");
		Properties prop = new Properties();
		try {
			prop.load(inputStream);
			String driver = prop.getProperty("driver");
            String url = prop.getProperty("url");
            String user = prop.getProperty("user");
            String password = prop.getProperty("password"); 
            // 3.���ݿ����ӳصĳ�ʼ���������Ĵ�С
            int  initSize = Integer.parseInt(prop.getProperty("InitSize"));
            // 4.��������
            Class.forName(driver);
            // 5.���������ļ��г�ʼ����������
            for (int i = 0 ; i < initSize ; i++) {
            	Connection connection = DriverManager.getConnection(url, user, password);
            	// 6.�����������ӷ������ӳ�
            	linkedList.add(connection);
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public PrintWriter getLogWriter() throws SQLException {
		return null;
	}

	public int getLoginTimeout() throws SQLException {
		return 0;
	}

	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		return null;
	}

	public void setLogWriter(PrintWriter arg0) throws SQLException {
	}

	public void setLoginTimeout(int arg0) throws SQLException {
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		return null;
	}
     
	/**
	 * �����ݿ����ӳ��л�ȡ����
	 */
	public Connection getConnection() throws SQLException {
		if (linkedList.size() > 0) {
			// �Ӽ����л�ȡһ������
			final Connection connection = linkedList.removeFirst();
			// ����connection�������
			return connection;
		} else {
			throw new RuntimeException("���ݿ����ӳط�æ,���Ժ�����!");
		}
	}
    public void closeConnection (Connection conn,Statement st,ResultSet re) throws Exception {
    	if (st != null) {
    		st.close();
    	}
    	if (re != null) {
    		re.close();
    	}
    	if (conn != null) {
    		// �������ݿ����ӳ�
    		linkedList.add(conn);
    	}
    }
	public Connection getConnection(String username, String password) throws SQLException {
		return null;
	}
}
