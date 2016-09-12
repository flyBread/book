package com.htmlserver.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author zhailz
 *
 *         时间：2016年9月6日 ### 上午11:16:25 jsp响应预留的页面
 */
@RequestMapping(value = "/book")
@Controller
public class BookControllerUtil {
	private static transient Logger logger = LoggerFactory.getLogger(BookControllerUtil.class);

	/**
	 * 获取所有导航栏信息
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "/d")
	public ModelAndView queryPageAuthority(HttpServletRequest request) throws Exception {
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		logger.info("增加object");
		JavaBean attributeValue = new JavaBean();
		attributeValue.setBookName("name");
		mv.addObject("booname", attributeValue);
		return mv;
	}
}

class JavaBean {
	private String bookName;

	public String getBookName() {
		return bookName;
	}

	public void setBookName(String bookName) {
		this.bookName = bookName;
	}
}
