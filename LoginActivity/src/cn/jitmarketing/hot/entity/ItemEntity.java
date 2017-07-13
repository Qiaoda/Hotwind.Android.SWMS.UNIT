package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ItemEntity implements Serializable{
	
	@SerializedName(value = "ItemID")
	public String ItemID;
	@SerializedName(value = "ItemCode")
	public String ItemCode;
	@SerializedName(value = "ItemName")
	public String ItemName;
	@SerializedName(value = "ItemImage")
	public String ItemImage;
//	@SerializedName(value = "Price")
//	public String Price;
	@SerializedName(value = "AttDId")
	public String AttDId;
	@SerializedName(value = "IsSomeSampling")
	public boolean IsSomeSampling;
	@SerializedName(value = "ItemUnit")
	public String ItemUnit;
//	@SerializedName(value = "ChangePrice")
//	public String ChangePrice;
//	@SerializedName(value = "ChangePriceDesc")
//	public String ChangePriceDesc;
	
}
