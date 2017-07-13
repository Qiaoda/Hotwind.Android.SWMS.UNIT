package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class RandomCheckHistoryBean implements Serializable{

	private String randomCode;
	private String randomTime;
	public String getRandomCode() {
		return randomCode;
	}
	public void setRandomCode(String randomCode) {
		this.randomCode = randomCode;
	}
	public String getRandomTime() {
		return randomTime;
	}
	public void setRandomTime(String randomTime) {
		this.randomTime = randomTime;
	}
	
	
}
