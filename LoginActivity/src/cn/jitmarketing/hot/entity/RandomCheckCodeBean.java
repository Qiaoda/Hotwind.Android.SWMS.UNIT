package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class RandomCheckCodeBean implements Serializable {

	private ArrayList<CodeBean> InventoryCheckTaskIDList;

	public ArrayList<CodeBean> getInventoryCheckTaskIDList() {
		return InventoryCheckTaskIDList;
	}

	public void setInventoryCheckTaskIDList(ArrayList<CodeBean> inventoryCheckTaskIDList) {
		InventoryCheckTaskIDList = inventoryCheckTaskIDList;
	}

	public static class CodeBean {
		private String InventoryCheckTaskID;

		public String getInventoryCheckTaskID() {
			return InventoryCheckTaskID;
		}

		public void setInventoryCheckTaskID(String inventoryCheckTaskID) {
			InventoryCheckTaskID = inventoryCheckTaskID;
		}

	}

}
