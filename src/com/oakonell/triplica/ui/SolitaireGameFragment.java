package com.oakonell.triplica.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oakonell.triplica.R;
import com.oakonell.triplica.model.GoalCard;
import com.oakonell.triplica.model.PlayCard;
import com.oakonell.triplica.model.Position;
import com.oakonell.triplica.model.Shape;
import com.oakonell.triplica.model.Triplica;
import com.oakonell.triplica.model.solitaire.SolitaireGame;
import com.oakonell.triplica.model.solitaire.SolitaireGame.GameState;
import com.oakonell.triplica.ui.PlayCardView.OnPlayCardClickListener;

public class SolitaireGameFragment extends Fragment {

	private SolitaireGame game = new SolitaireGame();
	private Map<Shape, GoalViews> goalCardViews;
	private PlayCardView playDeckView;
	private PlayCardView[] playCardViews;
	private PlayCardView deck;
	private List<ImageView> shapeViewsToAnimate;
	private TextView goalViewToAnimate;

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

		goalCardViews = new HashMap<Shape, GoalViews>();
		goalCardViews.put(Shape.DIAMOND, new GoalViews(rootView,
				R.id.goal_pile1, R.id.goal1_remaining));
		goalCardViews.put(Shape.ASTERISK, new GoalViews(rootView,
				R.id.goal_pile2, R.id.goal2_remaining));
		goalCardViews.put(Shape.OVAL, new GoalViews(rootView, R.id.goal_pile3,
				R.id.goal3_remaining));
		goalCardViews.put(Shape.SQUARE, new GoalViews(rootView,
				R.id.goal_pile4, R.id.goal4_remaining));
		goalCardViews.put(Shape.STAR, new GoalViews(rootView, R.id.goal_pile5,
				R.id.goal5_remaining));
		goalCardViews.put(Shape.X, new GoalViews(rootView, R.id.goal_pile6,
				R.id.goal6_remaining));

		updateGoalViews();

		playDeckView = (PlayCardView) rootView.findViewById(R.id.deck_new_card);
		playDeckView.showBack(true);
		game.setupBoard();
		deck = (PlayCardView) rootView.findViewById(R.id.deck);
		deck.showBack(true);

		playDeckView.bringToFront();
		// rootView.findViewById(R.id.deck_holder).bringToFront();

