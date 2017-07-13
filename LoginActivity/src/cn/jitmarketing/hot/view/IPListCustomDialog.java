package cn.jitmarketing.hot.view;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.IPListAdapter;
import cn.jitmarketing.hot.util.ConstValue;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class IPListCustomDialog extends Dialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void selectIP(String ip);
	}

	private OnCustomDialogListener customDialogListener;
	private ListView listview;
	private List<String> ipList;
	IPListAdapter adapter;

	public IPListCustomDialog(Context context, List<String> ipList, OnCustomDialogListener customDialogListener) {
		super(context);
		this.ipList = ipList;
		this.customDialogListener = customDialogListener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
		adapter = new IPListAdapter((Activity)context, ipList);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_ip_list);
		// 设置标题
//		setTitle(name);
		listview = (ListView) findViewById(R.id.custom_ip_listview);
		listview.setAdapter(adapter);
		listview.setOnItemClickListener(itemClickListener);
		initConfig();
	}
	
	/**
	 * 初始化dialog的配置
	 */
	private void initConfig() {
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (ConstValue.SCREEN_WIDTH * 0.9);
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
	}

	OnItemClickListener itemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			customDialogListener.selectIP(ipList.get(position));	
			IPListCustomDialog.this.dismiss();
		}
	};

}