package com.oakonell.triplica.model;

import com.oakonell.triplica.R;

public enum Shape {
	DIAMOND(R.drawable.diamond), STAR(R.drawable.star), ASTERISK(
			R.drawable.asterisk), OVAL(R.drawable.oval), SQUARE(
			R.drawable.rectangle), X(R.drawable.x), ;

	private int drawableResId;

	private Shape(int drawableResId) {
		this.drawableResId = drawableResId;
	}
	
	public int getDrawableResoure() {
		return drawableResId;
	}
}
