package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 图标（图标+标题）
 * */
public class TitleWidget extends LinearLayout {
	
	private boolean isNoleft = true;//是否有左边图标
	private boolean isNoRight = true;//是否有右边图标
	private LinearLayout leftLayout;//左边点击区域
	private LinearLayout rightLayout;//右边点击区域
	private TextView left; //左边图标
	private TextView title; //标题
	private TextView right; //右边图标
	
	public TitleWidget(Context context, AttributeSet attrs) {
		super(context, attrs);
		 // 导入布局  
        LayoutInflater.from(context).inflate(R.layout.custom_title_layout, this, true);  
        leftLayout = (LinearLayout) findViewById(R.id.lv_left);  
        rightLayout = (LinearLayout) findViewById(R.id.lv_right); 
        left = (TextView) findViewById(R.id.tv_left);  
        title = (TextView) findViewById(R.id.tv_title);  
        right = (TextView) findViewById(R.id.tv_right);  
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleWidget);
		int leftBackgroundID = a.getResourceId(R.styleable.TitleWidget_left_bg, 0);
		if(leftBackgroundID == 0) {
			//TODO
		} else {
			isNoleft = false;
			left.setBackgroundResource(leftBackgroundID);
		}
		int leftTextID = a.getResourceId(R.styleable.TitleWidget_left_text, 0);
		if(leftTextID == 0) {
			//TODO
		} else {
			isNoleft = false;
			left.setText(leftTextID);
		}
		if(isNoleft) {
			leftLayout.setBackgroundResource(R.color.nulls);
		}
		int rightBackgroundID = a.getResourceId(R.styleable.TitleWidget_right_bg, 0);
		if(rightBackgroundID == 0) {
			//TODO
		} else {
			isNoRight = false;
			right.setBackgroundResource(rightBackgroundID);
		}
		int rightTextID = a.getResourceId(R.styleable.TitleWidget_right_text, 0);
		if(rightTextID == 0) {
			//TODO
		} else {
			isNoRight = false;
			right.setText(rightTextID);
		}
		if(isNoRight) {
			rightLayout.setBackgroundResource(R.color.nulls);
		}
		/*
		 * 标题初始化
		 * */
		int textID = a.getResourceId(R.styleable.TitleWidget_text, 0);
		if(textID == 0) {
			//TODO
		} else {
			title.setText(textID);
		}
		a.recycle();
	}
	
	/**
	 * 设置左边图标图像
	 * resid 资源ID
	 * */
	public void setLeftBackgroundResource(int resid) {
		left.setBackgroundResource(resid);
	}
	
	/**
	 * 设置左边图标图像
	 * drawable Drawable图像
	 * */
	public void setLeftBackgroundResource(Drawable drawable) {
		left.setBackgroundDrawable(drawable);
	}
	
	/**
	 * 设置左边图标图像
	 * resid 资源ID
	 * */
	public void setLeftText(int resid) {
		left.setText(resid);
	}
	
	/**
	 * 设置左边图标标题
	 * text 文本
	 * */
	public void setLeftText(CharSequence text) {
		left.setText(text);
	}
	
	/**
	 * 设置图标标题
	 * resid 资源ID
	 * */
	public void setText(int resid) {
		title.setText(resid);
	}
	
	/**
	 * 设置图标标题
	 * text 文本
	 * */
	public void setText(CharSequence text) {
		title.setText(text);
	}
	
	/**
	 * 设置右边图标图像
	 * resid 资源ID
	 * */
	public void setRightBackgroundResource(int resid) {
		right.setBackgroundResource(resid);
	}
	
	/**
	 * 设置右边图标图像
	 * drawable Drawable图像
	 * */
	public void setRightBackgroundResource(Drawable drawable) {
		right.setBackgroundDrawable(drawable);
	}
	
	/**
	 * 设置左边图标图像
	 * resid 资源ID
	 * */
	public void setRightText(int resid) {
		right.setText(resid);
	}
	
	/**
	 * 设置右边图标标题
	 * text 文本
	 * */
	public void setRightText(CharSequence text) {
		right.setText(text);
	}
	
	/**
	 * 设置左边图标点击事件监听器
	 * listener 点击事件监听器
	 * */
	public void setOnLeftClickListner(View.OnClickListener listener) {
		leftLayout.setOnClickListener(listener);
	}
	
	/**
	 * 设置右边图标点击事件监听器
	 * listener 点击事件监听器
	 * */
	public void setOnRightClickListner(View.OnClickListener listener) {
		rightLayout.setOnClickListener(listener);
	}
	
	public void setRightTextColor(int color) {
		right.setTextColor(color);
	}
	
	public void setRightVisibility(int visibility) {
		right.setVisibility(visibility);
	}

	/**
	 * 获取右边控件
	 */
	public TextView getRightView(){
		return right;
	}
}
