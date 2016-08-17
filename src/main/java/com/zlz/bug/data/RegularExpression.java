package com.zlz.bug.data;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.zlz.bug.ContentsData.HtmlContentPage;

/**
 * @author zhailz
 *
 * 时间：2016年8月4日 ### 上午9:28:30
 */
public interface RegularExpression {

	String execute(String asXml);

	HtmlContentPage execute(HtmlPage firstPage);

}
