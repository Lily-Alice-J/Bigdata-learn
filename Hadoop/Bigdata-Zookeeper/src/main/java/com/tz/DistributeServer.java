package com.tz;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
/**
 * �ֲ�ʽ���������߶�̬��֪�����
 * @author TZ
 *
 */
public class DistributeServer {
	//Zookeeper��Ⱥ
	private static final String CONNECT_STRING = "192.168.25.132:2181,192.168.25.132:2182,192.168.25.132:2183";
	private static final int SESSION_TIMEOUT = 2000;
	private  ZooKeeper zkClient = null;
	private static String parentNode = "/servers";
	/**
	 * ��ȡzookeeper����
	 * @throws Exception
	 */
	public void getConnection() throws Exception{
	      zkClient = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {	
			public void process(WatchedEvent event) {
				System.out.println(event.getType()+event.getPath());
				try {
					zkClient.getChildren("/", true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * ����zkע���������Ϣ
	 * @param args
	 * @throws InterruptedException 
	 * @throws Exception 
	 * @throws Exception
	 */
	public void registServers(String hostName) throws Exception{
		    //������ʱ�ڵ�
			String path = zkClient.create(parentNode+"/server", hostName.getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			System.out.println(hostName+"is online"+path);
	}
	/**
	 * ִ��ҵ���߼�
	 * @param args
	 * @throws Exception 
	 * @throws Exception
	 */
	public void handleBusiness(String hostName) throws Exception{
		System.out.println(hostName+"start working");
		Thread.sleep(Long.MAX_VALUE);
	}
	public static void main(String[] args) throws Exception {
		//1.��ȡzk����
		DistributeServer distributeServer = new DistributeServer();
		distributeServer.getConnection();
		//2.����zkע���������Ϣ
		distributeServer.registServers(args[0]);
		//3.ִ��ҵ���߼�
		distributeServer.handleBusiness(args[0]);
	}
}
