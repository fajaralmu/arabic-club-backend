package com.fajar.arabicclub.dto;

import java.io.Serializable;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

 
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeyValue<K, V> implements Serializable, Entry<K, V>{/**
	 * 
	 */
	private static final long serialVersionUID = -1668484384625090190L;

	private K key;
	private V value;
	@Builder.Default
	private boolean valid = true;
	private boolean multiKey;
	
	@Override
	public K getKey() {
		return key;
	}
	@Override
	public V getValue() {
		return value;
	}
	@Override
	public V setValue(V value) {
		this.value = value;
		return value;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public void setKey(K key) {
		this.key = key;
	}
	public boolean isMultiKey() {
		return multiKey;
	}
	public void setMultiKey(boolean multiKey) {
		this.multiKey = multiKey;
	}
	public KeyValue(K k, V b, boolean v) { 
		this.key = k;
		this.value = b;
		this.valid = v;
	}
	
	
	
}

