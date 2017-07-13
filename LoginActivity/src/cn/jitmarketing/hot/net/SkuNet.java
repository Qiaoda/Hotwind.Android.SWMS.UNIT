package cn.jitmarketing.hot.net;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ScanSkuShelf;

public class SkuNet {

    private static Gson gson = new Gson();

    /**
     * 商品查询
     */
    public static HashMap<String, String> getStoreForSKU(String skuCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode);

        return map;
    }

    /**
     * 商品模糊查询
     */
    public static HashMap<String, String> getStoreForSKUFuzzy(String skuCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode.toUpperCase().trim());

        return map;
    }

    /**
     * 取新
     *///{SKUCode:'6956511905164',UserID:'1'}
    public static HashMap<String, String> getNew(String skuCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        return map;
    }

    /**
     * 取另外一只
     *///{SKUCode:'6956511905164',UserID:'1'}
    public static HashMap<String, String> getOther(String SKUCode, String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        map.put("SKUCount", SKUCount);
        return map;
    }
    /**
     * 直接取新
     */
    public static HashMap<String, String> directGetNew(String skuCode,String storage) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("ShelfLocationCode", storage.toUpperCase().trim());
        map.put("IsDirect", "true");
        return map;
    }
    /**
     * 直接取鞋盒
     */
    public static HashMap<String, String> directGetBox(String skuCode,String storage,String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("SKUCount", SKUCount);
        map.put("ShelfLocationCode", storage.toUpperCase().trim());
        map.put("IsDirect", "true");
        return map;
    }
    /**
     * 出样
     *///{SKUCode:'6956511905164',UserID:'1',SamplingType:'1'}
    public static HashMap<String, String> getSample(String skuCode, String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("SKUCount", SKUCount);
        return map;
    }

    /**
     * 换样
     */
    public static HashMap<String, String> getChangeSample(String skuCode, String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("SKUCount", SKUCount);
        return map;
    }

    /**
     * 商品复位
     */
    public static HashMap<String, String> getResetSkuList() {
        //{Unitid:'1',SKUCode:'V001001'}
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 商品复位成功
     */
    public static HashMap<String, String> getResetSkuOk(String skuCode, String shelfCode, String
            factId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("ShelfLocationCode", shelfCode);
        map.put("FactID", factId);
        return map;
    }

    /**
     * 修改密码
     */
    public static HashMap<String, String> getAmendPass(String newPassword, String oldPassword) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserAccount", HotApplication.getInstance().getUserName());
        map.put("UserPassword", oldPassword);
        map.put("NewPassword", newPassword);
        map.put("CustomerID", HotApplication.getInstance().getCustomId());
        return map;
    }

    /**
     * 换样列表
     */
    public static HashMap<String, String> getChangeSampleList(String SKUCode, String filter, int pageIndex, int pageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("Filter", filter);
        map.put("PageIndex", pageIndex + "");
        map.put("PageSize", pageSize + "");
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        return map;
    }

    /****
     * 批量换样请求
     *
     * @param SKUCode
     * @param SKUCount
     * @return
     */
    public static HashMap<String, String> batchChangeSample(String SKUCode, String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCount", SKUCount);
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        return map;
    }

    /***
     * 批量换样请求之出样（仓库出货）
     *
     * @param list
     * @param factId
     * @return
     */
    public static String deliverChangeSampleJson(List<InStockDetail> list, String factId) {
        try {
            JSONObject postData = new JSONObject();
            postData.put("UnitID", HotApplication.getInstance().getUnitId());
            postData.put("UserID", HotApplication.getInstance().getUserId());
            postData.put("FactID", factId);
            JSONArray array = new JSONArray();
            for (InStockDetail sb : list) {
                JSONObject mJson = new JSONObject();
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
     * 出样提醒列表
     */
    public static HashMap<String, String> getRemindSampleList(String SKUCode, int pageIndex, int pageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("PageIndex", pageIndex + "");
        map.put("PageSize", pageSize + "");
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        return map;
    }

    /**
     * 出另一个列表
     */
    public static HashMap<String, String> getOutOtherOneSample(String skuCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode);

        return map;
    }

    /**
     * 出另一个请求
     */
    public static HashMap<String, String> submitOutOtherOneSample(String skuCode, String mappingId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode);
        map.put("MappingID", mappingId);

        return map;
    }

    /**
     * 鞋子撤样列表
     */
    public static HashMap<String, String> getShoesWithDrawList(String skuCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode);

        return map;
    }

    /**
     * 撤样请求
     */
    public static HashMap<String, String> getRevokeSample(String SKUCode, String SKUCount) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCount", SKUCount);
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        return map;
    }

    /**
     * 撤一只请求
     */
    public static HashMap<String, String> submitRevokeOneSample(String skuCode, String mappingId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", skuCode);
        map.put("MappingID", mappingId);

        return map;
    }

    /**
     * 直接出（非鞋类）
     * 注：批量
     */
    public static String directOutSampleJson(String SKUCode, List<ScanSkuShelf> list) {
        try {
            JSONObject postData = new JSONObject();
            postData.put("UnitID", HotApplication.getInstance().getUnitId());
            postData.put("UserID", HotApplication.getInstance().getUserId());
            postData.put("SKUID", SKUCode.toUpperCase().trim());
            JSONArray array = new JSONArray();
            for (ScanSkuShelf sb : list) {
                JSONObject mJson = new JSONObject();
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
     * 直接撤（非鞋类）
     * 注：批量
     */
    public static String directRevokeSampleJson(String SKUCode, List<ScanSkuShelf> list) {
        try {
            JSONObject postData = new JSONObject();
            postData.put("UnitID", HotApplication.getInstance().getUnitId());
            postData.put("UserID", HotApplication.getInstance().getUserId());
            postData.put("SKUID", SKUCode.toUpperCase().trim());
            JSONArray array = new JSONArray();
            for (ScanSkuShelf sb : list) {
                JSONObject mJson = new JSONObject();
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
     * 直接撤（鞋类）
     * 注：单条
     */
    public static String shoesDirectSampleJson(String SKUCode, String shelfCode, String mapId) {
        try {
            JSONObject postData = new JSONObject();
            postData.put("UnitID", HotApplication.getInstance().getUnitId());
            postData.put("UserID", HotApplication.getInstance().getUserId());
            postData.put("SKUID", SKUCode.toUpperCase().trim());
            postData.put("ShelfLocationCode", shelfCode);
            postData.put("MappingID", mapId);//鞋类直接撤列表返回
            return postData.toString();
        } catch (JSONException e) {
            Log.e("JSONException", e.getMessage());
            return null;
        }
    }
}
