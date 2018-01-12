package org.lf.admin.service.wx.pay;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.lf.admin.service.OperException;
import org.lf.admin.service.sys.WXAppService;
import org.lf.admin.service.wx.WXOAuthService;
import org.lf.utils.StringUtils;
import org.lf.utils.servlet.WXLoginInterceptor;
import org.lf.wx.utils.WXUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;




@Service("WXPayService")
public class WXPayService {

	@Autowired 
	WXAppService appService;
	
	@Autowired
	WXOAuthService wxoAuthService;
	
	static final Logger logger = Logger.getLogger(WXPayService.class);

	
	public SortedMap<String, String> publicPay(String userId, HttpServletRequest request) throws Exception {
		SortedMap<String, String> reqMap = new TreeMap<String, String>();
		// 公众账号ID
		reqMap.put("appid", PayConfigUtil.APP_ID);
		// 商户号
		reqMap.put("mch_id", PayConfigUtil.MCH_ID);
		// 随机数
		reqMap.put("nonce_str", StringUtils.getShortUUID());
		// 商品描述
		reqMap.put("body", "testtttttttttt");
		// 订单号
		reqMap.put("out_trade_no", PayCommonUtil.genOrderNO());
		// 总金额单位分
		reqMap.put("total_fee", "1");
		// ip
		reqMap.put("spbill_create_ip", PayCommonUtil.getIpAddr(request));
		// 通知地址
		reqMap.put("notify_url", PayConfigUtil.NOTIFY_URL);
		// 交易类型
		reqMap.put("trade_type", "JSAPI");
		// 商品ID
		// map.put("product_id", "1000000");

		String accessToken = appService.getAccessToken(17);

		reqMap.put("openid", PayCommonUtil.getOpenIdByUserId(getUser(request), accessToken));
//		reqMap.put("openid", PayCommonUtil.getOpenIdByUserId("fuzuo", accessToken));

		// 签名
		String sign = PayCommonUtil.createSign(reqMap, PayConfigUtil.API_KEY);
		reqMap.put("sign", sign);
		// 请求数据
		Map<String, String> tempMap = unifiedOrder(reqMap);

		// 向前端发送相应的数据
		String timestamp = String.valueOf(new Date().getTime() / 1000);
		SortedMap<String, String> res = new TreeMap<String, String>();
		res.put("appId", tempMap.get("appid"));
		res.put("timeStamp", timestamp);
		res.put("nonceStr", tempMap.get("nonce_str"));
		res.put("package", "prepay_id=" + tempMap.get("prepay_id"));
		res.put("signType", "MD5");
		res.put("paySign", PayCommonUtil.createSign(res, PayConfigUtil.API_KEY));
		return res;
	}

