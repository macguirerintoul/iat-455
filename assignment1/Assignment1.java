/*
 * IAT 455
 * Assignment 1
 * 
 * Macguire Rintoul
 */

// Import libraries
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

// Where the magic happens
class Assignment1 extends Frame {
	BufferedImage portrait; // Placeholder for the original portrait image
	BufferedImage background; // Placeholder for the background image
	BufferedImage matte; // Placeholder for the extracted matte of the portrait
	BufferedImage composite; // Placeholder for the final composite

	int width; // Placeholder for the image width
	int height; // Placeholder for the image height

	// Constructor for the class
	public Assignment1() {
		try {
			// Load the base images from disk
			portrait = ImageIO.read(new File("portrait-g.jpg"));
			background = ImageIO.read(new File("background.jpg"));
		} catch (Exception e) {
			// If images can't be found or something else goes wrong, print a message
			System.out.println("Cannot load the provided image");
		}

		this.setTitle("Assignment 1"); // Set the title of the app window
		this.setVisible(true); // Show the app window
		width = portrait.getWidth(); // Set the image width
		height = portrait.getHeight(); // Set the image height
		question3(); // Run some code to help confirm my answer for Question 3
		question5(); // Complete Question 5

		// Terminates the program when the window is closed
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
	}

	// Function to confirm my answer for Question 3
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
		matte = createMatte(portrait); // Create a matte for the original portrait image
		composite = over(portrait, background, matte); // Put it all together using the over operation
	}

	// Combine two images and a matte using the over operation
	public BufferedImage over(BufferedImage foreground, BufferedImage background, BufferedImage matte) {
		// Placeholder for the result image
		BufferedImage result = new BufferedImage(foreground.getWidth(), foreground.getHeight(), foreground.getType());

		// Iterate through the images (assumes same width and height for all 3)
		for (int x = 0; x < foreground.getWidth(); x++) {
			for (int y = 0; y < foreground.getHeight(); y++) {
				// Get all 3 red values
				int fR = getRed(foreground.getRGB(x, y));
				int bR = getRed(background.getRGB(x, y));
				int mR = getRed(matte.getRGB(x, y));
				// Run the over operation on the red channel
				int newR = clip(fR * (mR / 255) + bR * (1 - (mR / 255)));

				// Get all 3 green values
				int fG = getGreen(foreground.getRGB(x, y));
				int bG = getGreen(background.getRGB(x, y));
				int mG = getGreen(matte.getRGB(x, y));
				// Run the over operation on the green channel
				int newG = clip(fG * (mG / 255) + bG * (1 - (mG / 255)));

				// Get all 3 blue values
				int fB = getBlue(foreground.getRGB(x, y));
				int bB = getBlue(background.getRGB(x, y));
				int mB = getBlue(matte.getRGB(x, y));
				// Run the over operation on the blue channel
				int newB = clip(fB * (mB / 255) + bB * (1 - (mB / 255)));

				// Set the colour for the corresponding pixel in the result image
				result.setRGB(x, y, new Color(newR, newG, newB).getRGB());
			}
		}
		return result;
	}

	// Utility function to clamp values to between 0 and 255
	private int clip(int v) {
		v = v > 255 ? 255 : v;
		v = v < 0 ? 0 : v;
		return v;
	}

	// Get the red channel value from a pixel
	protected int getRed(int pixel) {
		return (new Color(pixel)).getRed();
	}

	// Get the green channel value from a pixel
	protected int getGreen(int pixel) {
		return (new Color(pixel)).getGreen();
	}

	// Get the blue channel value from a pixel
	protected int getBlue(int pixel) {
		return (new Color(pixel)).getBlue();
	}

	/*
	 * Create a matte from a source image (function is optimized for the provided
	 * portrait)
	 */
	public BufferedImage createMatte(BufferedImage src) {
		// Placeholder for the resulting matte
		BufferedImage matte = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());

		// Iterate through the pixels in the source image
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int pixel = src.getRGB(x, y);
				/*
				 * In the original portrait, the background is green. So, if the pixel is mostly
				 * green, we want that pixel to end up being black. First, we can check whether
				 * the pixel is mostly green by seeing if it's greater or less than either of
				 * the other two channels. If greenness is greater than 0, green is the dominant
				 * channel in the pixel.
				 */
				int greenness = clip(getGreen(pixel) - Math.max(getBlue(pixel), getRed(pixel)));

				/*
				 * Now, we have the green value of the pixel. We want all green pixels to be
				 * black, so we will subtract the greenness from 255. By doing this, the
				 * greenest pixels will have the lowest values.
				 */
				int matteValue = 255 - greenness;

				/*
				 * In some cases, the greenness will be just slightly higher than the red or
				 * blue value, but not enough that it's very clearly a 'green' pixel (the colour
				 * of our background). These pixels should be white, since we can be fairly sure
				 * they're not part of the background, which would have a greater difference
				 * between green and the other channels. After experimenting, a threshold of 245
				 * worked well for the portrait image.
				 */
				if (matteValue < 245) {
					matteValue = 0;
				} else {
					matteValue = 255;
				}

				// Set each pixel to its calculated matte value
				matte.setRGB(x, y, new Color(matteValue, matteValue, matteValue).getRGB());
			}
		}
		return matte;
	}

	public void paint(Graphics g) {

		/*
		 * Retrieve the dimensions of the image to use when placing elements in the
		 * window
		 */
		int w = width;
		int h = height;

		this.setSize(w * 4 + 300, h * 1 + 150);

		Font f1 = new Font("Verdana", Font.PLAIN, 13);
		g.setColor(Color.BLACK);
		g.setFont(f1);

		/*
		 * Render the source images, the intermediate images, and the final image.
		 */
		g.drawString("Original portrait", 25, 45);
		g.drawImage(portrait, 25, 50, w, h, this);

		g.drawString("Original background", 2 * 25 + w, 45);
		g.drawImage(background, 2 * 25 + w, 50, w, h, this);

		g.drawString("Matte", 4 * 25 + 2 * w, 45);
		g.drawImage(matte, 4 * 25 + 2 * w, 50, w, h, this);

		g.drawString("Composite", 6 * 25 + 3 * w, 45);
		g.drawImage(composite, 6 * 25 + 3 * w, 50, w, h, this);
	}

	public static void main(String[] args) {
		Assignment1 a1 = new Assignment1();
	}
}