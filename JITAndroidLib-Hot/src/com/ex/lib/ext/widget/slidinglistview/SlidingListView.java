package com.ex.lib.ext.widget.slidinglistview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.AbsListView.OnScrollListener;

public class SlidingListView extends ListView implements OnScrollListener{

	/**
	 * 屏幕宽度
	 */
	private int screenWidth;

	/**
	 * 在被判定为滚动之前用户手指可以移动的最大距离
	 */
	private int touchSlop;

	/**
	 * 左布局
	 */
	private View leftLayout;

	/**
	 * 右布局
	 */
	private View rightLayout;

	/**
	 * 当前滑动的
	 */
	private LinearSlidingLayout curChild;

	/**
	 * 上次滑动的
	 */
	private LinearSlidingLayout priorChild;

	/**
	 * 左布局参数
	 */
	private MarginLayoutParams leftLayoutParams;

	/**
	 * 右布局参数
	 */
	private MarginLayoutParams rightLayoutParams;

	/**
	 * 子布局最多可以滑动到的左边缘。
	 */
	private int leftEdge = 0;

	/**
	 * 子布局最多可以滑动到的右边缘。
	 */
	private int rightEdge = 0;

	/**
	 * 用于计算手指滑动的速度。
	 */
	private VelocityTracker mVelocityTracker;

	/**
	 * 滚动显示和隐藏左侧布局时，手指滑动需要达到的速度。
	 */
	public static final int SNAP_VELOCITY = 200;

	/**
	 * 记录手指按下时的横坐标。
	 */
	private float xDown;

	/**
	 * 记录手指按下时的纵坐标。
	 */
	private float yDown;

	/**
	 * 记录手指移动时的横坐标。
	 */
	private float xMove;

	/**
	 * 记录手指移动时的纵坐标。
	 */
	private float yMove;

	/**
	 * 记录手机抬起时的横坐标。
	 */
	private float xUp;

	/**
	 * 手指按下时的位置
	 */
	private int downPosition = 0;

	/**
	 * 上一次按下的位置
	 */
	private int priorPosition = -1;

	/**
	 * 第一个可见位置
	 */
	private int firstVisiablePosition;
	
	/**
	 * 是否在滑动
	 */
	private boolean isScrolling;

