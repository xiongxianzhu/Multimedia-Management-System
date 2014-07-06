package com.gditc.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @ClassName: EncodingFilter.java
 * @Package com.gditc.filter
 * @Description: 编码过滤器， 转换为UTF-8
 * @Copyright: Copyright © 2014
 * @Company: 广东轻工职业技术学院计算机工程系软件121
 * @author Cryhelyxx
 * @date 2014年5月26日 下午4:56:38
 * @version V1.0
 */
//@WebFilter("/*")
public class EncodingFilter implements Filter {

	public void destroy() {
		// TODO Auto-generated method stub
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		// 判断请求的方法， 其中GET要是大写
		if ("GET".equals(req.getMethod())) { 	// 请求方式是GET
			EncodingRequestWrapper wrapper = new EncodingRequestWrapper(req);
			chain.doFilter(wrapper, response);
		} else {								// 请求方式是POST
			req.setCharacterEncoding("UTF-8");
			chain.doFilter(request, response);
			
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
