package com.oakonell.triplica.model;

public class PlayCard {
	private final Shape[] shapes = new Shape[3];
	private boolean flipped;

	public PlayCard(Shape top, Shape middle, Shape bottom) {
		shapes[0] = top;
		shapes[1] = middle;
		shapes[2] = bottom;
		flipped = false;
	}

	public void flip() {
		flipped = !flipped;
	}

	public Shape get(Position pos) {
		if (pos == Position.MIDDLE)
			return shapes[1];
		if (flipped) {
			if (pos == Position.TOP)
				return shapes[2];
			return shapes[0];
		}
		if (pos == Position.TOP)
			return shapes[0];
		return shapes[2];
	}
	
	public String toString() {
		return get(Position.TOP)  + "," + get(Position.MIDDLE) + "," + get(Position.BOTTOM);
	}
}
