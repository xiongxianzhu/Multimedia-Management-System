package com.gditc.mmms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.AsyncTask;
/**
 * 删除网络媒体文件异步任务
 * @File DeleteRemoteMediaTask.java
 * @Package com.gditc.mmms.service
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:03:30
 * @author Cryhelyxx
 * @version 1.0
 */
public class DeleteRemoteMediaTask extends AsyncTask<String, Integer, String>{
	
	private String ip;

	public DeleteRemoteMediaTask(String ip) {
		this.ip = ip;
	}

	@Override
	protected String doInBackground(String... params) {
		String url = "http://" + ip + ":8080/WebAndroid/DeleteFileServlet";
		Map<String, String> paramsMap = new HashMap<String, String>();
		paramsMap.put("mediaId", params[0]);
		try {
			return sendHttpClientPOSTRequest(url, paramsMap, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 通过HttpClient发送Post请求
	 * @param url 请求路径
	 * @param params 请求参数
	 * @param encoding 编码
	 * @return 请求是否成功
	 * @throws Exception
	 */
	private static String sendHttpClientPOSTRequest(String url,
			Map<String, String> params, String encoding) throws Exception {
		// 存放请求参数
		List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		if (params != null && !params.isEmpty()) {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				pairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		UrlEncodedFormEntity entity = new UrlEncodedFormEntity(pairs, encoding);
		HttpPost httpPost = new HttpPost(url);
		httpPost.setEntity(entity);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpPost);
		String serverResponse = "fail";
		if (response.getStatusLine().getStatusCode() == 200) {
			// HttpResponse ：Http 请求返回的响应
			serverResponse = EntityUtils.toString(response.getEntity(), HTTP.UTF_8);
			// 删除开头的"\r\n"
			serverResponse = serverResponse.substring(2);
		}
		// 返回服务器响应: "true" or "false" or "error"
		return serverResponse;
	}
}
