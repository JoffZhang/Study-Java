package com.joffzhang.java.io.socket.nio;

import java.io.IOException;

/**
 * @author zy
 * @date 2020/8/26 14:35
 * NIO客户端
 */
public class Client {
	//默认的端口号
	private static int DEFAULT_SERVER_PORT = 3333;

	private static ClientHandler clientHandler;
	public static void start(){
		start(DEFAULT_SERVER_PORT);
	}

	public static void start(int port) {
		if(clientHandler != null){
			clientHandler.stop();
		}
		clientHandler = new ClientHandler(port);
		new Thread(clientHandler,"client").start();
	}

	//向服务器发送消息
	public static boolean sendMsg(String msg) throws IOException {
		if(msg.equals("q")){return false;}
		clientHandler.sendMsg(msg);
		return true;
	}

}
