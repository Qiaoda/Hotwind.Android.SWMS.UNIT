package cn.jitmarketing.hot.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.service.GroupEntity;
import cn.jitmarketing.hot.service.NoticeSettingEntity;
import cn.jitmarketing.hot.util.ConstValue;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class SelectCustomDialog extends Dialog {
	
	private CheckBox cb1,cb2,cb3,cb4,cb5,cb6,cb7;
	Button confirm_btn;
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void change(int buttonId, boolean isChecked);
		public void confirm();
	}

	private Context context;
	private OnCustomDialogListener customDialogListener;
	ArrayList<GroupEntity> groupList;
	
	public SelectCustomDialog(Context context, ArrayList<GroupEntity> groupList, OnCustomDialogListener customDialogListener) {
		super(context);
		this.context = context;
		this.customDialogListener = customDialogListener;
		this.groupList = groupList;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.popwindow);
		confirm_btn = (Button) findViewById(R.id.confirm_btn);
		cb1=(CheckBox) findViewById(R.id.cb1);
		cb2=(CheckBox) findViewById(R.id.cb2);
		cb3=(CheckBox) findViewById(R.id.cb3);
		cb4=(CheckBox) findViewById(R.id.cb4);
		cb5=(CheckBox) findViewById(R.id.cb5);
		cb6=(CheckBox) findViewById(R.id.cb6);
		cb7=(CheckBox) findViewById(R.id.cb7);
		for(GroupEntity group : groupList) {
			for(NoticeSettingEntity entity : group.getSublist()) {
				if(entity.getShaixuanId() == 1) {
					cb1.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 2) {
					cb2.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 3) {
					cb3.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 4) {
					cb4.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 5) {
					cb5.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 6) {
					cb6.setChecked(entity.getOpen());
				} else if(entity.getShaixuanId() == 7) {
					cb7.setChecked(entity.getOpen());
				}
			}
		}
		cb1.setOnCheckedChangeListener(changeListener);
		cb2.setOnCheckedChangeListener(changeListener);
		cb3.setOnCheckedChangeListener(changeListener);
		cb4.setOnCheckedChangeListener(changeListener);
		cb5.setOnCheckedChangeListener(changeListener);
		cb6.setOnCheckedChangeListener(changeListener);
		cb7.setOnCheckedChangeListener(changeListener);
		confirm_btn.setOnClickListener(clickListener);
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
	private OnCheckedChangeListener changeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton button, boolean isChecked) {
			customDialogListener.change(button.getId(), isChecked);
		}
    	
    };

    private View.OnClickListener clickListener = new  View.OnClickListener() {

		@Override
		public void onClick(View v) {
			dismiss();
			customDialogListener.confirm();
		}
    	
    };
}