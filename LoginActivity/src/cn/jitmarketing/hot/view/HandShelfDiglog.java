package cn.jitmarketing.hot.view;

import com.ex.lib.core.utils.Ex;

import cn.jitmarketing.hot.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HandShelfDiglog extends DialogFragment {
	LinearLayout hand_shelf_layout;
	SkuEditText hand_shelf_location;
	ClearEditText hand_shelf_sku;
	ClearEditText hand_shelf_count;
	TextView hand_shelf_cancle;
	TextView hand_shelf_sure;
    public interface getNo{
    	void cancle();
    }
	public interface getInfo {
		void getInfoComplete(String location, String sku, int count);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);  
		View view = inflater.inflate(R.layout.activity_hand_shelf, container);  
		hand_shelf_location = (SkuEditText) view
				.findViewById(R.id.hand_shelf_location);
		hand_shelf_sku = (ClearEditText) view.findViewById(R.id.hand_shelf_sku);
		hand_shelf_count = (ClearEditText) view
				.findViewById(R.id.hand_shelf_count);
		hand_shelf_cancle = (TextView) view
				.findViewById(R.id.hand_shelf_cancle);
		hand_shelf_sure = (TextView) view.findViewById(R.id.hand_shelf_sure);
		hand_shelf_cancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getNo no=(getNo) getActivity();
				no.cancle();
			}
		});
		hand_shelf_sure.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getInfo info=(getInfo) getActivity();
				if(hand_shelf_location.getText(getActivity()).toString().toUpperCase().equals("")){
					Ex.Toast(getActivity()).show("请输入库位");;
					return;
				}
				if(hand_shelf_sku.getText().toString().equals("")){
					Ex.Toast(getActivity()).show("请输入SKU");;
					return;
				}
				if(hand_shelf_count.getText().toString().equals("")){
					Ex.Toast(getActivity()).show("请输入数量");;
					return;
				}
				info.getInfoComplete(hand_shelf_location.getText(getActivity()).toString().toUpperCase()
						, hand_shelf_sku.getText().toString(), Integer.valueOf(hand_shelf_count.getText().toString()));
			}
		});
		return view;
	}
}
