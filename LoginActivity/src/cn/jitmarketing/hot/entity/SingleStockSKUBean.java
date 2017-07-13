package cn.jitmarketing.hot.entity;


public class SingleStockSKUBean {
	
	private String sku;
	
	private int num;
	
	private boolean flag;
	

	public SingleStockSKUBean(String sku, int num) {
		super();
		this.sku = sku;
		this.num = num;
		this.flag=true;
	}
	

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}


	public String getSku() {
		return sku;
	}

	public void setSku(String sku) {
		this.sku = sku;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	
	

}
