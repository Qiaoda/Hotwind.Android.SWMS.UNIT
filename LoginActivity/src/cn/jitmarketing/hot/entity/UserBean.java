package cn.jitmarketing.hot.entity;

import com.google.gson.annotations.SerializedName;

public class UserBean {

//user":{"UserID":"1","UserAccount":"admin","UserPassword":"123123"," +
//		""UserCode":"店长","Status":0,"UnitID":1,"CustomerID":"hotwindwms"," +
//		""CreateBy":"1","CreateTime":"2015-04-18T00:00:00","LastUpdateBy":"1"," +
//				""LastUpdateTime":"2015-04-20T18:13:34","IsDelete":0}}}
	@SerializedName(value = "UserID")
	public String UserID;
	@SerializedName(value = "UserAccount")
	public String UserAccount;
	@SerializedName(value = "UserPassword")
	public String UserPassword;
	@SerializedName(value = "UserCode")
	public String UserCode;
	@SerializedName(value = "Status")
	public String Status;
	@SerializedName(value = "UnitID")
	public String UnitID;
	@SerializedName(value = "CustomerID")
	public String CustomerID;
	@Override
	
	public String toString() {
		return "User [UserID=" + UserID + ", UserAccount=" + UserAccount
				+ ", UserPassword=" + UserPassword + ", UserCode=" + UserCode
				+ ", Status=" + Status + ", UnitID=" + UnitID + ", CustomerID="
				+ CustomerID + "]";
	}
	
	
}
