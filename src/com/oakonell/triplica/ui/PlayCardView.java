package com.oakonell.triplica.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import com.oakonell.triplica.R;
import com.oakonell.triplica.model.PlayCard;
import com.oakonell.triplica.model.Position;
import com.oakonell.triplica.model.Shape;

public class PlayCardView extends CardView {
	private ImageView top;
	private ImageView middle;
	private ImageView bottom;

	private View front;
	private View back;

	private PlayCard card;

	boolean draggable;

	private OnPlayCardClickListener listener;
	private PlayCardView[] droppableViews;

	private TouchManager touchManager;
	private Handler handler = new Handler();

	public PlayCardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		inflate(getContext(), R.layout.play_card, this);

		top = (ImageView) findViewById(R.id.top);
		middle = (ImageView) findViewById(R.id.middle);
		bottom = (ImageView) findViewById(R.id.bottom);

		front = findViewById(R.id.front);
		back = findViewById(R.id.back);

		touchManager = new TouchManager(2);
		setOnTouchListener(new OnTouchListener() {
			// inspired by
			// http://www.wenda.io/questions/1734846/android-working-with-ontouch-onclick-and-onlongclick-together.html
			private float originalX;
			private float originalY;
			private Vector2D originalTouchOffsetInCard;
			private boolean isMoved;
			private boolean longClicked;
			private boolean rotating;
			private int angle;
			private PlayCardView currentlySelected;

			// private float angle;

			private void restorePosition(View v) {
				setX(originalX);
				setY(originalY);
			}

			private void staticRotate() {
				if (rotating) {
					return;
				}

				Vector2D current = touchManager.getVector(0, 1);
				Vector2D previous = touchManager.getPreviousVector(0, 1);

				rotating = true;
				float deltaAngle = Vector2D.getSignedAngleBetween(current,
						previous);
				// angle -= deltaAngle;
				float directionFactor = 1;
				if (deltaAngle < 0) {
					directionFactor = -directionFactor;
				}
				// TODO handle scaling of rotation to the "strength" of the
				// gesture
				final int angleIncrement = (int) (6 * directionFactor);
				final int updateMs = 5;
				angle = angleIncrement;
				setRotation(angle);
				Runnable rotateRunner = new Runnable() {
					@Override
					public void run() {
						setRotation(angle);
						if (angle < 180 && angle >= -180) {
							angle += angleIncrement;
							handler.postDelayed(this, updateMs);
						} else {
							setRotation(0);
							card.flip();
							listener.rotated(PlayCardView.this);
							invalidate();
							rotating = false;
						}
					}

				};
				handler.postDelayed(rotateRunner, 10);
			}

			@Override
			public boolean onTouch(final View v, final MotionEvent motionEvent) {
				touchManager.update(motionEvent);

				float rawX = motionEvent.getRawX();
				float rawY = motionEvent.getRawY();

				if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
					isMoved = false;
					longClicked = false;

					originalX = getX();
					originalY = getY();
					// DD
					originalTouchOffsetInCard = new Vector2D(
							motionEvent.getRawX() - originalX,
							motionEvent.getRawY() - originalY);

					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (!isMoved
									&& motionEvent.getAction() != MotionEvent.ACTION_UP) {
								restorePosition(v);
								longClicked = true;
								handleLongClick();
							}
						}

					}, 1000);

				} else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
					if (draggable && !longClicked
							&& front.getVisibility() == View.VISIBLE) {
						float newX = motionEvent.getRawX()
								- originalTouchOffsetInCard.getX();
						float newY = motionEvent.getRawY()
								- originalTouchOffsetInCard.getY();

						float deltaXForNoMove = 0.1f * getWidth();
						float deltaYForNoMove = 0.1f * getHeight();
						if (!isMoved
								&& Math.abs(newX - (int) originalX) >= deltaXForNoMove) {
							listener.startDrag(PlayCardView.this);
							isMoved = true;
						}
						if (!isMoved
								&& Math.abs(newY - (int) originalY) >= deltaYForNoMove) {
							isMoved = true;
							listener.startDrag(PlayCardView.this);
						}

						setX(newX);
						setY(newY);

						ViewAndIndex selected = findDroppable();
						if (currentlySelected != null
								&& (selected == null || currentlySelected != selected.view)) {
							// unhighlight drop selected
							currentlySelected.setSelected(false);
						}
						if (selected != null) {
							// highlight selected
							selected.view.setSelected(true);
							currentlySelected = selected.view;
						}

					}
				} else if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
					if (currentlySelected != null) {
						// unhighlight drop selected
						currentlySelected.setSelected(false);
					}
					if (!isMoved) {
						restorePosition(v);
						if (!longClicked) {
							handleClick(originalTouchOffsetInCard);
							return true;
						}
						return true;
					}
					isMoved = false;
					if (draggable) {
						handlePossibleDrop();
					}
				}
				if (draggable && touchManager.getPressCount() == 2) {
					staticRotate();
				}
				return true;
			}

			private void handlePossibleDrop() {
				// TODO find which deck the release is over- if any

				ViewAndIndex selected = findDroppable();

				if (selected == null) {
					// move the view back to where it started
					TranslateAnimation anim = new TranslateAnimation(0,
							originalX - getX(), 0, originalY - getY());
					anim.setDuration(400);
					anim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							setX(originalX);
							setY(originalY);
							// handler.postDelayed(new Runnable() {
							// @Override
							// public void run() {
							listener.cancelDrag();
							// }
							// }, 5);
						}
					});
					startAnimation(anim);
					return;
				}

				final PlayCardView theSelected = selected.view;
				final int theIndex = selected.index;

				int[] start = new int[2];
				int[] end = new int[2];
				getLocationInWindow(start);
				theSelected.getLocationInWindow(end);

				TranslateAnimation anim = new TranslateAnimation(0, end[0]
						- start[0], 0, end[1] - start[1]);
				anim.setDuration(600);
				anim.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						setX(originalX);
						setY(originalY);
						// handler.postDelayed(new Runnable() {
						// @Override
						// public void run() {
						listener.droppedOn(theSelected, theIndex);
						// }
						// }, 5);
					}
				});
				startAnimation(anim);

			}

			class ViewAndIndex {
				public PlayCardView view;
				public int index;
			}

			private ViewAndIndex findDroppable() {
				int[] myLocation = new int[2];
				getLocationInWindow(myLocation);
				int myLeft = myLocation[0];
				int myRight = myLeft + getWidth();
				int myTop = myLocation[1];
				int myBottom = myTop + getHeight();
				int index = 0;
				ViewAndIndex result = null;
				float distance = Float.MAX_VALUE;
				for (PlayCardView each : droppableViews) {
					if (each == PlayCardView.this) {
						continue;
					}

					int[] location = new int[2];
					each.getLocationInWindow(location);

					int eachLeft = location[0];
					int eachTop = location[1];
					int eachRight = eachLeft + each.getWidth();
					int eachBottom = eachTop + each.getHeight();

					if (((myLeft >= eachLeft && myLeft <= eachRight) || (myRight >= eachLeft && myRight <= eachRight))
							&& ((myTop >= eachTop && myTop <= eachBottom) || (myBottom >= eachTop && myBottom <= eachBottom))) {
						int leftDist = myLeft - eachLeft;
						int topDist = myTop - eachTop;
						float myDistance = leftDist * leftDist + topDist
								* topDist;
						if (myDistance < distance) {
							result = new ViewAndIndex();
							result.view = each;
							result.index = index;
							distance = myDistance;
						}
					}
					index++;
				}
				return result;
			}
		});
	}

	private void handleClick(Vector2D pointInCard) {
		if (listener == null)
			return;

		if (front.getVisibility() != View.VISIBLE) {
			// clicked the back
			listener.onClickBack(PlayCardView.this);
			return;
		}

		float yTouch = pointInCard.getY();
		int[] position = new int[2];
		getLocationInWindow(position);
		yTouch = yTouch - position[1];

		if (yTouch < 0.33 * getHeight()) {
			// Toast.makeText(getContext(), "OnClick Top", Toast.LENGTH_SHORT)
			// .show();
			listener.onClickFront(this, Position.TOP);
			return;
		}
		if (yTouch < 0.67 * getHeight()) {
			// Toast.makeText(getContext(), "OnClick Middle",
			// Toast.LENGTH_SHORT)
			// .show();
			listener.onClickFront(this, Position.MIDDLE);
			return;
		}
		// Toast.makeText(getContext(), "OnClick bottom", Toast.LENGTH_SHORT)
		// .show();
		listener.onClickFront(this, Position.BOTTOM);
	}

	private void handleLongClick() {
		if (listener == null)
			return;
		// perform LongClickOperation

		// Toast.makeText(getContext(), "Long Click",
		// Toast.LENGTH_SHORT).show();
	}

	public PlayCardView(Context context) {
		this(context, null);
	}

	public void setPlayCard(PlayCard card) {
		this.card = card;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		redraw(card);
	}

	private void redraw(PlayCard card) {
		if (card == null)
			return;
		Shape topShape = card.get(Position.TOP);
		Shape middleShape = card.get(Position.MIDDLE);
		Shape bottomShape = card.get(Position.BOTTOM);

		top.setImageResource(topShape.getDrawableResoure());
		middle.setImageResource(middleShape.getDrawableResoure());
		bottom.setImageResource(bottomShape.getDrawableResoure());
	}

	public void setSelected(boolean selected) {
		if (selected) {
			front.setBackgroundResource(R.drawable.placed_card_outline);
		} else {
			front.setBackgroundResource(R.drawable.card_outline);
		}
	}

	public void showBack(boolean showBack) {
		if (showBack) {
			back.setVisibility(View.VISIBLE);
			front.setVisibility(View.GONE);
		} else {
			back.setVisibility(View.GONE);
			front.setVisibility(View.VISIBLE);
		}
	}

	public void setOnClickPlayCardListener(
			final OnPlayCardClickListener listener) {
		this.listener = listener;
	}

	public interface OnPlayCardClickListener {
		void onClickBack(View view);

		void cancelDrag();

		void startDrag(PlayCardView playCardView);

		void droppedOn(PlayCardView theSelected, int theIndex);

		void onClickFront(View view, Position position);

		void rotated(View view);
	}

	public ImageView getShape(Position position) {
		switch (position) {
		case TOP:
			return top;
		case MIDDLE:
			return middle;
		case BOTTOM:
			return bottom;
		}
		return null;
	}

	public void setDroppables(PlayCardView[] playCardViews) {
		this.droppableViews = playCardViews;
	}

	public void setDraggable(boolean draggable) {
		this.draggable = draggable;
	}

}
