package com.gditc.mmms.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.gditc.mmms.common.Image;
import com.gditc.mmms.common.Media;
/**
 * 图片列表
 * @File ImageList.java
 * @Package com.gditc.mmms.data
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:06:19
 * @author Cryhelyxx
 * @version 1.0
 */
public class ImageList {

	public static List<Image> getImageDate(Context context) {
		List<Image> imageList = new ArrayList<Image>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有图片
			Cursor cursor = cr.query(
					MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null,
					null, null, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
			if (null == cursor)
				return null;
			if (cursor.moveToFirst()){
				do {
					Image image = new Image();
					Long _id = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Images.Media._ID));
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.TITLE));
					Long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Images.Media.SIZE));
					Long time = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN));
					String url = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					String name = cursor.getString(cursor
							.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));

					image.set_id(_id);
					image.setMediaType(Media.IMAGE);
					image.setTitle(title);
					image.setSize(size);
					image.setTime(time);
					image.setUri(url);
					image.setName(name);
					imageList.add(image);
				} while (cursor.moveToNext());
			}
		}
		return imageList;
	}
}
