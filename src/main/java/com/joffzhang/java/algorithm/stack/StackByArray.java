package com.joffzhang.java.algorithm.stack;

import java.util.Arrays;

/**
 * @author zy
 * @date 2020/8/18 13:32
 */
public class StackByArray<T> {

	private int capacity;
	private int size;

	private Object[] elementData;

	public StackByArray(int initSize) {
		this.capacity = initSize;
		elementData = new Object[capacity];
	}
	
	public void push(T data){
		if(size == capacity){
			throw new RuntimeException("满栈");
		}
		elementData[size++] = data;
	}

	public T pop(){
		if(empty()){
			throw new RuntimeException("空栈");
		}
		T data = (T) elementData[--size];
		elementData[size] = null;//设置null，让jvm垃圾回收
		return data;
	}

	public T  peek(){
		return (T) elementData[size-1];
	}
	public boolean empty(){
		return size == 0;
	}

	public int length(){
		return size;
	}

	public static void main(String[] args) throws Exception {
		StackByArray<String> stack = new StackByArray<>(10);
		for (int i = 0; i < 10; i++) {
			stack.push("111111-" + i);
		}
		System.out.println(stack);
		System.out.println("frist pop="+stack.pop());
		System.out.println("second pop="+stack.pop());
		System.out.println("thrid pop="+stack.pop());
		System.out.println("pop 之后的"+stack);
	}

	@Override
	public String toString() {
		return "StackByArray{" +
				"capacity=" + capacity +
				", size=" + size +
				", elementData=" + Arrays.toString(elementData) +
				'}';
	}
}
