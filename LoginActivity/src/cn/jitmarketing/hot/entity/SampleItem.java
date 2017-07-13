package cn.jitmarketing.hot.entity;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class SampleItem implements Serializable{
	private static final long serialVersionUID = 1L;
	@SerializedName(value = "")
	public String sampleId;
	@SerializedName(value = "")
	public String sampleName;
	@SerializedName(value = "")
	public String sampleTime;

	public SampleItem(String sampleId, String sampleName){
		this.sampleId = sampleId;
		this.sampleName = sampleName;
	}
	
	private void setTime(String sampleTime){
		this.sampleTime = sampleTime;
	}

	@Override
	public String toString() {
		return sampleName ;
	}

}
