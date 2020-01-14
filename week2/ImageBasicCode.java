
/*File ImageBasics.java

 IAT455 - Workshop week 2
 Digital Representation of Visual Information.

 **********************************************************/

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

class ImageBasics extends Frame {
    BufferedImage testImage;

    BufferedImage redChannel; // red channel
    BufferedImage greenChannel; // green channel
    BufferedImage blueChannel; // blue channel
    BufferedImage restoredImg; // restored image

    BufferedImage redChannel_reduced; // red channel with reduced bit-depth
    BufferedImage greenChannel_reduced; // green channel with reduced bit-depth
    BufferedImage blueChannel_reduced; // blue channel with reduced bit-depth
    BufferedImage restoredImg_reduced; // restored image with reduced bit-depth

    BufferedImage hue_img; // hue image
    BufferedImage saturation_img; // saturation image
    BufferedImage value_img; // value image

    int width; // width of the image
    int height; // height of the image

    public void runLengthEncode() {
        try {
            byte[] encoded = encodeRunLength(imageToByteArray());
            System.out.println("ENCODED BYTE ARRAY:");
            for (byte b : encoded) {
                int s = b & 0xff;
                System.out.printf(s + " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public byte[] imageToByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(value_img, "jpg", baos);
        byte[] ba = baos.toByteArray();
        System.out.println("IMAGE BYTE ARRAY:");
        for (byte b : ba) {
            int s = b & 0xff;
            System.out.printf(s + " ");
        }
        System.out.println("");
        // write a test file to see if the image to byteArray conversion worked
        /*
        ByteArrayInputStream bis = new ByteArrayInputStream(ba);
        BufferedImage bImage2 = ImageIO.read(bis);
        ImageIO.write(bImage2, "jpg", new File("bis.jpg") );
         */
        return ba;
    }

    public byte[] encodeRunLength(byte[] imageByteArray) throws IOException {
        ByteArrayOutputStream dest = new ByteArrayOutputStream();
        byte lastByte = imageByteArray[0];
        int matchCount = 1;
        for (int i = 1; i < imageByteArray.length; i++) {
            byte thisByte = imageByteArray[i];
            if (lastByte == thisByte) {
                matchCount++;
            } else {
                dest.write((byte) matchCount);
                dest.write((byte) lastByte);
                matchCount = 1;
                lastByte = thisByte;
            }
        }
        dest.write((byte) matchCount);
        dest.write((byte) lastByte);
        return dest.toByteArray();
    }

    public ImageBasics() {
        // constructor
        // Get an image from the specified file in the current directory on the
        // local hard disk.
        try {
            testImage = ImageIO.read(new File("bird1.jpg"));

        } catch (Exception e) {
            System.out.println("Cannot load the provided image");
        }
        this.setTitle("Week 2 workshop - RGB representation");
        this.setVisible(true);

        width = testImage.getWidth();
        height = testImage.getHeight();

        redChannel = filterImage(testImage, Filters.red);
        greenChannel = filterImage(testImage, Filters.green);
        blueChannel = filterImage(testImage, Filters.blue);
        restoredImg = filterImage(testImage, Filters.restored);

        redChannel_reduced = filterImage(testImage, Filters.reducedRed);
        greenChannel_reduced = filterImage(testImage, Filters.reducedGreen);
        blueChannel_reduced = filterImage(testImage, Filters.reducedBlue);
        restoredImg_reduced = filterImage(testImage, Filters.reducedAll);

        hue_img = filterImage(testImage, Filters.hue);
        saturation_img = filterImage(testImage, Filters.saturation);
        value_img = filterImage(testImage, Filters.value);

        //Anonymous inner-class listener to terminate program
        this.addWindowListener(
                new WindowAdapter() {//anonymous class definition
                    public void windowClosing(WindowEvent e) {
                        System.exit(0);//terminate the program
                    }//end windowClosing()
                }//end WindowAdapter
        );//end addWindowListener
    }// end constructor

    public BufferedImage filterImage(BufferedImage img, Filters filt)
    //produce the result image for each operation
    {
        int width = img.getWidth();
        int height = img.getHeight();

        WritableRaster wRaster = img.copyData(null);
        BufferedImage copy = new BufferedImage(img.getColorModel(), wRaster, img.isAlphaPremultiplied(), null);

        //apply the operation to each pixel
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int rgb = img.getRGB(i, j);
                copy.setRGB(i, j, filterPixel(rgb, filt));
            }
        }
        return copy;
    }

    public int filterPixel(int rgb, Filters filt) { //operation to be applied to each pixel, for obtaining the channels, reduced bit-depth image and HSV image
        Color c = new Color(rgb);
        float[] hsv = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);

        switch (filt) {
            case red:
                return rgb & 0xFFFF0000;

            case green:
                return rgb & 0xFF00FF00;

            case blue:
                return rgb & 0xFF0000FF;

            case restored:
                return rgb & 0xFFFF0000 | rgb & 0xFF00FF00 | rgb & 0xFF0000FF;

            case reducedRed://reducing to 3 bits
                return rgb & 0xFFE00000;

            case reducedGreen://reducing to 3 bits
                return rgb & 0xFF00E000;

            case reducedBlue://reducing to 3 bits
                return rgb & 0xFF0000E0;

            case reducedAll:
                return rgb & 0xFFE00000 | rgb & 0xFF00E000 | rgb & 0xFF0000E0;

            case hue:
                return Color.HSBtoRGB(hsv[0], 1.0F, 1.0F);

            case saturation:
                Color sat = new Color(hsv[1], hsv[1], hsv[1]);
                return sat.getRGB();

            case value:
                Color val = new Color(hsv[2], hsv[2], hsv[2]);
                return val.getRGB();

            default:
                return 0xFFFFFFFF;
        }
    }


