package com.zlz.bug.test;

import java.io.IOException;
import java.net.MalformedURLException;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.zlz.bug.data.DataModel;
import com.zlz.bug.data.NovalRegularExpression;
import com.zlz.bug.data.RegularExpression;
import com.zlz.bug.utils.ResourcesConstant;

/**
 * @author zhailz
 *
 * 时间：2016年8月4日 ### 上午9:17:32
 */
public class DataModelTest {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws MalformedURLException 
	 * @throws FailingHttpStatusCodeException 
	 */
	public static void main(String[] args) throws FailingHttpStatusCodeException, MalformedURLException, IOException {
		DataModel model = DataModel.getInstance();
		RegularExpression express = new NovalRegularExpression();
		String get = model.getRegularData("http://www.sqsxs.com/book/1/1755/1931264.html", express);
		System.out.println(get);

	}

}
