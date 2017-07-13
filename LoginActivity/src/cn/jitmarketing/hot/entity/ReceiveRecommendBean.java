package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class ReceiveRecommendBean implements Serializable{
	@SerializedName(value="SKUCode")
	public String SKUCode;
	@SerializedName(value="ShelfLocationCode")
	public String ShelfLocationCode;
}
