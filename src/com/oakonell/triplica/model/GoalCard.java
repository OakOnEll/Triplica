package com.oakonell.triplica.model;

public class GoalCard {
	private final Shape shape;

	public GoalCard(Shape shape) {
		this.shape = shape;
	}

	public Shape getShape() {
		return shape;
	}

	public String toString() {
		return "GoalCard: " + shape;
	}
}
