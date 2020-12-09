package com.joffzhang.java.proxy.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author zy
 * @date 2020/7/29 13:29
 */
public class MyMethodInterceptorImpl implements MethodInterceptor {
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		//这里增强
		System.out.println(MyMethodInterceptorImpl.class.toString() + " before invokeSuper " + method);
		//此处一定要使用proxy的invokeSuper方法来调用目标类的方法
		//当调用invokeSuper方法时，实际上是调用代理类的CGLIB$g$0方法，CGLIB$g$0直接调用了目标类的g方法。
		Object result = proxy.invokeSuper(obj, args);
		System.out.println(MyMethodInterceptorImpl.class.toString() + " after invokeSuper " + method);
		return result;
	}

	public Object getProxy(Class<?> cls) {
		//创建Enhancer对象，类似于JDK动态代理的Proxy类    实例化一个增强器，也就是cglib中的一个class generator
		Enhancer enhancer = new Enhancer();
		//设置目标类的字节码文件
		enhancer.setSuperclass(cls);
		//设置回调函数  设置拦截对象
		enhancer.setCallback(this);
		//正式创建代理类
		return enhancer.create();
	}
}
