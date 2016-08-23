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
import com.zlz.bug.ContentsRegularExpression;
import com.zlz.bug.ContentsData.HtmlContentPage;

/**
 * @author zhailz
 *
 *         时间：2016年8月3日 ### 下午4:59:25
 */
public class DataModel {
	private Logger logger = LoggerFactory.getLogger(DataModel.class);
	private WebClient webClient = null;
	private int timeOut = 35000000;
	private Map<String, String> urlType = new HashMap<String, String>();
	private Map<String, HtmlPage> pages = new HashMap<String, HtmlPage>();

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
		logger.info("初始化加载的开始.....");
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

	public HtmlPage getPageByUrlScriptEnabled(String url)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		HtmlPage firstPage = pages.get(url);
		if (firstPage == null) {
			firstPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
			// TODO 需要特殊的类型，来进行判定吗？
			webClient.getOptions().setJavaScriptEnabled(false);
			firstPage = webClient.getPage(url);
			pages.put(url, firstPage);
		}
		return firstPage;
	}

	public NextPage getRegularData(String url)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		this.setBaseurl(new URL(url));
		NextPage page = new NextPage();
		page.setCurrentUrl(url);
		HtmlPage firstPage = pages.get(url);
		if (firstPage == null) {
			firstPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
			// TODO 需要特殊的类型，来进行判定吗？
			webClient.getOptions().setJavaScriptEnabled(false);
			firstPage = webClient.getPage(url);
			pages.put(url, firstPage);
		}
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

	// 求取文件的目录页
	public HtmlContentPage getContentsData(String url, ContentsRegularExpression express) throws Exception {
		this.setBaseurl(new URL(url));
		webClient.getCurrentWindow().getEnclosedPage();
		webClient.getOptions().setJavaScriptEnabled(false);
		HtmlPage firstPage = pages.get(url);
		if (firstPage == null) {
			firstPage = (HtmlPage) webClient.getPage(url);
			pages.put(url, firstPage);
		}

		HtmlContentPage base = express.execute(firstPage, url);
		return base;
	}

	@SuppressWarnings("unchecked")
	public String getFormateData(String url) throws Exception {
		this.setBaseurl(new URL(url));
		HtmlPage firstPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
		// TODO 需要特殊的类型，来进行判定吗？
		webClient.getOptions().setJavaScriptEnabled(false);
		firstPage = webClient.getPage(url);

		List<HtmlDivision> objects = (List<HtmlDivision>) firstPage.getByXPath("//div[@id=\"content\"]");
		if (objects != null && objects.size() == 1) {
			return objects.get(0).asText();
		}

		if (objects != null && objects.isEmpty()) {
			objects = (List<HtmlDivision>) firstPage.getByXPath("//div[@class=\"content\"]");
			if (objects != null && objects.size() == 1) {
				return objects.get(0).asText();
			}
		}

		return null;
	}

	public void saveFormateValueToFile(File file, String value) throws IOException {
		if (!file.exists()) {
			file.createNewFile();
		} else {
			FileUtils.write(file, value, "UTF-8", true);
		}

	}

}
