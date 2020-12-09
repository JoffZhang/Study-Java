package com.joffzhang.java.io.socket.bio;

import java.io.IOException;
import java.util.Random;

/**
 * @author zy
 * @date 2020/8/26 11:50
 */
public class BIOStartMain {

	public static void main(String[] args) throws InterruptedException {
		new Thread(()->{
			try {
				System.out.println("线程"+Thread.currentThread());
				//ServerNormal.start();
				ServerBetter.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}).start();
		//避免客户端先于服务器启动前执行代码
		System.out.println("线程"+Thread.currentThread());
		Thread.sleep(100);
		//运行客户端
		char operators[] = {'+','-','*','/'};
		final Random random = new Random(System.currentTimeMillis());
		new Thread(()->{
			while(true){
				//产生随机算数表达式
				String exepression = random.nextInt(10)+""+operators[random.nextInt(4)]+(random.nextInt(10)+1);
				Client.send(exepression);
				try {
					System.out.println("线程"+Thread.currentThread());
					Thread.sleep(random.nextInt(1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
