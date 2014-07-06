package com.gditc.mmms.app;

import java.util.concurrent.ExecutionException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;

import com.gditc.mmms.R;
import com.gditc.mmms.service.GetNetworkImageTask;
import com.gditc.mmms.utils.MyApplication;
/**
 * 查看网络图片
 * @File LookNetworkImage.java
 * @Package com.gditc.mmms.app
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月18日 下午6:13:01
 * @author Cryhelyxx
 * @version 1.0
 */
public class LookNetworkImageActivity extends Activity {
	
	private ImageView imgView = null;
	private String filePath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.look_network_image);
		// 设置界面标题
		this.setTitle("查看网络图片");
		MyApplication.getInstance().addActivity(this);
		loadinFormation();
		initData();
	}

	private void loadinFormation() {
		imgView = (ImageView) this.findViewById(R.id.networkImage);
	}

	private void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getBundleExtra("data");
		filePath = bundle.getString("imagePath");
		GetNetworkImageTask task = new GetNetworkImageTask(this);
		byte[] imageResult = new byte[]{};
		try {
			imageResult = task.execute(filePath).get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		Bitmap bitmap = BitmapFactory.decodeByteArray(
				imageResult, 0, imageResult.length);
		imgView.setImageBitmap(bitmap);
	}

	
}
