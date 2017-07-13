package cn.jitmarketing.hot.service;

import java.io.Serializable;
import java.util.ArrayList;

public class GroupEntity implements Serializable {
	
	private String groupName;
	private int id;
	private ArrayList<NoticeSettingEntity> sublist = new ArrayList<NoticeSettingEntity>();
	
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public ArrayList<NoticeSettingEntity> getSublist() {
		return sublist;
	}
	public void setSublist(ArrayList<NoticeSettingEntity> sublist) {
		this.sublist = sublist;
	}


}
