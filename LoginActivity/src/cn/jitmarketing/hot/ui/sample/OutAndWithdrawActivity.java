package cn.jitmarketing.hot.ui.sample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.OutAndWithdrawAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.OutAndWithDrawBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.RefreshableView;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 鞋子的出样、撤样
 */
public class OutAndWithdrawActivity extends BaseSwipeOperationActivity implements OnClickListener {

	/**
	 * 网络请求标志位
	 */
	private static final int WHAT_NET_QUERY_LIST_OUT = 0x12; // 查询出另一个列表
	private static final int WHAT_NET_QUERY_LIST_WITHDRAW = 0X13; // 查询撤样列表
	private static final int WHAT_NET_SUBMIT_OUT_THE_OTHER_ONE = 0X14; // 提交出另一个
	private static final int WHAT_NET_SUBMIT_REVOKE = 0X15; // 提交撤样
	private static final int WHAT_NET_SUBMIT_REVOKE_ONE = 0x16; // 提交撤一只
	private static final int REQUEST_CODE_DIRECT_WITHDRAW = 0x17;

	@ViewInject(R.id.reset_postion_title)
	TitleWidget titleWidget;
	@ViewInject(R.id.only_list)
	ListView only_list;
	@ViewInject(R.id.text_tips)
	TextView text_tips;
	@ViewInject(R.id.refreshable_view)
	RefreshableView refreshableView;
	@ViewInject(R.id.hide_search_layout)
	LinearLayout hide_search_layout;

	public static final int SHOE_OUT = 0; // 0 鞋类出样
	public static final int SHOE_WITHDRAW = 1; // 1 鞋类撤样
	private SkuEntity mCurSkuEntity;
	private ItemEntity mItemEntity;
	// private String skuCodeStr = "";
	// private String shelfCode = null;

	// 出样另一只或撤样列表
	private List<OutAndWithDrawBean> outAndWithDrawList;
	private OutAndWithdrawAdapter mAdapter;

	private String mSkuCode;
	private String mCurMappingId; // 提交出另一只、撤一只 需要的参数
	private float mCount; // 提交撤样 需要的参数
	private int adapterType = SHOE_OUT;
	private boolean isWithdrawSuccess;

	@Override
	protected void exInitBundle() {
		initIble(this, this);
		Bundle bundle = getIntent().getExtras();
		adapterType = bundle.getInt("type", SHOE_WITHDRAW);
		mCurSkuEntity = (SkuEntity) bundle.getSerializable("sku");
		mItemEntity = (ItemEntity) bundle.getSerializable("item");
		mSkuCode = bundle.getString("skuCodeStr");
	}

	@Override
	protected int exInitLayout() {
		return R.layout.activity_only_list;
	}

