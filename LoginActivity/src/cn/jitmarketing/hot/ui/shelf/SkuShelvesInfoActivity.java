package cn.jitmarketing.hot.ui.shelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseSwipeOperationActivity;

import org.jsoup.helper.StringUtil;

import java.util.ArrayList;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.adapter.OldSkuShelvesAdapter;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.util.SaveListUtil;
import cn.jitmarketing.hot.util.SkuUtil;
import cn.jitmarketing.hot.view.SkuEditText;
import cn.jitmarketing.hot.view.TitleWidget;

/**
 * 上架界面
 */
public class SkuShelvesInfoActivity extends BaseSwipeOperationActivity implements
        OnClickListener {
    @ViewInject(R.id.shelf_title)
    TitleWidget shelf_title;
    @ViewInject(R.id.only_list)
    ListView only_list;
    @ViewInject(R.id.text_one)
    TextView text_one;
    @ViewInject(R.id.text_two)
    TextView text_two;
    private SkuEditText sku_shelf;
    private TextView sku_really_all_count;
    private TextView sku_scan_detail;
    private OldSkuShelvesAdapter listAdapter;
    private ArrayList<ShelfBean> wareList = new ArrayList<ShelfBean>();
    private ArrayList<SkuBean> skuList = new ArrayList<SkuBean>();
    private ArrayList<SkuBean> newList = new ArrayList<SkuBean>();
    private String shelfStr;
    private boolean lastestIsSku;
    private boolean canReceive;
    private static final int WHAT_NET_SKU_SHELF = 0x10;
    private static final int FOR_RESULT_ADD = 0x11;
    private static final int FOR_RESULT_HAND = 0x12;
    private ArrayList<String> stringlist;
    private String s;
    private HotApplication ap;

    @Override
    protected void exInitAfter() {}

    @Override
    protected void exInitBundle() {
        initIble(this, this);
    }

    @Override
    protected int exInitLayout() {
        return R.layout.activity_only_list2;
    }

    @Override
    protected void exInitView() {
        ap = (HotApplication) getApplication();
        shelf_title.setOnLeftClickListner(this);
        shelf_title.setText("上架");
        text_one.setOnClickListener(this);
        text_two.setOnClickListener(this);
        stringlist = new ArrayList<String>();
        listAdapter = new OldSkuShelvesAdapter(getLayoutInflater(), newList);
        View footview = getLayoutInflater().inflate(R.layout.sku_shelves_foot,null);
        sku_shelf = (SkuEditText) footview.findViewById(R.id.sku_shelf);
        sku_shelf.stopEdit();
        sku_really_all_count = (TextView) footview
                .findViewById(R.id.sku_really_all_count);
        sku_scan_detail = (TextView) footview
                .findViewById(R.id.sku_scan_detail);
        sku_scan_detail.setOnClickListener(this);
        View headView = getLayoutInflater().inflate(R.layout.sku_shelves_item_layout, null);
        only_list.addHeaderView(headView);
        only_list.addFooterView(footview);
        only_list.setAdapter(listAdapter);
        sku_really_all_count.setText("" + 0);
    }

    @Override
    protected void onResume() {
        canReceive = true;
        // 开启扫描服务
        //		startService(new Intent(this,ScanService.class));
        //		// 绑定服务
        //		bindService(new Intent(this,ScanService.class), serviceConnection,Context
        // .BIND_AUTO_CREATE);
        super.onResume();
    }

    @Override
    protected void onPause() {
        canReceive = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        canReceive = false;
        super.onStop();
    }

    @Override
    public void onReceiver(Intent intent) {
        //		byte[] code = intent.getByteArrayExtra("barocode");
        //		int codelen = intent.getIntExtra("length", 0);
        //		String barcode = new String(code, 0, codelen).toUpperCase().trim();
        //		dealBarCode(barcode);

        //		if (canReceive) {
        //			byte[] code = intent.getByteArrayExtra("barocode");
        //			int codelen = intent.getIntExtra("length", 0);
        //			if (null != code) {
        //				s = sku_shelf.getText(this);
        //				String barcode = new String(code, 0, codelen).toUpperCase().trim();
        //				if ((!SkuUtil.isWarehouse(barcode)) && s.length() == 0) {
        //					ap.getsoundPool(ap.Sound_sku);
        //					stringlist.add(barcode);
        //					SkuBean sb = new SkuBean(barcode, 1);
        //					SkuUtil.getSku(skuList, sb);
        //					SkuUtil.skuShowList(skuList, newList);
        //					listAdapter.notifyDataSetChanged();
        //					sku_really_all_count.setText(""
        //							+ SkuUtil.getSkuCount(skuList));
        //				} else if ((!SkuUtil.isWarehouse(barcode)) && s.length() > 0) {
        //					ap.getsoundPool(ap.Sound_sku);
        //					skuList.clear();
        //					sku_shelf.setText("");
        //					stringlist.add(barcode);
        //					SkuBean sb = new SkuBean(barcode, 1);
        //					SkuUtil.getSku(skuList, sb);
        //					SkuUtil.skuShowList(skuList, newList);
        //					listAdapter.notifyDataSetChanged();
        //					sku_really_all_count.setText(""
        //							+ SkuUtil.getSkuCount(skuList));
        //				} else if (SkuUtil.isWarehouse(barcode)
        //						&& stringlist.size() > 0) {
        //					ap.getsoundPool(ap.Sound_location);
        //					sku_shelf.setText(barcode);
        //					// 遍历寻找库位是否存在
        //					SkuUtil.addcheckShelf(wareList, barcode, skuList);
        //					stringlist.clear();
        //				}
        //			}
        //		}
    }

    //	@Override
    //	public void fillCode(String code) {
    //		dealBarCode(code);
    //	}

    private void dealBarCode(String barcode) {
        if (canReceive) {
            s = sku_shelf.getText(this);
            if ((!SkuUtil.isWarehouse(barcode)) && s.length() == 0) {
                ap.getsoundPool(ap.Sound_sku);
                stringlist.add(barcode);
                SkuBean sb = new SkuBean(barcode, 1);
                SkuUtil.getSku(skuList, sb);
                SkuUtil.skuShowList(skuList, newList);
                listAdapter.notifyDataSetChanged();
                sku_really_all_count.setText(""
                        + SkuUtil.getSkuCount(skuList));
            } else if ((!SkuUtil.isWarehouse(barcode)) && s.length() > 0) {
                ap.getsoundPool(ap.Sound_sku);
                skuList.clear();
                sku_shelf.setText("");
                stringlist.add(barcode);
                SkuBean sb = new SkuBean(barcode, 1);
                SkuUtil.getSku(skuList, sb);
                SkuUtil.skuShowList(skuList, newList);
                listAdapter.notifyDataSetChanged();
                sku_really_all_count.setText(""
                        + SkuUtil.getSkuCount(skuList));
            } else if (SkuUtil.isWarehouse(barcode)
                    && stringlist.size() > 0) {
                ap.getsoundPool(ap.Sound_location);
                sku_shelf.setText(barcode);
                // 遍历寻找库位是否存在
                SkuUtil.addcheckShelf(wareList, barcode, skuList);
                stringlist.clear();
            }
        }
    }

    //	@Override
    //	public Map<String, String> onStart(int what) {
    //		switch (what) {
    //		case WHAT_NET_SKU_SHELF:
    //			return WarehouseNet.oldskuShelf(wareList);
    //		}
    //		return null;
    //	}

    @Override
    public void onError(int what, int e, String message) {
        switch (what) {
            case WHAT_NET_SKU_SHELF:
                Ex.Toast(this).showLong(R.string.urlError);
                break;
        }
    }

    @Override
    public void onSuccess(int what, String result, boolean hashCache) {
        ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
        if (!net.isSuccess) {
            Ex.Toast(mContext).showLong(net.message);
            return;
        }
        switch (what) {
            case WHAT_NET_SKU_SHELF:
                Ex.Toast(mActivity).showLong(net.message);
                this.finish();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.lv_left:
                this.finish();
                break;
            case R.id.text_one:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.noteTitle)
                        .setMessage("确认提交？")
                        .setNegativeButton(R.string.cancelTitle,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                })
                        .setPositiveButton(R.string.sureTitle,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int whichButton) {
                                        if (!StringUtil.isBlank(sku_shelf.getText(mContext))
                                                && wareList.size() != 0) {
                                            // 提交上架数据
                                            startJsonTask(HotConstants.Global.APP_URL_USER
                                                            + HotConstants.Shelf.SKUShelves, WHAT_NET_SKU_SHELF, true,
                                                    NET_METHOD_POST, SaveListUtil.skuShelfSave(sku_shelf.getText(mContext), wareList), false);
                                        } else if (skuList.size() == 0) {
                                            Ex.Toast(mActivity).show("无商品码，请先扫描");
                                        } else if (StringUtil.isBlank(sku_shelf.getText(mContext))) {
                                            Ex.Toast(mActivity).show("无库位码，请先扫描");
                                        }
                                    }
                                }).show();
                break;
            case R.id.sku_scan_detail:
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", wareList);
                Intent intent1 = new Intent();
                intent1.putExtra("bundle", bundle);
                intent1.setClass(SkuShelvesInfoActivity.this,
                        ShelfDetailActivity.class);
                startActivity(intent1);
                break;
            case R.id.text_two:
                // 手工添加数据
                Intent intent = new Intent();
                intent.setClass(SkuShelvesInfoActivity.this,
                        HandShelfActivity.class);
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
            SkuBean sb = new SkuBean(sku, Integer.valueOf(count));
            SkuUtil.HandgetSku(skuList, sb);
            SkuBean ssb = new SkuBean(sku, Integer.valueOf(count));
            SkuUtil.skuShowList(skuList, newList);
            listAdapter.notifyDataSetChanged();
            sku_shelf.setText(location);
            SkuUtil.handaddShelf(wareList, location, ssb);
            sku_really_all_count.setText("" + SkuUtil.getSkuCount(skuList));
        }
    }

    @Override
    public void fillCode(String code) {
        dealBarCode(code);
    }
}
