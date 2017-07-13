package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class AllocationBean implements Serializable{
	@SerializedName(value="AllocationOrderCode")
	public String allocationOrderCode;
	@SerializedName(value="AllocationOrderID")
	public String AllocationOrderID;
	@SerializedName(value="CreateTime")
	public String createTime;
	@SerializedName(value="TargetUnitName")
	public String targetUnitName;
	@SerializedName(value="Qty")
	public int qty;
	@SerializedName(value="List")
	public ArrayList<SkuBean> list;	
	@SerializedName(value="CreateBy")
	public String OperPerson;
	@SerializedName(value="Status")
	public int Status;
	@SerializedName(value="TargetUnitID")
	public String TargetUnitID;
	@SerializedName(value="OrderType")
	public int OrderType;
	@SerializedName(value="BackDefectiveType")
	public int BackDefectiveType;
	@SerializedName(value="Remark")
	public String Remark;
}
