package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SkuBean implements Serializable{

//	{"SKUID":"6346bdf6e7f211e4ad5b525400b6f9e4","SKUCode":"V001001",
//		"SKUName":"虚拟衣服SKU-1","SKUColor":"蓝色","SKUSize":"175/90",
//		"SKUCount":1029,"SKUShelfLocation":"Ew001S10001、Ew001S10002","SKUPrice":0}
	@SerializedName(value = "ColorID")
	public String ColorID;
	@SerializedName(value = "SKUID")
	public String skuId;
	@SerializedName(value = "SKUCode")
	public String skuCode;
	@SerializedName(value = "SKUName")
	public String skuName;
	@SerializedName(value = "SKUColor")
	public String skuColor;
	@SerializedName(value = "SKUSize")
	public String skuSize;
	@SerializedName(value = "SKUCount")
	public float skuCount;
	@SerializedName(value = "SKUShelfLocation")
	public String skuShelfLocation;
	@SerializedName(value = "SKUPrice")
	public String skuPrice;
	@SerializedName(value = "SKUIsSampling")
	public boolean skuIsSampling;
	@SerializedName(value = "SKUSamplingCount")
	public float skuSamplingCount;
	@SerializedName(value = "SampleDate")
	public String sampleDate;
	@SerializedName(value = "IsSomeOut")
	public boolean IsSomeOut;
	@SerializedName(value = "DifferenceCount")
	public String DifferenceCount;
	//用于盘点
	@SerializedName(value = "TheoryCount")
	public String TheoryCount;
	
	public SkuBean(String skuCode, int skuCount) {
		super();
		this.skuCode = skuCode;
		this.skuCount = skuCount;
	}
	public SkuBean(String skuCode, float skuCount) {
		super();
		this.skuCode = skuCode;
		this.skuCount = skuCount;
	}
	
}
