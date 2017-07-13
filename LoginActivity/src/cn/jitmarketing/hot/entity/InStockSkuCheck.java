package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InStockSkuCheck implements Serializable{
	
	@SerializedName(value="SKUCode")
	public String skuCode;
	@SerializedName(value="planQty")
	public int planQty;
	@SerializedName(value="factQty")
	public int factQty;
}
