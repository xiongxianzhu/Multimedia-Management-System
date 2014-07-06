package com.gditc.mmms.common;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gditc.mmms.R;
/**
 * 媒体适配器
 * @File MediaAdapter.java
 * @Package com.gditc.mmms.common
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:04:55
 * @author Cryhelyxx
 * @version 1.0
 */
public class MediaAdapter extends BaseAdapter {
	
	private Context context;
	private List<Media> listMedia;
	
	public MediaAdapter(Context context, List<Media> listMedia) {
		super();
		this.context = context;
		this.listMedia = listMedia;
	}

	@Override
	public int getCount() {
		return listMedia.size();
	}

	@Override
	public Object getItem(int arg0) {
		return listMedia.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		return ((Media) listMedia.get(position)).get_id().longValue();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(context).inflate(
					R.layout.media_item, null);
		}
		Media m = listMedia.get(position);
		ImageView iv = (ImageView) convertView.findViewById(R.id.image);
		if (m.getMediaType() == Media.MUSIC)
			iv.setBackgroundResource(R.drawable.file_audio);
		else if (m.getMediaType() == Media.IMAGE)
			iv.setBackgroundResource(R.drawable.file_image);
		else if (m.getMediaType() == Media.VIDEO)
			iv.setBackgroundResource(R.drawable.file_video);
		// 媒体名
		TextView textMusicName = (TextView) convertView
				.findViewById(R.id.media_item_title);
		textMusicName.setText(m.getTitle());
		return convertView;
	}
	
	public String toTime(Long time) {
		time /= 1000;
		int minute = time.intValue() / 60;
		//int hour = minute / 60;
		int second = time.intValue() % 60;
		minute %= 60;
		return String.format("%02d:%02d", minute, second);
	}

}
