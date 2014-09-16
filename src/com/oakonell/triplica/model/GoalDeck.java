package com.oakonell.triplica.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GoalDeck {
	private static final int NUM_GOAL_OF_EACH_SHAPE = 6;

	private List<GoalCard> cards = new ArrayList<GoalCard>();

	public GoalDeck() {
		// create the deck- simply NUM_GOAL_OF_EACH_SHAPE of each shape
		for (Shape each : Shape.values()) {
			for (int i = 0; i < NUM_GOAL_OF_EACH_SHAPE; i++) {
				cards.add(new GoalCard(each));
			}
		}
	}

	public void shuffle() {
		Collections.shuffle(cards);
	}

	public List<GoalCard> getCards() {
		return cards;
	}

	public GoalCard removeTopCard() {
		return cards.remove(cards.size() - 1);
	}
}
