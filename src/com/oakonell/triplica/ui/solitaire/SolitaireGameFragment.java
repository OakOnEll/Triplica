package com.oakonell.triplica.ui.solitaire;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.easyandroidanimations.library.FlipHorizontalToAnimation;
import com.oakonell.triplica.R;
import com.oakonell.triplica.model.GoalCard;
import com.oakonell.triplica.model.PlayCard;
import com.oakonell.triplica.model.Position;
import com.oakonell.triplica.model.Shape;
import com.oakonell.triplica.model.Triplica;
import com.oakonell.triplica.model.solitaire.SolitaireGame;
import com.oakonell.triplica.ui.GoalCardView;
import com.oakonell.triplica.ui.PlayCardView;
import com.oakonell.triplica.ui.PlayCardView.OnPlayCardClickListener;

public class SolitaireGameFragment extends Fragment {
	private static final int FLIP_CARD_DURATION_MS = 400;
	// game model
	private SolitaireGame game;

	// Views
	public class ViewHolder {
		public Map<Shape, GoalViews> goalCardViews;
		// deck related views
		public PlayCardView playDeckView;
		public PlayCardView deck;
		public PlayCardView deck_background;
		// play card views
		public PlayCardView[] playCardViews;
		// views used for animating
		public List<ImageView> shapeViewsToAnimate;
		public TextView goalViewToAnimate;

		public PlayCardView[] underPiles;
		public SolitairePlayCardLinearLayout cards_layout;
	}

	private ViewHolder views;

	class GoalViews {
		GoalCardView cardView;
		TextView remainingView;

		GoalViews(View view, int cardId, int remainingId) {
			cardView = (GoalCardView) view.findViewById(cardId);
			remainingView = (TextView) view.findViewById(remainingId);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.solitaire_game_fragment,
				container, false);

		findViews(rootView);

		// TODO a new game- handle loading an existing one
		game = new SolitaireGame();
		game.setupBoard();

		// set state from game
		updateGoalViews();
		views.playDeckView.showBack(true);
		views.deck.showBack(true);
		views.deck_background.showBack(true);
		// views.playDeckView.bringToFront();
		// rootView.findViewById(R.id.deck_holder).bringToFront();

		int numPlayCards = game.getNumPlayPiles();
		for (int i = 0; i < numPlayCards; i++) {
			PlayCardView playCardView = views.playCardViews[i];
			configurePlayCardPile(playCardView, i);
		}

		updatePlayCards();

		views.deck.setOnClickPlayCardListener(new OnPlayCardClickListener() {
			@Override
			public void startDrag(PlayCardView playCardView) {
			}

			@Override
			public void rotated(View view) {
			}

			@Override
			public void onClickFront(View view, Position position) {
			}

			@Override
			public void onClickBack(View view) {
				// mark the current play card back to normal
				int stackInPlay = game.getStackInPlayIndex();
				if (stackInPlay >= 0) {
					views.playCardViews[stackInPlay].setSelected(false);
				}

				views.cards_layout.setIndexOnTop(0);

				PlayCard deckCard = game.getDeckCard();
				if (game.getDeck().isEmpty()) {
					views.deck_background.setVisibility(View.INVISIBLE);
				}
				views.playDeckView.setVisibility(View.INVISIBLE);
				views.playDeckView.setPlayCard(deckCard);
				views.playDeckView.showBack(false);

				new FlipHorizontalToAnimation(views.deck)
						.setFlipToView(views.playDeckView)
						.setInterpolator(new LinearInterpolator())
						.setDuration(FLIP_CARD_DURATION_MS)
						// .setListener(new
						// com.easyandroidanimations.library.AnimationListener()
						// {
						//
						// @Override
						// public void onAnimationEnd(
						// com.easyandroidanimations.library.Animation
						// animation) {
						//
						// }
						// })
						.animate();

				TriplicaClaimAnimation animator = new TriplicaClaimAnimation(
						getView(), views, game);

				// TODO need an "animating variable" to prevent inputs
				// while
				// animating
				animator.animateTriplicas(new Runnable() {
					@Override
					public void run() {
						game.endTurn();
						views.playDeckView.setDraggable(true);
					}
				});

			}

			@Override
			public void droppedOn(PlayCardView theSelected, int theIndex) {
			}

			@Override
			public void cancelDrag() {
				// TODO Auto-generated method stub

			}
		});

