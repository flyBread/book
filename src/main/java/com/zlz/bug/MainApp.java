package com.zlz.bug;

import com.zlz.bug.utils.BookUtil;

/**
 * @author zhailz
 *
 *         时间：2016年8月24日 ### 下午1:40:02
 * 
 *         入口类
 */
public class MainApp {

	public static void main(String[] args) throws Exception {
		MainApp app = new MainApp();
		app.run();
	}

	private void run() throws Exception {
		BookUtil.downLoad("烽皇");

		// // 首先发起的流程，我需要知道我关注的是什么
		// List<String> bookNames = BookUtil.personFofusBookNames();
		// if (bookNames != null && bookNames.size() > 0) {
		// for (String string : bookNames) {
		// // 得到最新的章节的内容
		// BookUtil.getNewestChapter(string);
		// }
		// }
	}
}
