package model.dao;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import util.FirebaseUtil;


public class UserDAO {
    private static final String COLLECTION_NAME = "users";
    Firestore db = FirebaseUtil.getFirestore();

    public boolean decrementRemainingConverts(String userId, Long numberOfFilesL) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .get()
                    .get();
            if (!doc.exists()) {
                return false;
            }
            Long remainingConverts = doc.getLong("remainingConverts");
            // Check if enough remaining converts
            if (remainingConverts == null || remainingConverts < numberOfFilesL) {
                return false; // Not enough remaining converts
            }
            Map<String, Object> updates = new HashMap<>();
            updates.put("remainingConverts", remainingConverts - numberOfFilesL);
            updates.put("updatedAt", new Date());
            db.collection(COLLECTION_NAME)
                    .document(userId)
                    .update(updates)
                    .get();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean incrementRemainingConverts(String userId, Long numberOfFilesL) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION_NAME)
                    .document(userId)
                    .get()
                    .get();
            if (!doc.exists()) {
                return false;
            }
            Long remainingConverts = doc.getLong("remainingConverts");
            if (remainingConverts == null) {
                remainingConverts = 0L;
            }
            
            Map<String, Object> updates = new HashMap<>();
            updates.put("remainingConverts", remainingConverts + numberOfFilesL);
            updates.put("updatedAt", new Date());
            db.collection(COLLECTION_NAME)
                    .document(userId)
                    .update(updates)
                    .get(); 
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}