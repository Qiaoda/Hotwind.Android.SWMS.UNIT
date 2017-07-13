package cn.jitmarketing.hot.choupan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.anim;
import android.R.integer;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.RandomCheckCodeBean;
import cn.jitmarketing.hot.entity.RandomCheckCodeBean.CodeBean;
import cn.jitmarketing.hot.entity.RandomCheckDetailBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.view.BaseDialog;
import cn.jitmarketing.hot.view.TitleWidget;

public class RandomCheckActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.ac_title)
	private TitleWidget ac_title;
	@ViewInject(R.id.random_check_detail)
	private TextView random_check_detail;
	@ViewInject(R.id.detail_layout)   
	private LinearLayout detail_layout;
	@ViewInject(R.id.random_code)
	// 抽盘单号
	private TextView random_code;
	@ViewInject(R.id.random_num)
	// 抽盘件数
	private TextView random_num;
	@ViewInject(R.id.random_stock)
	// 抽盘库位
	private TextView random_stock;
	@ViewInject(R.id.random_money)
	// 抽盘金额
	private TextView random_money;
	@ViewInject(R.id.percent_num)
	// 占门店总件数
	private TextView percent_num;
	@ViewInject(R.id.percent_money)
	// 占门店总金额
	private TextView percent_money;

	private BaseDialog checkDialog;
	private Spinner spinner;
	private TextView create_new_cancel;
	private TextView create_new_sure;
	/* 网络请求tag */
	private static final int REQUEST_CHECK_CODE = 0x11;// 获取抽盘单号
	private static final int REQUEST_CREATE_CHECK = 0x12;// 创建抽盘任务
	private static final int REQUEST_CHECK_DETAIL = 0x13;// 获取抽盘明细
	private RandomCheckCodeBean codeBean;
	private ArrayAdapter<String> adapter;
	private ArrayList<String> mItems = new ArrayList<String>();
	private String InventoryCheckTaskID;
	private RandomCheckDetailBean randomCheckDetailBean;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_random_check;
	}

	@Override
	protected void exInitView() {
		random_check_detail.setOnClickListener(this);
		ac_title.setOnLeftClickListner(this);
		ac_title.setOnRightClickListner(this);
		createCheckDialog();
		requestRandomCheckDetail();
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case REQUEST_CHECK_CODE:

			return WarehouseNet.getRandomCheckCode();

		case REQUEST_CREATE_CHECK:

			return WarehouseNet.createRandomCheck(InventoryCheckTaskID);
		case REQUEST_CHECK_DETAIL:

			return WarehouseNet.getRandomCheckDetail();
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		// 请求不成功
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			switch (what) {
			case REQUEST_CHECK_CODE:
				break;
			case REQUEST_CHECK_DETAIL:
				break;
			}

			return;
		}
		// 请求成功
		switch (what) {
		case REQUEST_CHECK_CODE:
			mItems.clear();
			codeBean = mGson.fromJson(mGson.toJson(net.data), RandomCheckCodeBean.class); 
			if (codeBean != null) {
				for (CodeBean bean : codeBean.getInventoryCheckTaskIDList()) {
					mItems.add(bean.getInventoryCheckTaskID());
				}
				adapter.notifyDataSetChanged();
			} 
			break;

		case REQUEST_CREATE_CHECK:
			JSONObject object;
			try {
				object = new JSONObject(mGson.toJson(net.data));
				if (object.getBoolean("UpdInventoryCheckTaskStart")) {
					checkDialog.dismiss();
					requestRandomCheckDetail();
				} else {
					Toast.makeText(mActivity, net.message, Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case REQUEST_CHECK_DETAIL:
			randomCheckDetailBean = mGson.fromJson(mGson.toJson(net.data), RandomCheckDetailBean.class);
			if (randomCheckDetailBean != null) {
				setDetail(randomCheckDetailBean);
				detail_layout.setVisibility(View.VISIBLE); 
				random_check_detail.setVisibility(View.VISIBLE);
			} else {
				detail_layout.setVisibility(View.GONE);
				random_check_detail.setVisibility(View.GONE);
				Toast.makeText(mActivity, net.message, Toast.LENGTH_SHORT).show();
			}
			break;
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case REQUEST_CHECK_CODE:
		case REQUEST_CREATE_CHECK:
		case REQUEST_CHECK_DETAIL:
			Ex.Toast(mContext).showLong("请求失败，请稍后重试");
			break;
		}
	}

	@Override
	public void fillCode(String code) {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			onBackPressed();
			break;
		case R.id.lv_right:// 创建抽盘
			checkDialog.show();
			requestRandomCheckCode();
			break;
		case R.id.random_check_detail:
			startActivity(new Intent(this, RandomCheckDetailActivity.class));
			break;
		case R.id.create_new_sure:
			InventoryCheckTaskID = spinner.getSelectedItem().toString();
			requestCreateRandomCheck();
			break;
		case R.id.create_new_cancel:
			checkDialog.dismiss();
			break;
		default:
			break;
		}

	}

	private void createCheckDialog() {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_create_check, null);
		checkDialog = new BaseDialog(mActivity, view);
		checkDialog.setCanceledOnTouchOutside(false);
		spinner = (Spinner) view.findViewById(R.id.spinner);
		create_new_cancel = (TextView) view.findViewById(R.id.create_new_cancel);
		create_new_sure = (TextView) view.findViewById(R.id.create_new_sure);
		create_new_cancel.setOnClickListener(this);
		create_new_sure.setOnClickListener(this);
		adapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_item, mItems);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
	}

	/* 请求抽盘单号 */
	private void requestRandomCheckCode() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.RandomCheck.RandomCheckCode, REQUEST_CHECK_CODE, NET_METHOD_POST, false);
	}

	/* 创建抽盘 */
	private void requestCreateRandomCheck() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.RandomCheck.CreateRandomCheck, REQUEST_CREATE_CHECK, NET_METHOD_POST, false);
	}

	/* 获取抽盘明细 */
	private void requestRandomCheckDetail() {
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.RandomCheck.GetRandomCheckDetail, REQUEST_CHECK_DETAIL, NET_METHOD_POST, false);
	}

	/* 明细赋值 */
	private void setDetail(RandomCheckDetailBean randomCheckDetailBean) {
		random_code.setText(randomCheckDetailBean.getInventoryCheckTaskID());
		random_num.setText(randomCheckDetailBean.getHtcp_Qty_Sum());
		random_stock.setText(randomCheckDetailBean.getHtcp_Location_Count());
		random_money.setText(randomCheckDetailBean.getHtcp_Amount_Sum());
		percent_num.setText(randomCheckDetailBean.getHtcp_Qty_Sum_Total());
		percent_money.setText(randomCheckDetailBean.getHtcp_Amount_Sum_Total());
	}
}
