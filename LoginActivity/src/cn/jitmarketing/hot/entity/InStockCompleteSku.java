package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InStockCompleteSku implements Serializable{
	
	@SerializedName(value="DetailtwoID")
	public String detailID;
	@SerializedName(value="SKUID")
	public String skuCode;
	@SerializedName(value="SKUDesc")
	public String sKUDesc;
	@SerializedName(value="Color")
	public String color;
	@SerializedName(value="Size")
	public String size;
	@SerializedName(value="Qty")
	public int qty;
	@SerializedName(value="UnitID")
	public String unitID;
	@SerializedName(value="CustomerID")
	public String customerID;
	@SerializedName(value="CreateTime")
	public String createTime;
	@SerializedName(value="LastUpdateTime")
	public String lastUpdateTime;
	
}
