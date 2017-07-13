package cn.jitmarketing.hot.service;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UploadLogService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		new Thread() {
			public void run() {

			};
		}.start();
		return super.onStartCommand(intent, flags, startId);
	}

	private void upLog() {
		try {
			AsyncHttpClient ahClient = new AsyncHttpClient();

			RequestParams params = new RequestParams();

			ahClient.post("URL", params, new AsyncHttpResponseHandler() {

				@Override
				public void onSuccess(int statusCode, Header[] arg1, byte[] arg2) {
					// TODO Auto-generated method stub
					if (statusCode == 200) {

					}
				}

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
					// TODO Auto-generated method stub

				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
