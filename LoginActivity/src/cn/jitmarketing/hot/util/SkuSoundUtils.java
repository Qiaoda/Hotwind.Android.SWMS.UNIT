package cn.jitmarketing.hot.util;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;

public class SkuSoundUtils {
	
	 /**
     * 判断是库位码
     *
     * @param barcode
     * @return
     */
    public static int  isWarehouse(String barcode) {
        HotApplication hotApplication= HotApplication.getInstance();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
        java.util.regex.Matcher m = pattern.matcher(barcode.substring(0, 1));
        if (m.matches() && barcode.length() == 7 && !barcode.startsWith("E") && !barcode.startsWith("N") && !barcode.startsWith("T")){
            hotApplication.getsoundPool(hotApplication.Sound_location);
            return HotConstants.Global.STOCK_CODE;
        } else{
            hotApplication.getsoundPool(hotApplication.Sound_sku);
            return HotConstants.Global.SKU_CODE;
        }


    }

    /**
     * 判断是库位码
     *不发出声响
     * @param barcode
     * @return
     */
    public static int  isWarehouseN(String barcode) {
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("[a-zA-Z]+");
        java.util.regex.Matcher m = pattern.matcher(barcode.substring(0, 1));
        if (m.matches() && barcode.length() == 7 && !barcode.startsWith("E") && !barcode.startsWith("N") && !barcode.startsWith("T")){
            return HotConstants.Global.STOCK_CODE;
        } else{
            return HotConstants.Global.SKU_CODE;
        }


    }

}
