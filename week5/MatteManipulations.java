
/*File MatteManipulations.java

 IAT455 - Workshop week 5
 Matte Creation and Manipulations

 **********************************************************/
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.WritableRaster;
import java.io.File;

import javax.imageio.ImageIO;

class MatteManipulations extends Frame {
	BufferedImage skullImage;
	BufferedImage boardImage;
	BufferedImage carvingImage;
	BufferedImage redImage;

	BufferedImage supressedImage;
	BufferedImage invertedMatteImage;
	BufferedImage newBackgroundImage;
	BufferedImage colorDiffResultImage;

	BufferedImage subtractedImage;
	BufferedImage improvedMatteImage;
	BufferedImage overImage;
	BufferedImage backgroundImage;

	int width; // width of the image
	int height; // height of the image

	public MatteManipulations() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			skullImage = ImageIO.read(new File("skull.jpg"));
			boardImage = ImageIO.read(new File("board0.jpg"));
			redImage = ImageIO.read(new File("red.jpg"));
			carvingImage = ImageIO.read(new File("carving.jpg"));
			backgroundImage = ImageIO.read(new File("backgroundImage.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 5 workshop - Matte Creation and Manipulations");
		this.setVisible(true);

		width = skullImage.getWidth();
		height = skullImage.getHeight();

		// Color Difference Method
		supressedImage = suppressToBlack(skullImage);
		invertedMatteImage = createInvertedMatte(skullImage);
		newBackgroundImage = combineImages(invertedMatteImage, boardImage, Operations.multiply);
		colorDiffResultImage = combineImages(supressedImage, newBackgroundImage, Operations.add);

		// Difference Matting
		subtractedImage = combineImages(carvingImage, redImage, Operations.subtract);
		improvedMatteImage = improveMatte(subtractedImage);
		overImage = over(carvingImage, improvedMatteImage, backgroundImage);

		// Anonymous inner-class listener to terminate program
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener
	}// end constructor

	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2, Operations op) {
		BufferedImage result = new BufferedImage(src1.getWidth(), src1.getHeight(), src1.getType());
		for (int x = 0; x < src1.getWidth(); x++) {
			for (int y = 0; y < src1.getHeight(); y++) {
				int pixel1 = src1.getRGB(x, y);
				int pixel2 = src2.getRGB(x, y);

				int red1 = getRed(pixel1);
				int green1 = getGreen(pixel1);
				int blue1 = getBlue(pixel1);

				int red2 = getRed(pixel2);
				int green2 = getGreen(pixel2);
				int blue2 = getBlue(pixel2);
				switch (op) {
				case multiply:
					int newRed = clip((int) (red1 * ((double) red2 / 255)));
					int newGreen = clip((int) (green1 * ((double) green2 / 255)));
					int newBlue = clip((int) (blue1 * ((double) blue2 / 255)));
					result.setRGB(x, y, new Color(newRed, newGreen, newBlue).getRGB());
					break;
				case add:
					result.setRGB(x, y, new Color(clip(red1 + red2), clip(green1 + green2), clip(blue1 + blue2)).getRGB());
					break;
				case subtract:
					int newR = Math.abs(red1 - red2);
					int newG = Math.abs(green1 - green2);
					int newB = Math.abs(blue1 - blue2);
					result.setRGB(x, y, new Color(newR, newG, newB).getRGB());
					break;
				default:
					break;
				}
			}
		}
		return result;
	}

	public BufferedImage suppressToBlack(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = src.getRGB(x, y);
				int red = getRed(pixel);
				int green = getGreen(pixel);
				int blue = getBlue(pixel);
				if (blue > green) {
					result.setRGB(x, y, new Color(red, green, green).getRGB());
				} else {
					result.setRGB(x, y, new Color(red, green, blue).getRGB());
				}
			}
		}

		return result;
	}

	public BufferedImage createInvertedMatte(BufferedImage src) {
		BufferedImage invertedMatte = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = src.getRGB(x, y);
				int matte = clip(getBlue(pixel) - Math.max(getGreen(pixel), getRed(pixel)));
				invertedMatte.setRGB(x, y, new Color(matte, matte, matte).getRGB());
			}
		}

		return invertedMatte;
	}

	public BufferedImage invert(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		// Write your code here

		return result;
	}

	public BufferedImage over(BufferedImage foreground, BufferedImage matte, BufferedImage background) {
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

	public BufferedImage improveMatte(BufferedImage src) {
		BufferedImage matte = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		/*
		 * For each pixel, •Get its red/green/blue value •Get its HSB representation //
		 * hsb[0], hsb[1], hsb[2] •float newBrightness= hsb[2] > 0.05 ? 1 : hsb[2] •int
		 * newRgb= Color.HSBtoRGB(hsb[0], 0, brightness);
		 */
		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				int pixel = src.getRGB(x, y);
				float[] hsb = Color.RGBtoHSB(getRed(pixel), getGreen(pixel), getBlue(pixel), null);
				float newBrightness = hsb[2] > 0.05 ? 1 : hsb[2];
				int newRGB = Color.HSBtoRGB(hsb[0], 0, newBrightness);
				matte.setRGB(x, y, newRGB);
			}
		}

		return matte;
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

	public void paint(Graphics g) {

		// if working with different images, this may need to be adjusted
		int w = width;
		int h = height;

		this.setSize(w * 7 + 300, h * 4 + 150);

		g.drawImage(skullImage, 25, 50, w, h, this);
		g.drawImage(supressedImage, 25 + w + 25, 50, w, h, this);
		g.drawImage(invertedMatteImage, 25 + w * 2 + 50, 50, w, h, this);
		g.drawImage(newBackgroundImage, 25 + w * 3 + 75, 50, w, h, this);
		g.drawImage(colorDiffResultImage, w * 4 + 125, 50, w, h, this);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Skull image", 25, 45);
		g.drawString("Step 1: suppressed image", 50 + w, 45);
		g.drawString("Step 2: inverted matte image", 72 + 2 * w, 45);
		g.drawString("Step 3: new background image", 100 + 3 * w, 45);
		g.drawString("Step 4: result image", 125 + 4 * w, 45);

		g.drawImage(carvingImage, 25, 130 + h, w, h, this);
		g.drawImage(redImage, 25 + w + 25, 130 + h, w, h, this);
		g.drawImage(subtractedImage, 25 + w * 2 + 50, 130 + h, w, h, this);
		g.drawImage(improvedMatteImage, 25 + w * 3 + 75, 130 + h, w, h, this);
		g.drawImage(overImage, w * 4 + 125, 130 + h, w, h, this);

		g.drawString("Carving Image", 25, 120 + h);
		g.drawString("Red background", 60 + w, 120 + h);
		g.drawString("Subtracted", 82 + 2 * w, 120 + h);
		g.drawString("Improved matte", 110 + 3 * w, 120 + h);
		g.drawString("Over", 135 + 4 * w, 120 + h);

		Font f2 = new Font("Verdana", Font.BOLD, 10);
		g.setFont(f2);

		g.drawString("Difference Matting", w * 5 + 130, 250 + h);
		g.drawString("Color Difference Method", w * 5 + 130, 130);
	}
	// =======================================================//

	public static void main(String[] args) {

		MatteManipulations img = new MatteManipulations();// instantiate this object
		// img.repaint();// render the image

	}// end main
}
// =======================================================//
