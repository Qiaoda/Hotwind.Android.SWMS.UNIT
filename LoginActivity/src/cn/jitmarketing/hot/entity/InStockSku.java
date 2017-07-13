package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class InStockSku implements Serializable{
	@SerializedName(value="DetailID")
	public String detailID;
//	@SerializedName(value="ReceiveSheetNo")
//	public String receiveSheetNo;
	@SerializedName(value="SKUCode")
	public String skuCode;
	@SerializedName(value="SKUDesc")
	public String sKUDesc;
	@SerializedName(value="Color")
	public String color;
	@SerializedName(value="Size")
	public String size;
	@SerializedName(value="Qty")
	public int shouldCount;
	@SerializedName(value="SKUCount")
	public int reallyCount;
	
	public InStockSku(String skuCode, int shouldCount, int reallyCount){
		this.skuCode = skuCode;
		this.shouldCount = shouldCount;
		this.reallyCount = reallyCount;
	}
}
