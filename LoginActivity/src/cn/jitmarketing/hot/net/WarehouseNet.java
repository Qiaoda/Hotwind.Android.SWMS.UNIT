package cn.jitmarketing.hot.net;

import android.R.integer;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.entity.BeforeInStockBean;
import cn.jitmarketing.hot.entity.DoWeBean;
import cn.jitmarketing.hot.entity.InStockDetail;
import cn.jitmarketing.hot.entity.ShelfBean;
import cn.jitmarketing.hot.entity.SkuBean;

public class WarehouseNet {

    private static Gson gson = new Gson();

    /**
     * 移库
     */
    public static HashMap<String, String> moveShelfLocation(String oldShelf, String newShelf,
                                                            List<SkuBean> skuList) {

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", oldShelf);
        map.put("NewShelfLocationCode", newShelf);
        map.put("SKUList", gson.toJson(skuList));

        return map;
    }

    /**
     * 商品上架
     */
    public static HashMap<String, String> skuShelf(List<DoWeBean> shelfList) {
        HashMap<String, String> map = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (DoWeBean bean : shelfList) {
            HashMap<String, String> mapChild = new HashMap<String, String>();
            mapChild.put("ShelfLocationCode", bean.ShelfLocation);
            mapChild.put("SKUList", gson.toJson(bean.lastList));
            list.add(mapChild);
        }
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("Data", gson.toJson(list));
        return map;
    }

    /**
     * 老商品上架
     */
    public static HashMap<String, String> oldskuShelf(List<ShelfBean> shelfList) {
        HashMap<String, String> map = new HashMap<String, String>();
        List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        for (ShelfBean bean : shelfList) {
            HashMap<String, String> mapChild = new HashMap<String, String>();
            mapChild.put("ShelfLocationCode", bean.shelfCode);
            mapChild.put("SKUList", gson.toJson(bean.skuList));
            list.add(mapChild);
        }
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("Data", gson.toJson(list));
        return map;
    }

    /**
     * 收货单明细（获取暂存）
     */
    public static HashMap<String, String> stockInfo(String ReceiveSheetNo) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ReceiveSheetNo", ReceiveSheetNo);
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 收货单推荐库位列表
     */
    public static HashMap<String, String> recommend(String ReceiveSheetNo) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("ReceiveSheetNo", ReceiveSheetNo);
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 拿货
     */
    public static HashMap<String, String> skuShelf(String shelfLocationId, List<ShelfBean>
			shelfList) {
        List<SkuBean> skuList = new ArrayList<SkuBean>();
        for (ShelfBean shelf : shelfList) {
            skuList.addAll(shelf.skuList);
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", shelfLocationId);
        map.put("SKUList", gson.toJson(skuList));

        return map;
    }

    /**
     * 初盘确认
     */
    public static HashMap<String, String> suring(String id, String CheckTastID, String TaskStatus) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", id);
        map.put("CheckTastID", CheckTastID);
        map.put("TaskStatus", TaskStatus);
        return map;
    }

    /**
     * 盘点结束
     */
    public static HashMap<String, String> stockTaskFinish(String TaskID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("TaskID", TaskID);
        return map;
    }

    /**
     * 创建盘点任务成功
     */
    public static HashMap<String, String> newOk(String CheckNoticeID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("CheckNoticeID", CheckNoticeID);
        return map;
    }

    /**
     * 暂存列表
     */
    public static HashMap<String, String> tempList(String AllocationOrderID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("AllocationOrderID", AllocationOrderID);
        return map;
    }

    /**
     * 需要盘点的库位任务通知列表
     */
    public static HashMap<String, String> getStockinfoList() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 盘点的库位差异列表
     */
    public static HashMap<String, String> getStockdifferent(String currTaskId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("TaskID", currTaskId);
        return map;
    }

    /**
     * 盘点任务历史详情
     */
    public static HashMap<String, String> getStockDetailList(String checkTaskID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("CheckTaskID", checkTaskID);
        return map;
    }
    //	public static HashMap<String, String> getStockDetailList(String checkTaskID, int
	// PageIndex,int PageSize, String Type) {
    //		HashMap<String, String> map = new HashMap<String, String>();
    //		map.put("UserID", HotApplication.getInstance().getUserId());
    //		map.put("UnitID", HotApplication.getInstance().getUnitId());
    //		map.put("CheckTaskID", checkTaskID);
    //		map.put("PageIndex", String.valueOf(PageIndex));
    //		map.put("PageSize", String.valueOf(PageSize));
    //		map.put("Type", Type);
    //		return map;
    //	}

    /**
     * 获取盘点
     */
    public static HashMap<String, String> getStockTask(String CheckNoticeID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("CheckNoticeID", CheckNoticeID);
        return map;
    }

    /**
     * 需要盘点的库位列表
     */
    public static HashMap<String, String> openSampleShelf(String ShelfLocationCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", ShelfLocationCode);
        return map;
    }

    /**
     * 需要盘点的库位查询
     */
    public static HashMap<String, String> getShelfQuery(String ShelfLocationCode, String SKUCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", ShelfLocationCode);
        map.put("SKUCode", SKUCode);
        return map;
    }

    /**
     * 需要盘点的库位列表
     */
    public static HashMap<String, String> getShelfDetail(String ShelfLocationCode, String
            SortName, String SortType, String PageIndex, String PageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("PageIndex", PageIndex);
        map.put("PageSize", PageSize);
        map.put("ShelfLocationCode", ShelfLocationCode);
        map.put("SortName", SortName);
        map.put("SortType", SortType);
        return map;
    }

