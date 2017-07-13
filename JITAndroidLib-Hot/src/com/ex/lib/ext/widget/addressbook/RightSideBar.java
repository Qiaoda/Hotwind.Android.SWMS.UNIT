package com.ex.lib.ext.widget.addressbook;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ex.lib.core.utils.mgr.MgrT;

public class RightSideBar extends LinearLayout {

	public static final String[] TEST_LETTERS = { "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
			"#" };// "#"

	private OnLetterTouchListener letterTouchListener;

	private final int DEFAULT_TEXT_SIZE = 13;

	private int textsize = DEFAULT_TEXT_SIZE;

	// private boolean first = true;

	private String[] LETTERS = null;

	public RightSideBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		// ViewTreeObserver vto = getViewTreeObserver();
		// vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
		// @Override
		// public void onGlobalLayout() {
		// if (first) {
		// reSetTextSize(getHeight());
		// }
		//
		// first = getHeight() == 0;
		// }
		// });
		//
		// initViews();
	}

	public void setLetters(String[] ls) {
		if (ls != null && ls.length != 0) {
			LETTERS = ls;
			reSetTextSize(getHeight());
			initViews();
		}

	}

	private final void reSetTextSize(int totalHeight) {
		textsize = (int) (getHeight() / LETTERS.length / 3.5);
		if (textsize > 14) {
			textsize = 14;
		}
		int padding = (int) (textsize / 1.2);
		for (int i = 0; i < getChildCount(); i++) {
			View v = getChildAt(i);
			if (v instanceof TextView) {
				TextView tv = (TextView) v;
				tv.setTextSize(textsize);
				tv.setPadding(padding, 0, padding, 0);
				tv.setTextColor(Color.parseColor("#333333"));
			}
		}
	}

	private final void initViews() {
		this.setOrientation(VERTICAL);

		for (String str : LETTERS) {
			TextView tv = new TextView(getContext());
			tv.setText(str);
			tv.setTextSize(textsize);

			int padding = MgrT.getInstance().getDip2Px(getContext(), 3);
			tv.setPadding(padding, 0, padding, 0);
			// tv.setPadding(15, 0, 15, 0);
			tv.setGravity(Gravity.CENTER);

			tv.setTextColor(Color.BLACK);
			tv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1));
			addView(tv);
		}
	}

	public void setOnLetterTouchListener(OnLetterTouchListener listener) {
		this.letterTouchListener = listener;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_MOVE:
			int position = (int) (event.getY() / (getHeight() / LETTERS.length));// +1
			if (position >= 0) { // && position < LETTERS.length
				if (position >= LETTERS.length) {
					position = LETTERS.length - 1;
				}
				Log.e("look", "LETTERS[" + position + "]:" + LETTERS[position]);
				if (letterTouchListener != null) {
					letterTouchListener.onLetterTouch(LETTERS[position], position);
				}
			}
			return true;
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_UP:
			if (letterTouchListener != null) {
				letterTouchListener.onActionUp();
			}
			return true;
		}
		return false;
	}

	public interface OnLetterTouchListener {

		public void onLetterTouch(String letter, int position);

		public void onActionUp();
	}

}
