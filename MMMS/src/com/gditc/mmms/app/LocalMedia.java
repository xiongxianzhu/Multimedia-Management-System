package com.gditc.mmms.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
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
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.gditc.mmms.R;
import com.gditc.mmms.common.Image;
import com.gditc.mmms.common.Media;
import com.gditc.mmms.common.MediaAdapter;
import com.gditc.mmms.common.Music;
import com.gditc.mmms.common.Video;
import com.gditc.mmms.data.ImageList;
import com.gditc.mmms.data.MusicList;
import com.gditc.mmms.data.VideoList;
import com.gditc.mmms.service.GetJsonDataService;
import com.gditc.mmms.service.UploadTask;
import com.gditc.mmms.utils.MyApplication;
/**
 * 本地媒体界面
 * @File LocalMedia.java
 * @Package com.gditc.mmms.app
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:01:21
 * @author Cryhelyxx
 * @version 1.0
 */
public class LocalMedia extends Activity {

	private static int NOTIFICATION_UPLOAD_ID = 0x123;

	private GridView gv = null;
	public static List<Media> listMedia = null;
	private List<Music> listMusic = null;
	private List<Image> listImage = null;
	private List<Video> listVideo = null;
	private MediaAdapter adapter = null;

	private NotificationManager nm = null;

	public static String ip = null;
	private SharedPreferences mPerPreferences = null;
	private static String jsonData = null;

