package cn.jitmarketing.hot.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CGYBean implements Serializable{
        public String ShelfLocationCode;
        public String ChecktaskShelflocationID;
        public String status;
        public String CheckTime;
        public String CheckQty;
        public String TheoryQty;
        public ArrayList<SkuBean> SKUList;
}
