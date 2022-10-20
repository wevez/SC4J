package tech.sc4j.player;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class EXTINFData {
	
	private final short length;
	private final InputStream inputStream;
	
	public EXTINFData(float length, final String url) throws MalformedURLException, IOException {
		this.length = (short) (length * 1000);
		this.inputStream = new URL(url).openStream();
	}
	
	public short getLength() { return this.length; }
	
	public InputStream getStream() { return this.inputStream; }

}
