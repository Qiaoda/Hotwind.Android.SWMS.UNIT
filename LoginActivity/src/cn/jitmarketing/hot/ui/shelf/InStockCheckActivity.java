package cn.jitmarketing.hot.ui.shelf;

import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.BeforeCheckStockListAdapter;
import cn.jitmarketing.hot.adapter.CheckStockListAdapter;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.InStockSku;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.TitleWidget;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;

/**入库核对界面*///6956511905164
public class InStockCheckActivity extends BaseSwipeBackAcvitiy implements OnClickListener{

	@ViewInject(R.id.check_title)
	TitleWidget check_title;
	@ViewInject(R.id.check_list)
	ListView check_list;
	ArrayList<BeforeInStockBean> skuList=new ArrayList<BeforeInStockBean>();
	private ArrayList<BeforeInStockBean> sendList;
	private CheckStockListAdapter listAdapter;

	@Override
	protected void exInitAfter() {
         
	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}
   @Override
  protected String[] exInitReceiver() {
	// TODO Auto-generated method stub
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
		sendList = (ArrayList<BeforeInStockBean>) getIntent().getSerializableExtra("sendList");
	    skuList = (ArrayList<BeforeInStockBean>) getIntent().getSerializableExtra("skuList");
		listAdapter = new CheckStockListAdapter(getLayoutInflater(), SkuUtil.newcbCheck(skuList, sendList));
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
