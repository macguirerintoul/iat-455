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
	BufferedImage suppressed;
	BufferedImage matte;
	BufferedImage betterMatte;
	BufferedImage composite;

	int width; // width of the image
	int height; // height of the image

	public Assignment1() {
		try {
			portrait = ImageIO.read(new File("portrait-g.jpg"));
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
		// suppressed = suppressToBlack(portrait);
		matte = createMatte(portrait);
		composite = over(portrait, background, matte);
	}

	public BufferedImage over(BufferedImage foreground, BufferedImage background, BufferedImage matte) {
		BufferedImage result = new BufferedImage(foreground.getWidth(), foreground.getHeight(), foreground.getType());

		for (int x = 0; x < foreground.getWidth(); x++) {
			for (int y = 0; y < foreground.getHeight(); y++) {
				int fR = getRed(foreground.getRGB(x, y));
				int bR = getRed(background.getRGB(x, y));
				int mR = getRed(matte.getRGB(x, y));
				int newR = clip(fR * (mR / 255) + bR * (1 - (mR / 255)));

				int fG = getGreen(foreground.getRGB(x, y));
				int bG = getGreen(background.getRGB(x, y));
				int mG = getGreen(matte.getRGB(x, y));
				int newG = clip(fG * (mG / 255) + bG * (1 - (mG / 255)));

				int fB = getBlue(foreground.getRGB(x, y));
				int bB = getBlue(background.getRGB(x, y));
				int mB = getBlue(matte.getRGB(x, y));
				int newB = clip(fB * (mB / 255) + bB * (1 - (mB / 255)));

				result.setRGB(x, y, new Color(newR, newG, newB).getRGB());
			}
		}

		return result;
	}

	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}

	public BufferedImage suppressToBlack(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = src.getRGB(x, y);
				int red = getRed(pixel);
				int green = getGreen(pixel);
				int blue = getBlue(pixel);
				if (green > blue) {
					result.setRGB(x, y, new Color(red, blue, blue).getRGB());
				} else {
					result.setRGB(x, y, new Color(red, green, blue).getRGB());
				}
			}
		}
		return result;
	}

	public BufferedImage createMatte(BufferedImage src) {
		BufferedImage matte = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = src.getRGB(x, y);
				int matteValue = 255 - clip(getGreen(pixel) - Math.max(getBlue(pixel), getRed(pixel)));
				if (matteValue < 245) {
					matteValue = 0;
				} else {
					matteValue = 255;
				}
				matte.setRGB(x, y, new Color(matteValue, matteValue, matteValue).getRGB());
			}
		}
		return matte;
	}

	public void paint(Graphics g) {

		// if working with different images, this may need to be adjusted
		int w = width;
		int h = height;

		this.setSize(w * 4 + 300, h * 1 + 150);

		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setColor(Color.BLACK);
		g.setFont(f1);

		g.drawString("Original portrait", 25, 45);
		g.drawImage(portrait, 25, 50, w, h, this);

		g.drawString("Original background", 50 + w, 45);
		g.drawImage(background, 2 * 25 + w, 50, w, h, this);

		g.drawString("Matte", 72 + 2 * w, 45);
		g.drawImage(matte, 4 * 25 + 2 * w, 50, w, h, this);

		g.drawString("Composite", 100 + 3 * w, 45);
		g.drawImage(composite, 6 * 25 + 3 * w, 50, w, h, this);

	}

	public static void main(String[] args) {
		Assignment1 a1 = new Assignment1();
	}
}