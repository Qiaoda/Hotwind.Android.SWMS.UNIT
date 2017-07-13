package cn.jitmarketing.hot.view;

import com.ex.lib.core.utils.Ex;

import cn.jitmarketing.hot.MainActivity;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.HotConstants.Global;
import cn.jitmarketing.hot.service.NoticeService;
import cn.jitmarketing.hot.service.UpdateService;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * 自定义dialog
 * 
 * @author Mr.Xu
 * 
 */
public class StockCustomDialog extends Dialog {
	
	// 定义回调事件，用于dialog的点击事件
	public interface OnCustomDialogListener {
		public void isReset(boolean reset);
	}

	private Context context;
	private OnCustomDialogListener customDialogListener;
	private TextView dif_money_txt, dif_count_txt;
	private String diffrenceCount;
	private String differenceMoney;

	public StockCustomDialog(Context context, OnCustomDialogListener customDialogListener, String diffrenceCount, String differenceMoney) {
		super(context);
		this.context = context;
		this.diffrenceCount = diffrenceCount;
		this.differenceMoney = differenceMoney;
		this.customDialogListener = customDialogListener;
		requestWindowFeature(Window.FEATURE_NO_TITLE);//dialog去掉标题
		setCancelable(false);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_stock);
		// 设置标题
//		setTitle(name);
		dif_money_txt = (TextView) findViewById(R.id.dif_money_txt); 
		dif_count_txt = (TextView) findViewById(R.id.dif_count_txt); 
		Button confirmBtn = (Button) findViewById(R.id.dialog_confirm_btn);
		Button cancelBtn = (Button) findViewById(R.id.dialog_cancel_btn);
		confirmBtn.setOnClickListener(clickListener);
		cancelBtn.setOnClickListener(clickListener);
		dif_count_txt.setText(diffrenceCount);
		dif_money_txt.setText(differenceMoney);
		initConfig();
	}
	
	/**
	 * 初始化dialog的配置
	 */
	private void initConfig() {
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (ConstValue.SCREEN_WIDTH * 0.95);
		lp.height = (int) (ConstValue.SCREEN_HEIGHT * 0.5);
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
				//日志操作
				LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->重盘");
				new AlertDialog.Builder(context)
				.setMessage("确定重盘吗？")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//日志操作
						LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->重盘->确定");
						customDialogListener.isReset(true);
						StockCustomDialog.this.dismiss();
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						//日志操作
						LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->重盘->取消");
					}
				}).show();
			} else if(v.getId() == R.id.dialog_cancel_btn) {
				//日志操作
				LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->不重盘");
				new AlertDialog.Builder(context)
				.setMessage("不重盘将不能再次进入此库位盘点，确定不重盘吗？")
				.setPositiveButton(R.string.sureTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int whichButton) {
						//日志操作
						LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->不重盘->确定");
						customDialogListener.isReset(false);
					}
				})
				.setNegativeButton(R.string.cancelTitle,
						new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						//日志操作
						LogUtils.logOnFile("盘点->库位盘点->确定->盘点差异->不重盘->取消");
					}
				}).show();
			}
		}
	};

}