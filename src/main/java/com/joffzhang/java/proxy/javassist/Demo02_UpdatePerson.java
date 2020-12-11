package com.joffzhang.java.proxy.javassist;

import javassist.*;

import java.lang.reflect.Method;

/**
 * 修改现有的类对象#
 * 前面说到新增一个类对象。这个使用场景目前还没有遇到过，一般会遇到的使用场景应该是修改已有的类。比如常见的日志切面，权限切面。我们利用javassist来实现这个功能。
 * @author zy
 * @date 2020/12/11 11:22
 */
public class Demo02_UpdatePerson {

	/**
	 * 在personFly方法前后加上了打印日志。然后新增了一个方法joinFriend。执行main函数可以发现已经添加上了。
	 *
	 * 另外需要注意的是：上面的insertBefore() 和 setBody()中的语句，如果你是单行语句可以直接用双引号，但是有多行语句的情况下，你需要将多行语句用{}括起来。javassist只接受单个语句或用大括号括起来的语句块。
	 * @throws Exception
	 */
	public static void update() throws Exception {
		ClassPool pool = ClassPool.getDefault();
		CtClass ctClass = pool.get("com.joffzhang.java.proxy.javassist.PersonService");

		CtMethod personFly = ctClass.getDeclaredMethod("personFly");
		personFly.insertBefore("System.out.println(\"起飞前准备降落伞\");");
		personFly.insertAfter("System.out.println(\"成功起飞\");");

		//新增一个方法
		CtMethod joinFriend = new CtMethod(CtClass.voidType, "joinFriend", new CtClass[]{}, ctClass);
		joinFriend.setModifiers(Modifier.PUBLIC);
		joinFriend.setBody("{System.out.println(\"i want to be your friend\");}");
		ctClass.addMethod(joinFriend);

		Object personService = ctClass.toClass().newInstance();
		//调用personFly()
		Method personFly1 = personService.getClass().getMethod("personFly");
		personFly1.invoke(personService);
		//调用joinFriend()
		Method joinFriend1 = personService.getClass().getMethod("joinFriend");
		joinFriend1.invoke(personService);


	}

	public static void main(String[] args) {
		try {
			update();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
