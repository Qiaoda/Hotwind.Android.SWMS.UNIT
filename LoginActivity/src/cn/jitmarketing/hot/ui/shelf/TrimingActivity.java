package cn.jitmarketing.hot.ui.shelf;

import java.util.Map;

import android.content.Intent;
import android.text.Editable;
import android.text.LoginFilter;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.AddAndSubView;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.SkuEditText;

public class TrimingActivity extends BaseSwipeOperationActivity implements OnClickListener {
	@ViewInject(R.id.triming_layout)
	LinearLayout triming_layout;
	@ViewInject(R.id.triming_location)
	SkuEditText triming_location;
	@ViewInject(R.id.triming_sku)
	ClearEditText triming_sku;
	@ViewInject(R.id.triming_cancle)
	TextView triming_cancle;
	@ViewInject(R.id.triming_sure)
	TextView triming_sure;
	@ViewInject(R.id.jian_btn)
	Button jian_btn;
	@ViewInject(R.id.jia_btn)
	Button jia_btn;
	@ViewInject(R.id.num_edit)
	EditText num_edit;
	@ViewInject(R.id.one_radio)
	RadioButton one_radio;
	@ViewInject(R.id.two_radio)
	RadioButton two_radio;
	@ViewInject(R.id.edit)
	EditText edit;

	private static final int WHAT_NET_triming = 0x10;
	AddAndSubView addAndSubView1;
	HotApplication ap;
	String location;
	String sku;
	String changeQry;
	String skuCodeStr;
	int num = 0;
	String mText1;
	// 防止双击事件记时
	private long lastClickTime = 0;

	@Override
	protected void exInitAfter() {
		super.exInitAfter();
		ap = (HotApplication) getApplication();
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_triming;
	}

	@Override
	public void onReceiver(Intent intent) {
		// byte[] barcode = intent.getByteArrayExtra("barocode");
		// int barocodelen = intent.getIntExtra("length", 0);
		// skuCodeStr = new String(barcode, 0,
		// barocodelen).toUpperCase().trim();
		// dealBarCode(skuCodeStr);

		// if(barcode != null)
		// skuCodeStr = new String(barcode, 0,
		// barocodelen).toUpperCase().trim();
		// else {
		// ap.getsoundPool(ap.Sound_error);
		// return;
		// }
		// if (SkuUtil.isWarehouse(skuCodeStr)) {
		// ap.getsoundPool(ap.Sound_location);
		// triming_location.setText(skuCodeStr);
		// }else{
		// ap.getsoundPool(ap.Sound_sku);
		// triming_sku.setText(skuCodeStr);
		// }
	}

	// @Override
	// public void fillCode(String code) {
	// skuCodeStr = code;
	// dealBarCode(code);
	// }

