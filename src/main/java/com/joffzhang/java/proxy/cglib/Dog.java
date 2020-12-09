package com.joffzhang.java.proxy.cglib;

/**
 * @author zy
 * @date 2020/7/29 14:00
 */
public class Dog {
	final public void run(String name) {
		System.out.println("dog " + name + "----run");
	}

	public void eat() {
		System.out.println("dog ----eat");
	}
}
