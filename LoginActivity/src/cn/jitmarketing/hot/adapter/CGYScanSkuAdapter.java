package cn.jitmarketing.hot.adapter;

import java.util.ArrayList;
import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.ui.shelf.ShelfDetailActivity.OldShelfDetailAdapter;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.MoreCustomDialog;
import cn.jitmarketing.hot.view.MoreCustomDialog.OnCustomDialogListener;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CGYScanSkuAdapter extends BaseAdapter {
	
	private List<SkuBean> scanSkuList;
	private LayoutInflater inflater;
	private boolean isCanDelete;
	private Activity activity;
	private List<Boolean> selectList = new ArrayList<Boolean>();
	private OldShelfDetailAdapter detailAdapter;
	private ArrayList<ShelfBean> wareList;
	int pos;

	public CGYScanSkuAdapter(LayoutInflater inflater, List<SkuBean> scanSkuList) {
		this.inflater = inflater;
		this.scanSkuList = scanSkuList;
	}

	public CGYScanSkuAdapter(Activity activity, LayoutInflater inflater,
			List<SkuBean> scanSkuList, boolean isCanDelete) {
		this.isCanDelete = isCanDelete;
		this.inflater = inflater;
		this.scanSkuList = scanSkuList;
		this.activity = activity;
		for(SkuBean s : scanSkuList) {
			selectList.add(false);
		}
	}
	
	public CGYScanSkuAdapter(Activity activity, LayoutInflater inflater,
			List<SkuBean> scanSkuList, boolean isCanDelete, OldShelfDetailAdapter detailAdapter, ArrayList<ShelfBean> wareList, int pos) {
		this.isCanDelete = isCanDelete;
		this.inflater = inflater;
		this.scanSkuList = scanSkuList;
		this.activity = activity;
		this.detailAdapter = detailAdapter;
		this.wareList = wareList;
		this.pos = pos;
		for(SkuBean s : scanSkuList) {
			selectList.add(false);
		}
	}

	public List<SkuBean> getScanSkuList() {
		return scanSkuList;
	}

	public void setScanSkuList(List<SkuBean> scanSkuList) {
		this.scanSkuList = scanSkuList;
	}

	@Override
	public int getCount() {
		return scanSkuList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return scanSkuList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup arg2) {
		final ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.cgy_scan_stock, null);
			holder = new ViewHolder();
			holder.scan_sku = (TextView) convertView
					.findViewById(R.id.scan_sku);
			holder.scan_count = (TextView) convertView
					.findViewById(R.id.scan_count);
			holder.delete_btn = (Button) convertView
					.findViewById(R.id.delete_btn);
			holder.jian_btn = (Button) convertView
					.findViewById(R.id.jian_btn);
			holder.jia_btn = (Button) convertView
					.findViewById(R.id.jia_btn);
			holder.num_edit = (EditText) convertView
					.findViewById(R.id.num_edit);
			holder.linearLayout1 = (LinearLayout) convertView
					.findViewById(R.id.linearLayout1);
			holder.more_btn = (Button) convertView
					.findViewById(R.id.more_btn);
			holder.confirm_btn = (Button) convertView
					.findViewById(R.id.confirm_btn);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.scan_sku.setText(scanSkuList.get(position).skuCode);
		holder.scan_count.setText("" + (int) scanSkuList.get(position).skuCount);
		if (isCanDelete) {
			convertView.setEnabled(true);
		} else {
			convertView.setEnabled(false);
		}
		convertView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				//日志操作
				LogUtils.logOnFile("盘点->库位盘点->明细->Item长按->数量操作");
				if(isCanDelete) {
					new MoreCustomDialog(activity, (int)scanSkuList.get(position).skuCount, new OnCustomDialogListener() {
						
						@Override
						public void delete() {
							scanSkuList.remove(position);
							selectList.remove(position);
							notifyDataSetChanged();
							if(scanSkuList.size() == 0 && wareList != null && detailAdapter != null) {
								wareList.remove(pos);
								detailAdapter.notifyDataSetChanged();
							}
						}
						
						@Override
						public void confirm(int num) {
							scanSkuList.get(position).skuCount = num;
							notifyDataSetChanged();
						}
					}).show();
				}
				return false;
			}
		});
		return convertView;
	}
	
	public class ViewHolder {
		public TextView scan_sku;
		public TextView scan_count;
		public Button delete_btn;
		public Button jian_btn;
		public EditText num_edit;
		public Button jia_btn;
		public Button more_btn;
		public Button confirm_btn;
		public LinearLayout linearLayout1;
	}
}
