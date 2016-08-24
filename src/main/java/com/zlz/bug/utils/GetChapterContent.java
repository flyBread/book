package com.zlz.bug.utils;

import java.util.concurrent.ArrayBlockingQueue;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zlz.bug.ContentsRegularExpression;
import com.zlz.bug.ContentsData.HtmlContentPage;
import com.zlz.bug.ContentsData.Node;
import com.zlz.bug.data.DataModel;

/**
 * @author zhailz
 *
 *         时间：2016年8月23日 ### 下午7:27:38
 */
public class GetChapterContent extends Thread {

	String href;
	ArrayBlockingQueue<String> contents;
	String chapterName;

	public GetChapterContent(String href, ArrayBlockingQueue<String> contents, String chapterName) {
		this.href = href;
		this.contents = contents;
		this.chapterName = chapterName;
	}

	@Override
	public void start() {
		if (contents.isEmpty()) {
			ContentsRegularExpression express = new ContentsRegularExpression();
			try {
				HtmlPage pages = DataModel.getInstance().getPageByUrlScriptEnabled(href);
				String pagesxml = pages.asXml();
				String[] name = chapterName.split("[ ]");
				String shortName = name[name.length - 1];
				if (pagesxml.contains(chapterName) || pagesxml.contains(shortName)) {
					HtmlContentPage pagesContent = DataModel.getInstance().getContentsData(href, express);
					if (pagesContent.getNodepages() != null && !pagesContent.getNodepages().isEmpty()) {
						for (Node node : pagesContent.getNodepages()) {
							if (node.atext.contains(shortName)) {
								String newesturl = node.url;
								String content = DataModel.getInstance().getFormateData(newesturl);
								if (content != null) {
									contents.add(content);
									break;
								}
							}
						}
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
