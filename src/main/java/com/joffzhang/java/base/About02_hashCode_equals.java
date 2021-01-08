package com.joffzhang.java.base;

/**
 * @author zy
 * @date 2021/1/4 12:25
 * 散列表中，hashCode()相等即两个键值对的哈希值相等，然而哈希值相等，并不一定能得出键值对相等
 */
public class About02_hashCode_equals {
	/*
	 *str1：1179395 | str2：1179395
	 *false
	 */
	public static void main(String[] args) {
		String str1 = "通话";
		String str2 = "重地";
		System.out.println(String.format("str1：%d | str2：%d", str1.hashCode(), str2.hashCode()));
		System.out.println(str1.equals(str2));
	}
}
