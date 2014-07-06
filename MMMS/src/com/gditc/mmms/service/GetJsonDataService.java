package com.gditc.mmms.service;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.gditc.mmms.utils.StreamTool;
/**
 * 获取网络媒体JSON数据
 * @File RemoteMediaService.java
 * @Package com.gditc.mmms.service
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月8日 下午5:08:36
 * @author Cryhelyxx
 * @version 1.0
 */
public class GetJsonDataService extends AsyncTask<String, Integer, String>{

	private String ip = "";
	private ProgressDialog progressDialog;
	private Context context;

	public GetJsonDataService(Context context, String ip) {
		this.context = context;
		this.ip = ip;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("提示信息");
		progressDialog.setMessage("正在连接网络服务器...");
		// 设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
		progressDialog.setCancelable(false);
		// 设置ProgressDialog样式为圆圈的形式
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 在onPreExecute()中我们让ProgressDialog显示出来
		progressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		String path = "http://" + ip + ":8080/WebAndroid/FileListServlet";
		//String path = "http://172.17.161.98:8080/WebAndroid/FileListServlet";

		URL url = null;
		HttpURLConnection conn = null;
		/**
		 * TODO:
		 *  1. conn.getResponseCode(); 在高级API下出现异常， 该APP在低API下可以正常运行
		 *  2. 要在新线程中执行该代码， 在高级API机器中， conn.getResponseCode();不能出现在UI线程
		 *  3. android 4.0需要使用多线程异步处理即可
		 *  4. 关键问题是不要在主线程里访问网络 不符合原则 这是根本。。。切记
		 *  
		 */
		int code = 0;
		InputStream is = null;
		try {
			url = new URL(path);
			conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			// POST/GET要大写
			conn.setRequestMethod("POST");
			code = conn.getResponseCode();
			if (code == 200) {
				is = conn.getInputStream();
				return parseJSON(is);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		// 使ProgressDialog框消失
		progressDialog.dismiss();
	}

	/**
	 * 解析JSON数据
	 * @param is
	 * @return
	 * @throws Exception 
	 */
	private static String parseJSON(InputStream is) throws Exception {
		byte[] data = StreamTool.read(is);
		String json = new String(data);
		return json;
	}
}