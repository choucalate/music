package com.model;

import android.widget.Button;

public class ButtonMap {

	private Button b;
	private int num1, num2;
	private boolean islong;

	public ButtonMap(Button myBut, int n1, int n2) {
		b = myBut;
		num1 = n1;
		num2 = n2;
	}

	public ButtonMap(Button myBut, boolean longClick) {
		b = myBut;
		islong = longClick;
	}

	public boolean getClick() {
		return islong;
	}

	public Button getB() {
		return b;
	}

	public void setB(Button b) {
		this.b = b;
	}

	public int getNum1() {
		return num1;
	}

	public void setNum1(int num1) {
		this.num1 = num1;
	}

	public int getNum2() {
		return num2;
	}

	public void setNum2(int num2) {
		this.num2 = num2;
	}
}
