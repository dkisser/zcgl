package org.lf.admin.service.wx.pay;

public class PayConfigUtil {

	public enum SignType {
		MD5, HMACSHA256
	}

	public static final String FAIL = "FAIL";
	public static final String SUCCESS = "SUCCESS";
	public static final String HMACSHA256 = "HMAC-SHA256";
	public static final String MD5 = "MD5";

	// 以下相关参数需要根据自己实际情况进行配置
	public static final String APP_ID = "ww342013a5f3df8c7f";// appid

	public static final String MCH_ID = "1487013572";// 商户号
	public static final String API_KEY = "4aac6057b0204b949521a92708c47809";// API

	public static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";// 统一下单接口
	public static final String ORDERQUERY_URL = "https://api.mch.weixin.qq.com/pay/orderquery";// 查询订单接口
	public static final String CLOSEORDER_URL = "https://api.mch.weixin.qq.com/pay/closeorder";// 关闭订单接口
	public static final String DOWNLOADBILL_URL = "https://api.mch.weixin.qq.com/pay/downloadbill";// 下载对账单接口

	public static final String NOTIFY_URL = "http://crow.natapp4.cc/zcgl/wx/pay/wxpay_notify.do";// 回调地址
}
