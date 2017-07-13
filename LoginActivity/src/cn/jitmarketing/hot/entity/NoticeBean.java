package cn.jitmarketing.hot.entity;

import com.google.gson.annotations.SerializedName;

public class NoticeBean {

	@SerializedName(value = "ItemName")
	public String ItemName;
	@SerializedName(value = "OpID")//1024 2048取新 ，4096 4098出样
	public int OpID;
	@SerializedName(value = "OperTypeString")
	public String OperTypeString;
	@SerializedName(value = "OperPerson")
	public String OperPerson;
	@SerializedName(value = "OperTime")
	public String OperTime;
	@SerializedName(value = "SKUCode")
	public String SKUCode;
	@SerializedName(value = "TransactionStatus")
	public int Type;
	@SerializedName(value = "NoticeType")
	public int NoticeType;
	@SerializedName(value = "AttDId")
	public String AttDId;
}
