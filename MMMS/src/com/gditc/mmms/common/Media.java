package com.gditc.mmms.common;
/**
 * 媒体实体类
 * @File Media.java
 * @Package com.gditc.mmms.common
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:04:35
 * @author Cryhelyxx
 * @version 1.0
 */
public class Media {
	
	public static final Integer MUSIC = 1;
	public static final Integer IMAGE = 2;
	public static final Integer VIDEO = 3;
	private Long _id;
	private String title;
	private String name;
	private String uri;
	private Long size;
	private Long time;
	private Integer mediaType;
	/**
	 * @return the _id
	 */
	public Long get_id() {
		return _id;
	}
	/**
	 * @param _id the _id to set
	 */
	public void set_id(Long _id) {
		this._id = _id;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the uri
	 */
	public String getUri() {
		return uri;
	}
	/**
	 * @param uri the uri to set
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}
	/**
	 * @return the size
	 */
	public Long getSize() {
		return size;
	}
	/**
	 * @param size the size to set
	 */
	public void setSize(Long size) {
		this.size = size;
	}
	/**
	 * @return the time
	 */
	public Long getTime() {
		return time;
	}
	/**
	 * @param time the time to set
	 */
	public void setTime(Long time) {
		this.time = time;
	}
	/**
	 * @return the mediaType
	 */
	public Integer getMediaType() {
		return mediaType;
	}
	/**
	 * @param mediaType the mediaType to set
	 */
	public void setMediaType(Integer mediaType) {
		this.mediaType = mediaType;
	}
	/**
	 * @return the music
	 */
	public static Integer getMusic() {
		return MUSIC;
	}
	/**
	 * @return the image
	 */
	public static Integer getImage() {
		return IMAGE;
	}
	/**
	 * @return the video
	 */
	public static Integer getVideo() {
		return VIDEO;
	}
	
}
