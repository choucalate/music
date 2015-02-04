package com.model;

public class TupleStringInt {
	private int x;
	private String y;

	public TupleStringInt(int myint, String myStr) {
		this.x = myint;
		this.y = myStr;
	}

	public String getStr() {
		return y;
	}

	public int getInt() {
		return x;
	}

}
