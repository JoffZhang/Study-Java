package com.joffzhang.java.proxy.javassist;

import javassist.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 *
 *	javassist ---> ASM --->编辑JVM指令吗
 * @author zy
 * @date 2020/12/11 9:57
 */
public class Demo01_CreatePerson {

	public static CtClass createPerson() throws Exception {
		ClassPool classPool = ClassPool.getDefault();

		//1.创建一个空类
		CtClass ctClass = classPool.makeClass("com.joffzhang.java.proxy.javassist.Person");
		//2.新增一个字段private String name;
		//字段名为name
		CtField name = new CtField(classPool.get("java.lang.String"), "name", ctClass);
		//访问级别private
		name.setModifiers(Modifier.PRIVATE);
		//初始值是”xiaomei“
		ctClass.addField(name,CtField.Initializer.constant("xiaomei"));

		//3.生成getter、setter方法
		ctClass.addMethod(CtNewMethod.setter("setName",name));
		ctClass.addMethod(CtNewMethod.getter("getName",name));

		//4.添加无参的构造函数
		CtConstructor ctConstructor = new CtConstructor(new CtClass[]{}, ctClass);
		ctConstructor.setBody("{name = \"xiaohong\";}");
		ctClass.addConstructor(ctConstructor);

		//5.添加有参的构造函数
		ctConstructor = new CtConstructor(new CtClass[]{classPool.get("java.lang.String")},ctClass);
		//$0=this / $1,$2,$3... 代表方法参数
		ctConstructor.setBody("{$0.name=$1;}");
		ctClass.addConstructor(ctConstructor);

		//6.创建一个名为printName方法，无参数，无返回值，输出name值
		CtMethod printName = new CtMethod(CtClass.voidType, "printName", new CtClass[]{}, ctClass);
		printName.setModifiers(Modifier.PUBLIC);
		printName.setBody("{System.out.print(name);}");
		ctClass.addMethod(printName);
		return ctClass;
	}

	/**
	 * 输出，编译成.class文件
	 * @param ctClass
	 */
	public static void writeFile(CtClass ctClass){
		//这里会将这个创建的对象编译为.class文件
		ctClass.debugWriteFile("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java\\");
	}

	public static void main(String[] args){
		try {
//			//创建类
//			CtClass person = createPerson();
//			//编译成.class文件
//			writeFile(person);
//			//调用生成的类对象
//			//1.通过反射的方式调用
//			useByReflection(person);


//			//2.通过读取.class文件的方式调用，反射调用
//			useByLoadClass();


			//3.通过接口方式		如果类可以抽象出一个接口类。就可以newInstance()强转为接口，可以将反射的省略
			useByInterface();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void useByInterface() throws NotFoundException, CannotCompileException, IllegalAccessException, InstantiationException {
		ClassPool classPool = ClassPool.getDefault();
		classPool.appendClassPath("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java");
		//获取接口
		CtClass ctClassI = classPool.get("com.joffzhang.java.proxy.javassist.PersonI");
		//获取上面生成的类
		CtClass ctClass = classPool.get("com.joffzhang.java.proxy.javassist.Person");
		//使代码生成的类，实现PersonI接口
		ctClass.setInterfaces(new CtClass[]{ctClassI});

		//通过接口直接调用 强转
		PersonI personI = (PersonI) ctClass.toClass().newInstance();
		System.out.println(personI.getName());
		personI.setName("xiaonv");
		personI.printName();
	}

	private static void useByLoadClass() throws NotFoundException, InvocationTargetException, NoSuchMethodException, CannotCompileException, InstantiationException, IllegalAccessException {
		ClassPool classPool = ClassPool.getDefault();
		//设置类路径
		classPool.appendClassPath("D:\\ideaWorkSpace\\microservice-springcloud\\Study-Java\\src\\main\\java");
		CtClass ctClass = classPool.get("com.joffzhang.java.proxy.javassist.Person");

		//通过反射方式
		useByReflection(ctClass);

	}

	private static void useByReflection(CtClass ctClass) throws CannotCompileException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
		//实例化
		Object person = ctClass.toClass().newInstance();
		//设置
		Method setName = person.getClass().getMethod("setName", String.class);
		setName.invoke(person,"chuntian");
		//输出
		Method printName = person.getClass().getMethod("printName");
		printName.invoke(person);
	}


}
