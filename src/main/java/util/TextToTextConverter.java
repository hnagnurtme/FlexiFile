package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;

public class TextToTextConverter {

    /** Convert TXT/LOG/CSV/HTML to DOCX */
    public static void convertToDocx(File inputFile, File outputDocx) throws Exception {
        String ext = getFileExtension(inputFile.getName()).toLowerCase();
        try (XWPFDocument doc = new XWPFDocument()) {

            switch (ext) {
                case "txt", "log", "html" -> {
                    // Treat as plain text, keep all tags
                    List<String> lines = Files.readAllLines(inputFile.toPath());
                    for (String line : lines) {
                        XWPFParagraph para = doc.createParagraph();
                        XWPFRun run = para.createRun();
                        run.setText(line);
                    }
                }
                case "csv" -> {
                    try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
                        List<String[]> rows = reader.readAll();
                        XWPFTable table = doc.createTable();
                        for (int i = 0; i < rows.size(); i++) {
                            String[] row = rows.get(i);
                            XWPFTableRow tableRow = (i == 0) ? table.getRow(0) : table.createRow();
                            IntStream.range(0, row.length).forEach(j -> {
                                if (j >= tableRow.getTableCells().size())
                                    tableRow.createCell().setText(row[j]);
                                else
                                    tableRow.getCell(j).setText(row[j]);
                            });
                        }
                    }
                }
                default -> throw new UnsupportedOperationException("Unsupported input type: " + ext);
            }

            try (FileOutputStream out = new FileOutputStream(outputDocx)) {
                doc.write(out);
            }
        }
    }

    /** Convert DOCX to TXT/LOG/CSV/HTML */
    public static void convertFromDocx(File docxFile, File outputFile) throws Exception {
        String ext = getFileExtension(outputFile.getName()).toLowerCase();
        try (XWPFDocument doc = new XWPFDocument(new FileInputStream(docxFile))) {

            switch (ext) {
                case "txt", "log", "html" -> {
                    try (PrintWriter writer = new PrintWriter(outputFile)) {
                        for (XWPFParagraph para : doc.getParagraphs()) {
                            writer.println(para.getText());
                        }
                    }
                    break;
                }
                case "csv" -> {
                    try (CSVWriter writer = new CSVWriter(new FileWriter(outputFile))) {
                        for (XWPFTable table : doc.getTables()) {
                            for (XWPFTableRow row : table.getRows()) {
                                String[] cells = row.getTableCells().stream()
                                        .map(XWPFTableCell::getText)
                                        .toArray(String[]::new);
                                writer.writeNext(cells);
                            }
                        }
                    }
                    break;
                }
                default -> throw new UnsupportedOperationException("Unsupported output type: " + ext);
            }
        }
    }

    /** Generic convert method based on extensions */
    public static void convert(File inputFile, File outputFile) throws Exception {
        String inputExt = getFileExtension(inputFile.getName()).toLowerCase();
        String outputExt = getFileExtension(outputFile.getName()).toLowerCase();

        if ("docx".equals(inputExt)) {
            convertFromDocx(inputFile, outputFile);
        } else if ("docx".equals(outputExt)) {
            convertToDocx(inputFile, outputFile);
        } else if (Arrays.asList("txt","log","csv","html").contains(inputExt) &&
                   Arrays.asList("txt","log","csv","html").contains(outputExt)) {

            List<String> lines;
            if ("csv".equals(inputExt)) {
                try (CSVReader reader = new CSVReader(new FileReader(inputFile))) {
                    List<String[]> csvRows = reader.readAll();
                    lines = new ArrayList<>();
                    for (String[] row : csvRows) {
                        lines.add(String.join(", ", row));
                    }
                }
            } else {
                lines = Files.readAllLines(inputFile.toPath());
            }

            try (PrintWriter writer = new PrintWriter(outputFile)) {
                for (String line : lines) writer.println(line);
            }
        } else {
            throw new UnsupportedOperationException("Conversion not supported: " + inputExt + " -> " + outputExt);
        }
    }

    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
