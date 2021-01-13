package com.example.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author test
 */
public class FileUtils {
	public void downLoad(String path){
		HttpServletResponse response= ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
		try {
			// path： 欲下载的文件的路径
			File file = new File(path);
			// 获取文件名 - 设置字符集
			String downloadFileName = new String(file.getName().getBytes(StandardCharsets.UTF_8), "iso-8859-1");
			// 以流的形式下载文件
			InputStream fis;
			fis = new BufferedInputStream(new FileInputStream(path));
			byte[] buffer = new byte[fis.available()];
			fis.read(buffer);
			fis.close();
			// 清空response
			assert response != null;
			response.reset();
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
			response.addHeader("Content-Length", "" + file.length());
			OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			toClient.write(buffer);
			toClient.flush();
			toClient.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
