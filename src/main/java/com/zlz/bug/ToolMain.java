package com.zlz.bug;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import com.zlz.bug.utils.BCons;

/**
 * @author zhailz
 *
 * 时间：2016年4月20日 ### 下午2:07:22
 */
public class ToolMain extends JFrame{

	/**
	 * 系统的变量
	 */
	public static int Lheight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() * 0.15);

	public static int Lweight = (int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() * 0.1);

	public static int sheight = 750;

	public static int swidth = 800;
	private static final long serialVersionUID = 1L;

	ToolMainPanel mainPanel = new ToolMainPanel();
	
	public ToolMain(String title) {
		// 主界面设置
		this.setTitle(title);
		Image imag = Toolkit.getDefaultToolkit().getImage(BCons.frameIcon);
		this.setIconImage(imag);
		this.setLocation(Lheight, Lweight);
		this.setSize(new Dimension(swidth+80, sheight));
		mainPanel.setVisible(true);
		mainPanel.setBorder(BorderFactory.createTitledBorder(BCons.TitleString));
		this.add(mainPanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}
	

	public static void main(String[] args) {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});

	}

	protected static void createAndShowGUI() {
		new ToolMain(BCons.TitleString);
	}
}
