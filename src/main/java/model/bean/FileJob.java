package model.bean;

import java.util.Date;

public class FileJob {
    private String id;           // Firestore document ID
    private String userId;       // liên kết User
    private String fileName;
    private String fileUrl;      // URL Cloudinary
    private String resultUrl;    // URL file convert
    private String targetFormat;
    private String status;       // PENDING / PROCESSING / DONE / FAILED
    private Date createdAt;
    private Date updatedAt;

    // ==============================
    // Constructors
    // ==============================

    public FileJob() {
        // Default constructor
    }

    public FileJob(String id, String userId, String fileName, String fileUrl,
                   String targetFormat, String status, Date createdAt, Date updatedAt) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.targetFormat = targetFormat;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public FileJob(String id, String userId, String fileName, String fileUrl,
                   String targetFormat) {
        this(id, userId, fileName, fileUrl, targetFormat, "PENDING", new Date(), new Date());
    }

    // ==============================
    // Getters & Setters
    // ==============================

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public String getResultUrl() {
        return resultUrl;
    }

    public void setResultUrl(String resultUrl) {
        this.resultUrl = resultUrl;
    }

    public String getTargetFormat() {
        return targetFormat;
    }

    public void setTargetFormat(String targetFormat) {
        this.targetFormat = targetFormat;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // ==============================
    // Helper
    // ==============================

    @Override
    public String toString() {
        return "FileJob{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", fileName='" + fileName + '\'' +
                ", fileUrl='" + fileUrl + '\'' +
                ", resultUrl='" + resultUrl + '\'' +
                ", targetFormat='" + targetFormat + '\'' +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
