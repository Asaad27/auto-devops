package com.pfa.devops.jenkins;

import java.net.URI;

public class CustomPair {
	public String artifact_name;
	public String url;

	public CustomPair(String artifact_name, String url) {
		this.artifact_name = artifact_name;
		this.url = url;
	}
}
