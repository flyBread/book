package com.zlz.bug.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;

import org.apache.commons.io.output.StringBuilderWriter;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhailz
 *
 *         时间：2016年8月23日 ### 上午8:59:07
 * 
 *         判断机制
 */
public class ToolUtil {

	private static Logger logger = LoggerFactory.getLogger(ToolUtil.class);

	@SuppressWarnings("resource")
	public static String readFileToString(File file, String encode) throws Exception {
		InputStream in = null;
		if (file.exists()) {
			if (file.isDirectory()) {
				throw new IOException("File '" + file + "' exists but is a directory");
			}
			if (file.canRead() == false) {
				throw new IOException("File '" + file + "' cannot be read");
			}
		} else {
			throw new FileNotFoundException("File '" + file + "' does not exist");
		}
		in = new FileInputStream(file);
		StringBuilderWriter sw = new StringBuilderWriter();
		InputStreamReader inreader = new InputStreamReader(in, encode);
		char[] buffer = new char[1024 * 4];
		long count = 0;
		int n = 0;
		while (-1 != (n = inreader.read(buffer))) {
			sw.write(buffer, 0, n);
			count += n;
		}
		logger.info("读取的长度是：{}", count);
		return sw.toString();

	}

	// 判断网站是否是正版的软件
	public static boolean isProfessionalWeb(String webName) {
		if (webName != null && webName.length() > 0) {
			return webName.contains("起点中文网") || webName.contains("创世中文网");
		}
		return false;
	}

	public static long convertTime(String time) throws ParseException {
		long timel = 0l;
		String[] vas = time.split(" : ");
		Date date = DateUtils.parseDate(vas[1], BCons.TimeFormate);
		if (date != null) {
			timel = date.getTime();
		}
		return timel;
	}

	public static void main(String[] args) throws ParseException {
		String value = "更新时间 : 2016-08-22 18:44:02";
		System.out.println(convertTime(value));
	}

}
