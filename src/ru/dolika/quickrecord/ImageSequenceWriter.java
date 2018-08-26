package ru.dolika.quickrecord;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageSequenceWriter implements Runnable, AutoCloseable {

	final long timestamp;

	public ImageSequenceWriter(long timestamp) {
		this.timestamp = timestamp;
	}

	private boolean finish = false;

	@Override
	public void run() {
		BufferedImage image;
		Robot r;
		if (!new File(timestamp + "").exists()) {
			new File(timestamp + "").mkdir();
		}
		try {
			r = new Robot();
			while (!finish) {
				image = r.createScreenCapture(new Rectangle(new Dimension(3200, 1080)));
				try {
					ImageIO.write(image, "jpg", new File(timestamp + "/" + System.currentTimeMillis() + ".jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void finish() {
		finish = true;
	}

	@Override
	public void close() {
		finish();
	}

}
