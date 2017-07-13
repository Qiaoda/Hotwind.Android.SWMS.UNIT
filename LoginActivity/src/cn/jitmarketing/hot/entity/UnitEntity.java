package cn.jitmarketing.hot.entity;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class UnitEntity {
	@SerializedName(value="AreaList")
	public List<CityBean> AreaList;
	@SerializedName(value="CityList")
	public List<AreaBean> CityList;
	@SerializedName(value="UnitList")
	public List<UnitBeanOne> UnitList;

}
