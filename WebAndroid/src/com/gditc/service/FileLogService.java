package com.gditc.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import com.gditc.dao.FileDao;

public class FileLogService {

	private Map<String, Object> map = new HashMap<String, Object>();

	private static final String JSONFILE = "files_log.json";
	private String dir = null;
	
	public FileLogService() {
		super();
	}

	public FileLogService(Map<String, Object> map, String dir) {
		this.map = map;
		this.dir = dir;
	}

	/**
	 * 往json文件中写入数据
	 * @throws Exception
	 */
	public void writeToJson() throws Exception {
		// json对象 {}
		JSONObject jsonObject = JSONObject.fromObject(map);
		//json对象：{"dir":"G:\\MMMS_files","name":"高进 - 我多想抱着你哭.mp3","contentType":"mp3; charset=UTF-8","type":"mp3","size":10593584}
		System.out.println("JSON对象" +jsonObject); 
		FileDao dao = new FileDao();
		String jsonPath = dir + File.separator + JSONFILE;
		if (dao.getJsonPath() == null) {
			dao.addJsonPath(jsonPath);
		} else if (!jsonPath.equals(dao.getJsonPath())) {
			dao.updateJsonPath(jsonPath);
		}
		String jsonArr = getJsonArray(jsonPath);
		if (jsonArr == null) {
			writeTo(jsonPath, "[" + jsonObject.toString() + "]");
		} else {
			StringBuilder builder = new StringBuilder();
			builder.append(jsonArr);
			builder.deleteCharAt(builder.length() - 1);		// 删除 "]"
			builder.append(",");							// 追加 ",", 对象间的分隔用","
			builder.append(jsonObject.toString() + "]");
			writeTo(jsonPath, builder.toString());
		}
	}

	/**
	 * 从json文件中读取数据
	 * @param fileDir
	 * @param fileName
	 * @return 
	 * @throws Exception
	 */
	public String getJsonArray(String jsonPath) throws Exception {
		// 声明File对象
		File f = new File(jsonPath);
		if (!f.exists())
			f.createNewFile();
		// 通过子类实例化父类对象(多态)
		Reader reader = null;
		reader = new FileReader(f);		// 使用FileReader() 解决了读取文件内容出现中文乱码的问题
		String jsonArr = "";
		int len = 0;
		// 读取文件的内容
		while (-1 != (len = reader.read())) {
			jsonArr += (char) len;
		}
		reader.close();
		if ("".equals(jsonArr)) {
			return null;
		} else {
			return jsonArr;
		}
	}

	/**
	 * 往文件中写入数据
	 * @param fileDir
	 * @param fileName
	 * @param content
	 * @throws Exception
	 */
	public void writeTo(String jsonPath, String content) throws Exception {
		// 声明File对象
		File f = new File(jsonPath);
		// 通过子类实例化父类对象(多态)
		OutputStream out = null;				// 准备好一个输入的对象
		//out = new FileOutputStream(filePath, true); 	// 此处表示在文件末尾追加内容
		out = new FileOutputStream(f);
		// 只能输出byte数组, 所以将字符串变为byte数组
		byte[] b = content.getBytes();
		out.write(b);							// 写入内容
		out.close();							// 关闭输出流
	}
}
