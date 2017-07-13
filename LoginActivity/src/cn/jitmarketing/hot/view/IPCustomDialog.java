package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.util.ConstValue;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class IPCustomDialog extends Dialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void addIP(String name, String ip, String port, boolean isNormal);
	}

	private Context context;
	private OnCustomDialogListener customDialogListener;
	private IPEditText ipEdt;
	private CheckBox ipNormalUseCkb;
	private ClearEditText portEditText;
	private ClearEditText nameEditText;

	public IPCustomDialog(Context context, OnCustomDialogListener customDialogListener) {
		super(context);
		this.context = context;
		this.customDialogListener = customDialogListener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_ip);
		// 设置标题
//		setTitle(name);
		nameEditText = (ClearEditText) findViewById(R.id.dialog_name_edt);
		ipEdt = (IPEditText) findViewById(R.id.dialog_ip_edt); 
		ipNormalUseCkb = (CheckBox) findViewById(R.id.dialog_ip_normal_chb);
		portEditText = (ClearEditText) findViewById(R.id.dialog_port_edt);
		Button confirmBtn = (Button) findViewById(R.id.dialog_confirm_btn);
		Button cancelBtn = (Button) findViewById(R.id.dialog_cancel_btn);
		confirmBtn.setOnClickListener(clickListener);
		cancelBtn.setOnClickListener(clickListener);
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
			if(v.getId() == R.id.dialog_confirm_btn) {
				if(!ipEdt.invalid()) {
					Toast.makeText(context, "请输入合法的ip地址", Toast.LENGTH_LONG).show();
					return;
				}
				if(nameEditText.getText() == null || nameEditText.getText().toString().trim().equals("")) {
					Toast.makeText(context, "请输入地址名称", Toast.LENGTH_LONG).show();
					return;
				}
 				customDialogListener.addIP(String.valueOf(nameEditText.getText()), String.valueOf(ipEdt.getText(context)),
 						String.valueOf(portEditText.getText()), ipNormalUseCkb.isChecked());
				IPCustomDialog.this.dismiss();
			} else if(v.getId() == R.id.dialog_cancel_btn) {
				IPCustomDialog.this.dismiss();
			}
		}
	};

}