		views.playDeckView.setDroppables(views.playCardViews);
		views.playDeckView
				.setOnClickPlayCardListener(new OnPlayCardClickListener() {
					@Override
					public void onClickFront(View view, Position position) {
						// do nothing
					}

					@Override
					public void startDrag(PlayCardView playCardView) {
						// do nothing
					}

					@Override
					public void droppedOn(PlayCardView theSelected,
							int theSelectIndex) {
						game.placePlayCard(theSelectIndex);
						views.cards_layout.setIndexOnTop(2 + theSelectIndex);
						theSelected.setDraggable(true);
						theSelected.setPlayCard(game
								.getPlayPile(theSelectIndex).getTopCard());
						theSelected.setSelected(true);
						views.playDeckView.showBack(true);
						views.playDeckView.setDraggable(false);
						if (!game.getDeck().isEmpty()) {
							views.deck.setVisibility(View.VISIBLE);
						} else {
							views.playDeckView.setVisibility(View.INVISIBLE);
							views.deck_background.setVisibility(View.INVISIBLE);
							views.deck.setVisibility(View.INVISIBLE);
						}
						if (game.getDeck().getCards().size() == 1) {
							views.deck_background.setVisibility(View.INVISIBLE);
						}
					}

					@Override
					public void rotated(View view) {
						// do nothing
					}

					@Override
					public void onClickBack(View view) {
						if (true) throw new RuntimeException("Clicked back of invalid card...");
						// mark the current play card back to normal
						int stackInPlay = game.getStackInPlayIndex();
						if (stackInPlay >= 0) {
							views.playCardViews[stackInPlay].setSelected(false);
						}

						views.cards_layout.setIndexOnTop(0);

						PlayCard deckCard = game.getDeckCard();
						if (game.getDeck().isEmpty()) {
							views.deck_background.setVisibility(View.INVISIBLE);
						}
						views.playDeckView.setVisibility(View.INVISIBLE);
						views.playDeckView.setPlayCard(deckCard);
						views.playDeckView.showBack(false);

						new FlipHorizontalToAnimation(views.deck)
								.setFlipToView(views.playDeckView)
								.setInterpolator(new LinearInterpolator())
								.setDuration(FLIP_CARD_DURATION_MS)
								// .setListener(new
								// com.easyandroidanimations.library.AnimationListener()
								// {
								//
								// @Override
								// public void onAnimationEnd(
								// com.easyandroidanimations.library.Animation
								// animation) {
								//
								// }
								// })
								.animate();

						TriplicaClaimAnimation animator = new TriplicaClaimAnimation(
								getView(), views, game);

						// TODO need an "animating variable" to prevent inputs
						// while
						// animating
						animator.animateTriplicas(new Runnable() {
							@Override
							public void run() {
								game.endTurn();
								views.playDeckView.setDraggable(true);
							}
						});

					}

					@Override
					public void cancelDrag() {
						// Nothing to do
					}

				});

		return rootView;
	}

	private void findViews(View rootView) {
		views = new ViewHolder();

		views.cards_layout = (SolitairePlayCardLinearLayout) rootView
				.findViewById(R.id.cards_layout);

		views.goalCardViews = new HashMap<Shape, GoalViews>();
		views.goalCardViews.put(Shape.DIAMOND, new GoalViews(rootView,
				R.id.goal_pile1, R.id.goal1_remaining));
		views.goalCardViews.put(Shape.ASTERISK, new GoalViews(rootView,
				R.id.goal_pile2, R.id.goal2_remaining));
		views.goalCardViews.put(Shape.OVAL, new GoalViews(rootView,
				R.id.goal_pile3, R.id.goal3_remaining));
		views.goalCardViews.put(Shape.SQUARE, new GoalViews(rootView,
				R.id.goal_pile4, R.id.goal4_remaining));
		views.goalCardViews.put(Shape.STAR, new GoalViews(rootView,
				R.id.goal_pile5, R.id.goal5_remaining));
		views.goalCardViews.put(Shape.X, new GoalViews(rootView,
				R.id.goal_pile6, R.id.goal6_remaining));

		views.playDeckView = (PlayCardView) rootView
				.findViewById(R.id.deck_new_card);
		views.deck = (PlayCardView) rootView.findViewById(R.id.deck);
		views.deck_background = (PlayCardView) rootView
				.findViewById(R.id.deck_background);

		views.playCardViews = new PlayCardView[4];
		views.playCardViews[0] = (PlayCardView) rootView
				.findViewById(R.id.pile1);
		views.playCardViews[1] = (PlayCardView) rootView
				.findViewById(R.id.pile2);
		views.playCardViews[2] = (PlayCardView) rootView
				.findViewById(R.id.pile3);
		views.playCardViews[3] = (PlayCardView) rootView
				.findViewById(R.id.pile4);

		views.shapeViewsToAnimate = new ArrayList<ImageView>();
		ImageView shape1 = (ImageView) rootView.findViewById(R.id.anim_shape1);
		ImageView shape2 = (ImageView) rootView.findViewById(R.id.anim_shape2);
		ImageView shape3 = (ImageView) rootView.findViewById(R.id.anim_shape3);
		views.shapeViewsToAnimate.add(shape1);
		views.shapeViewsToAnimate.add(shape2);
		views.shapeViewsToAnimate.add(shape3);

		views.goalViewToAnimate = (TextView) rootView
				.findViewById(R.id.anim_goal_remaining);

		views.underPiles = new PlayCardView[4];
		views.underPiles[0] = (PlayCardView) rootView
				.findViewById(R.id.under_pile1);
		views.underPiles[1] = (PlayCardView) rootView
				.findViewById(R.id.under_pile2);
		views.underPiles[2] = (PlayCardView) rootView
				.findViewById(R.id.under_pile3);
		views.underPiles[3] = (PlayCardView) rootView
				.findViewById(R.id.under_pile4);
	}

