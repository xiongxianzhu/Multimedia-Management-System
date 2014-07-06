package com.gditc.mmms.app;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.gditc.mmms.R;
import com.gditc.mmms.app.RefreshableView.PullToRefreshListener;
import com.gditc.mmms.common.Media;
import com.gditc.mmms.common.MediaAdapter;
import com.gditc.mmms.service.DeleteRemoteMediaTask;
import com.gditc.mmms.service.DownloadTask;
import com.gditc.mmms.service.GetJsonDataService;
import com.gditc.mmms.utils.MyApplication;
/**
 * 网络媒体界面
 * @File RemoteMedia.java
 * @Package com.gditc.mmms.app
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 上午11:50:13
 * @author Cryhelyxx
 * @version 1.0
 */
public class RemoteMedia extends Activity {

	private static int NOTIFICATION_DOWNLOAD_ID = 0x234;
	private static final int REFRESH = 0x001;

	private String ip = null;
	private SharedPreferences mPerPreferences = null;
	private ListView lv = null;
	private List<Media> listMedia = null;
	private List<Media> listMusic = null;
	private List<Media> listImage = null;
	private List<Media> listVideo = null;
	private RefreshableView refreshableView = null;  

	private NotificationManager nm = null;
	private Handler hRefresh;
	private static String jsonData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.remote_media);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		// 刷新UI元素
		hRefresh = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				switch(msg.what){
				case REFRESH:
					/*Refresh UI*/
					refresh();
					break;
				}
			}
		};
		initData();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		lv = (ListView) this.findViewById(R.id.remote_listView);
		refreshableView = (RefreshableView) this.findViewById(R.id.refreshable_view);
		// 获取系统的NotificationManager服务
		nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		ip = getIPAddress();	// 获取IP地址
		listMedia = new ArrayList<Media>();
		listImage = new ArrayList<Media>();
		listMusic = new ArrayList<Media>();
		listVideo = new ArrayList<Media>();
		GetJsonDataService myTask = new GetJsonDataService(this, ip);

		try {
			if (jsonData == null) {
				/*
				 *  获取doInBackground(String... params)返回的值， 即json数据
				 *  不能在主线程以外的线程里创建AsynTask实例
				 */
				jsonData = myTask.execute().get();
			}

			Toast.makeText(getApplicationContext(), "刷新成功", Toast.LENGTH_LONG).show();
			JSONArray jsonArr = new JSONArray(jsonData);
			for (int i = 0; i < jsonArr.length(); i++) {
				JSONObject jsonObject = jsonArr.getJSONObject(i);
				Media media = new Media();
				media.set_id(jsonObject.getLong("mediaId"));
				media.setTitle(jsonObject.getString("mediaTitle"));
				media.setName(jsonObject.getString("mediaName"));
				media.setUri(jsonObject.getString("mediaUri"));
				media.setSize(jsonObject.getLong("mediaSize"));
				media.setMediaType(jsonObject.getInt("mediaType"));
				media.setTime(jsonObject.getLong("mediaTime"));
				if (media.getMediaType() == Media.IMAGE)
					listImage.add(media);
				if (media.getMediaType() == Media.MUSIC)
					listMusic.add(media);
				if (media.getMediaType() == Media.VIDEO)
					listVideo.add(media);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 控制显示在ListView里的放置顺序：图片、 音频、 视频
		for (int i = 0; i < listImage.size(); i++)
			listMedia.add(listImage.get(i));
		for (int i = 0; i < listMusic.size(); i++)
			listMedia.add(listMusic.get(i));
		for (int i = 0; i < listVideo.size(); i++)
			listMedia.add(listVideo.get(i));

		MediaAdapter adapter = new MediaAdapter(this, listMedia);
		lv.setAdapter(adapter);
		this.registerForContextMenu(lv);	//注册上下文菜单
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				playMedia(listMedia.get(position));
			}
		});
		/**
		 * 参考文章：
		 * 1. 在主线线程之外的线程中直接更新页面显示的问题
		 * 		http://blog.csdn.net/vincent_czz/article/details/7070354
		 * 2. ListView下拉刷新上拉加载更多实现
		 * 		http://blog.csdn.net/baiyuliang2013/article/details/20624183
		 * 3. android下拉刷新完全解析
		 * 		http://blog.csdn.net/guolin_blog/article/details/9255575
		 */
		refreshableView.setOnRefreshListener(new PullToRefreshListener() {

			@Override  
			public void onRefresh() {  // 执行刷新UI元素操作， 借助Handler
				try {  
					Thread.sleep(1000);  
				} catch (InterruptedException e) {  
					e.printStackTrace();  
				}  
				hRefresh.sendEmptyMessage(REFRESH);
				refreshableView.finishRefreshing();  
			}  
		}, 0);  
	}

	@SuppressWarnings("deprecation")
	private String getIPAddress() {
		mPerPreferences = this.getSharedPreferences("ipaddress", MODE_PRIVATE);
		ip = mPerPreferences.getString("ip", null);
		if (null == ip || "".equals(ip.trim())) {
			Bundle args = new Bundle();
			showDialog(1, args);
		}

		return ip;
	}

	/**
	 * 弹出对话框， 用来设置IP值
	 */
	@SuppressWarnings("deprecation")
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("网络设置选项");
		final LinearLayout ll = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.ipsetting, null);
		builder.setView(ll);
		builder.setPositiveButton("确 定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText txtIP = (EditText) ll.findViewById(R.id.txt_ipaddress);
				ip = txtIP.getText().toString().trim();
				if ("".equals(ip) || null == ip) {
					dialog.dismiss();
					AlertDialog.Builder builder2 = new Builder(RemoteMedia.this);
					builder2.setTitle("提示信息");
					builder2.setMessage("你没有激活网络媒体服务，将不能将媒体同步到网络服务器上。 ");
					builder2.setNegativeButton("返 回", new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
					builder2.create();
					builder2.show();
				} else {
					SharedPreferences.Editor mEditor = mPerPreferences.edit();
					mEditor.putString("ip", ip);
					mEditor.commit();
					refresh(); // 刷新界面， 将获取到的数据显示出来
				}

			}
		});
		builder.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ip = null;
				dialog.dismiss();
				//dialog.cancel();
			}
		});
		builder.create();
		builder.show();
		return super.onCreateDialog(id, args);
	}

	/**
	 * 刷新界面
	 */
	public void refresh() {
		jsonData = null;			// 清空缓存
		initData();
	}

	/**
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflator = new MenuInflater(this);
		// 设置上下文标题
		menu.setHeaderTitle("操作该媒体");
		// 装填R.menu.context对应的菜单， 并添加到menu中
		inflator.inflate(R.menu.context02, menu);
	}

	/**
	 * 上下文菜单被选中事件监听
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.item_play02:
			playMedia(listMedia.get(menuInfo.position));
			break;

		case R.id.item_infoDetail02:
			lookMediaInfoDetail(listMedia.get(menuInfo.position));
			break;


		case R.id.item_asyncToLocal02:
			syncMediaToLocal(listMedia.get(menuInfo.position));
			break;

		case R.id.item_delete02:
			deleteMedia(listMedia.get(menuInfo.position));
			break;

		}
		return true;
	}

	/**
	 * 播放网络媒体
	 * @param media 媒体文件
	 */
	private void playMedia(Media media) {
		String path = media.getUri();
		String serverPath = "http://" + ip + ":8080/WebAndroid" + path; 
		Integer type = media.getMediaType();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse(serverPath);
		if (type == Media.IMAGE) {
			/*Log.i("imageUri", serverPath);
			intent.setDataAndType(uri, "image/*");*/
			/**
			 * 跳转到另一Activity， 并传递参数
			 */
			// 创建Bundle对象
			Bundle bundle = new Bundle();
			bundle.putString("imagePath", serverPath);
			
			// 创建Intent对象
			//Intent i = new Intent(getApplicationContext(), LookNetworkImageActivity.class);
			// 下面两句与上面一句等效
			Intent i = new Intent();
			i.setClass(getApplicationContext(), LookNetworkImageActivity.class);
			
			i.putExtra("data", bundle);
			// 启动intent对应的Activity
			RemoteMedia.this.startActivity(i);
		} else if (type == Media.MUSIC) {
			Log.i("musicUri", serverPath);
			intent.setDataAndType(uri, "audio/*");
			RemoteMedia.this.startActivity(intent);
		}
		else if (type == Media.VIDEO){
			Log.i("videoUri", serverPath);
			intent.setDataAndType(uri, "video/*");
			RemoteMedia.this.startActivity(intent);
		}
	}

	/**
	 * 查看媒体详细信息
	 * @param media 媒体文件
	 */
	@SuppressLint("SimpleDateFormat")
	private void lookMediaInfoDetail(Media media) {
		AlertDialog.Builder builder = new Builder(this);
		// 设置对话框标题
		builder.setTitle("媒体详细信息");
		//String name = media.getName();				// 媒体文件名称
		String title = media.getTitle();			// 媒体标题
		Integer type = media.getMediaType();		// 媒体类型
		Long size = media.getSize();				// 媒体大小
		Long time = media.getTime();				// 创建时间
		// 时间格式转换
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy年MM月dd日");
		SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss");
		String t1 = sdf1.format(time);
		String t2 = sdf2.format(time);
		// 文件大小格式转换
		String s = bytes2kb(size);

		String mediaType = null;
		if (type == Media.IMAGE)
			mediaType = "图片";
		else if (type == Media.MUSIC)
			mediaType = "音频";
		else if (type == Media.VIDEO)
			mediaType = "视频";

		boolean isExist = isAsyncToLocal(media);
		String isAsync = "否";
		if (isExist)
			isAsync = "是";

		builder.setMessage(
				"媒体名称： " + title + 
				"\n媒体类型： " + mediaType +
				"\n媒体大小： " + s +
				"\n创建日期： " + t1 +
				"\n创建时间： " + t2 +
				"\n是否已同步到本地： " + isAsync);
		builder.setNegativeButton("返 回", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss(); 		// 结束对话框
			}
		});
		builder.create();	// 创建对话框
		builder.show();		// 显示对话框
	}

	/**
	 * 该媒体是否已同步到本地
	 * @param media
	 * @return
	 */
	private boolean isAsyncToLocal(Media media) {
		mPerPreferences = this.getSharedPreferences("isAsyncToLocal", MODE_PRIVATE);
		boolean isExist = mPerPreferences.contains(media.get_id().toString());
		return isExist;
	}

	/**
	 * 该媒体是否已同步到网络
	 * @param media
	 * @return
	 */
	private boolean isAsyncToNetwork(Media media) {
		mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
		boolean isExist = mPerPreferences.contains(media.get_id().toString());
		return isExist;
	}

	/**
	 * 文件大小单位转换， 字节转换为kb
	 * @param bytes
	 * @return
	 */
	public static String bytes2kb(Long bytes) {
		BigDecimal filesize = new  BigDecimal(bytes);
		BigDecimal megabyte = new  BigDecimal(1024 * 1024);
		float returnValue = filesize.divide(megabyte,
				2, BigDecimal.ROUND_UP).floatValue();
		if (returnValue > 1)
			return (returnValue + " MB");
		BigDecimal kilobyte = new BigDecimal(1024);
		returnValue  =  filesize.divide(kilobyte,
				2, BigDecimal.ROUND_UP).floatValue();
		return (returnValue + " KB");
	}

	/**
	 * 同步网络媒体到本地
	 * @param media 媒体文件
	 */
	private void syncMediaToLocal(Media media) {
		String filePath = media.getUri();
		String fileName = media.getName();
		filePath = "http://" + ip + ":8080/WebAndroid" + filePath;

		/*
		 * 自增ID实现多任务下载， 加入队列排队下载
		 */
		DownloadTask task = new DownloadTask(this, nm, fileName, NOTIFICATION_DOWNLOAD_ID++);
		task.execute(filePath);

		boolean isExist = isAsyncToLocal(media);
		if (!isExist) {
			mPerPreferences = this.getSharedPreferences("isAsyncToLocal", MODE_PRIVATE);
			SharedPreferences.Editor mEditor = mPerPreferences.edit();
			mEditor.putString(media.get_id().toString(), media.get_id().toString());
			mEditor.commit();
		}
	}

	/**
	 * 删除媒体
	 * @param media 媒体文件
	 */
	private void deleteMedia(Media media) {
		Long mediaId = media.get_id();
		DeleteRemoteMediaTask task = new DeleteRemoteMediaTask(ip);
		String reVal = null;
		try {
			reVal = task.execute(String.valueOf(mediaId)).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		if (reVal.equals("success")) {
			// 在isAsyncToNetwork.xml文件中移除该媒体的信息
			boolean isExist = isAsyncToNetwork(media);
			if (isExist) {
				mPerPreferences = RemoteMedia.this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
				SharedPreferences.Editor mEditor = mPerPreferences.edit();
				mEditor.remove(media.get_id().toString());
				mEditor.commit();
			}

			// 在isAsyncToLocal.xml文件中移除该媒体的信息
			isExist = isAsyncToNetwork(media);
			if (isExist) {
				mPerPreferences = RemoteMedia.this.getSharedPreferences("isAsyncToLocal", MODE_PRIVATE);
				SharedPreferences.Editor mEditor = mPerPreferences.edit();
				mEditor.remove(media.get_id().toString());
				mEditor.commit();
			}

			Toast.makeText(RemoteMedia.this,
					"删除成功", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(RemoteMedia.this,
					"删除失败", Toast.LENGTH_LONG).show();
		}
		refresh();
	}
}
