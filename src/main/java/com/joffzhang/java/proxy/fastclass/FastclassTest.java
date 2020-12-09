package com.joffzhang.java.proxy.fastclass;

/**
 * @author zy
 * @date 2020/7/29 15:30
 */
public class FastclassTest {
	public static void main(String[] args) {
		Test test = new Test();
		Test2 fc = new Test2();
		int index = fc.getIndex("f()V");
		fc.invoke(index,test,null);
	}
}

class Test{
	public void f(){
		System.out.println(Test.class.toString() + " f method " );
	}

	public void g(){
		System.out.println(Test.class.toString() + " g method " );
	}
}
class Test2{
	//Test2是Test的Fastclass，在Test2中有两个方法getIndex和invoke。
	// 在getIndex方法中对Test的每个方法建立索引，并根据入参（方法名+方法的描述符）来返回相应的索引。
	// Invoke根据指定的索引，以ol为入参调用对象O的方法。这样就避免了反射调用，提高了效率。
	public Object invoke(int index,Object o ,Object[] o1){
		Test t = (Test)o;
		switch (index){
			case 1:
				t.f();
				return null;
			case 2:
				t.g();
				return null;
		}
		return null;
	}

	public int getIndex(String signature){
		switch (signature.hashCode()){
			case 3078479:
				return 1;
			case 3108270:
				return 2;
		}
		return -1;
	}
}