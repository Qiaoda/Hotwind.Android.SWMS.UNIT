package cn.jitmarketing.hot.view;

import cn.jitmarketing.hot.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class TipDialog extends Dialog implements android.view.View.OnClickListener {

	/* 上下文 */
	private Context context;
	/* 提示内容 */
	private String message;

	private DialogSureClickListener dialogSureClickListener;
	private TextView tipMessage;

	private TextView d_btn_sure, d_btn_cancel;

	public TipDialog(Context context, String message) {
		super(context, R.style.share_dialog);
		this.context = context;
		this.message = message;
	}

	public void setDialogSureClickListener(DialogSureClickListener dialogSureClickListener) {
		this.dialogSureClickListener = dialogSureClickListener;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_tip);
		tipMessage = (TextView) findViewById(R.id.tip_message);
		d_btn_sure = (TextView) findViewById(R.id.d_btn_sure);
		d_btn_cancel = (TextView) findViewById(R.id.d_btn_cancel);
		d_btn_sure.setOnClickListener(this);
		d_btn_cancel.setOnClickListener(this);
		tipMessage.setText(message);

		/* 设置窗体大小 */
		Window dialogWindow = getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		lp.width = (int) (getScreenWidth(context) * 0.95);
		lp.height = LayoutParams.WRAP_CONTENT;
		dialogWindow.setGravity(Gravity.CENTER);
		dialogWindow.setAttributes(lp);
		// /*点击其它位置不消失*/
		// this.setCancelable(false);
	}

	/**
	 * 获得屏幕宽度
	 * 
	 * @return
	 */
	public static int getScreenWidth(Context context) {
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		return outMetrics.widthPixels;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.d_btn_sure:
			if (dialogSureClickListener != null) {
				dialogSureClickListener.sureClick();
			}
			break;
		case R.id.d_btn_cancel:
			dismiss();
			break;

		default:
			break;
		}

	}

	public interface DialogSureClickListener {

		void sureClick();

	}
}
