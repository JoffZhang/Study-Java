package com.joffzhang.java.algorithm.lru;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zy
 * @date 2020/8/12 15:23
 * LRU算法，   哈希表+双向链表
 * 时间复杂度：对于 put 和 get 都是 O(1)O(1)。
 * 空间复杂度：O(capacity)，因为哈希表和双向链表最多存储 capacity+1 个元素。
 */
public class LRUCache {
	//哈希表
	private Map<Integer,DLinkedNode> cacheMap = new HashMap<>();
	private int size;
	private int capacity;
	private DLinkedNode head,tail;

	public LRUCache(int capacity) {
		this.capacity = capacity;
		this.size = 0;
		// 使用伪头部和伪尾部节点
		head = new DLinkedNode();
		tail = new DLinkedNode();
		head.next = tail;
		tail.prev = head;
	}
	//获取，当key存在，先通过哈希表定位，再移动到头部
	public int get(int key){
		//1.使用哈希表定位
		DLinkedNode node = cacheMap.get(key);
		if(node == null){ return -1;}
		//2.如果存在key，移动node
		moveToHead(node);
		return node.value;
	}
	//添加哈希表、双向链表，容量判断，是否删除
	public void put(int key ,int value){
		//1.使用哈希表定位
		DLinkedNode node = cacheMap.get(key);
		//2.1如果node不存在
		if(node == null){
			//2.1.1创建新node
			DLinkedNode newNode = new DLinkedNode(key, value);
			//2.1.2添加进哈希表
			cacheMap.put(key,newNode);
			//2.1.3添加至双向链表的头部
			addToHead(newNode);
			++size;
			//2.1.4添加新node，判断容量，超出做删除
			if(size > capacity){
				//容量超出，删除双向链表的尾部节点
				DLinkedNode tail = removeTail();
				//删除哈希表
				cacheMap.remove(tail.key);
				--size;
			}
		}
		//2.2 如果key存在，则修改,并移动到头部
		else{
			node.value = value;
			moveToHead(node);
		}
	}

	private DLinkedNode removeTail() {
		DLinkedNode res = tail.prev;
		removeNode(res);
		return res;
	}

	private void moveToHead(DLinkedNode node) {
		removeNode(node);
		addToHead(node);
	}

	private void addToHead(DLinkedNode node) {
		node.prev = head;
		node.next = head.next;
		head.next.prev = node;
		head.next = node;
	}

	private void removeNode(DLinkedNode node) {
		node.prev.next = node.next;
		node.next.prev = node.prev;
	}

	//链表节点
	class DLinkedNode{
		public int key,value;
		public DLinkedNode prev,next;
		public DLinkedNode() {
		}

		public DLinkedNode(int key, int value) {
			this.key = key;
			this.value = value;
		}
	}

	public static void main(String[] args) {
		LRUCache lruCache = new LRUCache(7);
		for (int i = 1; i < 10; i++) {
			lruCache.put(i,i);
			if(i == 7){
				lruCache.get(2);
				lruCache.get(1);
			}
		}
		print(lruCache.head,lruCache.tail);
	}

	private static void print(DLinkedNode head,DLinkedNode tail) {
		DLinkedNode next = head.next;
		if(next != tail){
			System.out.println("key="+next.key+",value="+next.value);
			print(next,tail);
		}
	}


}


