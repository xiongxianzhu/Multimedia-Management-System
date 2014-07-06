package com.gditc.filter;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
/**
 * 
 * @ClassName: EncodingRequestWrapper.java
 * @Package com.gditc.filter
 * @Description: TODO
 * @Copyright: Copyright © 2014
 * @Company: 广东轻工职业技术学院计算机工程系软件121
 * @author Cryhelyxx
 * @date 2014年5月26日 下午5:04:50
 * @version V1.0
 */
public class EncodingRequestWrapper extends HttpServletRequestWrapper {
	
	private HttpServletRequest request;

	public EncodingRequestWrapper(HttpServletRequest request) {
		super(request);
		this.request = request;
	}
	/**
	 * 获取请求参数
	 */
	public String getParameter(String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				value = new String(value.getBytes("ISO8859-1"), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