	private void updatePlayCards() {
		int numPlayCards = game.getNumPlayPiles();
		for (int i = 0; i < numPlayCards; i++) {
			PlayCard card = game.getPlayPile(i).getTopCard();
			if (card == null) {
				views.playCardViews[i].setVisibility(View.INVISIBLE);
			} else {
				views.playCardViews[i].setSelected(i == game
						.getStackInPlayIndex());
				views.playCardViews[i].setVisibility(View.VISIBLE);
				views.playCardViews[i].setPlayCard(card);
			}
		}
	}

	private void updateGoalViews() {
		for (Shape each : Shape.values()) {
			int remaining = game.getGoalsRemaining(each);
			GoalViews goalViews = views.goalCardViews.get(each);
			if (remaining > 0) {
				goalViews.cardView.setVisibility(View.VISIBLE);
				goalViews.cardView.setCard(new GoalCard(each));
				goalViews.remainingView.setText(remaining + "");
				goalViews.remainingView.setVisibility(View.VISIBLE);
			} else {
				goalViews.cardView.setVisibility(View.INVISIBLE);
				goalViews.remainingView.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void configurePlayCardPile(final PlayCardView playCardView,
			final int i) {
		final PlayCardView underPile = views.underPiles[i];

		playCardView.setDroppables(views.playCardViews);

		playCardView.setOnClickPlayCardListener(new OnPlayCardClickListener() {
			@Override
			public void onClickFront(View view, Position position) {
				Set<Triplica> triplicas = game.markTriplicas(i, position);
				// TODO animate/highlight claimed triplica
				for (Triplica each : triplicas) {
					List<ImageView> triplicaShapeViews = TriplicaClaimAnimation
							.getTriplicaShapeViews(each, views);
					for (ImageView eachView : triplicaShapeViews) {
						eachView.setBackgroundColor(Color.GREEN);
					}
				}
			}

			@Override
			public void startDrag(PlayCardView playCardView) {
				// display the card underneath
				underPile.setPlayCard(game.getPlayPile(i).getNextCard());
				underPile.setVisibility(View.VISIBLE);

				for (Triplica each : game.getPendingTriplicas()) {
					List<ImageView> triplicaShapeViews = TriplicaClaimAnimation
							.getTriplicaShapeViews(each, views);
					for (ImageView eachView : triplicaShapeViews) {
						eachView.setBackgroundColor(Color.TRANSPARENT);
					}
				}

			}

			@Override
			public void onClickBack(View view) {
				throw new RuntimeException("Can't get here!");
			}

			@Override
			public void rotated(View view) {
				// TODO unhighlight claimed triplica
				game.getPendingTriplicas().clear();
			}

			@Override
			public void droppedOn(PlayCardView theSelected, int theSelectIndex) {
				for (Triplica each : game.getPendingTriplicas()) {
					List<ImageView> triplicaShapeViews = TriplicaClaimAnimation
							.getTriplicaShapeViews(each, views);
					for (ImageView eachView : triplicaShapeViews) {
						eachView.setBackgroundColor(Color.TRANSPARENT);
					}
				}

				playCardView.setDraggable(false);
				theSelected.setDraggable(true);
				underPile.setVisibility(View.GONE);
				game.placePlayCard(theSelectIndex);
				theSelected.setPlayCard(game.getPlayPile(theSelectIndex)
						.getTopCard());
				theSelected.setSelected(true);
				playCardView.setPlayCard(game.getPlayPile(i).getTopCard());
				playCardView.setSelected(false);

				views.cards_layout.setIndexOnTop(theSelectIndex + 2);
				views.cards_layout.invalidate();
			}

			@Override
			public void cancelDrag() {
				for (Triplica each : game.getPendingTriplicas()) {
					List<ImageView> triplicaShapeViews = TriplicaClaimAnimation
							.getTriplicaShapeViews(each, views);
					for (ImageView eachView : triplicaShapeViews) {
						eachView.setBackgroundColor(Color.GREEN);
					}
				}

				views.cards_layout.invalidate();
			}
		});
	}

}
