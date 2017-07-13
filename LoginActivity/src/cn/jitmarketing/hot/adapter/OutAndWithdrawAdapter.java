package cn.jitmarketing.hot.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.OutAndWithDrawBean;
import cn.jitmarketing.hot.ui.sample.OutAndWithdrawActivity;

/**
 * 鞋子出另一只&撤样列表适配器
 * Created by fgy on 2016/4/7.
 */
public class OutAndWithdrawAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<OutAndWithDrawBean> mList;
    private int adapterType = OutAndWithdrawActivity.SHOE_OUT;

    public void setAdapterType(int adapterType) {
        this.adapterType = adapterType;
    }

    public OutAndWithdrawAdapter(Context context, List<OutAndWithDrawBean> list) {
        mList = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public OutAndWithDrawBean getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_out_and_withdraw, parent, false);
            holder = new ViewHolder();
            holder.lineLayout = (LinearLayout) convertView.findViewById(R.id.line_layout);
            holder.withdrawBtns = (LinearLayout) convertView.findViewById(R.id.line_withdraw_btns);
            holder.btnOut = (TextView) convertView.findViewById(R.id.btn_out);
            holder.btnChey = (TextView) convertView.findViewById(R.id.btn_chey);
            holder.btnChelyz = (TextView) convertView.findViewById(R.id.btn_chelyz);
            holder.btnChezj = (TextView) convertView.findViewById(R.id.btn_chezj);

            holder.tv_one = (TextView) convertView.findViewById(R.id.tv_one);
            holder.tv_skuLocation = (TextView) convertView.findViewById(R.id.tv_skuLocation);
            holder.tv_skuManager = (TextView) convertView.findViewById(R.id.tv_skuManager);
            holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        
       
        if (this.adapterType == OutAndWithdrawActivity.SHOE_WITHDRAW) {
            holder.btnOut.setVisibility(View.GONE);
            holder.withdrawBtns.setVisibility(View.VISIBLE);
        } else {
            holder.btnOut.setVisibility(View.VISIBLE);
            holder.withdrawBtns.setVisibility(View.GONE);
        }

        final OutAndWithDrawBean item = getItem(position);
      //如果库位是试穿库 禁止任何操作
        if(item.getShelfLocationCode().equals("TE00001")){
        	holder.btnChey.setEnabled(false);
        	holder.btnChelyz.setEnabled(false);
        	holder.btnChezj.setEnabled(false);
        }

        if (adapterType == OutAndWithdrawActivity.SHOE_WITHDRAW && item.getCount() == 1) {
            holder.tv_one.setText("1双");
            holder.btnChelyz.setEnabled(true);
        } else {
            holder.tv_one.setText("1只");
            holder.btnChelyz.setEnabled(false);
        }
        holder.tv_skuLocation.setText("鞋盒库位:" + item.getShelfLocationCode());
        holder.tv_skuManager.setText("仓管员:" + item.getUserCode());
        holder.tv_time.setText(item.getLastUpdateTime().replace("T", " "));

        holder.btnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onOutClick(item);
                }
            }
        });
        holder.btnChey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCheyClick(item);
                }
            }
        });
        holder.btnChelyz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCheLyzClick(item);
                }
            }
        });
        holder.btnChezj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCHeZjClick(item);
                }
            }
        });
        return convertView;
    }

    private static final class ViewHolder {
        LinearLayout lineLayout;
        LinearLayout withdrawBtns;

        TextView tv_one, tv_skuLocation, tv_skuManager, tv_time;

        TextView btnOut;
        TextView btnChey;
        TextView btnChelyz;
        TextView btnChezj;
    }

    private OnItemBtnClickListener listener;

    public void setListener(OnItemBtnClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemBtnClickListener {
        /**
         * 点击出另一个
         */
        void onOutClick(OutAndWithDrawBean item);

        /**
         * 点击撤样
         */
        void onCheyClick(OutAndWithDrawBean item);

        /**
         * 点击撤另一个
         */
        void onCheLyzClick(OutAndWithDrawBean item);

        /**
         * 点击直接撤
         */
        void onCHeZjClick(OutAndWithDrawBean item);
    }
}
