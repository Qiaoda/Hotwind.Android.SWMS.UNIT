package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;


import android.R.integer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class NumberAmendView extends LinearLayout {
	
	private Button jiaButton;
	private Button jianButton;
	private EditText numEdit;

	public NumberAmendView(Context context) {
		this(context,null);
		// TODO Auto-generated constructor stub
	}
	public NumberAmendView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
		// TODO Auto-generated constructor stub
	}

	public NumberAmendView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}
    
	private void initView(Context context) {
		View view=LayoutInflater.from(context).inflate(R.layout.widget_number_amend, null);
		jiaButton=(Button) view.findViewById(R.id.jia_btn);
		jianButton=(Button) view.findViewById(R.id.jian_btn);
		numEdit=(EditText) view.findViewById(R.id.num_edit);
		numEdit.setSelection(numEdit.getText().toString().length());
		numEdit.setEnabled(false);
		jiaButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				setNum(v.getId());
			}
		});
		jianButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				setNum(v.getId());
			}
		});
		addView(view);
	}
	
	/**
	 * 取值
	 */
	public int getNum() {
		return Integer.valueOf(numEdit.getText().toString());
		
	}
	/**
	 * 输入框设值
	 * @param id
	 */
	private void setNum(int id) {
		int num=getNum();
		if (id==R.id.jia_btn) {
			num++;
		}else if (id==R.id.jian_btn&&num>0) {
			num--;
		}
		numEdit.setText(String.valueOf(num));
	}
	/**
     * 设值能否编辑
     * @param bool
     */
    public void setCanEnable(boolean bool){
    	numEdit.setEnabled(bool);
    	numEdit.setSelection(numEdit.getText().toString().length());

    }
    /**输入框设入初始值*/
    public void setStartNum(int num){
    	numEdit.setText(String.valueOf(num));

    }

}
