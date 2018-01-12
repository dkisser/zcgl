package org.lf.admin.service.wx.pay;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.lf.utils.StringUtils;
import org.lf.wx.utils.WXUtils;

import com.alibaba.fastjson.JSONObject;

public class PayCommonUtil {
	
	/**
	 * 是否签名正确,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 * @return boolean
	 */
	public static boolean isTenpaySign(SortedMap<String, String> packageParams, String API_KEY) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while(it.hasNext()) {
			Map.Entry entry = (Map.Entry)it.next();
			String k = (String)entry.getKey();
			String v = (String)entry.getValue();
			if(!"sign".equals(k) && null != v && !"".equals(v)) {
				sb.append(k + "=" + v + "&");
			}
		}
		
		sb.append("key=" + API_KEY);
		
		//算出摘要
		String mysign = StringUtils.toMD5(sb.toString()).toUpperCase().toLowerCase();
		String tenpaySign = ((String)packageParams.get("sign")).toLowerCase();
		
		//System.out.println(tenpaySign + "    " + mysign);
		return tenpaySign.equals(mysign);
	}

	/**
	 * @author
	 * @date 2016-4-22
	 * @Description：sign签名
	 * @param characterEncoding
	 *            编码格式
	 * @param parameters
	 *            请求参数
	 * @return
	 */
	public static String createSign(SortedMap<String, String> packageParams, String API_KEY) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k) && !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
			}
		}
		sb.append("key=" + API_KEY);
		String sign = StringUtils.toMD5(sb.toString()).toUpperCase();
		return sign;
	}

	/**
	 * @author
	 * @date 2016-4-22
	 * @Description：将请求参数转换为xml格式的string
	 * @param parameters
	 *            请求参数
	 * @return
	 */
	public static String getRequestXml(SortedMap<String, String> parameters) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		Set es = parameters.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if ("attach".equalsIgnoreCase(k) || "body".equalsIgnoreCase(k) || "sign".equalsIgnoreCase(k)) {
				sb.append("<" + k + ">" + "<![CDATA[" + v + "]]></" + k + ">");
			} else {
				sb.append("<" + k + ">" + v + "</" + k + ">");
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}

	/**
	 * 取出一个指定长度大小的随机正整数.
	 * 
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}

	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * 
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}
	
    /**
     * 获取request里的ip
     * 
     * @param request
     * @return
     */
    public static String getIpAddr(HttpServletRequest request) {  
        InetAddress addr = null;  
        try {  
            addr = InetAddress.getLocalHost();  
        } catch (UnknownHostException e) {  
            return request.getRemoteAddr();  
        }  
        byte[] ipAddr = addr.getAddress();  
        String ipAddrStr = "";  
        for (int i = 0; i < ipAddr.length; i++) {  
            if (i > 0) {  
                ipAddrStr += ".";  
            }  
            ipAddrStr += ipAddr[i] & 0xFF;  
        }  
        return ipAddrStr;  
    }
    
    /**
     * 生成订单号
     * 
     * @return
     */
	public static String genOrderNO() {
		Date curDate = new Date();
		String dateStart = new SimpleDateFormat("yyyyMMddHHmmss")
				.format(curDate);
		String dateEnd = new SimpleDateFormat("SSS").format(curDate);
		int hashCodeV = StringUtils.getUUID().hashCode();
		if (hashCodeV < 0) {// 有可能是负数
			hashCodeV = -hashCodeV;
		}
		return dateStart + String.format("%010d", hashCodeV) + dateEnd;
	}
	
	public static String getOpenIdByUserId(String userId, String accessToken) {
		
		String openIdUrl = "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=" + accessToken;
		Map<String, Object> map = new HashMap<>();
		map.put("userid", userId);
		String re = WXUtils.downloadString(openIdUrl, "POST", new JSONObject(map).toString());
		JSONObject jObject = JSONObject.parseObject(re);
		return jObject.getString("openid");
	}
    /**  
     * xml 转 Map  
     * @param xml  
     * @return  
     */  
	public static SortedMap<String, String> xmlToMap(String xml) {
		SortedMap<String, String> map = new TreeMap<String, String>();
		Document doc = null;
		try {
			doc = DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (doc == null)
			return map;
		Element root = doc.getRootElement();
		for (Iterator<Element> iterator = root.elementIterator(); iterator.hasNext();) {
			Element e = iterator.next();
			map.put(e.getName(), e.getText());
		}
		return map;
	}
}