	private void dealBarCode(String skuCodeStr) {
		if (SkuUtil.isWarehouse(skuCodeStr)) {
			ap.getsoundPool(ap.Sound_location);
			triming_location.setText(skuCodeStr);
		} else {
			ap.getsoundPool(ap.Sound_sku);
			triming_sku.setText(skuCodeStr);
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_triming:
			int type = 1;
			// if(one_radio.isChecked()) {
			// type = 1;
			// }
			// if(two_radio.isChecked()) {
			// type = 2;
			// }
			return WarehouseNet.trimingHistory(sku, changeQry, location, type, edit.getText() == null ? null : edit.getText().toString());
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		// 操作日志
		LogUtils.logOnFile("调整单->添加" + message);
		switch (what) {
		case WHAT_NET_triming:
			Ex.Toast(mContext).showLong("你的网速不太好,获取失败");
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// 操作日志
		LogUtils.logOnFile("调整单->添加" + result);
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			// 操作日志
			LogUtils.logOnFile("调整单->添加" + net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_triming:
			Ex.Toast(mActivity).show(net.message);
			// 操作日志
			LogUtils.logOnFile("调整单->添加" + net.message);
			TrimActivity.isfinish = true;
			this.finish();
			break;
		}
	}

	@Override
	protected void exInitView() {
		// InputFilter[] filters = new InputFilter[1];
		// filters[0] = new MyInputFilter("-123456789");
		// triming_sku.setFilters(filters);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), LayoutParams.WRAP_CONTENT);
		triming_layout.setLayoutParams(params);
		triming_cancle.setOnClickListener(this);
		triming_sure.setOnClickListener(this);
		jian_btn.setOnClickListener(this);
		jia_btn.setOnClickListener(this);
		triming_sku.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				triming_sku.removeTextChangedListener(this);// 解除文字改变事件
				triming_sku.setText(s.toString().toUpperCase());// 转换
				triming_sku.setSelection(s.toString().length());// 重新设置光标位置
				triming_sku.addTextChangedListener(this);// 重新绑
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		num_edit.setText("0");
		num_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if (s != null) {
					if (s.toString().equals("0-")) {
						num_edit.setText("-");
						num_edit.setSelection(1);
					} else {
						if (!s.toString().equals("-") && !s.toString().equals("")) {
							try {
								num = Integer.valueOf(s.toString());
								if (s.toString().length() > 1 && s.toString().startsWith("0")) {
									num_edit.setText("" + num);
									num_edit.setSelection(num_edit.getText().toString().length());
								}
							} catch (Exception e) {
								num = 0;
								num_edit.setText("0");
								num_edit.setSelection(1);
							}
						}
					}

				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
		LinearLayout linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
		addAndSubView1 = new AddAndSubView(TrimingActivity.this);
		// linearLayout1.addView(addAndSubView1);
		// 限制特殊字符
		edit.addTextChangedListener(new TextWatcher() {
			String tmp = "";
			String digits ="[,.。，、？]"; 

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				edit.setSelection(s.length());

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				tmp = s.toString();

			}

			@Override
			public void afterTextChanged(Editable s) {
				String str = s.toString();
				if (str.equals(tmp)) {
					return;
				}
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < str.length(); i++) {
					if (String.valueOf(str.charAt(i)).matches("[\u4e00-\u9fa5]+")||String.valueOf(str.charAt(i)).matches("[a-zA-Z0-9 /]+")
							||digits.indexOf(str.charAt(i))>=0) {
						if (str.length()>300) {
							Toast.makeText(mActivity, "文字过多", Toast.LENGTH_SHORT).show();
						}else{
							sb.append(str.charAt(i));	
						}
						
					}
				}
				tmp = sb.toString();
				edit.setText(tmp);
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.triming_cancle:
			// 日志操作
			LogUtils.logOnFile("调整单->添加->取消");
			finish();
			break;
		case R.id.triming_sure:
			// 防止双击操作
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastClickTime > 1000) {
				lastClickTime = currentTime;
				// 执行事件
				location = triming_location.getText(this).toString().toUpperCase();
				sku = triming_sku.getText().toString().toUpperCase();
				if (num_edit.getText().toString().equals("-") || num_edit.getText().toString().equals("")) {
					Ex.Toast(mActivity).show("请输入正确数量");
					return;
				}
				if (num_edit.getText().toString().equals("0")) {
					Ex.Toast(mActivity).show("调整数量不能为0");
					return;
				}
				changeQry = String.valueOf(num);
				// changeQry = String.valueOf(addAndSubView1.getNum());
				if (sku.trim().equals("")) {
					Ex.Toast(mActivity).show("请输入SKU");
					return;
				}
				if (changeQry.equals("")) {
					Ex.Toast(mActivity).show("请输入数量");
					return;
				}
				if (location.equals("")) {
					Ex.Toast(mActivity).show("请输入库位");
					return;
				}
				if (!triming_location.invalid()) {
					Ex.Toast(mActivity).show(getResources().getString(R.string.validat_sku));
					return;
				}
				// 日志操作
				LogUtils.logOnFile("调整单->添加->确定--Sku=" + sku + ";数量=" + changeQry + ";库位=" + location + ";备注=" + edit.getText().toString());
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.TrimSave, WHAT_NET_triming, NET_METHOD_POST, false);
			}

			break;
		case R.id.jian_btn:
			// 日志操作
			LogUtils.logOnFile("调整单->添加->减1");
			if (num_edit.getText() == null || num_edit.getText().toString().equals("") || num_edit.getText().toString().equals("-")) {
				num = -1;
			} else {
				num = Integer.valueOf(num_edit.getText().toString()) - 1;
			}
			num_edit.setText(String.valueOf(num));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		case R.id.jia_btn:
			// 日志操作
			LogUtils.logOnFile("调整单->添加->加1");
			if (num_edit.getText() == null || num_edit.getText().toString().equals("") || num_edit.getText().toString().equals("-")) {
				num = 1;
			} else {
				num = Integer.valueOf(num_edit.getText().toString()) + 1;
			}
			num_edit.setText(String.valueOf(num));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		}
	}

	public class MyInputFilter extends LoginFilter.UsernameFilterGeneric {

		private String mAllowedDigits;

		public MyInputFilter(String digits) {
			mAllowedDigits = digits;
		}

		@Override
		public boolean isAllowed(char c) {
			if (mAllowedDigits.indexOf(c) != -1) {
				return true;
			}
			return false;
		}
	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		dealBarCode(code);
		// 日志操作
		LogUtils.logOnFile("调整单->添加->扫码：" + code);
	}
}
