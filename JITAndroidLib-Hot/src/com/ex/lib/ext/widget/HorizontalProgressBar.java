package com.ex.lib.ext.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.ex.lib.R;
import com.ex.lib.core.utils.mgr.MgrT;

public class HorizontalProgressBar extends View {

	private Paint paint;

	private float textSize;
	private int textColor;
	private int bgColor;
	private int progressColor;
	// 进度最大值，默认100
	private int max;
	// 进度值，默认1
	private int progress = 1;
	// 默认true
	private boolean textIsDisplayable;
	
	private RectF rect;

	public HorizontalProgressBar(Context context) {
		this(context, null);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HorizontalProgressBar(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();

		TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
				R.styleable.HorizontalProgressBar);
		textColor = mTypedArray.getColor(
				R.styleable.HorizontalProgressBar_h_textColor, Color.RED);
		bgColor = mTypedArray.getColor(
				R.styleable.HorizontalProgressBar_bgColor, Color.GREEN);
		progressColor = mTypedArray.getColor(
				R.styleable.HorizontalProgressBar_progressColor, Color.GREEN);
		textSize = mTypedArray.getDimension(
				R.styleable.HorizontalProgressBar_h_textSize, 15);
		max = mTypedArray.getInteger(R.styleable.HorizontalProgressBar_h_max,
				100);
		textIsDisplayable = mTypedArray.getBoolean(
				R.styleable.HorizontalProgressBar_h_textIsDisplayable, true);
		mTypedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int centre = getWidth() / 2;
		paint.setAntiAlias(true);
		paint.setColor(bgColor);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setStrokeWidth(getWidth());
//		if (rect == null) {
//			rect = new RectF(getLeft(), getTop(), getRight(), getBottom());
//		}
//		float r = DensityUtil.dip2px(getContext(), 60);
//		canvas.drawRoundRect(rect, r, r, paint);

		// 绘制进度
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(progressColor);
		paint.setStrokeWidth(getWidth() / max);
		float right = getLeft() + getWidth() / max * (progress - 1);
		canvas.drawRect(-getLeft() + MgrT.getInstance().getDip2Px(getContext(), 35) , 0, right, getBottom(), paint);

		// 绘制进度值文字
		paint.setStrokeWidth(0);
		paint.setColor(textColor);
		paint.setTextSize(textSize);
		String text = progress + "/" + max;
		float textWidth = paint.measureText(text);
		if (textIsDisplayable) {
			canvas.drawText(text, centre - textWidth / 2, getHeight() / 2 + textWidth / text.length(), paint);
		}
	}

	public synchronized int getMax() {
		return max;
	}

	public synchronized void setMax(int max) {
		if (max < 0) {
			throw new IllegalArgumentException("max not less than 0");
		}
		this.max = max;
	}

	public synchronized int getProgress() {
		return progress;
	}

	public synchronized void setProgress(int progress) {
		if (progress < 0) {
			throw new IllegalArgumentException("progress not less than 0");
		}
		if (progress > max) {
			progress = max;
		}
		if (progress <= max) {
			this.progress = progress;
			postInvalidate();
		}
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public float getTextSize() {
		return textSize;
	}

	public void setTextSize(float textSize) {
		this.textSize = textSize;
	}

	public int getBgColor() {
		return bgColor;
	}

	public void setBgColor(int bgColor) {
		this.bgColor = bgColor;
	}

	public int getProgressColor() {
		return progressColor;
	}

	public void setProgressColor(int progressColor) {
		this.progressColor = progressColor;
	}

	public boolean isTextIsDisplayable() {
		return textIsDisplayable;
	}

	public void setTextIsDisplayable(boolean textIsDisplayable) {
		this.textIsDisplayable = textIsDisplayable;
	}

}
