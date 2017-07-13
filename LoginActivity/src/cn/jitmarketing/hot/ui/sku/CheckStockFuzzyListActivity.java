package cn.jitmarketing.hot.ui.sku;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.ItemListAdapter;
import cn.jitmarketing.hot.entity.ItemBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.entity.SearchSkuBean;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.ImageUtil;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;

/**模糊查询列表*/
public class CheckStockFuzzyListActivity extends BaseSwipeBackAcvitiy implements OnClickListener, OnItemClickListener{

//	@ViewInject(R.id.btn_back)
//	ImageView btn_search;
//	@ViewInject(R.id.text_title)
//	EditText check_stock_search;
	@ViewInject(R.id.only_list)
	ListView search_list;
    
	private static final int WHAT_NET_GET_STOCK_FUZZY = 0x13;
	private static final int FOR_RESULT_SEARCH = 0x16;
	private ItemListAdapter itemListAdapter;
	private  List<SearchSkuBean> searchSkuList;
    private String searchCondition;
    
    Handler handler=new Handler(){
    	@Override
		public void handleMessage(Message msg){
    		if(msg.what==1){
    			searchSkuList.clear();
    			itemListAdapter.notifyDataSetChanged();
    		}
    	}
    };
	@Override
	protected void exInitAfter() {

	}

	@Override
	protected void exInitBundle() {
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		return R.layout.check_search_list;
	}

	@Override
	protected void exInitView() {
//		check_stock_search=(EditText) findViewById(R.id.check_stock_search);
		search_list=(ListView) findViewById(R.id.search_list);
		search_list.setOnItemClickListener(this);
//		check_stock_search.addTextChangedListener(new TextWatcher() {
//			
//			@Override
//			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
//				// TODO Auto-generated method stub	
//			}	
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//					int arg3) {
//				// TODO Auto-generated method stub	
//			}	
//			@Override
//			public void afterTextChanged(Editable arg0) {
//				// TODO Auto-generated method stub
//				arg0=check_stock_search.getText();
//				searchCondition=arg0.toString();
//				if(searchCondition.length()>0){
//					startTask(HotConstants.Global.APP_URL_USER + HotConstants.SKU.GetStoreForSKUFuzzy, 
//							WHAT_NET_GET_STOCK_FUZZY,false, NET_METHOD_POST, false);
//				}else{
//					handler.sendEmptyMessage(1);
//				}
//			}
//		});
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_GET_STOCK_FUZZY:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				return;
			}
			if(null == net.data){
				Ex.Toast(mActivity).show(net.message);
				return ;
			}
			String itemStr = mGson.toJson(net.data);
			searchSkuList = mGson.fromJson(itemStr,
					new TypeToken<List<SearchSkuBean>>() {}.getType());  
			itemListAdapter = new ItemListAdapter(CheckStockFuzzyListActivity.this, searchSkuList);
			search_list.setAdapter(itemListAdapter);
		}
	}
	@Override
	public Map<String, String> onStart(int what) {
		// TODO Auto-generated method stub
		switch (what) {
		case WHAT_NET_GET_STOCK_FUZZY:		 
			return SkuNet.getStoreForSKUFuzzy(searchCondition);
		}
		return null;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent intent = new Intent();
		intent.putExtra("item", searchSkuList.get(position));
		setResult(RESULT_OK,intent);
		this.finish();
	}

}
