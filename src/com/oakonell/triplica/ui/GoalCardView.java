package com.oakonell.triplica.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.oakonell.triplica.R;
import com.oakonell.triplica.model.GoalCard;
import com.oakonell.triplica.model.Shape;

public class GoalCardView extends CardView {
	private ImageView shapeView;
	private View back;

	public GoalCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		inflate(getContext(), R.layout.goal_card, this);

		if (isInEditMode()) {
			// shapeView.setImageResource(R.drawable.goal_card_back);
		} else {
			shapeView = (ImageView) findViewById(R.id.shape);
			back = findViewById(R.id.back);
		}
	}

	public GoalCardView(Context context) {
		this(context, null);
	}

	public void setCard(GoalCard card) {
		Shape shape = card.getShape();
		shapeView.setImageResource(shape.getDrawableResoure());
	}

	public void showBack(boolean showBack) {
		if (showBack) {
			back.setVisibility(View.VISIBLE);
			shapeView.setVisibility(View.GONE);
		} else {
			back.setVisibility(View.GONE);
			shapeView.setVisibility(View.VISIBLE);
		}
	}
}
