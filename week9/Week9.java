
//IAT455 - Workshop week 9

//**********************************************************/
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.lang.String;

import javax.imageio.ImageIO;

class Week9 extends Frame { // controlling class
	BufferedImage src1;
	BufferedImage src1_bright;
	BufferedImage src1_brightGama;

	BufferedImage statueImg;
	BufferedImage backgroundImg;
	BufferedImage statueMatte;
	BufferedImage edge_mask;

	BufferedImage blurred;
	BufferedImage colorCorrected;
	BufferedImage coloredEdges;
	BufferedImage shadedStatue;
	BufferedImage finalResult;

	int width, width1;
	int height, height1;

	public Week9() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			src1 = ImageIO.read(new File("backdoor.jpg"));
			statueImg = ImageIO.read(new File("statue.jpg"));
			backgroundImg = ImageIO.read(new File("background.jpg"));
			statueMatte = ImageIO.read(new File("statue_mat0.jpg"));
			edge_mask = ImageIO.read(new File("edge_mask.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 9 workshop");
		this.setVisible(true);

		width = src1.getWidth();
		height = src1.getHeight();

		width1 = statueImg.getWidth();
		height1 = statueImg.getHeight();

		src1_bright = increaseBrightness(src1, 5);
		src1_brightGama = gammaIncreaseBrightness(src1, 0.65);

		BufferedImage background_copy = copyImg(backgroundImg);
		// produce copy of image to work around Java exception - see:
		// http://background-subtractor.googlecode.com/svn-history/r68/trunk/src/imageProcessing/ImageBlurrer.java
		// http://bugs.java.com/bugdatabase/view_bug.do?bug_id=4957775
		blurred = blur(background_copy);

		colorCorrected = colorCorrect(statueImg, blurred);

		coloredEdges = statueImg; // TODO: replace the statueImg with proper method call

		BufferedImage edgelessStatue = statueImg; // TODO: replace the statueImg with proper method call
		shadedStatue = statueImg; // TODO: replace the statueImg with proper method call

		finalResult = statueImg; // TODO: replace the statueImg with proper method call

		// Anonymous inner-class listener to terminate program
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener
	}// end constructor

	public BufferedImage combineImages(BufferedImage src1, BufferedImage src2, Operations op) {

		if (src1.getType() != src2.getType()) {
			System.out.println("Source Images should be of the same type");
			return null;
		}
		BufferedImage result = new BufferedImage(src1.getWidth(), src1.getHeight(), src1.getType());

		// TODO: Complete this code

		return result;
	}

	public BufferedImage increaseBrightness(BufferedImage src, int factor) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		int pixel, red, green, blue, new_red, new_green, new_blue = 0;

		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				pixel = src.getRGB(x, y);
				red = getRed(pixel);
				green = getGreen(pixel);
				blue = getBlue(pixel);
				new_red = clip(red * factor);
				new_green = clip(green * factor);
				new_blue = clip(blue * factor);

