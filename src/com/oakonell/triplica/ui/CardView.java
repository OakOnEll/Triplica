package com.oakonell.triplica.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.oakonell.triplica.R;

public abstract class CardView extends LinearLayout {
	public CardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (!isInEditMode()) {
			// TODO can't display rounded corners in edit mode?
			setBackgroundResource(R.drawable.card_outline);
		}
	}

	public CardView(Context context) {
		this(context, null);
	}

}
