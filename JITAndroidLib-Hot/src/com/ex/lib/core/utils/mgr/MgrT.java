/*      
 * Copyright (c) 2014 by EagleXad
 * Team: EagleXad 
 * Create: 2014-08-29
 */

package com.ex.lib.core.utils.mgr;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @ClassName: MgrT
 * @Description: MgrT 管理类
 * @author Aloneter
 * @date 2014-8-26 下午4:06:25
 * @Version 1.0
 * 
 */
public class MgrT {

	/**
	 * 创建者
	 */
	private static class tHolder {

		private static final MgrT mgr = new MgrT();
	}

	/**
	 * 获取当前实例对象
	 * 
	 * @return
	 */
	public static MgrT getInstance() {

		return tHolder.mgr;
	}

	/**
	 * 流转换字符串 (默认字符集)
	 * 
	 * @param in
	 * @return
	 */
	public String getInStream2Str(InputStream in) {

		return getInStream2Str(in, Charset.defaultCharset());
	}

	/**
	 * 流转换字符串 (字符串处理字符集)
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 */
	public String getInStream2Str(InputStream in, String encoding) {

		return getInStream2Str(in, Charset.forName(encoding));
	}

	/**
	 * 流转换字符串 (处理字符集)
	 * 
	 * @param in
	 * @param encoding
	 * @return
	 */
	public String getInStream2Str(InputStream in, Charset encoding) {

		InputStreamReader input = new InputStreamReader(in, encoding);
		StringWriter output = new StringWriter();

		try {
			char[] buffer = new char[4096];
			int n = 0;
			while (-1 != (n = input.read(buffer))) {
				output.write(buffer, 0, n);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output.toString();
	}

	/**
	 * Bitmap 转换字节流
	 * 
	 * @param bm
	 * @return
	 */
	public byte[] getBitmap2Bytes(Bitmap bm) {

		if (bm == null) {

			return null;
		}

		byte[] result = null;
		ByteArrayOutputStream baos = null;

		try {
			baos = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 100, baos);

			result = baos.toByteArray();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			try {
				if (baos != null) {
					baos.flush();
					baos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * 字节流转换 Bitmap
	 * 
	 * @param b
	 * @return
	 */
	public Bitmap getBytes2Bitmap(byte[] b) {

		if (b.length == 0) {

			return null;
		}

		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}

	/**
	 * Drawable 转换 Bitmap
	 * 
	 * @param d
	 * @return
	 */
	public Bitmap getDrawable2Bitmap(Drawable d) {

		if (d == null) {

			return null;
		}

		int w = d.getIntrinsicWidth();
		int h = d.getIntrinsicHeight();

		Bitmap.Config config = d.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;

		Bitmap b = Bitmap.createBitmap(w, h, config);

		Canvas canvas = new Canvas(b);

		d.setBounds(0, 0, w, h);
		d.draw(canvas);

		return b;
	}

	/**
	 * Bitmap 转换 Drawable
	 * 
	 * @param bm
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public Drawable getBitmap2Drawable(Bitmap bm) {

		if (bm == null) {

			return null;
		}

		BitmapDrawable bd = new BitmapDrawable(bm);

		bd.setTargetDensity(bm.getDensity());

		return new BitmapDrawable(bm);
	}

	/**
	 * @param htmlStr
	 * @return
	 */
	public String getHtml2Text(String htmlStr) {

		String result = "";

		Pattern p_script;
		Matcher m_script;
		Pattern p_style;
		Matcher m_style;
		Pattern p_html;
		Matcher m_html;

		String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
		String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";
		String regEx_html = "<[^>]+>";

		try {
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll("");

			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll("");

			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(regEx_html);
			htmlStr = m_html.replaceAll("");

			result = htmlStr;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * 对象转换 Byte
	 * 
	 * @param obj
	 * @return
	 */
	public byte[] getObject2Byte(Object obj) {

		byte[] bytes = null;
		ByteArrayOutputStream bos = null;
		ObjectOutputStream oos = null;

		try {
			bos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(bos);

			oos.writeObject(obj);

			oos.flush();

			bytes = bos.toByteArray();

			oos.close();
			bos.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return bytes;
	}

	/**
	 * Byte 转换 对象
	 * 
	 * @param bytes
	 * @return
	 */
	public Object getByte2Object(byte[] bytes) {

		Object obj = null;
		ByteArrayInputStream bis = null;
		ObjectInputStream ois = null;

		try {
			bis = new ByteArrayInputStream(bytes);
			ois = new ObjectInputStream(bis);

			obj = ois.readObject();

			ois.close();
			bis.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return obj;
	}

	/**
	 * 字符串转换 Int
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public int getString2Int(String str, int defValue) {

		try {
			return Integer.parseInt(str);
		} catch (Exception e) {
		}

		return defValue;
	}

	/**
	 * 字符串转换 long
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public long getString2Long(String str, long defValue) {

		try {
			return Long.parseLong(str);
		} catch (Exception e) {
		}

		return defValue;
	}

	/**
	 * 字符串转换 double
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public double getString2Double(String str, double defValue) {

		try {
			return Double.parseDouble(str);
		} catch (Exception e) {
		}

		return defValue;
	}

	/**
	 * 字符串转换 float
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public float getString2Float(String str, float defValue) {

		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
		}

		return defValue;
	}

	/**
	 * 字符串转换 bool
	 * 
	 * @param str
	 * @param defValue
	 * @return
	 */
	public boolean getString2Bool(String str, boolean defValue) {

		try {
			return Boolean.parseBoolean(str);
		} catch (Exception e) {
		}

		return defValue;
	}

	/**
	 * Dip 转换 Px
	 * 
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public int getDip2Px(Context context, float dpValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * Px 转换 Dip
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public int getPx2Dip(Context context, float pxValue) {

		final float scale = context.getResources().getDisplayMetrics().density;

		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * Px 转换 Sp
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public int getPx2Sp(Context context, float pxValue) {

		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (pxValue / fontScale + 0.5f);
	}

	/**
	 * Sp 转换 Px
	 * 
	 * @param context
	 * @param spValue
	 * @return
	 */
	public int getSp2Px(Context context, float spValue) {

		float fontScale = context.getResources().getDisplayMetrics().scaledDensity;

		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * @param array
	 * @param cls
	 * @return
	 */
	public <T> List<T> getString2List(String array, Class<T> cls) {

		List<T> list = new ArrayList<T>();

		try {
			JSONArray jsonArray = new JSONArray(array);

			for (int i = 0; i < jsonArray.length(); i++) {
				T t = getString2Cls(jsonArray.get(i).toString(), cls);
				list.add(t);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return list;
	}

	/**
	 * @param bean
	 * @param cls
	 * @return
	 */
	public <T> T getString2Cls(String bean, Class<T> cls) {

		Gson gson = new Gson();

		return gson.fromJson(bean, cls);
	}

	/**
	 * @param t
	 * @return
	 */
	public <T> String getCls2String(T t) {

		Gson gson = new Gson();

		return gson.toJson(t);
	}

	/**
	 * @param stream
	 * @param cls
	 * @return
	 */
	public <T> T getStream2Cls(InputStream stream, Class<T> cls) {

		Gson gson = new Gson();

		return gson.fromJson(new InputStreamReader(stream), cls);
	}

	/**
	 * @param stream
	 * @param cls
	 * @return
	 */
	public <T> List<T> getStream2List(InputStream stream, Class<T> cls) {

		Gson gson = new Gson();
		List<T> list = gson.fromJson(new InputStreamReader(stream), new TypeToken<List<T>>() {
		}.getType());

		return list;
	}

	/**
	 * 图片反转
	 * 
	 * @param img
	 * @return
	 */
	public Bitmap getBitmap2Turn(Bitmap img) {

		Matrix matrix = new Matrix();
		matrix.postRotate(90);

		int width = img.getWidth();
		int height = img.getHeight();

		return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}

	public Bitmap getBitmap2Turn(Bitmap img, float rote) {

		Matrix matrix = new Matrix();
		matrix.postRotate(rote);

		int width = img.getWidth();
		int height = img.getHeight();

		return Bitmap.createBitmap(img, 0, 0, width, height, matrix, true);
	}

	/**
	 * 图片缩放
	 * 
	 * @param bigimage
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public Bitmap getBitmap2change(Bitmap bigimage, int newWidth, int newHeight) {

		// 获取这个图片的宽和高
		int width = bigimage.getWidth();
		int height = bigimage.getHeight();

		// 创建操作图片用的matrix对象
		Matrix matrix = new Matrix();

		// 计算缩放率，新尺寸除原始尺寸
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;

		// 缩放图片动作
		matrix.postScale(scaleWidth, scaleHeight);

		return Bitmap.createBitmap(bigimage, 0, 0, width, height, matrix, true);
	}

	/**
	 * 程序切割图片
	 * 
	 * @param bitmap
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @return
	 */
	public Bitmap getBitmap2Clip(Bitmap bitmap, int x, int y, int w, int h) {

		return Bitmap.createBitmap(bitmap, x, y, w, h);
	}

	/**
	 * create the bitmap from a byte array 生成水印图片
	 * 
	 * @param src
	 * @param watermark
	 * @return
	 */
	public Bitmap getBitmap2WaterMark(Bitmap src, Bitmap watermark) {

		if (src == null) {

			return null;
		}

		int w = src.getWidth();
		int h = src.getHeight();
		int ww = watermark.getWidth();
		int wh = watermark.getHeight();

		// create the new blank bitmap
		Bitmap newb = Bitmap.createBitmap(w, h, Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

		Canvas cv = new Canvas(newb);
		// draw src into
		cv.drawBitmap(src, 0, 0, null);// 在 0，0坐标开始画入src
		// draw watermark into
		cv.drawBitmap(watermark, w - ww + 5, h - wh + 5, null);// 在src的右下角画入水印
		// save all clip
		cv.save(Canvas.ALL_SAVE_FLAG);// 保存
		// store
		cv.restore();// 存储

		return newb;
	}

	/**
	 * 圆角处理 实际上是在原图片上画了一个圆角遮罩。对于paint.setXfermode(new
	 * PorterDuffXfermode(Mode.SRC_IN)); 方法我刚看到也是一知半解Mode.SRC_IN参数是个画图模式，该类型是指只显
	 * 示两层图案的交集部分，且交集部位只显示上层图像。 实际就是先画了一个圆角矩形的过滤框，于是形状有了，再将框中的内容填充为图片
	 * 
	 * @param bitmap
	 * @param roundPx
	 * @return
	 */
	public Bitmap getBitmap2RoundedCorner(Bitmap bitmap, float roundPx) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	/**
	 * 灰白处理 就是利用了ColorMatrix 类自带的设置饱和度的方法setSaturation()。
	 * 不过其方法内部实现的更深一层是利用颜色矩阵的乘法实现的，对于颜色矩阵的乘法下面还有使用
	 * 
	 * @param bmpOriginal
	 * @return
	 */
	public Bitmap getBitmap2Grayscale(Bitmap bmpOriginal) {

		int height = bmpOriginal.getHeight();
		int width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();

		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);

		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);

		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);

		return bmpGrayscale;
	}

	/**
	 * 黑白处理 这张图片不同于灰白处理的那张，不同之处是灰白处理虽然没有了颜色，
	 * 但是黑白的程度层次依然存在，而此张图片连层次都没有了，只有两个区别十分明显的黑白 颜色。
	 * 实现的算法也很简单，对于每个像素的rgb值求平均数，如果高于100算白色，低于100算黑色。 不过感觉100这个标准值太大了，导致图片白色区
	 * 域太多，把它降低点可能效果会更好
	 * 
	 * @param mBitmap
	 * @return
	 */
	public Bitmap getBitmap2BlackAndwhite(Bitmap mBitmap) {

		int mBitmapWidth = 0;
		int mBitmapHeight = 0;

		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
		Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.ARGB_8888);

		int iPixel = 0;
		for (int i = 0; i < mBitmapWidth; i++) {
			for (int j = 0; j < mBitmapHeight; j++) {
				int curr_color = mBitmap.getPixel(i, j);

				int avg = (Color.red(curr_color) + Color.green(curr_color) + Color.blue(curr_color)) / 3;
				if (avg >= 100) {
					iPixel = 255;
				} else {
					iPixel = 0;
				}
				int modif_color = Color.argb(255, iPixel, iPixel, iPixel);

				bmpReturn.setPixel(i, j, modif_color);
			}
		}

		return bmpReturn;
	}

	/**
	 * 镜像处理 原理就是将原图片反转一下，调整一 下它的颜色作出倒影效果，再将两张图片续加在一起，
	 * 不过如果在反转的同时再利用Matrix加上一些倾斜角度就更好了，不过那样做的话加工后的图片的高度需要同比例计算出来，
	 * 不能简单的相加了，否则就图片大小就容不下现有的像素内容。
	 * 
	 * @param bitmap
	 * @return
	 */
	public Bitmap getBitmap2ReflectionImageWithOrigin(Bitmap bitmap) {

		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2, width, height / 2, matrix, false);
		Bitmap bitmapWithReflection = Bitmap.createBitmap(width, (height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0, bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff, 0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight() + reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 加旧处理
	 * 
	 * @param bitmap
	 * @return
	 */
	public Bitmap getBitmap2OldBitmap(Bitmap bitmap) {

		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.RGB_565);

		Canvas canvas = new Canvas(output);

		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		float[] array = { 1, 0, 0, 0, 50, 0, 1, 0, 0, 50, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0 };
		cm.set(array);
		paint.setColorFilter(new ColorMatrixColorFilter(cm));

		canvas.drawBitmap(bitmap, 0, 0, paint);

		return output;
	}

	/**
	 * 　　浮雕处理 观察浮雕就不难发现，其实浮雕的特点就是在颜色有跳变的地方就刻条痕迹。127，127,127为深灰色，
	 * 近似于石头的颜色，此处取该颜色为底色。算法是将上一个点的rgba值减去当前点的rgba值然后加上127得到当前点的颜色。
	 * 
	 * @param mBitmap
	 * @return
	 */
	public Bitmap getBitmap2Embossment(Bitmap mBitmap) {

		int mBitmapWidth = 0;
		int mBitmapHeight = 0;

		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();

		Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight, Bitmap.Config.RGB_565);
		int preColor = 0;
		int prepreColor = 0;

		preColor = mBitmap.getPixel(0, 0);

		for (int i = 0; i < mBitmapWidth; i++) {
			for (int j = 0; j < mBitmapHeight; j++) {
				int curr_color = mBitmap.getPixel(i, j);
				int r = Color.red(curr_color) - Color.red(prepreColor) + 127;
				int g = Color.green(curr_color) - Color.red(prepreColor) + 127;
				int b = Color.green(curr_color) - Color.blue(prepreColor) + 127;
				int a = Color.alpha(curr_color);
				int modif_color = Color.argb(a, r, g, b);
				bmpReturn.setPixel(i, j, modif_color);
				prepreColor = preColor;
				preColor = curr_color;
			}
		}

		Canvas c = new Canvas(bmpReturn);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpReturn, 0, 0, paint);

		return bmpReturn;
	}

	/**
	 * 　　油画处理 其实油画因为是用画笔画的，彩笔画的时候没有那么精确会将本该这点的颜色滑到另一个点处。
	 * 算法实现就是取一个一定范围内的随机数，每个点的颜色是该点减去随机数坐标后所得坐标的颜色。
	 * 
	 * @param bmpSource
	 * @return
	 */
	public Bitmap getBitmap2OilPainting(Bitmap bmpSource) {

		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(), bmpSource.getHeight(), Bitmap.Config.RGB_565);
		int color = 0;
		int Radio = 0;
		int width = bmpSource.getWidth();
		int height = bmpSource.getHeight();

		Random rnd = new Random();
		int iModel = 10;
		int i = width - iModel;
		while (i > 1) {
			int j = height - iModel;
			while (j > 1) {
				int iPos = rnd.nextInt(100000) % iModel;
				color = bmpSource.getPixel(i + iPos, j + iPos);
				bmpReturn.setPixel(i, j, color);
				j = j - 1;
			}
			i = i - 1;
		}

		return bmpReturn;
	}

	/**
	 * 　　模糊处理 算法实现其实是取每三点的平均值做为当前点颜色，这样看上去就变得模糊了。
	 * 这个算法是三点的平均值，如果能够将范围扩大，并且不是单纯的平均值， 而是加权
	 * 平均肯定效果会更好。不过处理速度实在是太慢了，而Muzei这种软件在处理的时候 ，不仅仅速度特别快，而且还有逐渐变模糊的变化过程，显然人家不是用这
	 * 种算法实现的。 他们的实现方法正在猜测中，实现后也来更新。
	 * 
	 * @param bmpSource
	 * @param Blur
	 * @return
	 */
	public Bitmap getBitmap2Blur(Bitmap bmpSource, int Blur) {

		int mode = 5;

		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(), bmpSource.getHeight(), Bitmap.Config.ARGB_8888);
		int pixels[] = new int[bmpSource.getWidth() * bmpSource.getHeight()];
		int pixelsRawSource[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3];
		int pixelsRawNew[] = new int[bmpSource.getWidth() * bmpSource.getHeight() * 3];

		bmpSource.getPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		for (int k = 1; k <= Blur; k++) {

			for (int i = 0; i < pixels.length; i++) {
				pixelsRawSource[i * 3 + 0] = Color.red(pixels[i]);
				pixelsRawSource[i * 3 + 1] = Color.green(pixels[i]);
				pixelsRawSource[i * 3 + 2] = Color.blue(pixels[i]);
			}

			int CurrentPixel = bmpSource.getWidth() * 3 + 3;

			for (int i = 0; i < bmpSource.getHeight() - 3; i++) {
				for (int j = 0; j < bmpSource.getWidth() * 3; j++) {
					CurrentPixel += 1;
					int sumColor = 0;
					sumColor = pixelsRawSource[CurrentPixel - bmpSource.getWidth() * 3];
					sumColor = sumColor + pixelsRawSource[CurrentPixel - 3];
					sumColor = sumColor + pixelsRawSource[CurrentPixel + 3];
					sumColor = sumColor + pixelsRawSource[CurrentPixel + bmpSource.getWidth() * 3];
					pixelsRawNew[CurrentPixel] = Math.round(sumColor / 4);
				}
			}

			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = Color.rgb(pixelsRawNew[i * 3 + 0], pixelsRawNew[i * 3 + 1], pixelsRawNew[i * 3 + 2]);
			}
		}

		bmpReturn.setPixels(pixels, 0, bmpSource.getWidth(), 0, 0, bmpSource.getWidth(), bmpSource.getHeight());

		return bmpReturn;
	}

	/**
	 * 图片合并
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @param path
	 * @return
	 * @throws FileNotFoundException
	 */
	public Bitmap getBitmap2Join(Bitmap bitmap1, Bitmap bitmap2, String path) throws FileNotFoundException {

		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2, 120, 350, null); // 120、350为bitmap2写入点的x、y坐标

		// 将合并后的bitmap3保存为png图片到本地
		FileOutputStream out = new FileOutputStream(path + "/image3.png");
		bitmap3.compress(Bitmap.CompressFormat.PNG, 90, out);

		return bitmap3;
	}

	/**
	 * 文字保存为png图片
	 * 
	 * @param path
	 *            文件保存路径
	 * @param data
	 *            保存数据
	 * @throws FileNotFoundException
	 * */
	public void getText2Image(String path, ArrayList<String> data) throws FileNotFoundException {

		int height = data.size() * 20; // 图片高
		Bitmap bitmap = Bitmap.createBitmap(270, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawColor(Color.WHITE); // 背景颜色

		Paint p = new Paint();
		p.setColor(Color.BLACK); // 画笔颜色
		p.setTextSize(15); // 画笔粗细

		for (int i = 0; i < data.size(); i++) {
			canvas.drawText(data.get(i), 20, (i + 1) * 20, p);
		}

		FileOutputStream out = new FileOutputStream(path);
		bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	}

	/**
	 * Bitmap to Drawable
	 * 
	 * @param bitmap
	 * @param mcontext
	 * @return
	 */
	public Drawable getBitmap2Drawble(Bitmap bitmap, Context mcontext) {
		
		Drawable drawable = new BitmapDrawable(mcontext.getResources(), bitmap);
		
		return drawable;
	}

}
