package cn.jitmarketing.hot;

import java.util.HashMap;


public class HotBaseNet {

	protected HashMap<String, String> generateParam(String action, HashMap<String, String> map) {

//		Gson gson = new Gson();
//
//		HashMap<String, String> param = new HashMap<String, String>();
//		param.put("Token", ""); // 公共参数。访问凭据，预留，当前可为空
//		param.put("UserID", "09697F65-71B6-4CC5-903D-576EBB7329B6"); // 公共参数。会员ID.
//		param.put("CustomerID", GJConstants.Global.CUSTOMER_ID); // 参数访客编号
//
//		if (map != null && map.size() > 0) {
//			param.put("Parameters", gson.toJson(map)); // 接口参数。具体定义见接口中的参数描述
//		}else{
//			param.put("Parameters", "{}"); 
//		}
//
		HashMap<String, String> params = new HashMap<String, String>();
//		params.put("type", GJConstants.Global.TYPE_PRODUCT);
//		params.put("Action", action); // 接口请求操作.指定了本次接口调用所调用的请求操作.
//		params.put("channel", GJConstants.Global.CHANNEL); // 调用渠道。分自有平台和第三方平台。0：系统自有调用；1：第三方调用
//		params.put("client", GJConstants.Global.CLIENT); // 客户端。0：服务端调用；1：web调用；2：App调用；3：wap调用
//		params.put("req", gson.toJson(param)); // 请求参数。请求参数分为公共参数和接口参数。他包含了接口调用时，前端所提供的所有数据,内容值必须进行URL Encoding。

		return params;
	}

}
