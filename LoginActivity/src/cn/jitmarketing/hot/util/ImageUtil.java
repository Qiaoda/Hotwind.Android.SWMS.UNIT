package cn.jitmarketing.hot.util;

import cn.jitmarketing.hot.R;

import com.squareup.picasso.Picasso;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

public class ImageUtil {

	public static void getBitmapByBase64(String result, ImageView imageView){
//		//编码,记住这个流，经常用于图片和Base64数据的切换
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		//图片压缩并转换成流，bitmap在这我就不初始化了
//		bitmap.compress(Bitmap.compressFormat,100,baos);
//		byte[] byteServer = baos.toByteArray();
//		String result = Base64.encodeToString(byteServer.Base64.DEFAULT);
		//android端
		if(null == result) return;
		byte[] byteIcon = Base64.decode(result,Base64.DEFAULT);
		Bitmap bitmap = BitmapFactory.decodeByteArray(byteIcon,0,byteIcon.length);
		imageView.setImageBitmap(bitmap);
	}
	
	/**
	 * 下载图片
	 * @param context
	 * @param url
	 * @param view
	 */
	public static void uploadImage(Context context, String url, ImageView imageView) {
		//如果加载发生错误会重复三次请求，三次都失败才会显示erro Place holder
		Picasso.with(context)
	    .load(url)
	    .placeholder(R.drawable.icon)
	    .error(R.drawable.icon)
	    .into(imageView);
	}
	
	/**
	 * 下载图片
	 * @param context
	 * @param url
	 * @param view
	 */
	public static void uploadImage(Context context, String url, ImageView imageView, int width, int height) {
		//如果加载发生错误会重复三次请求，三次都失败才会显示erro Place holder
		Picasso.with(context)
	    .load(url)
	    .resize(width, height)
	    .centerCrop()
	    .placeholder(R.drawable.icon)
	    .error(R.drawable.icon)
	    .into(imageView);
	}
}
