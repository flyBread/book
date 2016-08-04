package com.zlz.bug.data;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class NextPage {

	private HtmlPage currentPage;
	
	private String currentUrl;
	
	
	private String upContent;
	
	private HtmlPage nextPage;
	
	
	private String nextUrl;


	public HtmlPage getCurrentPage() {
		return currentPage;
	}


	public void setCurrentPage(HtmlPage currentPage) {
		this.currentPage = currentPage;
	}


	public String getCurrentUrl() {
		return currentUrl;
	}


	public void setCurrentUrl(String currentUrl) {
		this.currentUrl = currentUrl;
	}


	public String getUpContent() {
		return upContent;
	}


	public void setUpContent(String upContent) {
		this.upContent = upContent;
	}


	public HtmlPage getNextPage() {
		return nextPage;
	}


	public void setNextPage(HtmlPage nextPage) {
		this.nextPage = nextPage;
	}


	public String getNextUrl() {
		return nextUrl;
	}


	public void setNextUrl(String nextUrl) {
		this.nextUrl = nextUrl;
	}
	
	
	
	
	
}
