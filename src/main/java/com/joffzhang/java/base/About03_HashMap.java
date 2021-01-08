package com.joffzhang.java.base;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zy
 * @date 2021/1/4 14:40
 */
public class About03_HashMap {

	public static void main(String[] args) {

		Map<Object,Object>  map = new HashMap<>();

		for (int i = 0; i < 100; i++) {
			map.put(i,i);
		}

	}
}
