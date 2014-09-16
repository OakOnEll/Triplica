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

	public void add(PlayCard card) {
		cards.add(card);
	}
}
