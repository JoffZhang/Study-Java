package com.joffzhang.java.io.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * @author zy
 * @date 2020/8/25 16:14
 *
 */
public class AIOServer {
	public static void main(String[] args) {
		int port = 3333;
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		threadPoolExecutor.execute(() -> {
			AsynchronousChannelGroup group = null;
			try {
				group = AsynchronousChannelGroup.withThreadPool(Executors.newFixedThreadPool(4));
				AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
				server.accept(null, new CompletionHandler<AsynchronousSocketChannel, AsynchronousServerSocketChannel>() {

					@Override
					public void completed(AsynchronousSocketChannel result, AsynchronousServerSocketChannel attachment) {
						server.accept(null,this);//接收下一个请求
						try{
							Future<Integer> f = result.write(Charset.defaultCharset().encode("hello world"));
							f.get();
							System.out.println("服务端发送时间：" + LocalDateTime.now().toString());
							result.close();
						} catch (InterruptedException | ExecutionException | IOException e){
							e.printStackTrace();
						}
					}

					@Override
					public void failed(Throwable exc, AsynchronousServerSocketChannel attachment) {
					}
				});
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
		//客户端
		try {
			AsynchronousSocketChannel client = AsynchronousSocketChannel.open();
			Future<Void> future = client.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
			future.get();
			ByteBuffer buffer = ByteBuffer.allocate(100);
			client.read(buffer, null, new CompletionHandler<Integer, Void>() {
				@Override
				public void completed(Integer result, Void attachment) {
					System.out.println("客户端打印：" + new String(buffer.array()));
				}

				@Override
				public void failed(Throwable exc, Void attachment) {
					exc.printStackTrace();
					try {
						client.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			Thread.sleep(10_000);
		} catch (InterruptedException | ExecutionException | IOException e) {
			e.printStackTrace();
		}
	}
}
