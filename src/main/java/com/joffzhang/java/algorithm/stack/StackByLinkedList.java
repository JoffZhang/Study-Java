package com.joffzhang.java.algorithm.stack;

/**
 * @author zy
 *
 * 栈
 *	特点：栈是先进后出，只能访问栈顶的数据
 *主要机制可以用数组来实现，也可以用链表来实现
 * @date 2020/8/18 11:31
 * 基于链表实现栈
 */
public class StackByLinkedList<T> {

	Node<T> top;	//永远指向栈顶元素
	int size;	//元素个数

	//入栈
	public void push(T data){
		if(data == null){
			throw new NullPointerException();
		}
		Node<T> node = new Node<>(data, top);//next指向当前元素top，如果是第一个元素next指向null;
		this.top = node;	//把当前元素指向top
		size++;
	}
	//出栈
	public T pop(){
		T data = top.data;
		top = top.next;
		size--;
		return data;
	}
	//返回栈顶的元素，但不出栈
	public T peek(){
		return top.data;
	}
	// 判断链栈是否为空栈
	public boolean empty(){
		return size == 0;
	}

	public static void main(String[] args) throws Exception {
		StackByLinkedList<String> stack = new StackByLinkedList<>();
		for (int i = 0; i <= 10; i++) {
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
		if(empty()){
			return "[]";
		}else{
			StringBuilder stringBuilder = new StringBuilder("[");
			for(Node current = top;current!=null; current= current.next){
				stringBuilder.append(current.data+",");
			}
			return stringBuilder.deleteCharAt(stringBuilder.length()-1).append("]").toString();
		}
	}
	//定义节点
	 class Node<T>{
		T data;
		Node next;

		public Node(T data, Node next) {
			this.data = data;
			this.next = next;
		}
	}

}
