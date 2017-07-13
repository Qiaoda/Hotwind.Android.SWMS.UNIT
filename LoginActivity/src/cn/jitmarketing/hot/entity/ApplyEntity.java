package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ApplyEntity implements Serializable {
	
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
	public int TransactionStatus;
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
	@SerializedName(value = "QtyString")
	public String QtyString;
	@SerializedName(value = "Qty")
	public String Qty;
	@SerializedName(value = "OpeQty")
	public String OpeQty;
	@SerializedName(value = "TimeOutString")
	public String TimeOutString;
	@SerializedName(value = "TimeOut")
	public String TimeOut;
	@SerializedName(value = "History")
	public List<ApplyHistoryEntity> History;
	
}
