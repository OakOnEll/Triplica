package com.oakonell.triplica.ui.solitaire;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class SolitairePlayCardLinearLayout extends LinearLayout {
	private int indexOnTop;
	
	
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
		if (i == childCount -  1) {
			return indexOnTop;
		}
		if (i<indexOnTop) {
			return i;
		}
		return i+1;
//		return (indexOnTop + i + 1) % childCount ;
//		if (i >= childCount -  1)
//			return 0;
//		return i + 1;

		//		if (i >= childCount - (indexOnTop + 1))
//			return indexOnTop;
//		return indexOnTop + i + 1;
	}
	
	public void setIndexOnTop(int indexOnTop) {
		this.indexOnTop = indexOnTop;
	}
}
