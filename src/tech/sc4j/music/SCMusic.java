package tech.sc4j.music;

import java.util.ArrayList;
import java.util.List;

import jaco.mp3.player.MP3Player;
import tech.sc4j.SC4J;
import tech.sc4j.player.EXTINFData;
import tech.sc4j.util.SCWebUtil;

public class SCMusic {
	
	private boolean paused;
	private final String title, artwork_url, trackId, trackAuthorization, artist, kind;
	private int likes, length;
	//private final Date postDate;
	
	private Thread playingThread;
	
	public SCMusic(final String data) {
		this.title = SCWebUtil.clip(data, "\"title\":\"", "\",");
		this.artwork_url = SCWebUtil.clip(data, "\"artwork_url\":\"", "\",");
		this.trackId = SCWebUtil.clip(data, "\"media\":{\"transcodings\":[{\"url\":\"https://api-v2.soundcloud.com/media/soundcloud:tracks:", "/stream/hls");
		this.trackAuthorization = SCWebUtil.clip(data, "\",\"track_authorization\":\"", "\",");
		this.artist = SCWebUtil.clip(data, "\"username\":\"", "\",");
		this.kind = SCWebUtil.clip(data, "\"kind\":\"", "\",");
		final String likeString = SCWebUtil.clip(data, "\"likes_count\":", ",");
		try {
			this.likes = Integer.parseInt(likeString);
		} catch (NumberFormatException e) {
			this.likes = -1;
		}
		this.length = -1;
		//this.postDate = new Date(SCWebUtil.clip(data, "\"created_at\":\"", "\","));
	}
	
	public String getTitle() { return this.title; }
	
	public String getArtworkURL() { return this.artwork_url; }
	
	public String getArtist() { return this.artist; }
	
	public String getKind() { return this.kind; }
	
	public int getLikes() { return this.likes; }
	
	public int getLength() { return this.length; }
	
	//public Date getPostDate() { return this.postDate; }
	
	public void play() {
		 final String[] extinf_datas = SCWebUtil.visitSiteThreaded2(SCWebUtil.clip(SCWebUtil.visitSiteThreaded2(String.format(
				 "https://api-v2.soundcloud.com/media/soundcloud:tracks:%s/stream/hls?client_id=jOJjarVXJfZlI309Up55k93EUDG7ILW6&track_authorization=%s",
				 this.trackId,
				 this.trackAuthorization)),
				 "{\"url\":\"",
				 "\"}"
				)).split("#EXTINF:");
		 int lengthMS = 0;
		 final List<EXTINFData> dataObjects = new ArrayList<>();
		 for (int i = 1, l = extinf_datas.length; i < l; i++) {
			 final String s = extinf_datas[i];
			 try {
				 final int sharpIndex = s.indexOf('#');
				 EXTINFData data = new EXTINFData(Float.parseFloat(s.substring(0, 8)), s.substring(9, sharpIndex == -1 ? s.length() : sharpIndex));
				 dataObjects.add(data);
				 lengthMS += data.getLength();
			 } catch (Exception e) {
				 e.printStackTrace();
			 }
		 }
		 this.length = lengthMS / 1000;
		 if (playingThread == null) {
			 playingThread = new Thread(() -> {
				 try {
					 dataObjects.get(0).loadStream();
				 } catch (Exception e) {
					 e.printStackTrace();
				 }
				 for (int i = 0, l = dataObjects.size(); i < l; i++) {
					 SC4J.instance.setPlayer(new MP3Player().set(dataObjects.get(i).getStream())).play();
					 try {
						 if (i <= l - 1) dataObjects.get(i + 1).loadStream();
					 } catch (Exception e) {
						 e.printStackTrace();
					 }
					 try {
						 Thread.sleep(dataObjects.get(i).getLength() - 35); // EXTINF縺ｯ34ms縺上ｉ縺�縺ｮ繝ｩ繧ｰ縺後≠繧九ｓ縺倥ｃ
					 } catch (InterruptedException e) {
						 e.printStackTrace();
					 }
					 while (SC4J.instance.isPaused()) { }
				 }
				 SC4J.instance.onMusicEnd();
			 });
		 }
		 playingThread.start();
	}
	
	public void stopThread() {
		playingThread.stop();
	}

}
