package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class TakeGoodsEntity implements Serializable {
	
	@SerializedName(value = "FactID")
	public String FactID;
	@SerializedName(value = "TransactID")
	public String TransactID;
	@SerializedName(value = "OpID")
	public String OpID;//库存总数量
	@SerializedName(value = "TransactionStatus")
	public int TransactionStatus;
	@SerializedName(value = "ItemID")
	public String ItemID;
	@SerializedName(value = "ItemName")
	public String ItemName;//是否有出样
	@SerializedName(value = "SKUID")
	public String SKUID;//当前出样数量
	@SerializedName(value = "ColorName")
	public String ColorName;
	@SerializedName(value = "SizeName")
	public String SizeName;
	@SerializedName(value = "Qty")
	public String Qty;
	@SerializedName(value = "PUnitName")
	public String PUnitName;
	@SerializedName(value = "Image")
	public String Image;//库存总数量
	@SerializedName(value = "Price")
	public String Price;
	@SerializedName(value = "LastUpdateByName")
	public String LastUpdateByName;
	@SerializedName(value = "ChangePrice")
	public String ChangePrice;//是否有出样
	@SerializedName(value = "SKUStockMappingID")
	public String SKUStockMappingID;//当前出样数量
	@SerializedName(value = "LinkedFactID")
	public String LinkedFactID;
	@SerializedName(value = "UnitID")
	public String UnitID;
	@SerializedName(value = "LastUpdateBy")
	public String LastUpdateBy;
	@SerializedName(value = "LastUpdateTime")
	public String LastUpdateTime;
	@SerializedName(value = "OperTypeString")
	public String OperTypeString;//库存总数量
	@SerializedName(value = "StockSKUShelfLocationString")
	public String StockSKUShelfLocationString;
	@SerializedName(value = "CanOperShelfLocationCodes")
	public String[] CanOperShelfLocationCodes;
	@SerializedName(value = "TimeOut")
	public String TimeOut;
	@SerializedName(value = "TimeOutString")
	public String TimeOutString;
	@SerializedName(value = "ColorID")
	public String ColorID;
	
}
