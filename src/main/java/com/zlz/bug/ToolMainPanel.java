package com.zlz.bug;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.zlz.bug.utils.ResourcesConstant;

/**
 * @author zhailz
 *
 *         时间：2016年4月20日 ### 下午2:11:55
 */
public class ToolMainPanel extends JPanel {

	/**
	 * 序列号
	 */
	private static final long serialVersionUID = 1L;

	private JComboBox<String> configfield = null;

	private JTextArea logvalue = null;

	private JRadioButton choose = null;

	private JTextField nv = null;

	private JTable leftList = null;

	public JRadioButton getChoose() {
		return choose;
	}

	public void setChoose(JRadioButton choose) {
		this.choose = choose;
	}

	public ToolMainPanel() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setEnvPanel();
	}

	// 开始的环境设置
	private void setEnvPanel() {
		JLabel configlabel = getJLabel("名称");
		JComboBox<String> configfield = new JComboBox<String>();
		configfield.setEditable(true);// 将JComboBox设成是可编辑的.
		configfield.setMaximumSize(new Dimension(ResourcesConstant.Lweight, 21));
		configfield.setAlignmentY(Component.RIGHT_ALIGNMENT);
		configfield.setSelectedItem(ResourcesConstant.BegineSource);
		JPanel env = new JPanel();
		env.setLayout(new BorderLayout());
		env.add(configlabel, BorderLayout.LINE_START);
		env.add(configfield, BorderLayout.CENTER);
		JButton pr = new JButton("开始");
		env.add(pr, BorderLayout.LINE_END);
		pr.addActionListener(new BegineSearch(configfield,this));
		env.setMaximumSize(new Dimension(ResourcesConstant.Lweight, 23));
		this.add(env);
		logvalue = new JTextArea();
		this.add(logvalue);
		this.add(Box.createVerticalStrut(2));


	}

	
	// 规范化label的尺寸
	private JLabel getJLabel(String string) {
		JLabel label = new JLabel();
		label.setText(string);
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setPreferredSize(new Dimension(100, 21));
		return label;
	}

	

	public JTextArea getLogvalue() {
		return logvalue;
	}

	public void setLogvalue(JTextArea nodevalue) {
		this.logvalue = nodevalue;
	}

	public JComboBox<String> getConfigfield() {
		return configfield;
	}

	public void setConfigfield(JComboBox<String> configfield) {
		this.configfield = configfield;
	}

	public JTextField getNv() {
		return nv;
	}

	public void setNv(JTextField nv) {
		this.nv = nv;
	}

	public JTable getLeftList() {
		return leftList;
	}

	public void setLeftList(JTable leftList) {
		this.leftList = leftList;
	}

}
