package com.zlz.bug.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author zhailz
 *
 *         时间：2016年8月3日 ### 下午4:59:25
 */
public class DataModel {
	private Logger logger = LoggerFactory.getLogger(DataModel.class);
	private WebClient webClient = null;
	private int timeOut = 350000;
	private Map<String, String> urlType = new HashMap<String, String>();

	public Map<String, String> getUrlType() {
		return urlType;
	}

	public void setUrlType(Map<String, String> urlType) {
		this.urlType = urlType;
	}

	private URL baseurl = null;

	// 私有构造函数
	private DataModel() {
		ini();
	}

	private void ini() {
		webClient = new WebClient();
		webClient.setAjaxController(new NicelyResynchronizingAjaxController());
		webClient.getOptions().setJavaScriptEnabled(true);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setTimeout(timeOut);
		webClient.getOptions().setThrowExceptionOnScriptError(false);
		loadConfigPropertiies();
	}

	// 加载配置文件，或者爬虫搜索过程中的简单规则
	private void loadConfigPropertiies() {
		loadCacheUrlType();
	}

	private void loadCacheUrlType() {
		try {
			Properties proper = new Properties();
			InputStream filepro = this.getClass().getResourceAsStream("/data.properties");
			proper.load(filepro);
			Enumeration<Object> enus = proper.keys();
			while (enus.hasMoreElements()) {
				String key = enus.nextElement().toString();
				String value = proper.getProperty(key);
				urlType.put(key, value);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static class DataModelHolder {
		private static DataModel instance = new DataModel();
	}

	public static DataModel getInstance() {
		return DataModelHolder.instance;
	}

	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		DataModel model = DataModel.getInstance();
		RegularExpression express = new NovalRegularExpression();
//		String url = "http://www.7dsw.com/book/28/28453/9437445.html";
//		String url = "http://www.baoliny.com/77109/21496248.html";
//		String url = "http://www.baoliny.com/77109/21496248.html";
		String url = "https://www.google.com.hk/#gws_rd=cr,ssl";
		NextPage get = model.getRegularData(url, express);
		System.out.println(get.getUpContent());
		File file = new File("test.txt");
		if (!file.exists()) {
			file.createNewFile();
		}

		if (get.getUpContent() != null) {
			FileUtils.write(file, get.getUpContent(), true);
		}

		while (get.getNextUrl() != null) {
			get = model.getRegularData(get.getNextUrl(), express);
			System.out.println(get.getUpContent());
			if (get.getUpContent() != null) {
				FileUtils.write(file, get.getUpContent(), true);
			}
		}
	}

	public NextPage getRegularData(String url, RegularExpression express)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		this.setBaseurl(new URL(url));

		NextPage page = new NextPage();
		page.setCurrentUrl(url);
		HtmlPage firstPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		// TODO 需要特殊的类型，来进行判定吗？
		webClient.getOptions().setJavaScriptEnabled(false);
		firstPage = webClient.getPage(url);
		page.setCurrentPage(firstPage);

		String content = getHtmlDivContent(firstPage);
		if (content != null) {
			iniURLCache(url);
			page.setUpContent(content);
			return page;
		}

		// 双页比对的办法来确定值
		String nexturl = getNext(firstPage, url);
		if (nexturl != null) {
			page.setNextUrl(nexturl);
			HtmlPage nextPage = webClient.getPage(nexturl);
			page.setNextPage(nextPage);
			String firsttext = firstPage.getBody().asText();
			String nexttext = nextPage.getBody().asText();
			if (firsttext.length() > ConfigCenter.BodyasTextLength) {
				SimpleTextFilter filter = new SimpleTextFilter();
				content = filter.filter(firsttext, nexttext, this);
			}

			if (content != null) {
				page.setUpContent(content);
				return page;
			}
		}

		return null;

	}

	private String getNext(HtmlPage rootPage, String url) {
		String shorturl = null;
		List<?> value = rootPage.getByXPath("//A[contains(text(),\"下一\")]");
		List<String> urls = new ArrayList<String>();
		if (value != null && !value.isEmpty()) {
			for (Object htmlAnchor : value) {
				if (htmlAnchor instanceof HtmlAnchor) {
					HtmlAnchor next = (HtmlAnchor) htmlAnchor;
					if (next.getTextContent().contains("下一章") || next.getTextContent().contains("下一页")) {
						shorturl = next.getHrefAttribute();
						urls.add(shorturl);
						if (shorturl.startsWith("http:")) {
							return shorturl;
						}
					}
				}
			}
		}

		if (!urls.isEmpty()) {
			for (String urlrl : urls) {
				String[] urlarray = url.split("/");
				urlarray[urlarray.length - 1] = urlrl;
				String nexturl = StringUtils.join(urlarray, "/");
				return nexturl;
			}
		}

		return null;
	}

	private void iniURLCache(String url) throws MalformedURLException {
		URL urlT = new URL(url);
		String urlshort = urlT.getHost();
		urlType.put(urlshort, ConfigCenter.FindInDivIdContent);
	}

	private String getHtmlDivContent(HtmlPage rootPage) {
		List<?> content = rootPage.getByXPath("//*[@id=content]");
		if (content != null && !content.isEmpty()) {
			for (Object con : content) {
				if (con instanceof HtmlDivision) {
					HtmlDivision next = (HtmlDivision) con;
					String nextStringValue = next.asText();
					if (nextStringValue.length() > ConfigCenter.contentAsTextLengthMin) {
						return nextStringValue;
					}
				}
			}
		}

		return null;
	}

	public URL getBaseurl() {
		return baseurl;
	}

	public void setBaseurl(URL baseurl) {
		this.baseurl = baseurl;
	}
}
