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

    // Constructors, getters, setters
}
