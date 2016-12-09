package com.swingUI.data;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.io.ByteArrayInputStream;

public class HttpClientModel  {

	private JEditorPane htmlPane = null;
	private HttpClient client = null;
	private String url = null;
	WebClient webClient = null;

	public HttpClientModel(String url) {
		    webClient = new WebClient();
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
	 		webClient.getOptions().setJavaScriptEnabled(true);
	 		webClient.getOptions().setCssEnabled(false);
	 		webClient.getOptions().setTimeout(3500);
	 		webClient.getOptions().setThrowExceptionOnScriptError(false);
	}

	public void loadPage() {
		try {
			
	 		HtmlPage rootPage = (HtmlPage) webClient.getCurrentWindow().getEnclosedPage();
	 		rootPage = webClient.getPage(url);
	 		System.out.println(rootPage.getUrl());
	 		System.out.println(rootPage.getHead().asXml());
	 		System.out.println(rootPage.getBody().asXml());
			
			GetMethod get = new GetMethod(url);
			get.setFollowRedirects(true);
			int iGetResultCode = client.executeMethod(get);
			if (iGetResultCode == 200) {
				final String content = get.getResponseBodyAsString();
				HTMLDocument doc = new HTMLDocument();
				doc.remove(0, doc.getLength());
				doc.putProperty("IgnoreCharsetDirective", Boolean.TRUE);
				htmlPane = new JEditorPane();
				htmlPane.setContentType("text/html");
				htmlPane.setEditable(false);
				htmlPane.read(new ByteArrayInputStream(content.getBytes()), doc);
				System.out.println(doc);
				System.out.println(content);
				System.out.println(htmlPane.getDocument());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public JEditorPane getHtmlPane() {
		return htmlPane;
	}

	public void setHtmlPane(JEditorPane htmlPane) {
		this.htmlPane = htmlPane;
	}

	public HttpClient getClient() {
		return client;
	}

	public void setClient(HttpClient client) {
		this.client = client;
	}

}