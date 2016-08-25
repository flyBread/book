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
		// 首先发起的流程，我需要知道我关注的是什么
		String[] bookNames = BookUtil.personFofusBookNames();
		if (bookNames != null && bookNames.length > 0) {
			for (String string : bookNames) {
				BookUtil.getNewestChapter(string);
			}
		}
	}
}
