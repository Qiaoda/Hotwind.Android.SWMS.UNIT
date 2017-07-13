package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ApplyHistoryEntity implements Serializable {
	
	@SerializedName(value = "OpID")
	public String OpID;
	@SerializedName(value = "OperTypeString")
	public String OperTypeString;
	@SerializedName(value = "ItemName")
	public String ItemName;
	@SerializedName(value = "FactID")
	public String FactID;
	@SerializedName(value = "Image")
	public String Image;
	@SerializedName(value = "SizeName")
	public String SizeName;
	@SerializedName(value = "ColorName")
	public String ColorName;
	@SerializedName(value = "PUnitName")
	public boolean PUnitName;
	@SerializedName(value = "SKUID")
	public String SKUID;
	@SerializedName(value = "TransactionStatus")
	public String TransactionStatus;
	@SerializedName(value = "TransactionStatusString")
	public String TransactionStatusString;
	@SerializedName(value = "RequestPerson")
	public String RequestPerson;
	@SerializedName(value = "RequestPersonID")
	public String RequestPersonID;
	@SerializedName(value = "OperTime")
	public String OperTime;
	@SerializedName(value = "Price")
	public String Price;
	@SerializedName(value = "ChangePrice")
	public String ChangePrice;
	
}
