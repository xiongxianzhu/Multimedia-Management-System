package com.gditc.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.gditc.dao.FileDao;
import com.gditc.service.FileLogService;
/**
 * 从客户端获取删除云端文件请求， 并响应客户端
 * @File DeleteFileServlet.java
 * @Package com.gditc.servlet
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site http://blog.csdn.net/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月15日 下午8:16:28
 * @author Cryhelyxx
 * @version 1.0
 */
public class DeleteFileServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteFileServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Long mediaId = Long.valueOf(request.getParameter("mediaId"));
		FileDao dao = new FileDao();
		FileLogService service = new FileLogService();
		String jsonArr = null;
		try {
			jsonArr = service.getJsonArray(dao.getJsonPath());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (jsonArr == null) {
			request.setAttribute("result", "fail");
			RequestDispatcher rd = request.getRequestDispatcher("/returnResult.jsp");
			rd.forward(request, response);
		} else {
			/*
			 *  string to JSONArray(Object to JSONArray)
			 *  字符串转JSON数组(对象转JSON数组)
			 */
			JSONArray jsonArray = JSONArray.fromObject(jsonArr);
			System.out.println(jsonArray);
			
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				if (mediaId == jsonObject.getLong("mediaId")) {
					File delFile = new File(jsonObject.getString("filePath"));
					if (delFile.exists()) {
						// 将文件从物理磁盘上删除
						delFile.delete();
						// 在文件日志上移除该文件的json对象
						jsonArray.remove(jsonObject);
						
						try {
							// 如果不存在json对象， 则清空json日志文件
							if (jsonArray.size() == 0) {
								service.writeTo(dao.getJsonPath(), "");
							} else {
								service.writeTo(dao.getJsonPath(), jsonArray.toString());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						request.setAttribute("result", "success");
					} else {
						request.setAttribute("result", "fail");
					}
				} else {
					request.setAttribute("result", "fail");
				}
			}
			RequestDispatcher rd = request.getRequestDispatcher("/returnResult.jsp");
			rd.forward(request, response);
		}
	}
}
