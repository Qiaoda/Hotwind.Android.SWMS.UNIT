package cn.jitmarketing.hot.pandian;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.R.layout;
import cn.jitmarketing.hot.adapter.SingleStockAdapter;
import cn.jitmarketing.hot.choupan.RandomCheckDetailActivity;
import cn.jitmarketing.hot.choupan.RandomCheckHistoryActivity;
import cn.jitmarketing.hot.entity.SingleStockSKUBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.ui.shelf.NewInStockCompleteActivity;
import cn.jitmarketing.hot.util.NetWorkUtil;
import cn.jitmarketing.hot.util.SkuSoundUtils;
import cn.jitmarketing.hot.view.BaseDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.NumberAmendView;
import cn.jitmarketing.hot.view.TipDialog;
import cn.jitmarketing.hot.view.TipDialog.DialogSureClickListener;
import cn.jitmarketing.hot.view.TitleWidget;
import android.R.integer;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleStockActivity extends BaseSwipeOperationActivity implements OnClickListener {

	@ViewInject(R.id.ac_title)
	private TitleWidget ac_title;
	@ViewInject(R.id.sku_list)
	private ListView skuListView;
	@ViewInject(R.id.tip_num)
	private TextView tipNum;
	@ViewInject(R.id.sku_save)
	private TextView skuSave;

	private SingleStockAdapter sAdapter;
	private List<SingleStockSKUBean> skuBeans = new ArrayList<SingleStockSKUBean>();
	private List<SingleStockSKUBean> errorSkuBeans = new ArrayList<SingleStockSKUBean>();

	private int dialogtype = 0;// 1 2 增删弹框 手动弹框
	private int skuPosition;
	private BaseDialog numberAmendDialog;
	private TextView dialog_title;
	private LinearLayout sku_layout;
	private ClearEditText dialog_sku;
	private NumberAmendView amend_view;
	private TextView d_btn_sure, d_btn_cancel;
	private TipDialog tipDialog;
	private TipDialog saveDialog;

	// -------------------------文件写入
	private File file;
	private static final String FILE_NAME = "PD.txt";
	private AsyncHttpClient asyncHttpClient;
	private static final int WriteStyle_Z = 1;// 1追加
	private static final int WriteStyle_F = 2;// 2覆盖
	// --------------------------异常数据
	private List<String> errorSku = new ArrayList<String>();
	// ---------------------------检测是否可提交
	private static final int SHOW_DIALOG = 0X11;
	private static final int DISMISS_DIALOG = 0x12;
	private static final int SAVE_DIS_DIALOG=0X13;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_DIALOG:
				Ex.Dialog(mActivity).showProgressDialog("", getResources().getString(R.string.tongji));
				break;
			case DISMISS_DIALOG:
				Ex.Dialog(mActivity).dismissProgressDialog();
				break;
			case SAVE_DIS_DIALOG:
				Ex.Dialog(mActivity).dismissProgressDialog();
				saveDialog.show();
				break;

			default:
				break;
			}
		};
	};

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_single_stock;
	}

	@Override
	protected void exInitView() {
		ac_title.setOnLeftClickListner(this);
		ac_title.setOnRightClickListner(this);
		skuSave.setOnClickListener(this);
		sAdapter = new SingleStockAdapter(mActivity, skuBeans);
		skuListView.setAdapter(sAdapter);
		createDialog();
		createTipDialog();
		skuListView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				skuPosition = position;
				showAmendDialog();
				return true;
			}
		});
		file = new File(getFilesDir(), FILE_NAME);
		// SDcard读写
		// if
		// (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		// {
		//
		// file = new File(Environment.getExternalStorageDirectory(),
		// FILE_NAME);
		// }
		// 有未上传数据
		if (file.exists()) {
			readData();
		}
		refreshScanNum();
		asyncHttpClient = new AsyncHttpClient();
	}

	@Override
	public void fillCode(String code) {
		if (!numberAmendDialog.isShowing() || !tipDialog.isShowing() || !saveDialog.isShowing()) {
			switch (SkuSoundUtils.isWarehouse(code)) {
			case HotConstants.Global.SKU_CODE:
				addSku(WriteStyle_Z, code, 1);
				break;
			case HotConstants.Global.STOCK_CODE:
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.lv_left:
			if (getScanNum() != 0) {
				tipDialog.show();
			} else {
				onBackPressed();
			}
			break;
		case R.id.lv_right:
			showHandDialog();
			break;
		case R.id.d_btn_sure:
			if (dialogtype == 1) {// 增删
				if (amend_view.getNum() == 0) {
					skuBeans.remove(skuBeans.get(skuPosition));
				} else {
					skuBeans.get(skuPosition).setNum(amend_view.getNum());
				}
				refreshScanNum();
				sAdapter.notifyDataSetChanged();
				startThread();
			} else if (dialogtype == 2) {// 手工
				if (TextUtils.isEmpty(dialog_sku.getText().toString())) {
					Toast.makeText(mActivity, "请输入SKU", Toast.LENGTH_SHORT).show();
					return;
				}
				if (amend_view.getNum() == 0) {
					Toast.makeText(mActivity, "SKU数量不能为0", Toast.LENGTH_SHORT).show();
					return;
				}
				addSku(WriteStyle_F, dialog_sku.getText().toString().toUpperCase(), amend_view.getNum());
			}
			numberAmendDialog.dismiss();
			break;
		case R.id.d_btn_cancel:
			numberAmendDialog.dismiss();
			break;
		case R.id.sku_save:
			if (NetWorkUtil.isNetworkConnected(mActivity)) {
				if (skuBeans.size() != 0) {
					new Thread() {
						@Override
						public void run() {
							handler.sendEmptyMessage(SHOW_DIALOG);
							writeData(WriteStyle_F, getSkuString(skuBeans));
							handler.sendEmptyMessage(SAVE_DIS_DIALOG);
						}
					}.start();
				} else {
					Toast.makeText(mActivity, "请扫描商品后再保存", Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(mActivity, "请检查网络连接", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	/** 开启子线程写文件 */
	private void startThread() {
		new Thread() {
			@Override
			public void run() {
				handler.sendEmptyMessage(SHOW_DIALOG);
				writeData(WriteStyle_F, getSkuString(skuBeans));
				handler.sendEmptyMessage(DISMISS_DIALOG);
			}
		}.start();
	}

	/** 添加数据 */
	private void addSku(int style, String sku, int num) {
		if (isExist(sku)) {// 存在直接累加个数
			SingleStockSKUBean isSkuBean = getExistBean(sku);
			skuBeans.remove(getExistBean(sku));
			isSkuBean.setNum(isSkuBean.getNum() + num);
			skuBeans.add(0, isSkuBean);
		} else {// 不存在 创建新对象
			SingleStockSKUBean newBean = new SingleStockSKUBean(sku, num);
			skuBeans.add(0, newBean);
		}
		refreshScanNum();
		sAdapter.notifyDataSetChanged();
		if (style == WriteStyle_Z) {
			writeData(WriteStyle_Z, "09,01,Android," + sku + "," + num + "\r\n");
		} else if (style == WriteStyle_F) {
			startThread();
		}

	}

	/** 检测是否已扫描过 */
	private boolean isExist(String sku) {
		for (SingleStockSKUBean skuBean : skuBeans) {
			if (skuBean.getSku().equals(sku)) {
				return true;
			}
		}
		return false;
	}

	/** 刷新扫描次数 */
	private void refreshScanNum() {
		tipNum.setText(Html.fromHtml("本次已扫描:" + "SKU数  <font color='blue'>" + skuBeans.size() + "</font>" + "  总件数 <font color='green'>" + getScanNum() + "</font>"));
		if (skuBeans.size() >= 1000) {
			Toast.makeText(mActivity, "SKU数目过多，建议保存操作", Toast.LENGTH_SHORT).show();
		}
	}

	/** 获取当前扫描的件数 */
	private int getScanNum() {
		int num = 0;
		for (SingleStockSKUBean bean : skuBeans) {
			num += bean.getNum();
		}
		return num;
	}

	/** 获取已存在的sku对象 */
	private SingleStockSKUBean getExistBean(String sku) {
		for (SingleStockSKUBean skuBean : skuBeans) {
			if (skuBean.getSku().equals(sku)) {
				return skuBean;
			}
		}
		return null;
	}

	/**
	 * 修改数量dialog
	 */
	private void createDialog() {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_number_amend, null);
		numberAmendDialog = new BaseDialog(mActivity, view);
		numberAmendDialog.setCanceledOnTouchOutside(false);
		dialog_title = (TextView) view.findViewById(R.id.dialog_title);
		sku_layout = (LinearLayout) view.findViewById(R.id.sku_layout);
		dialog_sku = (ClearEditText) view.findViewById(R.id.dialog_sku);
		amend_view = (NumberAmendView) view.findViewById(R.id.amend_view);
		d_btn_sure = (TextView) view.findViewById(R.id.d_btn_sure);
		d_btn_cancel = (TextView) view.findViewById(R.id.d_btn_cancel);
		d_btn_sure.setOnClickListener(this);
		d_btn_cancel.setOnClickListener(this);
		numberAmendDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				dialog_sku.setText("");

			}
		});
	}

	/**
	 * show手工输入dialog
	 */
	private void showHandDialog() {
		dialogtype = 2;
		dialog_title.setText("手工录入SKU数量");
		amend_view.setStartNum(0);
		sku_layout.setVisibility(View.VISIBLE);
		amend_view.setCanEnable(true);
		numberAmendDialog.show();
	}

	/**
	 * show amend dialog
	 */
	private void showAmendDialog() {
		dialogtype = 1;
		dialog_title.setText("修改盘点SKU数量");
		sku_layout.setVisibility(View.GONE);
		amend_view.setStartNum(skuBeans.get(skuPosition).getNum());
		amend_view.setCanEnable(true);
		numberAmendDialog.show();
	}

	/** 创建提示dialog */
	private void createTipDialog() {
		tipDialog = new TipDialog(mActivity, "当前有未提交盘点数据，请确认是否退出？");
		tipDialog.setDialogSureClickListener(new DialogSureClickListener() {
			@Override
			public void sureClick() {
				file.delete();
				SingleStockActivity.this.finish();
			}
		});
		saveDialog = new TipDialog(mActivity, "是否保存本次盘点？");
		saveDialog.setDialogSureClickListener(new DialogSureClickListener() {

			@Override
			public void sureClick() {
				saveDialog.dismiss();
				upLoad();
			}
		});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			if (getScanNum() != 0) {
				tipDialog.show();
			} else {
				onBackPressed();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** 文件写入数据 */
	private void writeData(int style, String skuString) {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fos = null;
			if (style == WriteStyle_Z) {
				fos = openFileOutput(file.getName(), MODE_APPEND);
			} else if (style == WriteStyle_F) {
				fos = openFileOutput(file.getName(), MODE_PRIVATE);
			}
			// FileOutputStream fos = new FileOutputStream(file);
			fos.write(skuString.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 读取文件数据 */
	private void readData() {
		InputStreamReader isr = null;
		BufferedReader br = null;
		String str = "";
		try {
			FileInputStream fis = openFileInput(file.getName());
			isr = new InputStreamReader(fis);
			br = new BufferedReader(isr);
			// br = new BufferedReader(new FileReader(file));
			List<SingleStockSKUBean> skuBeant = new ArrayList<SingleStockSKUBean>();
			while ((str = br.readLine()) != null) {
				skuBeant.add(new SingleStockSKUBean(str.split(",")[3], Integer.valueOf(str.split(",")[4])));
			}
			for (int i = 0; i < skuBeant.size(); i++) {
				for (int j = 0; j < skuBeant.size(); j++) {
					if (skuBeant.get(i).getNum() != 0 && j > i && skuBeant.get(i).getSku().equals(skuBeant.get(j).getSku())) {
						skuBeant.get(i).setNum(skuBeant.get(i).getNum() + skuBeant.get(j).getNum());
						skuBeant.get(j).setNum(0);
					}
				}
			}
			for (int i = 0; i < skuBeant.size(); i++) {
				if (skuBeant.get(i).getNum() != 0) {
					skuBeans.add(skuBeant.get(i));
				}
			}
			sAdapter.notifyDataSetChanged();
			br.close();
			isr.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** getSkuString */
	private String getSkuString(List<SingleStockSKUBean> beans) {
		String str = "";
		for (SingleStockSKUBean singleStockSKUBean : beans) {
			str = str + "09,01,Android," + singleStockSKUBean.getSku() + "," + singleStockSKUBean.getNum() + "\r\n";
		}
		return str;
	}

	/** 文件上传 */
	private void upLoad() {
		errorSku.clear();
		RequestParams params = new RequestParams();
		try {
			params.put("type", "AndroidUpload");
			params.put("UserId", HotApplication.getInstance().getUserId());
			params.put("filename", file);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		asyncHttpClient.setTimeout(5 * 60 * 1000);
		Ex.Dialog(mActivity).showProgressDialog("", getResources().getString(R.string.ex_str_loading));
		asyncHttpClient.post(HotConstants.Global.APP_URL_USER + HotConstants.SingleStock.SingleStockCheck, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
				Ex.Dialog(mActivity).dismissProgressDialog();
				if (statusCode == 200) {
					try {
						JSONObject jsonObject = new JSONObject(new String(responseBody));
						if (jsonObject.getBoolean("Success")) {
							file.delete();
							skuBeans.clear();
							sAdapter.notifyDataSetChanged();
							refreshScanNum();
							Toast.makeText(mActivity, "本次盘点保存成功", Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(mActivity, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
							JSONArray jsonArray = jsonObject.getJSONArray("Data");
							for (int i = 0; i < jsonArray.length(); i++) {
								errorSku.add(jsonArray.getString(i));
							}
							// 异常数据处理
							errorSkuHandle(errorSku);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
				Ex.Dialog(mActivity).dismissProgressDialog();
				Toast.makeText(mActivity, "与服务器连接失败", Toast.LENGTH_SHORT).show();
			}
		});
	}

	/** 异常数据处理 */
	private void errorSkuHandle(List<String> errorSku) {
		int position = 0;
		errorSkuBeans.clear();
		for (int i = 0; i < errorSku.size(); i++) {
			for (SingleStockSKUBean bean : skuBeans) {
				if (bean.getSku().equals(errorSku.get(i))) {
					bean.setFlag(false);
				}
			}
		}
		errorSkuBeans.addAll(skuBeans);
		skuBeans.clear();
		for (SingleStockSKUBean bean : errorSkuBeans) {
			if (bean.isFlag()) {
				skuBeans.add(bean);
			} else {
				skuBeans.add(0, bean);
			}
		}
		skuListView.smoothScrollToPosition(0);
		sAdapter.notifyDataSetChanged();
	}

}
