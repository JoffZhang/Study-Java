package com.joffzhang.java.proxy.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author zy
 * @date 2020/7/29 10:29
 */
public class MyInvocationHandler implements InvocationHandler {
	//目标对象
	private Object target;

	/**
	 * 构造方法
	 *
	 * @param target
	 */
	public MyInvocationHandler(Object target) {
		super();
		this.target = target;
	}

	/**
	 * 执行目标对象的方法
	 *
	 * @param proxy
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		System.out.println(MyInvocationHandler.class.toString() + "... before");
		Object result = method.invoke(target, args);
		System.out.println(MyInvocationHandler.class.toString() + "... after");
		return result;
	}

	/**
	 * 获取目标对象的代理对象
	 *
	 * @return
	 */
	public Object getProxy() {
		return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), target.getClass().getInterfaces(), this);
	}
}
