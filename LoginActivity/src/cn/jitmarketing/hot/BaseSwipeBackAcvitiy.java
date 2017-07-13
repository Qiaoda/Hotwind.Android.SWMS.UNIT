package cn.jitmarketing.hot;

import android.content.Intent;

import com.ex.lib.core.ExBaseSwipeBackAcvitiy;
import com.ex.lib.core.ible.ExNetIble;
import com.ex.lib.core.ible.ExReceiverIble;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;


public class BaseSwipeBackAcvitiy extends ExBaseSwipeBackAcvitiy implements ExNetIble, ExReceiverIble {

	private static final String LOCKHOME = "urovo.rcv.message";
	
	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void exInitView() {
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

}
