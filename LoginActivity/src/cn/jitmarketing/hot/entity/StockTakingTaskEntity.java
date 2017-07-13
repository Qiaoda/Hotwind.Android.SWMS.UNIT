package cn.jitmarketing.hot.entity;

import java.io.Serializable;

public class StockTakingTaskEntity implements Serializable {
	
	public String CheckTaskID;
	public String CheckTaskName;
	public String ConfirmTaskTime;
	public int Status;
	public String ResultStatus;
	public String ResultStatusString;
}