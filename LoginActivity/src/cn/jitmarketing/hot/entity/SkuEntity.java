package cn.jitmarketing.hot.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class SkuEntity implements Serializable {
 
    @SerializedName(value = "SKUCode")
    public String SKUCode;
    @SerializedName(value = "ColorName")
    public String ColorName;
    @SerializedName(value = "ColorID")
    public String ColorID;
    @SerializedName(value = "SKUCount")
    public String SKUCount;//库存总数量
    @SerializedName(value = "SizeName")
    public String SizeName;
    @SerializedName(value = "SKUCountString")
    public String SKUCountString;
    @SerializedName(value = "IsHasSample")
    public boolean IsHasSample;//是否有出样
    @SerializedName(value = "SampleCountString")
    public String SampleCountString;//当前出样数量（带单位）
    @SerializedName(value = "SampleList")
    public ArrayList<SampleEntity> SampleList;
    @SerializedName(value = "StockSKUShelfLocationString")
    public String StockSKUShelfLocationString;
    @SerializedName(value = "SampleCount")
    public String SampleCount;   // 出样数量
    @SerializedName(value = "SampleTJ")
    public String SampleTJ;//样品所在的库位（撤样时的推荐库位）
    @SerializedName(value = "ZHI")
    public String ZHI; // 出样仓库剩余数量（鞋类只数，非鞋类为0）
    @SerializedName(value = "SHU")
    public String SHU; // 出样仓库剩余数量（鞋类则为双数，非鞋泪则为件数）
	@SerializedName(value = "Price")
	public String Price;
	@SerializedName(value = "ChangePrice")
	public String ChangePrice;
	@SerializedName(value = "ChangePriceDesc")
	public String ChangePriceDesc;

}
