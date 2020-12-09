package com.joffzhang.java.algorithm.consistenthash;

import java.util.*;

/**
 * @author zy
 * @date 2020/8/11 15:16
 * 一致性hash
 * 一致性哈希是分布式系统组件负载均衡的首选算法，它既可以在客户端实现，也可以在中间件上实现。其应用有：
 *
 * 分布式散列表(DHT)的设计；
 * 分布式关系数据库(MySQL)：分库分表时，计算数据与节点的映射关系；
 * 分布式缓存：Memcached 的客户端实现了一致性哈希，还可以使用中间件 twemproxy 管理 redis/memcache 集群；
 * RPC 框架 Dubbo：用来选择服务提供者；
 * 亚马逊的云存储系统 Dynamo；
 * 分布式 Web 缓存；
 * Bittorrent DHT；
 * LVS。
 */
public class ConsistentHashing {

	public static void main(String[] args) {
		String[] serverNodes = new String[]{"192.168.0.1","192.168.0.2","192.168.0.3","192.168.0.4"};
		//当VIRTUAL_NODE_NUM为1的时候，相当于是无虚拟节点
		ConsistentHashingVirtualNode consistentHashingVirtualNode = new ConsistentHashingVirtualNode(serverNodes, 1);
		dumpObjectNodeMap(consistentHashingVirtualNode,"无虚拟节点",0,65536);

		consistentHashingVirtualNode = new ConsistentHashingVirtualNode(serverNodes, 10);
		dumpObjectNodeMap(consistentHashingVirtualNode,"虚拟节点10",0,65536);

		consistentHashingVirtualNode = new ConsistentHashingVirtualNode(serverNodes, 500);
		dumpObjectNodeMap(consistentHashingVirtualNode,"虚拟节点500",0,65536);

		consistentHashingVirtualNode.removeRealNode("192.168.0.3");
		dumpObjectNodeMap(consistentHashingVirtualNode,"虚拟节点500，删除物理节点",0,65536);

		consistentHashingVirtualNode.addRealNode("192.168.0.3");
		dumpObjectNodeMap(consistentHashingVirtualNode,"虚拟节点500，添加物理节点",0,65536);



	}

	//统计对象和节点的映射关系
	public static void dumpObjectNodeMap(ConsistentHashingVirtualNode consistentHashingVirtualNode,String label,int objectMin,int objectMax){
		Map<String ,Integer> nodeMap = new HashMap<>();
		//统计
		for(Integer i = objectMin; i < objectMax ; i++){
			String serverNode = consistentHashingVirtualNode.getServerNode(i.toString());
			Integer count = nodeMap.get(serverNode);
			nodeMap.put(serverNode,count == null ? 1 : ++count);
		}
		//打印
		System.out.println("========" + label + "========");
		double totalCount = objectMax-objectMin + 1;
		nodeMap.forEach((s, c) ->{
			double d = (100* c / totalCount);
			System.out.println("IP=" + s + ": RATE=(" + d + ")%");
		});
	}

}

/****************************************************************
 *2.使用一种数据结构来保存hash环，可以采用的方案有很多种，最简单的是采用数组或链表。但这样查找的时候需要进行排序，如果节点数量多，速度就可能变得很慢。
 * 针对集群负载均衡状态读多写少的状态，很容易联想到使用二叉平衡树的结构去储存，实际上可以使用TreeMap（内部实现是红黑树）来作为Hash环的储存结构。
 ****************************************************************/

/***3。HASH环     ****/
class ConsistentHashingVirtualNode {
	//物理节点 真实集群地址			使用链表或TreeSet
	private Set<String> realNodes = new TreeSet<>();
	//虚拟节点映射关系		用于保存Hash环上的节点  哈希值 => 物理节点
	private SortedMap<Long, String> virtualNodes = new TreeMap<>();
	//虚拟节点	物理节点至虚拟节点的复制倍数
	private int VIRTUAL_NODE_NUM = 1;

	public ConsistentHashingVirtualNode(String[] serverNodes, int VIRTUAL_NODE_NUM) {
		realNodes.addAll(Arrays.asList(serverNodes));
		this.VIRTUAL_NODE_NUM = VIRTUAL_NODE_NUM;
		init();
	}

	//查找对象映射的节点
	public String getServerNode(String widgetKey) {
		long hash = HashUtil.getHashByFNV(widgetKey);
		//只取出所有大于该HASH值的部分而不必遍历整个Tree
		SortedMap<Long, String> subMap = virtualNodes.tailMap(hash);
		String virtualNodeName ;
		if(subMap == null || subMap.isEmpty()){
			//hash值在最尾部，应该映射到第一个节点上
			virtualNodeName = virtualNodes.get(virtualNodes.firstKey());
		}else{
			virtualNodeName = subMap.get(subMap.firstKey());
		}
		//取出的就是真实ip
		return virtualNodeName;
		
	}


	//根据物理节点，构建虚拟节点映射表
	public void init() {
		//物理节点构造传入已初始化，构建虚拟节点
		for (String realNode : realNodes) {
			addVirtualNode(realNode);
		}
	}

	public void addRealNode(String nodeIp){
		realNodes.add(nodeIp);
		addVirtualNode(nodeIp);
	}

	public void removeRealNode(String nodeIp){
		realNodes.remove(nodeIp);
		removeVirtualNode(nodeIp);
	}

	private void removeVirtualNode(String nodeIp) {
		for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
			long hash = HashUtil.getHashByFNV(getVirtualNodeName(nodeIp, i));
			virtualNodes.remove(hash);
		}
	}

	private void addVirtualNode(String nodeIp) {
		for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
			long hash = HashUtil.getHashByFNV(getVirtualNodeName(nodeIp, i));
			//可以将虚拟节点名称存到value，也可将真实ip存到value。
			virtualNodes.put(hash, nodeIp);
		}
	}

	private String getVirtualNodeName(String nodeIp, int num) {
		return nodeIp + "&&VN" + num;
	}
}



/****************************************************************
 * 1.对输入进行均匀散列的HASH算法
 ****************************************************************/
class HashUtil {
	/**
	 * 计算Hash值, 使用FNV1_32_HASH算法
	 * 对输入进行均匀散列的Hash算法，可供选择的有很多，memcached官方使用了基于md5的KETAMA算法，但这里处于计算效率的考虑，使用了FNV1_32_HASH算法
	 *
	 * @param str
	 * @return
	 */
	public static Long getHashByFNV(String str) {
		final int p = 16777619;
		Long hash = 2166136261L;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash ^ str.charAt(i)) * p;
		}
		hash += hash << 13;
		hash ^= hash >> 7;
		hash += hash << 3;
		hash ^= hash >> 17;
		hash += hash << 5;
		if (hash < 0) {
			hash = Math.abs(hash);
		}
		return hash;
	}
}