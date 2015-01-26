package com.oakonell.triplica.ui.solitaire;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oakonell.triplica.model.Position;
import com.oakonell.triplica.model.Shape;
import com.oakonell.triplica.model.Triplica;
import com.oakonell.triplica.model.solitaire.SolitaireGame;
import com.oakonell.triplica.ui.solitaire.SolitaireGameFragment.GoalViews;
import com.oakonell.triplica.ui.solitaire.SolitaireGameFragment.ViewHolder;

public class TriplicaClaimAnimation {
	private ViewHolder views;
	private View view;

	private SolitaireGame game;

	public TriplicaClaimAnimation(View view, ViewHolder holder,
			SolitaireGame game) {
		this.view = view;
		this.game = game;
		this.views = holder;
	}

	protected void animateTriplicas(final Runnable continuation) {
		List<Triplica> triplicas = new ArrayList<Triplica>(
				game.getPendingTriplicas());
		animateTriplicas(triplicas, continuation);
	}

	private void animateTriplicas(final List<Triplica> triplicas,
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

	private void animateTriplica(final Triplica triplica,
			final Runnable continuation) {

		List<ImageView> triplicaShapeViews = getTriplicaShapeViews(triplica,
				views);

		Shape shape = game.getPlayPile(triplica.startStackNum).getTopCard()
				.get(triplica.startPosition);
		GoalViews goalViews = views.goalCardViews.get(shape);
		final TextView targetRemainingView = goalViews.remainingView;

		// prepare the copy of the shapes over the existing shapes
		List<Animation> animations = new ArrayList<Animation>();
		for (int i = 0; i < 3; i++) {
			ImageView original = triplicaShapeViews.get(i);
			original.setBackgroundColor(Color.TRANSPARENT);
			ImageView toAnimate = views.shapeViewsToAnimate.get(i);
			int left = getLeftRelativeTo(original, view);
			int top = getTopRelativeTo(original, view);

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
						for (View each : views.shapeViewsToAnimate) {
							each.setVisibility(View.GONE);
						}
						animateRemainingTarget(targetRemainingView,
								continuation);
					}
				});
			}
		}

		for (int i = 0; i < 3; i++) {
			ImageView toAnimate = views.shapeViewsToAnimate.get(i);
			Animation anim = animations.get(i);
			toAnimate.startAnimation(anim);
		}

	}

	public static List<ImageView> getTriplicaShapeViews(
			final Triplica triplica, ViewHolder views) {
		List<ImageView> triplicaShapeViews = new ArrayList<ImageView>();
		triplicaShapeViews.add(views.playCardViews[triplica.startStackNum]
				.getShape(triplica.startPosition));
		ImageView middleView;
		if (triplica.startPosition == triplica.endPosition) {
			middleView = views.playCardViews[triplica.startStackNum + 1]
					.getShape(triplica.startPosition);
		} else {
			middleView = views.playCardViews[triplica.startStackNum + 1]
					.getShape(Position.MIDDLE);
		}
		triplicaShapeViews.add(middleView);
		triplicaShapeViews.add(views.playCardViews[triplica.endStackNum]
				.getShape(triplica.endPosition));
		return triplicaShapeViews;
	}

	private void animateRemainingTarget(final TextView targetRemainingView,
			final Runnable continuation) {
		final TextView toAnimate = views.goalViewToAnimate;
		int left = getLeftRelativeTo(targetRemainingView, view);
		int top = getTopRelativeTo(targetRemainingView, view);

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

		int xDelta = getLeftRelativeTo(targetRemainingView, view)
				- getLeftRelativeTo(toAnimate, view);
		int yDelta = getTopRelativeTo(targetRemainingView, view)
				- getTopRelativeTo(toAnimate, view);

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
