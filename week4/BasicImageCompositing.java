
/*File BasicImageCompositing.java
 IAT455 - Workshop week 4
 Basic Image Compositing
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
import java.awt.image.WritableRaster;

class BasicImageCompositing extends Frame {
    BufferedImage birdImage;
    BufferedImage boardImage;
    BufferedImage matteImage;
    BufferedImage placeholderImage;

    // ====== [Lab 4] result images ====== //
    BufferedImage addImage;
    BufferedImage subtractImage;
    BufferedImage keymixImage;
    BufferedImage premultipliedImage;
    BufferedImage P1, P2, P3, P4, P5;
    // =================================== //

    int width; // width of the image
    int height; // height of the image

    public BasicImageCompositing() {
        // constructor
        // Get an image from the specified file in the current directory on the
        // local hard disk.
        try {
            birdImage = ImageIO.read(new File("bird2.jpg"));
            boardImage = ImageIO.read(new File("board.jpg"));
            matteImage = ImageIO.read(new File("matte.jpg"));
            placeholderImage = ImageIO.read(new File("placeholderImg.jpg"));

        } catch (Exception e) {
            System.out.println("Cannot load the provided image");
        }
        this.setTitle("Week 4 workshop - Basic image compositing");
        this.setVisible(true);

        width = birdImage.getWidth();
        height = birdImage.getHeight();

        // ============= Lab 4 ============= //
        // addImage =
        // subtractImage =
        // keymixImage =
        // premultipliedImage =
        // P1 =
        // P2 =
        // P3 =
        // P4 =
        // P5 =
        // ================================= //

        // Anonymous inner-class listener to terminate program
        this.addWindowListener(new WindowAdapter() {// anonymous class definition
            public void windowClosing(WindowEvent e) {
                System.exit(0);// terminate the program
            }// end windowClosing()
        }// end WindowAdapter
        );// end addWindowListener
    }// end constructor

    public void runAllOperations() {
        addImage = operate("add");
        subtractImage = operate("subtract");
        keymixImage = operate("keymix");
        premultipliedImage = operate("premultiplied");
        P1 = operate("dissolve", 0.9);
        P2 = operate("dissolve", 0.7);
        P3 = operate("dissolve", 0.5);
        P4 = operate("dissolve", 0.3);
        P5 = operate("dissolve", 0.1);
    }

    public BufferedImage operate(String operation) {
        return operate(operation, 0.0);
    }

    public BufferedImage operate(String operation, double mv) {

        WritableRaster wRaster = birdImage.copyData(null);
        BufferedImage outputImage = new BufferedImage(birdImage.getColorModel(), wRaster,
                birdImage.isAlphaPremultiplied(), null);

        // apply the operation to each pixel
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int argb = birdImage.getRGB(x, y);
                int brgb = boardImage.getRGB(x, y);
                int mrgb = matteImage.getRGB(x, y);
                int newR = 0;
                int newG = 0;
                int newB = 0;
                int newRGB = 0;

                // check which operation was
                switch (operation) {
                case "add":
                    newR = getRed(argb) + getRed(brgb);
                    newG = getGreen(argb) + getGreen(brgb);
                    newB = getBlue(argb) + getBlue(brgb);
                    break;
                case "subtract":
                    newR = Math.abs(getRed(argb) - getRed(brgb));
                    newG = Math.abs(getGreen(argb) - getGreen(brgb));
                    newB = Math.abs(getBlue(argb) - getBlue(brgb));
                    break;
                case "keymix":
                    float m = getRed(mrgb) / 255;
                    newR = (int) (getRed(argb) * m + (1 - m) * getRed(brgb));
                    newG = (int) (getGreen(argb) * m + (1 - m) * getGreen(brgb));
                    newB = (int) (getBlue(argb) * m + (1 - m) * getBlue(brgb));
                    break;
                case "premultiplied":
                    newR = getRed(argb) * getRed(mrgb) / 255;
                    newG = getGreen(argb) * getGreen(mrgb) / 255;
                    newB = getBlue(argb) * getBlue(mrgb) / 255;
                    break;
                case "dissolve":
                    newR = (int) (mv * getRed(argb) + (1 - mv) * getRed(brgb));
                    newG = (int) (mv * getGreen(argb) + (1 - mv) * getGreen(brgb));
                    newB = (int) (mv * getBlue(argb) + (1 - mv) * getBlue(brgb));
                    break;
                default:
                    break;
                }
                newRGB = new Color(clipChannelValue(newR), clipChannelValue(newG), clipChannelValue(newB)).getRGB();
                outputImage.setRGB(x, y, newRGB);
            }
        }

        return outputImage;
    }

    // ========== helpers ========== //
    private int getRed(int rgb) {
        return new Color(rgb).getRed();
    }

    private int getGreen(int rgb) {
        return new Color(rgb).getGreen();
    }

    private int getBlue(int rgb) {
        return new Color(rgb).getBlue();
    }

    private int clipChannelValue(int v) {
        v = v > 255 ? 255 : v;
        v = v < 0 ? 0 : v;
        return v;
    }
    // ============================= //

    public void paint(Graphics g) {

        // if working with different images, this may need to be adjusted
        int w = width / 2;
        int h = height / 2;

        this.setSize(w * 7 + 300, h * 4 + 150);

        g.drawImage(birdImage, 25, 50, w, h, this);
        g.drawImage(boardImage, 25 + w + 25, 50, w, h, this);
        g.drawImage(matteImage, 25 + w * 2 + 50, 50, w, h, this);
        g.drawImage(addImage, 25 + w * 3 + 75, 50, w, h, this);
        g.drawImage(subtractImage, w * 4 + 125, 50, w, h, this);
        g.drawImage(keymixImage, w * 5 + 150, 50, w, h, this);
        g.drawImage(premultipliedImage, w * 6 + 175, 50, w, h, this);

        g.setColor(Color.BLACK);
        Font f1 = new Font("Verdana", Font.PLAIN, 13);
        g.setFont(f1);
        g.drawString("Source 1", 25, 45);
        g.drawString("Source 2", 50 + w, 45);
        g.drawString("Matte", 72 + 2 * w, 45);
        g.drawString("Add Src1 +Src2", 100 + 3 * w, 45);
        g.drawString("Subtract Src1 -Src2", 125 + 4 * w, 45);
        g.drawString("Keymix", 150 + 5 * w, 45);
        g.drawString("Premultiplied", 175 + 6 * w, 45);

        g.drawImage(birdImage, 25, 180 + h, w, h, this);
        g.drawImage(P1, 25 + w + 25, 180 + h, w, h, this);
        g.drawImage(P2, 25 + w * 2 + 50, 180 + h, w, h, this);
        g.drawImage(P3, 25 + w * 3 + 75, 180 + h, w, h, this);
        g.drawImage(P4, w * 4 + 125, 180 + h, w, h, this);
        g.drawImage(P5, w * 5 + 150, 180 + h, w, h, this);
        g.drawImage(boardImage, w * 6 + 175, 180 + h, w, h, this);

        g.drawString("Initial Image A", 25, 180 + h + h + 20);
        g.drawString("0.9A + 0.1B", 60 + w, 180 + h + h + 20);
        g.drawString("0.7A + 0.3B", 82 + 2 * w, 180 + h + h + 20);
        g.drawString("0.5A + 0.5B", 110 + 3 * w, 180 + h + h + 20);
        g.drawString("0.3A + 0.7B", 135 + 4 * w, 180 + h + h + 20);
        g.drawString("0.1A + 0.9B", 160 + 5 * w, 180 + h + h + 20);
        g.drawString("Final Image B", 185 + 6 * w, 180 + h + h + 20);

        Font f2 = new Font("Verdana", Font.BOLD, 15);
        g.setFont(f2);

        g.drawString("DISSOLVE between images", 170 + h, 150 + h);
    }
    // =======================================================//

    public static void main(String[] args) {

        BasicImageCompositing img = new BasicImageCompositing();// instantiate this object
        img.runAllOperations();
        img.repaint();// render the image

    }// end main
}
// =======================================================//