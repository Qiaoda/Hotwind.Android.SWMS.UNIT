package cn.jitmarketing.hot.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;
import cn.jitmarketing.hot.entity.StockTakingDetailEntity;

public class SaveListUtil {
	
	/**
	 * 修改盘点详情
	 * @param receiveSheetNo
	 * @param list
	 * @return
	 */
	public static String updatePandian(List<StockTakingDetailEntity> list, String ShelfLocationCode) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ShelfLocationCode", ShelfLocationCode); 
			JSONArray array = new JSONArray();
			for(StockTakingDetailEntity sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("Status", sb.Status);
				mJson.put("KCSKUCount", sb.KCSKUCount);
				mJson.put("PDSKUCount", sb.PDSKUCount);
				mJson.put("SKUCode", sb.SKUID);
				array.put(mJson);
			}
			postData.put("Data", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 批量拿货
	 * @param receiveSheetNo
	 * @param list
	 * @return
	 */
	public static String takeGoodsBatch(List<InStockDetail> list, String FactID) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("FactID", FactID); 
			JSONArray array = new JSONArray();
			for(InStockDetail sb : list) {
				JSONObject mJson = new JSONObject();  
//				mJson.put("SKUCode", sb.SKUCode);
				mJson.put("SKUCount", sb.SKUCount);
				mJson.put("ShelfLocationCode", sb.ShelfLocationCode);
				array.put(mJson);
			}
			postData.put("List", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 保存入库
	 * @param receiveSheetNo
	 * @param list
	 * @return
	 */
	public static String instockSave(String receiveSheetNo, List<BeforeInStockBean> list) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId());
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ReceiveSheetNo", receiveSheetNo); 
			JSONArray array = new JSONArray();
			for(BeforeInStockBean sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("SKUCode", sb.SKUCode.toUpperCase().trim());
				mJson.put("SKUCount", sb.SKUCount);
				array.put(mJson);
			}
			postData.put("SKUList", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 保存上架
	 * @param receiveSheetNo
	 * @param list
	 * @return
	 */
	public static String shelfSave(String receiveSheetNo, List<InStockDetail> list) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ReceiveSheetNo", receiveSheetNo); 
			JSONArray array = new JSONArray();
			for(InStockDetail sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("SKUCode", sb.SKUCode.toUpperCase().trim());
				mJson.put("SKUCount", sb.SKUCount);
				mJson.put("ShelfLocationCode", sb.ShelfLocationCode);
				array.put(mJson);
			}
			postData.put("SKUList", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 店长盘点
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String ownerStockSave(String ShelfLocationCode) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ShelfLocationCode", ShelfLocationCode); 
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 销售人员盘点
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String CGYStockSave(String ShelfLocationCode, ArrayList<SkuBean> list) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ShelfLocationCode", ShelfLocationCode); 
			JSONArray array = new JSONArray();
			for(SkuBean sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("SKUCode", sb.skuCode.toUpperCase().trim());
				mJson.put("SKUCount", sb.skuCount);
				array.put(mJson);
			}
			postData.put("Data", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 仓管员
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String skuShelfSave(String ShelfLocationCode, ArrayList<ShelfBean> list) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId());
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ShelfLocationCode", ShelfLocationCode); 
			JSONArray array = new JSONArray();
			for(ShelfBean sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("shelfId", sb.shelfId);
				mJson.put("ShelfLocationCode", sb.shelfCode);
				mJson.put("shelfName", sb.shelfName);
				JSONArray array1 = new JSONArray();
				for(SkuBean sb1 : sb.skuList) {
					JSONObject mJson1 = new JSONObject();  
					mJson1.put("SKUID", sb1.skuId);
					mJson1.put("SKUCode", sb1.skuCode.toUpperCase().trim());
					mJson1.put("SKUName", sb1.skuName);
					mJson1.put("SKUColor", sb1.skuColor);
					mJson1.put("SKUSize", sb1.skuSize);
					mJson1.put("SKUCount", sb1.skuCount);
					mJson1.put("SKUShelfLocation", sb1.skuShelfLocation);
					mJson1.put("SKUPrice", sb1.skuPrice);
					mJson1.put("SKUIsSampling", sb1.skuIsSampling);
					mJson1.put("SKUSamplingCount", sb1.skuSamplingCount);
					mJson1.put("SampleDate", sb1.sampleDate);
					mJson1.put("IsSomeOut", sb1.IsSomeOut);
					mJson1.put("DifferenceCount", sb1.DifferenceCount);
					mJson1.put("TheoryCount", sb1.TheoryCount);
					array1.put(mJson1);
				}
				mJson.put("SKUList", array1);
				array.put(mJson);
			}
			postData.put("Data", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 仓管员
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String skuShelfSave(ArrayList<InStockDetail> list, String targetUnitID, String AllocationOrderID, String BackDefectiveType, String Remark) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("TargetUnitID", targetUnitID); 
			postData.put("AllocationOrderID", AllocationOrderID); 
			postData.put("BackDefectiveType", BackDefectiveType); 
			postData.put("Remark", Remark); 
			JSONArray array = new JSONArray();
			for(InStockDetail sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("ShelfLocationCode", sb.ShelfLocationCode);
				mJson.put("SKUCode", sb.SKUCode.toUpperCase().trim());
				mJson.put("SKUCount", sb.SKUCount);
				array.put(mJson);
			}
			postData.put("List", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 仓管员
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String skuTempShelfSave(ArrayList<InStockDetail> list, String AllocationOrderID) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("AllocationOrderID", AllocationOrderID);
			JSONArray array = new JSONArray();
			for(InStockDetail sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("ShelfLocationCode", sb.ShelfLocationCode);
				mJson.put("SKUCode", sb.SKUCode.toUpperCase().trim());
				mJson.put("SKUCount", sb.SKUCount);
				array.put(mJson);
			}
			postData.put("List", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
	/**
	 * 移库
	 * @param ChecktaskShelflocationID
	 * @param ShelfLocationCode
	 * @param list
	 * @return
	 */
	public static String moveShelfSave(String oldShelf, String newShelf, List<SkuBean> list) {
		try {
			JSONObject postData = new JSONObject();  
			postData.put("UnitID", HotApplication.getInstance().getUnitId()); 
			postData.put("UserID", HotApplication.getInstance().getUserId()); 
			postData.put("ShelfLocationCode", oldShelf); 
			postData.put("NewShelfLocationCode", newShelf); 
			JSONArray array = new JSONArray();
			for(SkuBean sb : list) {
				JSONObject mJson = new JSONObject();  
				mJson.put("SKUID", sb.skuId);
				mJson.put("SKUCode", sb.skuCode.toUpperCase().trim());
				mJson.put("SKUName", sb.skuName);
				mJson.put("SKUColor", sb.skuColor);
				mJson.put("SKUSize", sb.skuSize);
				mJson.put("SKUCount", sb.skuCount);
				mJson.put("SKUShelfLocation", sb.skuShelfLocation);
				mJson.put("SKUPrice", sb.skuPrice);
				mJson.put("SKUIsSampling", sb.skuIsSampling);
				mJson.put("SKUSamplingCount", sb.skuSamplingCount);
				mJson.put("SampleDate", sb.sampleDate);
				mJson.put("IsSomeOut", sb.IsSomeOut);
				mJson.put("DifferenceCount", sb.DifferenceCount);
				mJson.put("TheoryCount", sb.TheoryCount);
				array.put(mJson);
			}
			postData.put("SKUList", array);
			return postData.toString();
        } catch (JSONException e) {
        	Log.e("JSONException", e.getMessage());
        	return null;
        }
	}
	
}
