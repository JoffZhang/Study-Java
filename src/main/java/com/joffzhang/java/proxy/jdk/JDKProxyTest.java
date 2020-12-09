package com.joffzhang.java.proxy.jdk;

/**
 * @author zy
 * @date 2020/7/29 10:27
 */
public class JDKProxyTest {

	public static void main(String[] args) {
		/*java.lang.reflect.Proxy.newProxyInstance
		 *	java.lang.reflect.WeakCache.get
		 * 		java.lang.reflect.Proxy.ProxyClassFactory.apply
		 * 			sun.misc.ProxyGenerator#generateProxyClass(java.lang.String, java.lang.Class[], int)
		 */
		// 保存生成的代理类的字节码文件	com\sun\proxy\$Proxy0.class
		System.getProperties().put("sun.misc.ProxyGenerator.saveGeneratedFiles", "true");
		//实例化目标对象
		UserService userService = new UserServiceImpl();
		//实例化InvocationHandler
		MyInvocationHandler myInvocationHandler = new MyInvocationHandler(userService);
		//根据目标对象生成代理对象
		UserService proxy = (UserService) myInvocationHandler.getProxy();
		//调用代理对象的方法
		proxy.add();
	}
	/*查看生成的代理类字节码文件	com\sun\proxy\$Proxy0.class
		1、代理类继承了Proxy类并且实现了要代理的接口，由于java不支持多继承，所以JDK动态代理不能代理类

		2、重写了equals、hashCode、toString

		3、有一个静态代码块，通过反射或者代理类的所有方法

		4、通过invoke执行代理类中的目标方法doSomething
	 */
}
