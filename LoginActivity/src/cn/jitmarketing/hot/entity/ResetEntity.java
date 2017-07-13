package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.Comparator;

import com.google.gson.annotations.SerializedName;

public class ResetEntity implements Serializable,Comparator<ResetEntity>{
	
	@SerializedName(value = "TimeOutString")
	public String TimeOutString;
	@SerializedName(value = "FactID")
	public String FactID;
	@SerializedName(value = "ShelfLocationCode")
	public String ShelfLocationCode;
	@SerializedName(value = "OpID")
	public String OpID;//库存总数量
	@SerializedName(value = "ItemID")
	public String ItemID;
	@SerializedName(value = "ItemName")
	public String ItemName;//是否有出样
	@SerializedName(value = "SKUCode")
	public String SKUCode;//当前出样数量
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
	@SerializedName(value = "ChangePrice")
	public String ChangePrice;//是否有出样
	@SerializedName(value = "SKUStockMappingID")
	public String SKUStockMappingID;//当前出样数量
	@SerializedName(value = "LinkedFactID")
	public String LinkedFactID;
	@SerializedName(value = "UnitID")
	public String UnitID;
	@SerializedName(value = "LastUpdateTime")
	public String LastUpdateTime;
	@SerializedName(value = "OperTypeString")
	public String OperTypeString;//库存总数量
	@SerializedName(value = "TimeOut")
	public String TimeOut;
	@SerializedName(value = "QtyString")
	public String QtyString;
	@SerializedName(value = "OperPerson")
	public String OperPerson;
	@SerializedName(value = "IsShoe")
	public boolean IsShoe;//是否是鞋子
	@SerializedName(value = "IsRevokeSample")
	public boolean IsRevokeSample;//是否撤样
	@SerializedName(value = "ColorID")
	public String ColorID;
	@Override
	public int compare(ResetEntity lhs, ResetEntity rhs) {
		// TODO Auto-generated method stub
		return lhs.TimeOut.compareTo(rhs.TimeOut);
	}
	
}
