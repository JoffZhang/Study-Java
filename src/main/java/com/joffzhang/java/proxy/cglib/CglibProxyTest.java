package com.joffzhang.java.proxy.cglib;

import net.sf.cglib.core.DebuggingClassWriter;

/**
 * @author zy
 * @date 2020/7/29 13:27
 */
public class CglibProxyTest {

	public static void main(String[] args) {
		//在指定目录下生成动态代理类，我们可以反编译看一下里面到底是一些什么东西  com.joffzhang.java.proxy.cglib.Dog$$EnhancerByCGLIB$$xxxxxxxxx
		/*
		 *CGLIB生成的class明明规则有多种，他们都是由接口NamingPolicy对应的实现来定义的	org.springframework.cglib.core.DefaultNamingPolicy
		 * 	被代理class name(包名和类型)+"$$"+
		 * 	使用cglib处理的class name(只有类名，不包含包名)+"ByCGLIB"+"$$"+
		 * 	key的hashcode+
		 * 	序列号
		 *
		 * 生成3个class文件，  一个是生成的代理类，另外两个都是fastClass机制需要的。另外两个类都继承了FastClass这个类。其中一个class为生成的代理类中的每个方法创建了索引，另一个则为我们被代理类的所有方法包含其父类的方法建立了索引
		 * invoke 方法调用的对象没有增强过，invokeSuper调用的对象已经增强了，所以会走一遍MethodInterceptor的interceptor方法，如果是个拦截器链条,就会重新走一次连接器链。
		 */
		System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java");
		MyMethodInterceptorImpl myMethodInterceptor = new MyMethodInterceptorImpl();
		Dog proxy = (Dog) myMethodInterceptor.getProxy(Dog.class);

		proxy.run("xxxxxx");
		proxy.eat();

		System.out.println(proxy);
	}
}
