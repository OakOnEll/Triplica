package com.oakonell.triplica.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
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

	public PlayCardView(Context context, AttributeSet attrs) {
		super(context, attrs);

		inflate(getContext(), R.layout.play_card, this);

		top = (ImageView) findViewById(R.id.top);
		middle = (ImageView) findViewById(R.id.middle);
		bottom = (ImageView) findViewById(R.id.bottom);

		front = findViewById(R.id.front);
		back = findViewById(R.id.back);
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
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClickBack(PlayCardView.this);
			}
		});
		top.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClickFront(PlayCardView.this, Position.TOP);
			}
		});
		middle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClickFront(PlayCardView.this, Position.MIDDLE);
			}
		});
		bottom.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClickFront(PlayCardView.this, Position.BOTTOM);
			}
		});
	}

	public interface OnPlayCardClickListener {
		void onClickBack(View view);

		void onClickFront(View view, Position position);
	}

	public ImageView  getShape(Position position) {
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
}
