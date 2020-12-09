package com.joffzhang.java.base;

import java.io.Serializable;

public class BaseObj implements Cloneable, Serializable {
	private int id;

	public BaseObj() {
	}

	public BaseObj(Integer id) {
		this.id = id;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		//TODO Auto-generated method stub
		return super.clone();
	}

	@Override
	public String toString() {
		return "BaseObj [id=" + id + "]";
	}
}