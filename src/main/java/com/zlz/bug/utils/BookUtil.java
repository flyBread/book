package com.zlz.bug.utils;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.*;
import com.zlz.bug.ContentsData.HtmlContentPage;
import com.zlz.bug.ContentsData.Node;
import com.zlz.bug.ContentsRegularExpression;
import com.zlz.bug.data.DataModel;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BookUtil {

	private static Logger logger = LoggerFactory.getLogger(BookUtil.class);

	private static ExecutorService pool = Executors.newFixedThreadPool(16);

	public static Boolean storeBookByContentUrl(File file, String contentsUrl) throws Exception {
		// 根据目录页，抽取目录以及每一章节对应的页面的地址
		// 然后得到每一章节的内容，最后，保存为文件
		DataModel model = DataModel.getInstance();
		// 首先就是搜索，然后找到文本的目录
		ContentsRegularExpression express = new ContentsRegularExpression();
		HtmlContentPage get = DataModel.getInstance().getContentsData(contentsUrl, express);
		// 目录地址
		if (get != null && get.getType().equalsIgnoreCase(BCons.contents)) {
			TreeSet<Node> contents = get.getNodepages();
			if (contents != null && !contents.isEmpty()) {
				for (Node node : contents) {
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
			} else {
				return false;
			}

		}

		return false;
	}

	/**
	 * 根据名字寻找目录页
	 */
	public static List<String> getContentsUrl(String name) {
		try {
			HtmlPage page = getBaiDuSearch(name);
			return getMuLuURL(page);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// google 搜索最新的章节
		return null;
	}

	/**
	 * 获取目录页的网址
	 */
	@SuppressWarnings("unchecked")
	private static List<String> getMuLuURL(HtmlPage page) {
		List<String> contents = new ArrayList<String>();
		List<HtmlHeading3> value = (List<HtmlHeading3>) page
				.getByXPath("//div[@class=\"result c-container \"]//h3[@class=\"t\"]");
		for (HtmlHeading3 domNode : value) {
			List<HtmlAnchor> a = (List<HtmlAnchor>) domNode
					.getByXPath("//a[@data-click and @target=\"_blank\" and not(@class)]");
			for (HtmlAnchor htmlAnchor : a) {
				if (!contents.contains(htmlAnchor.getHrefAttribute()) && htmlAnchor.asText().length() > 0) {
					contents.add(htmlAnchor.getHrefAttribute());
				}
			}

		}
		return contents;
	}

	/**
	 * 根据名称，返回百度的搜索的页面
	 */
	public static HtmlPage getBaiDuSearch(String name)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		return getBaiDuSearch(name, 0);
	}

	/**
	 * 根据名称，返回百度的搜索的页面
	 */
	public static HtmlPage getBaiDuSearch(String name, int pageNumber)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		@SuppressWarnings("deprecation")
		String namegb = URLEncoder.encode(name);
		// &pn=0 百度搜索的第几页
		pageNumber = pageNumber * 10;
		String baiduURl = "https://www.baidu.com/s?ie=utf-8&wd=" + namegb + "&pn=" + pageNumber;
		HtmlPage page = DataModel.getInstance().getPageByUrlScriptEnabled(baiduURl);
		return page;

	}

	// 拿到订阅的名字
	public static List<String> personFofusBookNames() throws Exception {
		String filep = "foucus.txt";
		File file = new File(filep);
		String it = ToolUtil.readFileToString(file, Charsets.UTF_8.name());
		logger.info("找到关注的数目名字：{}", it);
		ArrayList<String> bookNames = new ArrayList<String>(30);
		if (it.contains(",") || it.contains("\n")) {
			String[] spitevalue = it.split("[,|\n]");
			for (String booktemp : spitevalue) {
				if (booktemp.length() > 0) {
					bookNames.add(booktemp);
				}
			}
		}
		return bookNames;
	}

	// 寻找最新的章节

	public static JSONObject getNewestChapter(String bookName) {

		try {
			if (bookName != null && bookName.length() > 0) {
				JSONObject bookjson = new JSONObject();
				bookjson.put("name", bookName);
				// 得到最新的章节
				HtmlPage page = getBaiDuSearch(bookName);
				HtmlDivision divfirst = getBaiduFirstDiv(page, bookName);
				// 更新内容
				HtmlSpan time = (HtmlSpan) divfirst.getFirstByXPath("//span[@class=\"op_tb_fr\"]");
				if (time != null) {
					long updateTime = ToolUtil.convertTime(time.asText());
					if (updateTime > 0) {
						// long值
						bookjson.put("updateTime", updateTime);
						// 规范后的易观值
						String formateDay = DateFormatUtils.format(updateTime, BCons.TimeFormate);
						bookjson.put("updateTimeFormate", formateDay);
						logger.info("本书的更新时间是：{}", formateDay);
					}
				}

				// 书名称最新的章节的名称
				HtmlAnchor newest = divfirst.getFirstByXPath("//a[@class=\"op_tb_line\"]");
				if (newest != null) {
					String chapterName = newest.asText();
					if (chapterName.endsWith(".")) {
						chapterName = chapterName.replace(".", "");
					}
					bookjson.put("newestChapter", chapterName);
					logger.info("本书的最新的章节：{}", chapterName);

					// 最快的速度寻找到最新章节的内容
					String txt = fastGetNewestChapterConents(page, chapterName);
					if (txt != null) {
						bookjson.put("newestChapterContent", txt);
						logger.info("最新章节的内容：{}", txt);
					}

					// 备选的目录的位置
					List<String> contentUrl = getMuLuURL(page);
					if (contentUrl != null) {
						for (int i = 0; i < contentUrl.size(); i++) {
							String url = contentUrl.get(i);
							logger.info("本书的目录的地址：{}", url);
							bookjson.put("bookMulu" + i, url);
						}
					}

					// 保存json的数据
					DataModel.getInstance().store(bookjson);
					return new JSONObject().put(bookName, "/n/n" + newest.asText() + "/n" + txt);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static HtmlDivision getBaiduFirstDiv(HtmlPage page, String bookName)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlDivision div = page.getFirstByXPath("//div[@class=\"op_tb_more\"]");
		if (div == null) {
			page = getBaiDuSearch(bookName + " " + "小说");
			div = page.getFirstByXPath("//div[@class=\"result c-container \"]");
		}
		return div;
	}

	/**
	 * 得到最新的章节的内容
	 */
	@SuppressWarnings("unchecked")
	private static String fastGetNewestChapterConents(HtmlPage page, String chapterName) throws InterruptedException {
		ArrayBlockingQueue<String> blockcontens = new ArrayBlockingQueue<String>(4);
		List<String> contents = new ArrayList<String>();
		// 搜索结果
		List<HtmlHeading3> value = (List<HtmlHeading3>) page
				.getByXPath("//div[@class=\"result c-container \"]//h3[@class=\"t\"]");
		for (HtmlHeading3 domNode : value) {
			List<HtmlAnchor> a = (List<HtmlAnchor>) domNode
					.getByXPath("//a[@data-click and @target=\"_blank\" and not(@class)]");
			for (HtmlAnchor htmlAnchor : a) {
				if (blockcontens.isEmpty()) {
					String href = htmlAnchor.getHrefAttribute();
					if (!contents.contains(href)) {
						contents.add(href);
						new GetChapterContent(href, blockcontens, chapterName).run();
						// pool.execute(new GetChapterContent(href,
						// blockcontens, chapterName));
						// Thread.sleep(1000);
					}
				} else {
					return blockcontens.take();
				}
			}
		}
		return blockcontens.take();
	}

	public static void main(String[] args) throws Exception {

		// List<String> bookNames = BookUtil.personFofusBookNames();
		// if (bookNames != null && bookNames.size() > 0) {
		// for (String string : bookNames) {
		// getNewestChapter(string);
		// }
		// }
		getURLPageMainContent();
	}

	// 得到网页的内容
	public static String getURLPageMainContent() {
		try {
			String http = "http://web.6park.com/classbk/messages/8803.html";
			HtmlPage page = DataModel.getInstance().getPageByUrlScriptEnabled(http);
			System.out.println(page.asText());
		} catch (FailingHttpStatusCodeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean downLoad(String bookName) {
		File file = new File(bookName);
		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			List<String> mulus = getContentsUrl(bookName);
			if (mulus != null && !mulus.isEmpty()) {
				for (String muluurl : mulus) {
					boolean result = storeBookByContentUrl(file, muluurl);
					if (result) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return false;

	}
}
