package com.zlz.bug.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
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
		if (get != null && get.getType().equalsIgnoreCase(BCons.contents)) {
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
					model.saveFormateValueToFile(file, BCons.errorUrl + ": " + node.url);
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
			HtmlPage page = getBaiDuSearch(name);
			List<HtmlHeading3> value = (List<HtmlHeading3>) page
					.getByXPath("//div[@class=\"result c-container \"]//h3[@class=\"t\"]");
			for (HtmlHeading3 domNode : value) {
				List<HtmlAnchor> a = (List<HtmlAnchor>) domNode
						.getByXPath("//a[@data-click and @target=\"_blank\" and not(@class)]");
				for (HtmlAnchor htmlAnchor : a) {
					System.out.println(htmlAnchor.asText());
					if (!contents.contains(htmlAnchor.getHrefAttribute())) {
						System.out.println(htmlAnchor.getHrefAttribute());
						contents.add(htmlAnchor.getHrefAttribute());
					}
				}

			}
			return contents;

		} catch (Exception e) {
			e.printStackTrace();
		}
		// google 搜索最新的章节
		return null;
	}

	public static HtmlPage getBaiDuSearch(String name)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		@SuppressWarnings("deprecation")
		String namegb = URLEncoder.encode(name);
		// &pn=0 百度搜索的第几页
		String baiduURl = "https://www.baidu.com/s?ie=utf-8&wd=" + namegb + "&pn=0";
		HtmlPage page = DataModel.getInstance().getPageByUrlScriptEnabled(baiduURl);
		return page;

	}

	// 拿到订阅的名字
	public static String[] personFofusBookNames() throws Exception {
		String filep = "foucus.txt";
		File file = new File(filep);
		String it = ToolUtil.readFileToString(file, Charsets.UTF_8.name());
		logger.info("找到关注的数目名字：{}", it);
		if (it != null && it.length() > 0) {
			return it.split("[,]");
		}
		return null;

	}

	// 寻找最新的章节

	public static String getNewestChapter(String bookName) throws Exception {
		JSONObject bookjson = new JSONObject();
		bookjson.put("name", bookName);
		if (bookName != null && bookName.length() > 0) {
			// 得到最新的章节
			HtmlPage page = getBaiDuSearch(bookName);
			HtmlDivision divfirst = page.getFirstByXPath("//div[@class=\"op_tb_more\"]");
			// 更新内容
			HtmlSpan time = (HtmlSpan) divfirst.getFirstByXPath("//span[@class=\"op_tb_fr\"]");
			long updateTime = ToolUtil.convertTime(time.asText());
			if (updateTime > 0) {
				// long值
				bookjson.put("updateTime", updateTime);
				// 规范后的易观值
				bookjson.put("updateTimeFormate", DateFormatUtils.format(updateTime, BCons.TimeFormate));
			}

			// 书名称最新的章节的名称
			HtmlAnchor newest = divfirst.getFirstByXPath("//a[@class=\"op_tb_line\"]");
			if (newest != null) {
				bookjson.put("newestChapter", newest.asText());
			}

			// 最快的速度寻找到最新章节的内容
			fastGetNewestChapterConents(page, newest.asText());

		}
		return bookName;

	}

	@SuppressWarnings("unchecked")
	private static String fastGetNewestChapterConents(HtmlPage page, String chapterName) {
		ArrayBlockingQueue<String> blockcontens = new ArrayBlockingQueue<String>(4);
		List<String> contents = new ArrayList<String>();
		List<HtmlHeading3> value = (List<HtmlHeading3>) page
				.getByXPath("//div[@class=\"result c-container \"]//h3[@class=\"t\"]");
		for (HtmlHeading3 domNode : value) {
			List<HtmlAnchor> a = (List<HtmlAnchor>) domNode
					.getByXPath("//a[@data-click and @target=\"_blank\" and not(@class)]");
			for (HtmlAnchor htmlAnchor : a) {
				String href = htmlAnchor.getHrefAttribute();
				if (!contents.contains(href)) {
					contents.add(href);
					new GetChapterContent(href, blockcontens, chapterName).start();

				}
			}

		}
		return null;
	}

	public static void main(String[] args) throws Exception {

		String[] bookNames = BookUtil.personFofusBookNames();
		if (bookNames != null && bookNames.length > 0) {
			for (String string : bookNames) {
				getNewestChapter(string);
			}
		}
	}
}
