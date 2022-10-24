package tech.sc4j.player;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import jaco.mp3.resources.SoundStream;

public final class EXTINFData {
	
	private final short length;
	private final SoundStream inputStream;
	
	public EXTINFData(float length, final String url) throws MalformedURLException, IOException {
		this.length = (short) (length * 1000);
		this.inputStream = new SoundStream(new URL(url).openStream());
	}
	
	public short getLength() { return this.length; }
	
	public SoundStream getStream() { return this.inputStream; }

}
