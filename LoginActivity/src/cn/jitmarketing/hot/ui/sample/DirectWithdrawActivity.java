package cn.jitmarketing.hot.ui.sample;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ScanListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.OutAndWithDrawBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ScanSkuShelf;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;
import cn.jitmarketing.hot.util.ImageUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.util.TakeGoodsDialog;
import cn.jitmarketing.hot.view.MoreCustomDialog;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 直接出、直接撤页面
 * 注释：
 * 非鞋类直接出
 * 非鞋类直接撤（批量）
 * 鞋类直接撤（单个，库位一致）
 * Created by fgy on 2016/4/7.
 */
public class DirectWithdrawActivity extends BaseSwipeOperationActivity implements View
        .OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    /**
     * title控件
     */
    @ViewInject(R.id.check_stock_title)
    TitleWidget check_stock_title;
    /**
     * sku详细信息布局
     */
    @ViewInject(R.id.layout_content)
    LinearLayout layout_content;
    /**
     * sku列表
     */
    @ViewInject(R.id.list_sku)
    ListView list_sku;
    /**
     * 商品编码
     */
    @ViewInject(R.id.stock_sku_code)
    TextView stock_sku_code;
    /**
     * 商品名称
     */
    @ViewInject(R.id.stock_sku_name)
    TextView stock_sku_name;
    /**
     * 商品原价格
     */
    @ViewInject(R.id.stock_sku_price)
    TextView stock_sku_price;
    /**
     * 商品优惠价格
     */
    @ViewInject(R.id.stock_sku_change_price)
    TextView stock_sku_change_price;
    /**
     * 商品数量
     */
    @ViewInject(R.id.stock_sku_num)
    TextView stock_sku_num;
    /**
     * 库存
     */
    @ViewInject(R.id.stock_sku_stock)
    EditText stock_sku_stock;
    /**
     * 商品图标
     */
    @ViewInject(R.id.stock_sku_icon)
    ImageView stock_sku_icon;

    /**
     * 取新
     */
    @ViewInject(R.id.get_new)
    TextView get_new;
    /**
     * 另一只
     */
    @ViewInject(R.id.get_the_other)
    TextView get_the_other;
    /**
     * 出样
     */
    @ViewInject(R.id.get_the_sample)
    TextView get_the_sample;

    private static final int WHAT_NET_GET_STOCK = 0x10;
    private static final int WHAT_NET_DIRECT_OUT_SAMPLE = 0x11;
    private static final int WHAT_NET_DIRECT_REVOKE_SAMPLE = 0x12;
    private static final int WHAT_NET_SHOES_DIRECT_SAMPLE = 0x13;

    HotApplication ap;
    public static final int OUT = 0;     //直接出
    public static final int WITHDRAW = 1;//直接撤
    private int type;
    private int mSelScanSkuShelfPos = -1;
    public static boolean sampleSuccess;
    private static int scanNum = 0;
    private ScanSkuShelf curScanSkuShelf;

    private String skuCodeStr = "";
    private int selCount = 0;
    private ScanListAdapter scanListAdapter;
    private ArrayList<ScanSkuShelf> scanSkulist = new ArrayList<ScanSkuShelf>();
    private ItemEntity mItemEntity;
    private SkuEntity mCurSkuEntity;
    private OutAndWithDrawBean mWithdrawBean;

    private TakeGoodsDialog shelfDialog;
    private SkuEditText mSkuEditText;
    private Button jian_btn;
    private EditText num_edit;
    private Button jia_btn;
    private TextView btn_ok;
    private TextView btn_cancel;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            finish();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void exInitAfter() {
    }

    @Override
    protected void exInitBundle() {
        initIble(this, this);
        mCurSkuEntity = (SkuEntity) getIntent().getSerializableExtra("sku");
        mItemEntity = (ItemEntity) getIntent().getSerializableExtra("item");
        mWithdrawBean = (OutAndWithDrawBean) getIntent().getSerializableExtra("withdrawBean");
        skuCodeStr = getIntent().getStringExtra("skuCodeStr");
        selCount = getIntent().getIntExtra("selCount", 0);
        type = getIntent().getIntExtra("type", WITHDRAW);
    }

    @Override
    protected int exInitLayout() {
        return R.layout.activity_direct_withdraw;
    }

    @Override
    protected void exInitView() {
        scanNum = 0;
        ap = (HotApplication) getApplication();
        get_new.setOnClickListener(this);
        get_the_other.setOnClickListener(this);
        get_the_sample.setOnClickListener(this);
        check_stock_title.setOnLeftClickListner(this);
        check_stock_title.setOnRightClickListner(this);
        stock_sku_stock.setOnClickListener(this);
        get_new.setEnabled(false);
        if (type == OUT)
            check_stock_title.setText(getString(R.string.direct_out));

        setViewsValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public Map<String, String> onStart(int what) {
        /*switch (what) {
            case WHAT_NET_GET_STOCK:
                return SkuNet.getStoreForSKU(skuCodeStr);
            case WHAT_NET_DIRECT_OUT_SAMPLE:
                return SkuNet.directOutSample(skuCodeStr, scanSkulist);
            case WHAT_NET_DIRECT_REVOKE_SAMPLE:
                return SkuNet.directRevokeSample(skuCodeStr, scanSkulist);
            case WHAT_NET_SHOES_DIRECT_SAMPLE:
                return SkuNet.shoesDirectSample(mWithdrawBean.getSKUID(), scanSkulist.get(0)
                .ShelfLocationCode, mWithdrawBean.getMappingID());
        }*/
        return null;
    }

    @Override
    public void onReceiver(Intent intent) {
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void fillCode(String code) {
        //this.skuCodeStr = code;
        if (code != null) {
            dealBarCode(code);
        } else {
            ap.getsoundPool(ap.Sound_error);
            return;
        }
    }

    /***
     * 非鞋类出样、非鞋类撤样扫描
     */
    private void scanSkuShelf(String skuCodeStr) {
        if (!SkuUtil.isWarehouse(skuCodeStr)) {
            ap.getsoundPool(ap.Sound_sku);
            if (skuCodeStr.equals(this.skuCodeStr)) {
                //if (scanNum >= selCount) {
                if (checkLastSumbitNum()) {
                    Ex.Toast(mActivity).show(R.string.scan_num_gt_sel_num);
                    return;
                }
                scanNum++;
                if (curScanSkuShelf == null) {
                    curScanSkuShelf = new ScanSkuShelf();
                    curScanSkuShelf.SKUCount = 1;
                    scanSkulist.add(curScanSkuShelf);
                    scanListAdapter.notifyDataSetChanged();
                } else {
                    curScanSkuShelf.SKUCount++;
                    scanListAdapter.notifyDataSetChanged();
                }
            }
        } else {
            ap.getsoundPool(ap.Sound_location);
            //如果库位已存在自动合并数量
            int pos = checkRepeatStockPos(skuCodeStr);
            if (pos != -1) {
                curScanSkuShelf = scanSkulist.get(pos);
                ScanSkuShelf last = scanSkulist.get(scanSkulist.size() - 1);
                if (last.ShelfLocationCode == null || last.ShelfLocationCode.length() <= 0) {
                    curScanSkuShelf.SKUCount += last.SKUCount;
                    scanSkulist.remove(last);
                    Ex.Toast(mActivity).show("自动合并库位：" + curScanSkuShelf.ShelfLocationCode + ",数量：" + curScanSkuShelf.SKUCount);
                    scanListAdapter.notifyDataSetChanged();
                    scanNum = 0;
                    curScanSkuShelf = null;
                }
                return;
            }
            if (curScanSkuShelf != null) {
                curScanSkuShelf.ShelfLocationCode = skuCodeStr;
                Ex.Toast(mActivity).show("库位：" + curScanSkuShelf.ShelfLocationCode + ",数量：" + curScanSkuShelf.SKUCount);
                scanListAdapter.notifyDataSetChanged();
                scanNum = 0;
                curScanSkuShelf = null;
            }
        }
    }

    /**
     * 鞋类撤样扫描
     * 至少扫描一次商品
     */
    private void shoesSkuShelf(String skuCodeStr) {
        if (SkuUtil.isWarehouse(skuCodeStr)) {
            ap.getsoundPool(ap.Sound_location);
            if (scanNum <= 0) {
                Ex.Toast(mActivity).show("请先扫商品！");
                return;
            }

            if (mWithdrawBean != null && !TextUtils.isEmpty(mWithdrawBean.getShelfLocationCode())
                    && mWithdrawBean.getShelfLocationCode().equals(skuCodeStr)) {
                scanSkulist.clear();
                ScanSkuShelf shelf = new ScanSkuShelf();
                scanSkulist.add(shelf);
                shelf.ShelfLocationCode = skuCodeStr;
                shelf.SKUCount = 1;
                scanListAdapter.notifyDataSetChanged();
            } else {
                Ex.Toast(mActivity).show("请扫描库位：" + mWithdrawBean.getShelfLocationCode());
                return;
            }
        } else {
            ap.getsoundPool(ap.Sound_sku);
            if (skuCodeStr.equals(this.skuCodeStr)) {
                scanNum++;
            } else scanNum = 0;
        }
    }

    private void dealBarCode(String skuCodeStr) {
        //手动dialog
        if (shelfDialog != null && shelfDialog.isShowing()) {
            //库位代码
            if (SkuUtil.isWarehouse(skuCodeStr)) {
                ap.getsoundPool(ap.Sound_location);
                if (mItemEntity.IsSomeSampling) {
                    if (mWithdrawBean.getShelfLocationCode().toUpperCase().equals(skuCodeStr
                            .toUpperCase())) {
                        mSkuEditText.setText(skuCodeStr);
                    } else {
                        Ex.Toast(mActivity).show("请扫描库位：" + mWithdrawBean.getShelfLocationCode());
                        return;
                    }
                } else if (mSelScanSkuShelfPos == -1) {
                    mSkuEditText.setText(skuCodeStr);
                }
            } else
                ap.getsoundPool(ap.Sound_sku);
        } else {//直接扫描
            if (type == OUT) {//直接出
                if (!mItemEntity.IsSomeSampling) {//非鞋类直接出
                    scanSkuShelf(skuCodeStr);
                } else
                    Ex.Toast(mActivity).show("商品为鞋类");
            } else if (type == WITHDRAW) {//直接撤
                if (!mItemEntity.IsSomeSampling) {//非鞋类直接撤
                    scanSkuShelf(skuCodeStr);
                } else {
                    shoesSkuShelf(skuCodeStr);
                }
            }
        }
        enabledOkBtn();
    }

    /***
     * 确定按钮可用状态
     */
    void enabledOkBtn() {
        boolean isUse = true;
        if (scanSkulist != null && scanSkulist.size() > 0) {
            for (ScanSkuShelf scanSkuShelf : scanSkulist) {
                if (TextUtils.isEmpty(scanSkuShelf.ShelfLocationCode)) {
                    isUse = false;
                    break;
                }
            }
            if (isUse)
                get_new.setEnabled(true);
            else
                get_new.setEnabled(false);
        } else {
            get_new.setEnabled(false);
        }
    }

    @Override
    public void onError(int what, int e, String message) {
        sampleSuccess = false;
        Toast.makeText(this, "网络请求失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(int what, String result, boolean isCache) {
        sampleSuccess = false;
        ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
        if (!net.isSuccess) {
            Ex.Toast(mContext).showLong(net.message);
            return;
        }
        switch (what) {
            case WHAT_NET_GET_STOCK:
                if (null == net.data) {
                    Ex.Toast(mActivity).show(net.message);
                    layout_content.setVisibility(View.GONE);
                    return;
                } else {
                    layout_content.setVisibility(View.VISIBLE);
                }
                try {
                    String skulistStr = new JSONObject(mGson.toJson(net.data)).getString("skus");
                    String itemStr = new JSONObject(mGson.toJson(net.data)).getString("item");
                    mItemEntity = mGson.fromJson(itemStr, ItemEntity.class);
                    setViewsValue();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            case WHAT_NET_DIRECT_OUT_SAMPLE:
            case WHAT_NET_DIRECT_REVOKE_SAMPLE:
            case WHAT_NET_SHOES_DIRECT_SAMPLE:
                Ex.Toast(mActivity).show(net.message);
                setResult(RESULT_OK);
                this.finish();
                break;
        }
    }

    /***
     * sku信息
     */
    private void setViewsValue() {
        if (mCurSkuEntity == null || mItemEntity == null)
            return;
        // 编码
        stock_sku_code.setText(mCurSkuEntity.SKUCode);
        // 名称
        stock_sku_name.setText(mItemEntity.ItemName);
        // 价格 
        stock_sku_price.setText("￥" + SkuUtil.formatPrice(mCurSkuEntity.Price));
        // 库位
        if (type == OUT)
            stock_sku_stock.setText(mCurSkuEntity.StockSKUShelfLocationString);
        else if (type == WITHDRAW) {
            if (mItemEntity.IsSomeSampling) {
                String tip = mWithdrawBean.getShelfLocationCode();
                if (mWithdrawBean.getCount() == 1) {
                    tip += "(1双)";
                } else
                    tip += "(1只)";
                stock_sku_stock.setText(tip);
            } else
                stock_sku_stock.setText(mCurSkuEntity.SampleTJ);
        }
        // 图片
        String url; // 图片url
        if (mCurSkuEntity.ColorID == null || mCurSkuEntity.ColorID.equals("")) {
            url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + mCurSkuEntity.SKUCode + ".jpg";
        } else {
            url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + mItemEntity.ItemID + "$" +
                    mCurSkuEntity.ColorID + ".jpg";
        }
        ImageUtil.uploadImage(mActivity, url, stock_sku_icon);
        if (scanListAdapter == null) {
            scanListAdapter = new ScanListAdapter(this, scanSkulist);
            list_sku.setAdapter(scanListAdapter);
            list_sku.setOnItemClickListener(this);
            list_sku.setOnItemLongClickListener(this);
        } else {
            scanListAdapter.notifyDataSetChanged();
        }
        list_sku.setSelection(HotConstants.SELECTED);
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View view, final int position, long id) {
        //mSelScanSkuShelfPos = position;
        //showSkuShelfDialog();
        if (mItemEntity.IsSomeSampling)
            return;
        new MoreCustomDialog(mActivity, (int) scanSkulist.get(position).SKUCount, new MoreCustomDialog
                .OnCustomDialogListener() {

            @Override
            public void delete() {
                scanSkulist.remove(position);
                scanListAdapter.notifyDataSetChanged();
            }

            @Override
            public void confirm(int num) {
                scanSkulist.get(position).SKUCount = num;
                scanListAdapter.notifyDataSetChanged();
            }
        }).show();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
        return false;
    }

    /***
     * 检测数量
     * 若编辑数量大于选择数量，返回true,阻止提交
     * 使用：非鞋类直接出、非鞋类直接撤
     *
     * @return
     */
    private boolean checkLastSumbitNum() {
        if (scanSkulist != null && scanSkulist.size() > 0) {
            int sum = 0;
            for (ScanSkuShelf scanSkuShelf : scanSkulist)
                sum += scanSkuShelf.SKUCount;
            if (sum > selCount) {
                Ex.Toast(mActivity).show(R.string.scan_num_gt_sel_num);
                return true;
            } else
                return false;
        }
        return false;
    }

    /***
     * 检测重复库位所在索引
     *
     * @param stockCode
     * @return
     */
    private int checkRepeatStockPos(String stockCode) {
        if (scanSkulist != null && scanSkulist.size() > 0) {
            for (ScanSkuShelf scanSkuShelf : scanSkulist) {
                if (scanSkuShelf.ShelfLocationCode != null && scanSkuShelf.ShelfLocationCode.toUpperCase().equals(stockCode)) {
                    return scanSkulist.indexOf(scanSkuShelf);
                }
            }
        }
        return -1;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.lv_left:
                this.finish();
                break;
            case R.id.get_new:
                if (FastDoubleClickUtil.isFastDoubleClick()) {
                    Log.i("fast", "fast");
                    return;
                }
                if (type == OUT) {//直接出

                    if (checkLastSumbitNum())
                        return;

                    if (!mItemEntity.IsSomeSampling) {//非鞋类直接出
                        startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU
                                        .DirectOutSample,
                                WHAT_NET_DIRECT_OUT_SAMPLE,
                                true,
                                NET_METHOD_POST,
                                SkuNet.directOutSampleJson(skuCodeStr, scanSkulist),
                                false);
                    } else
                        Ex.Toast(mActivity).show("商品为鞋类");
                } else if (type == WITHDRAW) {//直接撤
                    if (!mItemEntity.IsSomeSampling) {//非鞋类直接撤

                        if (checkLastSumbitNum())
                            return;

                        startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU
                                        .DirectRevokeSample,
                                WHAT_NET_DIRECT_REVOKE_SAMPLE,
                                true,
                                NET_METHOD_POST,
                                SkuNet.directRevokeSampleJson(skuCodeStr, scanSkulist),
                                false);
                    } else {
                        startJsonTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU
                                        .ShoesDirectSample,
                                WHAT_NET_SHOES_DIRECT_SAMPLE,
                                true, NET_METHOD_POST,
                                SkuNet.shoesDirectSampleJson(skuCodeStr, scanSkulist.get(0)
                                        .ShelfLocationCode, mWithdrawBean.getMappingID()),
                                false);
                    }
                }
                break;
            case R.id.get_the_sample:
                if (FastDoubleClickUtil.isFastDoubleClick()) {
                    Log.i("fast", "fast");
                    return;
                }
                mSelScanSkuShelfPos = -1;
                showSkuShelfDialog();
                break;
            case R.id.jian_btn:
                if (Integer.valueOf(num_edit.getText().toString()) == 0)
                    return;
                num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) -
                        1));
                num_edit.setSelection(num_edit.getText().toString().length());
                break;
            case R.id.jia_btn:
                if (checkLastSumbitNum())
                    return;
                num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) +
                        1));
                num_edit.setSelection(num_edit.getText().toString().length());
                break;
            case R.id.btn_ok:
                if (mSkuEditText.getText(mActivity).length() > 0) {
                    if (mSelScanSkuShelfPos == -1) {
                        ScanSkuShelf scanSku = new ScanSkuShelf();
                        scanSku.ShelfLocationCode = mSkuEditText.getText(mActivity);
                        scanSku.SKUCount = Integer.parseInt(num_edit.getText() + "");
                        if (scanSku.SKUCount > 0)
                            scanSkulist.add(scanSku);
                    } else {
                        int count = Integer.parseInt(num_edit.getText() + "");
                        if (count > 0) {
                            scanSkulist.get(mSelScanSkuShelfPos).ShelfLocationCode = mSkuEditText
                                    .getText(mActivity);
                            scanSkulist.get(mSelScanSkuShelfPos).SKUCount = count;
                        } else {
                            scanSkulist.remove(mSelScanSkuShelfPos);
                            mSelScanSkuShelfPos = -1;
                        }
                    }
                    scanListAdapter.notifyDataSetChanged();
                    enabledOkBtn();
                } else {
                    if (mSelScanSkuShelfPos != -1) {
                        scanSkulist.remove(mSelScanSkuShelfPos);
                        scanListAdapter.notifyDataSetChanged();
                    }
                }
                mSelScanSkuShelfPos = -1;
                if (shelfDialog != null)
                    shelfDialog.dismiss();
                break;
            case R.id.btn_cancel:
                mSelScanSkuShelfPos = -1;
                if (shelfDialog != null)
                    shelfDialog.dismiss();
                break;
        }
    }

    void showSkuShelfDialog() {
        if (shelfDialog == null) {
            shelfDialog = new TakeGoodsDialog(mActivity, R.layout.show_sku_shelf_dialog, R.style
                    .Theme_dialog);
            mSkuEditText = (SkuEditText) shelfDialog.findViewById(R.id.the_customer_shelf);
            jian_btn = (Button) shelfDialog.findViewById(R.id.jian_btn);
            num_edit = (EditText) shelfDialog.findViewById(R.id.num_edit);
            jia_btn = (Button) shelfDialog.findViewById(R.id.jia_btn);
            btn_ok = (TextView) shelfDialog.findViewById(R.id.btn_ok);
            btn_cancel = (TextView) shelfDialog.findViewById(R.id.btn_cancel);
            jian_btn.setOnClickListener(this);
            jia_btn.setOnClickListener(this);
            btn_ok.setOnClickListener(this);
            btn_cancel.setOnClickListener(this);
        }
        if (mSelScanSkuShelfPos != -1) {
            if (!TextUtils.isEmpty(scanSkulist.get(mSelScanSkuShelfPos).ShelfLocationCode))
                mSkuEditText.setText(scanSkulist.get(mSelScanSkuShelfPos).ShelfLocationCode);
            num_edit.setText(scanSkulist.get(mSelScanSkuShelfPos).SKUCount + "");
        } else {
            mSkuEditText.setText("");
            num_edit.setText("0");
        }
        shelfDialog.show();
    }
}
