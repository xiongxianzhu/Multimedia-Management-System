package com.gditc.mmms.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import com.gditc.mmms.common.Media;
import com.gditc.mmms.common.Music;
/**
 * 音频列表
 * @File MusicList.java
 * @Package com.gditc.mmms.data
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:06:35
 * @author Cryhelyxx
 * @version 1.0
 */
public class MusicList {

	public static List<Music> getMusicData(Context context) {
		List<Music> musicList = new ArrayList<Music>();
		ContentResolver cr = context.getContentResolver();
		if (cr != null) {
			// 获取所有歌曲
			Cursor cursor = cr.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
					null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
			if (null == cursor)
				return null;
			if (cursor.moveToFirst()){
				do {
					Music music = new Music();
					Long _id = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media._ID));
					String title = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.TITLE));
					String singer = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ARTIST));
					if ("<unknown>".equals(singer))
						singer = "未知艺术家";
					String album = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.ALBUM));
					Long size = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.SIZE));
					Long time = cursor.getLong(cursor
							.getColumnIndex(MediaStore.Audio.Media.DURATION));
					String url = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DATA));
					String name = cursor.getString(cursor
							.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
					/*String subName = name.substring(name.length() - 3,
							name.length());*/
					
					music.set_id(_id);
					music.setMediaType(Media.MUSIC);
					music.setTitle(title);
					music.setSinger(singer);
					music.setAlbum(album);
					music.setSize(size);
					music.setTime(time);
					music.setUri(url);
					music.setName(name);
					musicList.add(music);
				} while (cursor.moveToNext());
			}
		}
		return musicList;
	}
}
