package model.bean;

import java.util.Date;

public class ConvertHistory {
    private String id;
    private String userId;
    private String fileName;
    private String sourceFormat;
    private String targetFormat;
    private String resultUrl;
    private Date convertedAt;
    private String status;

    // Constructors
    public ConvertHistory() {}

    public ConvertHistory(String id, String userId, String fileName, String sourceFormat, 
                         String targetFormat, String resultUrl, Date convertedAt, String status) {
        this.id = id;
        this.userId = userId;
        this.fileName = fileName;
        this.sourceFormat = sourceFormat;
        this.targetFormat = targetFormat;
        this.resultUrl = resultUrl;
        this.convertedAt = convertedAt;
        this.status = status;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getSourceFormat() { return sourceFormat; }
    public void setSourceFormat(String sourceFormat) { this.sourceFormat = sourceFormat; }

    public String getTargetFormat() { return targetFormat; }
    public void setTargetFormat(String targetFormat) { this.targetFormat = targetFormat; }

    public String getResultUrl() { return resultUrl; }
    public void setResultUrl(String resultUrl) { this.resultUrl = resultUrl; }

    public Date getConvertedAt() { return convertedAt; }
    public void setConvertedAt(Date convertedAt) { this.convertedAt = convertedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}