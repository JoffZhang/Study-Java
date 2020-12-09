package com.joffzhang.java.io.socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zy
 * @date 2020/8/25 13:42
 * NIO 是利用了单线程轮询事件的机制，通过高效地定位就绪的 Channel，来决定做什么，仅仅 select 阶段是阻塞的，可以有效避免大量客户端连接时，频繁线程切换带来的问题，应用的扩展能力有了非常大的提高。
 */
public class NIOServer {


	public static void main(String[] args) throws IOException {
		int port = 3333;
		ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(4, 4, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
		/**
		 * 1.通过Select.open()创建一个Selector，作为类似调度员的角色
		 * 服务端监测到新的连接后，不再创建一个新的线程，而是直接将新的连接绑定到clientSelector上，这样就不用IO模型中1w个while循环在死等
		 */
		Selector selector = Selector.open();
		/**	2.clientSelector负责轮询连接是否有数据可读 **/
		Selector clientSelector = Selector.open();
		threadPoolExecutor.execute(() -> {
			try (
					/** 3.创建一个ServerSocketChannel，并向Select注册。**/
					ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
			) {
				serverSocketChannel.bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
				//明确配置非阻塞模式。阻塞模式下，注册操作是不允许的，会抛出IllegalBlockingModeException 异常；
				serverSocketChannel.configureBlocking(false);
				//通过SelelectionKey.OP_ACCEPT,告诉调度员，它关注的是新的连接请求;
				serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
				while (true) {
					System.out.println("===============selector.select()阻塞等待就绪的channel====================");
					////阻塞等待就绪的channel 。	监测是否有新的连接，这里的1指的是阻塞的时间为1ms
					if(selector.select(1000) > 0){
						Set<SelectionKey> selectionKeys = selector.selectedKeys();
						Iterator<SelectionKey> iterator = selectionKeys.iterator();
						while (iterator.hasNext()) {
							SelectionKey key = iterator.next();
							if(key.isAcceptable()) {
								try (
										//（1）每来一个新连接，不需要创建一个线程，而是直接注册到clientSelector
										SocketChannel clientChannel = ((ServerSocketChannel) key.channel()).accept()
								) {
									clientChannel.configureBlocking(false);
									clientChannel.register(clientSelector,SelectionKey.OP_READ);
									clientChannel.write(Charset.defaultCharset().encode("关注新的连接请求。你好,Object"));
								}finally {
									iterator.remove();
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		threadPoolExecutor.execute(() -> {
			try {
				while(true){
					//（2）批量轮询是否有那些连接有数据可读，这里的1指的是阻塞时间为1ms
					System.out.println("===============selector.select()阻塞等待看哪些连接有数据可读====================");
					if(clientSelector.select(1000) > 0){
						Set<SelectionKey> selectionKeys = clientSelector.selectedKeys();
						Iterator<SelectionKey> iterator = selectionKeys.iterator();
						while(iterator.hasNext()){
							SelectionKey key = iterator.next();
							if(key.isReadable()){
								try{
									SocketChannel clientChannel = (SocketChannel) key.channel();
									ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
									//（3）面向Buffer
									clientChannel.read(byteBuffer);
									byteBuffer.flip();
									System.out.println(Charset.defaultCharset().newDecoder().decode(byteBuffer).toString());
								}finally {
									iterator.remove();
									key.interestOps(SelectionKey.OP_READ);
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

	}
}
