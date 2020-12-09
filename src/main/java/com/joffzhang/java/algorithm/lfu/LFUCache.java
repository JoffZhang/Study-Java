package com.joffzhang.java.algorithm.lfu;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zy
 * @date 2020/8/17 11:06
 */
public class LFUCache {

	//缓存容量
	int capacity;
	//双向链表
	DLinkedNode link = new DLinkedNode();
	//key到node的映射
	Map<Integer ,Node> map = new HashMap<>();
	//频率到尾节点的映射
	Map<Integer,Node> tail = new HashMap<>();
	int nowsize = 0;

	public LFUCache(int capacity) {
		this.capacity = capacity;
		link.head.frequency = 0;
		link.tail.frequency = Integer.MAX_VALUE;
		tail.put(link.head.frequency ,link.head);
		tail.put(link.tail.frequency ,link.tail);
	}
	
	public int get(int key){
		Node node = map.get(key);
		if(node == null){ return -1;}
		active(node);//命中node，激活
		return node.value;
	}

	private void active(Node node) {
		int frequency = node.frequency;
		node.frequency++;
		Node prev = node.prev;
		Node master = tail.get(frequency);//当前频率的最大
		Node masterNext = master.next;	//当前最大的下一个
		if(node == master){
			if(prev.frequency == frequency){//最大之后加入节点
				tail.put(frequency,prev);
			}else{
				tail.remove(frequency);
			}
			if(masterNext.frequency == frequency+1){//下一组频率相邻
				link.remove(node);
				link.insertAter(tail.get(frequency+1),node);
				tail.put(frequency+1,node);
			}else{//下一组频率不相邻，链表结构不改变
				tail.put(frequency+1,node);
			}
		}
		//如果节点不是最大
		else{
			if(masterNext.frequency == frequency +1) {//下一组频率相邻
				link.remove(node);
				link.insertAter(masterNext,node);
				tail.put(frequency+1,node);
			}else{//不相邻
				link.remove(node);
				link.insertAter(master,node);
				tail.put(frequency+1,node);
			}
		}
	}

	public void put(int key,int value){
		if(capacity ==0){ return ;}
		Node node = map.get(key);
		if(node == null){
			if(nowsize >= capacity){//超容，移除
				removeLFU();
				nowsize--;
			}
			Node newNode = new Node(key, value);
			newNode.frequency = 1;
			Node oneMaster = tail.get(1);//使用频率为1 的group
			if(oneMaster == null){
				link.insertAter(link.head,newNode);
			}else{
				link.insertAter(oneMaster,newNode);
			}
			nowsize++;
			tail.put(1,newNode);
			map.put(key,newNode);
		}else{
			active(node);
			node.value = value;
		}
	}
	//移除最近最少使用的节点
	private void removeLFU() {
		Node node = link.head.next;
		Node next = node.next;
		link.remove(node);
		map.remove(node.key);
		if(node.frequency != next.frequency){
			tail.remove(node.frequency);
		}
	}

	public static void main(String[] args) {
		LFUCache cache = new LFUCache(3 /* capacity (缓存容量) */);
		String[] ops = {"put", "put", "put", "put", "get"};
		int[][] values = {{1, 1}, {2, 2}, {3, 3}, {4, 4}, {4}};
		for (int i = 0; i < ops.length; i++) {
			System.out.println(ops[i] + " " + values[i][0]);
			if (ops[i].equals("put")) {
				cache.put(values[i][0], values[i][1]);
			} else {
				int res = cache.get(values[i][0]);
				System.out.println(res);
			}
			cache.debug();
		}
	}
	void debug() {
		System.out.println(link.toString());
		System.out.println(tos(tail));
		System.out.println(tos(map));
		System.out.println("========");
	}

	String tos(Map<Integer, Node> ma) {
		StringBuilder builder = new StringBuilder();
		for (int i : ma.keySet()) {
			builder.append(i + ":" + ma.get(i) + "  ");
		}
		return builder.toString();
	}
	//定义双向链表节点
	class Node{
		Node prev,next;
		int key,value;
		int frequency;

		public Node(int key, int value) {
			this.key = key;
			this.value = value;
		}

		@Override
		public String toString() {
			return "Node{" +
					"key=" + key +
					", value=" + value +
					'}';
		}
	}
	//定义双向链表
	class DLinkedNode{
		Node head,tail;

		public DLinkedNode() {
			head = new Node(0,0);
			tail = new Node(0,0);
			head.next  = tail;
			tail.prev = head;
		}
		//移除双向链表中的节点
		void remove(Node node){
			Node prev = node.prev;
			Node next = node.next;
			prev.next = next;
			next.prev = prev;
		}
		//在节点之后插入新节点
		void insertAter(Node who,Node newNode){
			Node next = who.next;
			who.next = newNode;
			newNode.prev = who;
			newNode.next = next;
			next.prev = newNode;
		}

		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			for (Node i = head.next; i != tail; i = i.next) {
				builder.append(String.format("(%d:%d,%d)->", i.key, i.value, i.frequency));
			}
			return builder.toString();
		}
	}
}

