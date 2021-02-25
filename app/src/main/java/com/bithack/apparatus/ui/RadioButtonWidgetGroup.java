package com.bithack.apparatus.ui;

import java.util.ArrayList;
import java.util.Iterator;

public class RadioButtonWidgetGroup {
	ArrayList<RadioWidget> widgets = new ArrayList<>();

	public int add_button(RadioWidget b) {
		this.widgets.add(b);
		return this.widgets.size() - 1;
	}

	/* access modifiers changed from: protected */
	public void click(int n) {
		Iterator<RadioWidget> it = this.widgets.iterator();
		while (it.hasNext()) {
			it.next().set_checked(false);
		}
		this.widgets.get(n).set_checked(true);
	}
}
