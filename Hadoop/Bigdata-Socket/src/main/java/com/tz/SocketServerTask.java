package com.tz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
/**
 * ҵ���߼�ʵ��
 * @author Administrator
 *
 */
public class SocketServerTask implements Runnable{
    Socket socket ;
    InputStream in = null;
    BufferedReader br = null;
    OutputStream out = null;
    PrintWriter pw = null;
	public SocketServerTask(Socket socket) {
		this.socket = socket;
	}

	public void run() {
	      //1.�õ��ͻ��˷��͵��������
		  try {
		    out = socket.getOutputStream();
		    in = socket.getInputStream();
		    pw = new PrintWriter(out);
			//ת����
			br = new BufferedReader(new InputStreamReader(in));
			String parm  = br.readLine();
			//2.����õ�����ҵ���߼�ʵ����
			@SuppressWarnings("unchecked")
			Class<GetDataServerImpl> Clazz = (Class<GetDataServerImpl>) Class.forName("com.tz.GetDataServerImpl");
			GetDataServerImpl getDataServerImpl = Clazz.newInstance();
			String data = getDataServerImpl.getData(parm);
			//3.���ؿͻ�����������
			pw.println(data);
			pw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			//4.�ͷ���Դ
			if ( in != null ) {
				try {
					in.close();
					if ( br != null ){
						br.close();
					}
					   if (pw != null ){
						   pw.close();
					   }
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