		playDeckView.setOnClickPlayCardListener(new OnPlayCardClickListener() {
			@Override
			public void onClickFront(View view, Position position) {
				// TODO animate the flip
				Animation rotate = AnimationUtils.loadAnimation(getActivity(),
						R.anim.card_rotate);
				rotate.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						PlayCard deckCard = game.getDeckCard();
						deckCard.flip();
						// playDeckView.invalidate();
					}
				});
				playDeckView.startAnimation(rotate);
			}

			@Override
			public void onClickBack(View view) {
				// mark the current play card back to normal
				int stackInPlay = game.getStackInPlay();
				if (stackInPlay >= 0) {
					playCardViews[stackInPlay].setSelected(false);
				}
				game.endTurn();

				PlayCard deckCard = game.getDeckCard();
				playDeckView.showBack(false);
				playDeckView.setPlayCard(deckCard);

			}
		});

		int numPlayCards = game.getNumPlayPiles();
		playCardViews = new PlayCardView[numPlayCards];
		playCardViews[0] = (PlayCardView) rootView.findViewById(R.id.pile1);
		playCardViews[1] = (PlayCardView) rootView.findViewById(R.id.pile2);
		playCardViews[2] = (PlayCardView) rootView.findViewById(R.id.pile3);
		playCardViews[3] = (PlayCardView) rootView.findViewById(R.id.pile4);
		for (int i = 0; i < numPlayCards; i++) {
			PlayCardView playCardView = playCardViews[i];
			configurePlayCardPile(playCardView, i);
		}

		shapeViewsToAnimate = new ArrayList<ImageView>();
		ImageView shape1 = (ImageView) rootView.findViewById(R.id.anim_shape1);
		ImageView shape2 = (ImageView) rootView.findViewById(R.id.anim_shape2);
		ImageView shape3 = (ImageView) rootView.findViewById(R.id.anim_shape3);
		shapeViewsToAnimate.add(shape1);
		shapeViewsToAnimate.add(shape2);
		shapeViewsToAnimate.add(shape3);

		goalViewToAnimate = (TextView) rootView
				.findViewById(R.id.anim_goal_remaining);

		updatePlayCards();

		return rootView;
	}

	private void updatePlayCards() {
		int numPlayCards = game.getNumPlayPiles();
		for (int i = 0; i < numPlayCards; i++) {
			PlayCard card = game.getPlayPile(i).getTopCard();
			if (card == null) {
				playCardViews[i].setVisibility(View.INVISIBLE);
			} else {
				playCardViews[i].setSelected(i == game.getStackInPlay());
				playCardViews[i].setVisibility(View.VISIBLE);
				playCardViews[i].setPlayCard(card);
			}
		}
	}

	private void updateGoalViews() {
		for (Shape each : Shape.values()) {
			int remaining = game.getGoalsRemaining(each);
			GoalViews goalViews = goalCardViews.get(each);
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
		playCardView.setOnClickPlayCardListener(new OnPlayCardClickListener() {

			@Override
			public void onClickFront(View view, Position position) {
				if (game.isStartOfTurn()) {
					// highlight the currently played card?
					// TODO show animation (block input while animating)

					int amountToMoveRight = playCardViews[i].getLeft()
							- getView().findViewById(R.id.deck_holder)
									.getLeft();

					TranslateAnimation anim = new TranslateAnimation(0,
							amountToMoveRight, 0, 0);
					anim.setDuration(800);
					anim.setZAdjustment(Animation.ZORDER_TOP);
					anim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							game.placePlayCard(i);
							playCardView.setPlayCard(game.getPlayPile(i)
									.getTopCard());
							playCardView.setSelected(true);
							playDeckView.showBack(true);
						}
					});
					playDeckView.startAnimation(anim);

				} else {
					Set<Triplica> triplicas = game.claimTriplicas(i, position);
					Runnable continuation = new Runnable() {
						@Override
						public void run() {
							// Toast.makeText(getActivity(),
							// "Got " + triplicas.size() + " triplicas!",
							// Toast.LENGTH_SHORT).show();
							// TODO evaluate game end and display triplica
							updateGoalViews();
							if (game.getGameStatus().state == GameState.WON) {
								Toast.makeText(
										getActivity(),
										"You won with a score of "
												+ game.getGameStatus().score,
										Toast.LENGTH_SHORT).show();
								// TODO end of game stuff
							}
							// TODO if play deck is empty.. show a button to
							// end-
							// it may be that there are still triplicas that
							// could
							// finish and win, with score=0
							if (game.getGameStatus().state == GameState.LOST) {
								Toast.makeText(getActivity(), "You lost!",
										Toast.LENGTH_SHORT).show();
								// TODO end of game stuff
							}
						}
					};
					animateTriplicas(new ArrayList<Triplica>(triplicas),
							continuation);
				}
			}

			@Override
			public void onClickBack(View view) {
				throw new RuntimeException("Can't get here!");
			}
		});
	}

	protected void animateTriplicas(final List<Triplica> triplicas,
			final Runnable continuation) {
		if (triplicas.isEmpty()) {
			continuation.run();
			return;
		}
		Triplica triplica = triplicas.remove(0);
		Runnable next = new Runnable() {
			@Override
			public void run() {
				animateTriplicas(triplicas, continuation);
			}
		};

		animateTriplica(triplica, next);

	}

	protected void animateTriplica(final Triplica triplica,
			final Runnable continuation) {

		List<ImageView> triplicaShapeViews = new ArrayList<ImageView>();
		triplicaShapeViews.add(playCardViews[triplica.startStackNum]
				.getShape(triplica.startPosition));
		ImageView middleView;
		if (triplica.startPosition == triplica.endPosition) {
			middleView = playCardViews[triplica.startStackNum + 1]
					.getShape(triplica.startPosition);
		} else {
			middleView = playCardViews[triplica.startStackNum + 1]
					.getShape(Position.MIDDLE);
		}
		triplicaShapeViews.add(middleView);
		triplicaShapeViews.add(playCardViews[triplica.endStackNum]
				.getShape(triplica.endPosition));

		Shape shape = game.getPlayPile(triplica.startStackNum).getTopCard()
				.get(triplica.startPosition);
		GoalViews goalViews = goalCardViews.get(shape);
		final TextView targetRemainingView = goalViews.remainingView;

		// prepare the copy of the shapes over the existing shapes
		List<Animation> animations = new ArrayList<Animation>();
		for (int i = 0; i < 3; i++) {
			ImageView original = triplicaShapeViews.get(i);
			ImageView toAnimate = shapeViewsToAnimate.get(i);
			int left = getLeftRelativeTo(original, getView());
			int top = getTopRelativeTo(original, getView());

			RelativeLayout.LayoutParams toAnimateParams = (android.widget.RelativeLayout.LayoutParams) toAnimate
					.getLayoutParams();
			toAnimateParams.leftMargin = left;
			toAnimateParams.topMargin = top;
			toAnimateParams.width = original.getWidth();
			toAnimateParams.height = original.getHeight();
			toAnimate.setLayoutParams(toAnimateParams);
			toAnimate.setImageDrawable(original.getDrawable());
			toAnimate.setVisibility(View.VISIBLE);

			Animation anim = createTriplicaShapeAnimation(original,
					targetRemainingView);
			animations.add(anim);
			if (i == 2) {
				anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						for (View each : shapeViewsToAnimate) {
							each.setVisibility(View.GONE);
						}
						animateRemainingTarget(targetRemainingView,
								continuation);
					}
				});
			}
		}

		for (int i = 0; i < 3; i++) {
			ImageView toAnimate = shapeViewsToAnimate.get(i);
			Animation anim = animations.get(i);
			toAnimate.startAnimation(anim);
		}

	}

	protected void animateRemainingTarget(final TextView targetRemainingView,
			final Runnable continuation) {
		final TextView toAnimate = goalViewToAnimate;
		int left = getLeftRelativeTo(targetRemainingView, getView());
		int top = getTopRelativeTo(targetRemainingView, getView());

		RelativeLayout.LayoutParams toAnimateParams = (android.widget.RelativeLayout.LayoutParams) toAnimate
				.getLayoutParams();
		toAnimateParams.leftMargin = left;
		toAnimateParams.topMargin = top;
		toAnimateParams.width = targetRemainingView.getWidth();
		toAnimateParams.height = targetRemainingView.getHeight();
		toAnimate.setLayoutParams(toAnimateParams);
		toAnimate.setText(targetRemainingView.getText());
		toAnimate.setVisibility(View.VISIBLE);
		toAnimate.invalidate();

		final int remaining = Integer.parseInt(targetRemainingView.getText()
				.toString());

		ScaleAnimation scale = new ScaleAnimation(1, 2, 1, 2,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(500);
		scale.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// update the view for now, the model will update after all
				// triplicas are scored
				toAnimate.setVisibility(View.GONE);
				targetRemainingView.setText("" + (remaining - 1));
				continuation.run();
			}
		});

		toAnimate.startAnimation(scale);
	}

	private Animation createTriplicaShapeAnimation(ImageView toAnimate,
			TextView targetRemainingView) {
		AnimationSet set = new AnimationSet(true);

		// expand about center
		ScaleAnimation scale = new ScaleAnimation(1, 2, 1, 2,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scale.setDuration(500);
		set.addAnimation(scale);

		// then shrink and move to remaining number text view
		ScaleAnimation shrink = new ScaleAnimation(1, 0.1f, 1, 0.1f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		shrink.setStartOffset(500);
		shrink.setDuration(750);
		set.addAnimation(shrink);

		int xDelta = getLeftRelativeTo(targetRemainingView, getView())
				- getLeftRelativeTo(toAnimate, getView());
		int yDelta = getTopRelativeTo(targetRemainingView, getView())
				- getTopRelativeTo(toAnimate, getView());

		xDelta -= toAnimate.getWidth() / 2 - targetRemainingView.getWidth() / 2;
		yDelta -= toAnimate.getHeight() / 2 - targetRemainingView.getHeight()
				/ 2;

		TranslateAnimation translate = new TranslateAnimation(0, xDelta, 0,
				yDelta);

		translate.setStartOffset(500);
		translate.setDuration(800);
		set.addAnimation(translate);

		return set;
	}

	private int getLeftRelativeTo(View original, View view) {
		if (view == original)
			return 0;
		return original.getLeft()
				+ getLeftRelativeTo((View) original.getParent(), view);
	}

	private int getTopRelativeTo(View original, View view) {
		if (view == original)
			return 0;
		return original.getTop()
				+ getTopRelativeTo((View) original.getParent(), view);
	}
}
