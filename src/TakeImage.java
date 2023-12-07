import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class TakeImage {

    private final BufferedImage image;

    // Constructor that loads an image from the given path.
    public TakeImage(String path) throws IOException {
        image = loadImageResource(path);
    }

    // Constructor that loads an image from the given path and resizes it to the specified dimensions (x, y).
    public TakeImage(String path, int x, int y) throws IOException {
        BufferedImage originalImage = loadImageResource(path);
        image = resize(originalImage, x, y);
    }

    // Returns the BufferedImage object.
    public BufferedImage getImage() {
        return image;
    }

    // Resizes the given BufferedImage to new dimensions (newW, newH).
    private BufferedImage resize(BufferedImage img, int newW, int newH) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage dimg = new BufferedImage(newW, newH, img.getType());
        Graphics2D g = dimg.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(img, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return dimg;
    }

    // Loads a BufferedImage from the specified path in the classpath.
    private BufferedImage loadImageResource(String path) throws IOException {
        BufferedImage loadedImage = ImageIO.read(Objects.requireNonNull(getClass().getResource(path)));
        if (loadedImage == null) {
            throw new IOException("Image resource not found: " + path);
        }
        return loadedImage;
    }
}