package cn.jitmarketing.hot.util;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.helper.StringUtil;

import android.R.array;
import android.app.Service;
import android.telephony.TelephonyManager;
import android.util.Log;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.CheckBean;
import cn.jitmarketing.hot.entity.DoWeBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.InStockSku;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;

import com.ex.lib.core.utils.Ex;
import com.ex.sdk.core.utils.mgr.MgrNet;

import cn.jitmarketing.hot.entity.InStockSku;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.entity.ShelfBean;

public class SkuUtil {
	/*
	 * 盘点扫描
	 */
	public static List<SkuBean> getSku(List<SkuBean> skuList, SkuBean sb) {
		for (int i = 0; i < skuList.size(); i++) {
			if (sb.skuCode.equals(skuList.get(i).skuCode)) {
				sb.skuCount = skuList.get(i).skuCount + 1;
				skuList.add(sb);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(sb);
				return skuList;
			}
		}

		if (skuList.size() == 0) {
			skuList.add(sb);
		}
		return skuList;
	}

	/*
	 * 入库更改list
	 */
	public static List<InStockDetail> getInDetailSku(List<InStockDetail> skuList, InStockDetail sb) {
		for (int i = 0; i < skuList.size(); i++) {
			if (sb.SKUCode.equals(skuList.get(i).SKUCode)) {
				sb.SKUCount = skuList.get(i).SKUCount + 1;
				skuList.add(sb);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(sb);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(sb);
		}
		return skuList;
	}

	/*
	 * 盘点手工
	 */
	public static List<SkuBean> HandgetSku(List<SkuBean> skuList, SkuBean sb) {
		for (int i = 0; i < skuList.size(); i++) {
			if (sb.skuCode.equals(skuList.get(i).skuCode)) {
				sb.skuCount = skuList.get(i).skuCount + sb.skuCount;
				skuList.add(sb);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(sb);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(sb);
		}
		return skuList;
	}

	/*
	 * 入库扫描
	 */
	public static List<InStockSku> instockaddSku(List<InStockSku> skuList, InStockSku ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.skuCode.equalsIgnoreCase(skuList.get(i).skuCode)) {
				ins.reallyCount = skuList.get(i).reallyCount + 1;
				ins.shouldCount = skuList.get(i).shouldCount;
				skuList.add(ins);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 老入库扫描
	 */
	public static List<BeforeInStockBean> check(List<BeforeInStockBean> skuList, BeforeInStockBean ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.SKUCode.equalsIgnoreCase(skuList.get(i).SKUCode)) {
				ins.SKUCount = skuList.get(i).SKUCount + ins.SKUCount;
				skuList.add(ins);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 老入库扫描
	 */
	public static List<BeforeInStockBean> hhcheck(List<BeforeInStockBean> skuList, BeforeInStockBean ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.SKUCode.equalsIgnoreCase(skuList.get(i).SKUCode)) {
				skuList.get(i).SKUCount = skuList.get(i).SKUCount + ins.SKUCount;
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 入库扫描
	 */
	public static List<InStockDetail> newcheck(List<InStockDetail> skuList, InStockDetail ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.SKUCode.equalsIgnoreCase(skuList.get(i).SKUCode)) {
				ins.SKUCount = skuList.get(i).SKUCount + ins.SKUCount;
				skuList.add(ins);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 入库上架合公共只为看核对扫描
	 */
	public static List<InStockSku> instockcheck(List<InStockSku> skuList, InStockSku ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.skuCode.equalsIgnoreCase(skuList.get(i).skuCode)) {
				skuList.get(i).reallyCount += 1;
				ins.shouldCount = skuList.get(i).shouldCount;
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 入库上架合公共只为看核对手工
	 */
	public static List<InStockSku> handinstockand(List<InStockSku> skuList, InStockSku ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.skuCode.equalsIgnoreCase(skuList.get(i).skuCode)) {
				skuList.get(i).reallyCount += ins.reallyCount;
				skuList.get(i).shouldCount = skuList.get(i).shouldCount;
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	/*
	 * 入库手工
	 */
	public static List<InStockSku> handinstockaddSku(List<InStockSku> skuList, InStockSku ins) {
		for (int i = 0; i < skuList.size(); i++) {
			if (ins.skuCode.equalsIgnoreCase(skuList.get(i).skuCode)) {
				ins.reallyCount = skuList.get(i).reallyCount + ins.reallyCount;
				ins.shouldCount = skuList.get(i).shouldCount;
				skuList.add(ins);
				skuList.remove(skuList.get(i));
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(ins);
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(ins);
		}
		return skuList;
	}

	public static List<SkuBean> addSku(List<SkuBean> skuList, String skuCode) {
		for (int i = 0; i < skuList.size(); i++) {
			SkuBean sku = skuList.get(i);
			if (sku.skuCode.equals(skuCode)) {
				int count = (int) sku.skuCount;
				sku.skuCount = count + 1;
				return skuList;
			}
			if (i == skuList.size() - 1) {
				skuList.add(new SkuBean(skuCode, 1));
				return skuList;
			}
		}
		if (skuList.size() == 0) {
			skuList.add(new SkuBean(skuCode, 1));
		}
		return skuList;
	}

	public static int getSkuCount(List<SkuBean> skuList) {
		int count = 0;
		for (int i = 0; i < skuList.size(); i++) {
			count += skuList.get(i).skuCount;
		}
		return count;
	}

	public static int getnewSkuCount(List<InStockDetail> skuList) {
		int count = 0;
		for (int i = 0; i < skuList.size(); i++) {
			count += skuList.get(i).SKUCount;
		}
		return count;
	}

	public static int getScanCount(List<InStockSku> skuList) {
		int count = 0;
		for (int i = 0; i < skuList.size(); i++) {
			count += skuList.get(i).reallyCount;
		}
		return count;
	}

	public static int getshowCount(List<InStockSku> skuList) {
		int count = 0;
		for (int i = 0; i < skuList.size(); i++) {
			count += skuList.get(i).shouldCount;
		}
		return count;
	}

	public static int getCount(List<BeforeInStockBean> skuList) {
		int count = 0;
		for (int i = 0; i < skuList.size(); i++) {
			count += skuList.get(i).SKUCount;
		}
		return count;
	}

	/**
	 * 判断是库位码
	 * 
	 * @param barcode
	 * @return
	 */
	public static boolean isWarehouse(String barcode) {
		java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
		java.util.regex.Matcher m = pattern.matcher(barcode.substring(0, 1));
		if (m.matches() && barcode.length() == 7 && !barcode.startsWith("E") && !barcode.startsWith("N") && !barcode.startsWith("T"))
			return true;
		else
			return false;
		// return barcode.length() == 5 ? true : false ;
	}

	public static List<ShelfBean> addShelf(List<ShelfBean> shelfList, String shelfCode, String skuCode) {
		ArrayList<SkuBean> skuList = new ArrayList<SkuBean>();
		for (int i = 0; i < shelfList.size(); i++) {
			ShelfBean shelf = shelfList.get(i);
			if (shelf.shelfCode.equals(shelfCode)) {
				addSku(shelf.skuList, skuCode);
				return shelfList;
			}
			if (i == shelfList.size() - 1) {
				skuList.add(new SkuBean(skuCode, 1));
				shelfList.add(new ShelfBean(shelfCode, skuList));
				return shelfList;
			}
		}
		if (shelfList.size() == 0) {
			skuList.add(new SkuBean(skuCode, 1));
			shelfList.add(new ShelfBean(shelfCode, skuList));
		}
		return shelfList;
	}

	/** 上架扫描 */
	public static List<ShelfBean> addcheckShelf(List<ShelfBean> shelfList, String shelfCode, List<SkuBean> skuScanList) {
		ShelfBean scv = null;
		for (int i = 0; i < shelfList.size(); i++) {
			ShelfBean shelf = shelfList.get(i);
			if (shelf.shelfCode.equals(shelfCode)) {
				scv = shelf;
				break;
			}
		}
		if (scv == null) {
			scv = new ShelfBean(shelfCode, new ArrayList<SkuBean>());
			shelfList.add(scv);
		}

		for (SkuBean titem : skuScanList) {
			SkuBean tbean = null;
			for (SkuBean item : scv.skuList) {
				if (item.skuCode.equals(titem.skuCode)) {
					tbean = item;
					break;
				}
			}
			if (tbean == null) {
				tbean = new SkuBean(titem.skuCode, titem.skuCount);
				tbean.skuShelfLocation = titem.skuShelfLocation;
				scv.skuList.add(tbean);
			} else {
				tbean.skuCount += titem.skuCount;
				tbean.skuShelfLocation = titem.skuShelfLocation;
			}
		}
		return shelfList;
	}

	/** 最新的上架扫描 */
	public static void addnewcheckShelf(List<DoWeBean> doList, ArrayList<InStockDetail> sb) {

		// DoWeBean db=null;
		// for(int i=0;i<doList.size();i++){
		// DoWeBean dobean=doList.get(i);
		// if(dobean.ShelfLocation.equals(locatin)){
		// db=dobean;
		// break;
		// }
		// }
		// if(db==null){
		// db=new DoWeBean(locatin,new ArrayList<InStockDetail>());
		// doList.add(db);
		// }
		// for(InStockDetail item:sb){
		// InStockDetail t=null;
		// for(InStockDetail titem: db.lastList){
		// if(titem.SKUID.equals(item.SKUID)){
		// t=titem;
		// break;
		// }
		// }
		// if(t==null){
		// t=new InStockDetail(item.SKUID,item.SKUQty);
		// db.lastList.add(t);
		// }else{
		// t.SKUQty+=item.SKUQty;
		// }
		// }
	}

	public static List<ShelfBean> handaddShelf(List<ShelfBean> shelfList, String shelfCode, SkuBean sb) {
		for (int i = 0; i < shelfList.size(); i++) {
			ShelfBean shelf = shelfList.get(i);
			if (shelf.shelfCode.equals(shelfCode)) {
				for (int u = 0; u < shelf.skuList.size(); u++) {
					if (sb.skuCode.equals(shelf.skuList.get(u).skuCode)) {
						shelf.skuList.get(u).skuCount += sb.skuCount;
					}
				}
				return shelfList;
			}
			if (i == shelfList.size() - 1) {
				List<SkuBean> list = new ArrayList<SkuBean>();
				list.add(sb);
				ShelfBean s = new ShelfBean(shelfCode, list);
				shelfList.add(s);
				return shelfList;
			}
		}
		if (shelfList.size() == 0) {
			List<SkuBean> list = new ArrayList<SkuBean>();
			list.add(sb);
			ShelfBean s = new ShelfBean(shelfCode, list);
			shelfList.add(s);
		}
		return shelfList;
	}

	/** 发送POST请求 */
	public static String sendHttpPost(String url, Map<String, String> params) {

		HttpClient httpClient = new MgrNet().newInstance();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response;
		String result = null;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>(params.size());

				for (Map.Entry<String, String> entry : params.entrySet()) {
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}

				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
				httpPost.setEntity(entity);
			}

			response = httpClient.execute(httpPost);
			InputStream inputStream = response.getEntity().getContent();
			result = Ex.T().getInStream2Str(inputStream);

			Log.e("result-->", result);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

	/*
	 * 盘点获取最新3个list
	 */
	public static List<SkuBean> skuShowList(List<SkuBean> list1, List<SkuBean> list2) {
		list2.clear();
		if (list1.size() < 3) {
			for (int i = list1.size() - 1; i >= 0; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		} else {
			for (int i = list1.size() - 1; i >= list1.size() - 3; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		}
	}

	/*
	 * 新版入库获取最新3个list
	 */
	public static List<InStockDetail> skunewShowList(List<InStockDetail> list1, List<InStockDetail> list2) {
		list2.clear();
		if (list1.size() < 3) {
			for (int i = list1.size() - 1; i >= 0; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		} else {
			for (int i = list1.size() - 1; i >= list1.size() - 3; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		}
	}

	/*
	 * 新版入库获取最新3个list
	 */
	public static List<BeforeInStockBean> newShowList(List<BeforeInStockBean> list1, List<BeforeInStockBean> list2) {
		list2.clear();
		if (list1.size() < 3) {
			for (int i = list1.size() - 1; i >= 0; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		} else {
			for (int i = list1.size() - 1; i >= list1.size() - 3; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		}
	}

	/*
	 * 入库获取最新3个list
	 */
	public static List<InStockSku> instockShowList(List<InStockSku> list1, List<InStockSku> list2) {
		list2.clear();
		if (list1.size() < 3) {
			for (int i = list1.size() - 1; i >= 0; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		} else {
			for (int i = list1.size() - 1; i >= list1.size() - 3; i--) {
				list2.add(list1.get(i));
			}
			return list2;
		}
	}

	// public static List<InStockSku> addInStockSku(List<InStockSku> skuList,
	// String skuCode){
	// for(int i = 0 ; i < skuList.size() ; i ++){
	// InStockSku sku = skuList.get(i);
	// if(sku.skuCode.equals(skuCode)){
	// int count = (int) sku.reallyCount;
	// sku.reallyCount = count + 1 ;
	// return skuList;
	// }
	// if(i == skuList.size() - 1){
	// skuList.add(new InStockSku(skuCode, 0, 1));
	// return skuList;
	// }
	// }
	// if(skuList.size() == 0){
	// skuList.add(new InStockSku(skuCode, 0, 1));
	// }
	// return skuList;
	// }
	public static List<CheckBean> cbCheck(List<InStockDetail> skuList, List<InStockDetail> sendList) {
		List<CheckBean> cb = new ArrayList<CheckBean>();
		for (int i = 0; i < sendList.size(); i++) {
			CheckBean as = new CheckBean(sendList.get(i).SKUCode, sendList.get(i).SKUCount, 0);
			cb.add(as);
		}
		for (int j = 0; j < skuList.size(); j++) {
			InStockDetail ik = skuList.get(j);
			for (int u = 0; u < cb.size(); u++) {
				if (ik.SKUCode.equals(cb.get(u).sku)) {
					cb.get(u).reallyCount += ik.SKUCount;
					break;
				}
				if (u == cb.size() - 1) {
					CheckBean ass = new CheckBean(ik.SKUCode, 0, ik.SKUCount);
					cb.add(ass);
					break;
				}
			}
		}
		return cb;
	}

	public static List<CheckBean> newcbCheck(List<BeforeInStockBean> skuList, List<BeforeInStockBean> sendList) {
		List<CheckBean> cb = new ArrayList<CheckBean>();
		for (int i = 0; i < sendList.size(); i++) {
			CheckBean as = new CheckBean(sendList.get(i).SKUCode, sendList.get(i).SKUCount, 0);
			cb.add(as);
		}
		for (int j = 0; j < skuList.size(); j++) {
			BeforeInStockBean ik = skuList.get(j);
			for (int u = 0; u < cb.size(); u++) {
				if (ik.SKUCode.equals(cb.get(u).sku)) {
					cb.get(u).reallyCount += ik.SKUCount;
					break;
				}
				if (u == cb.size() - 1) {
					CheckBean ass = new CheckBean(ik.SKUCode, 0, ik.SKUCount);
					cb.add(ass);
					break;
				}
			}
		}
		return cb;
	}

	public static List<BeforeInStockBean> beforeCheck(List<BeforeInStockBean> skuList) {
		List<BeforeInStockBean> newBeanList = new ArrayList<BeforeInStockBean>();
		Map<String, BeforeInStockBean> map = new HashMap<String, BeforeInStockBean>();
		for (BeforeInStockBean bean : skuList) {
			if (map.get(bean.SKUCode) == null) {
				BeforeInStockBean bn = new BeforeInStockBean(bean.SKUCode, bean.SKUCount);
				map.put(bean.SKUCode, bn);
			} else {
				map.get(bean.SKUCode).SKUCount += bean.SKUCount;
			}
		}
		for (Map.Entry<String, BeforeInStockBean> entry : map.entrySet()) {
			newBeanList.add(entry.getValue());
		}
		return newBeanList;
	}

	public static List<InStockDetail> newbeforeCheck(List<InStockDetail> skuList) {
		List<InStockDetail> newBeanList = new ArrayList<InStockDetail>();
		Map<String, InStockDetail> map = new HashMap<String, InStockDetail>();
		for (InStockDetail bean : skuList) {
			if (map.get(bean.SKUCode) == null) {
				InStockDetail bn = new InStockDetail(bean.SKUCode, bean.SKUCount);
				map.put(bean.SKUCode, bn);
			} else {
				map.get(bean.SKUCode).SKUCount += bean.SKUCount;
			}
		}
		for (Map.Entry<String, InStockDetail> entry : map.entrySet()) {
			newBeanList.add(entry.getValue());
		}
		return newBeanList;
	}

	/**
	 * 价格格式化
	 * 
	 * @param price
	 * @return
	 */
	public static String formatPrice(String price) {

		String newPrice = "";
		if (price != null && price.split("\\.").length >= 2) {
			newPrice = price.split("\\.")[0];
			if (price.split("\\.")[1].length() > 2) {
				newPrice += "." + price.split("\\.")[1].substring(0, 2);
			} else {
				newPrice += "." + price.split("\\.")[1];
			}
		}

		return newPrice;
	}

	/**
	 * 获取时间格式化
	 * @return
	 */
	public String getTimeFormat() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(new Date());
	}
}
