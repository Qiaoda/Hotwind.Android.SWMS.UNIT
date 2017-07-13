package cn.jitmarketing.hot.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.NumberFormat;
import java.util.List;

import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ChangeSampleBean;
import cn.jitmarketing.hot.entity.SkuEntity;

public class SkuListAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<ChangeSampleBean> skuList;
    private Activity activity;
    private NumberFormat currencyFormatA = NumberFormat.getNumberInstance();

    public SkuListAdapter(Activity activity, List<ChangeSampleBean> skuList) {
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
            convertView = inflater.inflate(R.layout.change_sku_item_layout, null);
            holder = new ViewHolder();
            holder.sku_color = (TextView) convertView.findViewById(R.id.sku_color);
            holder.sku_size = (TextView) convertView.findViewById(R.id.sku_size);
            holder.sku_num = (TextView) convertView.findViewById(R.id.sku_num);
            holder.sku_is_stock = (TextView) convertView.findViewById(R.id.sku_is_stock);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ChangeSampleBean item = skuList.get(position);
        holder.sku_color.setText(item.SKUID);
        holder.sku_size.setText(item.EndQty + "");
        holder.sku_num.setText(item.PUnitName);
        holder.sku_is_stock.setText(item.Time);
        holder.sku_color.setSelected(true);
        //在这里加个判断，若为选中项，则改变背景图片和背景色
        if (HotConstants.SELECTED == position) {
            convertView.setBackgroundResource(R.drawable.shape_green_frame_sharp);
        } else {
            convertView.setBackgroundResource(R.drawable.shape_gray_frame_sharp);
        }
        return convertView;
    }

    public class ViewHolder {
        public TextView sku_color;
        public TextView sku_size;
        public TextView sku_num;
        public TextView sku_is_stock;
    }

}
