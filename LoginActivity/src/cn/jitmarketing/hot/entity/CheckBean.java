package cn.jitmarketing.hot.entity;

public class CheckBean {
  public String sku;
  public float shouldCount;
  public float reallyCount;
public CheckBean(String sku, float shouldCount, float reallyCount) {
	super();
	this.sku = sku;
	this.shouldCount = shouldCount;
	this.reallyCount = reallyCount;
}
public CheckBean() {
	super();

}
}
