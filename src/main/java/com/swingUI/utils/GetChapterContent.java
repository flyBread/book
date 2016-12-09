package com.swingUI.utils;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.swingUI.ContentsRegularExpression;
import com.swingUI.ContentsData.HtmlContentPage;
import com.swingUI.ContentsData.Node;
import com.swingUI.data.DataModel;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @author zhailz
 *
 *         时间：2016年8月23日 ### 下午7:27:38
 */
public class GetChapterContent implements Runnable {

	String href;
	ArrayBlockingQueue<String> contents;
	String chapterName;
	Logger logger = LoggerFactory.getLogger(GetChapterContent.class);

	public GetChapterContent(String href, ArrayBlockingQueue<String> contents, String chapterName) {
		this.href = href;
		this.contents = contents;
		this.chapterName = chapterName;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		if (contents.isEmpty()) {
			ContentsRegularExpression express = new ContentsRegularExpression();
			try {
				HtmlPage pages = DataModel.getInstance().getPageByUrlScriptEnabled(href);
				String pagesxml = pages.asXml();
				String[] name = chapterName.split("[ ]");
				String shortName = name[name.length - 1];

				// 如果一个页面匹配的过多，说明也不是什么好事
				if (StringUtils.countMatches(pagesxml, shortName) > 5) {
					return;
				}
				if (pagesxml.contains(chapterName) || pagesxml.contains(shortName)) {
					List<HtmlAnchor> value = (List<HtmlAnchor>) pages.getByXPath("//a[@href and contains(text(),'"
							+ chapterName + "') or contains(text(),'" + shortName + "')]");
					if (value != null && !value.isEmpty()) {
						for (HtmlAnchor anchor : value) {
							String href = anchor.getHrefAttribute();
							String fullPath = ToolUtil.getFullPath(pages.getUrl(), href);
							logger.info("得到最新的连接:{}", fullPath);
							String content = DataModel.getInstance().getFormateData(fullPath);
							if (content != null && content.length() > 60) {
								contents.add(content);
								break;
							}
						}
					} else {
						DealWithAllAnchor(express, shortName);
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void DealWithAllAnchor(ContentsRegularExpression express, String shortName) throws Exception {
		HtmlContentPage pagesContent = DataModel.getInstance().getContentsData(href, express);
		if (pagesContent.getNodepages() != null && !pagesContent.getNodepages().isEmpty()) {
			for (Node node : pagesContent.getNodepages()) {
				if (node.atext.contains(shortName)) {
					String newesturl = node.url;

					String content = DataModel.getInstance().getFormateData(newesturl);
					if (content != null && content.length() > 60) {
						contents.add(content);
						break;
					}
				}
			}
		}
	}

}
