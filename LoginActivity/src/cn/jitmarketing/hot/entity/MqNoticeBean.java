package cn.jitmarketing.hot.entity;

import com.google.gson.annotations.SerializedName;

public class MqNoticeBean {

	@SerializedName(value = "Ticker")
	public String Ticker;
	@SerializedName(value = "Title")
	public String Title;
	@SerializedName(value = "Content")
	public String Content;
	@SerializedName(value = "SubText")
	public String SubText;
	@SerializedName(value = "FactID")
	public String FactID;
	@SerializedName(value = "SubType")
	public String SubType;
	@SerializedName(value = "Type")
	public String Type;
	@SerializedName(value = "IsOutSample")
	public boolean IsOutSample;
	@SerializedName(value = "Requester")
	public String Requester;//请求人
	
}
