package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class BeforeInStockBean implements Serializable{
	public String SKUCode;
	public float SKUCount;
	public BeforeInStockBean(String sKUCode,float sKUCount) {
		super();
		SKUCode = sKUCode;
		SKUCount=sKUCount;
	}
	
}
