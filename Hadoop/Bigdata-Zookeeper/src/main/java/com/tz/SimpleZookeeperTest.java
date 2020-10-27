package com.tz;

import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

/**
 * 
 * @author TZ
 *
 */
public class SimpleZookeeperTest {
  private static final String CONNECTSTRING = "192.168.25.132:2181,192.168.25.132:2182,192.168.25.132:2183";
  private static final int SESSIONTIMEOUT = 2000;
  ZooKeeper zkClient  = null;
  @Test
  public void Test() throws Exception{
	  //1.���zookeeper���Ӷ���
	    zkClient  = new ZooKeeper(CONNECTSTRING, SESSIONTIMEOUT, new Watcher() {
		public void process(WatchedEvent event) {
			System.out.println(event.getPath()+ "---" + event.getType());
			try {
				zkClient.getChildren("/", true);
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	});
	  zkClient.create("/kobe", "kobe".getBytes(), Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
	  List<String> childrenList = zkClient.getChildren("/", true);
	  for (String children : childrenList) {
		System.out.println(children);
	}
	  /**
	   * �ж��Ƿ����
	   */
	  Stat stat = zkClient.exists("/kobe", false);
	  System.out.println(stat == null?"no":"yes");
	  /**
	   * �õ�Ԫ��
	   */
	  byte[] data = zkClient.getData("/kobe", false, null);
	  System.out.println(new String(data));
	  /**
	   * ɾ���ڵ�����������
	   */
	  zkClient.delete("/kobe", -1);
  }     
     
}
