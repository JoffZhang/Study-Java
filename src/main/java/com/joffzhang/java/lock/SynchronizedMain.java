package com.joffzhang.java.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author zy
 * @date 2020/9/7 11:05
 * synchronized
 */
public class SynchronizedMain {
	private static int i;

	/**
	 * 作用于实例对象的实例方法
	 * 锁：当前【实例】对象
	 **/
	public synchronized void increase() {
		i++;
	}

	/**
	 * 作用于静态方法
	 * 锁：当前类的【SynchronizedMain.class】对象
	 **/
	public static synchronized void syncStaticMethodUseClass() {
		i++;
	}
	/** ↑等同↓ */
	public void syncMethodUseClass(){
		synchronized(SynchronizedMain.class){
			i++;
		}
	}

	Object lock = new Object();

	/**
	 * 作用于代码块
	 **/
	public void syncObject() {
		//同步代码块
		synchronized (lock) {
			i++;
		}
	}

	/**
	 * 锁消除
	 * 消除StringBuffer同步锁
	 **/
	public String add(String str1, String str2) {
		//StringBuffer是线程安全,由于sb只会在append方法中使用,不可能被其他线程引用
		//因此sb属于不可能共享的资源,JVM会自动消除内部的锁
		StringBuffer sb = new StringBuffer();
		sb.append(str1).append(str2);
		return sb.toString();
	}

	public void syncWithInterrupt() throws InterruptedException {
		SynchronizedWithInterrupt synchronizedWithInterrupt = new SynchronizedWithInterrupt();
		Thread thread = new Thread(synchronizedWithInterrupt);
		//为保证thread线程在SynchronizedWithInterrupt构造函数内的线程  之后启动，让休息一下
		TimeUnit.SECONDS.sleep(1);
		//启动后调用f()方法,无法获取当前实例锁处于等待状态
		thread.start();
		TimeUnit.SECONDS.sleep(1);
		//中断线程,无法生效
		thread.interrupt();
		System.out.println("thread.id " +thread.getId()+" already do thread.interrupt() "+thread.isInterrupted());
	}

	public static void main(String[] args) throws InterruptedException {
		SynchronizedMain synchronizedMain = new SynchronizedMain();
		synchronizedMain.syncWithInterrupt();
	}
}

/**
 * 线程的中断操作对于正在等待获取的锁对象的synchronized方法或者代码块并不起作用，
 * 也就是对于synchronized来说，如果一个线程在等待锁，那么结果只有两种，要么它获得这把锁继续执行，要么它就保存等待，即使调用中断线程的方法，也不会生效
 */
class SynchronizedWithInterrupt implements Runnable {

	public synchronized void f() {
		System.out.println("Trying to call f() thread.id="+Thread.currentThread().getId());
		while (true) {//Never releases lock
			Thread.yield();
		}
	}

	/**
	 * 在构造函数中创建一个新线程并启动获取对象锁
	 */
	public SynchronizedWithInterrupt() {
		//该线程已持有当前实例锁
		new Thread() {
			@Override
			public void run() {
				System.out.println("SynchronizedWithInterrupt() Thread {} call f() thread.id="+Thread.currentThread().getId());
				f();// Lock acquired by this thread
			}
		}.start();
	}

	@Override
	public void run() {
		//中断判断
		while (true) {
			if (Thread.interrupted()) {
				System.out.println("中断线程！");
				break;
			} else {
				System.out.println("SynchronizedWithInterrupt run Thread {} call f() thread.id="+Thread.currentThread().getId());
				f();
			}
		}
	}
}