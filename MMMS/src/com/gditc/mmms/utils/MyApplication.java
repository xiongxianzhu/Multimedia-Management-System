package com.gditc.mmms.utils;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * 实现完全退出程序工具类
 * @File MyApplication.java
 * @Package com.gditc.mmms.utils
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:07:22
 * @author Cryhelyxx
 * @version 1.0
 */
public class MyApplication extends Application{

	private List<Activity> activityList = new LinkedList<Activity>();
	private static MyApplication instance;

	public MyApplication() {
		super();
	}

	/**
	 * 单例模式中获取唯一的MyApplication实例
	 * @return
	 */
	public static MyApplication getInstance() {
		if (null == instance) {
			instance = new MyApplication();
		}
		return instance;
	}

	/**
	 * 添加Activity到activityList中
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 遍历所有Activity并finish
	 */
	public void exit() {
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}

	/**
	 * 过滤掉不安全的字符
	 * @param str
	 * @return
	 */
	public String FilteSQLStr(String str){
		String s = str.replace("'", "");
		
		return s; 
	}
}
