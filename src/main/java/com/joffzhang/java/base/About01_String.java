package com.joffzhang.java.base;

/**
 * @author zy
 * @date 2020/9/25 15:33
 * 字符串常量池
 * 1.字符串池
 * 为了减少在JVM中创建的字符串的数量，字符串类维护了一个字符串常量池，每当以字面值形式创建一个字符串时，JVM会首先检查字符串常量池
 * 如果字符串已经存在池中，就返回池中的实例引用；如果字符串不在池中，就会实例化一个字符串并放到池中。
 * Java能够进行这样的优化是因为字符串是不可 变的，可以不用担心数据冲突进行共享
 * 2.手动入池
 * 一个初始为空的字符串池，它由类 String 私有地维护。
 * 当调用 intern 方法时，
 * 		如果池已经包含一个等于此 String 对象的字符串（用 equals(Object) 方法确定），则返回池中的字符串。
 * 		否则，将此 String 对象添加到池中，并返回此 String 对象的引用。特别地，手动入池遵循以下规则：
 * 对于任意两个字符串 s 和 t ，当且仅当 s.equals(t) 为 true 时，s.intern() == t.intern() 才为 true 。
 */
public class About01_String {
	public static void main(String[] args) {
		//1.字符串常量池
		String s1 = "hello";
		//↑ 在字符串池创建了一个对象
		String s2 = "hello";
		//↑ 字符串pool已经存在对象“abc”(共享),所以创建0个对象，累计创建一个对象
		System.out.println("s1 == s2 : "+ (s1 == s2) );			//指向同一个对象	true
		System.out.println("s1.eqauls(s2) : "+ s1.equals(s2) );	//值相同			true

		String s3 = new String("hello");
		//↑ 创建了两个对象，一个存放在字符串池中，一个存在与堆区中；
		//↑ 还有一个对象引用s3存放在栈中
		String s4 = new String("hello");
		//↑ 字符串池中已经存在“hello”对象，所以只在堆中创建了一个对象
		String s5 = s3.intern();
		//↑ 手动入池
		System.out.println("s3 == s4 : "+ (s3 == s4));	    	//false   s3和s4栈区的地址不同，指向堆区的不同地址；
		System.out.println("s3.equals(s4) : "+ s3.equals(s4));	//true  s3和s4的值相同
		System.out.println("s1 == s3 : "+ (s1 == s3));			//false	存放区域不同，一个方法区，一个堆区

		System.out.println("s1 == s5 : "+ (s1 == s5));

		String str1 = "he";		//1个对象
		String str2 = "llo";	//1个对象
		String s6 = str1+str2;	//jvm创建一个StringBuilder对象，将字符串append  toString 堆中的对象
		System.out.println("s1 == s6 : "+ (s1 == s6));			//false
	}
}
