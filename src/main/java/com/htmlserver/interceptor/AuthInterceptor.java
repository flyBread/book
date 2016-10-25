package com.htmlserver.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 权限控制拦截器
 * 
 */
public class AuthInterceptor extends HandlerInterceptorAdapter {
	protected final static Logger logger = LoggerFactory.getLogger(AuthInterceptor.class);
	protected static List<String> controllerUriFilter = Collections.synchronizedList(new ArrayList<String>());

	static {

		controllerUriFilter.add("/book/update");
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String requestURI = request.getRequestURI();
		if (logger.isInfoEnabled()) {
			logger.info("web AuthInterceptor==========requestURI={}", requestURI);
		}

		if (controllerUriFilter.contains(requestURI)) {
			return true;
		}

		int userId = 1;
		if (userId > 0) {
			return true;
		} else {
			response.sendRedirect(request.getContextPath() + "/back/login");
			return false;
		}

		// return super.preHandle(request, response, handler);
	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView == null) {
			modelAndView = new ModelAndView();
		}

		super.postHandle(request, response, handler, modelAndView);
	}
}
