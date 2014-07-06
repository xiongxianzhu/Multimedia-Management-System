package com.gditc.mmms.service;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class GetNetworkImageTask extends AsyncTask<String, Integer, byte[]> {
	
	private ProgressDialog progressDialog;
	private Context context;
	
	public GetNetworkImageTask(Context context) {
		super();
		this.context = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("提示信息");
		progressDialog.setMessage("正在加载网络图片...");
		// 设置setCancelable(false); 表示我们不能取消这个弹出框，等下载完成之后再让弹出框消失
        progressDialog.setCancelable(false);
        // 设置ProgressDialog样式为圆圈的形式
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		// 在onPreExecute()中我们让ProgressDialog显示出来
        progressDialog.show();
	}

	@Override
	protected byte[] doInBackground(String... params) {
		// 通过Apache的HttpClient来访问请求网络中的图片
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(params[0]);
		byte[] image = new byte[]{};
		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				image = EntityUtils.toByteArray(httpEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return image;
	}
	
	@Override
	protected void onPostExecute(byte[] result) {
		super.onPostExecute(result);
		// 使ProgressDialog框消失
        progressDialog.dismiss();
	}
}
