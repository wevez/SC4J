package tech.sc4j.music;

import java.util.ArrayList;
import java.util.List;

import jaco.mp3.player.MP3Player;
import tech.sc4j.player.EXTINFData;
import tech.sc4j.util.SCWebUtil;

public class SCMusic {
	
	private final String title, artwork_url, trackId, trackAuthorization;
	//private final Date postDate;
	
	public SCMusic(final String data) {
		this.title = SCWebUtil.clip(data, "\"title\":\"", "\",");
		this.artwork_url = SCWebUtil.clip(data, "\"artwork_url\":\"", "\",");
		this.trackId = SCWebUtil.clip(data, "\"media\":{\"transcodings\":[{\"url\":\"https://api-v2.soundcloud.com/media/soundcloud:tracks:", "/stream/hls");
		this.trackAuthorization = SCWebUtil.clip(data, "\",\"track_authorization\":\"", "\",");
		//this.postDate = new Date(SCWebUtil.clip(data, "\"created_at\":\"", "\","));
	}
	
	public String getTitle() { return this.title; }
	
	public String getArtworkURL() { return this.artwork_url; }
	
	//public Date getPostDate() { return this.postDate; }
	
	public void play() {
		 final String[] extinf_datas = SCWebUtil.visitSiteThreaded2(SCWebUtil.clip(SCWebUtil.visitSiteThreaded2(String.format(
				 "https://api-v2.soundcloud.com/media/soundcloud:tracks:%s/stream/hls?client_id=jOJjarVXJfZlI309Up55k93EUDG7ILW6&track_authorization=%s",
				 this.trackId,
				 this.trackAuthorization)),
				 "{\"url\":\"",
				 "\"}"
				)).split("#EXTINF:");
		 final List<EXTINFData> dataObjects = new ArrayList<>();
		 for (int i = 1, l = extinf_datas.length; i < l; i++) {
			 final String s = extinf_datas[i];
			 try {
				 final int sharpIndex = s.indexOf('#');
				 dataObjects.add(new EXTINFData(Float.parseFloat(s.substring(0, 8)), s.substring(9, sharpIndex == -1 ? s.length() : sharpIndex)));
			 } catch (Exception e) {
				 
			 }
		 }
		 final MP3Player player = new MP3Player();
		 for (int i = 0, l = dataObjects.size(); i < l; i++) {
			 player.set(dataObjects.get(i).getStream()).play();
			 try {
				Thread.sleep(dataObjects.get(i).getLength());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		 }
	}

}
