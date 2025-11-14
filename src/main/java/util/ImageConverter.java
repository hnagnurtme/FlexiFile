package util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageConverter {

    /**
     * Convert image between GIF, JPG, JPEG, PNG, WEBP
     */
    public static void convert(File inputFile, File outputFile) throws IOException {
        String outputExt = getFileExtension(outputFile.getName()).toLowerCase();

        BufferedImage img = ImageIO.read(inputFile);
        if (img == null) throw new IOException("Failed to read image: " + inputFile);

        boolean success = ImageIO.write(img, outputExt, outputFile);
        if (!success) {
            throw new IOException("Conversion failed: format not supported - " + outputExt);
        }
    }

    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }

    // Example main
    public static void main(String[] args) throws IOException {
        File input = new File("input.png");
        File output = new File("output.webp");
        convert(input, output);
        System.out.println("Image converted to: " + output.getAbsolutePath());
    }
}
