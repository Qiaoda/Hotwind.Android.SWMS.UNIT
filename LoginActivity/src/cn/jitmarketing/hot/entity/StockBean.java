package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StockBean implements Serializable{

	@SerializedName(value = "ShelfLocationCode")
	public String stockCode;
	@SerializedName(value = "LastUpdateTime")
	public String lastUpdateTime;
	@SerializedName(value = "Status")
	public int status = 0;
	
}
