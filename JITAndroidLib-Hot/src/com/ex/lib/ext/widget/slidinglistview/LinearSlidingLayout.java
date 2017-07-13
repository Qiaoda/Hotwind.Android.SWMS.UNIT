package com.ex.lib.ext.widget.slidinglistview;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;

public class LinearSlidingLayout extends LinearLayout {
	/**
	 * 屏幕宽度
	 */
	private int screenWidth;

	/**
	 * 左侧布局
	 */
	private View leftLayout;

	/**
	 * 右侧布局
	 */
	private View rightLayout;

	/**
	 * 左侧布局参数
	 */
	private MarginLayoutParams leftLayoutParams;

	/**
	 * 右侧布局参数
	 */
	private MarginLayoutParams rightLayoutParams;

	/**
	 * 右侧布局最多可以滑动到的左边缘。
	 */
	private int leftEdge = 0;

	/**
	 * 右侧布局最多可以滑动到的右边缘。
	 */
	private int rightEdge = 0;

	/**
	 * 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;

	/**
	 * 右侧布局当前是显示还是隐藏。只有完全显示或隐藏时才会更改此值，滑动过程中此值无效。
	 */
	private boolean isRightLayoutVisible;

	/**
	 * 是否正在滑动。
	 */
	private boolean isSliding;

	public LinearSlidingLayout(Context context) {
		super(context);
	}

	public LinearSlidingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (changed) {
			// 获取左侧布局对象
			leftLayout = getChildAt(0);
			leftLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
			leftLayoutParams.width = screenWidth;
			leftLayout.setLayoutParams(leftLayoutParams);
			// 获取右侧布局对象
			rightLayout = getChildAt(1);
			rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
			rightEdge = 0;
			leftEdge = rightLayoutParams.width;
		}
	}

	/**
	 * 使当前线程睡眠指定的毫秒数。
	 * 
	 * @param millis
	 *            指定当前线程睡眠多久，以毫秒为单位
	 */
	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	class ScrollTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... speed) {
			int rightMargin = rightLayoutParams.rightMargin;
			// 根据传入的速度来滚动界面，当滚动到达左边界或右边界时，跳出循环。
			while (true) {
				rightMargin = rightMargin + speed[0];
				if (rightMargin > rightEdge) {
					rightMargin = rightEdge;
					break;
				}
				if (rightMargin < -leftEdge) {
					rightMargin = -leftEdge;
					break;
				}
				publishProgress(rightMargin);
				// 为了要有滚动效果产生，每次循环使线程睡眠20毫秒，这样肉眼才能够看到滚动动画。
				sleep(speed[1]);
			}
			if (speed[0] > 0) {
				isRightLayoutVisible = true;
			} else {
				isRightLayoutVisible = false;
			}
			isSliding = false;
			return rightMargin;
		}

		@Override
		protected void onProgressUpdate(Integer... rightMargin) {
			rightLayoutParams.rightMargin = rightMargin[0];
			rightLayout.setLayoutParams(rightLayoutParams);
			leftLayoutParams.leftMargin = -leftEdge - rightMargin[0];
			leftLayout.setLayoutParams(leftLayoutParams);
		}

		@Override
		protected void onPostExecute(Integer rightMargin) {
			rightLayoutParams.rightMargin = rightMargin;
			rightLayout.setLayoutParams(rightLayoutParams);
			leftLayoutParams.leftMargin = -leftEdge - rightMargin;
			leftLayout.setLayoutParams(leftLayoutParams);
		}
	}

	/**
	 * 外部调用方法
	 * 
	 * @return
	 */
	public boolean isRightLayoutVisiable() {
		return isRightLayoutVisible;
	}

	public void setRightLayoutVisiable(boolean visiable) {
		this.isRightLayoutVisible = visiable;
	}

	/**
	 * 是否在滑动
	 * 
	 * @return
	 */
	public boolean isSliding() {
		return isSliding;
	}

	public void setSliding(boolean sliding) {
		this.isSliding = sliding;
	}

	/**
	 * 将屏幕滚动到左侧布局界面，滚动速度设定为-30.
	 */
	public void scrollToLeftLayout() {
		new ScrollTask().execute(-30, 20);
	}

	/**
	 * 将屏幕滚动到右侧布局界面，滚动速度设定为30.
	 */
	public void scrollToRightLayout() {
		new ScrollTask().execute(30, 20);
	}

	/**
	 * 删除的时候直接滚动
	 */
	public void scrollWithoutDelay() {
		new ScrollTask().execute(-30, 0);
	}
}
