package tech.sc4j.player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jaco.mp3.resources.SoundStream;

public final class EXTINFData {
	
	private final short length;
	private SoundStream inputStream;
	private final String url;
	
	public EXTINFData(float length, final String url) {
		this.length = (short) (length * 1000);
		this.url = url;
	}
	
	public void loadStream() {
		try {
			this.inputStream = new SoundStream(new URL(url).openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public short getLength() { return this.length; }
	
	public SoundStream getStream() { return this.inputStream; }

}
