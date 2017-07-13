package cn.jitmarketing.hot.util;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import cn.jitmarketing.hot.R;

public class ShelfText extends LinearLayout implements TextWatcher{

	private EditText shelf_one;
	private EditText shelf_two;
	private EditText shelf_three;
	private EditText shelf_four;

	public ShelfText(Context context) {
		super(context);
	}

	public ShelfText(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater inflater=(LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.widget_shelf, this);
		
		shelf_one = (EditText) findViewById(R.id.shelf_one);
		shelf_two = (EditText) findViewById(R.id.shelf_two);
		shelf_three = (EditText) findViewById(R.id.shelf_three);
		shelf_four = (EditText) findViewById(R.id.shelf_four);
		
		shelf_one.addTextChangedListener(this);
		shelf_two.addTextChangedListener(this);
		shelf_three.addTextChangedListener(this);
		shelf_four.addTextChangedListener(this);
			
	}

	public String getText(){
		StringBuilder builder = new StringBuilder();
		builder.append(shelf_one.getText().toString());
		builder.append(shelf_two.getText().toString());
		builder.append(shelf_three.getText().toString());
		builder.append(shelf_four.getText().toString());
		return builder.toString();
	}
	
	public void setText(String shelfCode){
		if(shelfCode.length()>0){
		shelf_one.setText(shelfCode.substring(0, 1));
		shelf_two.setText(shelfCode.substring(1, 3));
		shelf_three.setText(shelfCode.substring(3, 4));
		shelf_four.setText(shelfCode.substring(4, 5));
		}else{
			shelf_one.setText("");
			shelf_two.setText("");
			shelf_three.setText("");
			shelf_four.setText("");
		}
	}
	@Override
	public void afterTextChanged(Editable s) {
    	
    	if (shelf_one.getText().length() == 0)
    		shelf_one.requestFocus();
    	else if (shelf_two.getText().length() == 0)
    		shelf_two.requestFocus();
    	else if (shelf_two.getText().length() >= 2 && shelf_three.getText().length() == 0)
    		shelf_three.requestFocus();
    	else if (shelf_two.getText().length() >= 2 && shelf_four.getText().length() == 0)
    		shelf_four.requestFocus();
    }

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

}
