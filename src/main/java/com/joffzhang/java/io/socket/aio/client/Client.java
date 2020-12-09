package com.joffzhang.java.io.socket.aio.client;

import java.util.Scanner;

/**
 * @author zy
 * @date 2020/8/26 16:14
 */
public class Client {
	private static int DEFAULT_SERVER_PORT = 3333;

	private static AsyncClientHandler clienthandler;

	public static void start(){
		start(DEFAULT_SERVER_PORT);
	}

	public static void start(int port) {
		if(clienthandler != null){
			return;
		}
		clienthandler = new AsyncClientHandler(port);
		new Thread(clienthandler,"client").start();
	}

	//向服务器发送消息
	public static boolean sendMsg(String msg){
		if(msg.equals("q")){
			return false;
		}
		clienthandler.sendMsg(msg);
		return true;
	}

	public static void main(String[] args) {
		Client.start();
		System.out.println("请输入请求消息：");
		while(Client.sendMsg(new Scanner(System.in).nextLine())){
		}
	}

}
