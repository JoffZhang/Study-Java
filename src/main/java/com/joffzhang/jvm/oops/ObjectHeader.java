package com.joffzhang.jvm.oops;

import org.openjdk.jol.info.ClassLayout;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author zy
 * @date 2020/9/2 17:33
 * 对象内存布局结构
 * 对象头+元数据指针Class MetaDate+(数组长度,可有)+填充位
 */
public class ObjectHeader {

	protected static User user = new User();

	static ReentrantLock rl = new ReentrantLock();

	/**
	 * 检验ReentrantLock加锁原理机制 * * @see java.util.concurrent.locks.ReentrantLock * @see java.util.concurrent.locks.AbstractQueuedSynchronizer
	 * 原理：跟踪源码AbstractQueuedSynchronizer可以看出，这个是改变ReentrantLock类的state字段 * 如果加锁成功则state=1，否则为0
	 */
	public static void checkRtLock() {
		rl.lock();
		System.out.println("ReentrantLock ---> 锁原理解析");
		rl.unlock();
	}

	/**
	 * 同理：ReentrantLock通过改变标识为1来加锁，那么synchronized加锁机制 * 肯定也会改变类的什么东西【java对象头】来标识已经加锁
	 */
	public static void checkSyn() {
		synchronized (user) {
			System.out.println("synchronized ---> 锁原理解析 ---> java对象头");
			System.out.println(ClassLayout.parseInstance(user).toPrintable());
		}
	}

	public static void main(String[] args) {
		System.out.println(Integer.toHexString(user.hashCode()));
		checkSyn();
		System.out.println(ClassLayout.parseInstance(user).toPrintable());
	}
}
