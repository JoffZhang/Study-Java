package com.joffzhang.java.algorithm.lru;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author zy
 * @date 2020/8/12 11:38
 * 基于LinkedHashMap实现LRU算法
 * LinkedHashMap  内部维护了一个双向链表，用来维护插入顺序或者LRU顺序
 * 	final boolean accessOrder; accessOrder决定了维护的是哪一种顺序，默认为 false，此时维护的是插入顺序。为true时，维护的就是LRU顺序了。
 * 	当一个节点被访问时，如果 accessOrder 为 true，则会将该节点移到链表尾部
 *	在 put 等操作之后执行 afterNodeInsertion，当 removeEldestEntry() 方法返回 true 时会移除最老的节点，也就是链表首部节点 first。
 *	removeEldestEntry() 默认返回 false，如果需要让它为 true，需要继承 LinkedHashMap 并且重写这个方法
 */
public class LRUCacheByLinkedHashMap<K,V>  extends LinkedHashMap<K,V> {

	private final int CACHE_SIZE;

	/**
	 * 传递进来最多能缓存多少数据
	 * @param cacheSize	缓存大小
	 */
	public LRUCacheByLinkedHashMap(int cacheSize){
		//true标识让LikedHashMap按照访问顺序来进行排序，最近访问放在头部，最老访问放在尾部
		super((int)Math.ceil(cacheSize/0.75) + 1,0.75f, true);
		this.CACHE_SIZE = cacheSize;
	}
	//最后覆盖removeEldestEntry(）方法实现，在节点多于 MAX_ENTRIES 就会将最近最少使用的数据移除。
	//因为这个函数默认返回false，不重写的话缓存爆了的时候无法删除最近最久未使用的节点
	//钩子方法，通过put新增键值对的时候，若该方法返回true便移除该map中最老的键和值
	@Override
	protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
		//当map中的数据量大于指定的缓存个数的时候
		return size() > CACHE_SIZE;
	}

	public static void main(String[] args) {
		LRUCacheByLinkedHashMap<String, String> stringStringLRUCacheByLinkedHashMap = new LRUCacheByLinkedHashMap<>(10);
		for (int i = 0; i < 15; i++) {
			stringStringLRUCacheByLinkedHashMap.put("key"+i,"value"+i);
			if(i== 9){
				stringStringLRUCacheByLinkedHashMap.get("key0");
				stringStringLRUCacheByLinkedHashMap.get("key1");
				stringStringLRUCacheByLinkedHashMap.get("key4");
			}
		}

		stringStringLRUCacheByLinkedHashMap.forEach((s, s2) -> System.out.println("key="+s+"value="+s2));
	}
}
