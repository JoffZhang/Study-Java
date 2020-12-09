package com.joffzhang.java.io.socket.aio.client;

import com.joffzhang.java.io.socket.Calculator;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author zy
 * @date 2020/8/26 16:05
 */
public class ReadHandler implements CompletionHandler<Integer, ByteBuffer> {
	//用于读取半包消息和发送应答
	private AsynchronousSocketChannel channel;
	private CountDownLatch latch;

	public ReadHandler(AsynchronousSocketChannel channel, CountDownLatch countDownLatch) {
		this.channel = channel;
		this.latch = countDownLatch;
	}
	@Override
	public void completed(Integer result, ByteBuffer attachment) {
		//flip操作
		attachment.flip();
		byte[] message = new byte[attachment.remaining()];
		attachment.get(message);
		try{
			String body = new String(message,"UTF-8");
			System.out.println("客户端收到结果:"+ body);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void failed(Throwable exc, ByteBuffer attachment) {
		System.err.println("数据读取失败...");
		try {
			this.channel.close();
			latch.countDown();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
