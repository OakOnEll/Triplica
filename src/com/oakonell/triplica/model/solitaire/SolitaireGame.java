package com.oakonell.triplica.model.solitaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import com.oakonell.triplica.model.PlayCard;
import com.oakonell.triplica.model.PlayCardPile;
import com.oakonell.triplica.model.PlayDeck;
import com.oakonell.triplica.model.Position;
import com.oakonell.triplica.model.Shape;
import com.oakonell.triplica.model.Triplica;
import com.oakonell.triplica.model.TriplicaFinder;

public class SolitaireGame {
	private static final int NUM_PLAY_CARD_PILES = 4;
	private static final int NUM_INITIAL_GOALS = 6;

	private final PlayDeck playDeck = new PlayDeck();
	private final List<PlayCardPile> playPiles = new ArrayList<PlayCardPile>(
			NUM_PLAY_CARD_PILES);
	private final Map<Shape, Integer> goalsRemainingByShape = new HashMap<Shape, Integer>();

	// turn variables
	private List<Triplica> pendingTriplicas = new ArrayList<Triplica>();
	private int stackInPlayIndex = -1;

	public SolitaireGame() {
		for (Shape each : Shape.values()) {
			goalsRemainingByShape.put(each, NUM_INITIAL_GOALS);
		}
		for (int i = 0; i < NUM_PLAY_CARD_PILES; i++) {
			playPiles.add(new PlayCardPile());
		}
		status = new GameStatus();
		status.state = GameState.START_TURN;
	}

	public void setupBoard() {
		Random random = new Random();
		setupBoard(random);
	}

	/** for testing */
	public void setupBoard(Random random) {
		playDeck.shuffle(random);
		for (PlayCardPile each : playPiles) {
			each.add(playDeck.removeTopCard());
		}
	}

	public PlayCardPile getPlayPile(int stackNum) {
		if (stackNum < 0 || stackNum >= playPiles.size()) {
			throw new IllegalArgumentException(
					"Argument stackNum must be between 0 and "
							+ (playPiles.size() - 1) + " inclusive.");
		}
		return playPiles.get(stackNum);
	}

	public enum GameState {
		WON, LOST, START_TURN, TURN_IN_PROGRESS, ;
	}

	public static class GameStatus {
		public GameState state;
		public int score;
	}

	private GameStatus status;

	public GameStatus getGameStatus() {
		return status;
	}

	private GameStatus calculateStatus() {
		int numGoalsRemaining = 0;
		for (Entry<Shape, Integer> entry : goalsRemainingByShape.entrySet()) {
			numGoalsRemaining += entry.getValue();
		}

		int numPlayCardsRemaining = playDeck.getCards().size();

		if (numGoalsRemaining == 0) {
			status = new GameStatus();
			status.state = GameState.WON;
			status.score = numPlayCardsRemaining;
			return status;
		}

		if (numPlayCardsRemaining == 0) {
			status = new GameStatus();
			status.state = GameState.LOST;
			status.score = 0;
			return status;
		}
		return null;
	}

	public Set<Triplica> markTriplicas(int stackNum, Position position) {
		TriplicaFinder finder = new TriplicaFinder(playPiles);
		Set<Triplica> result = finder.claimTriplicas(stackNum, position,
				stackInPlayIndex);
		// remove any triplicas already found
		result.removeAll(pendingTriplicas);
		// add the new ones to the claimed triplicas
		pendingTriplicas.addAll(result);

		return result;
	}

	public int getStackInPlayIndex() {
		return stackInPlayIndex;
	}

	public void endTurn() {
		// adjust the goals
		if (!pendingTriplicas.isEmpty()) {
			for (Triplica each : pendingTriplicas) {
				Shape shape = playPiles.get(each.startStackNum).getTopCard()
						.get(each.startPosition);
				Integer remaining = goalsRemainingByShape.get(shape);
				remaining = Math.max(remaining - 1, 0);
				goalsRemainingByShape.put(shape, remaining);
			}
		}
		status = calculateStatus();

		if (status == null) {
			status = new GameStatus();
			status.state = GameState.START_TURN;
		}
		pendingTriplicas = new ArrayList<Triplica>();
		stackInPlayIndex = -1;
	}

	public int getGoalsRemaining(Shape each) {
		return goalsRemainingByShape.get(each);
	}

	public int getNumPlayPiles() {
		return playPiles.size();
	}

	public PlayCard getDeckCard() {
		return playDeck.getTopCard();
	}

	public boolean isStartOfTurn() {
		return status.state == GameState.START_TURN;
	}

	public void placePlayCard(int i) {
		if (stackInPlayIndex < 0) {
			if (status != null && status.state != GameState.START_TURN) {
				throw new IllegalStateException(
						"Game is in invalid state to play a card from the deck");
			}
			stackInPlayIndex = i;
			PlayCard card = playDeck.removeTopCard();
			playPiles.get(i).add(card);
			status = new GameStatus();
			status.state = GameState.TURN_IN_PROGRESS;
		} else {
			if (status == null || status.state != GameState.TURN_IN_PROGRESS) {
				throw new IllegalStateException(
						"Game is in invalid state to move a card in play");
			}
			pendingTriplicas = new ArrayList<Triplica>();
			PlayCardPile stack = playPiles.get(stackInPlayIndex);
			PlayCard card = stack.removeTopCard();
			playPiles.get(i).add(card);
			stackInPlayIndex = i;
		}
	}

	public PlayDeck getDeck() {
		return playDeck;
	}

	public List<Triplica> getPendingTriplicas() {
		return pendingTriplicas;
	}

	/*
	 * X?* (56) ?(6)(6) ??? ?(6) X?* ?(6) ?X? ?(6) X?? X(6)
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		Iterator<Entry<Shape, Integer>> goalIter = goalsRemainingByShape
				.entrySet().iterator();

		if (playDeck.isEmpty()) {
			builder.append("   ");
		} else {
			if (stackInPlayIndex < 0) {
				builder.append(getDeckCard().toString());
			} else {
				builder.append("===");
			}
		}

		String deckSizeString = " (" + playDeck.getCards().size() + ")";
		builder.append(deckSizeString);
		outputGoal(goalIter.next(), builder);
		builder.append("   ");
		for (int i = 0; i < deckSizeString.length(); i++) {
			builder.append(' ');
		}
		outputGoal(goalIter.next(), builder);

		for (int index = 0; index < getNumPlayPiles(); index++) {
			PlayCardPile each = playPiles.get(index);
			builder.append(each.getTopCard().toString());
			if (index == stackInPlayIndex) {
				builder.append("  ");
				builder.append(each.getNextCard().toString());
			} else {
				builder.append("     ");
			}
			outputGoal(goalIter.next(), builder);
		}

		return builder.toString();
	}

	private void outputGoal(Entry<Shape, Integer> next, StringBuilder builder) {
		builder.append("    " + next.getKey().getDebuggable() + "("
				+ next.getValue() + ")\n");

	}
}
