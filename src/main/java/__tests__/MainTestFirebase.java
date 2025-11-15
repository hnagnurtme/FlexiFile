package __tests__;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserRecord;

import util.FirebaseUtil;

public class MainTestFirebase {

    public static void main(String[] args) {
        try {
            // Khởi tạo Firebase
            FirebaseUtil.initialize();

            // Lấy FirebaseAuth instance
            FirebaseAuth auth = FirebaseUtil.getAuth();

            // Test: list một user theo UID (thay UID = "test-uid" bằng UID thật nếu có)
            String testUid = "test-uid";
            try {
                UserRecord user = auth.getUser(testUid);
                System.out.println("User email: " + user.getEmail());
            } catch (Exception e) {
                System.out.println("Không tìm thấy user với UID: " + testUid);
            }

            System.out.println("✅ Firebase test completed successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
