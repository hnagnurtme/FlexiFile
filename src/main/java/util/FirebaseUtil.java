package util;

import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.cloud.FirestoreClient;

public class FirebaseUtil {

    private static boolean initialized = false;

    public static synchronized void initialize() {
        if (initialized) return;
        try {
            // Đọc file JSON từ resources
            InputStream serviceAccount = 
                FirebaseUtil.class.getClassLoader()
                .getResourceAsStream("serviceAccountKey.json");

            if (serviceAccount == null) {
                throw new RuntimeException("Không tìm thấy serviceAccountKey.json trong resources!");
            }

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
            initialized = true;

            System.out.println("🔥 Firebase initialized successfully");

        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo Firebase: " + e.getMessage(), e);
        }
    }

    /** Lấy FirebaseAuth instance */
    public static FirebaseAuth getAuth() {
        if (!initialized) initialize();
        return FirebaseAuth.getInstance();
    }

    /** Lấy Firestore instance */
    public static Firestore getFirestore() {
        if (!initialized) initialize();
        return FirestoreClient.getFirestore();
    }
}
