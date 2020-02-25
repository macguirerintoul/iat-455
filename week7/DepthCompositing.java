
/*File Exercise1.java

 IAT455 - Workshop week 7

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

class DepthCompositing extends Frame {
	BufferedImage bubblesImg;
	BufferedImage forestImg;
	BufferedImage bubbles_depthImg;
	BufferedImage forest_depthImg;

	BufferedImage carImg;
	BufferedImage car_depthImg;

	BufferedImage compositeImage;
	BufferedImage fogImage;

	int width_bubbles, width_car; // width of the image
	int height_bubbles, height_car; // height of the image

	public DepthCompositing() {
		// constructor
		// Get an image from the specified file in the current directory on the
		// local hard disk.
		try {
			bubblesImg = ImageIO.read(new File("bubbles.jpg"));
			forestImg = ImageIO.read(new File("forest.jpg"));
			bubbles_depthImg = ImageIO.read(new File("bubbles_depth.jpg"));
			forest_depthImg = ImageIO.read(new File("forest_depth.jpg"));

			carImg = ImageIO.read(new File("car.jpg"));
			car_depthImg = ImageIO.read(new File("car_depth.jpg"));

		} catch (Exception e) {
			System.out.println("Cannot load the provided image");
		}
		this.setTitle("Week 7 workshop");
		this.setVisible(true);

		width_bubbles = bubblesImg.getWidth();
		height_bubbles = bubblesImg.getHeight();

		width_car = carImg.getWidth();
		height_car = carImg.getHeight();

		compositeImage = composite(bubblesImg, forestImg, bubbles_depthImg, forest_depthImg);
		fogImage = carImg; // addFog(carImg, car_depthImg, 100); // You need to write the addFog method

		// keymixImage = keymixImages(birdImage, boardImage, matteImage);
		// premultipliedImage = combineImages(birdImage, matteImage,
		// Operations.multiply);

		// Anonymous inner-class listener to terminate program
		this.addWindowListener(new WindowAdapter() {// anonymous class definition
			public void windowClosing(WindowEvent e) {
				System.exit(0);// terminate the program
			}// end windowClosing()
		}// end WindowAdapter
		);// end addWindowListener
	}// end constructor

	public BufferedImage composite(BufferedImage src1, BufferedImage src2, BufferedImage src1_depth,
			BufferedImage src2_depth) {

		BufferedImage result = new BufferedImage(src1.getWidth(), src1.getHeight(), src1.getType());

		// Complete this method
		// Get the rgbvalues of the srcimages & depth images
		// Convert the rgbvalues of depth imagesto hsb
		// Compare the brightness of depth images
		// If bubbles_depth is brighter, use bubbles as result rgb
		// If forest_depthis brighter, use forest as result rgb
		for (int x = 0; x < src1.getWidth(); x++) {
			for (int y = 0; y < src1.getHeight(); y++) {
				int s1rgb = src1.getRGB(x, y);
				int s2rgb = src2.getRGB(x, y);

				float[] s1pixel = Color.RGBtoHSB(getRed(s1rgb), getGreen(s1rgb), getBlue(s1rgb), null);
				float[] s2pixel = Color.RGBtoHSB(getRed(s2rgb), getGreen(s2rgb), getBlue(s2rgb), null);
			}
		}
		return result;
	}

	// Write the addFog method here

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

		// if working with different images, this may need to be adjusted
		int w = width_bubbles / 3;
		int h = height_bubbles / 3;

		this.setSize(w * 5 + 150, h * 4);

		g.drawImage(bubblesImg, 10, 50, w, h, this);
		g.drawImage(bubbles_depthImg, 10 + w + 25, 50, w, h, this);
		g.drawImage(forestImg, 10 + w * 2 + 50, 50, w, h, this);
		g.drawImage(forest_depthImg, 10 + w * 3 + 75, 50, w, h, this);
		g.drawImage(compositeImage, 10 + w * 4 + 125, 50, w, h, this);

		g.drawImage(carImg, 25, 50 + h + 55, width_car / 2, height_car / 2, this);
		g.drawImage(car_depthImg, 25 + width_car / 2 + 25, 50 + h + 55, width_car / 2, height_car / 2, this);
		g.drawImage(fogImage, 25 + width_car + 50, 50 + h + 55, width_car / 2, height_car / 2, this);

		g.setColor(Color.BLACK);
		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setFont(f1);
		g.drawString("Bubbles", 25, 45);
		g.drawString("Bubbles Depth", 25 + w + 45, 45);
		g.drawString("Forest", 25 + w * 2 + 85, 45);
		g.drawString("Forest Depth", 25 + w * 3 + 125, 45);
		g.drawString("3D Composite", 25 + w * 4 + 165, 45);

		g.drawString("Car", 30, 50 + h + 50);
		g.drawString("Car Depth Image", 30 + width_car / 2 + 25, 50 + h + 50);
		g.drawString("Car Depth Image", 30 + width_car + 50, 50 + h + 50);
	}
	// =======================================================//

	public static void main(String[] args) {

		DepthCompositing img = new DepthCompositing();// instantiate this object
		img.repaint();// render the image

	}// end main
}
// =======================================================//