    /**
     * 库位历史详情
     */
    public static HashMap<String, String> getShelfHistoryDetail(String ShelfLocationCode, String
            CheckTaskID, String PageIndex, String PageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("PageIndex", PageIndex);
        map.put("PageSize", PageSize);
        map.put("ShelfLocationCode", ShelfLocationCode);
        map.put("ChecktaskID", CheckTaskID);
        return map;
    }

    /**
     * 需要盘点的库位列表
     */
    public static HashMap<String, String> getCheckStockList() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 盘点确认
     */
    public static HashMap<String, String> pandianCofirm(String ShelfLocationCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", ShelfLocationCode);
        return map;
    }

    /**
     * 库位盘点
     */
    public static HashMap<String, String> checkStock(String shelfLocationId, String
			ChecktaskShelflocationID, List<SkuBean> skuList) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", shelfLocationId);
        map.put("SKUList", gson.toJson(skuList));
        map.put("ChecktaskShelflocationID", ChecktaskShelflocationID);
        return map;
    }

    public static HashMap<String, String> searchPosRecord(String BillNo, String SKUCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("BillNo", BillNo);
        map.put("SKUCode", SKUCode);
        return map;
    }

    public static HashMap<String, String> posRecordParams(int page, int pageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("PageIndex", String.valueOf(page));
        map.put("PageSize", String.valueOf(pageSize));
        return map;
    }

    public static HashMap<String, String> stockDelParams(String ShelfLocationCode) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ShelfLocationCode", ShelfLocationCode);
        return map;
    }

    /**
     * 软件更新
     */
    public static HashMap<String, String> updateApp(String OrgVersion) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("OrgVersion", OrgVersion);
        return map;
    }

    /**
     * 库位添加
     */
    public static HashMap<String, String> stockListParams(String unitId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 拿货-需要出库的列表
     */
    public static HashMap<String, String> checkGoodsList(String defult) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("Filter", defult);
        return map;
    }

    /**
     * 申请列表-需要出库的列表
     */
    public static HashMap<String, String> getGoodsList(int page, int pageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("PageIndex", String.valueOf(page));
        map.put("PageSize", String.valueOf(pageSize));
        return map;
    }

    /**
     * 取消申请列表
     */
    public static HashMap<String, String> getApplyCancel(String factID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("FactID", factID);
        return map;
    }

    /**
     * 商品出库
     * params count 出库数量
     */
    public static HashMap<String, String> outStock(String skuCode, String selfCode, String factId) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("SKUCode", skuCode.toUpperCase().trim());
        map.put("ShelfLocationCode", selfCode);
        map.put("FactID", factId);
        return map;
    }

    /**
     * 需要入库的发货单列表
     */
    public static HashMap<String, String> getInStockList(int PageIndex,int PageSize) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("PageIndex", String.valueOf(PageIndex));
        map.put("PageSize", String.valueOf(PageSize));
        return map;
    }

    /**
     * 商品入库
     */
    public static HashMap<String, String> skuStockIn(String receiveSheetNo,
													 List<BeforeInStockBean> skuList) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("ReceiveSheetNo", receiveSheetNo);
        map.put("SKUList", gson.toJson(skuList));

        return map;
    }

    /**
     * 盘点历史
     */
    public static HashMap<String, String> stockHistory(String TaskID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("TaskID", TaskID);
        return map;
    }

    /**
     * 调整单历史
     */
    public static HashMap<String, String> trimHistory() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 手工调整单
     */
    public static HashMap<String, String> trimingHistory(String SKUCode, String ChangeQry, String ShelfLocationCode, int type, String remark) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("SKUCode", SKUCode.toUpperCase().trim());
        map.put("ChangeQry", ChangeQry);
        map.put("ShelfLocationCode", ShelfLocationCode);
        map.put("ChangeType", String.valueOf(type));
        map.put("Remark", remark);
        return map;
    }

    /**
     * 确定上架/提交暂存
     */
    public static HashMap<String, String> tempsto(String no, List<InStockDetail> list) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ReceiveSheetNo", no);
        map.put("SKUList", gson.toJson(list));
        return map;
    }

    /**
     * 暂存
     */
    public static HashMap<String, String> takeInfo() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 调拨历史
     */
    public static HashMap<String, String> allHistory(String allocationOrderID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("AllocationOrderID", allocationOrderID);
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        return map;
    }

    /**
     * 库位添加
     */
    public static HashMap<String, String> tempDelete(String allocationOrderID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("AllocationOrderID", allocationOrderID);
        return map;
    }
    /**
     * 入库上架搜索
     */
    public static HashMap<String, String> rukuSearch(String receiveSheetNo) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("ReceiveSheetNo", receiveSheetNo);
        return map;
    }
    /**
     * 获取抽盘单号
     */
    public static HashMap<String, String> getRandomCheckCode() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("InventoryCheckTaskType", "HTCP");
        return map;
    }
    /**
     * 创建抽盘任务
     */
    public static HashMap<String, String> createRandomCheck(String InventoryCheckTaskID) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("InventoryCheckTaskID", InventoryCheckTaskID);
        return map;
    }
    /**
     * 获取抽盘明细
     */
    public static HashMap<String, String> getRandomCheckDetail() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("UserID", HotApplication.getInstance().getUserId());
        map.put("UnitID", HotApplication.getInstance().getUnitId());
        map.put("InventoryCheckTaskType", "HTCP");
        return map;
    }
}

