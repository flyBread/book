package com.htmlserver.controller;

import com.cache.base.CacheAop;
import com.cache.base.RedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import redis.clients.jedis.ShardedJedis;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author zhailz
 *
 *         时间：2016年9月6日 ### 上午11:16:25 jsp响应预留的页面
 */
@RequestMapping(value = "/book")
@Controller
public class BookControllerUtil {
	private static transient Logger logger = LoggerFactory.getLogger(BookControllerUtil.class);

	@Resource
	private CacheAop cacheAop;

	// 根据配置信息拿到jedis
	private RedisPool cacheClient = RedisPool.getInstance("/redis/jerrymouse");

	/**
	 * cache 测试
	 */

	@RequestMapping(value = "/cacheTest")
	public String cacheTest(HttpServletRequest request,
			@RequestParam(value = "testvalue", required = true) String testvalue) {
		String value = null;

		// 第一种使用的方法
		cacheAop.getJedis().set("1", testvalue);

		// 第二种使用的方法
		ShardedJedis jedis = null;
		try {
			jedis = cacheClient.getJedis();
			if (jedis != null) {
				value = jedis.get("1");
			}
		} catch (Exception e) {
			cacheClient.returnBrokenJedis(jedis);
		} finally {
			cacheClient.returnJedis(jedis);
		}

		return value;
	}

	/**
	 * 获取所有导航栏信息
	 * 
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */

	@RequestMapping(value = "/d")
	public ModelAndView queryPageAuthority(HttpServletRequest request) {
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
