package com.zlz.bug;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.zlz.bug.data.DataModel;
import com.zlz.bug.data.HttpClientModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author zhailz
 *
 * 时间：2016年8月3日 ### 下午3:49:17
 */
public class BegineSearch implements ActionListener {
	
	private Logger logger = LoggerFactory.getLogger(BegineSearch.class);
	JComboBox<String> configfield = null;
	HttpClientModel data = null;
	ToolMainPanel toolMainPanel = null;
	public BegineSearch(JComboBox<String> configfield, ToolMainPanel toolMainPanel) {
		this.configfield = configfield;
		this.toolMainPanel = toolMainPanel;
	}

	public void actionPerformed(ActionEvent e) {
		try {
			String begine = configfield.getSelectedItem().toString();
			//输入的是网址
			logger.info("输入的参数是：{}",begine);
			toolMainPanel.logger(begine);
			if(begine.startsWith("http://")|| begine.startsWith("www.")){
				URL url = new URL(begine);
				data = new HttpClientModel(url.toString());
				data.loadPage();
				data.getHtmlPane().setVisible(true);
				toolMainPanel.add(data.getHtmlPane());
			}else{
				//首先就是搜索，然后找到文本的目录
				ContentsRegularExpression express = new ContentsRegularExpression();
				DataModel.getInstance().getRegularData("http://tianyibook.com/tianyibook/17/17496/index.html");
			}
			
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FailingHttpStatusCodeException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
