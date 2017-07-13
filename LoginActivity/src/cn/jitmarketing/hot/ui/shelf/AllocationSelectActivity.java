package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.AreaBean;
import cn.jitmarketing.hot.entity.CityBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.UnitBeanOne;
import cn.jitmarketing.hot.entity.UnitEntity;
import cn.jitmarketing.hot.net.WarehouseNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.view.SingleCheckDialog;
import cn.jitmarketing.hot.view.SingleCheckDialog.OnCustomDialogListener;

public class AllocationSelectActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
	@ViewInject(R.id.allocation_create_main_layout)
	FrameLayout allocation_create_main_layout;
	@ViewInject(R.id.allocation_create_layout)
	LinearLayout allocation_create_layout;
	@ViewInject(R.id.allocation_create_cancle)
	TextView allocation_create_cancle;
	@ViewInject(R.id.allocation_create_sure)
	TextView allocation_create_sure;
	@ViewInject(R.id.select_first_lay)
	LinearLayout select_first_lay;
	@ViewInject(R.id.select_second_lay)
	LinearLayout select_second_lay;
	@ViewInject(R.id.select_third_lay)
	LinearLayout select_third_lay;
	@ViewInject(R.id.select_first_txt)
	TextView select_first_txt;
	@ViewInject(R.id.select_second_txt)
	TextView select_second_txt;
	@ViewInject(R.id.select_third_txt)
	TextView select_third_txt;
	@ViewInject(R.id.allocation_create_confirm_layout)
	LinearLayout allocation_create_confirm_layout;
	@ViewInject(R.id.allocation_to_address_txt)
	TextView allocation_to_address_txt;
	@ViewInject(R.id.allocation_create_cancle1)
	TextView allocation_create_cancle1;
	@ViewInject(R.id.allocation_create_sure1)
	TextView allocation_create_sure1;
	@ViewInject(R.id.remark_edt)
	EditText remark_edt;
	@ViewInject(R.id.radiogroup)
	RadioGroup radiogroup;
	@ViewInject(R.id.oneRadio)
	RadioButton oneRadio;
	@ViewInject(R.id.twoRadio)
	RadioButton twoRadio;

	private static final int WHAT_NET_GET_ALLOCATION_SAVE = 0x11;
	private static final int WHAT_NET_GET_ALLOCATION_SELECT_LIST = 0x10;
	private UnitEntity unitEntity;
	private AlertDialog.Builder builder;
	private String[] citys;
	private String[] areas;
	private String[] units;
	private List<CityBean> areaList;// 大区列表
	private List<AreaBean> cityList;// 城市列表
	private List<UnitBeanOne> unitList;// 店铺列表
	private List<AreaBean> selectCityList = new ArrayList<AreaBean>();// 选择的城市列表
	private List<UnitBeanOne> selectUnitList = new ArrayList<UnitBeanOne>();// 选择的店铺列表
	private CityBean selectArea;// 选择的大区
	private AreaBean selectCity;// 选择的城市
	private UnitBeanOne selectUnit;// 选择的店铺
	private ArrayList<InStockDetail> wareList;
	private String AllocationOrderID;
	private int backDefectiveType = 0;// 默认为0，客户退次为1，门店退次2
	private String remark = "";
	// 防止双击操作
	private long lastClickTime = 0;
	private String userName;

	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		setFinishOnTouchOutside(false);
		return R.layout.activity_allocation_select;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			// 日志操作
			LogUtils.logOnFile("创建调拨单->调拨->确定" + net.message);
			Log.i("DIAOBO", net.message);
			switch (what) {
			case WHAT_NET_GET_ALLOCATION_SAVE:
				finish();
				break;
			}
			return;
		}

		switch (what) {
		case WHAT_NET_GET_ALLOCATION_SELECT_LIST:
			if (null == net.data) {
				Ex.Toast(mContext).showLong(net.message);
				// 日志操作
				LogUtils.logOnFile("创建调拨单->调拨->确定" + net.message);
				return;
			}
			String str = mGson.toJson(net.data);
			unitEntity = mGson.fromJson(str, UnitEntity.class);
			areaList = unitEntity.AreaList;
			cityList = unitEntity.CityList;
			unitList = unitEntity.UnitList;
			areas = new String[unitEntity.AreaList.size()];
			for (int i = 0; i < unitEntity.AreaList.size(); i++) {
				areas[i] = unitEntity.AreaList.get(i).name;
			}
			for (CityBean area : areaList) {
				if (area.id.equals(HotApplication.getInstance().getAreaId())) {
					selectArea = area;
					select_first_txt.setText(area.name);
					break;
				}
			}
			for (AreaBean city : cityList) {
				if (city.id.equals(HotApplication.getInstance().getCityId())) {
					selectCity = city;
					select_second_txt.setText(city.name);
				}
			}
			break;
		case WHAT_NET_GET_ALLOCATION_SAVE:
			Ex.Toast(mContext).show(net.message);
			// 日志操作
			LogUtils.logOnFile("创建调拨单->调拨->确定" + net.message);
			AllocationCreateActivity.allocationCreateActivity.finish();
			AllocationActivity.allocation = true;
			finish();
			break;
		}
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_GET_ALLOCATION_SAVE:
			Ex.Toast(mContext).showLong("调拨失败，请重试");
			// 日志操作
			LogUtils.logOnFile("创建调拨单->调拨->确定" + "调拨失败，请重试");
			break;
		case WHAT_NET_GET_ALLOCATION_SELECT_LIST:
			Ex.Toast(mContext).showLong("获取门店列表失败");
			// 日志操作
			LogUtils.logOnFile("创建调拨单->调拨->确定" + "获取门店列表失败");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_GET_ALLOCATION_SELECT_LIST:
			return WarehouseNet.stockListParams(MgrPerference.getInstance(this).getString(HotConstants.Unit.UNIT_ID));
		}
		return null;
	}

	@Override
	protected void exInitView() {
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
		allocation_create_main_layout.setLayoutParams(params);
		allocation_create_cancle.setOnClickListener(this);
		allocation_create_sure.setOnClickListener(this);
		userName = HotApplication.getInstance().getUserName();
		if (userName.substring(userName.length() - 3, userName.length()).equals("cg1") || userName.substring(userName.length() - 4, userName.length()).equals("cgy1")) {
			select_first_lay.setOnClickListener(this);// 大区
		}
		// select_first_lay.setOnClickListener(this);// 大区
		select_second_lay.setOnClickListener(this);
		select_third_lay.setOnClickListener(this);
		allocation_create_cancle1.setOnClickListener(this);
		allocation_create_sure1.setOnClickListener(this);
		oneRadio.setOnClickListener(this);
		twoRadio.setOnClickListener(this);
		allocation_create_layout.setVisibility(View.VISIBLE);
		allocation_create_confirm_layout.setVisibility(View.GONE);
		Bundle bd = getIntent().getExtras();
		wareList = (ArrayList<InStockDetail>) bd.getSerializable("List");
		AllocationOrderID = bd.getString("AllocationOrderID");
		startTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ALLOCATION_SELETE_LIST, WHAT_NET_GET_ALLOCATION_SELECT_LIST, NET_METHOD_POST, false);
		// 限制输入特殊字符
		remark_edt.addTextChangedListener(new TextWatcher() {
			String tmp = "";
			String digits = "[,.。，、？]";

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				remark_edt.setSelection(s.length());
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
					if (String.valueOf(str.charAt(i)).matches("[\u4e00-\u9fa5]+") || String.valueOf(str.charAt(i)).matches("[a-zA-Z0-9 /]+") || digits.indexOf(str.charAt(i)) >= 0) {
						if (str.length() > 300) {
							Toast.makeText(mActivity, "文字过多", Toast.LENGTH_SHORT).show();
						} else {
							sb.append(str.charAt(i));
						}
					}
				}
				tmp = sb.toString();
				remark_edt.setText(tmp);
			}
		});
	}

	private void setAreaData() {
		select_first_txt.setText(selectArea.name);
		selectCity = null;
		selectUnit = null;
		select_second_txt.setText(getResources().getString(R.string.city));
		select_third_txt.setText(getResources().getString(R.string.unit));
		// 日志操作
		LogUtils.logOnFile("请选择调拨门店->区域选择为：" + selectArea.name);
	}

	private void setCityData() {
		select_second_txt.setText(selectCity.name);
		select_third_txt.setText(getResources().getString(R.string.unit));
		selectUnit = null;
		// 日志操作
		LogUtils.logOnFile("请选择调拨门店->城市选择为：" + selectCity.name);
	}

	private void setUnitData() {
		select_third_txt.setText(selectUnit.unitName);
		// 日志操作
		LogUtils.logOnFile("请选择调拨门店->门店选择为：" + selectUnit.unitName);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.allocation_create_cancle:
			this.finish();
			break;
		case R.id.oneRadio:
			backDefectiveType = 1;
			break;
		case R.id.twoRadio:
			backDefectiveType = 2;
			break;
		case R.id.select_first_lay:// 区域
			// 日志操作
			LogUtils.logOnFile("请选择调拨门店->区域选择");
			if (FastDoubleClickUtil.isFastDoubleClick()) {
				return;
			}
			builder = new AlertDialog.Builder(this);
			builder.setTitle("选择一个区域");
			// 设置一个下拉的列表选择项
			builder.setSingleChoiceItems(areas, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectArea = areaList.get(which);
					setAreaData();
				}
			});
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (selectArea == null) {
						selectArea = areaList.get(0);
						setAreaData();
					}
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.show();
			break;
		case R.id.select_second_lay:// 城市
			// 日志操作
			LogUtils.logOnFile("请选择调拨门店->城市选择");
			if (selectArea == null) {
				Ex.Toast(this).show("请先选择一个大区");
				return;
			}
			selectCityList.clear();
			for (AreaBean city : cityList) {
				if (city.areaID.equals(selectArea.id)) {
					selectCityList.add(city);
				}
			}
			citys = new String[selectCityList.size()];
			for (int i = 0; i < selectCityList.size(); i++) {
				citys[i] = selectCityList.get(i).name;
			}
			if (FastDoubleClickUtil.isFastDoubleClick()) {
				return;
			}
			builder = new AlertDialog.Builder(this);
			builder.setTitle("选择一个城市");
			// 设置一个下拉的列表选择项
			builder.setSingleChoiceItems(citys, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					selectCity = selectCityList.get(which);
					setCityData();
				}
			});
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					if (selectCity == null) {
						selectCity = selectCityList.get(0);
						setCityData();
					}
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			builder.show();
			break;
		case R.id.select_third_lay:// 店铺
			// 日志操作
			LogUtils.logOnFile("请选择调拨门店->门店选择");
			if (selectCity == null) {
				Ex.Toast(this).show("请先选择一个城市");
				return;
			}
			selectUnitList.clear();
			int k = 0;
			try {
				for (int i = 0; i < unitList.size(); i++) {
					if (unitList.get(i).cityID.equals(selectCity.id) && unitList.get(i).areaID.equals(selectArea.id)) {
						selectUnitList.add(unitList.get(i));
						k = i;
					}
				}
			} catch (Exception e) {
				Log.i("f", unitList.get(k).unitID);
			}
			units = new String[selectUnitList.size()];
			for (int i = 0; i < selectUnitList.size(); i++) {
				units[i] = selectUnitList.get(i).unitName;
			}
			if (FastDoubleClickUtil.isFastDoubleClick()) {
				return;
			}
			SingleCheckDialog dialog = new SingleCheckDialog(this, selectUnitList, new OnCustomDialogListener() {

				@Override
				public void upload(UnitBeanOne selectUnit) {
					AllocationSelectActivity.this.selectUnit = selectUnit;
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
					allocation_create_main_layout.setLayoutParams(params);
					if (selectUnit.unitID.startsWith("1000")) {
						if (selectUnit.unitID.endsWith("-c") || selectUnit.unitID.endsWith("-C")) {
							backDefectiveType = 1;
							radiogroup.setVisibility(View.VISIBLE);
							FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
							allocation_create_main_layout.setLayoutParams(params1);
						} else {
							backDefectiveType = 0;
							radiogroup.setVisibility(View.GONE);
						}
					} else {
						backDefectiveType = 0;
						radiogroup.setVisibility(View.GONE);
					}
					setUnitData();
				}
			});
			dialog.show();
			// builder = new AlertDialog.Builder(this);
			// builder.setTitle("选择一个店铺");
			// // 设置一个下拉的列表选择项
			// builder.setSingleChoiceItems(units, 0,
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// selectUnit = selectUnitList.get(which);
			// setUnitData();
			// }
			// });
			// builder.setPositiveButton("确定",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// if (selectUnit == null) {
			// selectUnit = selectUnitList.get(0);
			// setUnitData();
			// }
			// }
			// });
			// builder.setNegativeButton("取消",
			// new DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			//
			// }
			// });
			// builder.show();
			break;
		case R.id.allocation_create_sure:
			// 日志操作
			LogUtils.logOnFile("请选择调拨门店->确定->备注：" + remark_edt.getText().toString());
			if (selectUnit == null) {
				Ex.Toast(this).show("请选择店铺");
				return;
			}
			// startJsonTask(HotConstants.Global.APP_URL_USER
			// + HotConstants.Shelf.ALLOCATION_SAVE,
			// WHAT_NET_GET_ALLOCATION_SAVE, true,
			// NET_METHOD_POST, SaveListUtil.skuShelfSave(wareList,
			// selectUnit.unitID), false);
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.4));
			allocation_create_main_layout.setLayoutParams(params);
			allocation_create_layout.setVisibility(View.GONE);
			allocation_create_confirm_layout.setVisibility(View.VISIBLE);
			String source = "这批货将调拨到<font color='green'>" + selectUnit.unitName + "</font>," + "<br>" + "您确认本次调拨操作";
			allocation_to_address_txt.setText(Html.fromHtml(source));
			break;
		case R.id.allocation_create_cancle1:
			// 日志
			LogUtils.logOnFile("请选择调拨门店->取消调拨");
			if (selectUnit.unitID.startsWith("1000")) {
				if (selectUnit.unitID.endsWith("-c") || selectUnit.unitID.endsWith("-C")) {
					FrameLayout.LayoutParams params1 = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
					allocation_create_main_layout.setLayoutParams(params1);
				} else {
					FrameLayout.LayoutParams param1 = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
					allocation_create_main_layout.setLayoutParams(param1);
				}
			} else {
				FrameLayout.LayoutParams param1 = new FrameLayout.LayoutParams((int) (ConstValue.SCREEN_WIDTH * 0.9), (int) (ConstValue.SCREEN_HEIGHT * 0.7));
				allocation_create_main_layout.setLayoutParams(param1);
			}

			allocation_create_layout.setVisibility(View.VISIBLE);
			allocation_create_confirm_layout.setVisibility(View.GONE);
			break;
		case R.id.allocation_create_sure1:
			// 避免双击操作
			long currentTime = System.currentTimeMillis();
			if (currentTime - lastClickTime > 1000) {
				lastClickTime = currentTime;
				// 执行事件
				// 日志
				LogUtils.logOnFile("请选择调拨门店->确定调拨");
				remark = remark_edt.getText().toString();
				startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.Shelf.ALLOCATION_SAVE, WHAT_NET_GET_ALLOCATION_SAVE, true, NET_METHOD_POST, SaveListUtil.skuShelfSave(wareList, selectUnit.unitID, AllocationOrderID, String.valueOf(backDefectiveType), remark), false);
			}
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// 日志操作
		LogUtils.logOnFile("->请选择调拨门店");
	}
}
