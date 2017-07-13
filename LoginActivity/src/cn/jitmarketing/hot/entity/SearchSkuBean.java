package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class SearchSkuBean implements Serializable{

//	public ItemBean item;
//	public List<SkuBean> skus;
	@SerializedName(value = "ItemCode")
	public String ItemCode;
	@SerializedName(value = "ItemName")
	public String ItemName;
	@SerializedName(value = "ItemImage")
	public String ItemImage;
	@SerializedName(value = "Price")
	public String Price;
	@SerializedName(value = "SKUCode")
	public String SKUCode;
}
