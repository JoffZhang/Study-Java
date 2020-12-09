package com.joffzhang.java.io.socket.aio.server;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author zy
 * @date 2020/8/26 16:00
 * 作为handler接收客户端连接
 */
public class AcceptHandler implements CompletionHandler<AsynchronousSocketChannel, AsyncServerHandler> {

	@Override
	public void completed(AsynchronousSocketChannel channel, AsyncServerHandler serverHandler) {
		//继续接收其他客户端的请求
		Server.clientCount++;
		System.out.println("连接的客户端数：" + Server.clientCount);
		serverHandler.channel.accept(serverHandler,this);
		//创建新的buffer
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		//异步读， 第三个参数为接收消息回调的业务handler
		channel.read(byteBuffer,byteBuffer,new ReadHandler(channel));
	}

	@Override
	public void failed(Throwable exc, AsyncServerHandler serverHandler) {
		exc.printStackTrace();
		serverHandler.latch.countDown();
	}
}