	@Override
	protected void exInitView() {
		titleWidget.setOnLeftClickListner(this);
		titleWidget.getRightView().setVisibility(View.GONE);
		text_tips.setVisibility(View.GONE);
		// 标题
		if (adapterType == SHOE_WITHDRAW)
			titleWidget.setText(R.string.sample_withdraw);
		else
			titleWidget.setText(R.string.out_sample);

		outAndWithDrawList = new ArrayList<OutAndWithDrawBean>();
		mAdapter = new OutAndWithdrawAdapter(this, outAndWithDrawList);
		only_list.setAdapter(mAdapter);
		hide_search_layout.setVisibility(View.GONE);
		refreshableView.setOnRefreshListener(new RefreshableView.PullToRefreshListener() {
			@Override
			public void onRefresh() {
				switch (adapterType) {
				case SHOE_OUT:
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.OUT_SAMPLE_OTHER_ONE_LIST, WHAT_NET_QUERY_LIST_OUT, false, NET_METHOD_POST, false);
					break;
				case SHOE_WITHDRAW:
					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.QUERY_WITHDRAW_LIST, WHAT_NET_QUERY_LIST_WITHDRAW, false, NET_METHOD_POST, false);
					break;
				}
			}
		}, 1);

		mAdapter.setListener(new OutAndWithdrawAdapter.OnItemBtnClickListener() {
			@Override
			public void onOutClick(OutAndWithDrawBean item) {
				mCurMappingId = item.getMappingID();

				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.OUT_SAMPLE_OTHER_ONE, WHAT_NET_SUBMIT_OUT_THE_OTHER_ONE, NET_METHOD_POST, false);
			}

			@Override
			public void onCheyClick(OutAndWithDrawBean item) {
				mCount = item.getCount();

				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.REVOKE_SAMPLE, WHAT_NET_SUBMIT_REVOKE, NET_METHOD_POST, false);
			}

			@Override
			public void onCheLyzClick(OutAndWithDrawBean item) {
				mCurMappingId = item.getMappingID();

				startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.REVOKE_ONE_SAMPLE, WHAT_NET_SUBMIT_REVOKE_ONE, NET_METHOD_POST, false);
			}

			@Override
			public void onCHeZjClick(OutAndWithDrawBean item) {
				Bundle bd = new Bundle();
				bd.putSerializable("item", mItemEntity);
				bd.putSerializable("sku", mCurSkuEntity);
				bd.putSerializable("skuCodeStr", mSkuCode);
				bd.putSerializable("skuCode", mSkuCode);
				bd.putSerializable("withdrawBean", item);
				Ex.Activity(mActivity).startForResult(DirectWithdrawActivity.class, bd, REQUEST_CODE_DIRECT_WITHDRAW);
			}
		});
	}

	@Override
	protected void exInitAfter() {
		switch (adapterType) {
		case SHOE_OUT:
			// 出另一只列表
			mAdapter.setAdapterType(SHOE_OUT);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.OUT_SAMPLE_OTHER_ONE_LIST, WHAT_NET_QUERY_LIST_OUT, NET_METHOD_POST, false);
			break;
		case SHOE_WITHDRAW:
			// 撤样列表
			mAdapter.setAdapterType(SHOE_WITHDRAW);
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.QUERY_WITHDRAW_LIST, WHAT_NET_QUERY_LIST_WITHDRAW, NET_METHOD_POST, false);
			break;
		}
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		refreshableView.finishRefreshing();
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			// skuCodeStr = null;
			// shelfCode = null;
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		if (null == net.data) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		String jsonStr = mGson.toJson(net.data);
		switch (what) {
		case WHAT_NET_QUERY_LIST_OUT: // 查询出另一个列表
		case WHAT_NET_QUERY_LIST_WITHDRAW: // 查询撤样列表
			outAndWithDrawList.clear();
			List<OutAndWithDrawBean> resultList = mGson.fromJson(jsonStr, new TypeToken<List<OutAndWithDrawBean>>() {
			}.getType());
			if (resultList != null && resultList.size() > 0) {
				outAndWithDrawList.addAll(resultList);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case WHAT_NET_SUBMIT_OUT_THE_OTHER_ONE: // 提交出另一只
		case WHAT_NET_SUBMIT_REVOKE: // 提交撤样
		case WHAT_NET_SUBMIT_REVOKE_ONE: // 提交撤一只
			Ex.Toast(mActivity).show(net.message);
			break;
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_QUERY_LIST_OUT:
		case WHAT_NET_QUERY_LIST_WITHDRAW:
			Ex.Toast(mContext).showLong("数据获取失败");
			break;
		case WHAT_NET_SUBMIT_OUT_THE_OTHER_ONE:
		case WHAT_NET_SUBMIT_REVOKE:
		case WHAT_NET_SUBMIT_REVOKE_ONE:
			Ex.Toast(mContext).showLong("请求失败");
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE_DIRECT_WITHDRAW && resultCode == RESULT_OK) {
			isWithdrawSuccess = true;
			startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.QUERY_WITHDRAW_LIST, WHAT_NET_QUERY_LIST_WITHDRAW, false, NET_METHOD_POST, false);
		}
	}

	@Override
	public void fillCode(String code) {
		// 此处没有扫描
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_QUERY_LIST_OUT:
			return SkuNet.getOutOtherOneSample(mSkuCode);
		case WHAT_NET_QUERY_LIST_WITHDRAW:
			return SkuNet.getShoesWithDrawList(mSkuCode);
		case WHAT_NET_SUBMIT_OUT_THE_OTHER_ONE:
			return SkuNet.submitOutOtherOneSample(mSkuCode, mCurMappingId);
		case WHAT_NET_SUBMIT_REVOKE:
			return SkuNet.getRevokeSample(mSkuCode, String.valueOf(mCount));
		case WHAT_NET_SUBMIT_REVOKE_ONE:
			return SkuNet.submitRevokeOneSample(mSkuCode, mCurMappingId);
		}
		return null;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			if (isWithdrawSuccess)
				setResult(RESULT_OK);
			this.finish();
			break;
		}
	}
}
