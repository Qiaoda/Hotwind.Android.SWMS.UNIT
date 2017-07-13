package cn.jitmarketing.hot.ui.sample;

import android.view.View;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.ui.sku.DirectNewActivity;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * Created by fgy on 2016/4/7.
 */
public class SampleActivity extends BaseSwipeBackAcvitiy implements View.OnClickListener {

    @ViewInject(R.id.allocation_title)
    TitleWidget allocation_title;

    @Override
    protected int exInitLayout() {
        return R.layout.activity_sample;
    }

    @Override
    protected void exInitView() {
        allocation_title.setOnLeftClickListner(this);
        findViewById(R.id.btn_chouy).setOnClickListener(this);
        findViewById(R.id.btn_chey).setOnClickListener(this);
        findViewById(R.id.btn_huany).setOnClickListener(this);
        findViewById(R.id.btn_chouytx).setOnClickListener(this);
        

        
        
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_left:
                finish();
                break;
            case R.id.btn_chouy:
                Ex.Activity(mActivity).start(OutSampleActivity.class, null);
                break;
            case R.id.btn_chey:
                Ex.Activity(mActivity).start(SampleWithdrawActivity.class, null);
                break;
            case R.id.btn_huany:
                Ex.Activity(mActivity).start(ChangeSampleActivity.class, null);
                break;
            case R.id.btn_chouytx:
                Ex.Activity(mActivity).start(SampleRemindActivity.class, null);
                break;
            default:
                break;
        }
    }
}
