package com.tz;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

/**
 * �ֲ�ʽ���������߶�̬��֪�ͻ���
 * @author Administrator
 *
 */
public class DistributeClient {
	private static final String CONNECT_STRING = "192.168.25.132:2181,192.168.25.132:2182,192.168.25.132:2183";
	private static final int SESSION_TIMEOUT = 2000;
	private  ZooKeeper zkClient = null;
	//volatile:ʹ���ҵ���̲߳���һ���������б���
	private volatile List<String> serversList;
	private static String parentNode = "/servers";
	/**
	 * ��ȡzookeeper����
	 * @throws Exception
	 */
	public  void getConnection() throws Exception{
	      zkClient = new ZooKeeper(CONNECT_STRING, SESSION_TIMEOUT, new Watcher() {	
			public void process(WatchedEvent event) {
				System.out.println(event.getType()+event.getPath());
				try {
					//�������»�ȡ�������б�
					getServerList();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * �õ�����zookeeper��ע��ķ������б�
	 * @param args
	 * @throws InterruptedException 
	 * @throws KeeperException 
	 */
	public void getServerList() throws KeeperException, InterruptedException{
		List<String> children = zkClient.getChildren(parentNode, true);
		List servers = new ArrayList<String>();
		for (String chi : children) {
			//�õ��ӽڵ��µ�����
			byte[] data = zkClient.getData(parentNode+"/"+chi,false, null);
			//���ӽڵ����з������б���뼯����
			servers.add(new String(data));
		}
		serversList = servers;
		System.out.println(serversList);
	}
	/**
	 * ִ��ҵ���߳�
	 * @param args
	 * @throws Exception
	 */
	public void handleBusiness() throws Exception{
		System.out.println("client start working");
		Thread.sleep(Long.MAX_VALUE);
	}
	public static void main(String[] args) throws Exception {
		 //1.��ȡzk����
		DistributeClient client = new DistributeClient();
		client.getConnection();
		//2.�õ�����zookeeper��ע��ķ������б�
		client.getServerList();
		//3.ִ��ҵ���߳�
		client.handleBusiness();
	}

}
