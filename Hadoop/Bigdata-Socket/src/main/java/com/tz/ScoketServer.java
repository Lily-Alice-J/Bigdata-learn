package com.tz;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket�����
 * @author Administrator
 *
 */
public class ScoketServer {

	public static void main(String[] args) throws IOException {
		//1.�õ�socket����
		ServerSocket serverSocket = new ServerSocket();
		serverSocket.bind(new InetSocketAddress("localhost", 8899));
       //2.���ܿͻ�������
		while(true){
			Socket socket = serverSocket.accept();
			//3.ִ��ҵ���߼���(�����߳̿���ʵ��˳��)
			new Thread(new SocketServerTask(socket)).start();
		}
		
	}

}
