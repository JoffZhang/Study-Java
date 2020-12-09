package com.joffzhang.java.io.socket.nio;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * @author zy
 * @date 2020/8/26 14:43
 * NIO客户端
 */
public class ClientHandler implements Runnable{
	private int port;
	private volatile boolean started;
	private Selector selector;
	private SocketChannel socketChannel;

	public ClientHandler(int port) {
		this.port = port;
		try {
			//创建选择器
			selector = Selector.open();
			//打开监听通道
			socketChannel = SocketChannel.open();
			//如果为true，则此通道将被置于阻塞模式；如果为false则该通道将被置于非阻塞模式
			socketChannel.configureBlocking(false);//开启非阻塞模式
			started = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		try{
			doConnect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//循环遍历selector
		while(started) {
			try {

				//无论是否有读写事件发生，selector每隔1s被唤醒一次
				selector.select(1000);
				//阻塞,只有当至少一个注册的事件发生的时候才会继续.
//				selector.select();
				Set<SelectionKey> keys = selector.selectedKeys();
				Iterator<SelectionKey> it = keys.iterator();
				SelectionKey key = null;
				while (it.hasNext()) {
					key = it.next();
					it.remove();
					try {
						handleInput(key);
					} catch (Exception e) {
						if (key != null) {
							key.cancel();
							if (key.channel() != null) {
								key.channel().close();
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		//selector关闭后会自动释放里面管理的资源
		if(selector != null){
			try{
				selector.close();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void handleInput(SelectionKey key) throws IOException {
		if(key.isValid()){
			SocketChannel sc = (SocketChannel) key.channel();
			if(key.isConnectable()){
				if(sc.finishConnect()){}
				else{
					System.exit(1);
				}
			}
			//读取消息
			if(key.isReadable()){
				ByteBuffer byteBuffer = ByteBuffer.allocate(1024);

				int readBytes = sc.read(byteBuffer);

				if(readBytes > 0){

					byteBuffer.flip();

					byte[] bytes = new byte[byteBuffer.remaining()];
					byteBuffer.get(bytes);
					String result = new String(bytes,"UTF-8");
					System.out.println("客户端收到消息：" + result);
				}
				//没有读取到字节 忽略
//				else if(readBytes==0);
				//链路已经关闭，释放资源
				else if(readBytes<0){
					key.cancel();
					sc.close();
				}

			}
		}
	}

	private void doConnect() throws IOException {
		if(socketChannel.connect(new InetSocketAddress(InetAddress.getLocalHost(),port))){
		}else{
			socketChannel.register(selector, SelectionKey.OP_CONNECT);
		}
	}

	public void stop() {
		started = false;
	}

	public void sendMsg(String msg) throws IOException {
		socketChannel.register(selector,SelectionKey.OP_READ);
		doWrite(socketChannel,msg);
	}

	//异步发送消息
	private void doWrite(SocketChannel channel,String request) throws IOException{
		//将消息编码为字节数组
		byte[] bytes = request.getBytes();
		//根据数组容量创建ByteBuffer
		ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
		//将字节数组复制到缓冲区
		writeBuffer.put(bytes);
		//flip操作
		writeBuffer.flip();
		//发送缓冲区的字节数组
		channel.write(writeBuffer);
		//****此处不含处理“写半包”的代码
	}

}
