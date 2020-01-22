
/*File ImageBasics.java

 IAT455 - Workshop week 3
 Basic Image Manipulation
 
 Starter code
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

class ImageManipulation extends Frame {
	BufferedImage testImage;
	BufferedImage testImage1;

	BufferedImage brightnessImage;
	BufferedImage RGBmultiplyImage;
	BufferedImage invertImage;
	BufferedImage contrastImage;
	BufferedImage monochrome1Image;
	BufferedImage monochrome2Image;
	BufferedImage edgeDetectionImage;

	int width; // width of the image
	int height; // height of the image

	int width1; // width of the image
	int height1; // height of the image

	public ImageManipulation() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			testImage = ImageIO.read(new File("bird1.jpg"));
			testImage1 = ImageIO.read(new File("church.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 3 workshop - Basic image manipulation");
		this.setVisible(true);

		width = testImage.getWidth();
		height = testImage.getHeight();

		width1 = testImage1.getWidth();
		height1 = testImage1.getHeight();

		brightnessImage = filterImage(testImage, Filters.brightness);
		RGBmultiplyImage = filterImage(testImage, Filters.RGBmultiply);
		invertImage = filterImage(testImage, Filters.invert);
		contrastImage = filterImage(testImage, Filters.contrast);
		monochrome1Image = filterImage(testImage, Filters.monochrome_average);
		monochrome2Image = filterImage(testImage, Filters.monochrome_perceptual);

		edgeDetectionImage = convolve(testImage1);

		// Anonymous inner-class listener to terminate program
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener
	}// end constructor

	public BufferedImage filterImage(BufferedImage img, Filters filt)
	// produce the result image for each operation
	{
		int width = img.getWidth();
		int height = img.getHeight();

		WritableRaster wRaster = img.copyData(null);
		BufferedImage copy = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

		// apply the operation to each pixel
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				int rgb = img.getRGB(i, j);
				copy.setRGB(i, j, filterPixel(rgb, filt));
			}
		}
		return copy;
	}

	public int filterPixel(int rgb, Filters filt) { // operation to be applied to each pixel

		int alpha = (rgb >>> 24) & 0xff;
		int red = (rgb >>> 16) & 0xff;
		int green = (rgb >>> 8) & 0xff;
		int blue = rgb & 0xff;

		int new_red;
		int new_green;
		int new_blue;

		switch (filt) {
		case brightness: // O = I*2
			new_red = (red * 2 > 255) ? 255 : (red * 2);
			new_green = (green * 2 > 255) ? 255 : (green * 2);
			new_blue = (blue * 2 > 255) ? 255 : (blue * 2);
			return new Color(new_red, new_green, new_blue, alpha).getRGB();

		case RGBmultiply: // R=R*0.1, G=G*1.25, B=B*1
			new_red = (int) (red * 0.1 > 255 ? 255 : red * 0.1);
			new_green = (int) (green * 1.25 > 255 ? 255 : green * 1.25);
			// blue does not need to be multiplied by 1
			return new Color(new_red, new_green, blue, alpha).getRGB();

		case invert: // O=1=I
			new_red = 255 - red;
			new_green = 255 - green;
			new_blue = 255 - blue;
			return new Color(new_red, new_green, new_blue, alpha).getRGB();

		case contrast: // O=(I-0.33)*3
			// ternary operators to restrict the values to between 0 and 255
			new_red = (int) ((red - 0.33 * 255) * 3 > 255 ? 255 : (red - 0.33 * 255) * 3 < 0 ? 0 : (red - 0.33 * 255) * 3);
			new_green = (int) ((green - 0.33 * 255) * 3 > 255 ? 255
					: (green - 0.33 * 255) * 3 < 0 ? 0 : (green - 0.33 * 255) * 3);
			new_blue = (int) ((blue - 0.33 * 255) * 3 > 255 ? 255
					: (blue - 0.33 * 255) * 3 < 0 ? 0 : (blue - 0.33 * 255) * 3);
			return new Color(new_red, new_green, new_blue, alpha).getRGB();

		case monochrome_average: // average R, G, B
			int avg = (red + green + blue) / 3;
			return new Color(avg, avg, avg, alpha).getRGB();

		case monochrome_perceptual: // human eye perception values
			int human = (int) (red * 0.309 + green * 0.609 + blue * 0.082);
			return new Color(human, human, human, alpha).getRGB();

		case blank_image:
			return rgb | 0xFFFFFFFF;

		default:
			return rgb | 0xFFFFFFFF;
		}
	}

	// Edge detection algorithm - spatial filtering by implementing the moving
	// window manually
	public BufferedImage convolve(BufferedImage image) {
		// write algorithm to perform edge detection based on spatial convolution, as
		// described in lecture/textbook
		// return a Bufferedimage = edgeDetectionImage

		return filterImage(testImage, Filters.blank_image); // remove this line, when finished with the algorithm
	}

	public void paint(Graphics g) {

		// if working with different images, this may need to be adjusted
		int w = width / 3;
		int h = height / 3;

		this.setSize(w * 5 + 300, h * 3 + 150);

		g.drawImage(testImage, 25, 50, w, h, this);
		g.drawImage(brightnessImage, 25 + w + 25, 50, w, h, this);
		g.drawImage(RGBmultiplyImage, 25 + w * 2 + 50, 50, w, h, this);
		g.drawImage(invertImage, 25 + w * 3 + 75, 50, w, h, this);
		g.drawImage(contrastImage, w * 4 + 125, 50, w, h, this);

		g.drawImage(monochrome1Image, 25, h + 30 + 250, w, h, this);
		g.drawImage(monochrome2Image, 25 + w + 25, h + 30 + 250, w, h, this);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Original image", 25, 45);
		g.drawString("Brightness x2.0", 50 + w, 45);
		g.drawString("RGB Multiply 0.1, 1.25, 1", 72 + 2 * w, 45);
		g.drawString("Invert", 100 + 3 * w, 45);
		g.drawString("Contrast", 125 + 4 * w, 45);

		g.drawString("Monochrome 1", 25, 45 + h + 220);
		g.drawString("Monochrome 2", 50 + w, 45 + h + 220);

		g.drawString("Monochrome 1 - based on averaging red, green, blue", 15, h + h / 2 + 60);
		g.drawString("Monochrome 2 - based on human perception of colors:", 15, h + h / 2 + 90);
		g.drawString("R*0.309+G*0.609+B*0.082", 15, h + h / 2 + 60 + 60);

		g.drawString("Edge detection - based on spatial convolution", w * 2 + 170, 20 + h + 100);

		g.drawImage(testImage1, w * 2 + 150, 50 + h + 100, width1 / 2, height1 / 2, this);
		g.drawImage(edgeDetectionImage, w * 2 + 180 + width1 / 2, 50 + h + 100, width1 / 2, height1 / 2, this);
	}
	// =======================================================//

	public static void main(String[] args) {

		ImageManipulation img = new ImageManipulation();// instantiate this object
		img.repaint();// render the image

	}// end main
}
