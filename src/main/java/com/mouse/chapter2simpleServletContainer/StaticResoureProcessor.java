package com.mouse.chapter2simpleServletContainer;

import java.io.IOException;

import com.mouse.chapter1httpServer.Request;
import com.mouse.chapter1httpServer.Response;

public class StaticResoureProcessor {

	public void process(Request request, Response response) {
		try {
			response.sendStaticResource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
