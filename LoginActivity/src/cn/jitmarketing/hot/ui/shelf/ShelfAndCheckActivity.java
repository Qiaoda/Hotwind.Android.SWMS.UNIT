package cn.jitmarketing.hot.ui.shelf;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.ex.lib.ext.xutils.annotation.ViewInject;

import java.util.ArrayList;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

public class ShelfAndCheckActivity extends BaseSwipeBackAcvitiy implements OnClickListener {
    @ViewInject(R.id.check_title)
    TitleWidget check_title;
    @ViewInject(R.id.check_list)
    ListView check_list;

    private ArrayList<InStockDetail> skuList;
    private ArrayList<InStockDetail> sendList;
    private CheckStockListAdapter listAdapter;

    @Override
    protected void exInitAfter() {}

    @Override
    protected void exInitBundle() {
        initIble(this, this);
    }

    @Override
    protected String[] exInitReceiver() {
        return null;
    }

    @Override
    protected int exInitLayout() {
        return R.layout.activity_in_check;
    }

    @Override
    protected void exInitView() {
        check_title.setText("核对");
        check_title.setOnLeftClickListner(this);
        skuList = (ArrayList<InStockDetail>) getIntent().getSerializableExtra("skuList");
        sendList = (ArrayList<InStockDetail>) getIntent().getSerializableExtra("sendList");
        listAdapter = new CheckStockListAdapter(getLayoutInflater(), SkuUtil.cbCheck(skuList, sendList));
        check_list.setAdapter(listAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_left:
                this.finish();
                break;
            case R.id.check_list:
                break;
        }
    }
}
