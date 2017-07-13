package cn.jitmarketing.hot.ui.shelf;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ShelfExpandAdapter;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.util.SkuUtil;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

/**商品上架明细界面二*/
public class DetailInfoActivity extends BaseSwipeOperationActivity implements OnClickListener,OnItemClickListener{

	@ViewInject(R.id.text_title)
	TextView text_title;
	@ViewInject(R.id.btn_back)
	ImageView btn_back;
	@ViewInject(R.id.only_list)
	ExpandableListView list_detail;
	@ViewInject(R.id.expand_add)
	TextView expand_add;
	@ViewInject(R.id.expand_ok)
	TextView expand_ok;
	@ViewInject(R.id.expand_cancel)
	TextView expand_cancel;

	private ArrayList<ShelfBean> wareList;
	private ShelfExpandAdapter shelfExpandAdapter;

	@Override
	protected void exInitAfter() {
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_only_expand_list;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exInitView() {
		text_title.setText("手动上架");
		btn_back.setOnClickListener(this);
		expand_ok.setVisibility(View.VISIBLE);
		expand_ok.setOnClickListener(this);
		expand_cancel.setOnClickListener(this);
		expand_add.setOnClickListener(this);
		wareList = (ArrayList<ShelfBean>) getIntent().getSerializableExtra("wareList");
		shelfExpandAdapter = new ShelfExpandAdapter(mActivity, wareList);
		list_detail.setAdapter(shelfExpandAdapter);
		list_detail.setGroupIndicator(null);  
		for (int i = 0; i < wareList.size(); i++) {  
			list_detail.expandGroup(i);  
		}  
	}

	@Override
	public void onReceiver(Intent intent) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
	}

	@Override
	public Map<String, String> onStart(int what) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> onStartNetParam(int what) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(int what, InputStream result, HashMap<String, String> cookies, boolean hashCache) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.expand_ok:
			//TODO 手工上架
			Intent intent = new Intent();
			intent.putExtra("wareList", wareList);
			setResult(Activity.RESULT_OK,intent);
			this.finish();
			break;
		case R.id.expand_cancel:
			this.finish();
			break;
		}

	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub
		
	}

}
