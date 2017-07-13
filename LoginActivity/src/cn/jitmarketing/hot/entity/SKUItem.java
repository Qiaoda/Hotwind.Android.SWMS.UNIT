package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SKUItem implements Serializable{

	@SerializedName(value = "SKUID")
	public String SKUID;
	@SerializedName(value = "Count")
	public String Count;		//库存
	@SerializedName(value = "OutCount")
	public String OutCount;		//出库数量
	@SerializedName(value = "ShelfLocation")
	public String ShelfLocation;
	@SerializedName(value = "Color")
	public String Color;
	@SerializedName(value = "Price")
	public String Price;
	@SerializedName(value = "Size")
	public String Size;
	@SerializedName(value = "SKUName")
	public String SKUName;
	@SerializedName(value = "Opid")
	public String Opid;			//1024  取新     2048   取另外一只  4096  出样
	@SerializedName(value = "SamplingType")
	public String SamplingType;
	@SerializedName(value = "OutTime")
	public String OutTime;
	@SerializedName(value = "FactID")
	public String FactID;
	@SerializedName(value = "CreateBy")
	public String CreateBy;
	@SerializedName(value = "SKUCode")
	public String SKUCode;
//	public String PendingCount;	//当日未经处理的数量
	public int PendedCount;	//当日已经处理的数量
	public String ResetTime;
	public String SKUImage;
	public String Status;
}