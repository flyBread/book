package com.zlz.bug;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JComboBox;

import com.zlz.bug.utils.HttpClientModel;

/**
 * @author zhailz
 *
 * 时间：2016年8月3日 ### 下午3:49:17
 */
public class BegineSearch implements ActionListener {
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
			URL url = new URL(begine);
			data = new HttpClientModel(url.toString());
			data.loadPage();
			data.getHtmlPane().setVisible(true);
			toolMainPanel.add(data.getHtmlPane());
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
