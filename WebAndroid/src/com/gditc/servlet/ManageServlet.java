package com.gditc.servlet;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.gditc.bean.Media;
import com.gditc.service.FileLogService;

/**
 * 
 * @ClassName: ManageServlet.java
 * @Package com.gditc.servlet
 * @Description: TODO
 * @Copyright: Copyright © 2014
 * @Company: 广东轻工职业技术学院计算机工程系软件121
 * @author Cryhelyxx
 * @date 2014年5月31日 下午5:55:27
 * @version V1.0
 */
public class ManageServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String mediaName = request.getParameter("mediaName");		
		String mediaType = request.getParameter("mediaType");
		System.out.println("文件名称："+ mediaName);
		System.out.println("文件类型："+ mediaType);
	}

	@SuppressWarnings("deprecation")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 检测我们是否一个文件上传的请求
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		
		Map<String, Object> map = null;
		Media media = new Media();
		
		if (isMultipart) {
			try {
				// 创建FileItem工厂, 创建磁盘工厂(多态)
				FileItemFactory factory = new DiskFileItemFactory(); 
				// 核心对象, 创建处理工具
				ServletFileUpload upload = new ServletFileUpload(factory);
				// 解决上传文件的路径乱码设置
				upload.setHeaderEncoding("UTF-8");
				// 解析, 读取Request中包含的文件
				List<FileItem> items = upload.parseRequest(request);
				// 获取文件存放的真实路径
				String dir = request.getSession().getServletContext().getRealPath("/MMMS_files");
				// 磁盘物理路径
				// String dir = "D:/MMMS_files";
				File dirFile = new File(dir);
				// 如果该文件夹不存在， 则创建文件夹， 文件夹名为files
				if (!dirFile.exists())
					dirFile.mkdirs();
				System.out.println("==================下面是客户端传递过来的参数信息===========");
				// 迭代
				for (FileItem item : items) {
					
					if(item.isFormField()) { 	//所接收的表单域， 如果文本类型参数
						String name = item.getFieldName();
						// 解决表单域字段的值的乱码
						String value = item.getString("UTF-8");
						System.out.println(name+ "="+ value);
						if (name.equals("mediaId"))
							media.set_id(Long.valueOf(value));
						if (name.equals("mediaTitle"))
							media.setTitle(value);
						if (name.equals("mediaTime"))
							media.setTime(Long.valueOf(value));
						if (name.equals("mediaType"))
							media.setMediaType(Integer.valueOf(value));
						
					}else{  					//如果文件类型参数
						System.out.println("==================下面是服务端保存文件后的信息============");
						/*
						 * 把网址以UrlEncode编码/UrlDecode解码:
						 * http://tool.chinaz.com/Tools/URLEncode.aspx 
						 */
						/*File saveFile = new File(dirFile, URLEncoder.encode(item.getName(), "UTF-8"));	// 新建目标文件
						if (!saveFile.exists()) {
							// 将Item的内容写入
							item.write(saveFile);
						}*/
						/**
						 * UUID是1.5中新增的一个类，在java.util下，用它可以产生一个号称全球唯一的ID
						 */
						UUID uuid = UUID.randomUUID();
						String fileFormat = item.getContentType().split(";")[0];
						String fileName = uuid.toString() + "." + fileFormat;
						File saveFile = new File(dirFile, fileName);	// 新建目标文件
						if (!saveFile.exists()) {
							// 将Item的内容写入
							item.write(saveFile);
						}
						
						System.out.println("文件存放目录：" + dir); // 文件存放路径
						System.out.println("文件名：" + fileName);	// 文件名
						System.out.println("文件后缀|编码集：" + item.getContentType());	// 文件类型及编码集， 如mp3; charset=UTF-8
						System.out.println("文件大小：" + item.getSize());		// 文件大小， 单位bytes
						
						map = new HashMap<String, Object>();
						map.put("mediaId", media.get_id());
						map.put("mediaTitle", media.getTitle());
						map.put("mediaName", item.getName());
						// 保证媒体在android客户端能正常播放， 需进行两次UrlEncoder编码
						map.put("mediaUri", "/MMMS_files/" + URLEncoder.encode(fileName));
						map.put("mediaType", media.getMediaType());
						map.put("mediaSize", item.getSize());
						map.put("mediaTime", media.getTime());
						map.put("fileName", fileName);
						// 文件在物理磁盘上的路径
						map.put("filePath", dir + "/" + fileName);
						
						media.setName(item.getName());
						media.setUri(map.get("mediaUri").toString());
						media.setSize(item.getSize());
						
						FileLogService service = new FileLogService(map, dir);
						service.writeToJson();
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			this.doGet(request, response);
		}
	}
}
