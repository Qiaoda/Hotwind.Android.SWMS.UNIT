package cn.jitmarketing.hot.entity;

import com.google.gson.annotations.SerializedName;

public class ResultNet<T> {

	@SerializedName(value = "Message")
	public String message;
	@SerializedName(value = "Success")
	public boolean isSuccess;
	@SerializedName(value = "Data")
	public T data;
}
