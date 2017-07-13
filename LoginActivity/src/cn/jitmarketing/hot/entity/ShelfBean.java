package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ShelfBean implements Serializable{

	@SerializedName(value = "shelfId")
	public String shelfId;
	@SerializedName(value = "shelfCode")
	public String shelfCode;
	@SerializedName(value = "shelfName")
	public String shelfName;
	@SerializedName(value = "skuList")
	public List<SkuBean> skuList;
	
	public ShelfBean(String shelfCode, List<SkuBean> skuList) {
		super();
		this.shelfCode = shelfCode;
		this.skuList = skuList;
	}
	
	
}
