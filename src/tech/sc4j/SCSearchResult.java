package tech.sc4j;

import java.util.ArrayList;
import java.util.List;

import tech.sc4j.music.SCMusic;
import tech.sc4j.util.SCWebUtil;

public class SCSearchResult {
	
	private final List<SCMusic> musics;
	
	public SCSearchResult(final String url) {
		this.musics = new ArrayList<>();
		final String data = SCWebUtil.visitSiteThreaded2(url).trim();
		int tabCount = -1, startIndex = -1, endIndex = -1;
		for (int i = 0, l = data.length(); i < l; i++) {
			final char current = data.charAt(i);
			switch (current) {
			case '{':
				if (tabCount++ == 0) {
					startIndex = i;
				}
				break;
			case '}':
				if (--tabCount == 0) {
					endIndex = i;
				}
				break;
			}
			if (startIndex != -1 && endIndex != -1) {
				try {
					this.musics.add(new SCMusic(data.substring(startIndex, endIndex)));
				} catch (Exception e) {
					
				}
				startIndex = -1;
				endIndex = -1;
			}
		}
	}
	
	public List<SCMusic> getMusicItems() { return this.musics; }

}
