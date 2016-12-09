package com.swingUI.ContentsData;

public class Node implements Comparable<Node> {
	public Node(String temp, String fullPath) {
		this.atext = temp;
		this.url = fullPath;
	}

	public String atext;
	public String url;

	@Override
	public int compareTo(Node o2) {

		if (this.url != null && o2.url != null) {
			return this.url.compareTo(o2.url);
		}

		return 0;
	}

}