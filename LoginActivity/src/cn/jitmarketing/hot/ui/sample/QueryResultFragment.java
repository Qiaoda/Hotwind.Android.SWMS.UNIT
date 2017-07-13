package cn.jitmarketing.hot.ui.sample;

import android.app.Activity;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import java.util.List;

import cn.jitmarketing.hot.BaseFragment;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.StockListAdapter;
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.util.ImageUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import de.greenrobot.event.EventBus;

/**
 * 扫描条码后的查询结果fragment
 */
public class QueryResultFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @ViewInject(R.id.stock_sku_code)
    private TextView tv_productCode;  // 商品编码
    @ViewInject(R.id.stock_sku_icon)
    private ImageView iv_productIcon; // 商品图片
    @ViewInject(R.id.stock_sku_name)
    private TextView tv_productName;  // 商品名称
    @ViewInject(R.id.stock_sku_price)
    private TextView tv_productPrice; // 商品价格
    @ViewInject(R.id.stock_sku_change_price)//折扣价
    private TextView stock_sku_change_price;
    @ViewInject(R.id.stock_sku_stock)
    private EditText tv_stockSku;     // 商品库位

    @ViewInject(R.id.list_sku)
    private ListView lv_productList;  // 商品库存量

    /**
     * 上下文
     */
    private Activity mActivity;
    /**
     * 商品实体
     */
    private ItemEntity mItemEntity;
    /**
     * 该商品对应库存量
     */
    private List<SkuEntity> mList;

    private SkuEntity mCurSkuEntity;
    private StockListAdapter adapter;

    private int type = 0;//默认0出样 1撤样

    public QueryResultFragment(ItemEntity itemEntity, List<SkuEntity> list, OnItemSelectedListener listener, int type) {
        mItemEntity = itemEntity;
        mList = list;
        this.listener = listener;
        this.type = type;
    }

    public QueryResultFragment(ItemEntity itemEntity, List<SkuEntity> list, OnItemSelectedListener
            listener) {
        mItemEntity = itemEntity;
        mList = list;
        this.listener = listener;
    }

    @Override
    protected int exInitLayout() {
        return R.layout.fragment_query_result;
    }

    @Override
    protected void exInitBundle() {
        super.exInitBundle();

        mActivity = getActivity();
        if (mList != null && mList.size() > 0)
            mCurSkuEntity = mList.get(0);

        adapter = new StockListAdapter(mActivity, mList);
    }

    @Override
    protected void exInitView(View root) {
        super.exInitView(root);

        lv_productList.setOnItemClickListener(this);
        lv_productList.setAdapter(adapter);
        
        setViewsValue();
        lv_productList.setSelection(HotConstants.SELECTED);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        // 点击item则item被选中，改变当前的商品编码、名称、图片、价格、库位
        HotConstants.SELECTED = position;
        setViewsValue();
        listener.onItemSelected(mItemEntity, mCurSkuEntity);
        EventBus.getDefault().post(new String(mList.get(position).SKUCode));
    }

    private void setViewsValue() {
        mCurSkuEntity = mList.get(HotConstants.SELECTED);
        // 编码
        tv_productCode.setText(mCurSkuEntity.SKUCode);
        // 名称
        tv_productName.setText(mItemEntity.ItemName);
     // 原价格
        tv_productPrice.setText("￥" + SkuUtil.formatPrice(mCurSkuEntity.Price));
        if (!TextUtils.isEmpty(mCurSkuEntity.ChangePrice)) {
        	tv_productPrice.setTextColor(getResources().getColor(R.color.color_black_text));
        	tv_productPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        	stock_sku_change_price.setText(SkuUtil.formatPrice(mCurSkuEntity.ChangePrice));
		}
        
        // 库位
        if (type == 1) {//撤样,显示出样的库位
            //鞋类显示所有库位，非鞋类显示样品库位
            if (!mItemEntity.IsSomeSampling)
                tv_stockSku.setText(mCurSkuEntity.SampleTJ);
            else
                tv_stockSku.setText(mCurSkuEntity.StockSKUShelfLocationString);
        } else
            tv_stockSku.setText(mCurSkuEntity.StockSKUShelfLocationString);
        // 图片
        String url; // 图片url
        if (mCurSkuEntity.ColorID == null || mCurSkuEntity.ColorID.equals("")) {
            url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + mCurSkuEntity.SKUCode + ".jpg";
        } else {
            url = HotConstants.Global.APP_URL_USER + "ItemImgs/" + mItemEntity.ItemID + "$" +
                    mCurSkuEntity.ColorID + ".jpg";
        }
        ImageUtil.uploadImage(mActivity, url, iv_productIcon);

        adapter.notifyDataSetChanged();
    }

    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(ItemEntity itemEntity, SkuEntity skuEntity);
    }

    /**
     * 拿到当前商品实体
     */
    public ItemEntity getItemEntity() {
        return mItemEntity;
    }

    /**
     * 拿到当前商品库存量
     */
    public SkuEntity getCurSkuEntity() {
        return mCurSkuEntity;
    }
}
