package com.zlz.bug.utils;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zlz.bug.ContentsRegularExpression;
import com.zlz.bug.ContentsData.HtmlContentPage;
import com.zlz.bug.ContentsData.Node;
import com.zlz.bug.data.DataModel;

public class BookUtil {

	private static Logger logger = LoggerFactory.getLogger(BookUtil.class);

	public static Boolean storeBookByContentUrl(File file, String contentsUrl) throws Exception {
		// 根据目录页，抽取目录以及每一章节对应的页面的地址
		// 然后得到每一章节的内容，最后，保存为文件
		DataModel model = DataModel.getInstance();
		// 首先就是搜索，然后找到文本的目录
		ContentsRegularExpression express = new ContentsRegularExpression();
		HtmlContentPage get = DataModel.getInstance().getContentsData(contentsUrl, express);

		// 目录地址
		if (get != null && get.getType().equalsIgnoreCase(ResourcesConstant.contents)) {
			List<Node> contents = get.getNodepages();
			for (int i = 0; i < contents.size(); i++) {
				Node node = contents.get(i);
				String contentTitle = node.atext;
				String contentText = model.getFormateData(node.url);
				if (contentTitle != null && contentText != null && contentText.length() > 0) {
					logger.info("开始写入：{}", contentTitle);
					model.saveFormateValueToFile(file, contentTitle);
					model.saveFormateValueToFile(file, "\n");
					model.saveFormateValueToFile(file, contentText);
				} else if (contentTitle != null) {
					model.saveFormateValueToFile(file, contentTitle);
					model.saveFormateValueToFile(file, "\n");
					model.saveFormateValueToFile(file, ResourcesConstant.errorUrl + ": " + node.url);
				}
			}

			return true;
		}

		return false;
	}

	// 根据名字寻找目录页
	@SuppressWarnings("unchecked")
	public static List<String> getContentsUrl(String name) {
		try {
			List<String> contents = new ArrayList<String>();
			@SuppressWarnings("deprecation")
			String namegb = URLEncoder.encode(name);
			String baiduURl = "https://www.baidu.com/s?ie=utf-8&wd=" + namegb;
			HtmlPage page = DataModel.getInstance().getPageByUrlScriptEnabled(baiduURl);
			List<HtmlHeading3> value = (List<HtmlHeading3>) page
					.getByXPath("//div[@class=\"result c-container \"]//h3[@class=\"t\"]");

			for (HtmlHeading3 domNode : value) {
				HtmlAnchor a = domNode.getFirstByXPath("//a[@data-click and @target=\"_blank\"]");
				contents.add(a.getHrefAttribute());
			}

			return contents;

		} catch (Exception e) {
			e.printStackTrace();
		}
		// google
		return null;
	}

	public static void main(String[] args) throws Exception {

		BookUtil.getContentsUrl("易鼎");
	}
}
