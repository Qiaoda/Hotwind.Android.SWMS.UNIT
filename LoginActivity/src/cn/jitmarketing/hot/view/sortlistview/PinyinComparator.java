package cn.jitmarketing.hot.view.sortlistview;

import java.util.Comparator;

import cn.jitmarketing.hot.entity.StockTakingWarehouseEntity;

/**
 * 
 * @author xiaanming
 *
 */
public class PinyinComparator implements Comparator<StockTakingWarehouseEntity> {

	public int compare(StockTakingWarehouseEntity o1, StockTakingWarehouseEntity o2) {
		if (o1.getSortLetters().equals("@")
				|| o2.getSortLetters().equals("#")) {
			return 1;
		} else if (o1.getSortLetters().equals("#")
				|| o2.getSortLetters().equals("@")) {
			return -1;
		} else {
			return o1.getSortLetters().compareTo(o2.getSortLetters());
		}
	}

}
