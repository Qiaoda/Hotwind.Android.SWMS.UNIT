package cn.jitmarketing.hot.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.jitmarketing.hot.entity.DoWeBean;
import cn.jitmarketing.hot.entity.InStockDetail;

public class ChangeValueUtil {
	
	public static ArrayList<DoWeBean> value(ArrayList<InStockDetail> inStockDetailList) {
		ArrayList<DoWeBean> doweList = new ArrayList<DoWeBean>();
		Map<String, ArrayList<InStockDetail>> map = new HashMap<String, ArrayList<InStockDetail>>();
		for(InStockDetail detail : inStockDetailList) {
			if(map.get(detail.ShelfLocationCode) == null) {
				ArrayList<InStockDetail> addList = new ArrayList<InStockDetail>();
				addList.add(detail);
				map.put(detail.ShelfLocationCode, addList);
			} else {
				ArrayList<InStockDetail> list = map.get(detail.ShelfLocationCode);
				boolean isExist = false;
				for(int j=0; j<list.size(); j++) {
					if(detail.SKUCode.equals(list.get(j).SKUCode)) {
						list.get(j).SKUCount += detail.SKUCount;
						isExist = true;
						break;
					}
				}
				if(!isExist) {
					map.get(detail.ShelfLocationCode).add(detail);
				}
			}
		}
		for(Map.Entry<String, ArrayList<InStockDetail>> entry : map.entrySet()){ 
			DoWeBean bean = new DoWeBean();
			bean.ShelfLocation = entry.getKey();
			bean.lastList = entry.getValue();
			doweList.add(bean);
		} 
		return doweList;
	}
	
	public static ArrayList<InStockDetail> reverseValue(ArrayList<DoWeBean> doList) {
		ArrayList<InStockDetail> detailList = new ArrayList<InStockDetail>();
		for(DoWeBean bean : doList) {
			for(InStockDetail detail : bean.lastList) {
				detailList.add(detail);
			}
		}
		return detailList;
	}

}
