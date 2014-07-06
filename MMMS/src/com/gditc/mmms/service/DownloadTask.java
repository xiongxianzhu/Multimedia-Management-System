package com.gditc.mmms.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.gditc.mmms.R;
import com.gditc.mmms.app.MainActivity;

/**
 * 实现文件由云端同步到本地， 即下载任务
 * @File DownloadTask.java
 * @Package com.gditc.mmms.service
 * @Description 不要在主线程中访问网络， 所以通过异步任务实现文件从网络上下载
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月10日 下午3:35:42
 * @author Cryhelyxx
 * @version 1.0
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

	private int NOTIFICATION_DOWNLOAD_ID = 0;
	private Context context;
	private NotificationManager nm = null;
	private Notification notification = null;
	private int mProgress = 0;
	private String fileName = null;
	private int fileSize = 0;
	private int totalSize = 0;

	public DownloadTask(Context context, NotificationManager nm, String fileName, int nfId) {
		super();
		this.context = context;
		this.nm = nm;
		this.fileName = fileName;
		this.NOTIFICATION_DOWNLOAD_ID = nfId;
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
		notification.icon = android.R.drawable.stat_sys_download;
		notification.tickerText = "Downloading...";

		RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.progressbar);
		notification.contentView = contentView;
		notification.contentView.setTextViewText(R.id.textView_progress, "进度 " + mProgress + "%");
		notification.contentView.setProgressBar(R.id.ProgressBar, 100, mProgress, false);
		// 发出通知
		nm.notify(NOTIFICATION_DOWNLOAD_ID, notification);
	}

	/**
	 * 在onPreExecute()完成后立即执行，用于执行较为费时的操作，此方法将接收输入参数和返回计算结果。
	 * 在执行过程中可以调用publishProgress(Progress... values)来更新进度信息
	 * 由execute(params)传递参数
	 */
	@Override
	protected String doInBackground(String... params) {
		String urlStr = params[0];		// 获取文件的网络路径
		String folder = "download";		// 下载的文件的存放目录
		OutputStream os = null;
		
		try {
			/** 
             * 通过URL取得HttpURLConnection 
             * 要网络连接成功，需在AndroidMainfest.xml中进行权限配置 
             * <uses-permission android:name="android.permission.INTERNET" /> 
             */  
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("POST");
			int code = 0;
			code = conn.getResponseCode();
			if (code == 200) {
				fileSize = conn.getContentLength();
				/** 
				 * //取得inputStream，并将流中的信息写入SDCard  
	             * 写前准备 
	             * 1. 在AndroidMainfest.xml中进行权限配置 
	             * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" /> 
	             * 取得写入SDCard的权限 
	             * 2. 取得SDCard的路径： Environment.getExternalStorageDirectory() 
	             * 3. 检查要保存的文件上是否已经存在 
	             * 4. 不存在，新建文件夹，新建文件 
	             * 5. 将input流中的信息写入SDCard 
	             * 6. 关闭流 
	             */  
				String sdCardDir = Environment.getExternalStorageDirectory() + "";
				String pathName = sdCardDir + "/" + folder + "/" + fileName;
				File file = new File(pathName);
				InputStream is = conn.getInputStream();
				if (!file.exists()) {
					String dir = sdCardDir + "/" + folder;
					//new File(dir).mkdirs();		// 新建文件夹
					new File(dir).mkdir();		// 新建文件夹
					file.createNewFile();		// 创建文件
					os = new FileOutputStream(file);
					// 读取大文件
					byte[] buffer = new byte[4*1024];
					int len = 0;
					long start = System.currentTimeMillis();
					while ((len = is.read(buffer)) != -1) {
						totalSize += len;	// 每读一次， 就将totalSize累加一次
						os.write(buffer, 0, len);
						int progress = (int)((float)totalSize / (float)fileSize * 100);
						long end = System.currentTimeMillis();
	                    if(progress == 100)
	                    	//时刻将当前进度更新给onProgressUpdate方法                       
	                    	publishProgress(progress);
	                    if((end - start) % 20 == 0){                        	
	                    	//时刻将当前进度更新给onProgressUpdate方法                       
	                    	publishProgress(progress);
	                    }
					}
					os.flush();
					is.close();
				} else {
					//showToast();
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (os != null)
					os.close();
			} catch (IOException e) {
				e.printStackTrace();
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
		nm.notify(NOTIFICATION_DOWNLOAD_ID, notification);
	}

	/**
	 * 当后台操作结束时，此方法将会被调用，计算结果将做为参数传递到此方法中，直接将结果显示到UI组件上
	 */
	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		Toast.makeText(context,
				fileName + "下载完成", Toast.LENGTH_SHORT).show();
		nm.cancel(NOTIFICATION_DOWNLOAD_ID);
	}
	
	/**
	 * 显示Toast提示
	 */
	public void showToast() {
		Toast.makeText(context, "成功秒传到本地",
				Toast.LENGTH_LONG).show();
	}
}
