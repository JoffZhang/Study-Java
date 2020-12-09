package com.joffzhang.jvm.oops;

/**
 * @author zy
 * @date 2020/9/2 17:35
 */
public class User {
	private boolean sex;
	private int age ;

	public User() {
	}

	public User(boolean sex, int age) {
		this.sex = sex;
		this.age = age;
	}

	public boolean isSex() {
		return sex;
	}

	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}
}
