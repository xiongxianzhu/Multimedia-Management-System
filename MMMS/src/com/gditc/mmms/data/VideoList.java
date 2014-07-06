package com.gditc.mmms.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.gditc.mmms.common.Media;
import com.gditc.mmms.common.Video;
/**
 * 视频列表
 * @File VideoList.java
 * @Package com.gditc.mmms.data
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:06:58
 * @author Cryhelyxx
 * @version 1.0
 */
public class VideoList {

	public static List<Video> getVideoData(Context context) {
		List<Video> videoList = new ArrayList<Video>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有视频
			Cursor cursor = cr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
					null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
			if (null == cursor)
				return null;
			if (cursor.moveToFirst()) {
				do {
					Video video = new Video();
					Long _id = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Video.Media._ID));
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.TITLE));
					Long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Video.Media.SIZE));
					String url = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.DATA));
					Long time = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Video.Media.DURATION));
					String name = cursor.getString(cursor
							.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
					/*String subName = name.substring(name.length() - 3,
							name.length());*/
					
					video.set_id(_id);
					video.setMediaType(Media.VIDEO);
					video.setTitle(title);
					video.setSize(size);
					video.setTime(time);
					video.setUri(url);
					video.setName(name);
					videoList.add(video);
				} while (cursor.moveToNext());
			}
		}
		return videoList;
	}
}
