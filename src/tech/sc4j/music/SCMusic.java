package tech.sc4j.music;

import java.util.Date;

import tech.sc4j.util.SCWebUtil;

public class SCMusic {
	
	private final String title, artwork_url, hls_url;
	//private final Date postDate;
	
	public SCMusic(final String data) {
		this.title = SCWebUtil.clip(data, "\"title\":\"", "\",");
		this.artwork_url = SCWebUtil.clip(data, "\"artwork_url\":\"", "\",");
		//this.postDate = new Date(SCWebUtil.clip(data, "\"created_at\":\"", "\","));
		this.hls_url = SCWebUtil.clip(data, "\"media\":{\r\n"
				+ "            \"transcodings\":[\r\n"
				+ "               {\r\n"
				+ "                  \"url\":\"", "\",");
	}
	
	public String getTitle() { return this.title; }
	
	public String getArtworkURL() { return this.artwork_url; }
	
	public String getHLSURL() { return this.hls_url; }
	
	//public Date getPostDate() { return this.postDate; }

}
