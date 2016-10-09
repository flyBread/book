package com.mouse.chapter1httpServer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author <a href="http://www.baidu.com"> zhailz </a> <br>
 *         time：2016年10月9日 <br>
 *         version: 1.0-上午10:03:55
 */
public class HttpServer {
	/**
	 * WEB_ROOT is the directory where our HTML and other files reside. * For
	 * this package, WEB_ROOT is the "webroot" directory under the working
	 * directory. The working directory is the location in the file system from
	 * where the java command was invoked.
	 */
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	// shutdown command
	public static final String SHUTDOWN_COMMAND = "/SHUTDOWN";

	// the shutdown command received
	public boolean shutdown = false;

	public static void main(String[] args) {
		HttpServer server = new HttpServer();
		server.await();
	}

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
				// 设置静态的资源，寻找WEB_ROOT下对应的文件
				// 文件的地址是：new File(HttpServer.WEB_ROOT, request.getUri())
				response.sendStaticResource();
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