	//private long firstTime = 0; 


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.local_media);
		MyApplication.getInstance().addActivity(this);
		loadingFormation();
		initData();
	}

	/**
	 * 加载组件
	 */
	private void loadingFormation() {
		gv = (GridView) findViewById(R.id.local_gridview);
		// 获取系统的NotificationManager服务
		nm = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
		//bar = (ProgressBar) this.findViewById(R.id.progressBar01);
	}

	/**
	 * 初始化数据
	 */
	private void initData() {
		listMedia = new ArrayList<Media>();
		listMedia = getLocalMediaList();

		adapter = new MediaAdapter(this, listMedia);
		gv.setAdapter(adapter);
		this.registerForContextMenu(gv);	// 注册上下文菜单 
		gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				playMedia(listMedia.get(position));
			}
		});
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
	 * 创建上下文菜单
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		MenuInflater inflator = new MenuInflater(this);
		// 设置上下文标题
		menu.setHeaderTitle("操作该媒体");
		// 装填R.menu.context对应的菜单， 并添加到menu中
		inflator.inflate(R.menu.context01, menu);
	}

	/**
	 * 上下文菜单被选中事件监听
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.item_play01:
			playMedia(listMedia.get(menuInfo.position));
			break;

		case R.id.item_infoDetail01:
			lookMediaInfoDetail(listMedia.get(menuInfo.position));
			break;

		case R.id.item_asyncToNetwork01:
			syncMediaToNetWork(listMedia.get(menuInfo.position));

			break;

		case R.id.item_delete01:
			deleteMedia(listMedia.get(menuInfo.position));
			break;

		}
		return true;
	}

	/**
	 * 播放媒体
	 * @param media 媒体文件
	 */
	private void playMedia(Media media) {
		String path = media.getUri();
		Integer type = media.getMediaType();
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri uri = Uri.parse("file://" + path);
		if (type == Media.IMAGE)
			intent.setDataAndType(uri, "image/*");
		else if (type == Media.MUSIC)
			intent.setDataAndType(uri, "audio/*");
		else if (type == Media.VIDEO)
			intent.setDataAndType(uri, "video/*");
		//getApplicationContext().startActivity(intent);
		LocalMedia.this.startActivity(intent);
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

		boolean isExist = isAsyncToNetwork(media);
		String isAsync = "否";
		if (isExist)
			isAsync = "是";

		builder.setMessage(
				"媒体名称： " + title + 
				"\n媒体类型： " + mediaType +
				"\n媒体大小： " + s +
				"\n创建日期： " + t1 +
				"\n创建时间： " + t2 +
				"\n是否已同步到网络： " + isAsync);
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
	 * 同步媒体到网络
	 * @param media 媒体文件
	 * @throws FileNotFoundException 
	 */
	private void syncMediaToNetWork(Media media) {
		ip = getIPAddress();
		if (ip == null || "".equals(ip)) {
			Toast.makeText(getApplicationContext(),
					"请按Menu按钮设置网络服务器IP", Toast.LENGTH_LONG).show();
		}
		GetJsonDataService myTask = new GetJsonDataService(this, ip);
		JSONArray jsonArr = null;
		try {
			/*if (jsonData == null || jsonData.equals("nodata")) {
				// 获取doInBackground(String... params)返回的值， 即json数据
				jsonData = myTask.execute().get();
			}*/
			
			// 获取doInBackground(String... params)返回的值， 即json数据
			jsonData = myTask.execute().get();
			if (!jsonData.equals("nodata")) {
				jsonArr = new JSONArray(jsonData);
				int i = 0;
				// 搜索云端是否存在该媒体
				for (i = 0; i < jsonArr.length(); i++) {
					JSONObject jsonObject = null;
					jsonObject = jsonArr.getJSONObject(i);
					if (media.get_id() == jsonObject.getInt("mediaId")) {
						Toast.makeText(LocalMedia.this,
								media.getName() + "成功秒传至云端", Toast.LENGTH_LONG).show();

						boolean isExist = isAsyncToNetwork(media);
						if (!isExist) {
							mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
							SharedPreferences.Editor mEditor = mPerPreferences.edit();
							mEditor.putString(media.get_id().toString(), media.get_id().toString());
							mEditor.commit();
						}

						break;
					}
				}
				// 如果下面条件成立， 表示在云端文件日志上全部遍历完， 仍找不到该媒体文件， 表明云端不存在该媒体，则执行上传该媒体文件任务
				if (i == jsonArr.length()) {
					/*
					 * 自增ID实现了多任务上传， 加入队列排队上传
					 */
					UploadTask task = new UploadTask(this, nm, ip, media, NOTIFICATION_UPLOAD_ID++);
					task.execute();

					boolean isExist = isAsyncToNetwork(media);
					if (!isExist) {
						mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
						SharedPreferences.Editor mEditor = mPerPreferences.edit();
						mEditor.putString(media.get_id().toString(), media.get_id().toString());
						mEditor.commit();
					}
				}
			} else {
				mPerPreferences = this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
				SharedPreferences.Editor mEditor = mPerPreferences.edit();
				mEditor.clear();
				mEditor.commit();
				
				/*
				 * 自增ID实现了多任务上传， 加入队列排队上传
				 */
				UploadTask task = new UploadTask(this, nm, ip, media, NOTIFICATION_UPLOAD_ID++);
				task.execute();

				boolean isExist = isAsyncToNetwork(media);
				if (!isExist) {
					mEditor.putString(media.get_id().toString(), media.get_id().toString());
					mEditor.commit();
				}
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
	 * 获取IP地址
	 * @return
	 */
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
					AlertDialog.Builder builder2 = new Builder(LocalMedia.this);
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
	 * 删除媒体
	 * @param media 媒体文件
	 */
	private void deleteMedia(Media media) {
		final Media m = media;
		final String path = media.getUri();	// 文件的路径
		final Long id = media.get_id();		// 媒体的id
		final Integer type = media.getMediaType();
		//final Uri uri = Uri.parse("content://" + path);
		//final Uri uri = Uri.parse("file://" + path);


		AlertDialog.Builder builder = new Builder(LocalMedia.this);
		builder.setTitle("提示信息");
		builder.setMessage("确定要删除该媒体文件吗？");
		builder.setPositiveButton("确 定", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (type == Media.IMAGE) {
					getContentResolver().delete(
							MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
							MediaStore.Images.Media._ID + "=" + id, null);
				} else if (type == Media.MUSIC) {
					/*
					 * 删除音频文件比较特殊， 在界面上消失和物理磁盘上消失
					 */
					// 界面上删除
					getContentResolver().delete(
							MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
							MediaStore.Audio.Media._ID + "=" + id, null);
					// 物理磁盘上删除
					File file = new File(path);
					if (file.exists()) {
						file.delete();
					}
					/*Uri deleteIdUri = ContentUris.withAppendedId(uri, id);
					getContentResolver().delete(deleteIdUri, null, null);*/

				} else if (type == Media.VIDEO) {
					getContentResolver().delete(
							MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
							MediaStore.Video.Media._ID + "=" + id, null);
				}
				refresh();		// 刷新界面
				// 在isAsyncToNetwork.xml文件中移除该媒体的信息
				boolean isExist = isAsyncToNetwork(m);
				if (isExist) {
					mPerPreferences = LocalMedia.this.getSharedPreferences("isAsyncToNetwork", MODE_PRIVATE);
					SharedPreferences.Editor mEditor = mPerPreferences.edit();
					mEditor.remove(m.get_id().toString());
					mEditor.commit();
				}

				Toast.makeText(getApplicationContext(),
						"删除成功", Toast.LENGTH_LONG).show();
			}
		});
		builder.setNegativeButton("取 消", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create();
		builder.show();
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
	 * 刷新界面
	 */
	public void refresh() {
		initData();
	}
}
