package com.joffzhang.java.algorithm.lfu;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zy
 * @date 2020/8/17 16:00
 */
public class LFUCache3 {

	int size,capacity;
	//存储缓存的内容Node
	Map<Integer ,Node> cache;
	NodeDLinkedList headFreqLinkedList;	//headFreqLinkedList.post 频次最大的双向链表
	NodeDLinkedList tailFreqLinkedList;	//tailFreqLinkedList.pre 频次最小的双向链表

	public LFUCache3(int capacity){
		this.capacity = capacity;
		this.cache = new HashMap<>();
		this.headFreqLinkedList = new NodeDLinkedList();
		this.tailFreqLinkedList = new NodeDLinkedList();
		headFreqLinkedList.post = tailFreqLinkedList;
		tailFreqLinkedList.pre = headFreqLinkedList;
	}

	public int get(int key){
		Node node = cache.get(key);
		if(node == null){
			return -1;
		}
		//该key访问频次+1
		freqInc(node);
		return node.value;
	}

	public void put(int key, int value){
		if(capacity == 0) { return ;}
		Node node = cache.get(key);
		if(node != null){
			//key存在，则更新value，访问频次+1
			node.value = value;
			freqInc(node);
		}else{
			//key不存在
			if(size == capacity){
				//如果缓存满了，删除tailFreqLinkedList.pre这个链表(即表示最小频次的链表)中的tail.prev
				cache.remove(tailFreqLinkedList.pre.tail.prev.key);
				tailFreqLinkedList.removeNode(tailFreqLinkedList.pre.tail.prev);
				size--;
				if(tailFreqLinkedList.pre.head.next == tailFreqLinkedList.pre.tail){
					removeDLinkedList(tailFreqLinkedList.pre);
				}
			}

			// cache中put新Key-Node对儿，并将新node加入表示freq为1的DoublyLinkedList中，若不存在freq为1的DoublyLinkedList则新建。
			Node newNode = new Node(key, value);
			cache.put(key, newNode);
			if(tailFreqLinkedList.pre.frequency != 1){
				NodeDLinkedList newNodeDLinkedList = new NodeDLinkedList(1);
				addDLinKedList(newNodeDLinkedList,tailFreqLinkedList.pre);
				newNodeDLinkedList.addNode(newNode);
			}else{
				headFreqLinkedList.pre.addNode(newNode);
			}
			size++;
		}
	}
	/**
	 * node的访问频次 + 1
	 */
	private void freqInc(Node node) {
		// 将node从原freq对应的双向链表里移除, 如果链表空了则删除链表。
		NodeDLinkedList currentFreqDLinedList = node.currentFreqDLinedList;
		NodeDLinkedList preDLinkedList = currentFreqDLinedList.pre;
		//删除node
		currentFreqDLinedList.removeNode(node);
		//删除空链表
		if(currentFreqDLinedList.head.next == currentFreqDLinedList.tail){
			removeDLinkedList(currentFreqDLinedList);
		}
		//将node加入新freq对应的双向链表，若不存在，则先创建该链表
		node.frequency++;
		if(preDLinkedList.frequency != node.frequency){
			NodeDLinkedList newFreqDLinkedList = new NodeDLinkedList(node.frequency);
			addDLinKedList(newFreqDLinkedList,preDLinkedList);
			newFreqDLinkedList.addNode(node);
		}else{
			preDLinkedList.addNode(node);
		}
	}

	//左（频次大）+右（频次小）
	//增加代表1频次的双向链表
	private void addDLinKedList(NodeDLinkedList newFreqDLinkedList, NodeDLinkedList preDLinkedList) {
		newFreqDLinkedList.post = preDLinkedList.post;
		preDLinkedList.post.pre = newFreqDLinkedList;
		newFreqDLinkedList.pre = preDLinkedList;
		preDLinkedList.post = newFreqDLinkedList;
	}

	//删除代表某1频次的双向链表
	private void removeDLinkedList(NodeDLinkedList dLinkedList) {
		dLinkedList.pre.post = dLinkedList.post;
		dLinkedList.post.pre = dLinkedList.pre;
	}

	private class Node{
		int key,value,frequency=0;
		Node prev,next;
		//node所在频次的双向链表
		NodeDLinkedList currentFreqDLinedList;
		public Node(int key, int value) {
			this.key = key;
			this.value = value;
		}
	}
	//添加节点从头，删除从尾
	private class NodeDLinkedList{
		Node head,tail;//该双向链表的头尾节点
		int frequency; //该双向链表表示的频次
		NodeDLinkedList pre;	//该双向链表的前继链表 (pre.freq < this.freq)
		NodeDLinkedList post;	//该双向链表的后继链表 (post.freq > this.freq)

		public NodeDLinkedList() {
		}

		public NodeDLinkedList(int frequency) {
			this.frequency = frequency;
			head = new Node(-1,-1);
			tail = new Node(-1,-1);
			head.next = tail;
			tail.prev = head;
		}

		public void removeNode(Node node) {
			//删除当前频次链表中的节点
			node.prev.next = node.next;
			node.next.prev = node.prev;
		}

		public void addNode(Node node) {
			node.next = head.next;
			head.next.prev = node;
			head.next = node;
			node.prev = head;
			node.currentFreqDLinedList = this;
		}
	}
}
