package com.mouse.chapter2simpleServletContainer.application;

import com.mouse.chapter1httpServer.Request;
import com.mouse.chapter1httpServer.Response;

import java.io.IOException;

public class StaticResoureProcessor {

	public void process(Request request, Response response) {
		try {
			response.sendStaticResource();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
