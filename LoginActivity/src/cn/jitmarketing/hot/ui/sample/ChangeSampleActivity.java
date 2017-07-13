package cn.jitmarketing.hot.ui.sample;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.SkuListAdapter;
import cn.jitmarketing.hot.entity.ChangeSampleBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.util.TakeGoodsDialog;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 换样列表页面
 * 注：
 * 换样一只（传0.5），换样一双（传1）（鞋类）
 * 批量换样（非鞋类）
 * Created by xy on 2016/4/9.
 */
public class ChangeSampleActivity extends BaseSwipeOperationActivity implements View.OnClickListener, AdapterView.OnItemClickListener {

    private static final int WHAT_NET_GET_SKU_SAMPLE_LIST = 0;
    private static final int WHAT_NET_BATCH_CHANGE_SAMPLE = 1;
    private static final int WHAT_NET_CHANGE_SAMPLE = 2;

    @ViewInject(R.id.allocation_title)
    TitleWidget allocation_title;
    @ViewInject(R.id.search_edt)
	ClearEditText searchEdt;
    @ViewInject(R.id.list_sku)
    ListView list_sku;
    @ViewInject(R.id.menu_list)
    ListView menu_list;
    @ViewInject(R.id.ll_menu)
    LinearLayout ll_menu;
    @ViewInject(R.id.no_data)
    TextView tvNoData;

    ArrayList<ChangeSampleBean> mList;
    ArrayList<ChangeSampleBean> searchList=new ArrayList<ChangeSampleBean>();
    SkuListAdapter adapter;
    String filterSample = FILTER_ALL;//过滤类别
    static final String FILTER_ALL = "1,2,3,4,5,6,7";
    private String skuCode;
    private String skuCount = "1";
    private String scanSkuCodeStr = "";
    HotApplication ap;

    @Override
    protected void exInitBundle() {
        initIble(this, this);
        mList = new ArrayList<ChangeSampleBean>();
    }

    @Override
    protected int exInitLayout() {
        return R.layout.activity_change_sample;
    }

    @Override
    protected void exInitView() {
        ap = (HotApplication) getApplication();
        searchEdt.addTextChangedListener(textWatcher);
		searchEdt.setOnEditorActionListener(onEditorActionListener);
        allocation_title.setOnLeftClickListner(this);
        allocation_title.setOnRightClickListner(this);
        ll_menu.setOnClickListener(this);
        adapter = new SkuListAdapter(mActivity, mList);
        list_sku.setAdapter(adapter);
        list_sku.setOnItemClickListener(this);
        initMenu();
        getList();
    }

