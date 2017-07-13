package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

public class ReceiveBean implements Serializable{
	@SerializedName(value="detailList")
	public ArrayList<InStockDetail> detailList;
	@SerializedName(value="instockdetailList")
	public ArrayList<InStockDetail> checkinstockdetailList;
}
