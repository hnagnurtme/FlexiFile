package worker;

import java.io.File;
import java.util.List;

import model.bean.FileJob;
import model.bo.FileJobBO;
import util.CloudinaryUtil;
import util.ImageConverter;
import util.PdfConverter;
import util.TextToTextConverter;



public class ConvertWorker implements Runnable {

    private final List<FileJob> fileJobs;
    private final String userId;
    private final FileJobBO fileJobBO = new FileJobBO();

    public ConvertWorker() {
        this.fileJobs = null;
        this.userId = null;
    }

    public ConvertWorker(String userId, List<FileJob> fileJobs) {
        this.userId = userId;
        this.fileJobs = fileJobs;
    }

    @Override
    public void run() {
        for (FileJob job : fileJobs) {
            try {
                // Update status PROCESSING + updatedAt
                fileJobBO.markProcessing(job);

                // Tải file gốc về local
                File inputFile = File.createTempFile("input-", "-" + job.getFileName());
                CloudinaryUtil.downloadToFile(job.getFileUrl(), inputFile.getAbsolutePath());

                // Chọn converter dựa theo targetFormat
                String ext = job.getTargetFormat().toLowerCase();
                File outputFile = File.createTempFile("output-", "." + ext);

                if (ext.equals("pdf")) {
                    PdfConverter.convertToPdf(inputFile, outputFile);
                } else if (List.of("docx","txt","log","csv","html").contains(ext)) {
                    TextToTextConverter.convert(inputFile, outputFile);
                } else if (List.of("png","jpg","jpeg","gif","webp").contains(ext)) {
                    ImageConverter.convert(inputFile, outputFile);
                } else {
                    throw new UnsupportedOperationException("Unsupported target format: " + ext);
                }

                // Upload file kết quả
                String resultUrl = CloudinaryUtil.uploadConvertedFile(outputFile);

                // Update status DONE + resultUrl + updatedAt
                fileJobBO.markDone(job, resultUrl);

                // Xóa file tạm
                inputFile.delete();
                outputFile.delete();

            } catch (Exception e) {
                e.printStackTrace();
                // Update status FAILED + updatedAt
                fileJobBO.markFailed(job);
            }
        }

        // Signal user khi tất cả file hoàn thành
        //fileJobBO.signalUserAllDone(userId);
    }

    // =========================
    // Test helper method
    // Chỉ nhận URL, convert và upload, trả về URL kết quả
    // =========================
    public static String convertSingleFile(String fileUrl, String targetFormat) {
        try {
            File inputFile = File.createTempFile("input-", "-" + fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
            CloudinaryUtil.downloadToFile(fileUrl, inputFile.getAbsolutePath());

            File outputFile = File.createTempFile("output-", "." + targetFormat);

            switch (targetFormat.toLowerCase()) {
                case "pdf" -> PdfConverter.convertToPdf(inputFile, outputFile);
                case "docx","txt","log","csv","html" -> TextToTextConverter.convert(inputFile, outputFile);
                case "png","jpg","jpeg","gif","webp" -> ImageConverter.convert(inputFile, outputFile);
                default -> throw new UnsupportedOperationException("Unsupported target format: " + targetFormat);
            }

            String resultUrl = CloudinaryUtil.uploadConvertedFile(outputFile);
            inputFile.delete();
            outputFile.delete();
            return resultUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // =========================
    // Test helper method
    // Chỉ nhận URL, convert và upload, trả về URL kết quả
    // =========================
    public static String convertSingleFile(String fileUrl, String targetFormat) {
        try {
            File inputFile = File.createTempFile("input-", "-" + fileUrl.substring(fileUrl.lastIndexOf('/') + 1));
            CloudinaryUtil.downloadToFile(fileUrl, inputFile.getAbsolutePath());

            File outputFile = File.createTempFile("output-", "." + targetFormat);

            switch (targetFormat.toLowerCase()) {
                case "pdf" -> PdfConverter.convertToPdf(inputFile, outputFile);
                case "docx","txt","log","csv","html" -> TextToTextConverter.convert(inputFile, outputFile);
                case "png","jpg","jpeg","gif","webp" -> ImageConverter.convert(inputFile, outputFile);
                default -> throw new UnsupportedOperationException("Unsupported target format: " + targetFormat);
            }

            String resultUrl = CloudinaryUtil.uploadConvertedFile(outputFile);
            inputFile.delete();
            outputFile.delete();
            return resultUrl;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void process(FileJob job) {
        try {
            fileJobBO.markProcessing(job);

            File inputFile = File.createTempFile("input-", "-" + job.getFileName());
            CloudinaryUtil.downloadToFile(job.getFileUrl(), inputFile.getAbsolutePath());

            String ext = job.getTargetFormat().toLowerCase();
            File outputFile = File.createTempFile("output-", "." + ext);

            if (ext.equals("pdf")) {
                PdfConverter.convertToPdf(inputFile, outputFile);
            } else if (List.of("docx","txt","log","csv","html").contains(ext)) {
                TextToTextConverter.convert(inputFile, outputFile);
            } else if (List.of("png","jpg","jpeg","gif","webp").contains(ext)) {
                ImageConverter.convert(inputFile, outputFile);
            } else {
                throw new UnsupportedOperationException("Unsupported target format: " + ext);
            }

            String resultUrl = CloudinaryUtil.uploadConvertedFile(outputFile);

            fileJobBO.markDone(job, resultUrl);

            inputFile.delete();
            outputFile.delete();

        } catch (Exception e) {
            e.printStackTrace();
            fileJobBO.markFailed(job);
        }
    }

}
