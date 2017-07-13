package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SampleEntity implements Serializable {

	public boolean isCanBatch;//前端的字段 是不是可以批量
	@SerializedName(value = "Type")
	public String Type;//前端的字段。判断是出样，换样，撤样        1是取另一只，2是取样，3是换样，4是撤样
	@SerializedName(value = "MappingID")
	public String MappingID;
	@SerializedName(value = "ShelfLocationID")
	public String ShelfLocationID;
	@SerializedName(value = "ItemID")
	public String ItemID;
	@SerializedName(value = "ShelfLocationCode")
	public String ShelfLocationCode;
	@SerializedName(value = "SKUID")
	public String SKUID;
	@SerializedName(value = "EndQty")
	public String EndQty;
	@SerializedName(value = "IsShoebox")
	public String IsShoebox;
	@SerializedName(value = "QuoteStockID")
	public String QuoteStockID;
	@SerializedName(value = "UnitID")
	public String UnitID;
	@SerializedName(value = "CustomerID")
	public String CustomerID;
	@SerializedName(value = "CreateBy")
	public String CreateBy;
	@SerializedName(value = "CreateTime")
	public String CreateTime;
	@SerializedName(value = "LastUpdateBy")
	public String LastUpdateBy;
	@SerializedName(value = "LastUpdateTime")
	public String LastUpdateTime;
	@SerializedName(value = "IsDelete")
	public String IsDelete;
	@SerializedName(value = "Label")
	public String Label;
	@SerializedName(value = "SampleTime")
	public String SampleTime;
	
	public SampleEntity copy(SampleEntity entity) {
		SampleEntity entityCopy = new SampleEntity();
		entityCopy.MappingID = entity.MappingID;
		entityCopy.ShelfLocationID = entity.ShelfLocationID;
		entityCopy.ItemID = entity.ItemID;
		entityCopy.ShelfLocationCode = entity.ShelfLocationCode;
		entityCopy.SKUID = entity.SKUID;
		entityCopy.EndQty = entity.EndQty;
		entityCopy.IsShoebox = entity.IsShoebox;
		entityCopy.QuoteStockID = entity.QuoteStockID;
		entityCopy.UnitID = entity.UnitID;
		entityCopy.CustomerID = entity.CustomerID;
		entityCopy.CreateBy = entity.CreateBy;
		entityCopy.CreateTime = entity.CreateTime;
		entityCopy.LastUpdateBy = entity.LastUpdateBy;
		entityCopy.LastUpdateTime = entity.LastUpdateTime;
		entityCopy.IsDelete = entity.IsDelete;
		entityCopy.Label = entity.Label;
		entityCopy.SampleTime = entity.SampleTime;
		return entityCopy;
	}
	
}
