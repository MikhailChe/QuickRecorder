package ru.dolika.quickrecord;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class QuickRecordLauncher extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5695666427922593662L;

	public static void main(String[] args) {

		List<Mixer.Info> mixers = Arrays.asList(AudioSystem.getMixerInfo());
		mixers = mixers
				.stream()
				.filter(a -> (AudioSystem.getMixer(a).getTargetLineInfo().length > 0))
				.filter(a -> !a.toString().toLowerCase().contains("port"))
				.collect(Collectors.toList());

		mixers.forEach(a -> {
			try {
				System.out.println(new String(a.toString().getBytes(("iso-8859-1")), "windows-1251"));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		Object[] choices = mixers.stream().map(a -> {
			try {
				return new String(a.toString().getBytes(("iso-8859-1")), "windows-1251");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "Invalid encoding";
		}).collect(Collectors.toList()).toArray();
		int choice1 = JOptionPane
				.showOptionDialog(null, "Select audio mixer \nto write sound from", "Select mixer",
						JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);
		int choice2 = JOptionPane
				.showOptionDialog(null, "Select audio mixer \nto write sound from", "Select mixer",
						JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]);

		if (choice1 == JOptionPane.CLOSED_OPTION || choice2 == JOptionPane.CLOSED_OPTION) {
			return;
		}

		try {
			new QuickRecordLauncher(
					(TargetDataLine) AudioSystem
							.getMixer(mixers.get(choice1))
							.getLine(new Line.Info(TargetDataLine.class)),
					(TargetDataLine) AudioSystem
							.getMixer(mixers.get(choice2))
							.getLine(new Line.Info(TargetDataLine.class))).setVisible(true);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
	}

	AudioRecorder micRecorder;
	AudioRecorder mixRecorder;
	ImageSequenceWriter screenshot;

	public QuickRecordLauncher(TargetDataLine line1, TargetDataLine line2) {
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		long timestamp = System.currentTimeMillis();
		micRecorder = new AudioRecorder(line1, timestamp, "mic");
		mixRecorder = new AudioRecorder(line2, timestamp, "mix");
		screenshot = new ImageSequenceWriter(timestamp);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(e.toString());
				micRecorder.close();
				mixRecorder.close();
				screenshot.close();
			}
		});
		new Thread(micRecorder).start();
		new Thread(mixRecorder).start();
		new Thread(screenshot).start();
	}
}
