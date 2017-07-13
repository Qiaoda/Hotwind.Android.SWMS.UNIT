package cn.jitmarketing.hot.ui.shelf;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;

public class HandMoveActivity extends BaseSwipeBackAcvitiy implements OnClickListener{
	private SkuEditText text_shelf_old;
	private SkuEditText text_shelf_new;
	private ClearEditText hand_move_sku;
	private ClearEditText hand_move_count;
	private TextView hand_move_cancle;
	private TextView hand_move_sure;
	private LinearLayout hand_move_layout;
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		setFinishOnTouchOutside(false);
		return R.layout.activity_hand_move;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exInitView() {
		// TODO Auto-generated method stub
		hand_move_layout=(LinearLayout) findViewById(R.id.hand_move_layout);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),LayoutParams.WRAP_CONTENT);
		hand_move_layout.setLayoutParams(params);
		hand_move_sku=(cn.jitmarketing.hot.view.ClearEditText) findViewById(R.id.hand_move_sku);
		hand_move_count=(cn.jitmarketing.hot.view.ClearEditText) findViewById(R.id.hand_move_count);
		hand_move_cancle=(TextView) findViewById(R.id.hand_move_cancle);
		hand_move_sure=(TextView) findViewById(R.id.hand_move_sure);
		text_shelf_old=(SkuEditText) findViewById(R.id.text_shelf_old);
		text_shelf_new=(SkuEditText) findViewById(R.id.text_shelf_new);
		hand_move_cancle.setOnClickListener(this);
		hand_move_sure.setOnClickListener(this);
		hand_move_sku.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				hand_move_sku.removeTextChangedListener(this);//解除文字改变事件 
				hand_move_sku.setText(s.toString().toUpperCase());//转换 
				hand_move_sku.setSelection(s.toString().length());//重新设置光标位置 
				hand_move_sku.addTextChangedListener(this);//重新绑 
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.hand_move_cancle:
			this.finish();
			break;
		case R.id.hand_move_sure:
			if (!text_shelf_old.invalid()) {
				Ex.Toast(mContext).show(getResources().getString(R.string.validat_sku));
				return;
			}
			String old=text_shelf_old.getText(this).toString();
			String news=text_shelf_new.getText(this).toString();
			String sku=hand_move_sku.getText().toString().toUpperCase();
			String c=hand_move_count.getText().toString();
			if(old.equals("")){
				Ex.Toast(mContext).show("请输入原库位");
				return;
			}
			if(sku.equals("")){
				Ex.Toast(mContext).show("请输入SKU");
				return;
			}
			if(c.equals("")){
				Ex.Toast(mContext).show("请输入数量");
				return;
			}
			if (!text_shelf_new.invalid()) {
				Ex.Toast(mContext).show(getResources().getString(R.string.validat_sku));
				return;
			}
			if(news.equals("")){
				Ex.Toast(mContext).show("请输入新库位");
				return;
			}
			SkuBean sb=new SkuBean(sku, Integer.valueOf(c));
			Bundle bundle=new Bundle();
		    bundle.putSerializable("sbBean", sb);
		    bundle.putString("old", old);
		    bundle.putString("news", news);
		    Intent intent=new Intent();
		    intent.putExtra("bundle", bundle);
		    setResult(RESULT_OK, intent);
		    this.finish();
			break;
		}
	}
}
