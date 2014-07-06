package com.gditc.mmms.service;

import java.io.File;
import java.nio.charset.Charset;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gditc.mmms.R;
import com.gditc.mmms.app.MainActivity;
import com.gditc.mmms.common.Media;
import com.gditc.mmms.service.CustomMultiPartEntity.ProgressListener;
/**
 * 内部类实现异步任务上传
 * @File UploadTask.java
 * @Package com.gditc.mmms.service
 * @Description 内部类，借助项目httpmime实现文件上传到网络服务器
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月15日 上午1:11:38
 * @author Cryhelyxx
 * @version 1.0
 */
public class UploadTask extends AsyncTask<HttpResponse, Integer, String> {

	private int NOTIFICATION_UPLOAD_ID = 0;
	private NotificationManager nm = null;
	private Notification notification = null;
	private long totalSize;
	private Context context;
	private int mProgress = 0;
	private String ip = null;
	private Media asyncMedia = null;

	public UploadTask(Context context, NotificationManager nm, String ip, Media m, int nfId) {
		this.context = context;
		this.nm = nm;
		this.ip = ip;
		this.asyncMedia = m;
		this.NOTIFICATION_UPLOAD_ID = nfId;
	}

	/**
	 * 在execute(Params... params)被调用后立即执行，一般用来在执行后台任务前对UI做一些标记
	 */
	@Override
	protected void onPreExecute() {
		notification = new Notification();
		PendingIntent pi = PendingIntent.getActivity(
				context, 0, 
				new Intent(context, MainActivity.class), 0);
		notification.contentIntent = pi;
		notification.icon = android.R.drawable.stat_sys_upload;
		notification.tickerText = "uploading...";

		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.progressbar);
		notification.contentView = contentView;
		notification.contentView.setTextViewText(R.id.textView_progress, "进度 " + mProgress + "%");
		notification.contentView.setProgressBar(R.id.ProgressBar, 100, mProgress, false);
		// 发出通知
		nm.notify(NOTIFICATION_UPLOAD_ID, notification);
	}

	/**
	 * 在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。
	 * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息
	 */
	@Override
	protected String doInBackground(HttpResponse... arg0) {
		HttpClient httpClient = new DefaultHttpClient();	// HttpClient ：发起Http连接请求的对象
		HttpContext httpContext = new BasicHttpContext();
		String url = "http://" + ip + ":8080/WebAndroid/ManageServlet";
		HttpPost httpPost = new HttpPost(url);

		final long start = System.currentTimeMillis();

		try {
			CustomMultiPartEntity multipartContent = new CustomMultiPartEntity(HttpMultipartMode.BROWSER_COMPATIBLE,
					null, Charset.forName(HTTP.UTF_8), new ProgressListener() {
				@Override
				public void transferred(long num) {
					long end = System.currentTimeMillis();
					if((end - start) % 20 == 0){  // 每20ms更新一次
						publishProgress((int) ((num / (float) totalSize) * 100));

					}
				}
			});

			/*HttpParams params = httpClient.getParams();  
            params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, Charset.forName("UTF-8"));//关键的一句，让API识别到charset 
			HttpConnectionParams.setConnectionTimeout(params, 10*1000); 	//连接超时  
			HttpConnectionParams.setSoTimeout(params, 10*1000);             //读取数据超时
			 */
			// We use FileBody to transfer an file
			//File sdCardDir = Environment.getExternalStorageDirectory();
			File file = new File(asyncMedia.getUri());
			String fileName = file.getName();
			Log.i("文件名", fileName);		// 文件名， 如xxx.mp3
			// Charset.forName("UTF-8")成功解决字符串的乱码, HTTP.UTF_8 : "UTF-8"
			multipartContent.addPart("mediaId", new StringBody(String.valueOf(asyncMedia.get_id()), Charset.forName(HTTP.UTF_8)));
			multipartContent.addPart("mediaTitle", new StringBody(asyncMedia.getTitle(), Charset.forName(HTTP.UTF_8)));
			multipartContent.addPart("mediaName", new StringBody(asyncMedia.getName(), Charset.forName(HTTP.UTF_8)));
			multipartContent.addPart("mediaTime", new StringBody(asyncMedia.getTime().toString(), Charset.forName(HTTP.UTF_8)));
			multipartContent.addPart("mediaType", new StringBody(asyncMedia.getMediaType().toString(), Charset.forName(HTTP.UTF_8)));
			String fileType =  fileName.substring(fileName.lastIndexOf('.') + 1);
			Log.i("文件类型", fileType);	// 文件类型， 如mp4、mp4
			/*FileBody fileBody = new FileBody(file, file.getName(),
					fileType, "utf-8");*/
			FileBody fileBody = new FileBody(file, fileName,
					fileType, HTTP.UTF_8);
			//multipartContent.addPart("file", new FileBody(file));
			multipartContent.addPart("file", fileBody);
			totalSize = multipartContent.getContentLength();
			//System.out.println("返回长度: " + totalSize);
			Log.i("返回文件大小", Long.toString(totalSize));		// 单位是字节(bytes)
			// Send it
			httpPost.setEntity(multipartContent);
			//httpPost.setHeader("Charset", "utf-8");
			HttpResponse httpResponse = httpClient.execute(httpPost, httpContext);  // HttpResponse ：Http 请求返回的响应
			HttpEntity httpEntity = httpResponse.getEntity();
			String serverResponse = null;
			if (httpEntity != null && httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				serverResponse = EntityUtils.toString(httpEntity, HTTP.UTF_8);
				Log.i("服务器响应", serverResponse);
			}
			

			//ResponseFactory rp = new ResponseFactory(serverResponse);
			// return (TypeImage) rp.getData();

			return serverResponse;
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			try {
				// 释放连接资源, 这个shutdown并不是将手机网络断掉，而是将建立Http连接请求时所分配的资源释放掉
				httpClient.getConnectionManager().shutdown();
			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}
		return null;
	}

	/**
	 * 在调用publishProgress(Progress... values)时，此方法被执行，直接将进度信息更新到UI组件上
	 */
	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		mProgress = (int) values[0];
		Log.i("mProgress", String.valueOf(mProgress));
		notification.contentView.setProgressBar(R.id.ProgressBar, 100, mProgress, false);
		notification.contentView.setTextViewText(R.id.textView_progress, "进度 " + mProgress + "%");
		nm.notify(NOTIFICATION_UPLOAD_ID, notification);
	}

	/**
	 * 当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Toast.makeText(context,
				"上传完成", Toast.LENGTH_LONG).show();
		nm.cancel(NOTIFICATION_UPLOAD_ID);
	}
}
