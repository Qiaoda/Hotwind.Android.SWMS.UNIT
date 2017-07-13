package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StockTakingDetailEntity implements Serializable{
	@SerializedName(value="SKUID")
	public String SKUID;
	@SerializedName(value="KCSKUCount")
	public int KCSKUCount;
	@SerializedName(value="PDSKUCount")
	public int PDSKUCount;
	@SerializedName(value="CYPrice")
	public String CYPrice;
	@SerializedName(value="Status")//修改状态，0是新增，1是修改（前端字段）
	public int Status;
	@SerializedName(value="CYCount")//差异数量
	public int CYCount;
	
}
