package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class StockTakingWarehouseEntity implements Serializable {
	
	public String ShelfLocationName;
	public String ShelfLocationCode;
	public int CheckStatus;
	public int CheckTimes;
	public String CheckPersonName;
	public String CheckDateTime;
	public String CheckPersonID;
	public String ShelfLocationTypeID;
	public int IsStartCheck;
	public int IsConfirm;
	public String PriceCount;
	public int DiffCount;
	
	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSortLetters() {
		return sortLetters;
	}
	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}
}