package cn.jitmarketing.hot;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.ex.lib.core.ExBaseAcvitiy;
import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.ible.ExReceiverIble;

public abstract class BaseActivity extends ExBaseAcvitiy implements ExNetIble, ExReceiverIble{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		
		
		
		return 0;
	}

	@Override
	protected void exInitView() {
	}
	
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub

	}
	
	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceiver(Intent intent) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onError(int what, int e, String message) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> onStart(int what) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> onStartNetParam(int what) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess(int what, InputStream result, HashMap<String, String> cookies, boolean hashCache) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 隐藏软键盘的方法
	 */
	protected void hideKeyborad() {
		try {
			// 隐藏软键盘
			if (null != this.getCurrentFocus()) {
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
						.hideSoftInputFromWindow(this.getCurrentFocus()
								.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
