package cn.jitmarketing.hot;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ex.lib.core.utils.Ex;
import com.ex.lib.core.utils.mgr.MgrPerference;
import com.ex.lib.ext.xutils.annotation.ViewInject;
import com.example.scandemo.BaseOperationActivity;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.jitmarketing.hot.HotConstants.Global;
import cn.jitmarketing.hot.entity.MenuBean;
import cn.jitmarketing.hot.entity.ResultNet;
import cn.jitmarketing.hot.entity.UnitBean;
import cn.jitmarketing.hot.entity.UserBean;
import cn.jitmarketing.hot.net.UserNet;
import cn.jitmarketing.hot.service.GroupEntity;
import cn.jitmarketing.hot.service.NoticeSettingEntity;
import cn.jitmarketing.hot.service.UpdateService;
import cn.jitmarketing.hot.util.ConstValue;
import cn.jitmarketing.hot.util.FastDoubleClickUtil;
import cn.jitmarketing.hot.util.LogUtils;
import cn.jitmarketing.hot.view.ClearEditText;
import cn.jitmarketing.hot.view.IPCustomDialog;
import cn.jitmarketing.hot.view.IPListCustomDialog;

public class LoginActivity extends BaseOperationActivity {  

	/**
	 * 用户名
	 */
	@ViewInject(R.id.login_username)
	ClearEditText login_username;
	/**
	 * 密码
	 */
	@ViewInject(R.id.login_password)
	ClearEditText login_password;
	/**
	 * 登陆按钮
	 */
	@ViewInject(R.id.login_btn)
	Button login_btn;
	/**
	 * 添加ip地址按钮
	 */
	@ViewInject(R.id.login_add_btn)
	Button loginAddButton;
	/**
	 * 显示的常用地址
	 */
	@ViewInject(R.id.login_ip_txt)
	public TextView loginTextView;
	/**
	 * 常用地址的布局
	 */
	@ViewInject(R.id.login_ip_layout)
	LinearLayout loginLayout;
	/**
	 * 保存在shareprefence中的ip字符串的key
	 */
	public static final String IP_STR = "ipStr";
	/**
	 * 保存在shareprefence中的常用ip字符串的key
	 */
	public static final String COMMON_IP = "COMMON_IP";
	public static final String COMMON_IP_NAME = "COMMON_IP_NAME";
	public static final String SPLIT_MARK = "AAPODADDF";
	public List<String> ipList = new ArrayList<String>();
	private String username;
	private String password;
	public static final int WHAT_NET_LOGIN = 0x10;

	@Override
	protected int exInitLayout() {
		return R.layout.activity_login1;
	}

	@Override
	protected void exInitBundle() {
		//初始化操作接口
		initIble(this, this);
	}

