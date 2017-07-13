package cn.jitmarketing.hot.ui.sku;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import java.util.Map;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SampleEntity;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;
import cn.jitmarketing.hot.util.LogUtils;

public class SendRequestDialog extends BaseSwipeBackAcvitiy implements OnClickListener {

	@ViewInject(R.id.sku_txt)
	TextView sku_txt;
	@ViewInject(R.id.color_txt)
	TextView color_txt;
	@ViewInject(R.id.size_txt)
	TextView size_txt;
	@ViewInject(R.id.default_num_txt)
	TextView default_num_txt;
	@ViewInject(R.id.jian_btn)
	Button jian_btn;
	@ViewInject(R.id.num_edit)
	EditText num_edit;
	@ViewInject(R.id.jia_btn)
	Button jia_btn;
	@ViewInject(R.id.send_req_cancel)
	TextView send_req_cancel;
	@ViewInject(R.id.send_req_ok)
	TextView send_req_ok;
	@ViewInject(R.id.more_layout)
	LinearLayout more_layout;

	private static final int WHAT_NET_GET_OTHER = 0x12;
	private static final int WHAT_NET_GET_SAMPLE = 0x13;
	private static final int WHAT_NET_CHANGE_SAMPLE = 0x14;
	private static final int WHAT_NET_REVOIKE_SAMPLE = 0x15;
	private static final int WHAT_NET_GET_BATCH_SAMPLE = 0x16;// 批量出样
	private static final int WHAT_NET_REVOIKE_BATCH_SAMPLE = 0x17;// 批量撤样

	private SampleEntity sampleEntity;// 其中的属性type,1是取另一只，2是取样，3是换样，4是撤样
	private String skuCodeStr;
	private SkuEntity sku;
	private String SKUCount;

	@Override
	protected int exInitLayout() {
		return R.layout.send_req_dialog;
	}

	@Override
	protected void exInitView() {
		Bundle bd = getIntent().getExtras();
		sampleEntity = (SampleEntity) bd.getSerializable("sampleEntity");
		skuCodeStr = bd.getString("skuCodeStr");
		sku = (SkuEntity) bd.getSerializable("sku");
		SKUCount = sampleEntity.EndQty;
		sku_txt.setText(sku.SKUCode);
		color_txt.setText(sku.ColorName);
		size_txt.setText(sku.SizeName);
		if (sampleEntity.isCanBatch) {
			more_layout.setVisibility(View.VISIBLE);
		} else {
			more_layout.setVisibility(View.GONE);
			default_num_txt.setVisibility(View.VISIBLE);
		}
		send_req_cancel.setOnClickListener(this);
		send_req_ok.setOnClickListener(this);
		jian_btn.setOnClickListener(this);
		jia_btn.setOnClickListener(this);
		num_edit.setText("1");
		num_edit.setSelection(1);
		num_edit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				if (s != null) {
					int num = 1;
					if (s.toString().equals("")) {
						// num_edit.setText("1");
						// num_edit.setSelection(1);
					} else if (s.toString().equals("0")) {
						// num_edit.setText("1");
						// num_edit.setSelection(1);
					} else {
						if (s.toString().startsWith("0") && s.toString().length() > 1) {
							num = Integer.valueOf(s.toString());
							num_edit.setText(num + "");
							num_edit.setSelection(s.toString().length() - 1);
						} else {
							num_edit.setSelection(num_edit.getText().toString().length());
						}
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
	}

	@Override
	protected void exInitAfter() {
		initIble(this, this);
	}

	@Override
	public void onError(int what, int e, String message) {
		CheckStockActivity.sampleSuccess = true;
		Ex.Toast(mContext).showLong("你的网速不太好,获取数据失败");
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		super.onSuccess(what, result, hashCache);
		CheckStockActivity.sampleSuccess = true;
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			this.finish();
			return;
		}
		switch (what) {
		case WHAT_NET_GET_SAMPLE:
		case WHAT_NET_GET_OTHER:
		case WHAT_NET_CHANGE_SAMPLE:
		case WHAT_NET_REVOIKE_SAMPLE:
		case WHAT_NET_GET_BATCH_SAMPLE:
		case WHAT_NET_REVOIKE_BATCH_SAMPLE:
			LogUtils.logOnFile("->取新成功");
			Ex.Toast(mActivity).show(net.message);
			this.finish();
			break;
		}
	}
   
	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_OTHER:
			return SkuNet.getOther(skuCodeStr, SKUCount);
		case WHAT_NET_GET_SAMPLE:
			return SkuNet.getSample(skuCodeStr, SKUCount);
		case WHAT_NET_CHANGE_SAMPLE:
			return SkuNet.getChangeSample(skuCodeStr, SKUCount);
		case WHAT_NET_REVOIKE_SAMPLE:
			return SkuNet.getRevokeSample(skuCodeStr, SKUCount);
		case WHAT_NET_GET_BATCH_SAMPLE:
			return SkuNet.getSample(skuCodeStr, num_edit.getText().toString());
		case WHAT_NET_REVOIKE_BATCH_SAMPLE:
			return SkuNet.getRevokeSample(skuCodeStr, num_edit.getText().toString());
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.send_req_cancel:
			this.finish();
			break;
		case R.id.send_req_ok:
			if (FastDoubleClickUtil.isFastDoubleClick()) {
				return;
			}
			if (num_edit.getText().toString().equals("")) {
				Toast.makeText(this, "请输入具体数字", Toast.LENGTH_LONG).show();
				return;
			}
			if (num_edit.getText().toString().equals("0")) {
				Toast.makeText(this, "数量不能为0", Toast.LENGTH_LONG).show();
				return;
			}
			// if(Integer.valueOf(num_edit.getText().toString()) > maxNum) {
			// Toast.makeText(context, "数量最大为" + maxNum,
			// Toast.LENGTH_LONG).show();
			// return;
			// }
			/** 取新 */
			if (sampleEntity.Type.equals("1")) {
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetOther, WHAT_NET_GET_OTHER, NET_METHOD_POST, false);
				/** 出样 */
			} else if (sampleEntity.Type.equals("2")) {
				// 批量出样
				if (sampleEntity.isCanBatch) {
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetBatchSample, WHAT_NET_GET_BATCH_SAMPLE, NET_METHOD_POST, false);
				}
				// 单个出样
				else {
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetSample, WHAT_NET_GET_SAMPLE, NET_METHOD_POST, false);
				}
				/** 换样 */
			} else if (sampleEntity.Type.equals("3")) {
				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.CHANGE_SAMPLE, WHAT_NET_CHANGE_SAMPLE, NET_METHOD_POST, false);
			}
			/** 撤样 */
			else if (sampleEntity.Type.equals("4")) {
				// 批量撤样
				if (sampleEntity.isCanBatch) {
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.BatchRevokeSample, WHAT_NET_REVOIKE_BATCH_SAMPLE, NET_METHOD_POST, false);
				}
				// 单个撤样
				else {
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.REVOKE_SAMPLE, WHAT_NET_REVOIKE_SAMPLE, NET_METHOD_POST, false);
				}
			}
			break;
		case R.id.jian_btn:
			if (Integer.valueOf(num_edit.getText().toString()) == 0)
				return;
			num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) - 1));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		case R.id.jia_btn:
			num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) + 1));
			num_edit.setSelection(num_edit.getText().toString().length());
			break;
		}

	}

}