				result.setRGB(x, y, new Color(new_red, new_green, new_blue).getRGB());
			}
		}

		return result;
	}

	public BufferedImage gammaIncreaseBrightness(BufferedImage src, double gamma) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		int pixel, red, green, blue, new_red, new_green, new_blue = 0;
		double power = 1.00 / gamma;

		for (int x = 0; x < src.getWidth(); x++) {
			for (int y = 0; y < src.getHeight(); y++) {
				pixel = src.getRGB(x, y);
				red = getRed(pixel);
				green = getGreen(pixel);
				blue = getBlue(pixel);

				new_red = clip((int) Math.pow(red, power));
				new_green = clip((int) Math.pow(green, power));
				new_blue = clip((int) Math.pow(blue, power));

				result.setRGB(x, y, new Color(new_red, new_green, new_blue).getRGB());
			}
		}
		return result;
	}

	public BufferedImage blur(BufferedImage image) {
		float data[] = { 0.0625f, 0.125f, 0.0625f, 0.125f, 0.125f, 0.125f, 0.0625f, 0.125f, 0.0625f };
		Kernel kernel = new Kernel(3, 3, data);
		ConvolveOp convolve = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		return convolve.filter(image, null);
	}

	public static BufferedImage copyImg(BufferedImage input) {
		BufferedImage tmp = new BufferedImage(input.getWidth(), input.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = 0; x < input.getWidth(); x++) {
			for (int y = 0; y < input.getHeight(); y++) {
				tmp.setRGB(x, y, input.getRGB(x, y));
			}
		}
		return tmp;
	}

	public BufferedImage colorCorrect(BufferedImage src, BufferedImage bg) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		// TODO: Complete this code

		return result;
	}

	public BufferedImage invert(BufferedImage src) {
		BufferedImage result = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		// TODO: Complete this code

		return result;
	}

	public BufferedImage over(BufferedImage foreground, BufferedImage matte, BufferedImage background) {

		// TODO: Complete this code

		return background; // TODO: Replace background here with the result of the over method
	}

	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	protected int getRed(int pixel) {
		return (pixel >>> 16) & 0xFF;
	}

	protected int getGreen(int pixel) {
		return (pixel >>> 8) & 0xFF;
	}

	protected int getBlue(int pixel) {
		return pixel & 0xFF;
	}

	public void paint(Graphics g) {
		int w = width / 2; // door image
		int h = height / 2;

		int w1 = width1 / 2; // statue
		int h1 = height1 / 2;

		this.setSize(w * 5 + 100, h * 4 + 50);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);

		g.drawImage(src1, 20, 50, w, h, this);
		g.drawImage(src1_bright, 50 + w, 50, w, h, this);
		g.drawImage(src1_brightGama, 80 + w * 2, 50, w, h, this);

		g.drawImage(statueImg, 150 + w * 3, 50, w1, h1, this);
		g.drawImage(backgroundImg, 150 + w * 3 + w1 + 40, 50, w1, h1, this);

		g.drawImage(statueMatte, 150 + w * 3, 50 + h1 + 70, w1, h1, this);
		g.drawImage(edge_mask, 150 + w * 3 + w1 + 40, 50 + h1 + 70, w1, h1, this);

		g.drawImage(blurred, 30, 50 + h + 180, w1, h1, this);
		g.drawString("Blurred background", 30, 50 + h + 170);

		g.drawImage(colorCorrected, 30 + w1 + 30, 50 + h + 180, w1, h1, this);
		g.drawString("Color corrected", 30 + w1 + 30, 50 + h + 170);

		g.drawImage(coloredEdges, 30 + w1 * 2 + 60, 50 + h + 180, w1, h1, this);
		g.drawString("Colored Edges", 30 + w1 * 2 + 60, 50 + h + 170);

		g.drawImage(shadedStatue, 30 + w1 * 3 + 90, 50 + h + 180, w1, h1, this);
		g.drawString("Shaded Statue", 30 + w1 * 3 + 90, 50 + h + 170);

		g.drawImage(finalResult, 30 + w1 * 4 + 120, 50 + h + 180, w1, h1, this);
		g.drawString("Final Result", 30 + w1 * 4 + 120, 50 + h + 170);

		g.drawString("Dark image", 20, 40);
		g.drawString("Increased brightness", 50 + w, 40);
		g.drawString("Increased brightness-Gamma", 80 + w * 2, 40);

		g.drawString("Statue Image", 150 + w * 3, 40);
		g.drawString("Background Image", 150 + w * 3 + w1 + 40, 40);

		g.drawString("Statue - Matte", 150 + w * 3, 50 + h1 + 60);
		g.drawString("Edge Matte", 150 + w * 3 + w1 + 40, 50 + h1 + 60);

	}
	// =======================================================//

	public static void main(String[] args) {

		Week9 img = new Week9();// instantiate this object
		img.repaint();// render the image

	}// end main
}
// =======================================================//