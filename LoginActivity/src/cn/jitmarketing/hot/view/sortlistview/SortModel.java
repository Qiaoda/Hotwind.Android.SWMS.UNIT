package cn.jitmarketing.hot.view.sortlistview;

public class SortModel {

	public String ShelfLocationName;
	public String ShelfLocationCode;
	public int CheckStatus;
	public int CheckTimes;
	public String CheckPersonName;
	public String CheckDateTime;
	public String CheckPersonID;
	public String ShelfLocationTypeID;

	private String name;   //显示的数据
	private String sortLetters;  //显示数据拼音的首字母
	
	public String getShelfLocationName() {
		return ShelfLocationName;
	}
	public void setShelfLocationName(String shelfLocationName) {
		ShelfLocationName = shelfLocationName;
	}
	public String getShelfLocationCode() {
		return ShelfLocationCode;
	}
	public void setShelfLocationCode(String shelfLocationCode) {
		ShelfLocationCode = shelfLocationCode;
	}
	public int getCheckStatus() {
		return CheckStatus;
	}
	public void setCheckStatus(int checkStatus) {
		CheckStatus = checkStatus;
	}
	public int getCheckTimes() {
		return CheckTimes;
	}
	public void setCheckTimes(int checkTimes) {
		CheckTimes = checkTimes;
	}
	public String getCheckPersonName() {
		return CheckPersonName;
	}
	public void setCheckPersonName(String checkPersonName) {
		CheckPersonName = checkPersonName;
	}
	public String getCheckDateTime() {
		return CheckDateTime;
	}
	public void setCheckDateTime(String checkDateTime) {
		CheckDateTime = checkDateTime;
	}
	public String getCheckPersonID() {
		return CheckPersonID;
	}
	public void setCheckPersonID(String checkPersonID) {
		CheckPersonID = checkPersonID;
	}
	public String getShelfLocationTypeID() {
		return ShelfLocationTypeID;
	}
	public void setShelfLocationTypeID(String shelfLocationTypeID) {
		ShelfLocationTypeID = shelfLocationTypeID;
	}
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
