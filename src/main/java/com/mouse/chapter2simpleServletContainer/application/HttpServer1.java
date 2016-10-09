package com.mouse.chapter2simpleServletContainer.application;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import com.mouse.chapter1httpServer.HttpServer;
import com.mouse.chapter1httpServer.Request;
import com.mouse.chapter1httpServer.Response;

/**
 * @author <a href="http://www.baidu.com"> zhailz </a> <br>
 *         time：2016年10月9日 <br>
 *         version: 1.0-上午11:12:29
 */
public class HttpServer1 extends HttpServer {

	public static void main(String[] args) {
		HttpServer1 server = new HttpServer1();
		server.await();
	}

	@Override
	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;
		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		// Loop waiting for a request
		while (!shutdown) {
			Socket socket = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				socket = serverSocket.accept();
				input = socket.getInputStream();
				output = socket.getOutputStream();
				// create Request object and parse
				Request request = new Request(input);
				request.parse();
				// create Response object
				Response response = new Response(output);
				response.setRequest(request);

				// check if this is a request for a servlet or
				// a static resource
				// a request for a servlet begins with "/servlet/"
				if (request.getUri().startsWith("/servlet/")) {
					ServletProcessor1 processor = new ServletProcessor1();
					processor.process(request, response);
				} else {
					StaticResoureProcessor processor = new StaticResoureProcessor();
					processor.process(request, response);
				}

				// Close the socket
				socket.close();
				// check if the previous URI is a shutdown command
				shutdown = request.getUri().equals(SHUTDOWN_COMMAND);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}
}
