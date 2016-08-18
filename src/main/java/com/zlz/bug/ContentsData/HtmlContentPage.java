package com.zlz.bug.ContentsData;

import java.util.List;

/**
 * @author zhailz
 *
 *         时间：2016年8月12日 ### 下午1:45:37
 * 
 *         标志每一个URL对应的Html的数据结构
 */
public class HtmlContentPage {

	// 过滤后的文本消息
	private String filterContent = null;

	private List<Node> nodepages = null;

	// 从此页面抽取的内容
	private String type = null;

	// 对应的URL
	private String url = null;

	public HtmlContentPage(String url) {
		this.setUrl(url);
	}

	public String getFilterContent() {
		return this.filterContent;
	}

	public List<Node> getNodepages() {
		return nodepages;
	}

	public String getType() {
		return type;
	}

	public String getUrl() {
		return url;
	}

	public void setFilterContent(String filter) {
		this.filterContent = filter;
	}

	public void setNodepages(List<Node> nodepages) {
		this.nodepages = nodepages;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
