package com.zlz.bug.utils;

import java.io.ByteArrayInputStream;

import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLDocument;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;

public class HttpClientModel  {

	private JEditorPane htmlPane = null;
	private HttpClient client = null;
	private String url = null;

	public HttpClientModel(String url) {
		this.setUrl(url);
		client = new HttpClient(new MultiThreadedHttpConnectionManager());
		client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
	}

	public void loadPage() {
		try {
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