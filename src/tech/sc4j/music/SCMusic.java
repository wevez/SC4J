package tech.sc4j.music;

import java.util.ArrayList;
import java.util.List;

import jaco.mp3.player.MP3Player;
import tech.sc4j.SC4J;
import tech.sc4j.player.EXTINFData;
import tech.sc4j.util.SCWebUtil;

public class SCMusic {
	
	private boolean paused, flag;
	private final String title, artwork_url, trackId, trackAuthorization, artist, kind;
	private int likes, length, playingIndex;

	//private final Date postDate;
	
	private Thread playingThread;
	private boolean stoped = false;
	private long lastMS, deltaMS;
	private MP3Player currentPlayer;
	
	private final List<EXTINFData> dataObjects = new ArrayList<>();;
	
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
		playingIndex = 0;
		if (dataObjects.isEmpty()) {
			 final String[] extinf_datas = SCWebUtil.visitSiteThreaded2(SCWebUtil.clip(SCWebUtil.visitSiteThreaded2(String.format(
					 "https://api-v2.soundcloud.com/media/soundcloud:tracks:%s/stream/hls?client_id=8m4K5d2x4mNmUHLhLmsGq9vxE3dDkxCm&track_authorization=%s",
					 this.trackId,
					 this.trackAuthorization)),
					 "{\"url\":\"",
					 "\"}"
					)).split("#EXTINF:");
			 int lengthMS = 0;
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
		}
		 if (playingThread == null) {
			 playingThread = new Thread(() -> {
				 dataObjects.get(0).loadStream();
				 for (int i = 0, l = dataObjects.size(); i < l; i++) {
					 playingIndex = i;
					 final EXTINFData d = dataObjects.get(playingIndex);
					 currentPlayer = new MP3Player().set(d.getStream());
					 currentPlayer.play();
					 lastMS = System.currentTimeMillis();
					 new Thread(() -> { if (playingIndex < l - 1) dataObjects.get(playingIndex + 1).loadStream(); }).start();
					 final long waitMS = d.getLength() + lastMS;
					 
					 while (System.currentTimeMillis() < waitMS) {
						 if (!currentPlayer.isPlaying()) {
							 if (flag) {
								 lastMS = System.currentTimeMillis() - deltaMS;
							 } else {
								 lastMS = System.currentTimeMillis() - (deltaMS = System.currentTimeMillis() - lastMS);
								 flag = true;
							 }
						 }
						 if (this.stoped) return;
					 }
					 playingIndex++;
				 }
				 SC4J.onMusicEnd();
			 });
		 }
		 playingThread.start();
	}
	
	public void pause() {
		if (this.currentPlayer != null) {
			this.currentPlayer.pause();
		}
	}
	
	public void restart() {
		this.currentPlayer.play();
	}
	
	public void stopThread() {
		if (this.currentPlayer != null) {
			this.currentPlayer.pause();
		}
		this.stoped = true;
	}
	
	public long getTotalMS() {
		long totalMS = 0l;
		for (int i = 0; i < playingIndex - 1; i++) {
			totalMS += dataObjects.get(i).getLength();
		}
		return totalMS + currentPlayer.getTotalMS();
	}
	
	// TODO
	public void setMS(long ms) {
		long totalMS = 0l;
		this.currentPlayer.pause();
		for (int i = 0, l = dataObjects.size() - 1; i < l; i++) {
			if ((totalMS += dataObjects.get(i).getLength()) > ms) {
				
				playingIndex = i;
				lastMS = 0;
				break;
			}
		}
		
	}
	
}
