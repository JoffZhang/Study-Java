package com.joffzhang.java.base;

import sun.misc.Unsafe;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/**
 * @author zy
 * @date 2020/9/24 15:57
 * 对象创建
 * 	1.new
 * 	2.Class.newInstance
 * 	3.Constructor.newInstance
 * 	4.clone
 * 	5.反序列化
 */
public class B01_ObjectCreation {

	public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException, CloneNotSupportedException, IOException {
		//1.使用new关键字创建对象								可以调用任意的构造函数（无参的和有参的）去创建对象
		System.out.println("使用new关键字创建对象：");
		BaseObj baseObj = new BaseObj();
		System.out.println(baseObj);

		//2.使用Class类的newInstance方法(反射机制)				调用无参的构造器创建对象
		System.out.println("使用Class类的newInstance方法创建对象：");
		BaseObj baseObj2 = (BaseObj) Class.forName(BaseObj.class.getName()).newInstance();
		BaseObj baseObj2_2 = BaseObj.class.newInstance();
		System.out.println(baseObj2);


		//3.使用Constructor类的newInstance方法(反射机制)
		//java.lang.relect.Constructor类里也有一个newInstance方法可以创建对象，该方法和Class类中的newInstance方法很像，但是相比之下，Constructor类的newInstance方法更加强大些，我们可以通过这个newInstance方法调用有参数的和私有的构造函数
		//使用newInstance方法的这两种方式创建对象使用的就是Java的反射机制，事实上Class的newInstance方法内部调用的也是Constructor的newInstance方法。
		System.out.println("使用Constructor类的newInstance方法创建对象：");
		Constructor<BaseObj> constructor = BaseObj.class.getConstructor(Integer.class);
		BaseObj baseObj3 = constructor.newInstance(123);
		System.out.println(baseObj3);

		//4.使用Clone方法创建对象		用clone方法创建对象的过程中并不会调用任何构造函数
		//要想使用clone方法，我们就必须先实现Cloneable接口并实现其定义的clone方法，这也是原型模式的应用
		System.out.println("使用Clone方法创建对象：");
		Constructor<BaseObj> constructor1 = BaseObj.class.getConstructor(Integer.class);
		BaseObj baseObj4 = constructor1.newInstance(123);
		Object obj4 = baseObj4.clone();
		System.out.println(obj4);

		//5.使用(反)序列化机制创建对象
		//JVM并不会调用任何构造函数。为了反序列化一个对象，我们需要让我们的类实现Serializable接口
		System.out.println("使用(反)序列化机制创建对象：");
		Constructor<BaseObj> constructor2 = BaseObj.class.getConstructor(Integer.class);
		BaseObj baseObj5 = constructor2.newInstance(123);
		//写对象
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("student.bin"));
		output.writeObject(baseObj5);
		output.close();
		//读对象
		ObjectInputStream input = new ObjectInputStream(new FileInputStream("student.bin"));
		BaseObj baseObj5_2 = (BaseObj) input.readObject();
		System.out.println(baseObj5_2);

		//6.使用Unsafe类创建对象
		//Unsafe类使Java拥有了像C语言的指针一样操作内存空间的能力，同时也带来了指针的问题。过度的使用Unsafe类会使得出错的几率变大，因此Java官方并不建议使用的
		System.out.println("使用Unsafe创建对象：");
		BaseObj baseObj6 = (BaseObj) getUnsafe().allocateInstance(BaseObj.class);
		System.out.println(baseObj6);
	}

	private static Unsafe getUnsafe(){
		try{
			//获取Unsafe内部的私有实例化单例对象
			Field filed = Unsafe.class.getDeclaredField("theUnsafe");
			//无视权限
			filed.setAccessible(true);
			return (Unsafe) filed.get(null);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}
}

