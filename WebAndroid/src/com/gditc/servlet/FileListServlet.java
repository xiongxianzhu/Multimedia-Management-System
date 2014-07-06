package com.gditc.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.gditc.dao.FileDao;
import com.gditc.service.FileLogService;
/**
 * 文件列表Servlet
 * @File FileListServlet.java
 * @Package com.gditc.servlet
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site http://blog.csdn.net/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月8日 上午12:44:05
 * @author Cryhelyxx
 * @version 1.0
 */
public class FileListServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		FileDao dao = new FileDao();
		FileLogService service = new FileLogService();
		String jsonArr = null;
		try {
			jsonArr = service.getJsonArray(dao.getJsonPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (jsonArr == null) {
			jsonArr = "nodata";
		}
		
		// 方案一：响应客户端， 向客户端返回数据， 该方案会导致返回客户端的JSON数据开头多了"\r\n"回车换行
		/*request.setAttribute("jsonData", jsonArr);
		RequestDispatcher rd = request.getRequestDispatcher("/jsonData.jsp");
		rd.forward(request, response);*/
		// 方案二：响应客户端， 向客户端返回数据
		response.getOutputStream().write(jsonArr.getBytes("UTF-8"));  
        response.setContentType("text/json; charset=UTF-8");  //JSON的类型为text/json  
	}
}
