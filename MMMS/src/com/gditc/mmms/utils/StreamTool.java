package com.gditc.mmms.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
/**
 * IO流工具类
 * @File StreamTool.java
 * @Package com.gditc.mmms.utils
 * @Description TODO
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:08:03
 * @author Cryhelyxx
 * @version 1.0
 */
public class StreamTool {
	/**
	 * 读取输入流数据
	 * @param inStream
	 * @return
	 */
	public static byte[] read(InputStream inStream) throws Exception{
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while( (len = inStream.read(buffer)) != -1 ){
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}
}
