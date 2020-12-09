package com.joffzhang.java.io.socket.aio.server;

import com.joffzhang.java.io.socket.aio.server.AsyncServerHandler;

/**
 * @author zy
 * @date 2020/8/26 15:52
 * AIO服务端
 * NIO 2.0引入了新的异步通道的概念，并提供了异步文件通道和异步套接字通道的实现。
 *
 *     异步的套接字通道时真正的异步非阻塞I/O，对应于UNIX网络编程中的事件驱动I/O（AIO）。他不需要过多的Selector对注册的通道进行轮询即可实现异步读写，从而简化了NIO的编程模型。
 */
public class Server {

	private static int DEFAULT_PORT = 3333;

	private static AsyncServerHandler serverHandler;

	public volatile static long clientCount = 0;

	public static void start(){
		start(DEFAULT_PORT);
	}

	public static void start(int port) {
		if(serverHandler != null){
			return ;
		}
		serverHandler = new AsyncServerHandler(port);
		new Thread(serverHandler,"server").start();
	}

	public static void main(String[] args) {
		Server.start();
	}
}
