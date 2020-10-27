package com.tz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *  Socket�ͻ���
 * @author Administrator
 *
 */
public class SocketClient {
	public static void main(String[] args) throws IOException {
		//1.��������
		Socket socket = new Socket("localhost", 8899);
		//2.��ȡ���������
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		PrintWriter pw = new PrintWriter(out);
		pw.println("hello");
		pw.flush();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String data = br.readLine();
		System.out.println(data);
		//3.�ر���Դ
		in.close();
		out.close();
		socket.close();
	}

}
