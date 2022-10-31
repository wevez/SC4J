package tech.sc4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import jaco.mp3.player.MP3Player;
import tech.sc4j.music.SCMusic;
import tech.sc4j.util.SCWebUtil;

public class SC4J {
	
	public static final SC4J instance = new SC4J();
	
	public static void main(final String[] args) {
	}
	
	private SCMusic currentMusic;
	private int songIndex, resultOffset, playingTime;
	private String lastSearchTitle;
	private final List<SCMusic> currentResult;
	private volatile boolean paused;
	private long startTime;
	private MP3Player currentPlayer;
	
	public boolean shuffling;
	public RepeatType repeatType;
	
	public SC4J() {
		this.repeatType = RepeatType.Repeat;
		this.currentResult = new ArrayList<>();
	}
	
	public void togglePaused() {
		this.paused = !this.paused;
		if (this.currentPlayer != null) {
			if (this.paused) {
				this.currentPlayer.pause();
			} else {
				this.currentPlayer.play();
			}
		}
	}
	
	public boolean isPaused() {
		return this.paused;
	}
	
	public void play(int index) {
		this.playingTime = 0;
		this.startTime = System.currentTimeMillis();
		this.songIndex = index;
		this.paused = false;
		if (this.songIndex >= this.currentResult.size()) {
			this.updateResult();
		}
		if (this.currentResult == null) return;
		if (this.currentPlayer != null && this.currentPlayer.isPlaying()) {
			this.currentPlayer.stop();
			this.currentMusic.stopThread();
		}
		currentMusic = getResult().get(index);
		new Thread() {
			@Override
			public void run() {
				currentMusic.play();
				super.run();
			}
		}.start();
	}
	
	public void onMusicEnd() {
		this.currentPlayer = null;
		if (shuffling) {
			currentResult.get(ThreadLocalRandom.current().nextInt(currentResult.size())).play();
		} else {
			switch (repeatType) {
			case None:
				break;
			case Repeat:
				play(++songIndex);
				break;
			case OneRepeat:
				currentMusic.play();
				break;
			}
		}
	}
	
	public void skip(boolean back) {
		if (this.currentResult == null) return;
		if (this.currentPlayer != null) {
			this.currentPlayer.stop();
		}
	}
	
	public List<SCMusic> getResult() {
		return this.currentResult;
	}
	
	public SCMusic getMusic() {
		return this.currentMusic;
	}
	
	public MP3Player setPlayer(final MP3Player player) {
		return this.currentPlayer = player;
	}
	
	public void search(final String title) {
		this.lastSearchTitle = title;
		this.resultOffset = 0;
		this.currentResult.clear();
		this.currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(title, 20, 0)).getMusicItems());
	}
	
	public void search(final String title, int maximum, int offset) {
		this.lastSearchTitle = title;
		this.resultOffset = 0;
		this.currentResult.clear();
		this.currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(title, maximum, offset)).getMusicItems());
	}
	
	private void updateResult() {
		if (this.lastSearchTitle == null) return;
		resultOffset++;
		this.currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(this.lastSearchTitle, 20, resultOffset)).getMusicItems());
	}

}
