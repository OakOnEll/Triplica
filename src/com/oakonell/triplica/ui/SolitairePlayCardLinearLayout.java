package com.oakonell.triplica.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SolitairePlayCardLinearLayout extends LinearLayout {

	public SolitairePlayCardLinearLayout(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		setChildrenDrawingOrderEnabled(true);
	}

	public SolitairePlayCardLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setChildrenDrawingOrderEnabled(true);
	}

	public SolitairePlayCardLinearLayout(Context context) {
		super(context);
		setChildrenDrawingOrderEnabled(true);
	}

	@Override
	protected int getChildDrawingOrder(int childCount, int i) {
		if (i == childCount - 1)
			return 0;
		return i + 1;
	}
}
