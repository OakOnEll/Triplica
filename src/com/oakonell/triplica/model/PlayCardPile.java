package com.oakonell.triplica.model;

import java.util.ArrayList;
import java.util.List;

public class PlayCardPile {
	private List<PlayCard> cards = new ArrayList<PlayCard>();

	public PlayCard getTopCard() {
		if (cards.isEmpty()) {
			return null;
		}
		return cards.get(cards.size() - 1);
	}

	public boolean isEmpty() {
		return cards.isEmpty();
	}

	public void add(PlayCard card) {
		cards.add(card);
	}

	public PlayCard removeTopCard() {
		if (cards.isEmpty())
			return null;
		return cards.remove(cards.size() - 1);
	}

	public PlayCard getNextCard() {
		if (cards.size() < 2) {
			return null;
		}
		return cards.get(cards.size() - 2);

	}
}
