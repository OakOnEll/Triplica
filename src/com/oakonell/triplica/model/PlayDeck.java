package com.oakonell.triplica.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PlayDeck {
	private List<PlayCard> cards = new ArrayList<PlayCard>();

	public PlayDeck() {
		// create the deck, unique permutations of the shapes
		Shape[] shapesArray = Shape.values();
		int numShapes = shapesArray.length;

		// loop through all unique combinations
		for (int top = 0; top < numShapes; top++) {
			for (int middle = 0; middle < numShapes; middle++) {
				if (middle == top)
					continue;
				for (int bottom = top + 1; bottom < numShapes; bottom++) {
					if (bottom == middle)
						continue;
					cards.add(new PlayCard(shapesArray[top],
							shapesArray[middle], shapesArray[bottom]));
				}
			}
		}
	}

	public void shuffle() {
		Collections.shuffle(cards);
		// randomly flip cards as well
		Random rand = new Random();
		for (PlayCard each : cards) {
			if (rand.nextBoolean()) {
				each.flip();
			}
		}
	}

	public List<PlayCard> getCards() {
		return cards;
	}

	public PlayCard removeTopCard() {
		return cards.remove(cards.size() - 1);
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	public PlayCard getTopCard() {
		return cards.get(cards.size() - 1);
	}

}
