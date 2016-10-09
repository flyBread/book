package com.mouse.chapter2simpleServletContainer.application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLStreamHandler;

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
		URLClassLoader loader = null;
		Servlet servlet = null;
		try {

			// /**
			// * 文件加载是一个点，这个必须的搞明白
			// */
			// ClassLoaderSub myClass = new ClassLoaderSub();
			// Class<?> calss = myClass.getClassByFile(servletName);
			// servlet = (Servlet) calss.newInstance();

			// create a URLClassLoader
			URL[] urls = new URL[1];
			URLStreamHandler streamHandler = null;
			File classPath = new File(Constants.WEB_ROOT);
			// the forming of repository is taken from the createClassLoader
			// method in
			// org.apache.catalina.startup.ClassLoaderFactory
			// String value = classPath.getCanonicalPath();
			// value = value +
			// "/com/mouse/chapter2simpleServletContainer/application";
			String repository = (new URL("file", null, classPath.getCanonicalPath() /* value */ + File.separator))
					.toString();
			// the code for forming the URL is taken from the addRepository
			// method in
			// org.apache.catalina.loader.StandardClassLoader class.
			urls[0] = new URL(null, repository, streamHandler);
			loader = new URLClassLoader(urls);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
		Class myClass = null;
		try {
			myClass = loader.loadClass(servletName);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		try {
			servlet = (Servlet) myClass.newInstance();
			servlet.service((ServletRequest) request, (ServletResponse) response);
		} catch (Exception e) {
			System.out.println(e.toString());
		} catch (Throwable e) {
			System.out.println(e.toString());
		}

	}

}
