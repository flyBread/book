package com.zlz.bug.test;

import com.zlz.bug.utils.BookUtil;

/**
 * @author zhailz
 *
 *         时间：2016年8月4日 ### 上午9:17:32
 */
public class DataModelTest {

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		// String pathname = "/Users/zhailz/Documents/数据存储/txt";
		// File file = new File(pathname, "houweidongguanchangbiji.txt");
		// if (!file.exists()) {
		// file.createNewFile();
		// }
		// String contentsUrl = "http://www.mossiella.com/";
		// BookUtil.storeBookByContentUrl(file, contentsUrl);

		BookUtil.getContentsUrl("修真界移民");
	}

}
