package com.mouse.chapter2simpleServletContainer;

import javax.servlet.Servlet;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.mouse.chapter1httpServer.Request;
import com.mouse.chapter1httpServer.Response;

/**
 * @author <a href="http://www.baidu.com"> zhailz </a> <br>
 *         time：2016年10月9日 <br>
 *         version: 1.0-上午11:14:11
 */
public class ServletProcessor1 {

	public void process(Request request, Response response) {
		String uri = request.getUri();
		String servletName = uri.substring(uri.lastIndexOf("/") + 1);

		Servlet servlet = null;
		try {

			// /**
			// * 文件加载是一个点，这个必须的搞明白
			// */
			// ClassLoaderSub myClass = new ClassLoaderSub();
			// Class<?> calss = myClass.getClassByFile(servletName);
			// servlet = (Servlet) calss.newInstance();

			if (servletName.equalsIgnoreCase("PrimitiveServlet")) {
				servlet = PrimitiveServlet.class.newInstance();

			}
			servlet.service((ServletRequest) request, (ServletResponse) response);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}
	}

	public static void main(String[] args) {
		try {
			ClassLoaderSub myClass = new ClassLoaderSub();
			String filepath = "/Users/zhailz/Documents/workspace/idle-workspace/book/src/main/java/com/mou"
					+ "se/chapter2simpleServletContainer/PrimitiveServlet.java";
			Class<?> calss = myClass.getClassByFile(filepath);
			Servlet servlet = (Servlet) calss.newInstance();
			System.out.println(servlet.getServletInfo());
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}

	}

}
