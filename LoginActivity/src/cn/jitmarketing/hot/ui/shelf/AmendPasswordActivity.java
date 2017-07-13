package cn.jitmarketing.hot.ui.shelf;

import java.util.Map;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.jitmarketing.hot.BaseSwipeBackAcvitiy;
import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotConstants;
import cn.jitmarketing.hot.R;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.net.SkuNet;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.view.ClearEditText;

public class AmendPasswordActivity extends BaseSwipeBackAcvitiy implements
		OnClickListener {
	
	@ViewInject(R.id.reset_password_layout)
	LinearLayout reset_password_layout;
	@ViewInject(R.id.old_password)
	ClearEditText old_password;
	@ViewInject(R.id.new_password)
	ClearEditText new_password;
	@ViewInject(R.id.confir_password)
	ClearEditText confir_password;
	@ViewInject(R.id.amend_cacal)
	TextView amend_cacal;
	@ViewInject(R.id.amend_sure)
	TextView amend_sure;
	
	private static final int WHAT_NET_AMEND_OK = 0x10;
	private String newPasswrod;
	private String oldPassword;

	@Override
	protected void exInitAfter() {
		// TODO Auto-generated method stub
		super.exInitAfter();
	}

	@Override
	protected void exInitBundle() {
		// TODO Auto-generated method stub
		initIble(this, this);
	}

	@Override
	protected int exInitLayout() {
		// TODO Auto-generated method stub
		return R.layout.activity_amend_password;
	}

	@Override
	protected String[] exInitReceiver() {
		// TODO Auto-generated method stub
		return super.exInitReceiver();
	}

	@Override
	protected void exInitView() {
		oldPassword = MgrPerference.getInstance(this).getString(HotConstants.User.SHARE_PASSWORD);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int)(ConstValue.SCREEN_WIDTH*0.9),(int)(ConstValue.SCREEN_HEIGHT*0.4));
		reset_password_layout.setLayoutParams(params);
		amend_cacal.setOnClickListener(this);
		amend_sure.setOnClickListener(this);
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_AMEND_OK:
			return SkuNet.getAmendPass(newPasswrod, oldPassword);
		}
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_AMEND_OK:
			Ex.Toast(this).show("你的网络不太好，请稍后重试");
			break;
		}
	}
	
	@Override
	public void onSuccess(int what, String result, boolean hashCache) {
		switch (what) {
		case WHAT_NET_AMEND_OK:
			ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
			if (!net.isSuccess) {
				Ex.Toast(mContext).show(net.message);
				return;
			}else{
				MgrPerference.getInstance(this).putString(HotConstants.User.SHARE_PASSWORD, newPasswrod);
				oldPassword = MgrPerference.getInstance(this).getString(HotConstants.User.SHARE_PASSWORD);
			this.finish();
			}
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.amend_cacal:
			this.finish();
			break;
		case R.id.amend_sure:
			String opass = old_password.getText().toString();
			newPasswrod = new_password.getText().toString();
			String confirmPassword = confir_password.getText().toString();
			if(opass.equals("")){
				Ex.Toast(mContext).show("您输入原密码");
				return;
			}
			if(newPasswrod.equals("")){
				Ex.Toast(mContext).show("您输入新密码");
				return;
			}
			if(confirmPassword.equals("")){
				Ex.Toast(mContext).show("您确认新密码");
				return;
			}
			if(!oldPassword.equals(opass)) {
				Ex.Toast(mContext).show("原密码不正确");
				return;
			}
			if(!newPasswrod.equals(confirmPassword)){
				Ex.Toast(mContext).show("您前后输入的密码不一致");
				return;
			}
			if (newPasswrod.equals(confirmPassword)) {
				startTask(HotConstants.Global.APP_URL_USER
						+ HotConstants.Shelf.amendpass, WHAT_NET_AMEND_OK,
						NET_METHOD_POST, false);
			}
			break;
		}
	}
}
