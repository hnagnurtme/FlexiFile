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

    public boolean createFileJob(FileJob job) {
        try {
            db.collection(COLLECTION_NAME)
              .document(job.getUserId())
              .collection("fileJobs")
              .document(job.getId())
              .set(job)
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

    public List<String> getAllUserIds() {
        List<String> userIds = new ArrayList<>();
        try {
            QuerySnapshot snapshot = db.collection(COLLECTION_NAME).get().get();
            for (DocumentSnapshot doc : snapshot.getDocuments()) {
                userIds.add(doc.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userIds;
    }

    public List<FileJob> getAllFileJobs() {
        List<FileJob> allJobs = new ArrayList<>();
        try {
            QuerySnapshot usersSnapshot = db.collection(COLLECTION_NAME).get().get();
            for (DocumentSnapshot userDoc : usersSnapshot.getDocuments()) {
                String userId = userDoc.getId();
                QuerySnapshot jobsSnapshot = db.collection(COLLECTION_NAME)
                        .document(userId)
                        .collection("fileJobs")
                        .get()
                        .get();
                for (DocumentSnapshot jobDoc : jobsSnapshot.getDocuments()) {
                    FileJob job = mapDocumentToFileJob(jobDoc, userId);
                    allJobs.add(job);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allJobs;
    }

    public List<FileJob> getFileJobsByUser(String userId, String status) {
        List<FileJob> result = new ArrayList<>();
        try {
            QuerySnapshot snapshot = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .collection("fileJobs")
                    .whereEqualTo("status", status)
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
}
