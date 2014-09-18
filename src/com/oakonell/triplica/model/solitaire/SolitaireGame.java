package com.oakonell.triplica.model.solitaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
		playDeck.shuffle();
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
		GameStatus status = new GameStatus();
		status.state = GameState.TURN_IN_PROGRESS;
		return status;
	}

	// turn variables
	private List<Triplica> claimedTriplicas = new ArrayList<Triplica>();
	int stackInPlay = -1;

	public Set<Triplica> claimTriplicas(int stackNum, Position position) {
		TriplicaFinder finder = new TriplicaFinder(playPiles);
		Set<Triplica> result = finder.claimTriplicas(stackNum, position,
				stackInPlay);
		// remove any triplicas already found
		result.removeAll(claimedTriplicas);
		// add the new ones to the claimed triplicas
		claimedTriplicas.addAll(result);
		// adjust the goals
		if (!result.isEmpty()) {
			Shape shape = playPiles.get(stackNum).getTopCard().get(position);
			Integer remaining = goalsRemainingByShape.get(shape);
			remaining = Math.max(remaining - result.size(), 0);
			goalsRemainingByShape.put(shape, remaining);
		}

		calculateStatus();
		return result;
	}

	public int getStackInPlay() {
		return stackInPlay;
	}

	public void endTurn() {
		status.state = GameState.START_TURN;
		claimedTriplicas = new ArrayList<Triplica>();
		stackInPlay = -1;
	}

	public int getGoalsRemaining(Shape each) {
		return goalsRemainingByShape.get(each);
	}

	public int getNumPlayPiles() {
		return playPiles.size();
	}

	public PlayCard getDeckCard() {
		status = new GameStatus();
		endTurn();
		return playDeck.getTopCard();
	}

	public boolean isStartOfTurn() {
		return status.state == GameState.START_TURN;
	}

	public void placePlayCard(int i) {
		stackInPlay = i;
		PlayCard card = playDeck.removeTopCard();
		playPiles.get(i).add(card);
		status = new GameStatus();
		status.state = GameState.TURN_IN_PROGRESS;
	}

	public PlayDeck getDeck() {
		return playDeck;
	}

}
