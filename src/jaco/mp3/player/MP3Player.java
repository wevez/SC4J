/*
 * Copyright (C) Cristian Sulea ( http://cristiansulea.entrust.ro )
 * 
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package jaco.mp3.player;

import jaco.mp3.resources.Decoder;
import jaco.mp3.resources.Frame;
import jaco.mp3.resources.SampleBuffer;
import jaco.mp3.resources.SoundStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.SourceDataLine;

/**
 * Java MP3 Player
 * 
 * @version 0.10.2, June 16, 2011
 * @author Cristian Sulea ( http://cristiansulea.entrust.ro )
 */
public class MP3Player {

  private transient boolean isPaused = false;
  private transient boolean isStopped = true;

  private transient volatile int volume = 50;

  private transient volatile Thread playingThread = null;
  private transient volatile SourceDataLine playingSource = null;
  private transient volatile int playingSourceVolume = 50;
  private long lastMS, totalMS;
  
  private Object object;
  
  public MP3Player set(File file, boolean recursively) {
	  if (file.isFile()) {
		  if (file.getName().endsWith(".mp3")) {
			  synchronized (MP3Player.this) {
				  this.object = file;
			  }
		  }
	  } else if (file.isDirectory()) {
		  File[] files = file.listFiles();
		  for (File file2 : files) {
			  if (file2.isFile() || recursively) {
				  set(file2, recursively);
			  }
		  }
	  } else {
		  throw new IllegalArgumentException("WTF is this? ( " + file + " )");
	  }
	  return this;
  }

  /**
   * Appends the specified file (or all the files, recursively, if represents a
   * folder) to the end of the play list.
   */
  public MP3Player set(File file) {
	  set(file, true);
	  return this;
  }

  /**
   * Appends the specified URL to the end of the play list.
   */
  public MP3Player set(URL url) {
	  synchronized (MP3Player.this) {
		  object = url;
	  }
	  return this;
  }
  
  public MP3Player set(SoundStream inputStream) {
	  object = inputStream;
	  return this;
  }

  /**
   * Starts the play (or resume if is paused).
   */
  public void play() {
	  this.lastMS = System.currentTimeMillis();
	  synchronized (MP3Player.this) {
		  if (isPaused) {
			  isPaused = false;
			  MP3Player.this.notifyAll();
			  return;
		  }
	  }
	  stop();
	  if (object == null) {
		  return;
	  }
	  synchronized (MP3Player.this) {
		  isStopped = false;
	  }
	  if (playingThread == null) {
		  playingThread = new Thread(() -> {
    		  InputStream inputStream = null;
    		  try {
    			  SoundStream soundStream = null;
    			  if (object instanceof SoundStream) {
    				  soundStream = (SoundStream) object;
    			  } else {
    				  if (object instanceof File) {
	    				  inputStream = new FileInputStream((File) object);
	    			  } else if (object instanceof URL) {
	    				  inputStream = ((URL) object).openStream();
	    			  } else {
	    				  throw new IOException("this is impossible; how come the play list contains this kind of object? :: " + object.getClass());
	    			  }
    				  soundStream = new SoundStream(inputStream);
    			  }
    			  Decoder decoder = new Decoder();
    			  while (true) {
    				  synchronized (MP3Player.this) {
    					  if (isStopped) {
    						  break;
    					  }
    					  if (isPaused) {
    						  if (playingSource != null) {
    							  playingSource.flush();
    						  }
    						  playingSourceVolume = volume;
    						  try {
    							  MP3Player.this.wait();
    						  } catch (InterruptedException e) {
    							  e.printStackTrace();
    						  }
    						  continue;
    					  }
    				  }
    				  try {
    					  Frame frame = soundStream.readFrame();
    					  if (frame == null) {
    						  break;
    					  }
    					  if (playingSource == null) {
    						  int frequency = frame.frequency();
    						  int channels = (frame.mode() == Frame.SINGLE_CHANNEL) ? 1 : 2;
    						  AudioFormat format = new AudioFormat(frequency, 16, channels, true, false);
    						  Line line = AudioSystem.getLine(new DataLine.Info(SourceDataLine.class, format));
    						  playingSource = (SourceDataLine) line;
    						  playingSource.open(format);
    						  playingSource.start();
    					  }
    					  SampleBuffer output = (SampleBuffer) decoder.decodeFrame(frame, soundStream);
    					  short[] buffer = output.getBuffer();
    					  int offs = 0;
    					  int len = output.getBufferLength();
    					  if (playingSourceVolume != volume) {
    						  if (playingSourceVolume > volume) {
    							  playingSourceVolume--;
    							  if (playingSourceVolume < volume) {
    								  playingSourceVolume = volume;
    							  }
    						  } else {
    							  playingSourceVolume++;
    							  if (playingSourceVolume > volume) {
    								  playingSourceVolume = volume;
    							  }
    						  }
    						  setVolume(playingSource, playingSourceVolume);
    					  }
    					  playingSource.write(toByteArray(buffer, offs, len), 0, len * 2);
    					  soundStream.closeFrame();
    				  } catch (Exception e) {
    					  e.printStackTrace();
    					  break;
    				  }
    			  }
    			  if (playingSource == null) {
    				  System.out.println("source is null because first frame is null, so probably the file is not a mp3");
    			  } else {
    				  synchronized (MP3Player.this) {
    					  if (!isStopped) {
    						  playingSource.drain();
    					  } else {
    						  playingSource.flush();
    					  }
    				  }
    				  playingSource.stop();
    				  playingSource.close();
    				  playingSource = null;
    			  }
    			  try {
    				  soundStream.close();
    			  } catch (Exception e) {
    				  e.printStackTrace();
    			  }
    		  } catch (IOException e) {
    			  e.printStackTrace();
    		  } finally {
    			  if (inputStream != null) {
    				  try {
    					  inputStream.close();
    				  } catch (Exception e) {
    					  e.printStackTrace();
    				  }
    			  }
    		  }
    		  synchronized (MP3Player.this) {
    			  isPaused = false;
    			  isStopped = true;
    		  }
    		  playingThread = null;
    	  }) {
		  };
		  playingThread.start();
	  }
  	}

