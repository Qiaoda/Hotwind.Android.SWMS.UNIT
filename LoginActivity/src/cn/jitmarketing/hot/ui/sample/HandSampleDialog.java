package cn.jitmarketing.hot.ui.sample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
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
import cn.jitmarketing.hot.entity.ItemEntity;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SampleEntity;
import cn.jitmarketing.hot.entity.SkuEntity;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.ui.sku.CheckStockActivity;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;

public class HandSampleDialog extends BaseSwipeBackAcvitiy implements
        OnClickListener {

    @ViewInject(R.id.sku_txt)
    TextView sku_txt;
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

    private static final int WHAT_NET_REVOIKE_BATCH_SAMPLE = 0x01;//批量撤样
    public static final int OPERATE_TYPE_OUT_SAMPLE = 0X18;      // 出样
    public static final int OPERATE_TYPE_WITHDRAW_SAMPLE = 0X19; // 撤样
    private int mOperateType = -1;

    private SampleEntity sampleEntity;// 其中的属性type,1是取另一只，2是取样，3是换样，4是撤样
    private String skuCodeStr;
    private SkuEntity sku;
    private ItemEntity item;
    private String SKUCount;

    @Override
    protected int exInitLayout() {
        return R.layout.hand_sample_dialog;
    }

    @Override
    protected void exInitView() {
        Bundle bd = getIntent().getExtras();
        mOperateType = bd.getInt("operateType", -1);

        sampleEntity = (SampleEntity) bd.getSerializable("sampleEntity");
        skuCodeStr = bd.getString("skuCodeStr");
        item = (ItemEntity) bd.getSerializable("item");
        sku = (SkuEntity) bd.getSerializable("sku");
        SKUCount = sampleEntity.EndQty;

        sku_txt.setText(sku.SKUCode);
        if (sampleEntity.isCanBatch) {
            more_layout.setVisibility(View.VISIBLE);
            default_num_txt.setVisibility(View.GONE);
        } else {
            more_layout.setVisibility(View.GONE);
            default_num_txt.setVisibility(View.VISIBLE);
        }
        switch (mOperateType) {
            case OPERATE_TYPE_OUT_SAMPLE:
                send_req_cancel.setText("出样");
                send_req_ok.setText("直接出");
                break;
            case OPERATE_TYPE_WITHDRAW_SAMPLE:
                send_req_cancel.setText("撤样");
                send_req_ok.setText("直接撤");
                break;
            default:
                Toast.makeText(this, "请初始化操作类型", Toast.LENGTH_SHORT).show();
                break;
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
                        //						num_edit.setText("1");
                        //						num_edit.setSelection(1);
                    } else if (s.toString().equals("0")) {
                        //						num_edit.setText("1");
                        //						num_edit.setSelection(1);
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
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
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
            case WHAT_NET_REVOIKE_BATCH_SAMPLE:
                Ex.Toast(mActivity).show(net.message);
                setResult(RESULT_OK);
                this.finish();
                break;
        }
    }

    @Override
    public Map<String, String> onStart(int what) {
        switch (what) {
            case WHAT_NET_REVOIKE_BATCH_SAMPLE:
                return SkuNet.getRevokeSample(skuCodeStr, num_edit.getText().toString());
        }
        return null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_req_cancel:
                if (FastDoubleClickUtil.isFastDoubleClick()) {
                    return;
                }
                if (Integer.parseInt(num_edit.getText() + "") <= 0) {
                    Ex.Toast(mActivity).show("数量不可为0");
                    return;
                }
                //撤样（非鞋类）
                if (mOperateType == OPERATE_TYPE_WITHDRAW_SAMPLE) {
                    startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.BatchRevokeSample, WHAT_NET_REVOIKE_BATCH_SAMPLE, NET_METHOD_POST, false);
                }
                break;
            case R.id.send_req_ok:
                if (mOperateType == OPERATE_TYPE_WITHDRAW_SAMPLE) {
                    Bundle bd = new Bundle();
                    bd.putSerializable("type", DirectWithdrawActivity.WITHDRAW);
                    bd.putSerializable("item", item);
                    bd.putSerializable("sku", sku);
                    bd.putSerializable("skuCodeStr", skuCodeStr);
                    bd.putSerializable("skuCode", skuCodeStr);
                    bd.putInt("selCount", Integer.parseInt(num_edit.getText().toString()));
                    Ex.Activity(mActivity).start(DirectWithdrawActivity.class, bd);
                    this.finish();
                    return;
                }
                if (true) {
                    this.finish();
                    return;
                }
                break;
            case R.id.jian_btn:
                if (Integer.valueOf(num_edit.getText().toString()) == 0)
                    return;
                num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) - 1));
                num_edit.setSelection(num_edit.getText().toString().length());
                break;
            case R.id.jia_btn:
                //不可大于出样数量
                if (TextUtils.isEmpty(sku.SampleCount)) return;
                if (Integer.valueOf(num_edit.getText().toString()) >= Integer.valueOf(sku.SampleCount)) {
                    Ex.Toast(mActivity).show(R.string.not_gt_sample_num);
                    return;
                }
                num_edit.setText(String.valueOf(Integer.valueOf(num_edit.getText().toString()) + 1));
                num_edit.setSelection(num_edit.getText().toString().length());
                break;
        }
    }
}
