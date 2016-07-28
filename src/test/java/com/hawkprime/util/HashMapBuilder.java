package com.hawkprime.util;

import java.util.HashMap;
import java.util.Map;

public class HashMapBuilder<K, V> {
	private Map<K, V> map;

	public HashMapBuilder() {
		map = new HashMap<K, V>();
	}

	public HashMapBuilder<K, V> put(K key, V value) {
		map.put(key, value);
		return this;
	}

	public Map<K, V> build() {
		return map;
	}
}
