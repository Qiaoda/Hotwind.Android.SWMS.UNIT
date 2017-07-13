package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StockDetaiStockDiffEntity implements Serializable {
	
	@SerializedName(value = "SKUCode")
	public String SKUCode;
	@SerializedName(value = "SKUCount")
	public int SKUCount;
	@SerializedName(value = "DifferenceCount")
	public int DifferenceCount;
	
}
