package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class MenuBean implements Serializable{

//	{"MenuID":"1","MenuCode":"CKC","ParentMenuID":null,"MenuLevel":null,
//		"MenuUrl":null,"IconUrl":"","DisplayIndex":1,"MenuName":"查库存",
//		"Status":1,"CustomerID":null,"CreateBy":null,"CreateTime":"2015-04-18T22:24:50",
//		"LastUpdateBy":null,"LastUpdateTime":"2015-04-18T22:24:50","IsDelete":0},
	@SerializedName(value = "MenuID")
	public String menuID;
	@SerializedName(value = "MenuCode")
	public String menuCode;
	@SerializedName(value = "ParentMenuID")
	public String parentMenuID;
	@SerializedName(value = "MenuUrl")
	public String menuUrl;
	@SerializedName(value = "IconUrl")
	public String iconUrl;
	@SerializedName(value = "DisplayIndex")
	public String displayIndex;
	@SerializedName(value = "MenuName")
	public String menuName;
	@SerializedName(value = "Status")
	public String status;
	public int num = 0;
	
	public MenuBean(String menuCode, String menuName){
		this.menuCode = menuCode;
		this.menuName = menuName;
	}
}
