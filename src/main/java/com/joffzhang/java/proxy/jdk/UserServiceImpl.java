package com.joffzhang.java.proxy.jdk;

/**
 * 2.创建业务接口实现类
 *
 * @author zy
 * @date 2020/7/29 10:27
 */
public class UserServiceImpl implements UserService {
	@Override
	public void add() {
		System.out.println(UserServiceImpl.class.toString() + "...add");
	}
}
