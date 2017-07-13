package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class StockTakingShopownerEntity implements Serializable {
	public String ShelfLocationName;
	public String ShelfLocationCode;
	public int CheckStatus;
	public int CheckTimes;
	public String CheckPersonName;
	public String CheckDateTime;
	public String CheckPersonID;
	public int ShelfLocationTypeID;
	public String IsStartCheck;//   是否打开盘点（1打开，0关闭）
	public String IsConfirm;//  是否确认盘点 （1确认，0未确认）
	public String PriceCount;//  差异金额
	public String DiffCount;// 差异数量 
	public int SysStock;//系统数量

}