package cn.jitmarketing.hot.service;

import java.io.Serializable;

public class NoticeSettingEntity implements Serializable {
	
	private String key;
	private boolean isOpen;
	private String name;
	private int parentID;
	private int shaixuanId;//拿货页面用的  前端字段
	
	public int getShaixuanId() {
		return shaixuanId;
	}

	public void setShaixuanId(int shaixuanId) {
		this.shaixuanId = shaixuanId;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean getOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public NoticeSettingEntity() {
	}

	public NoticeSettingEntity(String key, String name, int parentID, boolean isOpen) {
		this.key = key;
		this.isOpen = isOpen;
		this.name = name;
		this.parentID = parentID;
	}
	public NoticeSettingEntity(String key, String name, int parentID, boolean isOpen, int shaixuanId) {
		this.key = key;
		this.isOpen = isOpen;
		this.name = name;
		this.parentID = parentID;
		this.shaixuanId = shaixuanId;
	}

	public int getParentID() {
		return parentID;
	}

	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
}