    /****
     * 菜单
     */
    void initMenu() {
        mMenuList.clear();
        mMenuList.add(new MenuItem("1", "鞋子"));
        mMenuList.add(new MenuItem("2", "服装"));
        mMenuList.add(new MenuItem("3", "小商品"));
        mMenuList.add(new MenuItem("4", "皮具"));
        mMenuList.add(new MenuItem("5", "样品"));
        mMenuList.add(new MenuItem("6", "辅料"));
        mMenuList.add(new MenuItem("7", "其它"));
        menu_list.setAdapter(new MenuAdatper());
        menu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ll_menu.setVisibility(View.GONE);
                filterSample = mMenuList.get(position).MenuId;
                scanSkuCodeStr = "";
                getList();
            }
        });
    }

    @Override
    public Map<String, String> onStart(int what) {
        switch (what) {
            case WHAT_NET_GET_SKU_SAMPLE_LIST:
                return SkuNet.getChangeSampleList(scanSkuCodeStr, filterSample, 0, 10000);
            case WHAT_NET_BATCH_CHANGE_SAMPLE:
                return SkuNet.batchChangeSample(skuCode, skuCount);
            case WHAT_NET_CHANGE_SAMPLE:
                return SkuNet.getChangeSample(skuCode, skuCount);
        }
        return null;
    }

    @Override
    public void onSuccess(int what, String result, boolean hashCache) {
        ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
        if (!net.isSuccess) {
            Ex.Toast(mContext).showLong(net.message);
            return;
        }
        switch (what) {
            case WHAT_NET_GET_SKU_SAMPLE_LIST:
                try {
                    String skulistStr = new JSONArray(mGson.toJson(net.data)).toString();
                    mList.clear();
                    mList.addAll((ArrayList<ChangeSampleBean>) mGson.fromJson(skulistStr, new TypeToken<List<ChangeSampleBean>>() {
                    }.getType()));
                    searchList.addAll(mList);
                    if (mList.size() <= 0)
                        tvNoData.setVisibility(View.VISIBLE);
                    else tvNoData.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case WHAT_NET_BATCH_CHANGE_SAMPLE:
            case WHAT_NET_CHANGE_SAMPLE:
                Ex.Toast(mActivity).show(net.message);
                break;
        }
    }

    @Override
    public void onError(int what, int e, String message) {
        tvNoData.setVisibility(View.VISIBLE);
        Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
    }

    void getList() {
        startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetChangeSample, WHAT_NET_GET_SKU_SAMPLE_LIST, NET_METHOD_POST, false);
    }

    @Override
    public void fillCode(String code) {
        if (code != null) {
            if (!SkuUtil.isWarehouse(code)) {
                ap.getsoundPool(ap.Sound_sku);
                this.scanSkuCodeStr = code;
                filterSample = FILTER_ALL;
                getList();
            } else {
                ap.getsoundPool(ap.Sound_location);
            }
        } else {
            ap.getsoundPool(ap.Sound_error);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_left:
                finish();
                break;
            case R.id.lv_right:
                if (ll_menu.getVisibility() == View.GONE)
                    ll_menu.setVisibility(View.VISIBLE);
                else ll_menu.setVisibility(View.GONE);
                break;
            case R.id.ll_menu:
                ll_menu.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HotConstants.SELECTED = position;
        final ChangeSampleBean bean = mList.get(position);
        if (bean != null) {
            //鞋类换样
            if (bean.HaveShoebox == 1) {
                skuCode = bean.SKUID;
                String tip = "请确认是否换另一双";//换一双（1）
                skuCount = "1";
                if (bean.EndQty == 0.5) {//换一只（0.5）
                    tip = "请确认是否换另一只";
                    skuCount = "0.5";
                }
                final TakeGoodsDialog shelfDialog = new TakeGoodsDialog(mActivity, 300, 155, R.layout.show_alert_dialog, R.style.Theme_dialog);
                ((TextView) shelfDialog.findViewById(R.id.text_tips)).setText(tip);
                ((TextView) shelfDialog.findViewById(R.id.sku_txt)).setText(bean.SKUID);
                shelfDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        changeSample();

                        shelfDialog.dismiss();
                    }
                });
                shelfDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shelfDialog.dismiss();
                    }
                });
                shelfDialog.show();
            } else {
                //批量换样（非鞋类）
                final TakeGoodsDialog shelfDialog = new TakeGoodsDialog(mActivity, 300, 170, R.layout.show_sku_alert_dialog, R.style.Theme_dialog);
                final Button jian_btn = (Button) shelfDialog.findViewById(R.id.jian_btn);
                final EditText num_edit = (EditText) shelfDialog.findViewById(R.id.num_edit);
                final Button jia_btn = (Button) shelfDialog.findViewById(R.id.jia_btn);
                ((TextView) shelfDialog.findViewById(R.id.sku_txt)).setText(bean.SKUID);
                jia_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Double.parseDouble(num_edit.getText().toString()) >= bean.EndQty) {
                            return;
                        }
                        num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) + 1));
                        num_edit.setSelection(num_edit.getText().toString().length());
                    }
                });

                jian_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Integer.valueOf(num_edit.getText().toString()) == 0)
                            return;
                        num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) - 1));
                        num_edit.setSelection(num_edit.getText().toString().length());
                    }
                });
                shelfDialog.findViewById(R.id.btn_ok).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        skuCount = num_edit.getText() + "";
                        if (Integer.parseInt(skuCount) <= 0) {
                            Ex.Toast(mActivity).show("必须大于0");
                            return;
                        }
                        skuCode = bean.SKUID;
                        batchChangeSample();
                        shelfDialog.dismiss();
                    }
                });
                shelfDialog.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shelfDialog.dismiss();
                    }
                });
                shelfDialog.show();
            }
        }
        adapter.notifyDataSetChanged();
        //提示dialog
        //TakeGoodsDialog shelfDialog = new TakeGoodsDialog(mActivity, 300, 160, R.layout.show_tip_dialog, R.style.Theme_dialog);
    }

    /***
     * 批量换样（非鞋类）
     */
    void batchChangeSample() {
        startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.BatchChangeSample, WHAT_NET_BATCH_CHANGE_SAMPLE, NET_METHOD_POST, false);
    }

    /***
     * 换样一只（传0.5），换样一双（传1）（鞋类）
     */
    void changeSample() {
        startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.CHANGE_SAMPLE, WHAT_NET_CHANGE_SAMPLE, NET_METHOD_POST, false);
    }

    private List<MenuItem> mMenuList = new ArrayList<MenuItem>();

    private class MenuItem {
        public String MenuId;
        public String MenuName;

        public MenuItem() {
        }

        public MenuItem(String id, String MenuName) {
            this.MenuId = id;
            this.MenuName = MenuName;
        }
    }

    private class MenuAdatper extends BaseAdapter {

        @Override
        public int getCount() {
            return mMenuList.size();
        }

        @Override
        public Object getItem(int position) {
            return mMenuList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(mActivity);
            textView.setGravity(Gravity.CENTER | Gravity.CENTER_VERTICAL);
            textView.setText(mMenuList.get(position).MenuName);
            textView.setTextColor(getResources().getColor(R.color.text_color));
            textView.setPadding(10, 10, 10, 10);
            return textView;
        }
    }
    /**
	 * 搜索框文字监听器
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s.equals("")) {
			  mList.clear();
			  mList.addAll(searchList);
			  adapter.notifyDataSetChanged();
			} else {
				 mList.clear();
				 for (int i = 0; i < searchList.size(); i++) {
					if (searchList.get(i).SKUID.contains(s.toString().toUpperCase())) {
						mList.add(searchList.get(i));
					}
				}
				 adapter.notifyDataSetChanged();
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
				// 先隐藏键盘
				hideSoftKeyBoard(mActivity, searchEdt);
			}
			return true;
		}
	};

	/**
	 * 隐藏软键盘
	 */
	public void hideSoftKeyBoard(Context context, View editText) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
	}
}
