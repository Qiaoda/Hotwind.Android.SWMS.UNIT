package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class DifferentBean implements Serializable{
  public String ShelfLocationCode;
  public String ChecktaskShelflocationID;
  public String status;
  public String CheckTime;
  public float CheckQty;
  public float TheoryQty;
  public ArrayList<SkuBean> SKUList;
}
