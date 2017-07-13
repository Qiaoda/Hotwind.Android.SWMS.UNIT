package cn.jitmarketing.hot.net;

import java.util.HashMap;
import java.util.Map;

import cn.jitmarketing.hot.HotApplication;
import cn.jitmarketing.hot.HotBaseNet;

public class UserNet extends HotBaseNet{

	/**登录*/
	public static HashMap<String, String> postLogin(String username, String password) {
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("UserAccount", username);
		map.put("UserPassword", password);
		map.put("CustomerID", "hotwind");
		return map;
	}

	/**
	 * 测试登陆
	 */
	public static HashMap<String,String> login(String username,String password){

		HashMap<String,String> map=new HashMap<String, String>();

		map.put("UserAccount",username);
		map.put("UserPassword", password);
		map.put("CustomerID", "hotwind");
		return map;
	}
}
