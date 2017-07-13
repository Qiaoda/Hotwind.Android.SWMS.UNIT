package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class MoreCustomDialog extends Dialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void delete();
		public void confirm(int num);
	}

	private Context context;
	private OnCustomDialogListener customDialogListener;
	private Button jian_btn, jia_btn, delete_btn, cancel_btn, confirm_btn;
	private EditText num_edit;
	private int initNum;
	
	public MoreCustomDialog(Context context, int initNum, OnCustomDialogListener customDialogListener) {
		super(context);
		this.context = context;
		this.customDialogListener = customDialogListener;
		this.initNum = initNum;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_more);
		// 设置标题
//		setTitle(name);
		jian_btn = (Button) findViewById(R.id.jian_btn);
		jia_btn = (Button) findViewById(R.id.jia_btn);
		delete_btn = (Button) findViewById(R.id.delete_btn);
		cancel_btn = (Button) findViewById(R.id.cancel_btn);
		confirm_btn = (Button) findViewById(R.id.confirm_btn);
		num_edit = (EditText) findViewById(R.id.num_edit);
		jian_btn.setOnClickListener(clickListener);
		jia_btn.setOnClickListener(clickListener);
		delete_btn.setOnClickListener(clickListener);
		cancel_btn.setOnClickListener(clickListener);
		confirm_btn.setOnClickListener(clickListener);
		num_edit.setText(String.valueOf(initNum));
		num_edit.setSelection(String.valueOf(initNum).length());
		num_edit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if(s != null) {
					int num = 0;
					if(s.toString().equals("")) {
//						num_edit.setText("1");
//						num_edit.setSelection(1);
					} else if(s.toString().equals("0")) {
//						num_edit.setText("1");
//						num_edit.setSelection(1);
					} else {
						if(s.toString().startsWith("0") && s.toString().length() > 1) {
							num = Integer.valueOf(s.toString());
							num_edit.setText(num+"");
							num_edit.setSelection(s.toString().length()-1);
						} else {
							num_edit.setSelection(num_edit.getText().toString().length());
						}
					}
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
		});
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

	/**
	 * 点击事件
	 * 
	 */
	private View.OnClickListener clickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v.getId() == R.id.delete_btn) {
				new AlertDialog.Builder(context)
				.setMessage("确定删除？")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//日志操作
						LogUtils.logOnFile("~->明细->数量弹窗->删除->确定");
						customDialogListener.delete();
						MoreCustomDialog.this.dismiss();
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						//日志操作
						LogUtils.logOnFile("~->明细->数量弹窗->删除->取消");
					}
				}).show();
			} else if(v.getId() == R.id.cancel_btn) {
				//日志操作
				LogUtils.logOnFile("~->明细->数量弹窗->取消");
				MoreCustomDialog.this.dismiss();
			} else if(v.getId() == R.id.confirm_btn) {
				
				if(num_edit.getText().toString().equals("")) {
					Toast.makeText(context, "请输入具体数字", Toast.LENGTH_LONG).show();
					return;
				}
				if(Integer.valueOf(num_edit.getText().toString()) < 1) {
					Toast.makeText(context, "数量必须大于0", Toast.LENGTH_LONG).show();
					return;
				}
				customDialogListener.confirm(Integer.valueOf(num_edit.getText().toString()));
				//日志操作
				LogUtils.logOnFile("~->明细->数量弹窗->确定--数量="+num_edit.getText().toString());
				MoreCustomDialog.this.dismiss();
			} else if(v.getId() == R.id.jian_btn) {
				//日志操作
				LogUtils.logOnFile("~->明细->数量弹窗->减1");
				num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) - 1));
			} else if(v.getId() == R.id.jia_btn) {
				//日志操作
				LogUtils.logOnFile("~->明细->数量弹窗->加1");
				num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) + 1));
			}
		}
	};

}