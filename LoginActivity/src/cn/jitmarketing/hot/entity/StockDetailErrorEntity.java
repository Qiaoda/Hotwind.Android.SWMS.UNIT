package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class StockDetailErrorEntity implements Serializable {
	
	@SerializedName(value = "ErrorMessage")
	public String ErrorMessage;
}
