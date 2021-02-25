package com.bithack.apparatus.ui;

public abstract class Widget implements IWidget {
	WidgetValueCallback callback;
	boolean disabled = false;
	int height;
	public int id = 0;
	int width;
	public int x;
	public int y;
}
