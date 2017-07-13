package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class DoWeBean implements Serializable {
	public String ShelfLocation;
	public ArrayList<InStockDetail> lastList;

	public DoWeBean(String shelfLocation, ArrayList<InStockDetail> lastList) {
		super();
		ShelfLocation = shelfLocation;
		this.lastList = lastList;
	}
	public DoWeBean() {
		super();

	}
}
