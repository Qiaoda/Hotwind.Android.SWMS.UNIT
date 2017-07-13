package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ShopTaskBean implements Serializable {
	@SerializedName(value="TaskID")
	public String TaskID;
	@SerializedName(value="ShelfLocationCount")
	public int ShelfLocationCount;//         盘点总库位数
	@SerializedName(value="AlreadyShelfLocationCount")
	public int AlreadyShelfLocationCount;//   已盘库位数
	@SerializedName(value="NeedCount")
	public String NeedCount;//    当前盘点库存数量
	@SerializedName(value="AlreadyCount")
	public String AlreadyCount;//   已盘sku数量
	@SerializedName(value="DifferenceShelfLocationCount")
	public int DifferenceShelfLocationCount;//   已盘点有差异库位个数
	@SerializedName(value="DifferenceMoneyCount")
	public String DifferenceMoneyCount;//   已盘差异金额
	@SerializedName(value="AdjustMoneyCount")
	public String AdjustMoneyCount;//    已做调整单金额
	@SerializedName(value="PosPrice")
	public String PosPrice;//    待处理金额
}
