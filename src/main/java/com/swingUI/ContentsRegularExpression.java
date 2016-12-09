package com.swingUI;

import com.gargoylesoftware.htmlunit.html.HtmlAnchor;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.swingUI.ContentsData.HtmlContentPage;
import com.swingUI.ContentsData.Node;
import com.swingUI.utils.BCons;
import com.swingUI.utils.ToolUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 目录的过滤的内容
 */
public class ContentsRegularExpression {

	private Logger logger = LoggerFactory.getLogger(ContentsRegularExpression.class);

	private String regularExpression = "^第.*章";
	// 定义HTML标签的正则表达式
	private Pattern regularPattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);

	// 正则表达式

	@SuppressWarnings("unchecked")
	public HtmlContentPage execute(HtmlPage firstPage, String url) {
		HtmlContentPage page = new HtmlContentPage(url);
		// logger.info("拉去页面的内容：{}", firstPage.asXml());
		// 得到所有的索引
		List<HtmlAnchor> nodes = (List<HtmlAnchor>) firstPage.getByXPath("//a[@href]");
		TreeSet<Node> nodepages = new TreeSet<Node>();
		if (nodes != null && !nodes.isEmpty()) {
			page.setType(BCons.contents);
			for (HtmlAnchor anchor : nodes) {
				String temp = anchor.getTextContent();
				Matcher mather = regularPattern.matcher(temp);
				if (mather.find()) {
					String href = anchor.getHrefAttribute();
					String fullPath = ToolUtil.getFullPath(firstPage.getUrl(), href);
					// 有了具体的引用的地址，我们需要把这些的地址储存起来
					Node node = new Node(temp, fullPath);
					nodepages.add(node);
				}
			}
			page.setNodepages(nodepages);
		}
		return page;
	}

	public static void main(String[] args) {
		String value = "第七章";
		String value1 = "第七";
		String value2 = "七章";
		String regularExpression = "^第.*章|.*章";
		Pattern regularPattern = Pattern.compile(regularExpression, Pattern.CASE_INSENSITIVE);
		Matcher mather = regularPattern.matcher(value);
		System.out.println(mather.find());
		mather = regularPattern.matcher(value1);
		System.out.println(mather.find());
		mather = regularPattern.matcher(value2);
		System.out.println(mather.find());
	}
}
