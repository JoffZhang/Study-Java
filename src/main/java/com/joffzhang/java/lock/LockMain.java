package com.joffzhang.java.lock;

import org.openjdk.jol.info.ClassLayout;

/**
 * @author zy
 * @date 2020/9/4 14:13
 * 锁
 * 	无锁-》偏向锁-》轻量级锁-》重量级锁
 */
public class LockMain {

	public static void main(String[] args) throws InterruptedException {
		//noLockState();
		//anonymousBiasLockAndBiasLock();
		lightweightLock();
		//heavyweightLock();
	}

	/**1.无锁状态**/
	public static void noLockState(){
		Object obj = new Object();
		System.out.println("=========no lock state====================================");
		System.out.println("hash:"+Integer.toHexString(obj.hashCode()));
		System.out.println(ClassLayout.parseInstance(obj).toPrintable());
	}
	/**2.匿名偏向锁和偏向锁**/
	public static void anonymousBiasLockAndBiasLock() throws InterruptedException {
		Thread.sleep(5000);//等待jvm开启偏向锁
		Object obj = new Object();
		System.out.println("=========anonymous Bias Lock And Bias Lock================");
		System.out.println(ClassLayout.parseInstance(obj).toPrintable());
		synchronized( obj ){
			System.out.println(ClassLayout.parseInstance(obj).toPrintable());
		}
	}
	/**3.轻量级锁**/
	public static void lightweightLock() throws InterruptedException {
		Thread.sleep(5000);
		Object obj = new Object();
		System.out.println("=========light weight Lock================");
		synchronized (obj){
			System.out.println(ClassLayout.parseInstance(obj).toPrintable());
		}
		System.out.println(ClassLayout.parseInstance(obj).toPrintable());
		new Thread(()->{
			synchronized(obj){
				System.out.println(ClassLayout.parseInstance(obj).toPrintable());
			}
		}).start();
		Thread.sleep(1000);
		System.out.println(ClassLayout.parseInstance(obj).toPrintable());
	}
	/**4.重量级锁**/
	public static void heavyweightLock(){
		Object obj = new Object();
		System.out.println("=========heavy weight Lock================");
		for (int i = 0 ; i< 50;i++){
			new Thread(()->{
				synchronized (obj){
					System.out.println(ClassLayout.parseInstance(obj).toPrintable());
				}
			}).start();
		}
	}
}