  public boolean isPlaying() {
	  synchronized (MP3Player.this) {
    	return !isPaused && !isStopped;
	  }
  }

  public void pause() {
	  if (!isPlaying()) {
		  return;
	  }
	  synchronized (MP3Player.this) {
		  isPaused = true;
		  MP3Player.this.notifyAll();
	  }
  	}

  public boolean isPaused() {
	  synchronized (MP3Player.this) {
		  return isPaused;
	  }
  }
  
  public long getTotalMS() {
	  return this.totalMS;
  }

  public void stop() {
	  this.totalMS += System.currentTimeMillis() - lastMS;
    synchronized (MP3Player.this) {
      isPaused = false;
      isStopped = true;
      MP3Player.this.notifyAll();
    }
    if (playingThread != null) {
      try {
        playingThread.join();
      } catch (InterruptedException e) {
    	  e.printStackTrace();
      }
    }
  }

  public boolean isStopped() {
    synchronized (MP3Player.this) {
      return isStopped;
    }
  }

  /**
   * Sets a new volume for this player. The value is actually the percent value,
   * so the value must be in interval [0..100].
   * 
   * @param volume
   *          the new volume
   * 
   * @throws IllegalArgumentException
   *           if the volume is not in interval [0..100]
   */
  public MP3Player setVolume(int volume) {

    if (volume < 0 || volume > 100) {
      throw new IllegalArgumentException("Wrong value for volume, must be in interval [0..100].");
    }

    this.volume = volume;

    return this;
  }

  /**
   * Returns the actual volume.
   */
  public int getVolume() {
    return volume;
  }


  private void setVolume(SourceDataLine source, int volume) {
    try {
      FloatControl gainControl = (FloatControl) source.getControl(FloatControl.Type.MASTER_GAIN);
      BooleanControl muteControl = (BooleanControl) source.getControl(BooleanControl.Type.MUTE);
      if (volume == 0) {
        muteControl.setValue(true);
      } else {
        muteControl.setValue(false);
        gainControl.setValue((float) (Math.log(volume / 100d) / Math.log(10.0) * 20.0));
      }
    } catch (Exception e) {
    	e.printStackTrace();
    }
  }

  /**
   * Retrieves the position in milliseconds of the current audio sample being
   * played. This method delegates to the <code>
   * AudioDevice</code> that is used by this player to sound the decoded audio
   * samples.
   */
  public int getPosition() {
    int pos = 0;
    if (playingSource != null) {
      pos = (int) (playingSource.getMicrosecondPosition() / 1000);
    }
    return pos;
  }

  private byte[] toByteArray(short[] ss, int offs, int len) {
    byte[] bb = new byte[len * 2];
    int idx = 0;
    int total = 0;
    short s;
    while (len-- > 0) {
      total += s = ss[offs++];
      bb[idx++] = (byte) s;
      bb[idx++] = (byte) (s >>> 8);
    }
    return bb;
  }

  	private void readObject(ObjectInputStream objectInputStream) throws ClassNotFoundException, IOException {
	  objectInputStream.defaultReadObject();
 	}

}
