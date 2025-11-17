package model.dao;

import java.util.ArrayList;
import java.util.List;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;

import model.bean.FileJob;
import util.FirebaseUtil;

public class FileJobDAO {

    private final Firestore db = FirebaseUtil.getFirestore();
    private static final String COLLECTION_NAME = "users";

    /** Lấy danh sách fileJobs của user */
    public List<FileJob> getFileJobsByUser(String userId) {
        List<FileJob> result = new ArrayList<>();
        try {
            QuerySnapshot snapshot = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .collection("fileJobs")
                    .get()
                    .get();

            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                FileJob job = mapDocumentToFileJob(doc, userId);
                result.add(job);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /** Lấy FileJob theo user + jobId */
    public FileJob getFileJob(String userId, String jobId) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .collection("fileJobs")
                    .document(jobId)
                    .get()
                    .get();
            if (doc.exists()) return mapDocumentToFileJob(doc, userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Cập nhật trạng thái và kết quả file */
    public boolean updateFileJob(FileJob job) {
        try {
            db.collection(COLLECTION_NAME)
              .document(job.getUserId())
              .collection("fileJobs")
              .document(job.getId())
              .update(
                      "status", job.getStatus(),
                      "resultUrl", job.getResultUrl(),
                      "updatedAt", job.getUpdatedAt()
              )
              .get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Map Firestore Document -> FileJob */
    private FileJob mapDocumentToFileJob(DocumentSnapshot doc, String userId) {
        FileJob job = new FileJob();
        job.setId(doc.getId());
        job.setUserId(userId);
        job.setFileName(doc.getString("fileName"));
        job.setFileUrl(doc.getString("fileUrl"));
        job.setResultUrl(doc.getString("resultUrl"));
        job.setTargetFormat(doc.getString("targetFormat"));
        job.setStatus(doc.getString("status"));
        job.setCreatedAt(doc.getDate("createdAt"));
        job.setUpdatedAt(doc.getDate("updatedAt"));
        return job;
    }
}
