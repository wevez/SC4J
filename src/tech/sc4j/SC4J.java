package tech.sc4j;

import java.net.MalformedURLException;
import tech.sc4j.util.SCWebUtil;

public class SC4J {
	
	public static void main(final String[] args) throws MalformedURLException {
		final SCSearchResult r = search("Twinkle night");
		r.getMusicItems().get(0).play();
	}
	
	public static SCSearchResult search(final String title) {
		return new SCSearchResult(SCWebUtil.titleToURL(title, 20, 0));
	}
	
	public static SCSearchResult search(final String title, int maximum, int offset) {
		return new SCSearchResult(SCWebUtil.titleToURL(title, maximum, offset));
	}

}
