package com.joffzhang.java.io.socket.aio;

import com.joffzhang.java.io.socket.aio.client.Client;
import com.joffzhang.java.io.socket.aio.server.Server;

import java.util.Scanner;

/**
 * @author zy
 * @date 2020/8/26 16:36
 *
 * 我们可以在控制台输入我们需要计算的算数字符串，服务器就会返回结果，当然，我们也可以运行大量的客户端，都是没有问题的，以为此处设计为单例客户端，所以也就没有演示大量客户端并发。
 */
public class AIOStartMain {
	public static void main(String[] args) throws InterruptedException {
		//运行服务器
		Server.start();
		//避免客户端先于服务器启动前执行代码
		Thread.sleep(100);
		//运行客户端
		Client.start();
		System.out.println("请输入请求消息：");
		Scanner scanner = new Scanner(System.in);
		while(Client.sendMsg(scanner.nextLine()));
	}
}
