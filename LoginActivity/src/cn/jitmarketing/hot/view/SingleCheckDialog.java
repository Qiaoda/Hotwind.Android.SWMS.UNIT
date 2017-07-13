package cn.jitmarketing.hot.view;

import java.util.ArrayList;
import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.SingleSelectListAdapter;
import cn.jitmarketing.hot.entity.UnitBeanOne;
import cn.jitmarketing.hot.util.ConstValue;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ListView;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class SingleCheckDialog extends Dialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void upload(UnitBeanOne selectUnit);
	}

	private Context context;
	private OnCustomDialogListener customDialogListener;
	private ListView listview;
	private List<UnitBeanOne> unitList;
	private List<UnitBeanOne> showUnitList = new ArrayList<UnitBeanOne>();
	private TextView single_select_title;
	private ClearEditText unit_search_edt;
	private Button dialog_cancle;
	private Button dialog_sure;
	private SingleSelectListAdapter adapter;

	public SingleCheckDialog(Context context, List<UnitBeanOne> unitList, OnCustomDialogListener customDialogListener) {
		super(context);
		this.context = context;
		this.unitList = unitList;
		showUnitList.addAll(unitList);
		this.customDialogListener = customDialogListener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_single_check);
		// 设置标题
//		setTitle(name);
		adapter = new SingleSelectListAdapter(context, showUnitList);
		single_select_title = (TextView) findViewById(R.id.single_select_title);
		unit_search_edt = (ClearEditText) findViewById(R.id.unit_search_edt);
		dialog_cancle = (Button) findViewById(R.id.dialog_cancle);
		dialog_sure = (Button) findViewById(R.id.dialog_sure);
		listview = (ListView) findViewById(R.id.select_unit_listview);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(adapter);
		dialog_cancle.setOnClickListener(clickListener);
		dialog_sure.setOnClickListener(clickListener);
		unit_search_edt.addTextChangedListener(textWatcher);
		initConfig();
	}
	
	/**
	 * 初始化dialog的配置
	 */
	private void initConfig() {
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (ConstValue.SCREEN_WIDTH * 0.95);
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}
	
	View.OnClickListener clickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v == dialog_cancle) {
				dismiss();
			} else if(v == dialog_sure) {
				int selItem = adapter.getSelectItem();
				if(showUnitList.size()!=0){
					customDialogListener.upload(showUnitList.get(selItem));//	
				}
				dismiss();
			}
		}
	};
	
	TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			if(arg0 != null) {
				showUnitList.clear();
				adapter.setSelectItem();
				if(arg0.toString().trim().equals("")) {
					showUnitList.addAll(unitList);
				} else {
					for(UnitBeanOne bean : unitList) {
						if(bean.unitName.contains(arg0) || bean.unitID.contains(arg0)) {
							showUnitList.add(bean);
						}
					}
					if(showUnitList.size()==0){
						dialog_sure.setClickable(false);
						dialog_sure.setEnabled(false);
						}else{
							dialog_sure.setClickable(true);
							dialog_sure.setEnabled(true);
						}
				}
				adapter.notifyDataSetChanged();
			}
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
	};

}