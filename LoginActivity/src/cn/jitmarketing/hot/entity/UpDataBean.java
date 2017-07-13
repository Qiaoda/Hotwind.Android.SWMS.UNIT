package cn.jitmarketing.hot.entity;

import android.R.integer;

public class UpDataBean {
	
	private int progressNum;
	
	private String progres;
	
	private boolean isFailed;
	
	private boolean isFinish;

	public UpDataBean(int progressNum, String progres, boolean isFailed,
			boolean isFinish) {
		super();
		this.progressNum = progressNum;
		this.progres = progres;
		this.isFailed = isFailed;
		this.isFinish = isFinish;
	}

	public int getProgressNum() {
		return progressNum;
	}

	public void setProgressNum(int progressNum) {
		this.progressNum = progressNum;
	}

	public String getProgres() {
		return progres;
	}

	public void setProgres(String progres) {
		this.progres = progres;
	}

	public boolean isFailed() {
		return isFailed;
	}

	public void setFailed(boolean isFailed) {
		this.isFailed = isFailed;
	}

	public boolean isFinish() {
		return isFinish;
	}

	public void setFinish(boolean isFinish) {
		this.isFinish = isFinish;
	}
	
	

	
	

}
