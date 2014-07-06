package com.gditc.mmms.app;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.TabActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.gditc.mmms.R;
import com.gditc.mmms.common.Image;
import com.gditc.mmms.common.Media;
import com.gditc.mmms.common.Music;
import com.gditc.mmms.common.Video;
import com.gditc.mmms.data.ImageList;
import com.gditc.mmms.data.MusicList;
import com.gditc.mmms.data.VideoList;
import com.gditc.mmms.service.DownloadTask;
import com.gditc.mmms.service.GetJsonDataService;
import com.gditc.mmms.service.UploadTask;
import com.gditc.mmms.utils.MyApplication;
/**
 * 主界面
 * @File MainActivity.java
 * @Package com.gditc.mmms.app
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:01:47
 * @author Cryhelyxx
 * @version 1.0
 */
@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {

	//public static String url = "http://172.17.161.98:8080/WebAndroid/ManageServlet";
	private long firstTime = 0;
	private SharedPreferences mPerPreferences = null;
	private String ip = null;
	private List<Media> listMedia = null;
	private List<Music> listMusic = null;
	private List<Image> listImage = null;
	private List<Video> listVideo = null;
	private static int NOTIFICATION_UPLOAD_ID = 0x345;
	private static int NOTIFICATION_DOWNLOAD_ID = 0x456;
	private NotificationManager nm = null;

	private EditText txtIP = null;
	private static String jsonData = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initData();
	}

	private void loadingFormation() {
		// 获取系统的NotificationManager服务
		nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		listMedia = new ArrayList<Media>();
		listMedia = getLocalMediaList();
		// 获取该Activity里面的TabHost组件
		TabHost tabHost = getTabHost();
		/*TabHost tabHost = (TabHost) this.findViewById(R.id.tabhost);
		tabHost.setup();*/
		// 获取文件资源对象
		//Resources res = this.getResources();

		// 设置使用TabHost布局
		LayoutInflater.from(this).inflate(R.layout.activity_main,
				tabHost.getTabContentView(), true);
		// 创建"本地媒体"标签页
		TabSpec tab01 = tabHost.newTabSpec("tab01");	// 创建选项卡
		// 设置标签标题及图标
		//tab01.setIndicator("本地媒体", res.getDrawable(R.drawable.local));		
		tab01.setIndicator("本地媒体");		
		tab01.setContent(new Intent(this, LocalMedia.class));	// 设置内容
		tabHost.addTab(tab01);			// 添加标签页tab01到TabHost组件上
		// 创建"网络媒体"标签页
		TabSpec tab02 = tabHost.newTabSpec("tab02");	// 创建选项卡
		// 设置标签标题及图标 
		//tab02.setIndicator("网络媒体", res.getDrawable(R.drawable.remote));		
		tab02.setIndicator("网络媒体");		
		tab02.setContent(new Intent(this, RemoteMedia.class));	//设置内容
		tabHost.addTab(tab02);			// 添加标签页tab02到TabHost组件上
	}

	/**
	 * 获取本地媒体
	 * @return
	 */
	private List<Media> getLocalMediaList() {
		listMusic = MusicList.getMusicData(getApplicationContext());
		listImage = ImageList.getImageDate(getApplicationContext());
		listVideo = VideoList.getVideoData(getApplicationContext());

		listMedia.addAll(listMusic);
		listMedia.addAll(listImage);
		listMedia.addAll(listVideo);
		return listMedia;
	}

	/**
	 * 工具栏菜单
	 * 当用户单击Menu键时触发该方法
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.options_menu, menu);
		return super.onCreateOptionsMenu(menu);
		//return true;
	}

	/**
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.asyncAllMedia2Network :
			// 处理“同步所有本地媒体到网络”操作
			asyncAllMedia2Network();
			return true;

		case R.id.asyncAllMedia2Local :
			// 处理“同步所有网络媒体到本地”操作
			asyncAllMedia2Local();
			return true;

		case R.id.remotes_settings :
			Bundle args = new Bundle();
			showDialog(1, args);
			return true;
		}
		return super.onOptionsItemSelected(item);
		//return false;
	}

	/**
	 * 同步所有本地媒体到网络
	 */
	public void asyncAllMedia2Network() {
		mPerPreferences = this.getSharedPreferences("ipaddress", MODE_PRIVATE);
		ip = mPerPreferences.getString("ip", null);
		if (ip == null || "".equals(ip)) {
			Toast.makeText(MainActivity.this,
					"请按Menu按钮设置网络服务器IP", Toast.LENGTH_LONG).show();
		}
		GetJsonDataService myTask = new GetJsonDataService(this, ip);
		JSONArray jsonArr = null;
		try {
			// 获取doInBackground(String... params)返回的值， 即json数据
			jsonData = myTask.execute().get();
			for (Media m : listMedia) {
				/*if (jsonData == null) {
					// 获取doInBackground(String... params)返回的值， 即json数据
					jsonData = myTask.execute().get();
				}*/
				// 获取doInBackground(String... params)返回的值， 即json数据

				if (!jsonData.equals("nodata")) {
					jsonArr = new JSONArray(jsonData);
					int i = 0;
					// 搜索云端是否存在该媒体
					for (i = 0; i < jsonArr.length(); i++) {
						JSONObject jsonObject = null;
						jsonObject = jsonArr.getJSONObject(i);
						if (m.get_id() == jsonObject.getInt("mediaId")) {
							Toast.makeText(MainActivity.this,
									m.getName() + "秒传成功", Toast.LENGTH_LONG).show();
							break;
						}
					}
					// 如果下面条件成立， 则表明云端存在该媒体， 跳过上传， 继续检测下一个媒体文件在云端存不存在
					if (i != jsonArr.length())
						continue;
				}
				/*
				 * 自增ID实现了多任务上传， 加入队列排队上传
				 */
				UploadTask task = new UploadTask(
						this, nm, ip, m, NOTIFICATION_UPLOAD_ID++);
				task.execute();

				boolean isExist = isAsyncToNetwork(m);
				if (!isExist) {
					mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
					SharedPreferences.Editor mEditor = mPerPreferences.edit();
					mEditor.putString(m.get_id().toString(), m.get_id().toString());
					mEditor.commit();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 同步所有网络媒体到本地
	 */
	public void asyncAllMedia2Local() {
		mPerPreferences = this.getSharedPreferences("ipaddress", MODE_PRIVATE);
		ip = mPerPreferences.getString("ip", null);
		if (ip == null || "".equals(ip)) {
			Toast.makeText(MainActivity.this,
					"请按Menu按钮设置网络服务器IP", Toast.LENGTH_LONG).show();
		}
		listMedia = new ArrayList<Media>();
		GetJsonDataService myTask = new GetJsonDataService(this, ip);
		// 获取doInBackground(String... params)返回的值， 即json数据
		try {
			/*if (jsonData == null) {
				// 获取doInBackground(String... params)返回的值， 即json数据
				jsonData = myTask.execute().get();
			}*/
			// 获取doInBackground(String... params)返回的值， 即json数据
			jsonData = myTask.execute().get();
			if (!jsonData.equals("nodata")) {
				JSONArray jsonArr = new JSONArray(jsonData);
				int i = 0;
				for (i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonObject = jsonArr.getJSONObject(i);
					String filePath = jsonObject.getString("mediaUri");
					String fileName = jsonObject.getString("mediaName");
					String fileId = jsonObject.getString("mediaId");
					filePath = "http://" + ip + ":8080/WebAndroid" + filePath;
					/*
					 * 自增ID实现多任务下载， 加入队列排队下载
					 */
					DownloadTask task = new DownloadTask(
							this, nm, fileName, NOTIFICATION_DOWNLOAD_ID++);
					task.execute(filePath);

					boolean isExist = isAsyncToLocal(fileId);
					if (!isExist) {
						mPerPreferences = this.getSharedPreferences("isAsyncToLocal", MODE_PRIVATE);
						SharedPreferences.Editor mEditor = mPerPreferences.edit();
						mEditor.putString(fileId, fileId);
						mEditor.commit();
					}
				}

			} else {
				Toast.makeText(MainActivity.this,
						"云上无媒体文件可同步", Toast.LENGTH_LONG).show();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 弹出对话框， 用来设置IP值
	 */
	@Override
	protected Dialog onCreateDialog(int id, Bundle args) {
		AlertDialog.Builder builder = new Builder(this);
		builder.setIcon(android.R.drawable.ic_dialog_info);
		builder.setTitle("网络设置选项");
		final LinearLayout ll = (LinearLayout) this.getLayoutInflater()
				.inflate(R.layout.ipsetting, null);
		builder.setView(ll);

		mPerPreferences = this.getSharedPreferences("ipaddress", MODE_PRIVATE);
		txtIP = (EditText) ll.findViewById(R.id.txt_ipaddress);
		ip = mPerPreferences.getString("ip", null);
		if (ip != null || "".equals(ip))
			txtIP.setText(ip);

		builder.setPositiveButton("确 定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ip = txtIP.getText().toString().trim();
				if ("".equals(ip) || null == ip) {
					dialog.dismiss();
					AlertDialog.Builder builder2 = new Builder(MainActivity.this);
					builder2.setTitle("提示信息");
					builder2.setMessage("你没有激活网络媒体服务， 不能将媒体同步。");
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
	 * 该媒体是否已同步到网络
	 * @param media
	 * @return
	 */
	private boolean isAsyncToNetwork(Media m) {
		mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
		boolean isExist = mPerPreferences.contains(m.get_id().toString());
		return isExist;
	}

	/**
	 * 该媒体是否已同步到本地
	 * @param media
	 * @return
	 */
	private boolean isAsyncToLocal(String mediaId) {
		mPerPreferences = this.getSharedPreferences("isAsyncToLocal", MODE_PRIVATE);
		boolean isExist = mPerPreferences.contains(mediaId);
		return isExist;
	}

	/**
	 * 任何的按键都是 onKeyDown() 先接收的
	 * 如果按的是 menu 键，应该返回 false ，表示让后面需要接收 menu 键的事件继续处理。
	 * 返回 true 就表示这个事件到我这里就完结了，返回false表示继续传递给后面想要接收这个事件的地方
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode) {  
		case KeyEvent.KEYCODE_MENU: 
			return false;
		case KeyEvent.KEYCODE_BACK:  
			long secondTime = System.currentTimeMillis();   
			if (secondTime - firstTime > 2000) {   //如果两次按键时间间隔大于2秒，则不退出  
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();   
				firstTime = secondTime;//更新firstTime  
				return true;   
			} else {
				//完全退出程序
				MyApplication.getInstance().exit();
			}   
			break;  
		}
		//return false;
		return super.onKeyDown(keyCode, event); 
	}
}
