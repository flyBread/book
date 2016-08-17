package com.zlz.bug.test;

import java.io.File;

import com.zlz.bug.ContentsRegularExpression;
import com.zlz.bug.ContentsData.HtmlContentPage;
import com.zlz.bug.data.DataModel;

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
		DataModel model = DataModel.getInstance();

		// //首先就是搜索，然后找到文本的目录
		ContentsRegularExpression express = new ContentsRegularExpression();
		HtmlContentPage get = DataModel.getInstance()
				.getContentsData("http://tianyibook.com/tianyibook/17/17496/index.html", express);
		System.out.println(get);

		// 得到具体的消息
		String value = model.getFormateData("http://www.mossiella.com/");
		System.out.println(value);

		String pathname = "/Users/zhailz/Documents/数据存储/txt";
		File file = new File(pathname, "chongshengguatou1900.txt");

		// 保存到
		model.saveFormateValueToFile(file, value);

	}

}