    public void paint(Graphics g) {

        //if working with different images, this may need to be adjusted
        int w = width / 3;
        int h = height / 3;

        this.setSize(w * 5 + 300, h * 3 + 150);

        // original + R G B channels + restored
        g.drawImage(testImage, 10, 50, w, h, this);
        g.drawImage(redChannel, w + 20, 50, w, h, this);
        g.drawImage(greenChannel, w * 2 + 30, 50, w, h, this);
        g.drawImage(blueChannel, w * 3 + 40, 50, w, h, this);
        g.drawImage(restoredImg, w * 4 + 50, 50, w, h, this);

        // add caption to the displayed images
        g.setColor(Color.BLACK);
        Font f1 = new Font("Verdana", Font.BOLD, 15);
        g.setFont(f1);
        g.drawString("Original image", 15, 45);
        g.drawString("Red Channel", 15 + w + 20, 45);
        g.drawString("Green Channel", 15 + w * 2 + 20, 45);
        g.drawString("Blue Channel", 15 + w * 3 + 40, 45);
        g.drawString("Restored Image", 15 + w * 4 + 50, 45);
        g.drawString("R G B", 15 + w * 5 + 80, 45 + h / 2);

        // reduced R G B + restored
        g.drawImage(redChannel_reduced, w + 20, 50 + h + 30, w, h, this);
        g.drawImage(greenChannel_reduced, w * 2 + 30, 50 + h + 30, w, h, this);
        g.drawImage(blueChannel_reduced, w * 3 + 40, 50 + h + 30, w, h, this);
        g.drawImage(restoredImg_reduced, w * 4 + 50, 50 + h + 30, w, h, this);

        g.drawString("Red Channel-reduced", 10 + w + 20, 45 + h + 30);
        g.drawString("Green Channel-reduced", 10 + w * 2 + 20, 45 + h + 30);
        g.drawString("Blue Channel-reduced", 10 + w * 3 + 35, 45 + h + 30);
        g.drawString("Restored Image-reduced", 10 + w * 4 + 40, 45 + h + 30);

        g.drawString("Reduced bit-depth", 20, 45 + h / 2 + h + 30);

        // H S V
        g.drawImage(hue_img, w + 20, 50 + 2 * h + 80, w, h, this);
        g.drawImage(saturation_img, w * 2 + 30, 50 + 2 * h + 80, w, h, this);
        g.drawImage(value_img, w * 3 + 40, 50 + 2 * h + 80, w, h, this);

        g.drawString("Hue component", 10 + w + 20, 45 + 2 * h + 75);
        g.drawString("Saturation component", 10 + w * 2 + 20, 45 + 2 * h + 75);
        g.drawString("Value component", 10 + w * 3 + 35, 45 + 2 * h + 75);

        g.drawString("H S V", 60, 45 + h / 2 + 2 * h + 70);
    }
// =======================================================//

    public static void main(String[] args) {

        ImageBasics img = new ImageBasics();// instantiate this object
        img.repaint();// render the image
        img.runLengthEncode();

    }// end main
}
// =======================================================//
