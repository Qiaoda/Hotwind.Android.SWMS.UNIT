package cn.jitmarketing.hot.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.SKUItem;

import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrNet;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class GetOutStockListService extends Service implements ExNetIble{

	private static final String TAG = "GetOutStockListService"; 
	private IBinder binder=new GetOutStockListService.MyBinder();
	private static final String INTENT_JIT_OUTSTOCK_SERVICE = "cn.jitmarketing.hot.GetOutStockListService";
	private List<SKUItem> outStockList = new ArrayList<SKUItem>();
	private Activity mActivity;

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	//定义内容类继承Binder
	public class MyBinder extends Binder implements IOutStockService{
		@Override
		public List<SKUItem> getOutStock() {
			new Thread(new Runnable() {

				@Override
				public void run() {
					System.out.println("~~begin");
					
				}
			}).start();

			return outStockList;
		}
	}
	

	/**启动服务*/
	public void startService(Activity mActivity) {
		this.mActivity = mActivity;
		Intent intent = new Intent(GetOutStockListService.INTENT_JIT_OUTSTOCK_SERVICE);
		mActivity.startService(intent);
	}
	

	@Override
	public void onError(int arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, String> onStart(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> onStartNetParam(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSuccess(int arg0, String arg1, boolean arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSuccess(int arg0, InputStream arg1,
			HashMap<String, String> arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	/**发送POST请求*/
	public static String sendHttpPost(String url, Map<String, String> params){

		HttpClient httpClient = new MgrNet().newInstance();
		HttpPost httpPost = new HttpPost(url);
		HttpResponse response;
		String result = null;

		try {
			if (params != null && !params.isEmpty()) {
				List<NameValuePair> paramList = new ArrayList<NameValuePair>(params.size());

				for (Map.Entry<String, String> entry : params.entrySet()) {
					paramList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
				}

				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(paramList, "UTF-8");
				httpPost.setEntity(entity);
			}

			response = httpClient.execute(httpPost);
			InputStream inputStream = response.getEntity().getContent();
			result = Ex.T().getInStream2Str(inputStream);
			
			Log.e("result-->", result);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}

}