	/**
	 * 微信扫码支付 模式二
	 * 
	 * @return返回的就是二维中的值
	 * @throws Exception
	 */
	public String getWxPayQRCode() throws Exception {
		SortedMap<String, String> reqMap = new TreeMap<String, String>();
		// 公众账号ID
		reqMap.put("appid", PayConfigUtil.APP_ID);
		// 商户号
		reqMap.put("mch_id", PayConfigUtil.MCH_ID);
		// 随机数
		reqMap.put("nonce_str", StringUtils.getShortUUID());
		// 商品描述
		reqMap.put("body", "test");
		// 订单号
		reqMap.put("out_trade_no", PayCommonUtil.genOrderNO());
		// 总金额单位分
		reqMap.put("total_fee", "1");
		InetAddress ia = InetAddress.getLocalHost();
		// ip
		reqMap.put("spbill_create_ip", ia.getHostAddress());
		// 通知地址
		reqMap.put("notify_url", PayConfigUtil.NOTIFY_URL);
		// 交易类型
		reqMap.put("trade_type", "NATIVE");
		// 商品ID
		reqMap.put("product_id", "10000");
		// 签名
		String sign = PayCommonUtil.createSign(reqMap, PayConfigUtil.API_KEY);
		reqMap.put("sign", sign);
		// 统一下单
		Map<String, String> tempMap = unifiedOrder(reqMap);

		// 二维码内容
		String qrCode = "";
		if (tempMap.get("return_code").toString().equals("SUCCESS")) {
			qrCode = tempMap.get("code_url").toString();
		}
		return qrCode;
	}
	
	
	public void wxPayNotify(HttpServletRequest request, HttpServletResponse response) {
		// 读取参数
		InputStream inputStream;
		StringBuffer sb = new StringBuffer();
		try {
			inputStream = request.getInputStream();

			String s;
			BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			while ((s = in.readLine()) != null) {
				sb.append(s);
			}
			in.close();
			inputStream.close();

			// 解析xml成map
			Map<String, String> m = new HashMap<String, String>();
			m = PayCommonUtil.xmlToMap(sb.toString());

			// 过滤空 设置 TreeMap
			SortedMap<String, String> packageParams = new TreeMap<String, String>();
			Iterator it = m.keySet().iterator();
			while (it.hasNext()) {
				String parameter = (String) it.next();
				String parameterValue = m.get(parameter);

				String v = "";
				if (null != parameterValue) {
					v = parameterValue.trim();
				}
				packageParams.put(parameter, v);
			}

			logger.info(packageParams);
			// 判断签名是否正确
			if (PayCommonUtil.isTenpaySign(packageParams, PayConfigUtil.API_KEY)) {
				// ------------------------------
				// 处理业务开始
				// ------------------------------
				String resXml = "";
				if ("SUCCESS".equals((String) packageParams.get("result_code"))) {
					// 这里是支付成功
					// ////////执行自己的业务逻辑////////////////
					String mch_id = (String) packageParams.get("mch_id");
					String openid = (String) packageParams.get("openid");
					String is_subscribe = (String) packageParams.get("is_subscribe");
					String out_trade_no = (String) packageParams.get("out_trade_no");

					String total_fee = (String) packageParams.get("total_fee");

					logger.info("mch_id:" + mch_id);
					logger.info("openid:" + openid);
					logger.info("is_subscribe:" + is_subscribe);
					logger.info("out_trade_no:" + out_trade_no);
					logger.info("total_fee:" + total_fee);

					// ////////执行自己的业务逻辑////////////////

					logger.info("支付成功");
					// 通知微信.异步确认成功.必写.不然会一直通知后台.八次之后就认为交易失败了.
					resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>" + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";

				} else {
					logger.info("支付失败,错误信息：" + packageParams.get("err_code"));
					resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>" + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
				}
				// ------------------------------
				// 处理业务完毕
				// ------------------------------
				BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
				out.write(resXml.getBytes());
				out.flush();
				out.close();
			} else {
				logger.error("通知签名验证失败");
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}

	}
	
	
	
    /**
     * 作用：统一下单
     * 场景：公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
	private Map<String, String> unifiedOrder(SortedMap<String, String> reqData) throws Exception {
		String respXml = WXUtils.downloadString(PayConfigUtil.UNIFIEDORDER_URL, "POST", PayCommonUtil.getRequestXml(reqData));
		return processResponseXml(respXml);
	}

    /**
     * 处理 HTTPS API返回数据，转换成Map对象。return_code为SUCCESS时，验证签名。
     * @param xmlStr API返回的XML格式数据
     * @return Map类型数据
     * @throws Exception
     */
	private Map<String, String> processResponseXml(String xmlStr) throws Exception {
		String RETURN_CODE = "return_code";
		String return_code;
		SortedMap<String, String> respData = PayCommonUtil.xmlToMap(xmlStr);
		if (respData.containsKey(RETURN_CODE)) {
			return_code = respData.get(RETURN_CODE);
		} else {
			throw new Exception(String.format("No `return_code` in XML: %s", xmlStr));
		}

		if (return_code.equals(PayConfigUtil.FAIL)) {
			return respData;
		} else if (return_code.equals(PayConfigUtil.SUCCESS)) {
			if (PayCommonUtil.isTenpaySign(respData, PayConfigUtil.API_KEY)) {
				return respData;
			} else {
				throw new Exception(String.format("Invalid sign value in XML: %s", xmlStr));
			}
		} else {
			throw new Exception(String.format("return_code value %s is invalid in XML: %s", return_code, xmlStr));
		}
	}
    /**
     * 作用：查询订单<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> orderQuery() throws Exception {
    	SortedMap<String, String> reqMap = new TreeMap<>();
    	reqMap.put("transaction_id", "4001392001201708105468428003");
		Map orderMap = orderQuery(reqMap);
		return orderMap;
    }
	
	
    /**
     * 作用：查询订单<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据 
     * @return API返回数据
     * @throws Exception
     */
	private Map<String, String> orderQuery(SortedMap<String, String> reqData) throws Exception {
		String url = PayConfigUtil.ORDERQUERY_URL;
		String respXml = WXUtils.downloadString(url, "POST", PayCommonUtil.getRequestXml(fillRequestData(reqData)));
		return this.processResponseXml(respXml);
	}
	
	/**
     * 作用：关闭订单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> closeOrder() throws Exception {
    	SortedMap<String, String> reqMap = new TreeMap<>();
    	reqMap.put("out_trade_no", "4001392001201708105468428003");
    	Map resMap = closeOrder(reqMap);
    	return resMap;
    }


    /**
     * 作用：关闭订单<br>
     * 场景：公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> closeOrder(SortedMap<String, String> reqData) throws Exception {
        String url = PayConfigUtil.CLOSEORDER_URL;
        String respXml = WXUtils.downloadString(url, "POST", PayCommonUtil.getRequestXml(fillRequestData(reqData)));
        return this.processResponseXml(respXml);
    }

    /**
     * 作用：对账单下载（成功时返回对账单数据，失败时返回XML格式数据）<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付
     * @param reqData 向wxpay post的请求数据
     * @return API返回数据
     * @throws Exception
     */
    public Map<String, String> downloadBill() throws Exception {
    	SortedMap<String, String> reqMap = new TreeMap<>();
    	reqMap.put("bill_date", "20170810");
    	reqMap.put("bill_type", "ALL");
        return this.downloadBill(reqMap);
    }


    /**
     * 作用：对账单下载<br>
     * 场景：刷卡支付、公共号支付、扫码支付、APP支付<br>
     * 其他：无论是否成功都返回Map。若成功，返回的Map中含有return_code、return_msg、data，
     *      其中return_code为`SUCCESS`，data为对账单数据。
     * @param reqData 向wxpay post的请求数据
     * @param connectTimeoutMs 连接超时时间，单位是毫秒
     * @param readTimeoutMs 读超时时间，单位是毫秒
     * @return 经过封装的API返回数据
     * @throws Exception
     */
    public Map<String, String> downloadBill(SortedMap<String, String> reqData) throws Exception {
        String url = PayConfigUtil.DOWNLOADBILL_URL;
        String respStr = WXUtils.downloadString(url, "POST", PayCommonUtil.getRequestXml(fillRequestData(reqData)));
        Map<String, String> ret;
        // 出现错误，返回XML数据
        if (respStr.indexOf("<") == 0) {
            ret = PayCommonUtil.xmlToMap(respStr);
        }
        else {
            // 正常返回csv数据
            ret = new HashMap<String, String>();
            ret.put("return_code", WXPayConstants.SUCCESS);
            ret.put("return_msg", "ok");
            ret.put("data", respStr);
        }
        return ret;
    }


    
    
    /**
     * 向 Map 中添加 appid、mch_id、nonce_str、sign_type、sign <br>
     * 该函数适用于商户适用于统一下单等接口，不适用于红包、代金券接口
     *
     * @param reqData
     * @return
     * @throws Exception
     */
    public SortedMap<String, String> fillRequestData(SortedMap<String, String> reqMap) throws Exception {
    	reqMap.put("appid", PayConfigUtil.APP_ID);
    	reqMap.put("mch_id", PayConfigUtil.MCH_ID);
    	reqMap.put("nonce_str", StringUtils.getShortUUID());
    	reqMap.put("sign_type", PayConfigUtil.MD5);
    	reqMap.put("sign", PayCommonUtil.createSign(reqMap, PayConfigUtil.API_KEY));
        return reqMap;
    }
    
    public String getUser(HttpServletRequest request) throws OperException {
    	return WXLoginInterceptor.getUserIdFromToken(getDataByToken(request));
    }
    
	protected JSONObject getDataByToken(HttpServletRequest request) throws OperException {
		return WXLoginInterceptor.getJsonByRequest(request);
	}
}
