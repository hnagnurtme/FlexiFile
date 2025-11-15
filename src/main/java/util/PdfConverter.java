package util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPictureData;

import com.opencsv.CSVReader;

public class PdfConverter {

    private static final String FONT_DIR = "src/main/resources/fonts/";
    private static final float MARGIN = 50;
    private static final float FONT_SIZE = 12;
    private static final float LEADING = 1.2f * FONT_SIZE;
    private static final float PAGE_WIDTH = PDRectangle.LETTER.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.LETTER.getHeight();

    /** Load all TTF fonts into a map keyed by style */
    private static Map<String, PDType0Font> loadFonts(PDDocument pdfDoc) throws IOException {
        Map<String, PDType0Font> fonts = new HashMap<>();
        Files.list(Paths.get(FONT_DIR))
                .filter(path -> path.toString().toLowerCase().endsWith(".ttf"))
                .forEach(path -> {
                    try {
                        PDType0Font font = PDType0Font.load(pdfDoc, path.toFile());
                        String fontName = path.getFileName().toString().replace(".ttf", "");
                        fonts.put(fontName.toLowerCase(), font);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        return fonts;
    }

    /** Choose correct font based on bold/italic */
    private static PDType0Font selectFont(Map<String, PDType0Font> fonts, boolean bold, boolean italic) {
        String key;
        if (bold && italic) key = "notosans-bolditalic";
        else if (bold) key = "notosans-bold";
        else if (italic) key = "notosans-italic";
        else key = "notosans-regular";
        return fonts.getOrDefault(key, fonts.values().iterator().next());
    }

    private static List<String> wrapText(String text, PDType0Font font, float fontSize, float maxWidth) throws IOException {
        if (text == null) text = "";
        text = text.replace("\t", "    ").replaceAll("[\\p{Cntrl}&&[^\r\n]]", "");

        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String temp = line.length() == 0 ? word : line + " " + word;
            float width = font.getStringWidth(temp) / 1000 * fontSize;
            if (width > maxWidth) {
                if (line.length() > 0) lines.add(line.toString());
                line = new StringBuilder(word);
            } else {
                line = new StringBuilder(temp);
            }
        }
        if (line.length() > 0) lines.add(line.toString());
        return lines;
    }

    private static void writeDocxContentToPdf(PDDocument pdfDoc, List<Object> content, Map<String, PDType0Font> fonts) throws IOException {
        PDPage page = new PDPage();
        pdfDoc.addPage(page);
        PDPageContentStream contentStream = new PDPageContentStream(pdfDoc, page);
        float yPosition = PAGE_HEIGHT - MARGIN;

        for (Object item : content) {
            if (item instanceof TextChunk chunk) {
                PDType0Font font = selectFont(fonts, chunk.bold, chunk.italic);
                if (yPosition - LEADING < MARGIN) {
                    contentStream.close();
                    page = new PDPage();
                    pdfDoc.addPage(page);
                    contentStream = new PDPageContentStream(pdfDoc, page);
                    yPosition = PAGE_HEIGHT - MARGIN;
                }
                contentStream.beginText();
                contentStream.setFont(font, FONT_SIZE);
                contentStream.setLeading(LEADING);
                contentStream.newLineAtOffset(MARGIN, yPosition);
                for (String line : chunk.lines) {
                    contentStream.showText(line);
                    contentStream.newLine();
                    yPosition -= LEADING;
                }
                contentStream.endText();

            } else if (item instanceof BufferedImage img) {
                PDImageXObject pdImage = LosslessFactory.createFromImage(pdfDoc, img);
                float scale = Math.min((PAGE_WIDTH - 2 * MARGIN) / img.getWidth(),
                                       (PAGE_HEIGHT - 2 * MARGIN) / img.getHeight());
                float imgWidth = img.getWidth() * scale;
                float imgHeight = img.getHeight() * scale;

                if (yPosition - imgHeight < MARGIN) {
                    contentStream.close();
                    page = new PDPage();
                    pdfDoc.addPage(page);
                    contentStream = new PDPageContentStream(pdfDoc, page);
                    yPosition = PAGE_HEIGHT - MARGIN;
                }

                contentStream.drawImage(pdImage, MARGIN, yPosition - imgHeight, imgWidth, imgHeight);
                yPosition -= imgHeight + LEADING;
            }
        }

        contentStream.close();
    }

    /** Holds text + style info */
    private static class TextChunk {
        List<String> lines;
        boolean bold;
        boolean italic;

        TextChunk(List<String> lines, boolean bold, boolean italic) {
            this.lines = lines;
            this.bold = bold;
            this.italic = italic;
        }
    }

    public static void convertDocxToPdf(File docxFile, File pdfFile) throws Exception {
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(docxFile));
             PDDocument pdfDoc = new PDDocument()) {

            Map<String, PDType0Font> fonts = loadFonts(pdfDoc);
            List<Object> content = new ArrayList<>();

            for (XWPFParagraph para : doc.getParagraphs()) {
                boolean bold = para.getRuns().stream().anyMatch(r -> r.isBold());
                boolean italic = para.getRuns().stream().anyMatch(r -> r.isItalic());
                List<String> lines = wrapText(para.getText(), selectFont(fonts, bold, italic), FONT_SIZE, PAGE_WIDTH - 2 * MARGIN);
                content.add(new TextChunk(lines, bold, italic));
                content.add(new TextChunk(Collections.singletonList(""), false, false));
            }

            // Add images from DOCX
            for (XWPFPictureData picData : doc.getAllPictures()) {
                try (InputStream is = new ByteArrayInputStream(picData.getData())) {
                    BufferedImage img = ImageIO.read(is);
                    if (img != null) content.add(img);
                }
            }

            writeDocxContentToPdf(pdfDoc, content, fonts);
            pdfDoc.save(pdfFile);
        }
    }

    public static void convertTextToPdf(File textFile, File pdfFile) throws Exception {
        try (PDDocument pdfDoc = new PDDocument()) {
            Map<String, PDType0Font> fonts = loadFonts(pdfDoc);
            PDType0Font font = fonts.getOrDefault("notosans-regular", fonts.values().iterator().next());

            List<String> lines;
            if (textFile.getName().toLowerCase().endsWith(".csv")) {
                try (CSVReader reader = new CSVReader(new FileReader(textFile))) {
                    List<String[]> csvRows = reader.readAll();
                    lines = new ArrayList<>();
                    for (String[] row : csvRows) {
                        lines.addAll(wrapText(String.join(", ", row), font, FONT_SIZE, PAGE_WIDTH - 2 * MARGIN));
                    }
                }
            } else {
                lines = new ArrayList<>();
                for (String line : Files.readAllLines(textFile.toPath())) {
                    lines.addAll(wrapText(line, font, FONT_SIZE, PAGE_WIDTH - 2 * MARGIN));
                }
            }

            List<Object> content = new ArrayList<>();
            content.add(new TextChunk(lines, false, false));
            writeDocxContentToPdf(pdfDoc, content, fonts);
            pdfDoc.save(pdfFile);
        }
    }

    public static void convertToPdf(File inputFile, File outputPdf) throws Exception {
        String ext = getFileExtension(inputFile.getName()).toLowerCase();
        switch (ext) {
            case "docx" -> convertDocxToPdf(inputFile, outputPdf);
            case "txt", "log", "csv", "html" -> convertTextToPdf(inputFile, outputPdf);
            default -> throw new UnsupportedOperationException("File type not supported: " + ext);
        }
    }

    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
