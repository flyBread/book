package com.mouse.chapter2simpleServletContainer.application;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.mock.web.MockServletConfig;

public class PrimitiveServlet implements Servlet {
	public void init(ServletConfig config) throws ServletException {
		System.out.println("init");
	}

	public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
		System.out.println("from service");
		PrintWriter out = response.getWriter();
		out.println("Hello. Roses are red.");
		out.print("Violets are blue.");
	}

	public void destroy() {
		System.out.println("destroy");
	}

	public String getServletInfo() {
		return "servlet info: PrimitiveServlet";
	}

	public ServletConfig getServletConfig() {
		ServletConfig config = new MockServletConfig();
		return config;
	}
}