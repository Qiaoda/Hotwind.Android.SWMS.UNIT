package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class PosRecordEntity implements Serializable {

	@SerializedName(value = "BillNo")
	public String BillNo;
	@SerializedName(value = "SKUCode")
	public String SKUCode;
	@SerializedName(value = "ItemName")
	public String ItemName;
	@SerializedName(value = "Qry")
	public int Qry;
	@SerializedName(value = "Result")
	public String Result;
	@SerializedName(value = "IsOK")
	public boolean IsOK;
	@SerializedName(value = "ProcessStatus")
	public String ProcessStatus;
	@SerializedName(value = "LastUpdateTime")
	public String LastUpdateTime;
	@SerializedName(value = "CreateTime")
	public String CreateTime;
	@SerializedName(value = "ColorID")
	public String ColorID;
	@SerializedName(value = "ColorName")
	public String ColorName;
	@SerializedName(value = "SizeName")
	public String SizeName;
	@SerializedName(value = "ItemID")
	public String ItemID;
	@SerializedName(value = "Type")
	public String Type;
}
