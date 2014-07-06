package com.gditc.mmms.service;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
/**
 * 自定义MultiPartEntity
 * @File CustomMultiPartEntity.java
 * @Package com.gditc.mmms.service
 * @Description 目的是实现文件传输的进度条的百分比和解决参数及文件名乱码问题
 * @Copyright Copyright © 2014
 * @Site https://github.com/Cryhelyxx
 * @Blog http://blog.csdn.net/Cryhelyxx
 * @Email cryhelyxx@gmail.com
 * @Company 广东轻工职业技术学院计算机工程系
 * @Date 2014年6月17日 下午5:03:06
 * @author Cryhelyxx
 * @version 1.0
 */
public class CustomMultiPartEntity extends MultipartEntity {

	private final ProgressListener listener;

	public CustomMultiPartEntity(final ProgressListener listener) {
		super();
		this.listener = listener;
	}

	public CustomMultiPartEntity(final HttpMultipartMode mode, final ProgressListener listener) {
		super(mode);
		this.listener = listener;
	}

	public CustomMultiPartEntity(HttpMultipartMode mode, final String boundary, final Charset charset, final ProgressListener listener) {
		super(mode, boundary, charset);
		this.listener = listener;
	}

	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		super.writeTo(new CountingOutputStream(outstream, this.listener));
	}

	public static interface ProgressListener {
		void transferred(long num);
	}

	public static class CountingOutputStream extends FilterOutputStream {

		private final ProgressListener listener;
		private long transferred;

		public CountingOutputStream(final OutputStream out, final ProgressListener listener) {
			super(out);
			this.listener = listener;
			this.transferred = 0;
		}

		public void write(byte[] b, int off, int len) throws IOException {
			out.write(b, off, len);
			this.transferred += len;
			this.listener.transferred(this.transferred);
		}

		public void write(int b) throws IOException {
			out.write(b);
			this.transferred++;
			this.listener.transferred(this.transferred);
		}
	}

	/**
	 * 重置编码集
	 */
	@Override
	protected String generateContentType(String boundary, Charset charset) {
		 StringBuilder buffer = new StringBuilder();
	        buffer.append("multipart/form-data");
	        if (charset != null) {
	            buffer.append("; charset=");
	            buffer.append(charset.name());
	        }
	        buffer.append(";boundary=");
	        buffer.append(boundary);
	        return buffer.toString();
	}
}
