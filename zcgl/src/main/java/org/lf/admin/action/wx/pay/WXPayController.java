package org.lf.admin.action.wx.pay;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.lf.admin.action.wx.WXBaseController;
import org.lf.admin.service.WXResultCode;
import org.lf.admin.service.wx.WXOAuthService;
import org.lf.admin.service.wx.pay.WXPayService;
import org.lf.utils.AjaxResultModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin
@RequestMapping("/wx/pay/")
public class WXPayController extends WXBaseController {

	@Autowired
	WXPayService wXPayService;

	@Autowired
	WXOAuthService wxoAuthService;
	
	private static final String KEY = "4aac6057b0204b949521a92708c47809";

	@RequestMapping("getWxPayQRCode.do")
	@ResponseBody
	public AjaxResultModel getWxPayQRCode(HttpServletRequest request, HttpServletResponse response) {
		AjaxResultModel result = new AjaxResultModel();
		try {
			String code = wXPayService.getWxPayQRCode();
			result.setData(code);
			result.setCode(WXResultCode.SUCCESS.getCode());
			result.setMsg(WXResultCode.SUCCESS.getMsg());
		} catch (Exception e) {
			result.setCode(WXResultCode.ERROR.getCode());
			result.setMsg(e.getMessage());
		}
		return result;
	}

	@RequestMapping("publicPay.do")
	@ResponseBody
	public AjaxResultModel publicPay(HttpServletRequest request, HttpServletResponse response) {
		AjaxResultModel result = new AjaxResultModel();
		try {
			result.setData(wXPayService.publicPay(getUserId(request), request));
			result.setCode(WXResultCode.SUCCESS.getCode());
			result.setMsg(WXResultCode.SUCCESS.getMsg());
		} catch (Exception e) {
			result.setCode(WXResultCode.ERROR.getCode());
			result.setMsg(e.getMessage());
		}
		return result;
	}
}
