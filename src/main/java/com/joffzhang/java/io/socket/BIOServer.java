package com.joffzhang.java.io.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.*;

/**
 * @author zy
 * @date 2020/8/24 17:19
 * BIO 一请求一应答通信模型
 * socket.accept()、 socket.read()、 socket.write() 涉及的三个主要函数都是同步阻塞的
 */
public class BIOServer {

	public static void main(String[] args) throws IOException {
		int port = 3333;
		//ExecutorService executorService = Executors.newFixedThreadPool(3);
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 50L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

		new Thread(() -> {
			try {
				ServerSocket serverSocket = new ServerSocket(port);
				//接收到客户端连接请求后为每个客户端创建一个新的线程进行链路处理
				while(true){
					//accept  阻塞方法获取新的连接。  阻塞--程序释放cpu
					System.out.println("========accept阻塞========");
					Socket socket = serverSocket.accept();
					System.out.println("========获取新连接========"+socket.toString());
					//每一个新连接都创建一个线程负责读取数据
					threadPoolExecutor.execute(new Runnable() {
						@Override
						public void run() {
							int len;
							byte[] data = new byte[1024];
							try (
									InputStream inputStream = socket.getInputStream();
							){
								while((len =  inputStream.read(data)) != -1){
									System.out.print(Thread.currentThread().getId()+"处理数据"+socket.toString());
									System.out.println(new String(data,0,len));
								}
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					});
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();

	}

}
