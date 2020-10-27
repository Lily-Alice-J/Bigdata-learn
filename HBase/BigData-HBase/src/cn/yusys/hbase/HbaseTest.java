package cn.yusys.hbase;

import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.CompareFilter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HbaseTest {
	/**
	 * ����ss
	 */
	static Configuration config = null;
	private Connection connection = null;
	private Table table = null;

	@Before
	public void init() throws Exception {
		config = HBaseConfiguration.create();// ����
		config.set("hbase.rootdir", "hdfs://bigData:9000/hbase"); 
		config.set("hbase.zookeeper.quorum", "bigData:2181");//:2181
		config.set("hbase.master", "bigData:60000");//hbase��0ϵ��Ĭ�϶˿���60000;1ϵ��Ĭ�϶˿�16010
		connection = ConnectionFactory.createConnection(config);
		table = connection.getTable(TableName.valueOf("test-hive")); //�����ļ��д�����ʼ��
	}

	/**
	 * ����һ����
	 * 
	 * @throws Exception
	 */
	@Test
	public void createTable() throws Exception {
		// �����������
		@SuppressWarnings({ "deprecation", "resource" }) 
		HBaseAdmin admin = new HBaseAdmin(config); // hbase�����
		// ������������
		TableName tableName = TableName.valueOf("test1"); // ������
		HTableDescriptor desc = new HTableDescriptor(tableName);
		// ���������������
		HColumnDescriptor family = new HColumnDescriptor("info"); // ����
		// ��������ӵ�����
		desc.addFamily(family);
		HColumnDescriptor family2 = new HColumnDescriptor("info2"); // ����
		// ��������ӵ�����
		desc.addFamily(family2);
		// ������
		admin.createTable(desc); // ������
	}

	@Test
	@SuppressWarnings("deprecation")
	public void deleteTable() throws MasterNotRunningException,
			ZooKeeperConnectionException, Exception {
		HBaseAdmin admin = new HBaseAdmin(config);
		admin.disableTable("test1"); // ���Ǳ�Ϊ�ǽ��ñ�
		admin.deleteTable("test1");
		admin.close();
	}

	/**
	 * ��hbase����������
	 * @throws Exception
	 */
	@Test
	public void insertData() throws Exception {
		table.setAutoFlushTo(false);
		table.setWriteBufferSize(534534534);
		ArrayList<Put> arrayList = new ArrayList<Put>();
			Put put = new Put(Bytes.toBytes("1")); //rowkey
			put.add(Bytes.toBytes("info"), Bytes.toBytes("url"), Bytes.toBytes("www.baidu.com"));
			put.add(Bytes.toBytes("info"), Bytes.toBytes("datatime"), Bytes.toBytes("2018-11-19"));
			arrayList.add(put);
		//��������
		table.put(arrayList);
		//�ύ
		table.flushCommits();
	}
	/**
	 * �޸�����
	 * @throws Exception
	 */
	@Test
	public void uodateData() throws Exception {
		Put put = new Put(Bytes.toBytes("1")); //rowkey
		put.add(Bytes.toBytes("info"), Bytes.toBytes("url"), Bytes.toBytes("www.google.com"));
		put.add(Bytes.toBytes("info"), Bytes.toBytes("datatime"), Bytes.toBytes("2018-11-20"));
		//��������
		table.put(put);
		//�ύ
		table.flushCommits();
	}

	/**
	 * ɾ������
	 * 
	 * @throws Exception
	 */
	@Test
	public void deleteDate() throws Exception {
		Delete delete = new Delete(Bytes.toBytes("1234"));
		table.delete(delete);
		table.flushCommits();
	}

	/**
	 * ������ѯ
	 * 
	 * @throws Exception
	 */
	@Test
	public void queryData() throws Exception {
		Get get = new Get(Bytes.toBytes("default509bed3a-4b98-42a5-9211-d84621bd3594"));
		Result result = table.get(get);
		System.out.println(new String(result.value(),"utf-8"));
		// System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("datatime"))));
		// System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("sex"))));
	}

	/**
	 * ȫ��ɨ��
	 * 
	 * @throws Exception
	 */
	@Test
	public void scanData() throws Exception {
		Scan scan = new Scan();
		//scan.addFamily(Bytes.toBytes("info"));
		//scan.addColumn(Bytes.toBytes("info"), Bytes.toBytes("password"));
		scan.setStartRow(Bytes.toBytes("wangsf_0"));
		scan.setStopRow(Bytes.toBytes("wangwu"));
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("contenttitle"))));
			System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("docno"))));
			//System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("password"))));
			//System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("name"))));
		}
	}

	/**
	 * ȫ��ɨ��Ĺ�����
	 * ��ֵ������
	 * @throws Exception
	 */
	@Test
	public void scanDataByFilter1() throws Exception {

		// ����ȫ��ɨ���scan
		Scan scan = new Scan();
		//����������ֵ������
		SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes("info"),
				Bytes.toBytes("name"), CompareFilter.CompareOp.EQUAL,
				Bytes.toBytes("zhangsan2"));
		// ���ù�����
		scan.setFilter(filter);

		// ��ӡ�����
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("password"))));
			System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"))));
			//System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("password"))));
			//System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("name"))));
		}

	}
	/**
	 * rowkey������
	 * @throws Exception
	 */
	@Test
	public void scanDataByFilter2() throws Exception {
		
		// ����ȫ��ɨ���scan
		Scan scan = new Scan();
		//ƥ��rowkey��wangsenfeng��ͷ��
		RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^12341"));
		// ���ù�����
		scan.setFilter(filter);
		// ��ӡ�����
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("password"))));
			System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info"), Bytes.toBytes("name"))));
			//System.out.println(Bytes.toInt(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("password"))));
			//System.out.println(Bytes.toString(result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("name"))));
		}

		
	}
	
	/**
	 * ƥ������ǰ׺
	 * @throws Exception
	 */
	@Test
	public void scanDataByFilter3() throws Exception {
		// ����ȫ��ɨ���scan
		Scan scan = new Scan();
		//ƥ��rowkey��wangsenfeng��ͷ��
		ColumnPrefixFilter filter = new ColumnPrefixFilter(Bytes.toBytes("name"));
		// ���ù�����
		scan.setFilter(filter);
		// ��ӡ�����
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			System.out.println("rowkey��" + Bytes.toString(result.getRow()));
			System.out.println("info:name��"
					+ Bytes.toString(result.getValue(Bytes.toBytes("info"),
							Bytes.toBytes("name"))));
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info"), Bytes.toBytes("age")) != null) {
				System.out.println("info:age��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info"),
								Bytes.toBytes("age"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info"), Bytes.toBytes("sex")) != null) {
				System.out.println("infi:sex��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info"),
								Bytes.toBytes("sex"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("name")) != null) {
				System.out
				.println("info2:name��"
						+ Bytes.toString(result.getValue(
								Bytes.toBytes("info2"),
								Bytes.toBytes("name"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("age")) != null) {
				System.out.println("info2:age��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info2"),
								Bytes.toBytes("age"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("sex")) != null) {
				System.out.println("info2:sex��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info2"),
								Bytes.toBytes("sex"))));
			}
		}
		
	}
	/**
	 * ����������
	 * @throws Exception
	 */
	@Test
	public void scanDataByFilter4() throws Exception {
		
		// ����ȫ��ɨ���scan
		Scan scan = new Scan();
		//���������ϣ�MUST_PASS_ALL��and��,MUST_PASS_ONE(or)
		FilterList filterList = new FilterList(Operator.MUST_PASS_ONE);
		//ƥ��rowkey��wangsenfeng��ͷ��
		RowFilter filter = new RowFilter(CompareFilter.CompareOp.EQUAL, new RegexStringComparator("^wangsenfeng"));
		//ƥ��name��ֵ����wangsenfeng
		SingleColumnValueFilter filter2 = new SingleColumnValueFilter(Bytes.toBytes("info"),
				Bytes.toBytes("name"), CompareFilter.CompareOp.EQUAL,
				Bytes.toBytes("zhangsan"));
		filterList.addFilter(filter);
		filterList.addFilter(filter2);
		// ���ù�����
		scan.setFilter(filterList);
		// ��ӡ�����
		ResultScanner scanner = table.getScanner(scan);
		for (Result result : scanner) {
			System.out.println("rowkey��" + Bytes.toString(result.getRow()));
			System.out.println("info:name��"
					+ Bytes.toString(result.getValue(Bytes.toBytes("info"),
							Bytes.toBytes("name"))));
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info"), Bytes.toBytes("age")) != null) {
				System.out.println("info:age��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info"),
								Bytes.toBytes("age"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info"), Bytes.toBytes("sex")) != null) {
				System.out.println("infi:sex��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info"),
								Bytes.toBytes("sex"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("name")) != null) {
				System.out
				.println("info2:name��"
						+ Bytes.toString(result.getValue(
								Bytes.toBytes("info2"),
								Bytes.toBytes("name"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("age")) != null) {
				System.out.println("info2:age��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info2"),
								Bytes.toBytes("age"))));
			}
			// �ж�ȡ������ֵ�Ƿ�Ϊ��
			if (result.getValue(Bytes.toBytes("info2"), Bytes.toBytes("sex")) != null) {
				System.out.println("info2:sex��"
						+ Bytes.toInt(result.getValue(Bytes.toBytes("info2"),
								Bytes.toBytes("sex"))));
			}
		}
		
	}

	@After
	public void close() throws Exception {
		table.close();
		connection.close();
	}
}
