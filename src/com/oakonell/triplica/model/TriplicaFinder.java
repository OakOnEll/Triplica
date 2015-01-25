package com.oakonell.triplica.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TriplicaFinder {

	private final List<PlayCardPile> playPiles;

	public TriplicaFinder(List<PlayCardPile> playPiles) {
		this.playPiles = playPiles;
	}

	public Set<Triplica> claimTriplicas(int stackNum, Position position,
			int stackInPlay) {

		Set<Triplica> result = new HashSet<Triplica>();
		if (Math.abs(stackNum - stackInPlay) > 2) {
			return result;
		}

		switch (position) {
		case TOP:
			// check horizontals
			claimHorizontals(stackNum, position, result);
			// check diagonals
			if (stackNum >= 2) {
				claimBottomUpDiagonal(stackNum - 2, result);
			}
			if (stackNum < playPiles.size() - 2) {
				claimTopBottomDiagonal(stackNum, result);
			}
			break;
		case MIDDLE:
			// check horizontals
			claimHorizontals(stackNum, position, result);
			// check diagonals
			if (stackNum >= 1 && stackNum < playPiles.size() - 1) {
				claimBottomUpDiagonal(stackNum - 1, result);
				claimTopBottomDiagonal(stackNum - 1, result);
			}
			break;
		case BOTTOM:
			// check horizontals
			claimHorizontals(stackNum, position, result);
			// check diagonals
			if (stackNum < playPiles.size() - 2) {
				claimBottomUpDiagonal(stackNum, result);
			}
			if (stackNum >= 2) {
				claimTopBottomDiagonal(stackNum - 2, result);
			}
			break;
		}

		return result;
	}

	private void claimTopBottomDiagonal(int startStack, Set<Triplica> result) {
		PlayCardPile first = playPiles.get(startStack);
		PlayCardPile second = playPiles.get(startStack + 1);
		PlayCardPile third = playPiles.get(startStack + 2);
		if (first.getTopCard().get(Position.TOP) == second.getTopCard().get(
				Position.MIDDLE)
				&& second.getTopCard().get(Position.MIDDLE) == third
						.getTopCard().get(Position.BOTTOM)) {
			Triplica triplica = new Triplica();
			triplica.startStackNum = startStack;
			triplica.startPosition = Position.TOP;
			triplica.endStackNum = startStack + 2;
			triplica.endPosition = Position.BOTTOM;
			result.add(triplica);
		}
	}

	private void claimBottomUpDiagonal(int startStack, Set<Triplica> result) {
		PlayCardPile first = playPiles.get(startStack);
		PlayCardPile second = playPiles.get(startStack + 1);
		PlayCardPile third = playPiles.get(startStack + 2);
		if (first.getTopCard().get(Position.BOTTOM) == second.getTopCard().get(
				Position.MIDDLE)
				&& second.getTopCard().get(Position.MIDDLE) == third
						.getTopCard().get(Position.TOP)) {
			Triplica triplica = new Triplica();
			triplica.startStackNum = startStack;
			triplica.startPosition = Position.BOTTOM;
			triplica.endStackNum = startStack + 2;
			triplica.endPosition = Position.TOP;
			result.add(triplica);
		}
	}

	private void claimHorizontals(int stackNum, Position position,
			Set<Triplica> result) {
		// start with me
		if (stackNum < playPiles.size() - 2) {
			claimHorizontal(position, result, stackNum);
		}
		// I'm in middle
		if (stackNum >= 1 && stackNum < playPiles.size() - 1) {
			claimHorizontal(position, result, stackNum - 1);
		}
		// end with me
		if (stackNum >= 2) {
			claimHorizontal(position, result, stackNum - 2);
		}
	}

	private void claimHorizontal(Position position, Set<Triplica> result,
			int startStack) {
		PlayCardPile first = playPiles.get(startStack);
		PlayCardPile second = playPiles.get(startStack + 1);
		PlayCardPile third = playPiles.get(startStack + 2);
		if (first.getTopCard().get(position) == second.getTopCard().get(
				position)
				&& second.getTopCard().get(position) == third.getTopCard().get(
						position)) {
			Triplica triplica = new Triplica();
			triplica.startStackNum = startStack;
			triplica.startPosition = position;
			triplica.endStackNum = startStack + 2;
			triplica.endPosition = position;
			result.add(triplica);
		}
	}
}
