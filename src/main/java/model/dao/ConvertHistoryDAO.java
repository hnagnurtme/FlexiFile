package model.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;

import model.bean.ConvertHistory;
import util.FirebaseUtil;

public class ConvertHistoryDAO {

    private static final String USERS_COLLECTION = "users";
    private static final String FILE_JOBS_SUBCOLLECTION = "fileJobs";

    /**
     * Lấy lịch sử convert của user (5 file gần nhất)
     * Query từ subcollection: users/{userId}/fileJobs
     */
    public List<ConvertHistory> getRecentConvertsByUserId(String userId, int limit) {
        List<ConvertHistory> history = new ArrayList<>();
        
        try {
            Firestore db = FirebaseUtil.getFirestore();
            
            // Query từ subcollection: users/{userId}/fileJobs
            // Filter: status = 'DONE' AND resultUrl exists
            // Order by updatedAt DESC, Limit
            ApiFuture<QuerySnapshot> future = db.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(FILE_JOBS_SUBCOLLECTION)
                    .whereEqualTo("status", "DONE")
                    .orderBy("updatedAt", Query.Direction.DESCENDING)
                    .limit(limit)
                    .get();

            QuerySnapshot querySnapshot = future.get();
            
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                // Skip if no resultUrl
                String resultUrl = doc.getString("resultUrl");
                if (resultUrl == null || resultUrl.isEmpty()) {
                    continue;
                }
                
                ConvertHistory item = new ConvertHistory();
                item.setId(doc.getId());
                item.setUserId(userId);
                item.setFileName(doc.getString("fileName"));
                
                // Extract source format from fileName
                String fileName = doc.getString("fileName");
                String sourceFormat = extractFormat(fileName);
                item.setSourceFormat(sourceFormat);
                
                item.setTargetFormat(doc.getString("targetFormat"));
                item.setResultUrl(resultUrl);
                
                // Convert Timestamp to Date
                com.google.cloud.Timestamp timestamp = (com.google.cloud.Timestamp) doc.get("updatedAt");
                if (timestamp != null) {
                    item.setConvertedAt(timestamp.toDate());
                }
                
                item.setStatus(doc.getString("status"));
                
                history.add(item);
            }
            
            System.out.println("✅ Fetched " + history.size() + " convert history for user: " + userId);
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error fetching convert history: " + e.getMessage());
            e.printStackTrace();
        }
        
        return history;
    }

    /**
     * Lấy tất cả lịch sử convert của user
     */
    public List<ConvertHistory> getAllConvertsByUserId(String userId) {
        return getRecentConvertsByUserId(userId, 1000); // Giới hạn 1000 records
    }

    /**
     * Đếm số file đã convert thành công
     */
    public int countSuccessfulConverts(String userId) {
        try {
            Firestore db = FirebaseUtil.getFirestore();
            
            // Query từ subcollection: users/{userId}/fileJobs
            ApiFuture<QuerySnapshot> future = db.collection(USERS_COLLECTION)
                    .document(userId)
                    .collection(FILE_JOBS_SUBCOLLECTION)
                    .whereEqualTo("status", "DONE")
                    .get();

            QuerySnapshot querySnapshot = future.get();
            
            // Đếm số documents có resultUrl
            int count = 0;
            for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                String resultUrl = doc.getString("resultUrl");
                if (resultUrl != null && !resultUrl.isEmpty()) {
                    count++;
                }
            }
            
            System.out.println("✅ Total successful converts: " + count + " for user: " + userId);
            return count;
            
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("❌ Error counting converts: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Extract file format from fileName
     */
    private String extractFormat(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "unknown";
        }
        int lastDot = fileName.lastIndexOf('.');
        String format = fileName.substring(lastDot + 1).toLowerCase();
        
        // Normalize jpeg to jpg
        if ("jpeg".equals(format)) {
            return "jpg";
        }
        
        return format;
    }
}