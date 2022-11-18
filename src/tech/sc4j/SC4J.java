package tech.sc4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import tech.sc4j.music.SCMusic;
import tech.sc4j.util.SCWebUtil;

public class SC4J {
	
	private static SCMusic currentMusic;
	private static int songIndex, resultOffset, playingTime;
	private static String lastSearchTitle;
	private static final List<SCMusic> currentResult;
	private static volatile boolean paused;
	
	public static boolean shuffling;
	public static RepeatType repeatType;

	static {
		repeatType = RepeatType.Repeat;
		currentResult = new ArrayList<>();
	}
	
	public static void togglePaused() {
		paused = !paused;
		if (paused) {
			currentMusic.pause();
		} else {
			currentMusic.restart();
		}
	}
	
	public static boolean isPaused() {
		return paused;
	}
	
	public static void play(int index) {
		if (currentResult == null) return;
		playingTime = 0;
		songIndex = index;
		paused = false;
		if (songIndex >= currentResult.size()) {
			updateResult();
		}
		if (currentMusic != null) {
			currentMusic.stopThread();
		}
		currentMusic = getResult().get(index);
		currentMusic.play();
	}
	
	public static void onMusicEnd() {
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
	
	public static void skip(boolean back) {
		if (currentResult == null) return;
		// TODO
	}
	
	public static List<SCMusic> getResult() {
		return currentResult;
	}
	
	public static SCMusic getMusic() {
		return currentMusic;
	}
	
	public static void search(final String title) {
		lastSearchTitle = title;
		resultOffset = 0;
		currentResult.clear();
		currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(title, 20, 0)).getMusicItems());
	}
	
	public static void search(final String title, int maximum, int offset) {
		lastSearchTitle = title;
		resultOffset = 0;
		currentResult.clear();
		currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(title, maximum, offset)).getMusicItems());
	}
	
	private static void updateResult() {
		if (lastSearchTitle == null) return;
		resultOffset++;
		currentResult.addAll(new SCSearchResult(SCWebUtil.titleToURL(lastSearchTitle, 20, resultOffset)).getMusicItems());
	}

}
