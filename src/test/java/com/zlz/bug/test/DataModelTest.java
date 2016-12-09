package com.zlz.bug.test;

import org.json.JSONObject;

import com.mail.SendMail;
import com.search.BookSearchUtil;
import com.swingUI.utils.BookUtil;

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
	  String bookName = "永恒剑主";
		JSONObject value = BookSearchUtil.getNewestChapterByName(bookName);
		System.out.println(value);
		SendMail.getInstance().sendMail(bookName+" 准备好了", value.toString(), "zlztodolist@126.com");
	}

}
