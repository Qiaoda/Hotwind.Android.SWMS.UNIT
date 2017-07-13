package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ItemBean implements Serializable{
	
//	{"ItemID":"19db103fe7f711e4b6e7525400031786",
//		"ItemName":"虚拟商品1","ItemImage":"","ItemUnit":"","IsSampling":true,"ItemPrice":0}
	@SerializedName(value = "ItemID")
	public String itemID;
	@SerializedName(value = "ItemCode")
	public String itemCode;
	@SerializedName(value = "ItemName")
	public String itemName;
	@SerializedName(value = "ItemImage")
	public String itemImage;
	@SerializedName(value = "ItemUnit")
	public String itemUnit;
	@SerializedName(value = "ItemPrice")
	public String itemPrice;
	@SerializedName(value = "ItemCategoryName")
	public String itemCategoryName;
	@SerializedName(value = "IsSomeSampling")
	public boolean isSomeSampling;
	
}
