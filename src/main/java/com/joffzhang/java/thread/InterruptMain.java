package com.joffzhang.java.thread;

import java.util.concurrent.TimeUnit;

/**
 * @author zy
 * @date 2020/9/7 15:31
 * 线程中断
 * //中断线程（实例方法）
 * public void Thread.interrupt();
 * <p>
 * //判断线程是否被中断（实例方法）
 * public boolean Thread.isInterrupted();
 * <p>
 * //判断是否被中断并清除当前中断状态（静态方法）
 * public static boolean Thread.interrupted();
 * <p>
 * 中断两种情况：
 * 	一种是当线程处于阻塞状态或者试图执行一个阻塞操作时，我们可以使用实例方法interrupt()进行线程中断，执行中断操作后将会抛出interruptException异常(该异常必须捕捉无法向外抛出)并将中断状态复位
 * 	另外一种是当线程处于运行状态时，我们也可调用实例方法interrupt()进行线程中断，但同时必须手动判断中断状态，并编写中断线程的代码(其实就是结束run方法体的代码)
 */
public class InterruptMain {

	public static void main(String[] args) throws InterruptedException {
		//当前线程处于阻塞状态或视图执行一个阻塞操作，使用实例方法interrupt()中断，抛出interruptException（异常需捕获）
		//interrupt1();
		//运行期间非阻塞线程，调用Thread.interrupt()无效
		//interrupt2_1();
		//处于非阻塞状态线程需要手动进行中断检测并结束程序
		//interrupt2_2();
		//兼顾两种情况
		interrupt3();
	}

	private static void interrupt3() throws InterruptedException {
		Thread thread = new Thread(()->{
			try{
				//判断当前线程是否已中断,注意interrupted方法是静态的,执行后会对中断状态进行复位
				while(!Thread.interrupted()){
					TimeUnit.SECONDS.sleep(2);
				}
				System.out.println("情况2:线程处于运行状态");
			} catch (InterruptedException e) {
				System.out.println("情况1:线程处于阻塞状态或执行一个阻塞操作时，抛出interruptException");
			}
		});
		thread.start();
		TimeUnit.SECONDS.sleep(2);
		thread.interrupt();
	}

	private static void interrupt2_2() throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (true) {
					//判断当前线程是否被中断
					if (this.isInterrupted()) {//一开始为false,执行interrupt后变为true
						System.out.println("线程中断");
						break;
					}
				}
				System.out.println("已跳出循环，线程中断！");
			}
		};
		thread.start();
		TimeUnit.SECONDS.sleep(2);
		thread.interrupt();
		/**
		 * 输出结果：
		 * 	线程中断
		 * 	已跳出循环，线程中断！
		 */
	}

	/*运行期间非阻塞线程，调用Thread.interrupt()无效*/
	private static void interrupt2_1() throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				while (true) {
					System.out.println("未被中断,interrupt:" + this.isInterrupted());//一开始为false,执行interrupt后变为true
				}
			}
		};
		thread.start();
		TimeUnit.SECONDS.sleep(2);
		thread.interrupt();
		/**
		 * 输出结果(无限执行):
		 *	未被中断,interrupt:false
		 *	未被中断,interrupt:false
		 *	未被中断,interrupt:true (循环)
		 */
	}

	/*当前线程处于阻塞状态或视图执行一个阻塞操作，使用实例方法interrupt()中断，抛出interruptException（异常需捕获）*/
	public static void interrupt1() throws InterruptedException {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {//while在try中，通过异常中断就可以退出run循环
					while (true) {
						//当前线程处于阻塞状态，异常必须捕捉处理，无法往外抛出
						TimeUnit.SECONDS.sleep(2);
					}
				} catch (InterruptedException e) {
					System.out.println("Interruted when sleep");
					boolean interrupt = this.isInterrupted();
					System.out.println("interrupt:" + interrupt);
				}
			}
		};
		thread.start();
		TimeUnit.SECONDS.sleep(2);
		thread.interrupt();
		/**
		 * 输出结果:
		 *	Interruted When Sleep
		 *	interrupt:false
		 */
	}

}
