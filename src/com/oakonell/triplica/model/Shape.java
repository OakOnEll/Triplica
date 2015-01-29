package com.oakonell.triplica.model;

import com.oakonell.triplica.R;

public enum Shape {
	DIAMOND(R.drawable.diamond, "\u2666"), STAR(R.drawable.star,"\u2605"), ASTERISK(
			R.drawable.asterisk,"*"), OVAL(R.drawable.oval,"\u25CF"), SQUARE(
			R.drawable.rectangle, "\u25A0"), X(R.drawable.x, "X"), ;

	private int drawableResId;
	private String debug;

	private Shape(int drawableResId, String debug) {
		this.drawableResId = drawableResId;
		this.debug = debug;
	}

	public int getDrawableResoure() {
		return drawableResId;
	}

	public String getDebuggable() {
		return debug;
	}
}
