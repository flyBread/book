package com.zlz.bug.test;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.cache.base.CacheAop;
import com.htmlserver.controller.BookControllerUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:mvc-dispatcher-servlet.xml")
public class JerryMouseIT extends AbstractJUnit4SpringContextTests {

	static {
		System.setProperty("config.type", "jerrymouse");
	}

	protected Model model = new ExtendedModelMap();
	protected MockHttpServletRequest req = new MockHttpServletRequest();
	protected HttpServletResponse res = new MockHttpServletResponse();

	@Resource
	private BookControllerUtil bookControllerUtil;

	@Resource
	private CacheAop cacheAop;

	@Before
	public void ini() {

	}

	@Test
	public void bookUtilTest() throws Exception {
		bookControllerUtil.queryPageAuthority(req);
	}

	@Test
	public void reddisTest() {
		String jedis = bookControllerUtil.cacheTest(req, "sucess");
		System.out.println(jedis);
	}

}
