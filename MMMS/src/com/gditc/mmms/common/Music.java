package com.gditc.mmms.common;
/**
 * 音频--实体类
 * @File Music.java
 * @Package com.gditc.mmms.common
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:05:13
 * @author Cryhelyxx
 * @version 1.0
 */
public class Music extends Media {

	private String singer;
	private String album;
	/**
	 * @return the singer
	 */
	public String getSinger() {
		return singer;
	}
	/**
	 * @param singer the singer to set
	 */
	public void setSinger(String singer) {
		this.singer = singer;
	}
	/**
	 * @return the album
	 */
	public String getAlbum() {
		return album;
	}
	/**
	 * @param album the album to set
	 */
	public void setAlbum(String album) {
		this.album = album;
	}
}