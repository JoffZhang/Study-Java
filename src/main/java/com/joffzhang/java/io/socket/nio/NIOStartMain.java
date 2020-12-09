package com.joffzhang.java.io.socket.nio;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author zy
 * @date 2020/8/26 15:04
 */
public class NIOStartMain {

	public static void main(String[] args) throws InterruptedException, IOException {
		//运行服务器
		Server.start();
		//避免客户端先于服务器启动前执行代码
		Thread.sleep(1000);
		//运行客户端
		Client.start();
		while(Client.sendMsg(new Scanner(System.in).nextLine())){};

	}
}