	public SlidingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
		setOnScrollListener(this);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		createVelocityTracker(event);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			xDown = event.getRawX();
			yDown = event.getRawY();
			downPosition = pointToPosition((int) event.getX(), (int) event.getY());
			firstVisiablePosition = getFirstVisiblePosition();
			curChild = (LinearSlidingLayout) getChildAt(downPosition - firstVisiablePosition);
			//点击的时候把上一次选中的行左滑
			if (priorPosition != -1) {
				priorChild = (LinearSlidingLayout) getChildAt(priorPosition - firstVisiablePosition);
				if (priorChild != null && downPosition != priorPosition && priorChild.isRightLayoutVisiable()) {
					priorChild.scrollToLeftLayout();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			xMove = event.getRawX();
			yMove = event.getRawY();
			int moveDistanceX = (int) (xMove - xDown);
			int distanceY = (int) (yMove - yDown);
			if (Math.abs(moveDistanceX) > Math.abs(distanceY) && curChild != null && !isScrolling) {
				leftLayout = curChild.getChildAt(0);
				rightLayout = curChild.getChildAt(1);
				leftLayoutParams = (MarginLayoutParams) leftLayout.getLayoutParams();
				leftLayoutParams.width = screenWidth;
				leftLayout.setLayoutParams(leftLayoutParams);
				rightLayoutParams = (MarginLayoutParams) rightLayout.getLayoutParams();
				rightEdge = 0;
				leftEdge = rightLayoutParams.width;
					if (!curChild.isRightLayoutVisiable() && -moveDistanceX >= touchSlop && (curChild.isSliding() || Math.abs(distanceY) <= touchSlop)) {
						curChild.setSliding(true);
						leftLayoutParams.leftMargin = moveDistanceX;
						if (leftLayoutParams.leftMargin < -leftEdge) {
							leftLayoutParams.leftMargin = -leftEdge;
						}
						leftLayout.setLayoutParams(leftLayoutParams);
						rightLayoutParams.rightMargin = -leftEdge - moveDistanceX;
						if (rightLayoutParams.rightMargin > rightEdge) {
							rightLayoutParams.rightMargin = rightEdge;
						}
						rightLayout.setLayoutParams(rightLayoutParams);
					}
					if (curChild.isRightLayoutVisiable() && moveDistanceX >= touchSlop) {
						curChild.setSliding(true);
						rightLayoutParams.rightMargin = rightEdge - moveDistanceX;
						if (rightLayoutParams.rightMargin < -leftEdge) {
							rightLayoutParams.rightMargin = -leftEdge;
						}
						rightLayout.setLayoutParams(rightLayoutParams);
						leftLayoutParams.leftMargin = moveDistanceX - leftEdge;
						if (leftLayoutParams.leftMargin > rightEdge) {
							leftLayoutParams.leftMargin = rightEdge;
						}
						leftLayout.setLayoutParams(leftLayoutParams);
					}
			}
			break;
		case MotionEvent.ACTION_UP:
			xUp = event.getRawX();
			int upDistanceX = (int) (xUp - xDown);
			if (curChild != null && !isScrolling) {
				if (curChild.isSliding()) {
					// 手指抬起时，进行判断当前手势的意图，从而决定是滚动到左侧布局，还是滚动到右侧布局
					if (wantToShowLeftLayout()) {
						if (shouldScrollToLeftLayout()) {
							curChild.scrollToLeftLayout();
						} else {
							curChild.scrollToRightLayout();
						}
					} else if (wantToShowRightLayout()) {
						if (shouldScrollToRightLayout()) {
							curChild.scrollToRightLayout();
						} else {
							curChild.scrollToLeftLayout();
						}
					}
				} else if (upDistanceX < touchSlop && curChild.isRightLayoutVisiable()) {
					curChild.scrollToLeftLayout();
				}
			}
			recycleVelocityTracker();
			priorPosition = downPosition;
			break;
		}
		//如果当前正在左右滑动或者上一个还可见则消耗掉事件，避免上下滑动
		if (curChild != null && curChild.isSliding() || (priorChild != null && priorChild.isRightLayoutVisiable())) {
			return true;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 创建VelocityTracker对象，并将触摸事件加入到VelocityTracker当中。
	 * 
	 * @param event
	 *            右侧布局监听控件的滑动事件
	 */
	private void createVelocityTracker(MotionEvent event) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(event);
	}

	/**
	 * 获取手指在右侧布局的监听View上的滑动速度。
	 * 
	 * @return 滑动速度，以每秒钟移动了多少像素值为单位。
	 */
	private int getScrollVelocity() {
		mVelocityTracker.computeCurrentVelocity(1000);
		int velocity = (int) mVelocityTracker.getXVelocity();
		return Math.abs(velocity);
	}

	/**
	 * 回收VelocityTracker对象。
	 */
	private void recycleVelocityTracker() {
		mVelocityTracker.recycle();
		mVelocityTracker = null;
	}

	/**
	 * 判断当前手势的意图是不是想显示右侧布局。如果手指移动的距离是负数，且当前右侧布局是不可见的，则认为当前手势是想要显示右侧布局。
	 * 
	 * @return 当前手势想显示右侧布局返回true，否则返回false。
	 */
	private boolean wantToShowRightLayout() {
		return xUp - xDown < 0;
	}

	/**
	 * 判断当前手势的意图是不是想显示左侧布局。如果手指移动的距离是正数，且当前右侧布局是可见的，则认为当前手势是想要显示左侧布局。
	 * 
	 * @return 当前手势想显示左侧布局返回true，否则返回false。
	 */
	private boolean wantToShowLeftLayout() {
		return xUp - xDown > 0;
	}

	/**
	 * 判断是否应该滚动将左侧布局展示出来。如果手指移动距离大于右侧布局的1/2，或者手指移动速度大于SNAP_VELOCITY，
	 * 就认为应该滚动将左侧布局展示出来。
	 * 
	 * @return 如果应该滚动将左侧布局展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToLeftLayout() {
		return xUp - xDown > rightLayoutParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}

	/**
	 * 判断是否应该滚动将右侧布局展示出来。如果手指移动距离大于右侧布局的1/2， 或者手指移动速度大于SNAP_VELOCITY，
	 * 就认为应该滚动将右侧布局展示出来。
	 * 
	 * @return 如果应该滚动将右侧布局展示出来返回true，否则返回false。
	 */
	private boolean shouldScrollToRightLayout() {
		return xDown - xUp > rightLayoutParams.width / 2 || getScrollVelocity() > SNAP_VELOCITY;
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 判断是否在上下滑动
	 */
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
		case OnScrollListener.SCROLL_STATE_FLING:
			isScrolling = true;
			break;
		case OnScrollListener.SCROLL_STATE_IDLE:
			isScrolling = false;
			break;
		case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
			isScrolling = true;
			break;

		default:
			break;
		}
		
	}
	
	

}
