package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StockDetaiShelfDiffEntity implements Serializable {

	@SerializedName(value = "ShelfLocationCode")
	public String ShelfLocationCode;
	@SerializedName(value = "ShelfLocationName")
	public String ShelfLocationName;
	@SerializedName(value = "ShelfLocationCodeStatus")
	public String ShelfLocationCodeStatus;
	@SerializedName(value = "CheckStatus")
	public int CheckStatus;
	@SerializedName(value = "CheckTimes")
	public String CheckTimes;
	@SerializedName(value = "UserCode")
	public String UserCode;
	@SerializedName(value = "LastUpdateTime")
	public String LastUpdateTime;
	@SerializedName(value = "DiffCount")
	public int DiffCount;
	@SerializedName(value = "DiffPriceCount")
	public String DiffPriceCount;
	
}
