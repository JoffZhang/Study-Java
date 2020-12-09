package com.joffzhang.java.io.socket.nio;


/**
 * @author zy
 * @date 2020/8/26 14:00
 * NIO
 */
public class Server {
	private static int DEFAULT_PORT = 3333;
	private static ServerHandler serverHandler;

	public static void start(){
		start(DEFAULT_PORT);
	}

	public static void start(int port) {
		if(serverHandler != null){
			serverHandler.stop();
		}
		serverHandler = new ServerHandler(port);
		new Thread(serverHandler,"Server").start();
	}

}
