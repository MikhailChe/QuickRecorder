package ru.dolika.quickrecord;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

public class AudioRecorder implements Runnable, Closeable {

	static final AudioFormat format;

	static {
		format = new AudioFormat(48000, 16, 1, true, false);
	}

	final TargetDataLine line;

	final long timestamp;
	final String prefix;

	public AudioRecorder(TargetDataLine line, long timestamp, String prefix) {
		this.line = line;
		this.timestamp = timestamp;
		this.prefix = prefix;
	}

	@Override
	public void run() {

		if (!new File(timestamp + "").exists()) {
			new File(timestamp + "").mkdir();
		}
		try {
			line.open(format);
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
			line.close();
			return;
		}
		line.start();
		AudioInputStream ais = new AudioInputStream(line);

		try {
			long numbytes = AudioSystem.write(ais, Type.WAVE, new File(timestamp + "/" + prefix + ".wav"));
			System.out.println(prefix + ": " + numbytes);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void close() {
		finish();
	}

	public void finish() {
		line.stop();
		line.close();
	}

}
