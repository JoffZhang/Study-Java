package com.joffzhang.java.base;

/**
 * @author zy
 * @date 2020/9/24 17:31
 * <init>初始化过程
 * 1.父类静态（变量，代码块		--- 按顺序执行，属于同一级）
 * 2.子类静态（变量，代码块		--- 按顺序执行，属于同一级）
 * 3.父类全局变量，代码块（全局变量，代码块	---	按顺序执行，属于同一级）
 * 4.父类构造函数
 * 5.子类全局变量，代码块（全局变量，代码块	---	按顺序执行，属于同一级）
 * 6.子类构造函数
 * 其实并不是真的子类构造函数在父类构造函数之后执行，而是在子类构造器的第一行隐式调用了父类的构造函数;
 */
public class B02_ObjectInit {
	public static void main(String[] args) {
		Son s = new Son();

		InstanceInitializer instanceInitializer = new InstanceInitializer();
		System.out.println(instanceInitializer.j);
		//输出为 j = 0  变量j被赋予i的默认值0.	这一动作发生在实例变量i初始化之前和构造函数调用之前。
	}
}

class Parent {
	//实例变量
	private int p1 = getP1();
	//静态变量
	private static int p2 = getP2();

	public Parent() {
		System.out.println("Parent constructor");
	}

	//实例代码块
	{
		System.out.println("Parent Local code block");
	}

	//静态代码块
	static {
		System.out.println("Parent static code block");
	}


	private int getP1() {
		System.out.println("p1 is initialized!");
		return 1;
	}

	private static int getP2() {
		System.out.println("static p2 is initialized!");
		return 2;
	}
}

class Son extends Parent {
	//实例变量
	private int s1 = getS1();
	//静态变量
	private static int s2 = getS2();

	public Son() {
		System.out.println("Son constructor");
	}

	//实例代码块
	{
		System.out.println("Son Local code block");
	}

	//静态代码块
	static {
		System.out.println("Son static code block");
	}


	private int getS1() {
		System.out.println("s1 is initialized!");
		return 1;
	}

	private static int getS2() {
		System.out.println("static s2 is initialized!");
		return 2;
	}
}

class InstanceInitializer {
	int j = getI();
	int i = 1;

	int getI() {
		return i;
	}

	public InstanceInitializer() {
		i = 2;
	}
}