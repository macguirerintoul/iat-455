
/*File Exercise3.java

 IAT455 - Workshop week 6

 **********************************************************/
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

class Exercise3 extends Frame {
	BufferedImage cover;
	BufferedImage message;
	BufferedImage encodedImage;
	BufferedImage fourBitImage;
	BufferedImage brightImage;

	int width; // width of the image
	int height; // height of the image

	public Exercise3() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			cover = ImageIO.read(new File("cover.jpg"));
			message = ImageIO.read(new File("message.jpg"));
		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 6 workshop");
		this.setVisible(true);

		width = cover.getWidth();
		height = cover.getHeight();
		encodedImage = encodeMessage(cover, message);
		fourBitImage = reduceTo4Bits(encodedImage); // WRITE YOUR OWN reduceTo2Bits(srcImage);
		brightImage = modifyBrightness(fourBitImage, 15); // WRITE YOUR OWN modifyBrightness(twoBitImage, 85);

		// Anonymous inner-class listener to terminate program
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener
	}// end constructor

	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}

	private BufferedImage encodeMessage(BufferedImage cover, BufferedImage message) {
		BufferedImage result = new BufferedImage(cover.getWidth(), cover.getHeight(), cover.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int coverRGB = cover.getRGB(x, y);
				int messageRGB = message.getRGB(x, y);
				int newCoverRGB = coverRGB & 0xFFF0F0F0;
				int newMessageRGB = messageRGB & 0xFFF0F0F0;
				String newCoverInBinary = Integer.toBinaryString(newCoverRGB);
				String newMessageInBinary = Integer.toBinaryString(newMessageRGB);

				int r = Integer.parseInt(newCoverInBinary.substring(8, 12) + newMessageInBinary.substring(8, 12), 2);
				int g = Integer.parseInt(newCoverInBinary.substring(16, 20) + newMessageInBinary.substring(16, 20), 2);
				int b = Integer.parseInt(newCoverInBinary.substring(24, 28) + newMessageInBinary.substring(24, 28), 2);

				result.setRGB(x, y, new Color(r, g, b).getRGB());
			}
		}
		return result;
	}

	private BufferedImage reduceTo4Bits(BufferedImage srcImage) {
		BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = srcImage.getRGB(x, y);
				int newRGB = rgb & 0xFF0F0F0F;
				result.setRGB(x, y, newRGB);
			}
		}
		return result;
	}

	private BufferedImage modifyBrightness(BufferedImage srcImage, int multiplier) {
		BufferedImage result = new BufferedImage(srcImage.getWidth(), srcImage.getHeight(), srcImage.getType());
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int rgb = srcImage.getRGB(x, y);
				int r = getRed(rgb);
				int g = getGreen(rgb);
				int b = getBlue(rgb);
				float hsb[] = Color.RGBtoHSB(r, g, b, null);
				float newBrightness = hsb[2] * multiplier;
				result.setRGB(x, y, Color.HSBtoRGB(hsb[0], hsb[1], newBrightness));
			}
		}
		return result;
	}

	public void paint(Graphics g) {

		// if working with different images, this may need to be adjusted
		int w = width;
		int h = height;

		this.setSize(w * 4 + 300, h + 90);

		g.drawImage(encodedImage, 25, 50, w, h, this);
		g.drawImage(fourBitImage, 25 + w + 45, 50, w, h, this);
		g.drawImage(brightImage, 25 + w * 2 + 85, 50, w, h, this);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Original Image", 25, 45);
		g.drawString("2 bits per channel", 25 + w + 45, 45);
		g.drawString("Brightness * 85 = Result Image", 25 + w * 2 + 85, 45);
	}
	// =======================================================//

	public static void main(String[] args) {

		Exercise3 img = new Exercise3();// instantiate this object
		img.repaint();// render the image

	}// end main
}
// =======================================================//
