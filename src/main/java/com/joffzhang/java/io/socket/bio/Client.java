package com.joffzhang.java.io.socket.bio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author zy
 * @date 2020/8/26 11:42
 * 阻塞式I/O创建的客户端
 */
public class Client {
	//默认的端口号
	private static int DEFAULT_SERVER_PORT = 3333;

	public static void send(String expression){
		send(DEFAULT_SERVER_PORT,expression);
	}

	public static void send(int port, String expression) {
		System.out.println("算术表达式为：" + expression);
		try(
				Socket socket = new Socket(InetAddress.getLocalHost(),port);
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
		){
			out.println(expression);
			System.out.println("___结果为：" + in.readLine());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
