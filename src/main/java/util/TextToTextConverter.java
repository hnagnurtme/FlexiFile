package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

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
                            for (int j = 0; j < row.length; j++) {
                                if (j >= tableRow.getTableCells().size())
                                    tableRow.createCell().setText(row[j]);
                                else
                                    tableRow.getCell(j).setText(row[j]);
                            }
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
                        if ("html".equals(ext)) {
                            writer.println("<html><body>");
                        }

                        // Xuất paragraph
                        for (XWPFParagraph para : doc.getParagraphs()) {
                            if ("html".equals(ext))
                                writer.println("<p>" + para.getText() + "</p>");
                            else
                                writer.println(para.getText());
                        }

                        // Xuất table
                        for (XWPFTable table : doc.getTables()) {
                            if ("html".equals(ext)) {
                                writer.println("<table border='1' cellspacing='0' cellpadding='5'>");
                                for (XWPFTableRow row : table.getRows()) {
                                    writer.println("<tr>");
                                    for (XWPFTableCell cell : row.getTableCells()) {
                                        writer.println("<td>" + cell.getText() + "</td>");
                                    }
                                    writer.println("</tr>");
                                }
                                writer.println("</table><br>");
                            }
                        }

                        if ("html".equals(ext)) {
                            writer.println("</body></html>");
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

        // DOCX → anything
        if ("docx".equals(inputExt)) {
            convertFromDocx(inputFile, outputFile);
            return;
        }

        // Anything → DOCX
        if ("docx".equals(outputExt)) {
            convertToDocx(inputFile, outputFile);
            return;
        }

        // CSV → HTML (table)
        if ("csv".equals(inputExt) && "html".equals(outputExt)) {
            convertCsvToHtml(inputFile, outputFile);
            return;
        }

        // TXT/LOG/CSV/HTML → TXT/LOG/CSV/HTML
        if (Arrays.asList("txt", "log", "csv", "html").contains(inputExt) &&
            Arrays.asList("txt", "log", "csv", "html").contains(outputExt)) {

            if ("csv".equals(inputExt)) {
                // plain text CSV copy
                try (CSVReader reader = new CSVReader(new FileReader(inputFile));
                     PrintWriter writer = new PrintWriter(outputFile)) {

                    List<String[]> csvRows = reader.readAll();
                    for (String[] row : csvRows) {
                        writer.println(String.join(", ", row));
                    }
                }
            } else {
                List<String> lines = Files.readAllLines(inputFile.toPath());
                try (PrintWriter writer = new PrintWriter(outputFile)) {
                    for (String line : lines) writer.println(line);
                }
            }
            return;
        }

        throw new UnsupportedOperationException("Conversion not supported: " + inputExt + " -> " + outputExt);
    }

    /** NEW — Convert CSV → HTML table */
    private static void convertCsvToHtml(File csvFile, File htmlFile) throws Exception {
        try (CSVReader reader = new CSVReader(new FileReader(csvFile));
             PrintWriter writer = new PrintWriter(htmlFile)) {

            List<String[]> rows = reader.readAll();

            writer.println("<html><body>");
            writer.println("<table border='1' cellspacing='0' cellpadding='5'>");

            for (String[] row : rows) {
                writer.println("<tr>");
                for (String col : row) {
                    writer.println("<td>" + col + "</td>");
                }
                writer.println("</tr>");
            }

            writer.println("</table>");
            writer.println("</body></html>");
        }
    }

    private static String getFileExtension(String fileName) {
        int dot = fileName.lastIndexOf('.');
        return (dot == -1) ? "" : fileName.substring(dot + 1);
    }
}
