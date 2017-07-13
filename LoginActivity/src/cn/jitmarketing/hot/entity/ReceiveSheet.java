package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

/**
 * 对门店来说，发货单在门店入货
 */
public class ReceiveSheet implements Serializable{

	@SerializedName(value="ReceiveSheetNo")
	public String receiveSheetNo;
	@SerializedName(value="LinkedOrderNo")
	public String linkedOrderNo;
	@SerializedName(value="OrderType")
	public String orderType;
	@SerializedName(value="CreateTime")
	public String createTime;
	@SerializedName(value="LastUpdateTime")
	public String updateTime;
	@SerializedName(value="Status")
	public int status;
	@SerializedName(value="SKUQty")
	public float skuQty;
//	@SerializedName(value="detailList")
//	public ArrayList<InStockSku> detailList;
	public int ResultStatus;
	@SerializedName(value="DisOrderNo")
	public String DisOrderNo;
	@SerializedName(value="FromStoreName")
	public String FromStoreName;
	@SerializedName(value="FromRemark")
	public String FromRemark;
}

