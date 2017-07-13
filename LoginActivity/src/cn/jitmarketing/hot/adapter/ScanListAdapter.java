package cn.jitmarketing.hot.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ScanSkuShelf;

public class ScanListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ScanSkuShelf> skuList;
    private Activity activity;
    private NumberFormat currencyFormatA = NumberFormat.getNumberInstance();

    public ScanListAdapter(Activity activity, List<ScanSkuShelf> skuList) {
        this.activity = activity;
        this.skuList = skuList;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getCount() {
        return skuList.size();
    }

    @Override
    public Object getItem(int position) {

        return skuList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.sku_item_layout2, null);
            holder = new ViewHolder();
            holder.sku_num = (TextView) convertView.findViewById(R.id.sku_num);
            holder.sku_shelf = (TextView) convertView.findViewById(R.id.sku_is_stock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanSkuShelf item = skuList.get(position);
        holder.sku_num.setText(item.SKUCount + "");
        holder.sku_shelf.setText(item.ShelfLocationCode);
        return convertView;
    }

    public class ViewHolder {
        public TextView sku_shelf;
        public TextView sku_num;
    }

}