	@Override
	protected void exInitView() {
		super.exInitView();
		//获取手机高度、宽度、分辨率
		getMobileValue();
		//获取本地存储的ip集合
		getIPList();
		//获取用户登陆信息，如果之前登陆保存过，则直接做登陆请求
		getUserInfo();
		//控件点击监听
		login_btn.setOnClickListener(clickListener);
		loginAddButton.setOnClickListener(clickListener);
		loginLayout.setOnClickListener(clickListener);
		//安装完成后自动删除安装文件
		File updateFile = new File(getFilesDir(), UpdateService.fileName);
		if(updateFile.exists()) {
			updateFile.delete();
			Toast.makeText(this, "安装文件已删除", Toast.LENGTH_LONG).show();
		}
	}
	//监听事件
	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if(v == login_btn) {
				if(login_username.getText().toString().trim().length() == 0){
					Ex.Toast(mActivity).show("请输入用户名");
					return;
				}
				if(login_password.getText().toString().trim().length() == 0){
					Ex.Toast(mActivity).show("请输入密码");
					return;
				}
				//登录操作
				login();

			} else if(v == loginAddButton) {//添加新的IP地址
				hideKeyborad();
				//避免双击操作
				if (FastDoubleClickUtil.isFastDoubleClick()) {
					return;
				}

				IPCustomDialog dialog = new IPCustomDialog(LoginActivity.this, new IPCustomDialog.OnCustomDialogListener() {
					@Override
					public void addIP(String name, String ip, String port, boolean isNormal) {
						saveIp(name, ip, port);
						ipList.add(new StringBuffer().append(ip).append(":").append(port).append(SPLIT_MARK).append(name).toString());
						if(isNormal) {
							MgrPerference.getInstance(LoginActivity.this).putString(COMMON_IP, ip + ":" + port);
							MgrPerference.getInstance(LoginActivity.this).putString(COMMON_IP_NAME, name);
							loginTextView.setText(name);
						}
					}
				});
				dialog.show();
			} else if(v == loginLayout) {//选择常用IP地址
				hideKeyborad();
				if (FastDoubleClickUtil.isFastDoubleClick()) {
					return;
				}
				IPListCustomDialog dialog = new IPListCustomDialog(LoginActivity.this, ipList, new IPListCustomDialog.OnCustomDialogListener() {

					@Override
					public void selectIP(String ipStr) {
						MgrPerference.getInstance(LoginActivity.this).putString(COMMON_IP, ipStr.split(SPLIT_MARK)[0]);
						MgrPerference.getInstance(LoginActivity.this).putString(COMMON_IP_NAME, ipStr.split(SPLIT_MARK)[1]);
						loginTextView.setText(ipStr.split(SPLIT_MARK)[1]);
					}
				});
				dialog.show();
			}
		}
	};

	/**
	 * 获取手机高度、宽度、分辨率
	 */
	private void getMobileValue() {
		DisplayMetrics dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		ConstValue.SCREEN_WIDTH = dm.widthPixels;//宽度;
		ConstValue.SCREEN_HEIGHT = dm.heightPixels ;//高度;
		ConstValue.SCREEN_DENSITY = dm.density;//屏幕密度
	}

	/**
	 * 获取用户登陆信息，如果之前登陆保存过，则直接做登陆请求
	 */
	private void getUserInfo() {
		String username = MgrPerference.getInstance(this).getString((HotConstants.User.SHARE_USERNAME));
		String password = MgrPerference.getInstance(this).getString((HotConstants.User.SHARE_PASSWORD));
		login_username.setText(username);
		login_username.setSelection(username.length());
		login_password.setText(password);
		login_password.setSelection(password.length());
		HotApplication.getInstance().setUserName(username);
		HotApplication.getInstance().setCustomerName(password);
		if(username.length() > 0 && password.length() > 0) {
			login();
		}
	}

	/**
	 * 获取本地存储的ip集合
	 */
	private void getIPList() {
		if(MgrPerference.getInstance(this).getString(COMMON_IP).equals("")) { //首次进入 常用地址设置为默认地址
			MgrPerference.getInstance(this).putString(COMMON_IP, Global.DEFAULT_IP);
			MgrPerference.getInstance(this).putString(COMMON_IP_NAME, "默认接入点");
		}
		ipList.clear();
		StringBuffer sb = new StringBuffer();//格式为 IP地址+MARK+name
		sb.append(Global.DEFAULT_IP);
		sb.append(SPLIT_MARK);
		sb.append("默认接入点");
		ipList.add(sb.toString());//默认地址
		String ipStr = MgrPerference.getInstance(this).getString(IP_STR);
		if(ipStr != null && "".equals(ipStr)) {

		} else {
			String[] ips = ipStr.split(";");
			for(int i=0; i<ips.length; i++) {
				ipList.add(ips[i]);
			}
		}
		loginTextView.setText(MgrPerference.getInstance(this).getString(COMMON_IP_NAME));
	}

	/**
	 * @param name 地址名称
	 * @param ip 地址
	 * @param port 端口
	 * 添加保存ip地址，保存在sharePerference，
	 * ip地址保存的格式例如：192.168.111.111
	 * 每次添加更新一次保存的字符串，
	 * 每个ip地址以分号分开，
	 *
	 */
	private void saveIp(String name, String ip, String port) {
		StringBuffer sb = new StringBuffer();
		if(!"".equals(MgrPerference.getInstance(LoginActivity.this).getString(IP_STR))) {
			sb.append(MgrPerference.getInstance(LoginActivity.this).getString(IP_STR));
			sb.append(";");
		}
		sb.append(ip);
		sb.append(":");
		sb.append(port);
		sb.append(SPLIT_MARK);
		sb.append(name);
		MgrPerference.getInstance(LoginActivity.this).putString(IP_STR, sb.toString());
	}

	/**
	 * 保存用户信息
	 */
	private void setUserInfo(UserBean user, UnitBean unit){
		HotApplication.getInstance().setCustomId(user.CustomerID);
		HotApplication.getInstance().setUserId(user.UserID);
		HotApplication.getInstance().setUserName(username);
		HotApplication.getInstance().setPassword(password);
		HotApplication.getInstance().setUserCode(user.UserCode);
		HotApplication.getInstance().setUnitId(unit.UnitID);
		HotApplication.getInstance().setUnitName(unit.UnitName);
		HotApplication.getInstance().setCityId(unit.CityID);
		HotApplication.getInstance().setAreaId(unit.AreaID);
		MgrPerference.getInstance(this).putString(HotConstants.Unit.UNIT_ID, unit.UnitID);
		MgrPerference.getInstance(this).putString(HotConstants.User.UNIT_NAME, unit.UnitName);
		MgrPerference.getInstance(this).putString(HotConstants.User.USER_CODE, user.UserCode);
		MgrPerference.getInstance(this).putString(HotConstants.User.SHARE_CUSTOMERID, user.CustomerID);
		MgrPerference.getInstance(this).putString(HotConstants.User.SHARE_USERID, user.UserID);
		MgrPerference.getInstance(this).putString(HotConstants.User.SHARE_USERNAME, username);
		MgrPerference.getInstance(this).putString(HotConstants.User.SHARE_PASSWORD, password);
	}

	/**
	 * 登陆请求
	 */
	private void login() {
		//拼接URL
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(MgrPerference.getInstance(LoginActivity.this).getString(COMMON_IP));
		sb.append("/");
		sb.append(HotConstants.User.LOGIN);
		//发送网络请求
		startTask(sb.toString(), WHAT_NET_LOGIN, NET_METHOD_POST, false);
		//保存IP
		HotConstants.Global.APP_URL_USER = "http://" + MgrPerference.getInstance(LoginActivity.this).getString(COMMON_IP) + "/";
	}

	@Override
	protected void exInitAfter() {

	}

	@Override
	protected String[] exInitReceiver() {
		return null;
	}

	@Override
	public void onError(int what, int e, String message) {
		switch (what) {
		case WHAT_NET_LOGIN:
			Ex.Toast(mContext).showLong("你的网速不太好，登录失败，请稍后重试");
			break;
		}
	}

	@Override
	public Map<String, String> onStart(int what) {
		switch (what) {
		case WHAT_NET_LOGIN:
			HotConstants.Global.APP_URL_USER = "http://" + MgrPerference.getInstance(LoginActivity.this).getString(COMMON_IP) + "/";
			username = login_username.getText().toString().trim();
			password = login_password.getText().toString().trim();
			//操作日志
			LogUtils.logOnFile("登录->用户："+username+";密码："+password);
			return UserNet.postLogin(username, password);
		}
		return null;
	}

	@Override
	public void onSuccess(int what, String result,boolean isCache) {
		ResultNet<?> net = Ex.T().getString2Cls(result, ResultNet.class);
		if (!net.isSuccess) {
			Ex.Toast(mContext).showLong(net.message);
			return;
		}
		switch (what) {
		case WHAT_NET_LOGIN:
			try {
				JSONObject tobj= new JSONObject(mGson.toJson(net.data));
				String userStr =tobj.getString("user");
				String menulistStr = tobj.getString("menulist");
				String roleCode= tobj.getString("roleCode");//角色
				String unitStr = tobj.getString("unit");
				boolean isSingle=tobj.getBoolean("IsSingleShelflocation");
				MgrPerference.getInstance(this).putString(HotConstants.Unit.ROLE_CODE, roleCode);
				initNoticeSetting(roleCode);
				UnitBean unitBean = mGson.fromJson(unitStr, UnitBean.class);
				UserBean user = mGson.fromJson(userStr, UserBean.class);
				ArrayList<MenuBean> menuList = mGson.fromJson(menulistStr,
		                new TypeToken<List<MenuBean>>() {}.getType());
				if (user != null) {
					if(isSingle){
						if (!roleCode.equals("DKW")) {
							Toast.makeText(mActivity, "门店为单库位系统，请使用单库位角色", Toast.LENGTH_LONG).show();
							return;
						}
					}
					Bundle bundle = new Bundle();
					bundle.putSerializable("menuList", menuList);
				    bundle.putString("roleCode",roleCode);
					Ex.Activity(mActivity).start(MainActivity.class, bundle);
					setUserInfo(user, unitBean);
					MgrPerference.getInstance(this).putString(HotConstants.User.ROLE_CODE, roleCode);
					mActivity.finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			break;
		}
	}

	private void initNoticeSetting(String role) {
		ArrayList<GroupEntity> groupList = (ArrayList<GroupEntity>)MgrPerference.getInstance(this).getObject(role);
		if(groupList == null)
			groupList = new ArrayList<GroupEntity>();
		if(groupList.size() == 0) {
			GroupEntity pandian = new GroupEntity();
			pandian.setId(1);
			pandian.setGroupName("盘点");
			NoticeSettingEntity checktask = new NoticeSettingEntity("checktask", "新的盘点任务", 1, true);
			NoticeSettingEntity checkopen = new NoticeSettingEntity("checkopen", "开始盘点提醒", 1, true);
			NoticeSettingEntity checkclose = new NoticeSettingEntity("checkclose", "盘点关闭提醒", 1, true);

			GroupEntity nahuo = new GroupEntity();
			nahuo.setId(2);
			nahuo.setGroupName("拿货");
			NoticeSettingEntity outgoods1 = new NoticeSettingEntity("outgoods.1", "鞋子", 2, true, 1);
			NoticeSettingEntity outgoods2 = new NoticeSettingEntity("outgoods.2", "服装", 2, true, 2);
			NoticeSettingEntity outgoods3 = new NoticeSettingEntity("outgoods.3", "小商品", 2, true, 3);
			NoticeSettingEntity outgoods4 = new NoticeSettingEntity("outgoods.4", "皮具", 2, true, 4);
			NoticeSettingEntity outgoods5 = new NoticeSettingEntity("outgoods.5", "样品", 2, true, 5);
			NoticeSettingEntity outgoods6 = new NoticeSettingEntity("outgoods.6", "辅料", 2, true, 6);
			NoticeSettingEntity outgoods7 = new NoticeSettingEntity("outgoods.7", "其他", 2, true, 7);

			GroupEntity fuwei = new GroupEntity();
			fuwei.setId(3);
			fuwei.setGroupName("待复位");
			NoticeSettingEntity reset = new NoticeSettingEntity("waiting.for.reset", "待复位商品超时提醒", 3, true);

			GroupEntity shenqing = new GroupEntity();
			shenqing.setId(4);
			shenqing.setGroupName("未找到");
			NoticeSettingEntity applay = new NoticeSettingEntity("notfound", "未找到商品", 4, true);

			if(role.equals("DZ")) {//店长
				checkopen.setOpen(false);
				checkclose.setOpen(false);
				outgoods1.setOpen(false);
				outgoods2.setOpen(false);
				outgoods3.setOpen(false);
				outgoods4.setOpen(false);
				outgoods5.setOpen(false);
				outgoods6.setOpen(false);
				outgoods7.setOpen(false);
				reset.setOpen(false);
				applay.setOpen(false);

				pandian.getSublist().add(checktask);
				pandian.getSublist().add(checkopen);
				pandian.getSublist().add(checkclose);

				nahuo.getSublist().add(outgoods1);
				nahuo.getSublist().add(outgoods2);
				nahuo.getSublist().add(outgoods3);
				nahuo.getSublist().add(outgoods4);
				nahuo.getSublist().add(outgoods5);
				nahuo.getSublist().add(outgoods6);
				nahuo.getSublist().add(outgoods7);

				fuwei.getSublist().add(reset);

				shenqing.getSublist().add(applay);

				groupList.add(pandian);
				groupList.add(nahuo);
				groupList.add(fuwei);
				groupList.add(shenqing);
			} else if(role.equals("CGY")) {//仓管员
				pandian.getSublist().add(checkopen);
				pandian.getSublist().add(checkclose);

				nahuo.getSublist().add(outgoods1);
				nahuo.getSublist().add(outgoods2);
				nahuo.getSublist().add(outgoods3);
				nahuo.getSublist().add(outgoods4);
				nahuo.getSublist().add(outgoods5);
				nahuo.getSublist().add(outgoods6);
				nahuo.getSublist().add(outgoods7);

				fuwei.getSublist().add(reset);

				groupList.add(pandian);
				groupList.add(nahuo);
				groupList.add(fuwei);
			} else if(role.equals("YYY")) {//营业员
				pandian.getSublist().add(checkopen);
				pandian.getSublist().add(checkclose);

				shenqing.getSublist().add(applay);

				groupList.add(pandian);
				groupList.add(shenqing);
			}
			MgrPerference.getInstance(this).putObject(role, groupList);
		}
	}

	@Override
	public void onReceiver(Intent arg0) {
		// TODO Auto-generated method stub

	}


	@Override
	public Map<String, Object> onStartNetParam(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void onSuccess(int arg0, InputStream arg1,
			HashMap<String, String> arg2, boolean arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void fillCode(String code) {
		// TODO Auto-generated method stub

	}

}
