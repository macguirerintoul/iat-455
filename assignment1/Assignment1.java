import java.io.ByteArrayOutputStream;
import java.io.File;
import java.awt.Frame;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Graphics;

class Assignment1 extends Frame {
	BufferedImage portrait;
	BufferedImage background;
	BufferedImage matte;
	BufferedImage composite;

	int width; // width of the image
	int height; // height of the image

	public Assignment1() {
		try {
			portrait = ImageIO.read(new File("portrait.jpg"));
			background = ImageIO.read(new File("background.jpg"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Assignment 1");
		this.setVisible(true);
		width = portrait.getWidth();
		height = portrait.getHeight();
		question3();
		question5();

		// Terminates the program when the window is closed
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void question3() {
		System.out.println("Question 3");
		byte[] input = "1111111111110001111111111100000111111111111111111111100111111111111".getBytes();
		System.out.println("Original length:");
		System.out.println(input.length);

		ByteArrayOutputStream output = new ByteArrayOutputStream();
		byte lastByte = input[0];
		int matchCount = 1;
		for (int i = 1; i < input.length; i++) {
			byte thisByte = input[i];
			if (lastByte == thisByte) {
				matchCount++;
			} else {
				output.write((byte) matchCount);
				output.write((byte) lastByte);
				matchCount = 1;
				lastByte = thisByte;
			}
		}
		output.write((byte) matchCount);
		output.write((byte) lastByte);

		System.out.println("Compressed length:");
		System.out.println(output.toByteArray().length);

		System.out.println("Reduction in size:");
		System.out.println(100 - ((double) output.toByteArray().length / input.length) * 100 + "%");
	}

	private void question5() {

	}

	public void paint(Graphics g) {

		// if working with different images, this may need to be adjusted
		int w = width;
		int h = height;

		this.setSize(w * 4 + 300, h * 1 + 150);

		g.drawImage(portrait, 25, 50, w, h, this);
		g.drawImage(background, 25 + w + 25, 50, w, h, this);
		g.drawImage(matte, 25 + w + 25 + w, 50, w, h, this);
		g.drawImage(composite, 25 + w + 25 + w + 25, 50, w, h, this);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Original portrait", 25, 45);
		g.drawString("Original background", 50 + w, 45);
		g.drawString("Matte of portrait", 72 + 2 * w, 45);
		g.drawString("Final composite", 100 + 3 * w, 45);
	}

	public static void main(String[] args) {
		Assignment1 a1 = new Assignment1();
	}
}