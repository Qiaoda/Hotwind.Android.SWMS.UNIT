package com.ex.lib.core.utils.mgr;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

/**
 * @ClassName: QuerMgr
 * @Description: 二维码管理类
 * @author Aloneter
 * @date 2014-10-31 上午10:57:21
 * @Version 1.0
 * 
 */
public class MgrQuer {

	private int mWidth = 400; // 宽度
	private int mHeight = 400; // 高度

	/**
	 * 创建者
	 */
	private static class QuerHolder {

		private static final MgrQuer mgr = new MgrQuer();
	}

	/**
	 * 获取当前实例
	 * 
	 * @return
	 */
	public static MgrQuer getInstance() {

		return QuerHolder.mgr;
	}

	/**
	 * Method_设置二维码宽度
	 * 
	 * @param width
	 */
	public void setQRWidth(int width) {

		mWidth = width;
	}

	/**
	 * Method_设置二维码高度
	 * 
	 * @param height
	 */
	public void setQRHeight(int height) {

		mHeight = height;
	}

	/**
	 * Method_创建二维码图片
	 * 
	 * @param str_二维码字符内容
	 * @param view_图片控件
	 */
	public void createQRImage(String str, ImageView view) {

		Bitmap bitmap = getQRBitmapByStr(str);

		if (bitmap != null) {
			// 显示到一个ImageView上面
			view.setImageBitmap(bitmap);
		}
	}

	/**
	 * Method_获取二维码 Bitmap
	 * 
	 * @param str_二维码字符内容
	 * @return 对象
	 */
	public Bitmap getQRBitmapByStr(String str) {

		try {
			if (str == null || "".equals(str) || str.length() < 1) {

				return null;
			}

			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

			// 图像数据转换，使用了矩阵转换
			BitMatrix bitMatrix = new QRCodeWriter().encode(str, BarcodeFormat.QR_CODE, mWidth, mHeight, hints);
			int[] pixels = new int[mWidth * mHeight];

			// 下面这里按照二维码的算法，逐个生成二维码的图片，
			// 两个for循环是图片横列扫描的结果
			for (int y = 0; y < mHeight; y++) {
				for (int x = 0; x < mWidth; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * mWidth + x] = 0xff000000;
					} else {
						pixels[y * mWidth + x] = 0xffffffff;
					}
				}
			}

			// 生成二维码图片的格式，使用ARGB_8888
			Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, mWidth, 0, 0, mWidth, mHeight);

			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();

			return null;
		}
	}
}
