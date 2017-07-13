package cn.jitmarketing.hot.ui.shelf;

import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import java.util.ArrayList;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ShelfDetailAdapter;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.util.ChangeValueUtil;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.TitleWidget;

public class ShelfAndStockDetailActivity extends BaseSwipeBackAcvitiy implements
        OnClickListener {
    @ViewInject(R.id.shelf_detail_title)
    TitleWidget shelf_detail_title;
    ListView detail_lv;

    private ArrayList<InStockDetail> detailList; // 明细（暂存）list
    private ArrayList<InStockDetail> checkList;  // 核对list

    private static final int FOR_RESULT_HAND = 0x10;
    ShelfDetailAdapter adapter;

    private boolean isNoValue;

    @Override
    protected void exInitBundle() {
        initIble(this, this);
    }

    @Override
    protected int exInitLayout() {
        return R.layout.activity_shelf_detail;
    }

    @Override
    protected void exInitView() {
        shelf_detail_title.setText("明细");
        shelf_detail_title.setOnLeftClickListner(this);
        shelf_detail_title.setOnRightClickListner(this);
        detail_lv = (ListView) findViewById(R.id.detail_lv);

        detailList = (ArrayList<InStockDetail>) getIntent().getSerializableExtra("skuList");
        checkList = (ArrayList<InStockDetail>) getIntent().getSerializableExtra("checkList");

        adapter = new ShelfDetailAdapter(this, getLayoutInflater(), ChangeValueUtil.value(detailList));
        detail_lv.setAdapter(adapter);
        if (detailList.size() == 0) {//传入的时候就没有数据
            isNoValue = true;
        } else {
            isNoValue = false;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_left:
            	//日志操作
            	LogUtils.logOnFile("明细->返回");
                Intent intent1 = new Intent(ShelfAndStockDetailActivity.this,
                        MainShelfAndActivity.class);
                //			intent1.putExtra("wList", detailList);
                intent1.putExtra("wList", ChangeValueUtil.reverseValue(adapter.getWareList()));
                intent1.putExtra("isNoValue", isNoValue);
                setResult(RESULT_OK, intent1);
                finish();
                break;
            case R.id.lv_right://添加
            	//日志操作
            	LogUtils.logOnFile("明细->添加");
                Intent intent = new Intent();
                intent.setClass(ShelfAndStockDetailActivity.this,
                        HandShelfActivity.class);
                intent.putExtra("detailList", detailList);
                intent.putExtra("checkList", checkList);
                intent.putExtra("type","TYPE_DETAIL");
                startActivityForResult(intent, FOR_RESULT_HAND);
                //关闭扫描服务
                //			if(serviceConnection!=null){
                //				unbindService(serviceConnection);
                //				stopService(new Intent(this,ScanService.class));
                //			}
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == FOR_RESULT_HAND) {
            String location = data.getStringExtra("location");
            String sku = data.getStringExtra("sku");
            String count = data.getStringExtra("count");
            InStockDetail ssb = new InStockDetail(sku, (float) (Integer.valueOf(count).intValue()),
                    location);
            push(ssb);
            adapter = new ShelfDetailAdapter(this, getLayoutInflater(),
                    ChangeValueUtil.value(detailList));
            detail_lv.setAdapter(adapter);
        }
    }

    private void push(InStockDetail item) {
        ArrayList<InStockDetail> list = detailList;
        for (int i = 0; i < list.size(); i++) {
            InStockDetail titem = list.get(i);
            if (titem.SKUCode.equals(item.SKUCode)
                    && titem.ShelfLocationCode.equals(item.ShelfLocationCode)) {
                titem.SKUCount += item.SKUCount;
                return;
            }
        }
        list.add(item);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent1 = new Intent(ShelfAndStockDetailActivity.this,
                    MainShelfAndActivity.class);
            //			intent1.putExtra("wList", detailList);
            intent1.putExtra("wList", ChangeValueUtil.reverseValue(adapter.getWareList()));
            intent1.putExtra("isNoValue", isNoValue);
            setResult(RESULT_OK, intent1);
            finish();
        }
        return false;
    }
}
