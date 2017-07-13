package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class InStockDetail implements Serializable {
	public String SKUCode;
	public float SKUCount;
	public String ShelfLocationCode;

	public InStockDetail(String SKUCode, float SKUCount, String ShelfLocationCode) {
		super();
		this.SKUCode = SKUCode;
		this.SKUCount = SKUCount;
		this.ShelfLocationCode = ShelfLocationCode;
	}
	public InStockDetail(String SKUCode, float SKUCount) {
		super();
		this.SKUCode = SKUCode;
		this.SKUCount = SKUCount;
	}
	
	public InStockDetail() {
		super();
	}
	public String getSKUCode() {
		return SKUCode;
	}
	public void setSKUCode(String sKUCode) {
		SKUCode = sKUCode;
	}
	public float getSKUCount() {
		return SKUCount;
	}
	public void setSKUCount(float sKUCount) {
		SKUCount = sKUCount;
	}
	public String getShelfLocationCode() {
		return ShelfLocationCode;
	}
	public void setShelfLocationCode(String shelfLocationCode) {
		ShelfLocationCode = shelfLocationCode;
	}
	
}
